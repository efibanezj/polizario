package com.ij.polizario.config;

import com.ij.polizario.persistence.entities.FileType1Entity;
import com.ij.polizario.persistence.repositories.FileType1Repository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
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
@AllArgsConstructor
@EnableBatchProcessing
public class SpringBatchConfig {

    private static final String POLIZARIO_JOB_NAME = "polizarioJob";

    private final StepBuilderFactory stepBuilderFactory;
    private final FileType1Repository fileType1Repository;

    @Value("${input.files.path}")
    private Resource[] inputFiles;


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
                .processor(processor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public MultiResourceItemReader<FileType1Entity> multiAccountingInterfaceReader() {
        MultiResourceItemReader<FileType1Entity> reader = new MultiResourceItemReader<>();
        reader.setDelegate(accountingInterfaceReader());
        reader.setResources(inputFiles);
        return reader;
    }

    @Bean
    public FlatFileItemReader<FileType1Entity> accountingInterfaceReader() {
        FlatFileItemReader<FileType1Entity> itemReader = new FlatFileItemReader<FileType1Entity>();
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    @Bean
    public LineMapper<FileType1Entity> lineMapper() {

        DefaultLineMapper<FileType1Entity> lineMapper = new DefaultLineMapper<FileType1Entity>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("id", "accountingType", "debitValue", "creditValue");
        lineTokenizer.setDelimiter("/");
        BeanWrapperFieldSetMapper<FileType1Entity> fieldSetMapper = new BeanWrapperFieldSetMapper<FileType1Entity>();
        fieldSetMapper.setTargetType(FileType1Entity.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public ItemProcessor<FileType1Entity, FileType1Entity> processor() {
        return new DBLogProcessor();
    }

    @Bean
    public RepositoryItemWriter<FileType1Entity> itemWriter() {
        RepositoryItemWriter<FileType1Entity> writer = new RepositoryItemWriter<>();
        writer.setRepository(fileType1Repository);
        writer.setMethodName("save");
        return writer;
    }
}