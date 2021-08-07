package com.ij.polizario.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "file_type1")
public class FileType1Entity {

    @Id
    private String id;
    private String accountingType;
    private String debitValue;
    private String creditValue;
}
