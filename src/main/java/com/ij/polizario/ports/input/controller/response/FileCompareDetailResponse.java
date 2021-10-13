package com.ij.polizario.ports.input.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileCompareDetailResponse {

    private String tipo;
    private CompareCreditoResponse credito;
    private CompareDebitoResponse debito;

}