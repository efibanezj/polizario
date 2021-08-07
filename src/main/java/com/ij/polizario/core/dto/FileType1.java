package com.ij.polizario.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileType1 {

    private String id;
    private String accountingType;
    private String debitValue;
    private String creditValue;
}