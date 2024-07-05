package org.example.springbatchdemo.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class CountryReaderTasklet implements Tasklet, StepExecutionListener {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Country> rowMapper;
    private List<Country> countryList;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        this.countryList = jdbcTemplate.query("SELECT * FROM country", rowMapper);
        this.countryList.forEach(c -> log.info("Country: {}", c.getIso()));
        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put("elapsedTime", 0);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put("countryListSize", this.countryList.size());
        return ExitStatus.COMPLETED;
    }

    public Step getStep(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager) {
        return new StepBuilder("countryStepTasklet", jobRepository)
                .tasklet(this, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
