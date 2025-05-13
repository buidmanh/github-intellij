package operation;

import model.Customer;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CustomerOperation {
    private static CustomerOperation instance;
    private static final String USER_FILE = "data/users.txt";
    private static final int CUSTOMERS_PER_PAGE = 10;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");

    private CustomerOperation() {
        // Private constructor for singleton pattern
    }

    /**
     * Returns the single instance of CustomerOperation.
     * @return CustomerOperation instance
     */
    public static CustomerOperation getInstance() {
        if (instance == null) {
            instance = new CustomerOperation();
        }
        return instance;
    }

    /**
     * Validate the provided email address format.
     * @param userEmail The email to validate
     * @return true if valid, false otherwise
     */
    public boolean validateEmail(String userEmail) {
        if (userEmail == null) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, userEmail);
    }

    /**
     * Validate the provided mobile number format.
     * @param userMobile The mobile number to validate
     * @return true if valid, false otherwise
     */
    public boolean validateMobile(String userMobile) {
        if (userMobile == null) return false;
        return userMobile.length() == 10 && 
               userMobile.matches("^[0-9]+$") && 
               (userMobile.startsWith("04") || userMobile.startsWith("03"));
    }

    /**
     * Save the information of the new customer into the data/users.txt file.
     * @param userName Customer's username
     * @param userPassword Customer's password
     * @param userEmail Customer's email
     * @param userMobile Customer's mobile number
     * @return true if success, false if failure
     */
    public boolean registerCustomer(String userName, String userPassword,
                                  String userEmail, String userMobile) {
        // Validate all inputs
        if (!UserOperation.getInstance().validateUsername(userName) ||
            !UserOperation.getInstance().validatePassword(userPassword) ||
            !validateEmail(userEmail) ||
            !validateMobile(userMobile) ||
            UserOperation.getInstance().checkUsernameExist(userName)) {
            return false;
        }

        try {
            // Generate unique user ID and encrypt password
            String userId = UserOperation.getInstance().generateUniqueUserId();
            String encryptedPassword = UserOperation.getInstance().encryptPassword(userPassword);
            String registerTime = LocalDateTime.now().format(DATE_FORMATTER);

            // Create customer object
            Customer customer = new Customer(userId, userName, encryptedPassword,
                                          registerTime, "customer", userEmail, userMobile);

            // Append to file
            try (FileWriter fw = new FileWriter(USER_FILE, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(customer.toString());
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update the given customer object's attribute value.
     * @param attributeName The attribute to update
     * @param value The new value
     * @param customerObject The customer object to update
     * @return true if updated, false if failed
     */
    public boolean updateProfile(String attributeName, String value, Customer customerObject) {
        if (customerObject == null || value == null) return false;

        boolean isValid = false;
        switch (attributeName.toLowerCase()) {
            case "username":
                isValid = UserOperation.getInstance().validateUsername(value) &&
                         !UserOperation.getInstance().checkUsernameExist(value);
                if (isValid) customerObject.setUserName(value);
                break;
            case "password":
                isValid = UserOperation.getInstance().validatePassword(value);
                if (isValid) customerObject.setUserPassword(
                    UserOperation.getInstance().encryptPassword(value));
                break;
            case "email":
                isValid = validateEmail(value);
                if (isValid) customerObject.setUserEmail(value);
                break;
            case "mobile":
                isValid = validateMobile(value);
                if (isValid) customerObject.setUserMobile(value);
                break;
            default:
                return false;
        }

        if (isValid) {
            return updateCustomerInFile(customerObject);
        }
        return false;
    }

    /**
     * Delete the customer from the data/users.txt file.
     * @param customerId The ID of the customer to delete
     * @return true if deleted, false if failed
     */
    public boolean deleteCustomer(String customerId) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_id\":\"" + customerId + "\"")) {
                    found = true;
                    continue;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (found) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
                for (String line : lines) {
                    writer.println(line);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Retrieve one page of customers from the data/users.txt.
     * @param pageNumber The page number to retrieve
     * @return A CustomerListResult containing the list, current page, and total pages
     */
    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> customers = new ArrayList<>();
        List<String> allLines = new ArrayList<>();
        int totalPages = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_role\":\"customer\"")) {
                    allLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new CustomerListResult(customers, 1, 1);
        }

        totalPages = (int) Math.ceil((double) allLines.size() / CUSTOMERS_PER_PAGE);
        if (pageNumber < 1) pageNumber = 1;
        if (pageNumber > totalPages) pageNumber = totalPages;

        int start = (pageNumber - 1) * CUSTOMERS_PER_PAGE;
        int end = Math.min(start + CUSTOMERS_PER_PAGE, allLines.size());

        for (int i = start; i < end; i++) {
            String line = allLines.get(i);
            String[] parts = line.split("\", \"");
            customers.add(new Customer(
                parts[0].split("\":\"")[1],
                parts[1].split("\":\"")[1],
                parts[2].split("\":\"")[1],
                parts[3].split("\":\"")[1],
                parts[4].split("\":\"")[1],
                parts[5].split("\":\"")[1],
                parts[6].split("\":\"")[1].replace("\"}", "")
            ));
        }

        return new CustomerListResult(customers, pageNumber, totalPages);
    }

    /**
     * Removes all the customers from the data/users.txt file.
     */
    public void deleteAllCustomers() {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("\"user_role\":\"customer\"")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to update customer in file
    private boolean updateCustomerInFile(Customer customer) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_id\":\"" + customer.getUserId() + "\"")) {
                    lines.add(customer.toString());
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (found) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
                for (String line : lines) {
                    writer.println(line);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
} 