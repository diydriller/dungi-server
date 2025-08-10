package com.dungi.batchserver.batch.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ScheduleConfig {
    @Qualifier("decideWeeklyTopUserJob")
    private final Job decideBestJob;

    private final JobLauncher jobLauncher;

    @Scheduled(cron = "0 10 0 * * 1")
    public void executeDecideBestMemberJob() {
        try {
            jobLauncher.run(
                    decideBestJob,
                    new JobParametersBuilder()
                            .addString("datetime", LocalDateTime.now().toString())
                            .toJobParameters()
            );
        } catch (JobExecutionException ex) {
        }
    }

}
