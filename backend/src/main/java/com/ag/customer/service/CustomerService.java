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
 * Business Logic
 * The service gets its data from the CustomerDao
 */
@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(/*@Qualifier("jpa")*/ @Qualifier("jdbc") CustomerDao customerDao/*, CustomerRepository customerRepository*/) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer id) {
        return customerDao.selectCustomerById(id).orElseThrow(() -> new ResourceNotFoundException("customer with id[%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // If the email exist, throw an exception
        if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException("Customer with email %s already exist".formatted(customerRegistrationRequest.email()));
        }
        // Create the customer and save them to the database
        customerDao.insertCustomer(new Customer(customerRegistrationRequest.name(), customerRegistrationRequest.email(), customerRegistrationRequest.age()));
    }

    public void deleteCustomerById(Integer id) {
        // Check if customer exist
       if (!customerDao.existsPersonById(id)) {
           throw new ResourceNotFoundException("Customer with id[%s] not found".formatted(id));
       }
       // delete customer
       customerDao.deleteCustomerById(id);
    }

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
