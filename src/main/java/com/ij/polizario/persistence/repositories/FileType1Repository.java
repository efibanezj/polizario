package com.ij.polizario.persistence.repositories;

import com.ij.polizario.persistence.entities.FileType1Entity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileType1Repository extends CrudRepository<FileType1Entity,Integer> {


    List<FileType1Entity> findAll();

    List<FileType1Entity> findAllByAccountingTypeIn(List<String> accountingTypesList);
    List<FileType1Entity> findAllByContractNumberIn(List<String> accountingTypesList);
    List<FileType1Entity> findAllByAccountingTypeInAndContractNumberIn(List<String> typesList, List<String> accountingTypesList);

}
