package com.ij.polizario.persistence.repositories;

import com.ij.polizario.persistence.entities.NoQhInfoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NoQhInfoRepository extends CrudRepository<NoQhInfoEntity, Integer> {

    List<NoQhInfoEntity> findAll();

}
