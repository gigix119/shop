package model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;

public class UserRepository {
    private final String filePath;
    private final Gson gson;
    private List<User> users;

    public UserRepository(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.users = load();
    }

    private List<User> load() {
        try (Reader r = new FileReader(filePath)) {
            return gson.fromJson(r, new TypeToken<List<User>>(){}.getType());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<User> getAll() { return users; }

    public User authenticate(String login, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(login) && u.getPassword().equals(password))
                .findFirst().orElse(null);
    }
}