package com.lab.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelloWorldJobExecutionListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("before starting job :" + jobExecution.getJobInstance().getJobName());
        System.out.println("before starting job :" + jobExecution.getExecutionContext().toString());
        List<String> messages = new ArrayList<>();
        messages.add("hi");
        messages.add("nish");
        jobExecution.getExecutionContext().put("key",messages);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("after ending job :" + jobExecution.getJobInstance().getJobName());
        System.out.println("after ending job :" + jobExecution.getExecutionContext().toString());

    }
}
