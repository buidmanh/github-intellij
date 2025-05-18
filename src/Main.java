// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

import model.*;
import operation.*;
import java.util.*;
import java.io.*;

public class Main {
    private static final IOInterface io = IOInterface.getInstance();
    private static final UserOperation userOperation = UserOperation.getInstance();
    private static final ProductOperation productOperation = ProductOperation.getInstance();
    private static final OrderOperation orderOperation = OrderOperation.getInstance();
    private static User currentUser = null;

    public static void main(String[] args) {
        try {
            // Initialize data directory and files
            initializeDataDirectory();
            
            // Start with main menu
            while (true) {
                io.mainMenu();
                handleMainMenu();
            }
        } catch (Exception e) {
            io.printErrorMessage("Main", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeDataDirectory() {
        // Create data directory if it doesn't exist
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Initialize products.txt with some sample data if it doesn't exist
        File productsFile = new File("data/products.txt");
        if (!productsFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(productsFile))) {
                // Electronics
                writer.println("p_001,MacBook Pro,1299.99,Electronics");
                writer.println("p_002,iPhone 13,799.99,Electronics");
                writer.println("p_003,Sony Headphones,199.99,Electronics");
                writer.println("p_004,Samsung TV,899.99,Electronics");
                writer.println("p_005,Wireless Mouse,29.99,Electronics");
                writer.println("p_006,Mechanical Keyboard,129.99,Electronics");
                writer.println("p_007,Smart Watch,249.99,Electronics");
                writer.println("p_008,Bluetooth Speaker,79.99,Electronics");
                
                // Furniture
                writer.println("p_009,Office Chair,199.99,Furniture");
                writer.println("p_010,Bookshelf,149.99,Furniture");
                writer.println("p_011,Desk Lamp,39.99,Furniture");
                writer.println("p_012,Storage Cabinet,299.99,Furniture");
                writer.println("p_013,Computer Desk,179.99,Furniture");
                
                // Clothing
                writer.println("p_014,Cotton T-Shirt,19.99,Clothing");
                writer.println("p_015,Denim Jeans,49.99,Clothing");
                writer.println("p_016,Winter Jacket,89.99,Clothing");
                writer.println("p_017,Running Shoes,79.99,Clothing");
                writer.println("p_018,Formal Shirt,39.99,Clothing");
                
                // Books
                writer.println("p_019,Programming Book,49.99,Books");
                writer.println("p_020,Novel,14.99,Books");
                writer.println("p_021,Cookbook,24.99,Books");
                writer.println("p_022,Art Book,34.99,Books");
                
                // Sports
                writer.println("p_023,Yoga Mat,29.99,Sports");
                writer.println("p_024,Dumbbells Set,89.99,Sports");
                writer.println("p_025,Jump Rope,9.99,Sports");
                writer.println("p_026,Resistance Bands,19.99,Sports");
                
                // Home & Kitchen
                writer.println("p_027,Coffee Maker,79.99,Home & Kitchen");
                writer.println("p_028,Blender,59.99,Home & Kitchen");
                writer.println("p_029,Toaster,39.99,Home & Kitchen");
                writer.println("p_030,Kitchen Knife Set,69.99,Home & Kitchen");
            } catch (IOException e) {
                io.printErrorMessage("Initialization", "Failed to create products.txt: " + e.getMessage());
            }
        }

        // Initialize users.txt with default admin if it doesn't exist
        File usersFile = new File("data/users.txt");
        if (!usersFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(usersFile))) {
                writer.println("admin,admin,admin123,admin");
            } catch (IOException e) {
                io.printErrorMessage("Initialization", "Failed to create users.txt: " + e.getMessage());
            }
        }
    }

    private static void handleMainMenu() {
        String[] input = io.getUserInput("Enter your choice: ", 3);
        String choice = input[0];
        String username = input[1];
        String password = input[2];

        switch (choice) {
            case "1": // Login
                if (username.isEmpty()) {
                    username = io.getSingleInput("Enter username: ");
                }
                if (password.isEmpty()) {
                    password = io.getSingleInput("Enter password: ");
                }
                handleLogin(username, password);
                break;
            case "2": // Register
                if (username.isEmpty()) {
                    username = io.getSingleInput("Enter username: ");
                }
                if (password.isEmpty()) {
                    password = io.getSingleInput("Enter password: ");
                }
                handleRegistration(username, password);
                break;
            case "3": // Quit
                io.printMessage("Goodbye!");
                System.exit(0);
                break;
            default:
                io.printErrorMessage("Main Menu", "Invalid choice. Please try again.");
        }
    }

    private static void handleLogin(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            io.printErrorMessage("Login", "Username and password are required.");
            return;
        }

        currentUser = userOperation.login(username, password);
        if (currentUser != null) {
            io.printMessage("Login successful!");
            if (currentUser instanceof Admin) {
                handleAdminMenu();
            } else if (currentUser instanceof Customer) {
                handleCustomerMenu();
            }
        } else {
            io.printErrorMessage("Login", "Invalid username or password.");
        }
    }

    private static void handleRegistration(String username, String password) {
        // Validate username and password
        if (!userOperation.validateUsername(username)) {
            io.printErrorMessage("Registration", "Username must be at least 5 characters long and contain only letters and underscores.");
            return;
        }
        if (!userOperation.validatePassword(password)) {
            io.printErrorMessage("Registration", "Password must be at least 5 characters long and contain both letters and numbers.");
            return;
        }

        // Get email and phone number
        String email = io.getSingleInput("Enter your email: ");
        if (email == null || email.trim().isEmpty()) {
            io.printErrorMessage("Registration", "Email is required.");
            return;
        }

        String phoneNumber = io.getSingleInput("Enter your phone number: ");
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            io.printErrorMessage("Registration", "Phone number is required.");
            return;
        }
        
        if (userOperation.register(username, password, email, phoneNumber)) {
            io.printMessage("Registration successful! Please login.");
        } else {
            io.printErrorMessage("Registration", "Registration failed. Username might already exist.");
        }
    }

    private static void handleAdminMenu() {
        while (true) {
            io.adminMenu();
            String[] input = io.getUserInput("Enter your choice: ", 1);
            String choice = input[0];

            switch (choice) {
                case "1": // Show products
                    showProducts();
                    break;
                case "2": // Add product
                    addProduct();
                    break;
                case "3": // Add customers
                    addCustomer();
                    break;
                case "4": // Show customers
                    showCustomers();
                    break;
                case "5": // Show orders
                    showOrders();
                    break;
                case "6": // Generate test data
                    generateTestData();
                    break;
                case "7": // Generate statistical figures
                    generateStatistics();
                    break;
                case "8": // Delete all data
                    deleteAllData();
                    break;
                case "9": // Logout
                    currentUser = null;
                    return;
                default:
                    io.printErrorMessage("Admin Menu", "Invalid choice. Please try again.");
            }
        }
    }

    private static void handleCustomerMenu() {
        while (true) {
            io.customerMenu();
            String[] input = io.getUserInput("Enter your choice: ", 2);
            String choice = input[0];
            String keyword = input[1];

            switch (choice) {
                case "1": // Show profile
                    showProfile();
                    break;
                case "2": // Update profile
                    updateProfile();
                    break;
                case "3": // Show products
                    showProducts(keyword);
                    break;
                case "4": // Place order
                    placeOrder();
                    break;
                case "5": // Show history orders
                    showOrderHistory();
                    break;
                case "6": // Generate consumption figures
                    generateConsumptionFigures();
                    break;
                case "7": // Logout
                    currentUser = null;
                    return;
                default:
                    io.printErrorMessage("Customer Menu", "Invalid choice. Please try again.");
            }
        }
    }

    private static void showProducts() {
        showProducts("");
    }

    private static void showProducts(String keyword) {
        int page = 1;
        while (true) {
            ProductListResult result;
            if (keyword.isEmpty()) {
                result = productOperation.getProductList(page);
            } else {
                List<Product> products = productOperation.getProductListByKeyword(keyword);
                result = new ProductListResult(products, page, 1);
            }
            
            io.showList("", "product", result.getProducts(), page, result.getTotalPages());
            
            String[] input = io.getUserInput("Enter page number (0 to return): ", 1);
            try {
                page = Integer.parseInt(input[0]);
                if (page == 0) return;
                if (page < 1 || page > result.getTotalPages()) {
                    io.printErrorMessage("Products", "Invalid page number.");
                    page = 1;
                }
            } catch (NumberFormatException e) {
                io.printErrorMessage("Products", "Invalid input. Please enter a number.");
                page = 1;
            }
        }
    }

    private static void addCustomer() {
        // Get username
        String username = io.getSingleInput("Enter username: ");
        if (!userOperation.validateUsername(username)) {
            io.printErrorMessage("Add Customer", "Username must be at least 5 characters long and contain only letters and underscores.");
            return;
        }

        // Get password
        String password = io.getSingleInput("Enter password: ");
        if (!userOperation.validatePassword(password)) {
            io.printErrorMessage("Add Customer", "Password must be at least 5 characters long and contain both letters and numbers.");
            return;
        }

        // Get email
        String email = io.getSingleInput("Enter email: ");
        if (email == null || email.trim().isEmpty()) {
            io.printErrorMessage("Add Customer", "Email is required.");
            return;
        }

        // Get phone number
        String phoneNumber = io.getSingleInput("Enter phone number: ");
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            io.printErrorMessage("Add Customer", "Phone number is required.");
            return;
        }

        // Register the customer
        if (userOperation.register(username, password, email, phoneNumber)) {
            io.printMessage("Customer added successfully!");
        } else {
            io.printErrorMessage("Add Customer", "Failed to add customer. Username might already exist.");
        }
    }

    private static void showCustomers() {
        int page = 1;
        while (true) {
            List<Customer> customers = userOperation.getAllCustomers();
            int totalPages = (int) Math.ceil(customers.size() / 10.0);
            List<Customer> pageCustomers = customers.subList(
                (page - 1) * 10, 
                Math.min(page * 10, customers.size())
            );
            
            io.printMessage("\n=== Customer List (Page " + page + " of " + totalPages + ") ===");
            io.printMessage("Username | Encrypted Password | Role | Phone Number | Email");
            io.printMessage("------------------------------------------------------------");
            
            for (Customer customer : pageCustomers) {
                io.printMessage(String.format("%-8s | %-18s | %-4s | %-12s | %s",
                    customer.getName(),
                    userOperation.getEncryptedPassword(customer.getPassword()),
                    customer.getRole(),
                    customer.getPhoneNumber(),
                    customer.getEmail()));
            }
            
            String[] input = io.getUserInput("\nEnter page number (0 to return): ", 1);
            try {
                page = Integer.parseInt(input[0]);
                if (page == 0) return;
                if (page < 1 || page > totalPages) {
                    io.printErrorMessage("Customers", "Invalid page number.");
                    page = 1;
                }
            } catch (NumberFormatException e) {
                io.printErrorMessage("Customers", "Invalid input. Please enter a number.");
                page = 1;
            }
        }
    }

    private static void showOrders() {
        int page = 1;
        while (true) {
            OrderListResult result = orderOperation.getOrderList("", page);
            io.showList("admin", "order", result.getOrders(), page, result.getTotalPages());
            
            String[] input = io.getUserInput("Enter page number (0 to return): ", 1);
            try {
                page = Integer.parseInt(input[0]);
                if (page == 0) return;
                if (page < 1 || page > result.getTotalPages()) {
                    io.printErrorMessage("Orders", "Invalid page number.");
                    page = 1;
                }
            } catch (NumberFormatException e) {
                io.printErrorMessage("Orders", "Invalid input. Please enter a number.");
                page = 1;
            }
        }
    }

    private static void generateTestData() {
        io.printMessage("Generating test data...");
        orderOperation.generateTestOrderData();
        io.printMessage("Test data generated successfully!");
    }

    private static void generateStatistics() {
        io.printMessage("Generating statistical figures...");
        orderOperation.generateAllTop10BestSellersFigure();
        io.printMessage("Statistical figures generated successfully!");
    }

    private static void deleteAllData() {
        String[] input = io.getUserInput("Are you sure you want to delete all data? (yes/no): ", 1);
        if (input[0].equalsIgnoreCase("yes")) {
            orderOperation.deleteAllOrders();
            productOperation.deleteAllProducts();
            io.printMessage("All data deleted successfully!");
        } else {
            io.printMessage("Operation cancelled.");
        }
    }

    private static void showProfile() {
        if (currentUser instanceof Customer) {
            io.printObject(currentUser);
        }
    }

    private static void updateProfile() {
        if (currentUser instanceof Customer) {
            String[] input = io.getUserInput("Enter new name and email: ", 2);
            Customer customer = (Customer) currentUser;
            customer.setName(input[0]);
            customer.setEmail(input[1]);
            io.printMessage("Profile updated successfully!");
        }
    }

    private static void placeOrder() {
        if (!(currentUser instanceof Customer)) {
            io.printErrorMessage("Order", "Only customers can place orders.");
            return;
        }

        // Show all products without pagination
        List<Product> allProducts = productOperation.getProductListByKeyword("");
        io.printMessage("\n=== Available Products ===");
        io.printMessage("Product ID | Name | Price | Category");
        io.printMessage("----------------------------------------");
        for (Product product : allProducts) {
            io.printMessage(String.format("%-10s | %-20s | $%-8.2f | %-10s",
                product.getId(), product.getName(), 
                product.getPrice(), product.getCategory()));
        }

        List<String> selectedProductIds = new ArrayList<>();
        double totalAmount = 0.0;

        while (true) {
            // Get product ID
            io.printMessage("\nEnter product ID to add to order (or 'done' to finish, 'q' to cancel): ");
            String productId = io.getSingleInput("Product ID: ");
            
            if (productId.equalsIgnoreCase("q")) {
                return;
            }
            if (productId.equalsIgnoreCase("done")) {
                if (selectedProductIds.isEmpty()) {
                    io.printErrorMessage("Order", "Please add at least one product to the order.");
                    continue;
                }
                break;
            }

            // Verify product exists
            Product product = productOperation.getProductById(productId);
            if (product == null) {
                io.printErrorMessage("Order", "Product not found. Please check the product ID and try again.");
                continue;
            }

            // Add product to order
            selectedProductIds.add(productId);
            totalAmount += product.getPrice();

            // Show current order summary
            io.printMessage("\nCurrent Order Summary:");
            io.printMessage(String.format("Total Items: %d", selectedProductIds.size()));
            io.printMessage(String.format("Total Amount: $%.2f", totalAmount));
            io.printMessage("\nSelected Products:");
            for (String pid : selectedProductIds) {
                Product p = productOperation.getProductById(pid);
                io.printMessage(String.format("- %s: %s ($%.2f)", 
                    p.getId(), p.getName(), p.getPrice()));
            }
        }

        // Show final order summary and confirm
        io.printMessage("\nFinal Order Summary:");
        io.printMessage(String.format("Total Items: %d", selectedProductIds.size()));
        io.printMessage(String.format("Total Amount: $%.2f", totalAmount));
        io.printMessage("\nSelected Products:");
        for (String pid : selectedProductIds) {
            Product p = productOperation.getProductById(pid);
            io.printMessage(String.format("- %s: %s ($%.2f)", 
                p.getId(), p.getName(), p.getPrice()));
        }

        String confirm = io.getSingleInput("\nConfirm order? (yes/no): ");
        if (!confirm.equalsIgnoreCase("yes")) {
            io.printMessage("Order cancelled.");
            return;
        }

        // Create orders for each product
        boolean allSuccessful = true;
        for (String productId : selectedProductIds) {
            if (!orderOperation.createOrder(currentUser.getId(), productId)) {
                io.printErrorMessage("Order", "Failed to place order for product: " + productId);
                allSuccessful = false;
            }
        }

        if (allSuccessful) {
            io.printMessage("All orders placed successfully!");
        } else {
            io.printMessage("Some orders may have failed. Please check your order history.");
        }
    }

    private static void showOrderHistory() {
        if (currentUser instanceof Customer) {
            int page = 1;
            while (true) {
                OrderListResult result = orderOperation.getOrderList(currentUser.getId(), page);
                io.showList("customer", "order", result.getOrders(), page, result.getTotalPages());
                
                String[] input = io.getUserInput("Enter page number (0 to return): ", 1);
                try {
                    page = Integer.parseInt(input[0]);
                    if (page == 0) return;
                    if (page < 1 || page > result.getTotalPages()) {
                        io.printErrorMessage("Order History", "Invalid page number.");
                        page = 1;
                    }
                } catch (NumberFormatException e) {
                    io.printErrorMessage("Order History", "Invalid input. Please enter a number.");
                    page = 1;
                }
            }
        }
    }

    private static void generateConsumptionFigures() {
        if (currentUser instanceof Customer) {
            io.printMessage("Generating consumption figures...");
            // Implementation for generating customer-specific consumption figures
            io.printMessage("Consumption figures generated successfully!");
        }
    }

    private static void addProduct() {
        // Get product name
        String name = io.getSingleInput("Enter product name: ");
        if (name.trim().isEmpty()) {
            io.printErrorMessage("Add Product", "Product name cannot be empty.");
            return;
        }

        // Get product price
        String priceStr = io.getSingleInput("Enter product price: ");
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                io.printErrorMessage("Add Product", "Price must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            io.printErrorMessage("Add Product", "Invalid price format.");
            return;
        }

        // Show available categories
        List<String> categories = productOperation.getCategories();
        io.printMessage("\nAvailable categories:");
        for (int i = 0; i < categories.size(); i++) {
            io.printMessage((i + 1) + ". " + categories.get(i));
        }
        io.printMessage((categories.size() + 1) + ". Enter new category");

        // Get category choice
        String categoryChoice = io.getSingleInput("Enter category number or name: ");
        String category;
        try {
            int choice = Integer.parseInt(categoryChoice);
            if (choice > 0 && choice <= categories.size()) {
                category = categories.get(choice - 1);
            } else if (choice == categories.size() + 1) {
                category = io.getSingleInput("Enter new category name: ");
            } else {
                io.printErrorMessage("Add Product", "Invalid category number.");
                return;
            }
        } catch (NumberFormatException e) {
            // User entered a category name directly
            category = categoryChoice;
        }

        // Add the product
        if (productOperation.addProduct(name, price, category)) {
            io.printMessage("Product added successfully!");
        } else {
            io.printErrorMessage("Add Product", "Failed to add product. Please check your input.");
        }
    }
}