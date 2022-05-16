package com.ij.polizario.core.service.impl;

import com.ij.polizario.controller.response.PolizarioResumeResponse;
import com.ij.polizario.persistence.entities.PolizarioFileEntity;
import com.ij.polizario.persistence.repositories.PolizarioFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;

@Slf4j
@Service
public class PolizarioService {

    private final String polizarioFilesinputPath;
    private final String outputPath;

    private final PolizarioFileRepository polizarioFileRepository;

    public PolizarioService(PolizarioFileRepository polizarioFileRepository, @Value("${polizario.files.input.path}") String polizarioFilesinputPath,
                            @Value("${polizario.files.output.path}") String outputPath) {
        this.polizarioFileRepository = polizarioFileRepository;
        this.polizarioFilesinputPath = polizarioFilesinputPath;
        this.outputPath = outputPath;
    }

    public String generatePolizario() throws IOException {
        List<PolizarioResumeResponse> data = generateData().stream().map(this::buildPolizarioResponse).toList();
        return exportFile(data);
    }

    private PolizarioResumeResponse buildPolizarioResponse(PolizarioFileEntity entity) {

        return PolizarioResumeResponse.builder()
                .accountantDate(entity.getAccountantDate())
                .sequenceNumber(entity.getSequenceNumber())
                .account(entity.getAccount())
                .operateCenter(entity.getOperateCenter())//centroOperante
                .destinyCenter(entity.getDestinyCenter())//centroDestino()
                .debits(entity.getDebits())//cargos()
                .credits(entity.getCredits())//abonos()
                .correctIndicator(entity.getCorrectIndicator())//correctora()
                .accountantOperation(entity.getAccountantOperation())//operacionContable()
                .crossReference(entity.getCrossReference())//referenciaCruce()
                .description(entity.getDescription())
                .application(entity.getApplication())
                .pd(entity.getPd())
                .currency(entity.getCurrency())//divisa;
                .build();
    }


    public List<PolizarioFileEntity> generateData() {

        polizarioFileRepository.deleteAll();

        List<PolizarioFileEntity> polizarioFileEntityList = new ArrayList<>();

        File dir = new File(polizarioFilesinputPath);
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
                                        polizarioFileEntityList.add(new PolizarioFileEntity(fecha, strLine));
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

        if (CollectionUtils.isNotEmpty(polizarioFileEntityList)) {
            polizarioFileRepository.saveAll(polizarioFileEntityList);
        }
        return polizarioFileEntityList;
    }

    private String exportFile(List<PolizarioResumeResponse> responseList) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh_mm_ss");
        String fileName = outputPath + formatter.format(LocalDateTime.now()) + ".txt";

        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file, true);

        fileWriter.write(String.join("/", "Fecha Contable", "Secuencia", "Cuenta", "Centro operante", "Centro destino", "Cargos", "Abonos", "Correctora"
                , "Operacion contable", "Referencia cruce", "Descripción", "Aplicativo", "pd", "Divisa"
        ));
        fileWriter.write("\r\n");


        for (PolizarioResumeResponse op : responseList) {
            fileWriter.write(op.getResumeLine());
            fileWriter.write("\r\n");
        }

        fileWriter.close();
        return fileName;
    }
}