package com.ij.polizario.core.service.impl;

import com.ij.polizario.Util.Util;
import com.ij.polizario.core.service.IAccountingInterfaceService;
import com.ij.polizario.core.service.IPolizarioService;
import com.ij.polizario.exception.BusinessException;
import com.ij.polizario.exception.BusinessExceptionEnum;
import com.ij.polizario.persistence.entities.FileType1Entity;
import com.ij.polizario.persistence.entities.FileType2Entity;
import com.ij.polizario.persistence.repositories.FileType1Repository;
import com.ij.polizario.persistence.repositories.FileType2Repository;
import com.ij.polizario.ports.input.controller.request.AccountingInterfaceRequest;
import com.ij.polizario.ports.input.controller.response.AccountingInterfaceResponse;
import com.ij.polizario.ports.input.controller.response.ContractResponse;
import com.ij.polizario.ports.input.controller.response.PolizarioResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static java.lang.System.in;

@Slf4j
@Service
public class PolizarioServiceImpl implements IPolizarioService {

    private final String fileType2FilesPath;

    private final FileType2Repository fileType2Repository;

    public PolizarioServiceImpl(FileType2Repository fileType2Repository,@Value("${fileType2.files.path}")String fileType2FilesPath) {
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

        Double total = cargo - abono;

        LinkedHashSet<String> accountingTypesValues = getAccountingTypeList(data);

        return PolizarioResponse.builder()
                .accountingTypes(accountingTypesValues)
                .totalCargoValue(Util.doubleToString(cargo))
                .totalAbonoValue(Util.doubleToString(abono))
                .totalOperationValue(Util.doubleToString(total))
                .build();

    }

    private LinkedHashSet<String> getAccountingTypeList(List<FileType2Entity> dataList) {
        LinkedHashSet<String> accountingTypesValues = new LinkedHashSet<>();
        dataList.forEach(data -> accountingTypesValues.add(data.getDescription().substring(1,5)));
        return accountingTypesValues;
    }

    public List<FileType2Entity> generateData() {

        fileType2Repository.deleteAll();

        List<FileType2Entity> fileType2EntityList = new ArrayList<>();

        File dir = new File(fileType2FilesPath);
//        File dir = new File("C:/Develop/Projects/Polizario/polizarioFileLoader/src/main/resources/data/");
        FileFilter fileFilter = new WildcardFileFilter("POLIZARI*.*");
        File[] files = dir.listFiles(fileFilter);
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);


            try {
                FileInputStream fstream = new FileInputStream(files[i]);
//            FileInputStream fstream = new FileInputStream("C:\\Develop\\Projects\\Polizario\\polizarioFileLoader\\src\\main\\resources\\data\\POLIZARI.UG.F210719.LEY1116.TXT");
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));


                String strLine;
                //Loop through and check if a header or footer line, if not
                //equate a substring to a temp variable and print it....
                while ((strLine = br.readLine()) != null) {
//      if (!(strLine.charAt(1) == "h" || strLine.charAt(1) == "f"))
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

        if(CollectionUtils.isNotEmpty(fileType2EntityList)) {
            fileType2Repository.saveAll(fileType2EntityList);
        }
        return fileType2EntityList;
    }
}