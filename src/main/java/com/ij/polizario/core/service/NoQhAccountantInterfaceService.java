package com.ij.polizario.core.service;

import com.ij.polizario.Util.Util;
import com.ij.polizario.controller.response.AccountantOperationNoQHResumeResponse;
import com.ij.polizario.exception.BusinessException;
import com.ij.polizario.exception.BusinessExceptionEnum;
import com.ij.polizario.persistence.entities.NoQhInfoEntity;
import com.ij.polizario.persistence.repositories.NoQhInfoRepository;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ij.polizario.Util.Util.transformDate;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class NoQhAccountantInterfaceService {
    @Value("${no-qh.files.output.path}")
    private String outputPath;
    private final NoQhInfoRepository repo;
    private final JobLauncher launcherJob;
    private final Job job;

    public NoQhAccountantInterfaceService(JobLauncher launcherJob, @Qualifier("noQhLoaderJob") Job job, NoQhInfoRepository repo) {
        this.job = job;
        this.launcherJob = launcherJob;
        this.repo = repo;
    }

    public String generateNoQhResume() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {
        BatchStatus batchStatus = launchResumeFileLoadProcess();
        if (batchStatus == BatchStatus.COMPLETED) {

            List<NoQhInfoEntity> infoList = repo.findAll();
            infoList.removeIf(el -> el.getCuenta1().startsWith("8") || el.getCuenta1().startsWith("6"));

            Map<String, List<NoQhInfoEntity>> mapQhInfoByAccountantDate = infoList.stream()
                    .collect(groupingBy(NoQhInfoEntity::getAccountantDate));

            List<AccountantOperationNoQHResumeResponse> responseList = new ArrayList<>();
            for (Map.Entry<String, List<NoQhInfoEntity>> entry : mapQhInfoByAccountantDate.entrySet()) {
                List<List<NoQhInfoEntity>> agrupadosPorCuenta = entry.getValue().stream()
                        .collect(groupingBy(NoQhInfoEntity::getCuenta1))
                        .values()
                        .stream().toList();

                for (List<NoQhInfoEntity> listaAgrupadosPorCuenta : agrupadosPorCuenta) {
                    Map<String, List<NoQhInfoEntity>> mapCentroDestino = listaAgrupadosPorCuenta.stream()
                            .collect(groupingBy(NoQhInfoEntity::getDestinyCenter));
                    for (Map.Entry<String, List<NoQhInfoEntity>> entryCentroDestino : mapCentroDestino.entrySet()) {
                        List<NoQhInfoEntity> noQhInfoByCentroDestino = entryCentroDestino.getValue();
                        responseList.add(calculateInfoResume(noQhInfoByCentroDestino));
                    }
                }
            }


            return exportFile(responseList);
        } else {
            throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
        }
    }

    private String exportFile(List<AccountantOperationNoQHResumeResponse> responseList) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh_mm_ss");
        String fileName = outputPath + "No-QH - "+formatter.format(LocalDateTime.now()) + ".txt";

        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file, true);

        fileWriter.write(String.join("/", "Tipo","Fecha contable", "Número de cuenta","Centro destino", "Débito", "Crédito", "Diferencia", "Es cuenta diferencia 0", "Estado"));
        fileWriter.write("\r\n");

        for (AccountantOperationNoQHResumeResponse op : responseList) {
            fileWriter.write(op.getResumeLine());
            fileWriter.write("\r\n");
        }

        fileWriter.close();
        return fileName;
    }

    private AccountantOperationNoQHResumeResponse calculateInfoResume(List<NoQhInfoEntity> qhInfoByAccountantNumber) {

        Double debit = qhInfoByAccountantNumber
                .stream()
                .mapToDouble(entity -> calculateValueBySign(Util.mapDoubleNumber(entity.getImpDebMl()), entity.getOperationSign()))
                .sum();

        Double credit = qhInfoByAccountantNumber
                .stream()
                .mapToDouble(entity -> calculateValueBySign(Util.mapDoubleNumber(entity.getImpCredMl()), entity.getOperationSign()))
                .sum();

        Double diferrence = debit - credit;

        String accountantDate = transformDate(qhInfoByAccountantNumber.stream().findAny().get().getAccountantDate());

        var info = qhInfoByAccountantNumber.stream().findAny().get();
        String accountNumber = info.getCuenta1();
        String destinyCenter = info.getDestinyCenter();

        AccountantOperationNoQHResumeResponse response = AccountantOperationNoQHResumeResponse.builder()
                .type("NOQH")
                .accountantDate(accountantDate)
                .accountNumber(accountNumber)
                .destinyCenter(destinyCenter)
                .totalDebit(Util.doubleToString(debit))
                .totalCredit(Util.doubleToString(credit))
                .difference(Util.doubleToString(diferrence))
                .build();

        response.calculateStatus();
        return response;

    }

    private BatchStatus launchResumeFileLoadProcess() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        repo.deleteAll();
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("date", System.currentTimeMillis())
                .addString("JobId", UUID.randomUUID().toString())
                .addLong("time", System.currentTimeMillis()).toJobParameters();
        JobExecution execution = launcherJob.run(job, jobParameters);
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
