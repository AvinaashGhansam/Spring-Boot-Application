package com.ag.customer;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * This class represents a {@link Customer} entity with fields for ID, name, email, and age.
 * It includes basic operations such as getters, setters, and overrides for equals, hashCode, and toString methods.
 */
@Entity
public class Customer {
    @Id
    @SequenceGenerator(name = "customer_id_seq", sequenceName = "customer_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private Integer age;

    /**
     * Default constructor for JPA
     */
    public Customer() {}

    /**
     * Constructs a new {@link Customer} with specified id, name, email, and age
     * @param id id the unique identifier of the customer
     * @param name name the name of the customer
     * @param email email the email of the customer
     * @param age age the age of the customer
     */
    public Customer(Integer id, String name, String email, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    /**
     * Constructs a new {@link Customer} with the specified name, email, and age.
     * This constructor is typically used when creating a new Customer without specifying an ID.
     *
     * @param name the name of the customer
     * @param email the email address of the customer
     * @param age the age of the customer
     */
    public Customer(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    /**
     * Gets the ID of the customer.
     *
     * @return the ID of the customer
     */

    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of the customer.
     *
     * @param id the ID to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name of the customer
     * @return the name of the customer
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the customer
     * @param name the name of the customer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email of the customer
     * @return the customer email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the customer
     * @param email the email of the customer
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the age of the custer
     * @return the age of the customer
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the age of the customer
     * @param age age of the customer
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Compares this customer to the specified object for equality.
     *
     * @param o the object to compare this customer against
     * @return true if the specified object is equal to this customer, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(email, customer.email) && Objects.equals(age, customer.age);
    }

    /**
     * Returns a hash code value for the customer.
     *
     * @return a hash code value for this customer
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age);
    }

    /**
     * Returns a string representation of the customer.
     *
     * @return a string representation of the customer
     */
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
