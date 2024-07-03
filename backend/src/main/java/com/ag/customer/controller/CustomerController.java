package com.ag.customer.controller;

import com.ag.customer.service.CustomerService;
import com.ag.customer.Customer;
import com.ag.dto.CustomerRegistrationRequest;
import com.ag.dto.CustomerUpdateRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The {@link CustomerController} handles HTTP requests and sends data to the {@link CustomerService}.
 * It provides endpoints to perform CRUD operations on {@link Customer} entities.
 */
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    /**
     * Constructs a new {@link CustomerController} with the specified {@link CustomerService}.
     *
     * @param customerService the service to handle customer-related operations
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Retrieves all customers.
     *
     * @return a list of all customers
     */
    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param id the ID of the customer to retrieve
     * @return the customer with the specified ID
     */
    @GetMapping("{id}")
    public Customer getCustomerById(@PathVariable("id") Integer id) {
        return customerService.getCustomerById(id);
    }

    /**
     * Registers a new customer.
     *
     * @param customerRegistrationRequest the request containing the customer's registration information
     */
    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        customerService.addCustomer(customerRegistrationRequest);
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param id the ID of the customer to delete
     */
   @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable("id") Integer id) {
        customerService.deleteCustomerById(id);
   }


    /**
     * Updates the information of an existing customer.
     *
     * @param id the ID of the customer to update
     * @param updateCustomerRequest the request containing the updated customer information
     */
   @PutMapping("{id}")
    public void updateCustomerInformation(@PathVariable("id") Integer id,  @RequestBody CustomerUpdateRequest updateCustomerRequest) {
        customerService.updateCustomer(id, updateCustomerRequest);
   }
}
