package com.ij.polizario.config;

import com.ij.polizario.persistence.entities.FileType1Entity;
import org.springframework.batch.item.ItemProcessor;

public class DBLogProcessor implements ItemProcessor<FileType1Entity, FileType1Entity>
{
    public FileType1Entity process(FileType1Entity employee) throws Exception
    {
        System.out.println("Inserting employee : " + employee);
        return employee;
    }
}