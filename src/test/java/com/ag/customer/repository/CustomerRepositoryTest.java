package com.ag.customer.repository;

import com.ag.AbstractTestContainersUnitTest;
import com.ag.customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainersUnitTest {
    @Autowired
    private CustomerRepository underTest;
    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void existsCustomerByEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.save(customer);
        // When
        var actual = underTest.existsCustomerByEmail(email);
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailDoesNotExist() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        // When
        var actual = underTest.existsCustomerByEmail(email);
        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        // Given
        Random r = new Random();
        int low = 18, high = 60;
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Integer age = r.nextInt(high - low) + low;
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                age);
        underTest.save(customer);
        Integer id = underTest
                .findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email)).map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        boolean actual = underTest.existsCustomerById(id);
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdFailsWhenIdDoesNotExist() {
        // Given
        int id = -1;
        // When
        var actual = underTest.existsCustomerById(id);
        // Then
        assertThat(actual).isFalse();
    }
}