package view;

import javax.swing.*;
import java.awt.*;

import model.UserSession;

public class LoginFrame extends JFrame {

    public LoginFrame() {

        setTitle("Healthcare System Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel userLabel = new JLabel("User ID:");
        JTextField userField = new JTextField();

        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleBox = new JComboBox<>(new String[]{
                "PATIENT",
                "CLINICIAN",
                "DOCTOR",
                "STAFF"
        });

        JButton loginBtn = new JButton("Login");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(roleLabel);
        panel.add(roleBox);
        panel.add(new JLabel()); // spacer
        panel.add(loginBtn);

        add(panel);

        // ===============================
        // LOGIN BUTTON ACTION
        // ===============================
loginBtn.addActionListener(e -> {

    String id = userField.getText().trim();
    String role = roleBox.getSelectedItem().toString();

    // Basic validation
    if (id.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "User ID is required.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    // Save session
    UserSession.login(id, role);

    // Open main system
    MainFrame frame = new MainFrame();
    frame.setVisible(true);

    // Close login window
    dispose();
});

    }
}
