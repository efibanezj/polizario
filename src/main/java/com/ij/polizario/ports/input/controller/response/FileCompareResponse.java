package com.ij.polizario.ports.input.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileCompareResponse {

    private List<FileCompareDetailResponse> compare;

}
