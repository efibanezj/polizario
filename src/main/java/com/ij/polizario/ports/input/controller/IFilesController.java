package com.ij.polizario.ports.input.controller;

import com.ij.polizario.ports.input.controller.response.AccountingInterfaceResponse;
import com.ij.polizario.ports.input.controller.response.FileCompareResponse;
import com.ij.polizario.ports.input.controller.response.PolizarioResponse;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

public interface IFilesController {

	PolizarioResponse getPolizarioInfo();

	AccountingInterfaceResponse accountingInterface(String excludeType, String contractsNumbers)
			throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException;

	FileCompareResponse compareFiles(String accountingTypes, String contractsNumbers)
			throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException;

}
