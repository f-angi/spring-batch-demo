package org.example.springbatchdemo;


import org.example.springbatchdemo.batch.CountryReaderTasklet;
import org.example.springbatchdemo.batch.CountryStep;
import org.example.springbatchdemo.batch.StopJobTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
//@EnableBatchProcessing
public class AppConfiguration {

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.mysql_one.url}") String url,
            @Value("${spring.datasource.mysql_one.username}") String username,
            @Value("${spring.datasource.mysql_one.password}") String password) {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    public JdbcTransactionManager transactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
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

    @Bean(name = "countryJob")
    public Job getJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, CountryReaderTasklet tasklet, CountryStep step, StopJobTasklet stopJobTasklet) {
        var s1 = tasklet.getStep(jobRepository, transactionManager);
        var s2 = step.getStep(jobRepository, transactionManager);
        var s3 = stopJobTasklet.getStep(jobRepository, transactionManager);
        return new JobBuilder("countryJob", jobRepository)
                .start(s1)
                .on("*").to(s2)
                .from(s2).on("FAILED").to(s3)
                .end()
                .build();
    }

}
