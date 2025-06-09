package view;

import model.*;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JFrame {
    private ProductRepository productRepository;

    public AdminPanel(ProductRepository productRepository) {
        this.productRepository = productRepository;

        setTitle("Panel administratora");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JButton addBtn = new JButton("Dodaj produkt");
        addBtn.addActionListener(e -> addProduct());

        add(addBtn, BorderLayout.NORTH);
        refreshList();
    }

    private void refreshList() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        StringBuilder sb = new StringBuilder();
        for (Product p : productRepository.getAll()) {
            sb.append(p.toString()).append("\n");
        }
        area.setText(sb.toString());
        add(new JScrollPane(area), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void addProduct() {
        String name = JOptionPane.showInputDialog(this, "Nazwa produktu:");
        String priceStr = JOptionPane.showInputDialog(this, "Cena:");
        String stockStr = JOptionPane.showInputDialog(this, "Ilość:");
        String category = JOptionPane.showInputDialog(this, "Kategoria (np. Elektronika, RTV, Akcesoria):");

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            productRepository.getAll().add(new Product(name, price, stock, category));
            productRepository.saveAll();
            refreshList();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nieprawidłowe dane");
        }
    }
}