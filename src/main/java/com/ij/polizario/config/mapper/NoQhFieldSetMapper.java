package com.ij.polizario.config.mapper;

import com.ij.polizario.persistence.entities.NoQhInfoEntity;
import org.springframework.batch.item.file.LineMapper;

public class NoQhFieldSetMapper implements LineMapper<NoQhInfoEntity> {


    @Override
    public NoQhInfoEntity mapLine(String value, int lineNumber) {

        var fullLineWithDelimiter = generateFullLine(value);

        var impDebMl = value.substring(124, 137);// se le quitaron los decimales
        var impCredMl = value.substring(139, 152);// se le quitaron los decimales

        return NoQhInfoEntity.builder()
                .accountantDate(value.substring(7, 15))
                .destinyCenter(value.substring(107, 111))
                .impDebMl(impDebMl)
                .impCredMl(impCredMl)
                .operationSign(value.substring(184, 185))
                .cuenta1(value.substring(373, 388).trim())
                .fullLine(fullLineWithDelimiter.toString())
                .build();
    }

    private static StringBuilder generateFullLine(String value) {
        var fullLineWithDelimiter = new StringBuilder();
        fullLineWithDelimiter.append(value, 0, 4).append("/");
        fullLineWithDelimiter.append(value, 4, 7).append("/");
        fullLineWithDelimiter.append(value, 7, 15).append("/");
        fullLineWithDelimiter.append(value, 15, 23).append("/");
        fullLineWithDelimiter.append(value, 23, 25).append("/");
        fullLineWithDelimiter.append(value, 25, 29).append("/");
        fullLineWithDelimiter.append(value, 29, 32).append("/");
        fullLineWithDelimiter.append(value, 32, 33).append("/");
        fullLineWithDelimiter.append(value, 33, 36).append("/");
        fullLineWithDelimiter.append(value, 36, 37).append("/");
        fullLineWithDelimiter.append(value, 37, 39).append("/");
        fullLineWithDelimiter.append(value, 39, 44).append("/");
        fullLineWithDelimiter.append(value, 44, 48).append("/");
        fullLineWithDelimiter.append(value, 48, 50).append("/");
        fullLineWithDelimiter.append(value, 50, 51).append("/");
        fullLineWithDelimiter.append(value, 51, 52).append("/");
        fullLineWithDelimiter.append(value, 52, 55).append("/");
        fullLineWithDelimiter.append(value, 55, 60).append("/");
        fullLineWithDelimiter.append(value, 60, 63).append("/");
        fullLineWithDelimiter.append(value, 63, 64).append("/");
        fullLineWithDelimiter.append(value, 64, 74).append("/");
        fullLineWithDelimiter.append(value, 74, 92).append("/");
        fullLineWithDelimiter.append(value, 92, 98).append("/");
        fullLineWithDelimiter.append(value, 98, 102).append("/");
        fullLineWithDelimiter.append(value, 102, 106).append("/");
        fullLineWithDelimiter.append(value, 106, 110).append("/");
        fullLineWithDelimiter.append(value, 110, 117).append("/");
        fullLineWithDelimiter.append(value, 117, 124).append("/");
        fullLineWithDelimiter.append(value, 124, 137).append("/");// se le quitaron los decimales
        fullLineWithDelimiter.append(value, 139, 152).append("/");// se le quitaron los decimales
        fullLineWithDelimiter.append(value, 154, 169).append("/");
        fullLineWithDelimiter.append(value, 169, 184).append("/");
        fullLineWithDelimiter.append(value, 184, 185).append("/");
        fullLineWithDelimiter.append(value, 185, 197).append("/");
        fullLineWithDelimiter.append(value, 197, 200).append("/");
        fullLineWithDelimiter.append(value, 200, 214).append("/");
        fullLineWithDelimiter.append(value, 214, 215).append("/");
        fullLineWithDelimiter.append(value, 215, 245).append("/");
        fullLineWithDelimiter.append(value, 245, 263).append("/");
        fullLineWithDelimiter.append(value, 263, 266).append("/");
        fullLineWithDelimiter.append(value, 266, 269).append("/");
        fullLineWithDelimiter.append(value, 269, 272).append("/");
        fullLineWithDelimiter.append(value, 272, 275).append("/");
        fullLineWithDelimiter.append(value, 275, 279).append("/");
        fullLineWithDelimiter.append(value, 279, 283).append("/");
        fullLineWithDelimiter.append(value, 283, 284).append("/");
        fullLineWithDelimiter.append(value, 284, 285).append("/");
        fullLineWithDelimiter.append(value, 285, 304).append("/");
        fullLineWithDelimiter.append(value, 304, 373).append("/");
        fullLineWithDelimiter.append(value, 373, 388).append("/");
        fullLineWithDelimiter.append(value, 388, 400).append("/");
        return fullLineWithDelimiter;
    }

    public static void main(String[] args) {

        String value = "1234599";
        String impDebMl = value.substring(1, 5);
        System.out.println(impDebMl);
    }
}
