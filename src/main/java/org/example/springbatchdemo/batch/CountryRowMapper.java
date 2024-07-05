package org.example.springbatchdemo.batch;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CountryRowMapper implements RowMapper<Country> {
    @Override
    public Country mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Country(
                rs.getInt("id"),
                rs.getString("iso"),
                rs.getString("name"),
                rs.getString("nicename"),
                rs.getString("iso3"),
                rs.getInt("numcode"),
                rs.getInt("phonecode")
        );
    }
}
