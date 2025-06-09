package model;

public class Product {
    private String name;
    private double price;
    private int stock;
    private String category;


    public Product(String name, double price, int stock, String category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int s) {
        this.stock = s;
    }

    @Override
    public String toString() {
        return name + " - " + price + " zł (" + stock + " szt.)";
    }
}