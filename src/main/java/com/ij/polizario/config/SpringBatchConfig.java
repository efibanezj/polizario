package com.ij.polizario.config;

import com.ij.polizario.persistence.entities.FileType1Entity;
import com.ij.polizario.persistence.repositories.FileType1Repository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    private static final String POLIZARIO_JOB_NAME = "polizarioJob";
    private static final String FILES_TYPE1_DELIMITER = "/";

    private final StepBuilderFactory stepBuilderFactory;
    private final FileType1Repository fileType1Repository;

    @Value("${fileType1.files.path}")
    private Resource[] fileType1FilesPath;

    @Value("${fileType1.fields}")
    private String[] fileType1Fields;

    @Value("${fileType1.fields.includeFields}")
    private int[] fileType1IncludeFields;

    public SpringBatchConfig(StepBuilderFactory stepBuilderFactory, FileType1Repository fileType1Repository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.fileType1Repository = fileType1Repository;
    }


    @Bean("polizarioJob")
    public Job polizarioJob(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory
                .get(POLIZARIO_JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(accountingInterfaceStep())
                .build();
    }

    @Bean
    public Step accountingInterfaceStep() {
        return stepBuilderFactory.get("accountingInterfaceStep")
                .<FileType1Entity, FileType1Entity>chunk(10)
                .reader(multiAccountingInterfaceReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public MultiResourceItemReader<FileType1Entity> multiAccountingInterfaceReader() {
        MultiResourceItemReader<FileType1Entity> reader = new MultiResourceItemReader<>();
        reader.setDelegate(accountingInterfaceReader());
        reader.setResources(fileType1FilesPath);
        return reader;
    }

    @Bean
    public FlatFileItemReader<FileType1Entity> accountingInterfaceReader() {
        FlatFileItemReader<FileType1Entity> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    @Bean
    public LineMapper<FileType1Entity> lineMapper() {

        DefaultLineMapper<FileType1Entity> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(fileType1Fields);
        lineTokenizer.setDelimiter(FILES_TYPE1_DELIMITER);
        lineTokenizer.setIncludedFields(fileType1IncludeFields);
        BeanWrapperFieldSetMapper<FileType1Entity> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(FileType1Entity.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public RepositoryItemWriter<FileType1Entity> itemWriter() {
        RepositoryItemWriter<FileType1Entity> writer = new RepositoryItemWriter<>();
        writer.setRepository(fileType1Repository);
        writer.setMethodName("save");
        return writer;
    }
}