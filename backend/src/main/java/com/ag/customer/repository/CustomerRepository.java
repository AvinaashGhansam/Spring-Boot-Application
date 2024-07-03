package com.ag.customer.repository;

import com.ag.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The {@link CustomerRepository} interface extends {@link JpaRepository} to provide CRUD operations for {@link Customer} entities.
 * It also includes custom methods to check for the existence of a customer by email and ID.
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    /**
     * Checks if a customer exists with the specified email.
     *
     * @param email the email to check
     * @return true if a customer exists with the specified email, false otherwise
     */
    boolean existsCustomerByEmail(String email);

    /**
     * Checks if a customer exists with the specified ID.
     *
     * @param id the ID to check
     * @return true if a customer exists with the specified ID, false otherwise
     */
    boolean existsCustomerById(Integer id);
}
