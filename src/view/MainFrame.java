package view;

import model.Patient;
import repository.PatientRepository;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * MainFrame
 * ---------
 * Main GUI window for the Healthcare Referral System.
 *
 * Provides basic patient management functionality
 * including viewing, adding, and deleting patients.
 */
public class MainFrame extends JFrame {

    private JTable patientTable;
    private DefaultTableModel tableModel;
    private PatientRepository patientRepository;

    public MainFrame() {

        setTitle("Healthcare Referral System");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        patientRepository = new PatientRepository();

        initialisePatientTable();
        loadPatientData();

        add(new JScrollPane(patientTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private void initialisePatientTable() {

        String[] columnNames = {
                "NHS Number",
                "First Name",
                "Last Name",
                "Date of Birth",
                "Phone Number",
                "GP Surgery"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        patientTable = new JTable(tableModel);
    }

    private void loadPatientData() {

        try {
            patientRepository.load("data/patients.csv");

            List<Patient> patients = patientRepository.getAll();

            for (Patient p : patients) {
                tableModel.addRow(new Object[]{
                        p.getNhsNumber(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getDateOfBirth(),
                        p.getPhoneNumber(),
                        p.getRegisteredGpSurgery()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load patients: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createButtonPanel() {

        JButton addButton = new JButton("Add Patient");
        JButton deleteButton = new JButton("Delete Patient");

        addButton.addActionListener(e -> addPatient());
        deleteButton.addActionListener(e -> deletePatient());

        JPanel panel = new JPanel();
        panel.add(addButton);
        panel.add(deleteButton);

        return panel;
    }

    /**
     * Handles Add Patient button action.
     */
    private void addPatient() {

        try {
            String nhs = JOptionPane.showInputDialog(this, "Enter NHS Number:");
            if (nhs == null || nhs.isBlank()) return;

            String firstName = JOptionPane.showInputDialog(this, "Enter First Name:");
            if (firstName == null || firstName.isBlank()) return;

            String lastName = JOptionPane.showInputDialog(this, "Enter Last Name:");
            if (lastName == null || lastName.isBlank()) return;

            String dob = JOptionPane.showInputDialog(this, "Enter Date of Birth (YYYY-MM-DD):");
            if (dob == null || dob.isBlank()) return;

            String phone = JOptionPane.showInputDialog(this, "Enter Phone Number:");
            if (phone == null) phone = "";

            String gp = JOptionPane.showInputDialog(this, "Enter GP Surgery:");
            if (gp == null) gp = "";

            Patient newPatient = new Patient(
                    nhs,
                    firstName,
                    lastName,
                    dob,
                    phone,
                    gp
            );

            patientRepository.addPatient(newPatient);

            tableModel.addRow(new Object[]{
                    nhs,
                    firstName,
                    lastName,
                    dob,
                    phone,
                    gp
            });

            JOptionPane.showMessageDialog(this,
                    "Patient added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles Delete Patient button action.
     */
    private void deletePatient() {

        int selectedRow = patientTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nhs = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete patient with NHS:\n" + nhs,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            patientRepository.deletePatient(nhs);
            tableModel.removeRow(selectedRow);

            JOptionPane.showMessageDialog(this,
                    "Patient deleted successfully.",
                    "Deleted",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
