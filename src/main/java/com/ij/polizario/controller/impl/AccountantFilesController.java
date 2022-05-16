package com.ij.polizario.controller.impl;

import com.ij.polizario.core.service.impl.AccountantInterfaceService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("accountant-interfaces")
@AllArgsConstructor
public class AccountantFilesController {

    private final AccountantInterfaceService accountantInterfaceService;

    @GetMapping("/{type}/resume")
    @ResponseStatus(code = HttpStatus.OK)
    public String generateAccountantInterfaceResume(@PathVariable String type) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {
        String fileName = accountantInterfaceService.generateResumeFile(type);
        return "Archivo generado: " + fileName;
    }
}
