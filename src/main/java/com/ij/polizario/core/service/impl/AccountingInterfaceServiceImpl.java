package com.ij.polizario.core.service.impl;

import com.ij.polizario.Util.Util;
import com.ij.polizario.core.service.IAccountingInterfaceService;
import com.ij.polizario.exception.BusinessException;
import com.ij.polizario.exception.BusinessExceptionEnum;
import com.ij.polizario.persistence.repositories.FileType1Repository;
import com.ij.polizario.controller.request.AccountingInterfaceRequest;
import com.ij.polizario.controller.response.AccountingInterfaceResponse;
import com.ij.polizario.controller.response.ContractResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AccountingInterfaceServiceImpl implements IAccountingInterfaceService {

    private final JobLauncher jobLauncher;
    private final Job polizarioJob;
    private final FileType1Repository fileType1Repository;

    public AccountingInterfaceServiceImpl(JobLauncher jobLauncher,
                                          @Qualifier("polizarioJob") Job polizarioJob, FileType1Repository fileType1Repository) {
        this.jobLauncher = jobLauncher;
        this.polizarioJob = polizarioJob;
        this.fileType1Repository = fileType1Repository;
    }

    @Override
    public AccountingInterfaceResponse generateAccountingInterface(AccountingInterfaceRequest request) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {


        BatchStatus batchStatus = lunchFileLoaderJob();
        if (batchStatus == BatchStatus.COMPLETED) {

            List<FileType1Entity> interfaceData = getInterfaceData(request);

            Double debit = interfaceData
                    .stream()
                    .mapToDouble(entity -> calculateValueBySign(Util.mapDoubleNumber(entity.getDebitValue()),entity.getOperationSign()))
                    .sum();

            Double credit = interfaceData
                    .stream()
                    .mapToDouble(entity -> calculateValueBySign(Util.mapDoubleNumber(entity.getCreditValue()),entity.getOperationSign()))
                    .sum();

            Double diferencia = debit - credit;

            LinkedHashSet<String> accountingTypesValues = getAccountingList(interfaceData);

            List<ContractResponse> contractList = getContractList(interfaceData);

            return AccountingInterfaceResponse.builder()
                    .accountingTypes(accountingTypesValues)
                    .totalCreditValue(Util.doubleToString(credit))
                    .totalDebitValue(Util.doubleToString(debit))
                    .diferencia(Util.doubleToString(diferencia))
                    .contractList(contractList)
                    .build();

        } else {
            throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
        }
    }

    private Double calculateValueBySign(Double value, String sign){

        if(sign.equalsIgnoreCase("C")){
            return value * -1;
        }
        return value;
    }

    private LinkedHashSet<String> getAccountingList(List<FileType1Entity> interfaceData) {
        LinkedHashSet<String> accountingTypesValues = new LinkedHashSet<>();
        interfaceData.forEach(data -> accountingTypesValues.add(data.getAccountingType()));
        return accountingTypesValues;
    }

    private List<ContractResponse> getContractList(List<FileType1Entity> interfaceData) {

        Map<String, ContractResponse> contractNumberMap = new LinkedHashMap<>();
        interfaceData.forEach(data -> {
            ContractResponse contract = contractNumberMap.get(data.getContractNumber());
            if (contract == null) {
                contract = ContractResponse.builder()
                        .contractNumber(data.getContractNumber())
                        .credit("0")
                        .debit("0")
                        .total("0").
                        build();
            }
            Double debitCon = Util.mapDoubleNumber(contract.getDebit()) + Util.mapDoubleNumber(data.getDebitValue());
            Double creditCon = Util.mapDoubleNumber(contract.getCredit()) + Util.mapDoubleNumber(data.getCreditValue());

            contract.setDebit(Util.doubleToString(debitCon));
            contract.setCredit(Util.doubleToString(creditCon));
            contractNumberMap.put(contract.getContractNumber(), contract);
        });
        List<ContractResponse> contractList = new ArrayList<>(contractNumberMap.values());
        contractList.forEach(totalCon -> totalCon.setTotal(Util.doubleToString(Util.mapDoubleNumber(totalCon.getDebit()) - Util.mapDoubleNumber(totalCon.getCredit()))));

        return contractList;
    }

    private List<FileType1Entity> getInterfaceData(AccountingInterfaceRequest request) {

        if (CollectionUtils.isEmpty(request.getAccountingTypesList()) && CollectionUtils.isEmpty(request.getContractsNumberList())) {
            return fileType1Repository.findAll();
        } else if (CollectionUtils.isEmpty(request.getAccountingTypesList()) && CollectionUtils.isNotEmpty(request.getContractsNumberList())) {
            return fileType1Repository.findAllByContractNumberIn(request.getContractsNumberList());
        } else if (CollectionUtils.isNotEmpty(request.getAccountingTypesList()) && CollectionUtils.isEmpty(request.getContractsNumberList())) {
            return fileType1Repository.findAllByAccountingTypeIn(request.getAccountingTypesList());
        } else if (CollectionUtils.isNotEmpty(request.getAccountingTypesList()) && CollectionUtils.isNotEmpty(request.getContractsNumberList())) {
            return fileType1Repository.findAllByAccountingTypeInAndContractNumberIn(request.getAccountingTypesList(), request.getContractsNumberList());
        }
        throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
    }

    public BatchStatus lunchFileLoaderJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        fileType1Repository.deleteAll();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", UUID.randomUUID().toString())
                .addLong("JobId", System.currentTimeMillis())
                .addLong("time", System.currentTimeMillis()).toJobParameters();
        JobExecution execution = jobLauncher.run(polizarioJob, jobParameters);
        log.info("Execution {} ", execution.getStatus());
        return execution.getStatus();
    }

    public List<FileType1Entity> generateData(AccountingInterfaceRequest request) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        lunchFileLoaderJob();
        return getInterfaceData(request);
    }
}