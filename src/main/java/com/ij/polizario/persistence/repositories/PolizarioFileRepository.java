package com.ij.polizario.persistence.repositories;

import com.ij.polizario.persistence.entities.PolizarioFileEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PolizarioFileRepository extends CrudRepository<PolizarioFileEntity,Integer> {

    List<PolizarioFileEntity> findAll();

}
