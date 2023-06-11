package com.lab.config;

import com.lab.listener.HelloWorldJobExecutionListener;
import com.lab.listener.HelloWorldStepExecutionListener;
import com.lab.model.Product;
import com.lab.processor.InMemeItemProcessor;
import com.lab.reader.InMemeReader;
import com.lab.writer.ConsoleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.nio.file.FileSystem;

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

    @Autowired
    private InMemeItemProcessor inMemeItemProcessor;

    @Autowired
    private DataSource dataSource;

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
    public Step step3(){
        return stepBuilderFactory.get("step2")
                .<Integer,Integer>chunk(3)
                //.reader(reader())
                //.reader(flatFileItemReader(null))
                //processor(inMemeItemProcessor)
                //.reader(xmlItemReader(null))
                .reader(jdbcCursorItemReader())
                .writer(new ConsoleItemWriter())
                .build();
    }



    @Bean
    public ItemReader reader() {
        return new InMemeReader();
    }

    @Bean
    public JdbcCursorItemReader jdbcCursorItemReader(){
        JdbcCursorItemReader reader = new JdbcCursorItemReader();
        reader.setDataSource(this.dataSource);
        reader.setSql("select prod_id as product_id,prod_name as product_name,prod_desc as product_desc,unit,price from products");
        reader.setRowMapper(new BeanPropertyRowMapper(){
            {
                setMappedClass(Product.class);
            }
        });

        return reader;
    }

    @StepScope
    @Bean
    public StaxEventItemReader xmlItemReader(@Value("#{jobParameters['fileInput']}")
                                             FileSystemResource inputFile){

        StaxEventItemReader reader = new StaxEventItemReader();
        reader.setResource(inputFile);

        reader.setFragmentRootElementName("product");
        reader.setUnmarshaller(new Jaxb2Marshaller(){
            {
                setClassesToBeBound(Product.class);
            }
        });

        return reader;
    }

    @StepScope
    @Bean
    public FlatFileItemReader flatFileItemReader(
            @Value("#{jobParameters['fileInput']}")
            FileSystemResource inputFile){
           FlatFileItemReader reader = new FlatFileItemReader();
           reader.setResource(inputFile);
           reader.setLineMapper(
                   new DefaultLineMapper<Product>(){
                       {
                           setLineTokenizer(new DelimitedLineTokenizer(){
                               {
                                   setNames(new String[]{"productID","productName","productDesc","price","unit"});
                                   setDelimiter("|");


                               }

                           });

                           setFieldSetMapper(new BeanWrapperFieldSetMapper<Product>(){
                               {
                                   setTargetType(Product.class);
                               }
                           });
                       }
                   });
           reader.setLinesToSkip(1);
           return reader;
    }



    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .listener(helloWorldStepExecutionListener)
                .tasklet(helloWorldTasklet2()).build();

    }



    //@Bean
    public Job helloWorldJob(){
        return jobBuilderFactory.get("helloWorldJob")
                .listener(helloWorldJobExecutionListener)
                .start(step1())
                .next(step2())
                .build();
    }


    @Bean
    public Job helloWorldChunkBasedJob(){
        return jobBuilderFactory.get("chunkJob")
                .incrementer(new RunIdIncrementer())
                .listener(helloWorldJobExecutionListener)
                .start(step3())
                .next(step1())
                .build();
    }


}
