package model;

public abstract class User {
    private String userId;
    private String userName;
    private String userPassword;
    private String userRegisterTime;
    private String userRole;

    /**
     * Constructs a user object.
     * @param userId Must be unique, format: u_10 digits, such as u_1234567890
     * @param userName The user's name
     * @param userPassword The user's password
     * @param userRegisterTime Format: "DD-MM-YYYY_HH:MM:SS"
     * @param userRole Default value: "customer"
     */
    public User(String userId, String userName, String userPassword,
                String userRegisterTime, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRegisterTime = userRegisterTime;
        this.userRole = userRole;
    }

    /**
     * Default constructor
     */
    public User() {
        this.userId = "";
        this.userName = "";
        this.userPassword = "";
        this.userRegisterTime = "";
        this.userRole = "customer";
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRegisterTime() {
        return userRegisterTime;
    }

    public void setUserRegisterTime(String userRegisterTime) {
        this.userRegisterTime = userRegisterTime;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    /**
     * Returns the user Information as a formatted string.
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        return String.format("{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", \"user_register_time\":\"%s\", \"user_role\":\"%s\"}",
                userId, userName, userPassword, userRegisterTime, userRole);
    }
} 