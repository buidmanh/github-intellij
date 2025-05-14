package model;

public class Admin extends User {
    /**
     * Constructs an admin object.
     * @param id Must be unique, format: u_10 digits, such as u_1234567890
     * @param name The user's name
     * @param password The user's password
     */
    public Admin(String id, String name, String password) {
        super(id, name, password, "admin");
    }

    /**
     * Default constructor
     */
    public Admin() {
        super();
        setRole("admin");
    }

    /**
     * Returns the admin's attributes as a formatted string.
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        return String.format("Admin{id='%s', name='%s'}", 
            getId(), getName());
    }
} 