package com.ag.dto;

/**
 * A DTO (Data Transfer Object) representing a request to update customer information.
 *
 * <p>This record holds the necessary information for updating an existing customer's details,
 * including their name, email, and age. All fields are optional and can be null if no update
 * is required for that particular field.</p>
 *
 * @param name the new name of the customer (optional)
 * @param email the new email address of the customer (optional)
 * @param age the new age of the customer (optional)
 */
public record CustomerUpdateRequest(String name, String email, Integer age) {
}
