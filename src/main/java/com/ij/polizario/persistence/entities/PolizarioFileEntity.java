package com.ij.polizario.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "polizario_file")
public class PolizarioFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
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

    public PolizarioFileEntity(String fecha, String line) {

        this.accountantDate = fecha;
        this.sequenceNumber = line.substring(0, 6).trim();
        this.account = line.substring(6, 22).trim();
        this.operateCenter = line.substring(22, 27).trim();
        this.destinyCenter = line.substring(27, 32).trim();
        this.debits = line.substring(32, 58).trim();
        this.credits = line.substring(58, 84).trim();
        this.correctIndicator = line.substring(84, 86).trim();
        this.accountantOperation = line.substring(86, 90).trim();
        this.crossReference = line.substring(90, 102).trim();
        this.description = line.substring(104, 133).trim();

        if (line.length() > 137) {
            this.application = line.substring(134, 138).trim();
        }else{
            application = StringUtils.EMPTY;
        }
        if (line.length() > 140) {
            this.pd = line.substring(139, 141).trim();
        }else{
            pd = StringUtils.EMPTY;
        }
        if (line.length() > 150) {
            this.currency = line.substring(144, 151).trim();
        }else{
            currency = StringUtils.EMPTY;
        }
    }
}
