package operation;

import model.Admin;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminOperation {
    private static AdminOperation instance;
    private static final String USER_FILE = "data/users.txt";
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");

    private AdminOperation() {
        // Private constructor for singleton pattern
    }

    /**
     * Returns the single instance of AdminOperation.
     * @return AdminOperation instance
     */
    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }

    /**
     * Creates an admin account. This function should be called when
     * the system starts. The same admin account should not be
     * registered multiple times.
     */
    public void registerAdmin() {
        // Check if admin already exists
        if (UserOperation.getInstance().checkUsernameExist(DEFAULT_ADMIN_USERNAME)) {
            return;
        }

        try {
            // Generate unique user ID and encrypt password
            String userId = UserOperation.getInstance().generateUniqueUserId();
            String encryptedPassword = UserOperation.getInstance().encryptPassword(DEFAULT_ADMIN_PASSWORD);
            String registerTime = LocalDateTime.now().format(DATE_FORMATTER);

            // Create admin object
            Admin admin = new Admin(userId, DEFAULT_ADMIN_USERNAME, encryptedPassword,
                                  registerTime, "admin");

            // Append to file
            try (FileWriter fw = new FileWriter(USER_FILE, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(admin.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 