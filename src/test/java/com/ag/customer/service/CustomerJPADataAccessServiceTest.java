package com.ag.customer.service;

import com.ag.AbstractTestContainersUnitTest;
import com.ag.customer.Customer;
import com.ag.customer.repository.CustomerRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest extends AbstractTestContainersUnitTest {
    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        // Initialize the mock itself
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }
    @AfterEach
    void tearDown() throws Exception {
        // After each test we have a fresh mock to work with
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        // When
        // Perform the actual work of the test.
        underTest.selectAllCustomers();
        // Then
        // Assert the results and verify outputs
     verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        // Given
        int id = 1;
        // When
        underTest.selectCustomerById(id);
        // Then
        verify(customerRepository).findById(1);
    }

    @Test
    void insertCustomer() {
        // Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 20);
        // When
       underTest.insertCustomer(customer);
        // Then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        // Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        // When
        underTest.existsPersonWithEmail(email);
        // Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 1;
        // When
        underTest.deleteCustomerById(id);
        // Then
        verify(customerRepository).deleteById(id);

    }

    @Test
    void existsPersonById() {
        // Given
        int id = 1;
        // When
        underTest.existsPersonById(id);
        // Then
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void updateCustomer() {
        // Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 20);
        // When
        underTest.updateCustomer(customer);
        // Then
        verify(customerRepository).save(customer);
    }
}