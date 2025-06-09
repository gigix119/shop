package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    private List<Order> orders;
    private String filePath;

    public OrderRepository() {
        this("orders.json");
    }

    public OrderRepository(String filePath) {
        this.filePath = filePath;
        this.orders = loadOrders();
    }

    private List<Order> loadOrders() {
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Order>>() {}.getType();
            List<Order> loadedOrders = gson.fromJson(reader, listType);
            return loadedOrders != null ? loadedOrders : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void addOrder(Order order) {
        orders.add(order);
        saveAll();
    }

    public List<Order> getAllOrders() {
        return orders;
    }

    private void saveAll() {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(orders, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

