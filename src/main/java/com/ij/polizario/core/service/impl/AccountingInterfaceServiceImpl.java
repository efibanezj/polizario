package com.ij.polizario.core.service.impl;

import com.ij.polizario.core.service.IAccountingInterfaceService;
import com.ij.polizario.persistence.entities.FileType1Entity;
import com.ij.polizario.persistence.repositories.FileType1Repository;
import com.ij.polizario.ports.input.controller.response.AccountingInterfaceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
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
    public AccountingInterfaceResponse launchAccountingInterfaceJobLoader(String excludeType) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        fileType1Repository.deleteAll();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", UUID.randomUUID().toString())
                .addLong("JobId", System.currentTimeMillis())
                .addLong("time", System.currentTimeMillis()).toJobParameters();
        JobExecution execution = jobLauncher.run(polizarioJob, jobParameters);
        log.info("Execution {} ",execution.getStatus());

        Iterable<FileType1Entity> fileType1List = fileType1Repository.findAll();

        List<FileType1Entity> nuevaLista = new ArrayList<>();
        fileType1List.forEach(nuevaLista::add);

        Double debit = nuevaLista.stream()
                .filter(fileType1Entity -> !fileType1Entity.getAccounting_type().equalsIgnoreCase(excludeType))
                .mapToDouble(x -> Double.parseDouble(x.getDebit_value().replace("-", "").replace(",", "")))
                .sum();

        Double credit = nuevaLista.stream()
                .filter(fileType1Entity -> !fileType1Entity.getAccounting_type().equalsIgnoreCase(excludeType))
                .mapToDouble(x -> Double.parseDouble(x.getCredit_value().replace("-", "").replace(",", "")))
                .sum();

        Double total = debit - credit;

        LinkedHashSet<String> accountingTypesList = new LinkedHashSet<>();
        fileType1List.forEach(fileType1Entity -> {

            if (!fileType1Entity.getAccounting_type().equalsIgnoreCase(excludeType)) {
                accountingTypesList.add(fileType1Entity.getAccounting_type());
            }
        });

        String accountingTypes = accountingTypesList.toString();

        return AccountingInterfaceResponse.builder()
                .accountingTypes(accountingTypes)
                .totalCreditValue(doubleToString(credit))
                .totalDebitValue(doubleToString(debit))
                .totalOperationValue(doubleToString(total)).
                build();
    }

    private String doubleToString(Double value) {
        BigDecimal bd = new BigDecimal(value);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(bd.doubleValue());
    }
}