package com.ij.polizario.core.service.impl;

import com.ij.polizario.core.service.IAccountingInterfaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountingInterfaceServiceImpl implements IAccountingInterfaceService {

    private final JobLauncher jobLauncher;
    private final Job polizarioJob;

    public AccountingInterfaceServiceImpl(JobLauncher jobLauncher,
                                          @Qualifier("polizarioJob") Job polizarioJob) {
        this.jobLauncher = jobLauncher;
        this.polizarioJob = polizarioJob;
    }

    @Override
    public String launchAccountingInterfaceJobLoader() throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
        JobExecution execution = jobLauncher.run(polizarioJob, jobParameters);

        return execution.getStatus().name();
    }
}