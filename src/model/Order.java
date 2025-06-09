package model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String username;
    private List<CartItem> items;
    private double total;
    private String address;
    private LocalDateTime timestamp;
    private String paymentMethod;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Order(String username, List<CartItem> items, double total, String address, LocalDateTime timestamp) {
        this.username = username;
        this.items = items;
        this.total = total;
        this.address = address;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
