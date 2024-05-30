package com.ag.customer.service;

import com.ag.AbstractTestContainersUnitTest;
import com.ag.customer.Customer;
import com.ag.customer.repository.CustomerDao;
import com.ag.dto.CustomerRegistrationRequest;
import com.ag.dto.CustomerUpdateRequest;
import com.ag.exceptions.DuplicateResourceException;
import com.ag.exceptions.RequestValidationException;
import com.ag.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

// This is another way to initialize the mock
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest extends AbstractTestContainersUnitTest {
    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;
    private CustomerUpdateRequest alex;

    @BeforeEach
    void setUp() {
        // Initialize the mock itself
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();
        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomerById() {
        // Given
        int id = 10;
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 20);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        // When
        Customer actual = underTest.getCustomerById(id);
        // Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        // Given
        int id = 1;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.
                getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id[%s] not found"
                        .formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("alex", email, 20);
        // When
        underTest.addCustomer(request);
        // Then
        ArgumentCaptor<Customer>customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getName()).isEqualTo("alex");
        assertThat(capturedCustomer.getAge()).isEqualTo(20);
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        // Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("alex", email, 20);
        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with email %s already exist".formatted(email));
        // Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 10;
        when(customerDao.existsPersonById(id)).thenReturn(true);
        // When
        underTest.deleteCustomerById(id);
        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenDeleteCustomerByIdNotExist() {
        // Given
        int id = 10;
        when(customerDao.existsPersonById(id)).thenReturn(false);
        // When
        assertThatThrownBy(() -> underTest
                .deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id[%s] not found".formatted(id));
        // Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomersProperties() {
        // Given
        int id = 10;
        Customer customer = new Customer(FAKER.name().fullName(), "alex@gmail.com", 20);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alex.1@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest("Alex", newEmail, 20);
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);
        // When
        underTest.updateCustomer(id, request);
        // Then
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        int id = 10;
        Customer customer = new Customer(FAKER.name().fullName(), "alex@gmail.com", 20);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest("Alex", null, null);
        // When
        underTest.updateCustomer(id, request);
        // Then
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        int id = 10;
        Customer customer = new Customer(FAKER.name().fullName(), "alex@gmail.com", 20);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alex.1@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, newEmail, null);
        // When
        underTest.updateCustomer(id, request);
        // Then
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        int id = 10;
        Customer customer = new Customer(FAKER.name().fullName(), "alex@gmail.com", 20);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        int newAge = 22;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, newAge);
        // When
        underTest.updateCustomer(id, request);
        // Then
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        int id = 10;
        Customer customer = new Customer(FAKER.name().fullName(), "alex@gmail.com", 20);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alex.1@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, newEmail, null);
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);
        // When
        assertThatThrownBy(() -> underTest
                .updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email %s already exist".formatted(request.email()));
        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdatesHasNoChanges() {
        // Given
        int id = 10;
        Customer customer = new Customer(FAKER.name().fullName(), "alex@gmail.com", 20);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());
        // When
       assertThatThrownBy(() -> underTest.updateCustomer(id, request))
               .isInstanceOf(RequestValidationException.class)
               .hasMessage("No data changes found");
        // Then
        verify(customerDao, never()).updateCustomer(any());
    }



}