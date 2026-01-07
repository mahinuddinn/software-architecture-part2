package view;

import model.UserRole;
import model.UserSession;

import javax.swing.*;
import java.awt.*;

/**
 * LoginFrame
 * ----------
 * Login screen for role-based access control.
 */
public class LoginFrame extends JFrame {

    public LoginFrame() {

        setTitle("Healthcare System Login");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ----------------------------
        // UI Components
        // ----------------------------
        JTextField idField = new JTextField();
        JComboBox<UserRole> roleBox = new JComboBox<>(UserRole.values());
        JButton loginBtn = new JButton("Login");

        // ----------------------------
        // Layout
        // ----------------------------
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("User ID:"));
        panel.add(idField);

        panel.add(new JLabel("Role:"));
        panel.add(roleBox);

        panel.add(loginBtn);

        add(panel);

        // ----------------------------
        // Login Button Action
        // ----------------------------
        loginBtn.addActionListener(e -> {

            String id = idField.getText().trim();
            UserRole role = (UserRole) roleBox.getSelectedItem();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "User ID required",
                        "Login Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Save logged-in user session
            UserSession.login(id, role);

            // Open main application
            new MainFrame();

            // Close login window
            dispose();
        });

        setVisible(true);
    }
}
