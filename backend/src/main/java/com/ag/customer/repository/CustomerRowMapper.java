package com.ag.customer.repository;

import com.ag.customer.Customer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The {@link CustomerRowMapper} class implements the {@link RowMapper} interface to map rows of a {@link ResultSet} to {@link Customer} objects.
 */
@Component
public class CustomerRowMapper implements RowMapper<Customer> {
    /**
     * Maps a row of the given {@link ResultSet} to a {@link Customer} object.
     *
     * @param resultSet the {@link ResultSet} to map (pre-initialized for the current row)
     * @param rowNum    the number of the current row
     * @return the mapped {@link Customer} object
     * @throws SQLException if a SQLException is encountered getting column values
     */
    @Override
    public Customer mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Customer(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getInt("age"));
    }
}
