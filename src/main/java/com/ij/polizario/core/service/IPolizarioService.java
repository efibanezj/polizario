package com.ij.polizario.core.service;

import com.ij.polizario.persistence.entities.FileType2Entity;
import com.ij.polizario.controller.response.PolizarioResponse;

import java.util.List;

public interface IPolizarioService {

    PolizarioResponse generatePolizario();

    List<FileType2Entity> generateData();
}
