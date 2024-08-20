package br.com.mglu.orderbatch.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class S3JobConfiguration {

    @Bean
    public Step S3Step(final JobRepository jobRepository,
                       final DataSourceTransactionManager transactionManager,
                       final S3Tasklet s3Tasklet) {
        return new StepBuilder("orderStep", jobRepository)
                .tasklet(s3Tasklet, transactionManager)
                .build();
    }

    @Bean
    public Job S3Job(JobRepository jobRepository, Step orderStep) {
        return new JobBuilder("S3Job", jobRepository)
                .start(orderStep)
                .build();
    }

}
