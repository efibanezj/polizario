package com.ij.polizario.ports.input.controller;

import com.ij.polizario.ports.input.controller.response.AccountingInterfaceResponse;
import com.ij.polizario.ports.input.controller.response.PolizarioResponse;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

public interface IPolizarioController {

    PolizarioResponse getPolizarioInfo();
}
