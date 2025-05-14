package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order implements Serializable {
    private String orderId;
    private String customerId;
    private String productId;
    private LocalDateTime createTime;

    /**
     * Constructs an order object.
     * @param orderId Must be a unique string, format is o_5 digits such as o_12345
     * @param customerId ID of the user who placed the order
     * @param productId ID of the product ordered
     * @param createTime Format: "DD-MM-YYYY_HH:MM:SS"
     */
    public Order(String orderId, String customerId, String productId, LocalDateTime createTime) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.createTime = createTime;
    }

    /**
     * Default constructor
     */
    public Order() {
        this.orderId = "";
        this.customerId = "";
        this.productId = "";
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

    public LocalDateTime getCreateTime() {
        return createTime;
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

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * Returns the order information as a formatted string.
     * @return String in CSV format
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s,%s,%s,%s",
            orderId,
            customerId,
            productId,
            createTime.format(formatter));
    }
} 