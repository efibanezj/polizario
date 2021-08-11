package com.ij.polizario.ports.input.controller.impl;


import com.ij.polizario.core.service.IAccountingInterfaceService;
import com.ij.polizario.ports.input.controller.IAccountingInterfaceController;
import com.ij.polizario.ports.input.controller.request.AccountingInterfaceRequest;
import com.ij.polizario.ports.input.controller.response.AccountingInterfaceResponse;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/files")
public class AccountingInterfaceControllerImpl implements IAccountingInterfaceController {

    private final IAccountingInterfaceService iAccountingInterfaceService;

    public AccountingInterfaceControllerImpl(IAccountingInterfaceService iAccountingInterfaceService) {
        this.iAccountingInterfaceService = iAccountingInterfaceService;
    }

    @GetMapping("/accounting-interface")
    public AccountingInterfaceResponse accountingInterface(@RequestParam(required = false) String accountingTypes,
                                                           @RequestParam(required = false) String contractsNumbers)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        AccountingInterfaceRequest interfaceRequest = new AccountingInterfaceRequest(accountingTypes,contractsNumbers);
        return iAccountingInterfaceService.generateAccountingInterface(interfaceRequest);
    }
}
