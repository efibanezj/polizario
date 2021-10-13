package com.ij.polizario.persistence.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "file_type2")
public class FileType2Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String accountingDate;
    private String account;
    private String cargo;
    private String abono;
    private String accountingType;
    private String description;

    public FileType2Entity(String fecha, String strLine) {

        this.accountingDate = fecha;
        this.account = strLine.substring(6,18).trim();
        this.cargo = strLine.substring(36,57).trim();
        this.abono = strLine.substring(58,83).trim();
        this.accountingType = strLine.substring(104,108).trim();
        this.description = strLine.substring(103,133).trim();
    }
}
