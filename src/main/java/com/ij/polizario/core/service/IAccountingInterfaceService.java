package com.ij.polizario.core.service;

import com.ij.polizario.ports.input.controller.response.AccountingInterfaceResponse;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

public interface IAccountingInterfaceService {

    AccountingInterfaceResponse launchAccountingInterfaceJobLoader(String excludeType) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;
}
