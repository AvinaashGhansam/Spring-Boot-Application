package com.ag.customer.repository;

import com.ag.customer.Customer;

import java.util.List;
import java.util.Optional;

/**
 * The {@link CustomerDao} interface is responsible for accessing the database store for {@link Customer} entities.
 * It provides methods to perform CRUD operations and check for the existence of customers by email and ID.
 */
public interface CustomerDao {
    /**
     * Retrieves all customers from the database.
     *
     * @return a list of all customers
     */
    List<Customer> selectAllCustomers();

    /**
     * Retrieves a customer by their ID.
     *
     * @param id the ID of the customer to retrieve
     * @return an {@link Optional} containing the customer if found, or an empty {@link Optional} if not found
     */
    Optional<Customer> selectCustomerById(Integer id);

    /**
     * Inserts a new customer into the database.
     *
     * @param customer the customer to insert
     */
    void insertCustomer(Customer customer);

    /**
     * Checks if a customer exists with the specified email.
     *
     * @param email the email to check
     * @return true if a customer exists with the specified email, false otherwise
     */
    boolean existsPersonWithEmail(String email);

    /**
     * Deletes a customer by their ID.
     *
     * @param id the ID of the customer to delete
     */
    void deleteCustomerById(Integer id);

    /**
     * Checks if a customer exists with the specified ID.
     *
     * @param id the ID to check
     * @return true if a customer exists with the specified ID, false otherwise
     */
    boolean existsPersonById(Integer id);

    /**
     * Updates the information of an existing customer.
     *
     * @param updatedCustomer the customer with updated information
     */
    void updateCustomer(Customer updatedCustomer);
}
