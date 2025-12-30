package view;

import model.Patient;
import model.Prescription;
import model.Referral;
import model.Clinician;

import repository.PatientRepository;
import repository.PrescriptionRepository;
import repository.ReferralRepository;
import repository.ReferralManager;
import repository.ClinicianRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * MainFrame
 * ---------
 * Main GUI window for the Healthcare Referral System.
 *
 * MVC ROLE:
 * ----------
 * VIEW layer only.
 *  - Displays data in tables
 *  - Collects user input via dialogs
 *  - Delegates all logic + persistence to repositories / managers
 *
 * NO business logic here.
 */
public class MainFrame extends JFrame {

    /* =====================================================
       REPOSITORIES (MODEL LAYER)
       ===================================================== */
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ReferralRepository referralRepository;

    /* =====================================================
       TABLE MODELS + TABLES
       ===================================================== */
    private JTable patientTable;
    private JTable clinicianTable;
    private JTable prescriptionTable;
    private JTable referralTable;

    private DefaultTableModel patientTableModel;
    private DefaultTableModel clinicianTableModel;
    private DefaultTableModel prescriptionTableModel;
    private DefaultTableModel referralTableModel;

    /**
     * Application entry GUI
     */
    public MainFrame() {

        setTitle("Healthcare Referral System");
        setSize(1200, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialise repositories (MODEL)
        patientRepository = new PatientRepository();
        clinicianRepository = new ClinicianRepository();
        prescriptionRepository = new PrescriptionRepository();
        referralRepository = new ReferralRepository();

        // Tabbed interface
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", createPatientPanel());
        tabs.addTab("Clinicians", createClinicianPanel());
        tabs.addTab("Prescriptions", createPrescriptionPanel());
        tabs.addTab("Referrals", createReferralPanel());

        add(tabs, BorderLayout.CENTER);
    }

    /* =====================================================
       PATIENTS TAB
       ===================================================== */

    private JPanel createPatientPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        patientTableModel = new DefaultTableModel(
                new String[]{"NHS Number", "First Name", "Last Name", "DOB", "Phone", "GP Surgery"}, 0
        );

        patientTable = new JTable(patientTableModel);
        loadPatients();

        JButton add = new JButton("Add Patient");
        JButton delete = new JButton("Delete Patient");

        add.addActionListener(e -> addPatient());
        delete.addActionListener(e -> deletePatient());

        JPanel bottom = new JPanel();
        bottom.add(add);
        bottom.add(delete);

        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void loadPatients() {
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
            showError(e);
        }
    }

    private void addPatient() {
        try {
            Patient p = new Patient(
                    input("NHS Number"),
                    input("First Name"),
                    input("Last Name"),
                    input("Date of Birth"),
                    input("Phone"),
                    input("GP Surgery")
            );
            patientRepository.addPatient(p);
            loadPatients();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) return;

        try {
            patientRepository.deletePatient(patientTableModel.getValueAt(row, 0).toString());
            loadPatients();
        } catch (Exception e) {
            showError(e);
        }
    }

    /* =====================================================
       CLINICIANS TAB
       ===================================================== */

    private JPanel createClinicianPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        clinicianTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Role", "Specialty", "Workplace"}, 0
        );

        clinicianTable = new JTable(clinicianTableModel);
        loadClinicians();

        JButton add = new JButton("Add Clinician");
        add.addActionListener(e -> addClinician());

        panel.add(new JScrollPane(clinicianTable), BorderLayout.CENTER);
        panel.add(add, BorderLayout.SOUTH);
        return panel;
    }

    private void loadClinicians() {
        try {
            clinicianRepository.load("data/clinicians.csv");
            clinicianTableModel.setRowCount(0);

            for (Clinician c : clinicianRepository.getAll()) {
                clinicianTableModel.addRow(new Object[]{
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

    /* =====================================================
       PRESCRIPTIONS TAB
       ===================================================== */

    private JPanel createPrescriptionPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        prescriptionTableModel = new DefaultTableModel(
                new String[]{"ID", "Patient NHS", "Clinician", "Medication", "Dosage", "Pharmacy", "Status"}, 0
        );

        prescriptionTable = new JTable(prescriptionTableModel);
        loadPrescriptions();

        JButton add = new JButton("Add Prescription");
        add.addActionListener(e -> addPrescription());

        panel.add(new JScrollPane(prescriptionTable), BorderLayout.CENTER);
        panel.add(add, BorderLayout.SOUTH);
        return panel;
    }

    private void loadPrescriptions() {
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
            showError(e);
        }
    }

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
       REFERRALS TAB (FULL CSV SUPPORT)
       ===================================================== */

    private JPanel createReferralPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        referralTableModel = new DefaultTableModel(
                new String[]{
                        "Referral ID",
                        "Patient NHS",
                        "Referring Clinician",
                        "From Facility",
                        "To Facility",
                        "Urgency",
                        "Status",
                        "Date",
                        "Reason",
                        "Clinical Summary"
                }, 0
        );

        referralTable = new JTable(referralTableModel);
        loadReferrals();

        JButton add = new JButton("Create Referral");
        add.addActionListener(e -> createReferral());

        panel.add(new JScrollPane(referralTable), BorderLayout.CENTER);
        panel.add(add, BorderLayout.SOUTH);
        return panel;
    }

    private void loadReferrals() {
        try {
            referralRepository.load("data/referrals.csv");
            referralTableModel.setRowCount(0);

            for (Referral r : referralRepository.getAll()) {
                referralTableModel.addRow(new Object[]{
                        r.getReferralId(),
                        r.getPatientNhsNumber(),
                        r.getReferringClinicianId(),
                        r.getFromFacilityId(),
                        r.getToFacilityId(),
                        r.getUrgencyLevel(),
                        r.getStatus(),
                        r.getReferralDate(),
                        r.getReferralReason(),
                        r.getClinicalSummary()
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void createReferral() {
        try {
            Referral r = new Referral(
                    input("Referral ID"),
                    input("Patient NHS"),
                    input("Referring Clinician ID"),
                    input("From Facility"),
                    input("To Facility"),
                    input("Referral Reason"),
                    input("Clinical Summary"),
                    input("Requested Investigations"),
                    input("Urgency"),
                    "New",
                    "",
                    java.time.LocalDate.now().toString()
            );

            // SINGLETON usage (rubric)
            ReferralManager.getInstance().processReferral(r);

            loadReferrals();
        } catch (Exception e) {
            showError(e);
        }
    }

    /* =====================================================
       UTILITY METHODS
       ===================================================== */

    private String input(String label) {
        return JOptionPane.showInputDialog(this, label);
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
