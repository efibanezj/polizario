package com.ij.polizario.controller.impl;

import com.ij.polizario.controller.request.AccountingInterfaceRequest;
import com.ij.polizario.controller.response.FileCompareResponse;
import com.ij.polizario.controller.response.PolizarioResponse;
import com.ij.polizario.core.service.IAccountingInterfaceService;
import com.ij.polizario.core.service.ICompareService;
import com.ij.polizario.core.service.IExportFileService;
import com.ij.polizario.controller.response.AccountingInterfaceResponse;
import com.ij.polizario.core.service.impl.OldPolizarioServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class FilesControllerImpl {

    private final OldPolizarioServiceImpl IPolizarioService;
    private final IAccountingInterfaceService iAccountingInterfaceService;
    private final ICompareService iCompareService;
    private final IExportFileService iExportFileService;

    @GetMapping("/polizario")
    public PolizarioResponse getPolizarioInfo() {
        return IPolizarioService.generatePolizario();
    }

    @GetMapping("/accounting-interface")
    public AccountingInterfaceResponse accountingInterface(
            @RequestParam(required = false) String accountingTypes,
            @RequestParam(required = false) String contractsNumbers)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        AccountingInterfaceRequest interfaceRequest = new AccountingInterfaceRequest(accountingTypes, contractsNumbers);
        return iAccountingInterfaceService.generateAccountingInterface(interfaceRequest);
    }

    @GetMapping("/compareFiles")
    public FileCompareResponse compareFiles(
            @RequestParam(required = false) String accountingTypes,
            @RequestParam(required = false) String contractsNumbers)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        FileCompareResponse fileCompareResponse = iCompareService.compare(accountingTypes, contractsNumbers);
        iExportFileService.exportToExcel();

        return fileCompareResponse;
    }

}
