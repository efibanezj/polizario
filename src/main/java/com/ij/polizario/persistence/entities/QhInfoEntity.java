package com.ij.polizario.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "qh_info")
public class QhInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String column_A;
    private String accountantDate;
    private String column_C;
    private String column_D;
    private String column_E;
    private String column_F;
    private String column_G;
    private String column_H;
    private String column_I;
    private String column_J;
    private String accountingType;
    private String column_L;
    private String column_M;
    private String column_N;
    private String column_O;
    private String operationSign; //This field is "correctora"
    private String debitValue; //TODO wht means "-" sign right t to the debit or credit value?
    private String creditValue;
    private String column_S;
    private String column_T;
    private String column_U;
    private String column_V;
    private String column_W;
    private String column_X;
    private String column_Y;
    private String column_Z;
    private String column_AA;
    private String column_AB;
    private String column_AC;
    private String destinyCenter;//es lo mismo que codigo de oficina
    private String column_AE;
    private String column_AF;
    private String column_AG;
    private String column_AH;
    private String accountNumber;//sometimes this os only 1 field and in other cases 3 !!!
    private String column_AJ;
    private String column_AK;
    private String column_AL;
    private String contractNumber;
    private String column_AN;
    private String column_AO;
    private String column_AP;
    private String column_AQ;
    private String column_AR;
    private String column_AS;
    private String column_AT;
    private String column_AU;
    private String column_AV;
    private String column_AW;

    public String getFullLine() {
        return String.join("/", column_A, accountantDate, column_C, column_D, column_E, column_F, column_G, column_H, column_I, column_J, accountingType, column_L, column_M, column_N, column_O, operationSign, debitValue, creditValue, column_S, column_T, column_U, column_V, column_W, column_X, column_Y, column_Z, column_AA, column_AB, column_AC, destinyCenter, column_AE, column_AF, column_AG, column_AH, accountNumber, column_AJ, column_AK, column_AL, contractNumber, column_AN, column_AO, column_AP, column_AQ, column_AR, column_AS, column_AT, column_AU, column_AV, column_AW);
    }
}
