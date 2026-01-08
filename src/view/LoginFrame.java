package view;

import javax.swing.*;
import java.awt.*;

import model.UserSession;
import repository.PatientRepository;
import repository.ClinicianRepository;
import repository.StaffRepository;

public class LoginFrame extends JFrame {

    // ===============================
    // CLASS FIELDS (IMPORTANT)
    // ===============================
    private JTextField userIdField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;

    private PatientRepository patientRepository;
    private ClinicianRepository clinicianRepository;
    private StaffRepository staffRepository;

    // ===============================
    // CONSTRUCTOR
    // ===============================
    public LoginFrame() {

        // Initialise repositories
        patientRepository = new PatientRepository();
        clinicianRepository = new ClinicianRepository();
        staffRepository = new StaffRepository();

        try {
    patientRepository.load("data/patients.csv");
    clinicianRepository.load("data/clinicians.csv");
    staffRepository.load("data/staff.csv");
} catch (Exception e) {
    JOptionPane.showMessageDialog(
            this,
            "Failed to load user data files.\nLogin disabled.",
            "System Error",
            JOptionPane.ERROR_MESSAGE
    );
}


        setTitle("Healthcare System Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel userLabel = new JLabel("User ID:");
        userIdField = new JTextField();

        JLabel roleLabel = new JLabel("Role:");
        roleComboBox = new JComboBox<>(new String[]{
                "PATIENT",
                "CLINICIAN",
                "DOCTOR",
                "STAFF"
        });

        loginButton = new JButton("Login");

        panel.add(userLabel);
        panel.add(userIdField);
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(new JLabel()); // spacer
        panel.add(loginButton);

        add(panel);

        // ===============================
        // LOGIN BUTTON ACTION
        // ===============================
        loginButton.addActionListener(e -> handleLogin());
    }

    // ===============================
    // LOGIN LOGIC (CLEAN + READABLE)
    // ===============================
    private void handleLogin() {

        String userId = userIdField.getText().trim();
        String role = roleComboBox.getSelectedItem().toString();

        // 1. Role ↔ ID format validation
        if (!isIdValidForRole(userId, role)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid ID for selected role.\n\nExpected format:\n" +
                            role + " → " +
                            (role.equals("PATIENT") ? "P001"
                                    : role.equals("CLINICIAN") ? "C001"
                                    : role.equals("DOCTOR") ? "D001"
                                    : "S001"),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // 2. Check ID exists in repository
        boolean exists = false;

        switch (role) {
            case "PATIENT":
                exists = patientRepository.existsById(userId);
                break;
            case "CLINICIAN":
            exists = clinicianRepository.existsById(userId);
            break;

            case "DOCTOR":
            exists = isDemoDoctor(userId);
            break;

            case "STAFF":
                exists = staffRepository.existsById(userId);
                break;
        }

        if (!exists) {
            JOptionPane.showMessageDialog(
                    this,
                    "No " + role.toLowerCase() + " found with ID: " + userId,
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // 3. Successful login
        UserSession.startSession(userId, role);

        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        this.dispose();
    }

        /**
     * Validates that the entered ID matches the selected role
     */
    private boolean isIdValidForRole(String userId, String role) {

        if (userId == null || userId.isBlank()) {
            return false;
        }

        switch (role) {
            case "PATIENT":
                return userId.matches("P\\d{3}");
            case "CLINICIAN":
                return userId.matches("C\\d{3}");
            case "DOCTOR":
                return userId.matches("D\\d{3}");
            case "STAFF":
                return userId.matches("S\\d{3}");
            default:
                return false;
        }
    }

    /**
     * Demo doctor IDs (login only, no persistence)
     */
    private boolean isDemoDoctor(String userId) {
        return userId.equalsIgnoreCase("D001")
            || userId.equalsIgnoreCase("D002")
            || userId.equalsIgnoreCase("D999");
    }
}
