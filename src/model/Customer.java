package model;

import java.io.Serializable;

public class Customer implements Serializable {
    private String id;
    private String name;
    private String email;
    private String password;

    /**
     * Constructs a customer object.
     * @param id Must be unique, format: u_10 digits, such as u_1234567890
     * @param name The user's name
     * @param email The customer's email address
     * @param password The user's password
     */
    public Customer(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Default constructor
     */
    public Customer() {
        this.id = "";
        this.name = "";
        this.email = "";
        this.password = "";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the customer information as a formatted string.
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        return String.format("Customer{id='%s', name='%s', email='%s'}", 
            id, name, email);
    }
} 