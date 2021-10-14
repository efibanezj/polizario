package com.ij.polizario.core.service.impl;

import com.ij.polizario.Util.Util;
import com.ij.polizario.core.service.IAccountingInterfaceService;
import com.ij.polizario.core.service.ICompareService;
import com.ij.polizario.core.service.IPolizarioService;
import com.ij.polizario.persistence.entities.FileType1Entity;
import com.ij.polizario.persistence.entities.FileType2Entity;
import com.ij.polizario.persistence.repositories.FileType1Repository;
import com.ij.polizario.persistence.repositories.FileType2Repository;
import com.ij.polizario.ports.input.controller.request.AccountingInterfaceRequest;
import com.ij.polizario.ports.input.controller.response.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CompareServiceImpl implements ICompareService {

    private final IPolizarioService IPolizarioService;
    private final IAccountingInterfaceService iAccountingInterfaceService;
    private final FileType1Repository fileType1Repository;
    private final FileType2Repository fileType2Repository;

    @Override
    public FileCompareResponse compare(String accountingTypes, String contractsNumbers) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        AccountingInterfaceRequest interfaceRequest = new AccountingInterfaceRequest(accountingTypes, contractsNumbers);
        List<FileType1Entity> fileType1EntityList = iAccountingInterfaceService.generateData(interfaceRequest);
        List<FileType2Entity>  fileType2EntityList = IPolizarioService.generateData();


        List<FileCompareDetailResponse> compare = new ArrayList<>();

        Set<String> tiposContables = new LinkedHashSet<>();
        fileType1EntityList.forEach(fileType1Entity -> tiposContables.add(fileType1Entity.getAccountingType()));


        for (String tipo : tiposContables) {

            List<FileType1Entity> rowsFile1ByType = fileType1EntityList
                    .stream()
                    .filter(fileType1Entity -> fileType1Entity.getAccountingType().equalsIgnoreCase(tipo))
                    .collect(Collectors.toList());

            List<FileType2Entity> rowsFile2ByType = fileType2EntityList
                    .stream()
                    .filter(fileType2Entity -> fileType2Entity.getAccountingType().equalsIgnoreCase(tipo))
                    .collect(Collectors.toList());

            Double debito = rowsFile1ByType
                    .stream()
                    .mapToDouble(entity -> Util.mapDoubleNumber(entity.getDebitValue()))
                    .sum();

            Double cargo = rowsFile2ByType
                    .stream()
                    .mapToDouble(entity -> Util.mapDoubleNumber(entity.getCargo()))
                    .sum();

            Double diferenceDebito = Math.abs(debito - cargo);

            CompareDebitoResponse compareDebitoResponse = CompareDebitoResponse.builder()
                    .debito(Util.doubleToString(debito))
                    .cargo(Util.doubleToString(cargo))
                    .diferencia(Util.doubleToString(diferenceDebito))
                    .build();


            Double credito = rowsFile1ByType
                    .stream()
                    .mapToDouble(entity -> Util.mapDoubleNumber(entity.getCreditValue()))
                    .sum();

            Double abono = rowsFile2ByType
                    .stream()
                    .mapToDouble(entity -> Util.mapDoubleNumber(entity.getAbono()))
                    .sum();


            Double diferenceCredito = credito - abono;

            CompareCreditoResponse compareCreditoResponse = CompareCreditoResponse.builder()
                    .credito(Util.doubleToString(credito))
                    .abono(Util.doubleToString(abono))
                    .diferencia(Util.doubleToString(diferenceCredito))
                    .build();


            FileCompareDetailResponse fileCompareDetailResponse = FileCompareDetailResponse.builder()
                    .credito(compareCreditoResponse)
                    .debito(compareDebitoResponse)
                    .tipo(tipo)
                    .build();

            compare.add(fileCompareDetailResponse);

        }


        //ALL
        Double debito = fileType1EntityList
                .stream()
                .mapToDouble(entity -> Util.mapDoubleNumber(entity.getDebitValue()))
                .sum();

        Double cargo = fileType2EntityList
                .stream()
                .mapToDouble(entity -> Util.mapDoubleNumber(entity.getCargo()))
                .sum();

        Double diferenceDebito = Math.abs(debito - cargo);

        CompareDebitoResponse compareDebitoResponse = CompareDebitoResponse.builder()
                .debito(Util.doubleToString(debito))
                .cargo(Util.doubleToString(cargo))
                .diferencia(Util.doubleToString(diferenceDebito))
                .build();


        Double credito = fileType1EntityList
                .stream()
                .mapToDouble(entity -> Util.mapDoubleNumber(entity.getCreditValue()))
                .sum();

        Double abono = fileType2EntityList
                .stream()
                .mapToDouble(entity -> Util.mapDoubleNumber(entity.getAbono()))
                .sum();

        Double diferenceCredito = Math.abs(credito - abono);

        CompareCreditoResponse compareCreditoResponse = CompareCreditoResponse.builder()
                .credito(Util.doubleToString(credito))
                .abono(Util.doubleToString(abono))
                .diferencia(Util.doubleToString(diferenceCredito))
                .build();


        FileCompareDetailResponse fileCompareDetailResponse = FileCompareDetailResponse.builder()
                .credito(compareCreditoResponse)
                .debito(compareDebitoResponse)
                .tipo("Todos")
                .build();

        compare.add(fileCompareDetailResponse);



        return FileCompareResponse.builder().compare(compare).build();
    }
}
