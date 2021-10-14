package com.ij.polizario.ports.input.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompareDebitoResponse {

    private String cargo;
    private String debito;
    private String diferencia;
}
