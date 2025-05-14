package operation;

import model.Order;
import model.Product;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

public class OrderOperation {
    private static OrderOperation instance;
    private static final String ORDERS_FILE = "data/orders.txt";
    private static final int ITEMS_PER_PAGE = 10;
    private List<Order> orders;
    private final ProductOperation productOperation;
    private Set<String> usedOrderIds;

    private OrderOperation() {
        orders = new ArrayList<>();
        productOperation = ProductOperation.getInstance();
        usedOrderIds = new HashSet<>();
        loadOrders();
    }

    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }

    private String generateUniqueOrderId() {
        Set<String> existingIds = orders.stream()
                .map(Order::getOrderId)
                .collect(Collectors.toSet());
                
        String orderId;
        do {
            int number = ThreadLocalRandom.current().nextInt(1000000000, 2000000000);
            orderId = String.format("o_%010d", number);
        } while (existingIds.contains(orderId));
        
        return orderId;
    }

    public boolean createOrder(String customerId, String productId) {
        Product product = productOperation.getProductById(productId);
        if (product == null) {
            return false;
        }

        // Generate unique order ID
        final String orderId = generateUniqueOrderId();

        // Create and save order
        Order order = new Order(orderId, customerId, productId, product.getPrice());
        orders.add(order);
        saveOrders();
        return true;
    }

    public boolean deleteOrder(String orderId) {
        boolean removed = orders.removeIf(o -> o.getOrderId().equals(orderId));
        if (removed) {
            usedOrderIds.remove(orderId);
            saveOrders();
        }
        return removed;
    }

    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> filteredOrders;
        if (customerId != null && !customerId.isEmpty()) {
            filteredOrders = orders.stream()
                    .filter(o -> o.getCustomerId().equals(customerId))
                    .collect(Collectors.toList());
        } else {
            filteredOrders = new ArrayList<>(orders);
        }

        int startIndex = (pageNumber - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredOrders.size());
        int totalPages = (int) Math.ceil((double) filteredOrders.size() / ITEMS_PER_PAGE);

        List<Order> pageOrders = startIndex < filteredOrders.size() 
            ? filteredOrders.subList(startIndex, endIndex)
            : new ArrayList<>();

        return new OrderListResult(pageOrders, pageNumber, totalPages);
    }

    public void generateTestOrderData() {
        // Generate 10 customer IDs
        List<String> customerIds = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            customerIds.add(String.format("c_%03d", i));
        }

        // Generate random number of orders (50-200) for each customer
        for (String customerId : customerIds) {
            int numOrders = ThreadLocalRandom.current().nextInt(50, 201);
            for (int i = 0; i < numOrders; i++) {
                // Generate random product ID
                String productId = String.format("p_%03d", 
                    ThreadLocalRandom.current().nextInt(1, 101));

                // Generate random date within the year
                int year = 2024;
                int month = ThreadLocalRandom.current().nextInt(1, 13);
                int day = ThreadLocalRandom.current().nextInt(1, 29);
                int hour = ThreadLocalRandom.current().nextInt(0, 24);
                int minute = ThreadLocalRandom.current().nextInt(0, 60);
                
                LocalDateTime orderTime = LocalDateTime.of(year, month, day, hour, minute);
                String createTime = orderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                
                createOrder(customerId, productId);
            }
        }
    }

    public void generateAllTop10BestSellersFigure() {
        // Count product occurrences
        Map<String, Long> productCounts = orders.stream()
                .collect(Collectors.groupingBy(
                    Order::getProductId,
                    Collectors.counting()
                ));

        // Sort by count in descending order and get top 10
        List<Map.Entry<String, Long>> top10 = productCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        // Generate simple text-based chart
        try (PrintWriter writer = new PrintWriter(new FileWriter("top10_bestsellers.txt"))) {
            writer.println("Top 10 Best-Selling Products");
            writer.println("===========================");
            
            for (Map.Entry<String, Long> entry : top10) {
                String productId = entry.getKey();
                long count = entry.getValue();
                String bar = "=".repeat((int) (count / 10)); // Scale the bars
                writer.printf("%s: %s (%d orders)%n", productId, bar, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllOrders() {
        orders.clear();
        usedOrderIds.clear();
        saveOrders();
    }

    private void loadOrders() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE))) {
            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String orderId = parts[0];
                    String customerId = parts[1];
                    String productId = parts[2];
                    double price = 0.0;
                    LocalDateTime createTime = LocalDateTime.now();
                    try {
                        // Try to parse price as a double
                        price = Double.parseDouble(parts[3]);
                        // If there is a 5th part, try to parse createTime
                        if (parts.length >= 5) {
                            createTime = LocalDateTime.parse(parts[4], formatter);
                        }
                    } catch (NumberFormatException e) {
                        // If not a double, treat as createTime and try to parse price from 5th part
                        createTime = LocalDateTime.parse(parts[3], formatter);
                        if (parts.length >= 5) {
                            price = Double.parseDouble(parts[4]);
                        }
                    }
                    Order order = new Order(orderId, customerId, productId, price);
                    order.setCreateTime(createTime);
                    orders.add(order);
                    usedOrderIds.add(order.getOrderId());
                }
            }
        } catch (IOException e) {
            // File might not exist yet, which is okay
            orders = new ArrayList<>();
            usedOrderIds = new HashSet<>();
        }
    }

    private void saveOrders() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : orders) {
                writer.println(String.format("%s,%s,%s,%.2f",
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getProductId(),
                    order.getPrice()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 