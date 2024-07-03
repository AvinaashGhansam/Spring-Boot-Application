package com.ag.dto;

/**
 * A DTO (Data Transfer Object) representing a customer registration request.
 *
 * <p>This record holds the necessary information for registering a new customer,
 * including their name, email, and age.</p>
 *
 * @param name the name of the customer
 * @param email the email address of the customer
 * @param age the age of the customer
 */
public record CustomerRegistrationRequest (
    String name,
    String email,
    Integer age
) {}
