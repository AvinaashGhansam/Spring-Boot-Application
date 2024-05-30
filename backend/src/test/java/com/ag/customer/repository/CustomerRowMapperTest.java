package com.ag.customer.repository;

import com.ag.customer.Customer;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {
    @Test
    void mapRow() throws SQLException {
        // Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("sam");
        when(rs.getString("email")).thenReturn("sam@gmail.com");
        when(rs.getInt("age")).thenReturn(19);

        // When
        Customer customer = customerRowMapper.mapRow(rs, 1);
        // Then
        Customer expected  = new Customer(1, "sam", "sam@gmail.com", 19);
        assertThat(customer).isEqualTo(expected);
    }
}