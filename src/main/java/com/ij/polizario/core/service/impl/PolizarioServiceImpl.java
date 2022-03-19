package com.ij.polizario.core.service.impl;

import com.ij.polizario.Util.Util;
import com.ij.polizario.core.service.IPolizarioService;
import com.ij.polizario.persistence.entities.FileType2Entity;
import com.ij.polizario.persistence.repositories.FileType2Repository;
import com.ij.polizario.controller.response.ContractPolizarioResponse;
import com.ij.polizario.controller.response.PolizarioResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static java.lang.System.in;

@Slf4j
@Service
public class PolizarioServiceImpl implements IPolizarioService {

    private final String fileType2FilesPath;

    private final FileType2Repository fileType2Repository;

    public PolizarioServiceImpl(FileType2Repository fileType2Repository, @Value("${fileType2.files.path}") String fileType2FilesPath) {
        this.fileType2Repository = fileType2Repository;
        this.fileType2FilesPath = fileType2FilesPath;
    }

    @Override
    public PolizarioResponse generatePolizario() {

        List<FileType2Entity> data = generateData();

        Double cargo = data
                .stream()
                .mapToDouble(entity -> Util.mapDoubleNumber(entity.getCargo()))
                .sum();

        Double abono = data
                .stream()
                .mapToDouble(entity -> Util.mapDoubleNumber(entity.getAbono()))
                .sum();

        Double diferencia = Math.abs(cargo - abono);

        LinkedHashSet<String> accountingTypesValues = getAccountingTypeList(data);

        List<ContractPolizarioResponse> contractList = getContractList(data);

        return PolizarioResponse.builder()
                .accountingTypes(accountingTypesValues)
                .totalCargoValue(Util.doubleToString(cargo))
                .totalAbonoValue(Util.doubleToString(abono))
                .diferencia(Util.doubleToString(diferencia))
                .contractList(contractList)
                .build();

    }

    private List<ContractPolizarioResponse> getContractList(List<FileType2Entity> interfaceData) {

        Map<String, ContractPolizarioResponse> contractNumberMap = new LinkedHashMap<>();
        interfaceData.forEach(data -> {
            ContractPolizarioResponse contract = contractNumberMap.get(data.getAccount());
            if (contract == null) {
                contract = ContractPolizarioResponse.builder()
                        .contrato(data.getAccount())
                        .cargo("0")
                        .abono("0")
                        .total("0").
                        build();
            }
            Double abonoCon = Util.mapDoubleNumber(contract.getAbono()) + Util.mapDoubleNumber(data.getAbono());
            Double cargoCon = Util.mapDoubleNumber(contract.getCargo()) + Util.mapDoubleNumber(data.getCargo());

            contract.setAbono(Util.doubleToString(abonoCon));
            contract.setCargo(Util.doubleToString(cargoCon));
            contractNumberMap.put(contract.getContrato(), contract);
        });
        List<ContractPolizarioResponse> contractList = new ArrayList<>(contractNumberMap.values());
        contractList.forEach(totalCon -> totalCon.setTotal(Util.doubleToString(Util.mapDoubleNumber(totalCon.getAbono()) - Util.mapDoubleNumber(totalCon.getCargo()))));

        return contractList;
    }

    private LinkedHashSet<String> getAccountingTypeList(List<FileType2Entity> dataList) {
        LinkedHashSet<String> accountingTypesValues = new LinkedHashSet<>();
        dataList.forEach(data -> accountingTypesValues.add(data.getDescription().substring(1, 5)));
        return accountingTypesValues;
    }

    public List<FileType2Entity> generateData() {

        fileType2Repository.deleteAll();

        List<FileType2Entity> fileType2EntityList = new ArrayList<>();

        File dir = new File(fileType2FilesPath);
//        File dir = new File("C:/Develop/Projects/Polizario/polizarioFileLoader/src/main/resources/data/");
        FileFilter fileFilter = new WildcardFileFilter("POLIZARI*.*");
        File[] files = dir.listFiles(fileFilter);
        assert files != null;
        for (File file : files) {
            System.out.println(file);


            try {
                FileInputStream fstream = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));


                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if (strLine.contains("FECHA CONTABLE :")) {

                        String fecha = strLine.substring(17, 27);// 2021-07-19
                        System.out.println(fecha);
                        //save date
                        DODN:
                        while ((strLine = br.readLine()) != null) {
                            if (strLine.contains("SEQ. CUENTA ")) {
                                while ((strLine = br.readLine()) != null) {

                                    if (strLine.contains("TOTAL")) {
                                        break DODN;
                                    }
                                    if (!strLine.contains("-----")) {
                                        System.out.println(strLine);
                                        fileType2EntityList.add(new FileType2Entity(fecha, strLine));
                                    }
                                }

                            }
                        }
                    }
                }
                //Close the input stream
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (CollectionUtils.isNotEmpty(fileType2EntityList)) {
            fileType2Repository.saveAll(fileType2EntityList);
        }
        return fileType2EntityList;
    }
}