package operation;

import model.Customer;
import model.User;
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
    private final UserOperation userOperation;

    private CustomerOperation() {
        userOperation = UserOperation.getInstance();
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
     * Save the information of the new customer into the data/users.txt file.
     * @param username Customer's username
     * @param password Customer's password
     * @param email Customer's email
     * @return true if success, false if failure
     */
    public boolean registerCustomer(String username, String password, String email) {
        return userOperation.register(username, password, email);
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
                isValid = userOperation.validateUsername(value) &&
                         !userOperation.checkUsernameExist(value);
                if (isValid) customerObject.setName(value);
                break;
            case "password":
                isValid = userOperation.validatePassword(value);
                if (isValid) customerObject.setPassword(value);
                break;
            case "email":
                isValid = validateEmail(value);
                if (isValid) customerObject.setEmail(value);
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
        List<Customer> customers = userOperation.getAllCustomers();
        int totalPages = (int) Math.ceil((double) customers.size() / CUSTOMERS_PER_PAGE);
        
        if (pageNumber < 1) pageNumber = 1;
        if (pageNumber > totalPages) pageNumber = totalPages;

        int start = (pageNumber - 1) * CUSTOMERS_PER_PAGE;
        int end = Math.min(start + CUSTOMERS_PER_PAGE, customers.size());
        
        List<Customer> pageCustomers = customers.subList(start, end);
        return new CustomerListResult(pageCustomers, pageNumber, totalPages);
    }

    /**
     * Removes all the customers from the data/users.txt file.
     */
    public void deleteAllCustomers() {
        List<Customer> customers = userOperation.getAllCustomers();
        for (Customer customer : customers) {
            deleteCustomer(customer.getId());
        }
    }

    // Helper method to update customer in file
    private boolean updateCustomerInFile(Customer customer) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_id\":\"" + customer.getId() + "\"")) {
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

    public Customer getCustomerById(String customerId) {
        return userOperation.getAllCustomers().stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst()
                .orElse(null);
    }

    public List<Customer> getAllCustomers() {
        return userOperation.getAllCustomers();
    }
} 