package com.ij.polizario.persistence.repositories;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Deprecated
public interface FileType2Repository extends CrudRepository<FileType2Entity,Integer> {


    List<FileType2Entity> findAll();

}
