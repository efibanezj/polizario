package com.ij.polizario.controller.request;

import lombok.*;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingInterfaceRequest {

    private List<String> accountingTypesList;
    private List<String> contractsNumberList;

    public AccountingInterfaceRequest(String accountingTypes,String contractsNumbers) {
        this.accountingTypesList = accountingTypes != null ? Arrays.asList(accountingTypes.split(",")) : null;
        this.contractsNumberList = contractsNumbers != null ? Arrays.asList(contractsNumbers.split(",")) : null;
    }
}
