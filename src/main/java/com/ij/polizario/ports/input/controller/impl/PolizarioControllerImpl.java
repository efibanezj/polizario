package com.ij.polizario.ports.input.controller.impl;


import com.ij.polizario.core.service.IPolizarioService;
import com.ij.polizario.ports.input.controller.IPolizarioController;
import com.ij.polizario.ports.input.controller.response.PolizarioResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/files")
public class PolizarioControllerImpl implements IPolizarioController {

    private final IPolizarioService IPolizarioService;

    public PolizarioControllerImpl(IPolizarioService IPolizarioService) {
        this.IPolizarioService = IPolizarioService;
    }

    @GetMapping("/polizario")
    public PolizarioResponse getPolizarioInfo() {
        return IPolizarioService.generatePolizario();
    }
}
