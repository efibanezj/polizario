package com.ij.polizario.config;

import com.ij.polizario.persistence.entities.QhInfoEntity;
import com.ij.polizario.persistence.repositories.QhInfoRepository;
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
public class QHLoadBatchConfig {

    private static final String JOB_NAME = "qhLoaderJob";
    private static final String FILES_TYPE1_DELIMITER = "/";

    private final StepBuilderFactory stepBuilderFactory;
    private final QhInfoRepository qhFileRepository;

    @Value("${qh.files.input.path}")
    private Resource[] qhFilesPath;

    @Value("${qh.files.columns}")
    private String[] qhFileColumns;

    @Value("${qh.files.fields}")
    private int[] qhFileIncludeFields;

    public QHLoadBatchConfig(StepBuilderFactory stepBuilderFactory, QhInfoRepository qhFileRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.qhFileRepository = qhFileRepository;
    }


    @Bean(JOB_NAME)
    public Job qhLoaderJob(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory
                .get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(qhAccountingInterfaceStep())
                .build();
    }

    @Bean
    public Step qhAccountingInterfaceStep() {
        return stepBuilderFactory.get("qhAccountingInterfaceStep")
                .<QhInfoEntity, QhInfoEntity>chunk(10)
                .reader(qhMultiAccountingInterfaceReader())
                .writer(qhItemWriter())
                .build();
    }

    @Bean
    public MultiResourceItemReader<QhInfoEntity> qhMultiAccountingInterfaceReader() {
        MultiResourceItemReader<QhInfoEntity> reader = new MultiResourceItemReader<>();
        reader.setDelegate(qhAccountingInterfaceReader());
        reader.setResources(qhFilesPath);
        reader.setStrict(true);
        return reader;
    }

    @Bean
    public FlatFileItemReader<QhInfoEntity> qhAccountingInterfaceReader() {
        FlatFileItemReader<QhInfoEntity> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(qhLineMapper());
        return itemReader;
    }

    @Bean
    public LineMapper<QhInfoEntity> qhLineMapper() {

        DefaultLineMapper<QhInfoEntity> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(qhFileColumns);
        lineTokenizer.setDelimiter(FILES_TYPE1_DELIMITER);
        lineTokenizer.setIncludedFields(qhFileIncludeFields);
        BeanWrapperFieldSetMapper<QhInfoEntity> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(QhInfoEntity.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public RepositoryItemWriter<QhInfoEntity> qhItemWriter() {
        RepositoryItemWriter<QhInfoEntity> writer = new RepositoryItemWriter<>();
        writer.setRepository(qhFileRepository);
        writer.setMethodName("save");
        return writer;
    }
}
