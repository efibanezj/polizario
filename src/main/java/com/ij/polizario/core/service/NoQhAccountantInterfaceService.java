package com.ij.polizario.core.service;

import com.ij.polizario.Util.Util;
import com.ij.polizario.controller.response.AccountantOperationNoQHResumeResponse;
import com.ij.polizario.controller.response.AccountantOperationQHResumeResponse;
import com.ij.polizario.exception.BusinessException;
import com.ij.polizario.exception.BusinessExceptionEnum;
import com.ij.polizario.persistence.entities.NoQhInfoEntity;
import com.ij.polizario.persistence.entities.QhInfoEntity;
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
import java.util.stream.Collectors;

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

            List<AccountantOperationNoQHResumeResponse> responseList = infoList.stream().map(obj -> {
                AccountantOperationNoQHResumeResponse resp = new AccountantOperationNoQHResumeResponse();
                resp.setEntidad(obj.getEntidad());
                return resp;
            }).toList();

            return exportFile(responseList);
        } else {
            throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
        }
    }

    private String exportFile(List<AccountantOperationNoQHResumeResponse> responseList) throws IOException {

        responseList.forEach(a -> System.out.println("["+a.getEntidad()+"]"));

        return "fileName test";
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

        if (sign.equalsIgnoreCase("C")) {
            return value * -1;
        }
        return value;
    }
}
