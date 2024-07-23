package org.example.springbatchdemo.batch;

import org.example.springbatchdemo.ElapsedTimeMonitoring;
import org.example.springbatchdemo.exception.TimeExpiredException;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

public class CountryItemReader implements ItemStream, ItemReader<Country> {

    private static final String SQL = "select * from country where id > ";

    private final ElapsedTimeMonitoring elapsedTimeMonitoring;
    private final JdbcCursorItemReader jdbcCursorItemReader;

    public CountryItemReader(DataSource dataSource, RowMapper rowMapper, ElapsedTimeMonitoring elapsedTimeMonitoring, int lastProcessedRow) {
        this.elapsedTimeMonitoring = elapsedTimeMonitoring;
        this.jdbcCursorItemReader = new JdbcCursorItemReaderBuilder<Country>()
                .name("countryReader")
                .dataSource(dataSource)
                .sql(SQL + lastProcessedRow)
                .rowMapper(rowMapper)
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
            elapsedTimeMonitoring.increaseTime(); // FIXME listener
        } catch (TimeExpiredException e) {
            return null;
        }

        return country;
    }


}
