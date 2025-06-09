package view;

import model.*;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserRepository userRepository;

    public LoginView(UserRepository userRepository) {
        this.userRepository = userRepository;

        setTitle("Logowanie");
        setSize(300, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2));

        panel.add(new JLabel("Użytkownik:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Hasło:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginBtn = new JButton("Zaloguj");
        loginBtn.addActionListener(e -> login());
        panel.add(loginBtn);

        JButton registerBtn = new JButton("Zarejestruj");
        registerBtn.addActionListener(e -> new RegisterView(userRepository).setVisible(true));
        panel.add(registerBtn);

        add(panel);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        User user = userRepository.authenticate(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Witaj " + user.getUsername());
            dispose();
            ProductRepository pr = new ProductRepository("products.json");
            if (user.isAdmin()) {
                new AdminPanel(pr).setVisible(true);
            }
            new ShopView(pr, user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Błędne dane logowania");
        }
    }
}