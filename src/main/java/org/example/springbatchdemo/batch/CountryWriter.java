package org.example.springbatchdemo.batch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchdemo.ElapsedTimeMonitoring;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Data
@Component
@Slf4j
public class CountryWriter implements ItemWriter<Country>, StepExecutionListener, ChunkListener {

    private final ElapsedTimeMonitoring elapsedTimeMonitoring;
//    private StepExecution stepExecution;

    public CountryWriter(ElapsedTimeMonitoring elapsedTimeMonitoring) {
        this.elapsedTimeMonitoring = elapsedTimeMonitoring;
    }

    public void write(Chunk<? extends Country> chunk) throws Exception {
        chunk.getItems().forEach(c -> log.info("Writer {}", c.getName()));
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
//        this.stepExecution = stepExecution;
        var x = stepExecution.getJobExecution().getExecutionContext().get("lastProcessedRow");
        log.info("countryListSize read by countryStepChunk: {}", x);
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
