package br.com.mglu.orderbatch.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@Slf4j
@RequiredArgsConstructor
public class S3JobLauncher {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(fixedDelay = 60000, initialDelay = 10000)
    public void scheduleS3JobLauncher() throws Exception {
        log.info(">>> S3JobLauncher");
        try {
            final JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();
            jobLauncher.run(job, jobParameters);
            log.info("Batch job executed successfully");
        } catch (Exception e) {
            log.error("Error executing batch job", e);
        }
    }
}
