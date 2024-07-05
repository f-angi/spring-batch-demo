package org.example.springbatchdemo;


import org.example.springbatchdemo.batch.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class AppConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.mysql-one")
    public DataSourceProperties dataSourceOneProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataSourceOne() {
        return dataSourceOneProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTransactionManager transactionManagerOne(@Qualifier("dataSourceOne") DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplateOne() {
        return new JdbcTemplate(dataSourceOne());
    }

    @Bean
    @ConfigurationProperties("spring.datasource.mysql-batch")
    public DataSourceProperties dataSourceBatchProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataSource() {
        return dataSourceBatchProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplateBatch() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("org.example.springbatchdemo.ws");
        return marshaller;
    }

    @Bean
    public CountryInfoClient countryInfoClient(Jaxb2Marshaller marshaller) {
        CountryInfoClient client = new CountryInfoClient();
        client.setDefaultUri("http://www.oorsprong.org/websamples.countryinfo");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    @JobScope
    public CountryItemReader countryItemReader(RowMapper<Country> countryRowMapper, ElapsedTimeMonitoring elapsedTimeMonitoring,
                                                 @Value("#{jobExecutionContext['lastProcessedRow']}") int lastProcessedRow) {
        return new CountryItemReader(dataSourceOne(), countryRowMapper, elapsedTimeMonitoring, lastProcessedRow);
    }

    @Bean
    public Step countryStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            ItemReader<Country> countryItemReader,
                            ItemWriter countryItemWriter) {
        return new StepBuilder("countryStepChunk", jobRepository)
                .chunk(5, transactionManager)
                .allowStartIfComplete(true)
                .reader(countryItemReader)
                .writer(countryItemWriter)
                .build();
    }

    @Bean(name = "countryJob")
    public Job getJob(JobRepository jobRepository,
                      @Qualifier("transactionManager") PlatformTransactionManager transactionManager, JobMetadataReaderTasklet tasklet,
                      Step countryStep, StopJobTasklet stopJobTasklet) {
        var s1 = tasklet.getStep(jobRepository, transactionManager);
        var s2 = countryStep;
//        var s3 = stopJobTasklet.getStep(jobRepository, transactionManager);
//        return new JobBuilder("countryJob", jobRepository)
//                .start(s1)
//                .on("*").to(s2)
//                .from(s2).on("FAILED").to(s3)
//                .end()
//                .build();

        return new JobBuilder("countryJob", jobRepository)
                .start(s1).next(s2).build();
    }
}
