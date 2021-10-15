package com.ij.polizario.core.service.impl;

import com.ij.polizario.Util.Util;
import com.ij.polizario.core.service.IExportFileService;
import com.ij.polizario.persistence.entities.FileType1Entity;
import com.ij.polizario.persistence.entities.FileType2Entity;
import com.ij.polizario.persistence.repositories.FileType1Repository;
import com.ij.polizario.persistence.repositories.FileType2Repository;
import com.ij.polizario.ports.input.controller.response.excel.FileCompareExcel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ExportFileServiceImpl implements IExportFileService {

    private final FileType1Repository fileType1Repository;
    private final FileType2Repository fileType2Repository;

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"Fecha", "Tipo Contable", "abono", "credito", "diferenciaCrédito", "cargo", "debito", "diferenciaDébito"};
    static String SHEET = "Resultados";


    public void exportToExcel() {

        List<FileCompareExcel> information = generateDataToExport();


        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }

            int rowIdx = 1;
            for (FileCompareExcel info : information) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(info.getFecha());
                row.createCell(1).setCellValue(info.getTipoContable());
                row.createCell(2).setCellValue(info.getAbono());
                row.createCell(3).setCellValue(info.getCredito());
                row.createCell(4).setCellValue(info.getDiferenciaCredito());
                row.createCell(5).setCellValue(info.getCargo());
                row.createCell(6).setCellValue(info.getDebito());
                row.createCell(7).setCellValue(info.getDiferenciaDebito());

            }

            workbook.write(out);
            ByteArrayInputStream res = new ByteArrayInputStream(out.toByteArray());

            IOUtils.copy(res, new FileOutputStream("C:/Others/polizario/src/main/resources/data/Results.xlsx"));
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public List<FileCompareExcel> generateDataToExport() {

        List<FileType1Entity> fileType1EntityList = fileType1Repository.findAll();
        List<FileType2Entity> fileType2EntityList = fileType2Repository.findAll();

        //Fechas
        Set<String> fechas = new LinkedHashSet<>();
        fileType1EntityList.stream().forEach(entity -> fechas.add(entity.getFechaContable()));

        //Tipos contable
        Set<String> tiposContables = new LinkedHashSet<>();
        fileType1EntityList.forEach(fileType1Entity -> tiposContables.add(fileType1Entity.getAccountingType()));


        List<FileCompareExcel> fileCompareExcelList = new ArrayList<>();

        for (String fecha : fechas) {

            for (String tipo : tiposContables) {

                List<FileType1Entity> rowsFile1ByType = fileType1EntityList
                        .stream()
                        .filter(fileType1Entity -> fileType1Entity.getFechaContable().equalsIgnoreCase(fecha))
                        .filter(fileType1Entity -> fileType1Entity.getAccountingType().equalsIgnoreCase(tipo))
                        .collect(Collectors.toList());

                List<FileType2Entity> rowsFile2ByType = fileType2EntityList
                        .stream()
                        .filter(fileType1Entity -> fileType1Entity.getAccountingDate().equalsIgnoreCase(fecha))
                        .filter(fileType2Entity -> fileType2Entity.getAccountingType().equalsIgnoreCase(tipo))
                        .collect(Collectors.toList());


                //Debito
                Double debito = rowsFile1ByType
                        .stream()
                        .mapToDouble(entity -> Util.mapDoubleNumber(entity.getDebitValue()))
                        .sum();

                Double cargo = rowsFile2ByType
                        .stream()
                        .mapToDouble(entity -> Util.mapDoubleNumber(entity.getCargo()))
                        .sum();

                Double diferenceDebito = Math.abs(debito - cargo);


                //Credito
                Double credito = rowsFile1ByType
                        .stream()
                        .mapToDouble(entity -> Util.mapDoubleNumber(entity.getCreditValue()))
                        .sum();

                Double abono = rowsFile2ByType
                        .stream()
                        .mapToDouble(entity -> Util.mapDoubleNumber(entity.getAbono()))
                        .sum();


                Double diferenceCredito = credito - abono;


                FileCompareExcel fileCompareExcel = FileCompareExcel.builder()
                        .fecha(fecha)
                        .tipoContable(tipo)
                        .debito(Util.doubleToString(debito))
                        .cargo(Util.doubleToString(cargo))
                        .diferenciaDebito(Util.doubleToString(diferenceDebito))
                        .credito(Util.doubleToString(credito))
                        .abono(Util.doubleToString(abono))
                        .diferenciaCredito(Util.doubleToString(diferenceCredito))
                        .build();

                fileCompareExcelList.add(fileCompareExcel);
            }
        }
        return fileCompareExcelList;
    }
}
