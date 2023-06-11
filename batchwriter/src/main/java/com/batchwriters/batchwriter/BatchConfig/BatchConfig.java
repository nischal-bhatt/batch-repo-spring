package com.batchwriters.batchwriter.BatchConfig;

import com.batchwriters.batchwriter.model.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;


import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@EnableBatchProcessing
@Configuration
public class BatchConfig {

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private JobBuilderFactory job;

    @Autowired
    private DataSource dataSource;

    @Bean
    @StepScope
    public FlatFileItemReader reader(
            @Value("#{jobParameters['fileInput']}")FileSystemResource inputFile
            ){

        FlatFileItemReader reader
                = new FlatFileItemReader();
        reader.setResource(inputFile);
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper(){
            {
                setFieldSetMapper(new BeanWrapperFieldSetMapper(){
                    {
                        setTargetType(Product.class);
                    }
                });

                setLineTokenizer(new DelimitedLineTokenizer(){
                    {
                        setNames(new String[]{"productId","productName","productDesc","price","unit"});
                        setDelimiter(",");
                    }
                });
            }
        });

        return reader;
    }

    @Bean
    @StepScope
    public FlatFileItemWriter flatFileItemWriter(
            @Value("#{jobParameters['fileOutput']}")FileSystemResource inputFile
    ){

        FlatFileItemWriter flatFileItemWriter
                = new FlatFileItemWriter();

        flatFileItemWriter.setResource(inputFile);
        flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator(){
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor(){
                    {
                        setNames(new String[]{"productId","productName","productDesc","unit","price"});
                    }
                });
            }
        });
        flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("productID,productName,productDesc,price,unit");

            }
        });
        //flatFileItemWriter.setAppendAllowed(true);
        flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("written at " + new SimpleDateFormat().format(new Date()));
            }
        });
        return flatFileItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter dbWriter(){
        JdbcBatchItemWriter jdbcBatchItemWriter = new JdbcBatchItemWriter();
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setSql("insert into products (prod_id,prod_name,prod_desc,price,unit)" + "values (?,?,?,?,?) ");
        jdbcBatchItemWriter.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<Product>() {
            @Override
            public void setValues(Product o, PreparedStatement preparedStatement) throws SQLException {
                  preparedStatement.setInt(1,o.getProductId());
                  preparedStatement.setString(2,o.getProductName());
                  preparedStatement.setString(3,o.getProductDesc());
                  preparedStatement.setBigDecimal(4,o.getPrice());
                  preparedStatement.setInt(5,o.getUnit());

            }
        });

        return jdbcBatchItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter dbWriter2(){
        return new JdbcBatchItemWriterBuilder<Product>()
                .dataSource(this.dataSource)
                .sql("insert into products (prod_id,prod_name,prod_desc,price,unit)" + "values (:productId,:productName,:productDesc,:unit,:price) ")
                .beanMapped()
                .build();
    }

    @Bean
    public Step step1(){
        return steps.get("step1")
                .<Product,Product> chunk(3)
                .reader(reader(null))
                //.writer(flatFileItemWriter(null))
                //.writer(dbWriter())
                .writer(dbWriter2())
                .build();
    }

    @Bean
    public Job job1(){
        return job.get("job1")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }
}
