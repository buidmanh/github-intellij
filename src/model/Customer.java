package model;

import java.io.Serializable;

public class Customer extends User implements Serializable {
    private String email;
    private String phoneNumber;

    /**
     * Constructs a customer object.
     * @param id Must be unique, format: u_10 digits, such as u_1234567890
     * @param name The user's name
     * @param password The user's password
     * @param email The customer's email address
     * @param phoneNumber The customer's phone number
     */
    public Customer(String id, String name, String password, String email, String phoneNumber) {
        super(id, name, password, "customer");
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Default constructor
     */
    public Customer() {
        super();
        setRole("customer");
        this.email = "";
        this.phoneNumber = "";
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the customer information as a formatted string.
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        return String.format("Customer{id='%s', name='%s', email='%s', phone='%s', password='%s', role='%s'}", 
            getId(), getName(), email, phoneNumber, getPassword(), getRole());
    }
} 