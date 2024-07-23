package org.example.springbatchdemo.batch;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchdemo.ElapsedTimeMonitoring;
import org.example.springbatchdemo.exception.CustomRuntimeException;
import org.example.springbatchdemo.metadata.JobMetadata;
import org.example.springbatchdemo.metadata.JobMetadataDb;
import org.example.springbatchdemo.ws.CountryInfoClient;
import org.example.springbatchdemo.ws.CountryInfoClientAdapter;
import org.example.springbatchdemo.ws.FullCountryInfoResponse;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CountryWriter implements ItemWriter<Country>, StepExecutionListener, ChunkListener {

    private final ElapsedTimeMonitoring elapsedTimeMonitoring;
    private final JobMetadataDb jobMetadataDb;
    private final CountryInfoClientAdapter countryInfoClient;
    private StepExecution stepExecution;

    public void write(Chunk<? extends Country> chunk) throws Exception {
        chunk.getItems().forEach(c -> {
            log.info("Writer {}", c.getName());
            countryInfoClient.getCountry(c.getIso()).ifPresentOrElse(response -> {
                log.info("CountryInfoClient: {}", response.getFullCountryInfoResult().getSCapitalCity());
                // FIXME: listener
                jobMetadataDb.write(new JobMetadata(stepExecution.getJobExecution().getJobInstance().getJobName(), c.getId()));
            }, () -> {
                log.info("Max attempts reached for {}", c.getName());
            });
        });
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
//        var x = stepExecution.getJobExecution().getExecutionContext().get("lastProcessedRow");
//        log.info("lastProcessedRow read by CountryWriter: {}", x);

    }

    @Override
    public void afterChunk(ChunkContext context) {
//        try {
//            elapsedTimeMonitoring.increaseTime();
//        } catch (TimeExpiredException e) {
////            throw new RuntimeException("fooooo");
////            this.stepExecution.setTerminateOnly();
////            this.stepExecution.setExitStatus(new ExitStatus("STOPPED", "foooooo"));
//            context.getStepContext().getStepExecution().setExitStatus(new ExitStatus("STOPPED", "foooooo"));
//        }

        try {
            Thread.sleep((long) (Math.random() * 1000 + 2000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
