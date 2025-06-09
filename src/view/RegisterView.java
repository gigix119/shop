package view;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class RegisterView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox adminCheckBox;
    private UserRepository userRepository;

    public RegisterView(UserRepository userRepository) {
        this.userRepository = userRepository;

        setTitle("Rejestracja");
        setSize(300, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2));

        panel.add(new JLabel("Użytkownik:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Hasło:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Administrator:"));
        adminCheckBox = new JCheckBox();
        panel.add(adminCheckBox);

        JButton registerBtn = new JButton("Zarejestruj");
        registerBtn.addActionListener(e -> register());
        panel.add(registerBtn);

        add(panel);
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        boolean isAdmin = adminCheckBox.isSelected();

        List<User> users = userRepository.getAll();
        boolean exists = users.stream().anyMatch(u -> u.getUsername().equals(username));
        if (exists) {
            JOptionPane.showMessageDialog(this, "Użytkownik już istnieje");
        } else {
            String role = isAdmin ? "admin" : "user";
            users.add(new User(username, password, role));

            try {
                File original = new File("users.json");
                File backup = new File("users_backup.json");
                if (original.exists()) {
                    Files.copy(original.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                try (FileWriter writer = new FileWriter("users.json")) {
                    new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(users, writer);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, "Zarejestrowano użytkownika!");
            dispose();
        }
    }
}
