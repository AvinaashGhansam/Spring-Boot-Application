package com.ag.customer.controller;

import com.ag.customer.service.CustomerService;
import com.ag.customer.Customer;
import com.ag.dto.CustomerRegistrationRequest;
import com.ag.dto.CustomerUpdateRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The controller will send data down to the service
 */
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{id}")
    public Customer getCustomerById(@PathVariable("id") Integer id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        customerService.addCustomer(customerRegistrationRequest);
    }

   @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable("id") Integer id) {
        customerService.deleteCustomerById(id);
   }

   @PutMapping("{id}")
    public void updateCustomerInformation(@PathVariable("id") Integer id,  @RequestBody CustomerUpdateRequest updateCustomerRequest) {
        customerService.updateCustomer(id, updateCustomerRequest);
   }
}
