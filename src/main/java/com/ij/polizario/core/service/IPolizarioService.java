package com.ij.polizario.core.service;

import com.ij.polizario.persistence.entities.FileType2Entity;
import com.ij.polizario.ports.input.controller.request.AccountingInterfaceRequest;
import com.ij.polizario.ports.input.controller.response.AccountingInterfaceResponse;
import com.ij.polizario.ports.input.controller.response.PolizarioResponse;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.List;

public interface IPolizarioService {

    PolizarioResponse generatePolizario();

    List<FileType2Entity> generateData();
}
