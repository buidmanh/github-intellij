package model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String password;
    private String role;

    /**
     * Constructs a user object.
     * @param id Must be unique, format: u_10 digits, such as u_1234567890
     * @param name The user's name
     * @param password The user's password
     * @param role Default value: "customer"
     */
    public User(String id, String name, String password, String role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    /**
     * Default constructor
     */
    public User() {
        this.id = "";
        this.name = "";
        this.password = "";
        this.role = "customer";
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the user Information as a formatted string.
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', role='%s'}", 
            id, name, role);
    }
} 