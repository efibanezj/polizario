package com.ij.polizario.config.mapper;

import com.ij.polizario.persistence.entities.NoQhInfoEntity;
import org.springframework.batch.item.file.LineMapper;

public class NoQhFieldSetMapper implements LineMapper<NoQhInfoEntity> {


    @Override
    public NoQhInfoEntity mapLine(String value, int lineNumber) {

        String impDebMl = value.substring(124, 139);
        String impCredMl = value.substring(139, 154);

        return NoQhInfoEntity.builder()
                .accountantDate(value.substring(7, 15))
                .destinyCenter(value.substring(107, 111))
                .impDebMl(impDebMl.substring(0, 13) + "." + impDebMl.substring(13, 15))
                .impCredMl(impCredMl.substring(0, 13) + "." + impCredMl.substring(13, 15))
                .operationSign(value.substring(184, 185))
                .cuenta1(value.substring(373, 388).trim())
                .build();
    }
}
