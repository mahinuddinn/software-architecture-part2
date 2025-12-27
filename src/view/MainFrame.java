package view;

import model.Patient;
import model.Clinician;
import model.Prescription;
import model.Referral;

import repository.PatientRepository;
import repository.ClinicianRepository;
import repository.PrescriptionRepository;
import repository.ReferralRepository;
import repository.ReferralManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * MainFrame
 * ---------
 * This class represents the VIEW layer of the application
 * according to the Model-View-Controller (MVC) architecture.
 *
 * Responsibilities of this class:
 *  - Display data loaded from CSV files (Patients, Clinicians, Prescriptions, Referrals)
 *  - Provide a graphical user interface using Java Swing
 *  - Capture user input (add/delete operations)
 *  - Delegate data processing and persistence to repositories
 *
 * IMPORTANT:
 *  - No business logic is implemented here
 *  - No file handling is performed here
 *  - All persistence is handled by the Model layer
 */
public class MainFrame extends JFrame {

    /* =====================================================
       MODEL LAYER REFERENCES (Repositories)
       -----------------------------------------------------
       These repositories handle all data access and persistence.
       The View only calls their public methods.
       ===================================================== */

    private final PatientRepository patientRepository = new PatientRepository();
    private final ClinicianRepository clinicianRepository = new ClinicianRepository();
    private final PrescriptionRepository prescriptionRepository = new PrescriptionRepository();
    private final ReferralRepository referralRepository = new ReferralRepository();

    /* =====================================================
       TABLE MODELS
       -----------------------------------------------------
       DefaultTableModel acts as the data model for JTable.
       Each table has its own model.
       ===================================================== */

    private DefaultTableModel patientModel;
    private DefaultTableModel clinicianModel;
    private DefaultTableModel prescriptionModel;
    private DefaultTableModel referralModel;

    /* =====================================================
       TABLE COMPONENTS
       ===================================================== */

    private JTable patientTable;
    private JTable clinicianTable;
    private JTable prescriptionTable;
    private JTable referralTable;

    /**
     * Constructs the main application window.
     * Sets up the tabbed interface and loads initial data.
     */
    public MainFrame() {

        setTitle("Healthcare Referral System");
        setSize(1200, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centre window

        // Tabbed layout for each entity
        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Patients", createPatientPanel());
        tabs.add("Clinicians", createClinicianPanel());
        tabs.add("Prescriptions", createPrescriptionPanel());
        tabs.add("Referrals", createReferralPanel());

        add(tabs, BorderLayout.CENTER);
    }

    /* =====================================================
       PATIENT TAB
       ===================================================== */

    /**
     * Creates the Patients tab panel.
     */
    private JPanel createPatientPanel() {

        String[] columns = {
                "NHS Number", "First Name", "Last Name",
                "Date of Birth", "Phone Number", "GP Surgery"
        };

        patientModel = new DefaultTableModel(columns, 0);
        patientTable = new JTable(patientModel);

        loadPatients();

        JButton addBtn = new JButton("Add Patient");
        JButton delBtn = new JButton("Delete Patient");

        addBtn.addActionListener(e -> addPatient());
        delBtn.addActionListener(e -> deletePatient());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.add(buttonRow(addBtn, delBtn), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads patient data from CSV into the table.
     */
    private void loadPatients() {
        try {
            patientRepository.load("data/patients.csv");
            patientModel.setRowCount(0);

            for (Patient p : patientRepository.getAll()) {
                patientModel.addRow(new Object[]{
                        p.getNhsNumber(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getDateOfBirth(),
                        p.getPhoneNumber(),
                        p.getRegisteredGpSurgery()
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    /**
     * Adds a new patient using user input dialogs.
     */
    private void addPatient() {
        try {
            Patient p = new Patient(
                    input("NHS Number"),
                    input("First Name"),
                    input("Last Name"),
                    input("Date of Birth"),
                    input("Phone Number"),
                    input("GP Surgery")
            );

            patientRepository.addPatient(p);
            loadPatients();

        } catch (Exception e) {
            showError(e);
        }
    }

    /**
     * Deletes the selected patient.
     */
    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) return;

        try {
            String nhs = patientModel.getValueAt(row, 0).toString();
            patientRepository.deletePatient(nhs);
            loadPatients();
        } catch (Exception e) {
            showError(e);
        }
    }

    /* =====================================================
       CLINICIANS TAB
       ===================================================== */

    private JPanel createClinicianPanel() {

        String[] columns = {
                "Clinician ID", "Name", "Role",
                "Specialty", "Workplace"
        };

        clinicianModel = new DefaultTableModel(columns, 0);
        clinicianTable = new JTable(clinicianModel);

        loadClinicians();

        JButton addBtn = new JButton("Add Clinician");
        JButton delBtn = new JButton("Delete Clinician");

        addBtn.addActionListener(e -> addClinician());
        delBtn.addActionListener(e -> deleteClinician());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(clinicianTable), BorderLayout.CENTER);
        panel.add(buttonRow(addBtn, delBtn), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads clinicians from CSV.
     */
    private void loadClinicians() {
        try {
            clinicianRepository.load("data/clinicians.csv");
            clinicianModel.setRowCount(0);

            for (Clinician c : clinicianRepository.getAll()) {
                clinicianModel.addRow(new Object[]{
                        c.getClinicianId(),
                        c.getName(),
                        c.getRole(),
                        c.getSpecialty(),
                        c.getWorkplace()
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    /**
     * Adds a clinician.
     */
    private void addClinician() {
        try {
            Clinician c = new Clinician(
                    input("Clinician ID"),
                    input("Full Name"),
                    input("Role"),
                    input("Specialty"),
                    input("Workplace")
            );

            clinicianRepository.add(c);
            loadClinicians();

        } catch (Exception e) {
            showError(e);
        }
    }

    /**
     * Deletes selected clinician.
     */
    private void deleteClinician() {
        int row = clinicianTable.getSelectedRow();
        if (row == -1) return;

        try {
            String id = clinicianModel.getValueAt(row, 0).toString();
            clinicianRepository.delete(id);
            loadClinicians();
        } catch (Exception e) {
            showError(e);
        }
    }

    /* =====================================================
       PRESCRIPTIONS TAB
       ===================================================== */

    private JPanel createPrescriptionPanel() {

        String[] columns = {
                "Prescription ID", "Patient NHS",
                "Clinician", "Medication",
                "Dosage", "Pharmacy", "Status"
        };

        prescriptionModel = new DefaultTableModel(columns, 0);
        prescriptionTable = new JTable(prescriptionModel);

        loadPrescriptions();

        JButton addBtn = new JButton("Add Prescription");
        addBtn.addActionListener(e -> addPrescription());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(prescriptionTable), BorderLayout.CENTER);
        panel.add(buttonRow(addBtn), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads prescriptions and resolves clinician IDs to names.
     */
    private void loadPrescriptions() {
        try {
            prescriptionRepository.load("data/prescriptions.csv");
            prescriptionModel.setRowCount(0);

            for (Prescription p : prescriptionRepository.getAll()) {
                Clinician c = clinicianRepository.findById(p.getClinicianId());

                prescriptionModel.addRow(new Object[]{
                        p.getPrescriptionId(),
                        p.getPatientNhsNumber(),
                        c != null ? c.getName() : p.getClinicianId(),
                        p.getMedication(),
                        p.getDosage(),
                        p.getPharmacy(),
                        p.getCollectionStatus()
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    /**
     * Adds a prescription and persists it.
     */
    private void addPrescription() {
        try {
            Prescription p = new Prescription(
                    input("Prescription ID"),
                    input("Patient NHS"),
                    input("Clinician ID"),
                    input("Medication"),
                    input("Dosage"),
                    input("Pharmacy"),
                    input("Status")
            );

            prescriptionRepository.addPrescription(p);
            loadPrescriptions();

        } catch (Exception e) {
            showError(e);
        }
    }

    /* =====================================================
       REFERRALS TAB (SINGLETON)
       ===================================================== */

    private JPanel createReferralPanel() {

        String[] columns = {
                "Referral ID", "Patient NHS",
                "Referring Clinician",
                "From Facility", "To Facility",
                "Urgency", "Date"
        };

        referralModel = new DefaultTableModel(columns, 0);
        referralTable = new JTable(referralModel);

        loadReferrals();

        JButton addBtn = new JButton("Create Referral");
        addBtn.addActionListener(e -> createReferral());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(referralTable), BorderLayout.CENTER);
        panel.add(buttonRow(addBtn), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads referrals from CSV.
     */
    private void loadReferrals() {
        try {
            referralRepository.load("data/referrals.csv");
            referralModel.setRowCount(0);

            for (Referral r : referralRepository.getAll()) {
                referralModel.addRow(new Object[]{
                        r.getReferralId(),
                        r.getPatientNhsNumber(),
                        r.getReferringClinicianId(),
                        r.getFromFacilityId(),
                        r.getToFacilityId(),
                        r.getUrgencyLevel(),
                        r.getReferralDate()
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    /**
     * Creates a referral and processes it using Singleton ReferralManager.
     */
    private void createReferral() {
        try {
            Referral r = new Referral(
                    input("Referral ID"),
                    input("Patient NHS"),
                    input("Referring Clinician ID"),
                    input("From Facility"),
                    input("To Facility"),
                    input("Clinical Summary"),
                    input("Urgency Level"),
                    java.time.LocalDate.now().toString()
            );

            // Singleton pattern usage (rubric requirement)
            ReferralManager.getInstance().processReferral(r);

            loadReferrals();

        } catch (Exception e) {
            showError(e);
        }
    }

    /* =====================================================
       UTILITY METHODS
       ===================================================== */

    /**
     * Creates a row of buttons.
     */
    private JPanel buttonRow(JButton... buttons) {
        JPanel panel = new JPanel();
        for (JButton b : buttons) panel.add(b);
        return panel;
    }

    /**
     * Shows an input dialog.
     */
    private String input(String label) {
        return JOptionPane.showInputDialog(this, label);
    }

    /**
     * Centralised error handling.
     */
    private void showError(Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
