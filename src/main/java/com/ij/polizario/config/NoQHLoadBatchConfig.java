package com.ij.polizario.config;

import com.ij.polizario.config.mapper.NoQhFieldSetMapper;
import com.ij.polizario.persistence.entities.NoQhInfoEntity;
import com.ij.polizario.persistence.repositories.NoQhInfoRepository;
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
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class NoQHLoadBatchConfig {

    private static final String JOB_NAME = "noQhLoaderJob";

    private final StepBuilderFactory stepBuilderFactory;
    private final NoQhInfoRepository repo;
    @Value("${no-qh.files.input.path}")
    private Resource[] filePath;

    public NoQHLoadBatchConfig(StepBuilderFactory stepBuilderFactory, NoQhInfoRepository repo) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.repo = repo;
    }


    @Bean(JOB_NAME)
    public Job qhLoaderJob(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory
                .get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(noQhAccountingInterfaceStep())
                .build();
    }

    @Bean
    public Step noQhAccountingInterfaceStep() {
        return stepBuilderFactory.get("noQhAccountingInterfaceStep")
                .<NoQhInfoEntity, NoQhInfoEntity>chunk(10)
                .reader(noQhMultiAccountingInterfaceReader())
                .writer(noQhItemWriter())
                .build();
    }


    @Bean
    public MultiResourceItemReader<NoQhInfoEntity> noQhMultiAccountingInterfaceReader() {
        MultiResourceItemReader<NoQhInfoEntity> reader = new MultiResourceItemReader<>();
        reader.setDelegate(noQhAccountingInterfaceReader());
        reader.setStrict(true);
        reader.setResources(filePath);
        return reader;
    }

    @Bean
    public FlatFileItemReader<NoQhInfoEntity> noQhAccountingInterfaceReader() {
        FlatFileItemReader<NoQhInfoEntity> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(noQhLineMapper());
        return itemReader;
    }

    @Bean
    public LineMapper<NoQhInfoEntity> noQhLineMapper() {
        DefaultLineMapper<NoQhInfoEntity> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(noQhLineTokenizer());
        mapper.setFieldSetMapper(noQhfieldSetMapper());
        return mapper;
    }

    @Bean
    public RepositoryItemWriter<NoQhInfoEntity> noQhItemWriter() {
        RepositoryItemWriter<NoQhInfoEntity> writer = new RepositoryItemWriter<>();
        writer.setRepository(repo);
        writer.setMethodName("save");
        return writer;
    }


    @Bean
    public LineTokenizer noQhLineTokenizer() {


        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames(new String[] { "entidad"});
        tokenizer.setColumns(new Range[] {
                new Range(1, 4)
        });
        return tokenizer;
    }

    @Bean
    public FieldSetMapper<NoQhInfoEntity> noQhfieldSetMapper() {
        return new NoQhFieldSetMapper();
    }
}