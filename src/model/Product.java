package model;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private double price;
    private String category;

    /**
     * Constructs a product object.
     * @param id Product ID (must be unique)
     * @param name Product name
     * @param price Current price of the product
     * @param category Product category
     */
    public Product(String id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    /**
     * Default constructor
     */
    public Product() {
        this.id = "";
        this.name = "";
        this.price = 0.0;
        this.category = "";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the product information as a formatted string.
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', price=%.2f, category='%s'}", 
            id, name, price, category);
    }
} 