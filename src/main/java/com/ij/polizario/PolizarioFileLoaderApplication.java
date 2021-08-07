package com.ij.polizario;

import com.ij.polizario.config.JobConfiguration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.util.UUID;

@SpringBootApplication
@Import(JobConfiguration.class)
public class PolizarioFileLoaderApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PolizarioFileLoaderApplication.class, args);
    }

    private final JobLauncher jobLauncher;

    private final Job job;


    public PolizarioFileLoaderApplication(JobLauncher jobLauncher, @Qualifier("polizarioJob") Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", UUID.randomUUID().toString())
                .addLong("JobId", System.currentTimeMillis())
                .addLong("time", System.currentTimeMillis()).toJobParameters();

        JobExecution execution = jobLauncher.run(job, jobParameters);
        System.out.println("STATUS :: " + execution.getStatus());
    }
}
