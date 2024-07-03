package com.ag.customer.service;

import com.ag.customer.repository.CustomerRepository;
import com.ag.customer.repository.CustomerDao;
import com.ag.customer.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The {@link CustomerJPADataAccessService} class implements the {@link CustomerDao} interface to provide data access operations for {@link Customer} objects using JPA.
 */
@Repository("jpa")
public class CustomerJPADataAccessService implements CustomerDao {
    private final CustomerRepository customerRepository;

    /**
     * Constructs a new {@link CustomerJPADataAccessService} with the specified {@link CustomerRepository}.
     *
     * @param customerRepository the {@link CustomerRepository} to be used for data access operations
     */
    public CustomerJPADataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> selectAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customerRepository.findById(Math.toIntExact(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsPersonWithEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCustomerById(Integer id) {
        customerRepository.deleteById(Math.toIntExact(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsPersonById(Integer id) {
       return customerRepository.existsCustomerById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateCustomer(Customer updatedCustomer) {
        // update the customer
        customerRepository.save(updatedCustomer);
    }
}
