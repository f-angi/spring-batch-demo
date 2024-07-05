package org.example.springbatchdemo.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchdemo.metadata.JobMetadata;
import org.example.springbatchdemo.metadata.JobMetadataDb;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobMetadataReaderTasklet implements Tasklet, StepExecutionListener {

    private final JobMetadataDb jobMetadataDb;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        JobMetadata jobMetadata = jobMetadataDb.read(chunkContext.getStepContext().getJobName());
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("lastProcessedRow", jobMetadata.getLastProcessedRow());
        return RepeatStatus.FINISHED;
    }

//    @Override
//    public void beforeStep(StepExecution stepExecution) {
//    }

//    @Override
//    public ExitStatus afterStep(StepExecution stepExecution) {
//        stepExecution.getJobExecution().getExecutionContext().put("last", this.countryList.size());
//        return ExitStatus.COMPLETED;
//    }

    public Step getStep(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager) {
        return new StepBuilder("countryStepTasklet", jobRepository)
                .tasklet(this, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
