package org.example.springbatchdemo.batch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchdemo.ElapsedTimeMonitoring;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Data
@Component
@Slf4j
public class CountryStep implements ItemReader<Country>, ItemWriter<Country>, ItemStream, StepExecutionListener, ChunkListener {

    private static final String SQL = "select * from country";

    private final DataSource dataSource;
    private final RowMapper rowMapper;
    private final ElapsedTimeMonitoring elapsedTimeMonitoring;
    private final JdbcCursorItemReader jdbcCursorItemReader;
    private StepExecution stepExecution;

    public CountryStep(DataSource dataSource, RowMapper rowMapper, ElapsedTimeMonitoring elapsedTimeMonitoring) {
        this.dataSource = dataSource;
        this.rowMapper = rowMapper;
        this.elapsedTimeMonitoring = elapsedTimeMonitoring;
        this.jdbcCursorItemReader = new JdbcCursorItemReaderBuilder<Country>()
                .name("countryReader")
                .dataSource(dataSource)
                .sql(SQL)
                .rowMapper(rowMapper)
                .build();
    }

    public Step getStep(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager) {
        return new StepBuilder("countryStepChunk", jobRepository)
                .chunk(5, transactionManager)
                .allowStartIfComplete(true)
                .reader(this)
                .writer((ItemWriter) this)
                .build();
    }

    @Override
    public void close() throws ItemStreamException {
        jdbcCursorItemReader.close();
    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException {
        jdbcCursorItemReader.open(arg0);
    }

    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException {
        jdbcCursorItemReader.update(arg0);
    }

    @Override
    public Country read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        var country = (Country) jdbcCursorItemReader.read();
        try {
            elapsedTimeMonitoring.increaseTime();
        } catch (TimeExpiredException e) {
            return null;
        }

        return country;
    }

    public void write(Chunk<? extends Country> chunk) throws Exception {
        chunk.getItems().forEach(c -> log.info("Writer {}", c.getName()));
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        var x = stepExecution.getJobExecution().getExecutionContext().get("countryListSize");
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
