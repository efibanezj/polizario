package com.ij.polizario.controller;

import com.ij.polizario.core.service.impl.PolizarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("polizario")
@AllArgsConstructor
public class PolizarioFilesController {

    private final PolizarioService polizarioService;

    @GetMapping("/resume")
    @ResponseStatus(code = HttpStatus.OK)
    public String generateAccountantInterfaceResume() throws IOException {
        String fileName = polizarioService.generatePolizario();
        return "Archivo generado: " + fileName;
    }
}
