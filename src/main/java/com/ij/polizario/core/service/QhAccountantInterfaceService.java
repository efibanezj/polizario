package com.ij.polizario.core.service;

import com.ij.polizario.Util.Util;
import com.ij.polizario.controller.response.AccountantOperationQHResumeResponse;
import com.ij.polizario.exception.BusinessException;
import com.ij.polizario.exception.BusinessExceptionEnum;
import com.ij.polizario.persistence.entities.QhInfoEntity;
import com.ij.polizario.persistence.repositories.QhInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static com.ij.polizario.Util.Util.transformDate;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class QhAccountantInterfaceService {

    @Value("${qh.zda}")
    private String[] listQhZda;
    @Value("${qh.files.output.path}")
    private String outputPath;
    private final QhInfoRepository qhInfoRepository;
    private final JobLauncher jobLauncher;
    private final Job qhLoaderJob;
    @Value("${qh.files.input.path}")
    private String qhFilesPath;

    public QhAccountantInterfaceService(JobLauncher jobLauncher, @Qualifier("qhLoaderJob") Job qhLoaderJob, QhInfoRepository qhInfoRepository) {
        this.jobLauncher = jobLauncher;
        this.qhLoaderJob = qhLoaderJob;
        this.qhInfoRepository = qhInfoRepository;
    }

    public String generateResumeFile() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {
        return generateQhResume();
    }

    private String generateQhResume() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {
        BatchStatus batchStatus = launchResumeFileLoadProccess();
        if (batchStatus == BatchStatus.COMPLETED) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh_mm_ss");
            String fileName = "QH - " + formatter.format(LocalDateTime.now()) + ".txt";

            List<QhInfoEntity> qhInfoList = qhInfoRepository.findAll();
            exportInitialFile(outputPath + "CONSOLIDADO - " + fileName);

            qhInfoList.removeIf(el -> el.getAccountNumber().startsWith("8") || el.getAccountNumber().startsWith("6"));

            Map<String, List<QhInfoEntity>> mapQhInfoByAccountantDate = qhInfoList.stream()
                    .collect(groupingBy(QhInfoEntity::getAccountantDate));

            List<AccountantOperationQHResumeResponse> responseList = new ArrayList<>();
            for (Map.Entry<String, List<QhInfoEntity>> entry : mapQhInfoByAccountantDate.entrySet()) {
                List<List<QhInfoEntity>> agrupadosPorCuenta = entry.getValue().stream()
                        .collect(groupingBy(QhInfoEntity::getAccountNumber))
                        .values()
                        .stream().toList();

                for (List<QhInfoEntity> listaAgrupadosPorCuenta : agrupadosPorCuenta) {

                    Map<String, List<QhInfoEntity>> mapCentroDestino = listaAgrupadosPorCuenta.stream()
                            .collect(groupingBy(QhInfoEntity::getDestinyCenter));

                    for (Map.Entry<String, List<QhInfoEntity>> entryCentroDestino : mapCentroDestino.entrySet()) {
                        List<QhInfoEntity> qhInfoByCentroDestino = entryCentroDestino.getValue();
                        responseList.add(calculateQhInfoResume(qhInfoByCentroDestino));
                    }
                }
            }


            return exportFile(outputPath + "PROCESADO - " + fileName, responseList);
        } else {
            throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
        }
    }

    private void exportInitialFile(String fileName) throws IOException {
        String inputDir = "./files/input/interfaz_QH";
        try {
            mergeFiles(inputDir, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void mergeFiles(String inputDir, String outputFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
            Files.list(Paths.get(inputDir))
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try (Stream<String> lines = Files.lines(file)) {
                            lines.forEach(line -> {
                                try {
                                    writer.write(line);
                                    writer.newLine();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private String exportFile(String fileName, List<AccountantOperationQHResumeResponse> responseList) throws IOException {

        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file, true);

        fileWriter.write(String.join("/", "Tipo", "Fecha contable", "Número de cuenta", "Centro destino", "Débito", "Crédito", "Diferencia", "Es cuenta diferencia 0", "Estado"));
        fileWriter.write("\r\n");

        for (AccountantOperationQHResumeResponse op : responseList) {
            fileWriter.write(op.getResumeLine());
            fileWriter.write("\r\n");
        }

        fileWriter.close();
        return fileName;
    }

    private AccountantOperationQHResumeResponse calculateQhInfoResume(List<QhInfoEntity> qhInfoByAccountantNumber) {

        Double debit = qhInfoByAccountantNumber
                .stream()
                .mapToDouble(entity -> calculateValueBySign(Util.mapDoubleNumber(entity.getDebitValue()), entity.getOperationSign()))
                .sum();

        Double credit = qhInfoByAccountantNumber
                .stream()
                .mapToDouble(entity -> calculateValueBySign(Util.mapDoubleNumber(entity.getCreditValue()), entity.getOperationSign()))
                .sum();

        Double diferrence = debit - credit;

        String accountantDate = transformDate(qhInfoByAccountantNumber.stream().findAny().get().getAccountantDate());
        var info = qhInfoByAccountantNumber.stream().findAny().get();
        String accountNumber = info.getAccountNumber();
        String destinyCenter = info.getDestinyCenter();
        AccountantOperationQHResumeResponse response = AccountantOperationQHResumeResponse.builder()
                .type("QH")
                .accountantDate(accountantDate)
                .accountNumber(accountNumber)
                .destinyCenter(destinyCenter)
                .totalDebit(Util.doubleToString(debit))
                .totalCredit(Util.doubleToString(credit))
                .difference(Util.doubleToString(diferrence))
                .zda(isZda(accountNumber))
                .build();

        response.calculateStatus();
        return response;

    }

    private boolean isZda(String accountNumber) {

        if (listQhZda != null) {
            List<String> targetList = Arrays.asList(listQhZda);
            return targetList.contains(accountNumber);
        } else {
            return false;
        }
    }

    private BatchStatus launchResumeFileLoadProccess() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        qhInfoRepository.deleteAll();
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("date", System.currentTimeMillis())
                .addString("JobId", UUID.randomUUID().toString())
                .addLong("time", System.currentTimeMillis()).toJobParameters();
        JobExecution execution = jobLauncher.run(qhLoaderJob, jobParameters);
        log.info("Execution {} ", execution.getStatus());
        return execution.getStatus();
    }

    private Double calculateValueBySign(Double value, String sign) {

        if ("C".equalsIgnoreCase(sign)) {
            return value * -1;
        }
        return value;
    }
}
