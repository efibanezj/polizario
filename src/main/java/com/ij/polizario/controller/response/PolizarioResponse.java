package com.ij.polizario.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class PolizarioResponse {

    private String totalCargoValue;
    private String totalAbonoValue;
    private String diferencia;
    private LinkedHashSet<String> accountingTypes;
    private List<ContractPolizarioResponse> contractList;
}
