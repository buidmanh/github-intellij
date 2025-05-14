package operation;

import model.Order;
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
    private Set<String> usedOrderIds;

    private OrderOperation() {
        orders = new ArrayList<>();
        usedOrderIds = new HashSet<>();
        loadOrders();
    }

    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }

    public String generateUniqueOrderId() {
        String orderId;
        do {
            int number = ThreadLocalRandom.current().nextInt(10000, 100000);
            orderId = String.format("o_%05d", number);
        } while (usedOrderIds.contains(orderId));
        
        usedOrderIds.add(orderId);
        return orderId;
    }

    public boolean createAnOrder(String customerId, String productId, String createTime) {
        try {
            LocalDateTime orderTime = createTime != null 
                ? LocalDateTime.parse(createTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : LocalDateTime.now();
            
            String orderId = generateUniqueOrderId();
            Order order = new Order(orderId, customerId, productId, orderTime);
            orders.add(order);
            saveOrders();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
        List<Order> customerOrders = orders.stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .collect(Collectors.toList());

        int startIndex = (pageNumber - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, customerOrders.size());
        int totalPages = (int) Math.ceil((double) customerOrders.size() / ITEMS_PER_PAGE);

        List<Order> pageOrders = startIndex < customerOrders.size() 
            ? customerOrders.subList(startIndex, endIndex)
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
                
                createAnOrder(customerId, productId, createTime);
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
                    Order order = new Order(
                        parts[0], // orderId
                        parts[1], // customerId
                        parts[2], // productId
                        LocalDateTime.parse(parts[3], formatter) // createTime
                    );
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
                writer.println(order.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 