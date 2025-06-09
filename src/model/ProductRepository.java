package model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;

public class ProductRepository {
    private final String filePath;
    private final Gson gson;
    private List<Product> products;

    public ProductRepository(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.products = load();
    }

    private List<Product> load() {
        try (Reader r = new FileReader(filePath)) {
            return gson.fromJson(r, new TypeToken<List<Product>>(){}.getType());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<Product> getAll() { return products; }

    public void saveAll() {
        try (Writer w = new FileWriter(filePath)) {
            gson.toJson(products, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}