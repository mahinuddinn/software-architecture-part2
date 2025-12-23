package view;

import model.Patient;
import model.Prescription;
import model.Referral;
import repository.PatientRepository;
import repository.PrescriptionRepository;
import repository.ReferralManager;
import repository.ReferralRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * MainFrame
 * ---------
 * Main GUI window for the Healthcare Referral System.
 *
 * VIEW layer of MVC:
 *  - Displays patients, prescriptions, and referrals
 *  - Collects user input
 *  - Delegates processing to repositories / managers
 *
 * Business logic, persistence, and file output
 * are handled in the Model layer.
 */
public class MainFrame extends JFrame {

    /* =======================
       PATIENT COMPONENTS
       ======================= */
    private JTable patientTable;
    private DefaultTableModel patientTableModel;
    private PatientRepository patientRepository;

    /* =======================
       PRESCRIPTION COMPONENTS
       ======================= */
    private JTable prescriptionTable;
    private DefaultTableModel prescriptionTableModel;
    private PrescriptionRepository prescriptionRepository;

    /* =======================
       REFERRAL COMPONENTS
       ======================= */
    private JTable referralTable;
    private DefaultTableModel referralTableModel;
    private ReferralRepository referralRepository;

    /**
     * Constructs the main application window.
     */
    public MainFrame() {

        setTitle("Healthcare Referral System");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialise repositories (Model layer)
        patientRepository = new PatientRepository();
        prescriptionRepository = new PrescriptionRepository();
        referralRepository = new ReferralRepository();

        // Tabbed interface
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", createPatientPanel());
        tabs.addTab("Prescriptions", createPrescriptionPanel());
        tabs.addTab("Referrals", createReferralPanel());

        add(tabs, BorderLayout.CENTER);
    }

    /* =====================================================
       PATIENT TAB
       ===================================================== */

    private JPanel createPatientPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
                "NHS Number", "First Name", "Last Name",
                "Date of Birth", "Phone Number", "GP Surgery"
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
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPatient() {
        try {
            Patient patient = new Patient(
                    JOptionPane.showInputDialog(this, "NHS Number:"),
                    JOptionPane.showInputDialog(this, "First Name:"),
                    JOptionPane.showInputDialog(this, "Last Name:"),
                    JOptionPane.showInputDialog(this, "Date of Birth:"),
                    JOptionPane.showInputDialog(this, "Phone Number:"),
                    JOptionPane.showInputDialog(this, "GP Surgery:")
            );

            patientRepository.addPatient(patient);
            loadPatientsIntoTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) return;

        String nhs = patientTableModel.getValueAt(row, 0).toString();
        try {
            patientRepository.deletePatient(nhs);
            patientTableModel.removeRow(row);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* =====================================================
       PRESCRIPTIONS TAB
       ===================================================== */

    private JPanel createPrescriptionPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
                "Prescription ID", "Patient NHS", "Clinician ID",
                "Medication", "Dosage", "Pharmacy", "Status"
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
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPrescription() {
        try {
            Prescription p = new Prescription(
                    JOptionPane.showInputDialog(this, "Prescription ID:"),
                    JOptionPane.showInputDialog(this, "Patient NHS Number:"),
                    JOptionPane.showInputDialog(this, "Clinician ID:"),
                    JOptionPane.showInputDialog(this, "Medication:"),
                    JOptionPane.showInputDialog(this, "Dosage:"),
                    JOptionPane.showInputDialog(this, "Pharmacy:"),
                    JOptionPane.showInputDialog(this, "Status:")
            );

            prescriptionRepository.addPrescription(p);
            loadPrescriptionsIntoTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* =====================================================
       REFERRALS TAB (CSV LOAD + SINGLETON OUTPUT)
       ===================================================== */

    private JPanel createReferralPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
                "Referral ID",
                "Referring Clinician",
                "From Facility",
                "To Facility",
                "Urgency",
                "Created Date"
        };

        referralTableModel = new DefaultTableModel(columns, 0);
        referralTable = new JTable(referralTableModel);

        loadReferralsIntoTable();

        JButton addButton = new JButton("Create Referral");
        addButton.addActionListener(e -> createReferral());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);

        panel.add(new JScrollPane(referralTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadReferralsIntoTable() {
        try {
            referralRepository.load("data/referrals.csv");
            referralTableModel.setRowCount(0);

            for (Referral r : referralRepository.getAll()) {
                referralTableModel.addRow(new Object[]{
                        r.getReferralId(),
                        r.getReferringClinicianId(),
                        r.getReferringFacilityId(),
                        r.getReferredToFacilityId(),
                        r.getUrgencyLevel(),
                        r.getCreatedDate()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createReferral() {

        try {
            Referral referral = new Referral(
                    JOptionPane.showInputDialog(this, "Referral ID:"),
                    JOptionPane.showInputDialog(this, "Referring Clinician ID:"),
                    JOptionPane.showInputDialog(this, "Referring Facility ID:"),
                    JOptionPane.showInputDialog(this, "Referred To Facility ID:"),
                    JOptionPane.showInputDialog(this, "Clinical Summary:"),
                    JOptionPane.showInputDialog(this, "Urgency Level:"),
                    java.time.LocalDate.now().toString()
            );

            // SINGLETON usage (rubric requirement)
            ReferralManager.getInstance().processReferral(referral);

            loadReferralsIntoTable();

            JOptionPane.showMessageDialog(this, "Referral created successfully.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
