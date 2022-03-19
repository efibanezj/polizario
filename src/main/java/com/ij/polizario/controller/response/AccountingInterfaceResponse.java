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
public class AccountingInterfaceResponse {

    private String totalDebitValue;
    private String totalCreditValue;
    private String diferencia;
    private LinkedHashSet<String> accountingTypes;
    private List<ContractResponse> contractList;
}
