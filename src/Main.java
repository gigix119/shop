import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;
import model.UserRepository;
import view.LoginView;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Nie udało się zastosować motywu FlatLaf.");
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            UserRepository userRepository = new UserRepository("users.json");
            new LoginView(userRepository).setVisible(true);
        });
    }
}