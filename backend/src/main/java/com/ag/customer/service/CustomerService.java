package com.ag.customer.service;

import com.ag.customer.repository.CustomerDao;
import com.ag.customer.repository.CustomerRepository;
import com.ag.customer.Customer;
import com.ag.dto.CustomerRegistrationRequest;
import com.ag.dto.CustomerUpdateRequest;
import com.ag.exceptions.DuplicateResourceException;
import com.ag.exceptions.RequestValidationException;
import com.ag.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The {@code CustomerService} class provides business logic for managing customers.
 * It interacts with the {@link CustomerDao} to perform CRUD operations.
 */
@Service
public class CustomerService {
    private final CustomerDao customerDao;

    /**
     * Constructs a new {@code CustomerService} with the specified {@link CustomerDao}.
     *
     * @param customerDao the {@link CustomerDao} to be used for data access operations
     */
    public CustomerService(/*@Qualifier("jpa")*/ @Qualifier("jdbc") CustomerDao customerDao/*, CustomerRepository customerRepository*/) {
        this.customerDao = customerDao;
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return a list of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param id the ID of the customer to retrieve
     * @return the {@link Customer} with the specified ID
     * @throws ResourceNotFoundException if no customer with the specified ID is found
     */
    public Customer getCustomerById(Integer id) {
        return customerDao.selectCustomerById(id).orElseThrow(() -> new ResourceNotFoundException("customer with id[%s] not found".formatted(id)));
    }

    /**
     * Adds a new customer to the database.
     *
     * @param customerRegistrationRequest the request containing customer registration details
     * @throws DuplicateResourceException if a customer with the specified email already exists
     */
    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // If the email exist, throw an exception
        if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException("Customer with email %s already exist".formatted(customerRegistrationRequest.email()));
        }
        // Create the customer and save them to the database
        customerDao.insertCustomer(new Customer(customerRegistrationRequest.name(), customerRegistrationRequest.email(), customerRegistrationRequest.age()));
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param id the ID of the customer to delete
     * @throws ResourceNotFoundException if no customer with the specified ID is found
     */
    public void deleteCustomerById(Integer id) {
        // Check if customer exist
       if (!customerDao.existsPersonById(id)) {
           throw new ResourceNotFoundException("Customer with id[%s] not found".formatted(id));
       }
       // delete customer
       customerDao.deleteCustomerById(id);
    }

    /**
     * Updates customer information.
     *
     * @param id the ID of the customer to update
     * @param customerUpdateRequest the request containing updated customer details
     * @throws DuplicateResourceException if a customer with the specified email already exists
     * @throws RequestValidationException if no data changes are found
     */
    public void updateCustomer(Integer id, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = getCustomerById(id);

        boolean changes = false;

        if (customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customer.getName())) {
            customer.setName(customerUpdateRequest.name());
            changes = true;
        }

        if (customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsPersonWithEmail(customerUpdateRequest.email())) {
                throw new DuplicateResourceException("Email %s already exist".formatted(customerUpdateRequest.email()));
            }
            customer.setEmail(customerUpdateRequest.email());
            changes = true;
        }

        if (customerUpdateRequest.age() != null && !customerUpdateRequest.age().equals(customer.getAge())) {
            customer.setAge(customerUpdateRequest.age());
            changes = true;
        }

        // if no change exist
        if (!changes) {
            throw new RequestValidationException("No data changes found");
        }
        customerDao.updateCustomer(customer);
    }
}
