package com.ij.polizario.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolizarioResumeResponse {

    private String accountantDate;
    private String sequenceNumber;
    private String account;
    private String operateCenter;//centroOperante
    private String destinyCenter;//centroDestino;
    private String debits;//cargos;
    private String credits;//abonos;
    private String correctIndicator;//correctora;
    private String accountantOperation;//operacionContable;
    private String crossReference;//referenciaCruce;
    private String description;
    private String application;
    private String pd;
    private String currency;//divisa;

    public String getResumeLine() {
        return String.join("/", accountantDate,sequenceNumber, account, operateCenter, destinyCenter, debits, credits, correctIndicator, accountantOperation,
                crossReference, description, application, pd, currency);
    }
}
