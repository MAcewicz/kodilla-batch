package com.kodilla.batchtask;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/csv")
public class CsvController {

    private JobLauncher jobLauncher;
    private Job calculateAge;

    public CsvController(JobLauncher jobLauncher, Job calculateAge) {
        this.jobLauncher = jobLauncher;
        this.calculateAge = calculateAge;
    }

    @GetMapping("/convert")
    public void convertCsv() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(calculateAge, new JobParameters());
    }

}
