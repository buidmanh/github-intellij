package operation;

import model.Product;
import java.io.*;
import java.util.*;
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
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
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
        } catch (IOException e) {
            // File might not exist yet, which is okay
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
} 