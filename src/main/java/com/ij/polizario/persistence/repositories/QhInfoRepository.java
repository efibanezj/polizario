package com.ij.polizario.persistence.repositories;

import com.ij.polizario.persistence.entities.QhInfoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QhInfoRepository extends CrudRepository<QhInfoEntity,Integer> {


    List<QhInfoEntity> findAll();

}
