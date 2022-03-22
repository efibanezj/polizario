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
public class AccountantInterfaceQHResumeResponse {

    private List<AccountantOperationQHResumeResponse> operationResumeResponses;
}
