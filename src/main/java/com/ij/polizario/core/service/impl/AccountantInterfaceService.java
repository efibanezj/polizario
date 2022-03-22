package com.ij.polizario.core.service.impl;

import com.ij.polizario.Util.Util;
import com.ij.polizario.controller.response.AccountantOperationQHResumeResponse;
import com.ij.polizario.exception.BusinessException;
import com.ij.polizario.exception.BusinessExceptionEnum;
import com.ij.polizario.persistence.entities.QhInfoEntity;
import com.ij.polizario.persistence.repositories.QhInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
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
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class AccountantInterfaceService {

    @Value("${qh.zda}")
    private String[] listQhZda;
    @Value("${qh.files.output.path}")
    private String outputPath;
    private final QhInfoRepository qhInfoRepository;
    private final JobLauncher jobLauncher;
    private final Job qhLoaderJob;

    public AccountantInterfaceService(JobLauncher jobLauncher, @Qualifier("qhLoaderJob") Job qhLoaderJob, QhInfoRepository qhInfoRepository) {
        this.jobLauncher = jobLauncher;
        this.qhLoaderJob = qhLoaderJob;
        this.qhInfoRepository = qhInfoRepository;
    }

    public String generateResumeFile(String type) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {

        if (type.equalsIgnoreCase("qh")) {
            return generateQhResume();
        } else {
            throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
        }
    }

    private String generateQhResume() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {
        BatchStatus batchStatus = launchResumeFileLoadProccess();
        if (batchStatus == BatchStatus.COMPLETED) {

            List<QhInfoEntity> qhInfoList = qhInfoRepository.findAll();
            qhInfoList.removeIf(el -> el.getAccountNumber().startsWith("8") || el.getAccountNumber().startsWith("6"));

            Map<String, List<QhInfoEntity>> mapQhInfoByAccountantDate = qhInfoList.stream()
                    .collect(groupingBy(QhInfoEntity::getAccountantDate));

            List<AccountantOperationQHResumeResponse> responseList = new ArrayList<>();
            for (Map.Entry<String, List<QhInfoEntity>> entry : mapQhInfoByAccountantDate.entrySet()) {
                List<AccountantOperationQHResumeResponse> a = entry.getValue().stream()
                        .collect(groupingBy(QhInfoEntity::getAccountNumber))
                        .values()
                        .stream()
                        .map(this::calculateQhInfoResume).toList();
                responseList.addAll(a);
            }


            return exportFile(responseList);
        } else {
            throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
        }
    }

    private String exportFile(List<AccountantOperationQHResumeResponse> responseList) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh_mm_ss");
        String fileName = outputPath + formatter.format(LocalDateTime.now()) + ".txt";

        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file, true);

        fileWriter.write(String.join("/", "Fecha contable", "Número de cuenta", "Débito", "Crédito", "Diferencia", "Es cuenta diferencia 0", "Estado"));
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

        String accountantDate = qhInfoByAccountantNumber.stream().findAny().get().getAccountantDate();
        String accountNumber = qhInfoByAccountantNumber.stream().findAny().get().getAccountNumber();

        AccountantOperationQHResumeResponse response = AccountantOperationQHResumeResponse.builder()
                .accountantDate(accountantDate)
                .accountNumber(accountNumber)
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

        if (sign.equalsIgnoreCase("C")) {
            return value * -1;
        }
        return value;
    }
}
