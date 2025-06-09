package view;


import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import model.OrderRepository;
import view.RegisterView;

public class ShopView extends JFrame {
    private ProductRepository productRepository;
    private User user;
    private DefaultListModel<String> productModel;
    private JList<String> productList;
    private DefaultListModel<String> cartModel;
    private JList<String> cartList;
    private JTextField qtyField;
    private List<CartItem> cart;
    private OrderRepository orderRepository;

    public ShopView(ProductRepository repository, User user) {
        this.productRepository = repository;
        this.user = user;
        this.cart = new ArrayList<>();
        this.orderRepository = new OrderRepository();

        setTitle("Sklep - Witaj " + user.getUsername());
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        productModel = new DefaultListModel<>();
        productList = new JList<>(productModel);
        refreshProductList();

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Produkty"));
        centerPanel.add(new JScrollPane(productList), BorderLayout.CENTER);

        cartModel = new DefaultListModel<>();
        cartList = new JList<>(cartModel);
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = cartList.getSelectedIndex();
                    if (index != -1) {
                        removeCartItem(index);
                    }
                }
            }
        });

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Koszyk (2x kliknij by usunąć)"));
        rightPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        qtyField = new JTextField("1", 5);
        JButton addBtn = new JButton("Dodaj do koszyka");
        JButton orderBtn = new JButton("Złóż zamówienie");
        JButton adminBtn = new JButton("Panel administratora");
        JButton logoutBtn = new JButton("Wyloguj");

        addBtn.addActionListener(e -> addToCart());
        orderBtn.addActionListener(e -> submitOrder());
        adminBtn.addActionListener(e -> showAdminDashboard());
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginView(new UserRepository("users.json")).setVisible(true);
        });

        bottomPanel.add(new JLabel("Ilość:"));
        bottomPanel.add(qtyField);
        bottomPanel.add(addBtn);
        bottomPanel.add(orderBtn);

        if (user.getRole().equalsIgnoreCase("admin")) {
            bottomPanel.add(adminBtn);
        }

        bottomPanel.add(logoutBtn);

        add(centerPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshProductList() {
        productModel.clear();
        for (Product p : productRepository.getAll()) {
            productModel.addElement(p.toString());
        }
    }

    private void refreshCart() {
        cartModel.clear();
        for (CartItem item : cart) {
            cartModel.addElement(item.toString());
        }
    }

    private void addToCart() {
        int index = productList.getSelectedIndex();
        if (index != -1) {
            Product selected = productRepository.getAll().get(index);
            try {
                int qty = Integer.parseInt(qtyField.getText());
                if (qty > 0 && qty <= selected.getStock()) {
                    selected.setStock(selected.getStock() - qty);
                    cart.add(new CartItem(selected, qty));
                    refreshProductList();
                    refreshCart();
                } else {
                    JOptionPane.showMessageDialog(this, "Nieprawidłowa ilość.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Podaj poprawną liczbę.");
            }
        }
    }

    private void removeCartItem(int index) {
        if (index >= 0 && index < cart.size()) {
            CartItem removed = cart.remove(index);
            Product p = removed.getProduct();
            p.setStock(p.getStock() + removed.getQuantity());
            refreshProductList();
            refreshCart();
        }
    }

    private void submitOrder() {
        String couponCode = JOptionPane.showInputDialog(this, "Jeśli masz kod rabatowy, wpisz go teraz (lub zostaw puste):", "Kod rabatowy", JOptionPane.PLAIN_MESSAGE);
        if (couponCode != null && couponCode.equalsIgnoreCase("KOD10")) {
            JOptionPane.showMessageDialog(this, "Zastosowano kupon: 10% rabatu!");
        }
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Koszyk jest pusty.");
            return;
        }

        JTextField miastoField = new JTextField();
        JTextField ulicaField = new JTextField();
        JTextField kodField = new JTextField();
        JComboBox<String> platnoscBox = new JComboBox<>(new String[]{"Karta", "Gotówka", "BLIK"});

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Miejscowość:"));
        panel.add(miastoField);
        panel.add(new JLabel("Ulica i numer:"));
        panel.add(ulicaField);
        panel.add(new JLabel("Kod pocztowy:"));
        panel.add(kodField);
        panel.add(new JLabel("Forma płatności:"));
        panel.add(platnoscBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Dane dostawy", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String address = miastoField.getText().trim() + ", " + ulicaField.getText().trim() + ", " + kodField.getText().trim();
        String platnosc = (String) platnoscBox.getSelectedItem();
        LocalDateTime now = LocalDateTime.now();
        double total = cart.stream().mapToDouble(CartItem::getTotalPrice).sum();
        double discount = 0;

        if (total >= 5000) {
            discount = total * 0.10;
            total -= discount;
        }
        if (couponCode != null && couponCode.equalsIgnoreCase("KOD10")) {
            double extra = total * 0.10;
            total -= extra;
            discount += extra;
        }

        StringBuilder summary = new StringBuilder("Podsumowanie zamówienia:\n\n");
        for (CartItem item : cart) {
            summary.append(String.format("%s x%d = %.2f zł\n", item.getProduct().getName(), item.getQuantity(), item.getTotalPrice()));
        }
        summary.append("\nAdres dostawy: ").append(address);
        summary.append("\nForma płatności: ").append(platnosc);
        if (discount > 0) summary.append("\nRabat: -").append(String.format("%.2f zł", discount));
        summary.append("\nŁącznie do zapłaty: ").append(String.format("%.2f zł", total));
        summary.append("\nData: ").append(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        int confirm = JOptionPane.showConfirmDialog(this, summary.toString(), "Potwierdź zamówienie", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Order order = new Order(user.getUsername(), new ArrayList<>(cart), total, address, now);
            orderRepository.addOrder(order);
            cart.clear();
            refreshCart();
            JOptionPane.showMessageDialog(this, "✅ Zamówienie zostało pomyślnie zrealizowane!");
        }
    }

    private void showAdminDashboard() {
        List<Order> orders = orderRepository.getAllOrders();

        if (orders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Brak zamówień do wyświetlenia.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder summary = new StringBuilder("\uD83D\uDCCA Podsumowanie zamówień:\n\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Order order : orders) {
            summary.append("\u2022 Użytkownik: ").append(order.getUsername()).append("\n")
                    .append("  Adres: ").append(order.getAddress()).append("\n")
                    .append("  Data: ").append(order.getTimestamp().format(formatter)).append("\n")
                    .append("  Forma płatności: ").append(order.getPaymentMethod()).append("\n")
                    .append("  Produkty:\n");
            for (CartItem item : order.getItems()) {
                summary.append("    - ")
                        .append(item.getProduct().getName())
                        .append(" x ")
                        .append(item.getQuantity())
                        .append(" = ")
                        .append(String.format("%.2f zł", item.getTotalPrice()))
                        .append("\n");
            }
            summary.append("  SUMA: ")
                    .append(String.format("%.2f zł", order.getTotal()))
                    .append("\n\n");
        }

        JTextArea area = new JTextArea(summary.toString());
        area.setEditable(false);
        area.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "Panel administratora - zamówienia", JOptionPane.INFORMATION_MESSAGE);
    }
}



