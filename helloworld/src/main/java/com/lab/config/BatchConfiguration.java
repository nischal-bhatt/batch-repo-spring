package com.lab.config;

import com.lab.listener.HelloWorldJobExecutionListener;
import com.lab.listener.HelloWorldStepExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private HelloWorldJobExecutionListener helloWorldJobExecutionListener;
    @Autowired
    private HelloWorldStepExecutionListener helloWorldStepExecutionListener;


    private Tasklet helloWorldTasklet2() {
        return (
                new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("hello world2");
                        return RepeatStatus.FINISHED;
                    }
                }

        );
    }

    private Tasklet helloworldTasklet() {
        return (
                new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("hello world");
                        return RepeatStatus.FINISHED;
                    }
                }

        );
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .listener(helloWorldStepExecutionListener)
                .tasklet(helloworldTasklet()).build();
    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .listener(helloWorldStepExecutionListener)
                .tasklet(helloWorldTasklet2()).build();
    }



    @Bean
    public Job helloWorldJob(){
        return jobBuilderFactory.get("helloWorldJob")
                .listener(helloWorldJobExecutionListener)
                .start(step1())
                .next(step2())
                .build();
    }


}