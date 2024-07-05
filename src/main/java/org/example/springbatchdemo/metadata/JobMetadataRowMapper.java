package org.example.springbatchdemo.metadata;

import org.example.springbatchdemo.batch.Country;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class JobMetadataRowMapper implements RowMapper<JobMetadata> {
    @Override
    public JobMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new JobMetadata(
                rs.getString("job_name"),
                rs.getInt("last_processed_row")
        );
    }
}
