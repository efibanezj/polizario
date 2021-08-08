package com.ij.polizario.ports.input.controller.impl;


import com.ij.polizario.core.service.IAccountingInterfaceService;
import com.ij.polizario.ports.input.controller.IAccountingInterfaceController;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("/files")
public class AccountingInterfaceControllerImpl implements IAccountingInterfaceController {

    private final IAccountingInterfaceService iAccountingInterfaceService;

    @PostMapping("/accounting-interface")
    public String accountingInterface() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        return iAccountingInterfaceService.launchAccountingInterfaceJobLoader();
    }
}
