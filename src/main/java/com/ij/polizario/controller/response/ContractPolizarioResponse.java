package com.ij.polizario.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractPolizarioResponse {
    private String contrato;
    private String abono;
    private String cargo;
    private String total;

}
