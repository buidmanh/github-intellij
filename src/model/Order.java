package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order implements Serializable {
    private String orderId;
    private String customerId;
    private String productId;
    private double price;
    private LocalDateTime createTime;

    /**
     * Constructs an order object.
     * @param orderId Must be a unique string, format is o_5 digits such as o_12345
     * @param customerId ID of the user who placed the order
     * @param productId ID of the product ordered
     * @param price Price of the product
     */
    public Order(String orderId, String customerId, String productId, double price) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.price = price;
        this.createTime = LocalDateTime.now();
    }

    /**
     * Default constructor
     */
    public Order() {
        this.orderId = "";
        this.customerId = "";
        this.productId = "";
        this.price = 0.0;
        this.createTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductId() {
        return productId;
    }

    public double getPrice() {
        return price;
    }

    public String getCreateTime() {
        return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * Returns the order information as a formatted string.
     * @return String in CSV format
     */
    @Override
    public String toString() {
        return String.format("Order ID: %s, Customer ID: %s, Product ID: %s, Price: $%.2f, Time: %s",
            orderId, customerId, productId, price, getCreateTime());
    }
} 