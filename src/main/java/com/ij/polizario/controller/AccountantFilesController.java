package com.ij.polizario.controller;

import com.ij.polizario.core.service.NoQhAccountantInterfaceService;
import com.ij.polizario.core.service.QhAccountantInterfaceService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("accountant-interfaces")
@AllArgsConstructor
public class AccountantFilesController {

    private final QhAccountantInterfaceService qhAccountantInterfaceService;
    private final NoQhAccountantInterfaceService noQhAccountantInterfaceService;

    @GetMapping("/qh/resume")
    @ResponseStatus(code = HttpStatus.OK)
    public String generateQhAccountantInterfaceResume() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {
        String fileName = qhAccountantInterfaceService.generateResumeFile();
        return "Archivo generado: " + fileName;
    }

    @GetMapping("/no-qh/resume")
    @ResponseStatus(code = HttpStatus.OK)
    public String generateNoQhAccountantInterfaceResume() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {
        String fileName = noQhAccountantInterfaceService.generateNoQhResume();
        return "Archivo generado: " + fileName;
    }
}
