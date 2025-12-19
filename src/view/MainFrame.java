package view;

import model.Patient;
import model.Prescription;
import repository.PatientRepository;
import repository.PrescriptionRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * MainFrame
 * ---------
 * Main GUI window for the Healthcare Referral System.
 *
 * This class represents the VIEW layer in the MVC architecture.
 * It is responsible for:
 *  - Displaying data (patients, prescriptions)
 *  - Capturing user input
 *  - Delegating actions to repositories (model layer)
 *
 * Business logic and persistence are handled by repositories.
 */
public class MainFrame extends JFrame {

    /* =======================
       PATIENT GUI COMPONENTS
       ======================= */
    private JTable patientTable;
    private DefaultTableModel patientTableModel;
    private PatientRepository patientRepository;

    /* ============================
       PRESCRIPTION GUI COMPONENTS
       ============================ */
    private JTable prescriptionTable;
    private DefaultTableModel prescriptionTableModel;
    private PrescriptionRepository prescriptionRepository;

    /**
     * Constructs the main application window.
     */
    public MainFrame() {

        setTitle("Healthcare Referral System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialise repositories (Model layer)
        patientRepository = new PatientRepository();
        prescriptionRepository = new PrescriptionRepository();

        // Create tabbed layout
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", createPatientPanel());
        tabs.addTab("Prescriptions", createPrescriptionPanel());

        add(tabs, BorderLayout.CENTER);
    }

    /* =====================================================
       PATIENT TAB (CRUD: Add + Delete)
       ===================================================== */

    private JPanel createPatientPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
                "NHS Number",
                "First Name",
                "Last Name",
                "Date of Birth",
                "Phone Number",
                "GP Surgery"
        };

        patientTableModel = new DefaultTableModel(columns, 0);
        patientTable = new JTable(patientTableModel);

        loadPatientsIntoTable();

        JButton addButton = new JButton("Add Patient");
        JButton deleteButton = new JButton("Delete Patient");

        addButton.addActionListener(e -> addPatient());
        deleteButton.addActionListener(e -> deletePatient());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        bottomPanel.add(deleteButton);

        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPatientsIntoTable() {

        try {
            patientRepository.load("data/patients.csv");
            patientTableModel.setRowCount(0);

            for (Patient p : patientRepository.getAll()) {
                patientTableModel.addRow(new Object[]{
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

    private void addPatient() {

        try {
            String nhs = JOptionPane.showInputDialog(this, "NHS Number:");
            if (nhs == null || nhs.isBlank()) return;

            String first = JOptionPane.showInputDialog(this, "First Name:");
            if (first == null || first.isBlank()) return;

            String last = JOptionPane.showInputDialog(this, "Last Name:");
            if (last == null || last.isBlank()) return;

            String dob = JOptionPane.showInputDialog(this, "Date of Birth (YYYY-MM-DD):");
            if (dob == null || dob.isBlank()) return;

            String phone = JOptionPane.showInputDialog(this, "Phone Number:");
            if (phone == null) phone = "";

            String gp = JOptionPane.showInputDialog(this, "GP Surgery:");
            if (gp == null) gp = "";

            Patient patient = new Patient(nhs, first, last, dob, phone, gp);
            patientRepository.addPatient(patient);

            patientTableModel.addRow(new Object[]{
                    nhs, first, last, dob, phone, gp
            });

            JOptionPane.showMessageDialog(this, "Patient added successfully.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {

        int row = patientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient first.");
            return;
        }

        String nhs = patientTableModel.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete patient with NHS: " + nhs + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            patientRepository.deletePatient(nhs);
            patientTableModel.removeRow(row);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* =====================================================
       PRESCRIPTIONS TAB (CREATE + DISPLAY)
       ===================================================== */

    private JPanel createPrescriptionPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
                "Prescription ID",
                "Patient NHS",
                "Clinician ID",
                "Medication",
                "Dosage",
                "Pharmacy",
                "Status"
        };

        prescriptionTableModel = new DefaultTableModel(columns, 0);
        prescriptionTable = new JTable(prescriptionTableModel);

        loadPrescriptionsIntoTable();

        JButton addButton = new JButton("Add Prescription");
        addButton.addActionListener(e -> addPrescription());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);

        panel.add(new JScrollPane(prescriptionTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPrescriptionsIntoTable() {

        try {
            prescriptionRepository.load("data/prescriptions.csv");
            prescriptionTableModel.setRowCount(0);

            for (Prescription p : prescriptionRepository.getAll()) {
                prescriptionTableModel.addRow(new Object[]{
                        p.getPrescriptionId(),
                        p.getPatientNhsNumber(),
                        p.getClinicianId(),
                        p.getMedication(),
                        p.getDosage(),
                        p.getPharmacy(),
                        p.getCollectionStatus()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load prescriptions: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPrescription() {

        try {
            String id = JOptionPane.showInputDialog(this, "Prescription ID:");
            if (id == null || id.isBlank()) return;

            String nhs = JOptionPane.showInputDialog(this, "Patient NHS Number:");
            if (nhs == null || nhs.isBlank()) return;

            String clinician = JOptionPane.showInputDialog(this, "Clinician ID:");
            if (clinician == null || clinician.isBlank()) return;

            String med = JOptionPane.showInputDialog(this, "Medication:");
            if (med == null || med.isBlank()) return;

            String dosage = JOptionPane.showInputDialog(this, "Dosage:");
            if (dosage == null || dosage.isBlank()) return;

            String pharmacy = JOptionPane.showInputDialog(this, "Pharmacy:");
            if (pharmacy == null || pharmacy.isBlank()) return;

            String status = JOptionPane.showInputDialog(this, "Collection Status:");
            if (status == null || status.isBlank()) return;

            Prescription prescription = new Prescription(
                    id, nhs, clinician, med, dosage, pharmacy, status
            );

            prescriptionRepository.addPrescription(prescription);

            prescriptionTableModel.addRow(new Object[]{
                    id, nhs, clinician, med, dosage, pharmacy, status
            });

            JOptionPane.showMessageDialog(this, "Prescription added successfully.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
