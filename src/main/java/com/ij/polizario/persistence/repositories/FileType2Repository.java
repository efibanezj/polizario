package com.ij.polizario.persistence.repositories;

import com.ij.polizario.persistence.entities.FileType2Entity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileType2Repository extends CrudRepository<FileType2Entity,Integer> {


    List<FileType2Entity> findAll();

}
