package operation;

import model.User;
import model.Customer;
import model.Admin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class UserOperation {
    private static UserOperation instance;
    private static final String USER_FILE = "data/users.txt";
    private static final Random random = new Random();

    private UserOperation() {
        // Private constructor for singleton pattern
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
     * Generates and returns a 10-digit unique user id starting with 'u_'
     * every time when a new user is registered.
     * @return A string value in the format 'u_10digits', e.g., 'u_1234567890'
     */
    public String generateUniqueUserId() {
        StringBuilder userId = new StringBuilder("u_");
        for (int i = 0; i < 10; i++) {
            userId.append(random.nextInt(10));
        }
        return userId.toString();
    }

    /**
     * Encode a user-provided password.
     * @param userPassword The password to encrypt
     * @return Encrypted password
     */
    public String encryptPassword(String userPassword) {
        int passwordLength = userPassword.length();
        StringBuilder randomString = new StringBuilder();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        // Generate random string
        for (int i = 0; i < passwordLength * 2; i++) {
            randomString.append(chars.charAt(random.nextInt(chars.length())));
        }

        // Combine random string and password
        StringBuilder encrypted = new StringBuilder("^^");
        for (int i = 0; i < passwordLength; i++) {
            encrypted.append(randomString.substring(i * 2, i * 2 + 2))
                    .append(userPassword.charAt(i));
        }
        encrypted.append("$$");
        
        return encrypted.toString();
    }

    /**
     * Decode the encrypted password.
     * @param encryptedPassword The encrypted password to decrypt
     * @return Original user-provided password
     */
    public String decryptPassword(String encryptedPassword) {
        if (!encryptedPassword.startsWith("^^") || !encryptedPassword.endsWith("$$")) {
            return "";
        }
        
        String content = encryptedPassword.substring(2, encryptedPassword.length() - 2);
        StringBuilder decrypted = new StringBuilder();
        
        for (int i = 0; i < content.length(); i += 3) {
            if (i + 2 < content.length()) {
                decrypted.append(content.charAt(i + 2));
            }
        }
        
        return decrypted.toString();
    }

    /**
     * Verify whether a user is already registered or exists in the system.
     * @param userName The username to check
     * @return true if exists, false otherwise
     */
    public boolean checkUsernameExist(String userName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_name\":\"" + userName + "\"")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
     * @param userName The username for login
     * @param userPassword The password for login
     * @return A User object (Customer or Admin) if successful, null otherwise
     */
    public User login(String userName, String userPassword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_name\":\"" + userName + "\"")) {
                    // Extract user data from the line
                    String[] parts = line.split("\", \"");
                    String storedPassword = parts[2].split("\":\"")[1];
                    String userRole = parts[4].split("\":\"")[1].replace("\"}", "");
                    
                    // Decrypt stored password and compare
                    if (decryptPassword(storedPassword).equals(userPassword)) {
                        // Create appropriate user object based on role
                        if (userRole.equals("admin")) {
                            return new Admin(parts[0].split("\":\"")[1],
                                          userName,
                                          userPassword,
                                          parts[3].split("\":\"")[1],
                                          userRole);
                        } else {
                            return new Customer(parts[0].split("\":\"")[1],
                                             userName,
                                             userPassword,
                                             parts[3].split("\":\"")[1],
                                             userRole,
                                             parts[5].split("\":\"")[1],
                                             parts[6].split("\":\"")[1].replace("\"}", ""));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
} 