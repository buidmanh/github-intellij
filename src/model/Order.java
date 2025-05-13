package model;

public class Order {
    private String orderId;
    private String userId;
    private String proId;
    private String orderTime;

    /**
     * Constructs an order object.
     * @param orderId Must be a unique string, format is o_5 digits such as o_12345
     * @param userId ID of the user who placed the order
     * @param proId ID of the product ordered
     * @param orderTime Format: "DD-MM-YYYY_HH:MM:SS"
     */
    public Order(String orderId, String userId, String proId, String orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.proId = proId;
        this.orderTime = orderTime;
    }

    /**
     * Default constructor
     */
    public Order() {
        this.orderId = "";
        this.userId = "";
        this.proId = "";
        this.orderTime = "";
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    /**
     * Returns the order information as a formatted string.
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        return String.format("{\"order_id\":\"%s\", \"user_id\":\"%s\", \"pro_id\":\"%s\", \"order_time\":\"%s\"}",
                orderId, userId, proId, orderTime);
    }
} 