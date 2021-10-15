package com.ij.polizario.ports.input.controller.response.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileCompareExcel {

    private String fecha;
    private String tipoContable;
    private String abono;
    private String credito;
    private String diferenciaCredito;
    private String cargo;
    private String debito;
    private String diferenciaDebito;

}
