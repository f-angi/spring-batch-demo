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
public class StopJobTasklet implements Tasklet, StepExecutionListener {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        contribution.setExitStatus(new ExitStatus("STOPPED", "foooooooooooo"));
        return RepeatStatus.FINISHED;
    }

    public Step getStep(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager) {
        return new StepBuilder("stopJobStepTasklet", jobRepository)
                .tasklet(this, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
