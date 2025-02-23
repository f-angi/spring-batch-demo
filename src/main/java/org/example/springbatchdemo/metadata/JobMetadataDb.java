package org.example.springbatchdemo.metadata;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

// TODO: transactions
@Component
public class JobMetadataDb {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<JobMetadata> rowMapper;

    public JobMetadataDb(@Qualifier("jdbcTemplateBatch") JdbcTemplate jdbcTemplate, RowMapper<JobMetadata> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    public JobMetadata read(String jobName) {
        try {
            return jdbcTemplate.queryForObject("select * from job_metadata where job_name = ?", new Object[]{jobName}, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new JobMetadata(jobName, 0);
        }
    }

    // TODO insert on duplicate
    public void write(JobMetadata jobMetadata) {
        try {
            jdbcTemplate.queryForObject("select * from job_metadata where job_name = ?", new Object[]{jobMetadata.getJobName()}, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("insert into job_metadata values (?, ?)", jobMetadata.getJobName(), 0);
        }
        jdbcTemplate.update("update job_metadata set job_name=?, last_processed_row=?", jobMetadata.getJobName(), jobMetadata.getLastProcessedRow());
    }
}
