package com.ij.polizario.ports.input.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountingInterfaceResponse {

    private String accountingTypes;
    private String totalDebitValue;
    private String totalCreditValue;
    private String totalOperationValue;
}
