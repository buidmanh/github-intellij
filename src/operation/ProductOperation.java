package operation;

import model.Product;
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ProductOperation {
    private static ProductOperation instance;
    private static final String PRODUCTS_FILE = "data/products.txt";
    private static final int ITEMS_PER_PAGE = 10;
    private List<Product> products;

    private ProductOperation() {
        products = new ArrayList<>();
        loadProducts();
    }

    public static ProductOperation getInstance() {
        if (instance == null) {
            instance = new ProductOperation();
        }
        return instance;
    }

    public void extractProductsFromFiles() {
        // Implementation for extracting products from files
        // This would typically read from source files and save to products.txt
        try {
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Read all .txt files in the data directory
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt") && !name.equals("products.txt"));
            if (files != null) {
                for (File file : files) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Parse product data and add to products list
                            // Format: id,name,price,category
                            String[] parts = line.split(",");
                            if (parts.length >= 4) {
                                Product product = new Product(
                                    parts[0], // id
                                    parts[1], // name
                                    Double.parseDouble(parts[2]), // price
                                    parts[3]  // category
                                );
                                products.add(product);
                            }
                        }
                    }
                }
                saveProducts();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProductListResult getProductList(int pageNumber) {
        int startIndex = (pageNumber - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, products.size());
        int totalPages = (int) Math.ceil((double) products.size() / ITEMS_PER_PAGE);

        List<Product> pageProducts = startIndex < products.size() 
            ? products.subList(startIndex, endIndex)
            : new ArrayList<>();

        return new ProductListResult(pageProducts, pageNumber, totalPages);
    }

    public boolean deleteProduct(String productId) {
        boolean removed = products.removeIf(p -> p.getId().equals(productId));
        if (removed) {
            saveProducts();
        }
        return removed;
    }

    public List<Product> getProductListByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public Product getProductById(String productId) {
        return products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public void deleteAllProducts() {
        products.clear();
        saveProducts();
    }

    private void loadProducts() {
        File productsFile = new File(PRODUCTS_FILE);
        if (!productsFile.exists()) {
            // If products.txt doesn't exist, create it with sample data
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
                e.printStackTrace();
            }
        }

        // Now load the products
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        Product product = new Product(
                            parts[0].trim(), // id
                            parts[1].trim(), // name
                            Double.parseDouble(parts[2].trim()), // price
                            parts[3].trim()  // category
                        );
                        products.add(product);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing product price: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading products: " + e.getMessage());
            products = new ArrayList<>();
        }
    }

    private void saveProducts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product product : products) {
                writer.println(String.format("%s,%s,%.2f,%s",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getCategory()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getCategories() {
        return products.stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean addProduct(String name, double price, String category) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (price <= 0) {
            return false;
        }
        if (category == null || category.trim().isEmpty()) {
            return false;
        }

        // Generate unique product ID
        Set<String> existingIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());
        
        String productId;
        do {
            productId = String.format("p_%010d", ThreadLocalRandom.current().nextInt(1000000000));
        } while (existingIds.contains(productId));

        Product product = new Product(productId, name.trim(), price, category.trim());
        products.add(product);
        saveProducts();
        return true;
    }
} 