package operation;

import model.User;
import model.Customer;
import model.Admin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class UserOperation {
    private static UserOperation instance;
    private static final String USERS_FILE = "data/users.txt";
    private List<User> users;
    private Set<String> usedUserIds;

    private UserOperation() {
        users = new ArrayList<>();
        usedUserIds = new HashSet<>();
        loadUsers();
        
        // Create default admin if no users exist
        if (users.isEmpty()) {
            Admin admin = new Admin("admin", "admin", "admin123");
            users.add(admin);
            usedUserIds.add(admin.getId());
            saveUsers();
        }
    }

    /**
     * Returns the single instance of UserOperation.
     * @return UserOperation instance
     */
    public static UserOperation getInstance() {
        if (instance == null) {
            instance = new UserOperation();
        }
        return instance;
    }

    /**
     * Verify whether a user is already registered or exists in the system.
     * @param userName The username to check
     * @return true if exists, false otherwise
     */
    public boolean checkUsernameExist(String userName) {
        return users.stream().anyMatch(u -> u.getName().equals(userName));
    }

    /**
     * Validate the user's name.
     * @param userName The username to validate
     * @return true if valid, false otherwise
     */
    public boolean validateUsername(String userName) {
        return userName != null && 
               userName.length() >= 5 && 
               Pattern.matches("^[a-zA-Z_]+$", userName);
    }

    /**
     * Validate the user's password.
     * @param userPassword The password to validate
     * @return true if valid, false otherwise
     */
    public boolean validatePassword(String userPassword) {
        if (userPassword == null || userPassword.length() < 5) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasNumber = false;
        
        for (char c : userPassword.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }
        
        return hasLetter && hasNumber;
    }

    /**
     * Verify the provided user's name and password combination.
     * @param username The username for login
     * @param password The password for login
     * @return A User object (Customer or Admin) if successful, null otherwise
     */
    public User login(String username, String password) {
        return users.stream()
                .filter(u -> u.getName().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public boolean register(String username, String password, String email) {
        // Check if username already exists
        if (users.stream().anyMatch(u -> u.getName().equals(username))) {
            return false;
        }

        // Generate unique user ID
        String userId;
        do {
            int number = ThreadLocalRandom.current().nextInt(1000000000, 2000000000);
            userId = String.format("u_%010d", number);
        } while (usedUserIds.contains(userId));

        // Create new customer
        Customer customer = new Customer(userId, username, password, email);
        users.add(customer);
        usedUserIds.add(userId);
        saveUsers();
        return true;
    }

    public void addUser(User user) {
        users.add(user);
        usedUserIds.add(user.getId());
        saveUsers();
    }

    public List<Customer> getAllCustomers() {
        return users.stream()
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .collect(Collectors.toList());
    }

    public List<Admin> getAllAdmins() {
        return users.stream()
                .filter(u -> u instanceof Admin)
                .map(u -> (Admin) u)
                .collect(Collectors.toList());
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    User user;
                    if (parts[3].equals("admin")) {
                        user = new Admin(parts[0], parts[1], parts[2]);
                    } else {
                        // For customers, we need the email field
                        if (parts.length >= 5) {
                            user = new Customer(parts[0], parts[1], parts[2], parts[4]);
                        } else {
                            // Skip invalid customer entries
                            continue;
                        }
                    }
                    users.add(user);
                    usedUserIds.add(user.getId());
                }
            }
        } catch (IOException e) {
            // File might not exist yet, which is okay
            users = new ArrayList<>();
            usedUserIds = new HashSet<>();
        }
    }

    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                if (user instanceof Customer) {
                    Customer customer = (Customer) user;
                    writer.println(String.format("%s,%s,%s,%s,%s",
                        user.getId(),
                        user.getName(),
                        user.getPassword(),
                        user.getRole(),
                        customer.getEmail()));
                } else {
                    writer.println(String.format("%s,%s,%s,%s",
                        user.getId(),
                        user.getName(),
                        user.getPassword(),
                        user.getRole()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 