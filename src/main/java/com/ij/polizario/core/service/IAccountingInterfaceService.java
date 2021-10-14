package com.ij.polizario.core.service;

import com.ij.polizario.persistence.entities.FileType1Entity;
import com.ij.polizario.ports.input.controller.request.AccountingInterfaceRequest;
import com.ij.polizario.ports.input.controller.response.AccountingInterfaceResponse;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.List;

public interface IAccountingInterfaceService {

    AccountingInterfaceResponse generateAccountingInterface(AccountingInterfaceRequest request) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException;

    List<FileType1Entity> generateData(AccountingInterfaceRequest request) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException;

}
