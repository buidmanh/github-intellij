package operation;

import model.Admin;
import model.User;
import java.util.*;

public class AdminOperation {
    private static AdminOperation instance;
    private final UserOperation userOperation;

    private AdminOperation() {
        userOperation = UserOperation.getInstance();
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
    public boolean registerAdmin(String username, String password) {
        // Check if username already exists
        if (userOperation.checkUsernameExist(username)) {
            return false;
        }

        // Create new admin
        Admin admin = new Admin("admin", username, password);
        userOperation.addUser(admin);
        return true;
    }

    public Admin getAdminById(String adminId) {
        return userOperation.getAllAdmins().stream()
                .filter(a -> a.getId().equals(adminId))
                .findFirst()
                .orElse(null);
    }

    public List<Admin> getAllAdmins() {
        return userOperation.getAllAdmins();
    }
} 