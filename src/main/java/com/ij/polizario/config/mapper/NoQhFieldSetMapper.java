package com.ij.polizario.config.mapper;

import com.ij.polizario.persistence.entities.NoQhInfoEntity;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class NoQhFieldSetMapper implements FieldSetMapper<NoQhInfoEntity> {

    @Override
    public NoQhInfoEntity mapFieldSet(FieldSet fieldSet) {

        NoQhInfoEntity product = new NoQhInfoEntity();

        product.setEntidad(fieldSet.readString("entidad"));

        return product;
    }
}