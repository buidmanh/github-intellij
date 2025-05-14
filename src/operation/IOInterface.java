package operation;

import java.util.*;
import model.Customer;
import model.Product;
import model.Order;

public class IOInterface {
    private static IOInterface instance;
    private final Scanner scanner;

    private IOInterface() {
        scanner = new Scanner(System.in);
    }

    public static IOInterface getInstance() {
        if (instance == null) {
            instance = new IOInterface();
        }
        return instance;
    }

    public String[] getUserInput(String message, int numOfArgs) {
        printMessage(message);
        String input = scanner.nextLine().trim();
        
        // If no input provided, return array of empty strings
        if (input.isEmpty()) {
            return new String[numOfArgs];
        }
        
        String[] args = input.split("\\s+", numOfArgs);
        
        // If fewer arguments than expected, fill with empty strings
        if (args.length < numOfArgs) {
            String[] filledArgs = new String[numOfArgs];
            System.arraycopy(args, 0, filledArgs, 0, args.length);
            Arrays.fill(filledArgs, args.length, numOfArgs, "");
            return filledArgs;
        }
        
        return args;
    }

    public String getSingleInput(String message) {
        printMessage(message);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            printMessage("Input cannot be empty. Please try again or press Enter to return to menu.");
            input = scanner.nextLine().trim();
        }
        return input;
    }

    public void mainMenu() {
        printMessage("\n=== Main Menu ===");
        printMessage("1. Login");
        printMessage("2. Register");
        printMessage("3. Quit");
        printMessage("\nFor login/register, enter your choice followed by username and password.");
        printMessage("Example: 1 admin admin123");
    }

    public void adminMenu() {
        printMessage("\n=== Admin Menu ===");
        printMessage("1. Show products");
        printMessage("2. Add product");
        printMessage("3. Add customers");
        printMessage("4. Show customers");
        printMessage("5. Show orders");
        printMessage("6. Generate test data");
        printMessage("7. Generate all statistical figures");
        printMessage("8. Delete all data");
        printMessage("9. Logout");
    }

    public void customerMenu() {
        printMessage("\n=== Customer Menu ===");
        printMessage("1. Show profile");
        printMessage("2. Update profile");
        printMessage("3. Show products");
        printMessage("4. Place order");
        printMessage("5. Show history orders");
        printMessage("6. Generate all consumption figures");
        printMessage("7. Logout");
    }

    public void showList(String userRole, String listType, List<?> objectList, 
                        int pageNumber, int totalPages) {
        printMessage(String.format("\n=== %s List (Page %d of %d) ===", 
            listType, pageNumber, totalPages));
        
        if (objectList.isEmpty()) {
            printMessage("No items to display.");
            return;
        }

        // Print header based on list type
        switch (listType.toLowerCase()) {
            case "customer":
                printMessage("Row | Customer ID | Name | Email");
                printMessage("--------------------------------");
                break;
            case "product":
                printMessage("Row | Product ID | Name | Price | Category");
                printMessage("----------------------------------------");
                break;
            case "order":
                printMessage("Row | Order ID | Customer ID | Product ID | Create Time");
                printMessage("------------------------------------------------");
                break;
        }

        // Print items
        for (int i = 0; i < objectList.size(); i++) {
            Object item = objectList.get(i);
            int rowNumber = (pageNumber - 1) * 10 + i + 1;
            
            if (item instanceof Customer) {
                Customer customer = (Customer) item;
                printMessage(String.format("%3d | %-10s | %-20s | %-30s",
                    rowNumber, customer.getId(), customer.getName(), customer.getEmail()));
            } else if (item instanceof Product) {
                Product product = (Product) item;
                printMessage(String.format("%3d | %-10s | %-20s | $%-8.2f | %-10s",
                    rowNumber, product.getId(), product.getName(), 
                    product.getPrice(), product.getCategory()));
            } else if (item instanceof Order) {
                Order order = (Order) item;
                printMessage(String.format("%3d | %-8s | %-12s | %-10s | %s",
                    rowNumber, order.getOrderId(), order.getCustomerId(),
                    order.getProductId(), order.getCreateTime()));
            }
        }
    }

    public void printErrorMessage(String errorSource, String errorMessage) {
        System.err.println(String.format("Error in %s: %s", errorSource, errorMessage));
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printObject(Object targetObject) {
        if (targetObject != null) {
            printMessage(targetObject.toString());
        } else {
            printMessage("null");
        }
    }
} 