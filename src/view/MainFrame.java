package view;

import model.Patient;
import model.Prescription;
import model.Referral;
import model.Clinician;

import repository.PatientRepository;
import repository.PrescriptionRepository;
import repository.ReferralManager;
import repository.ReferralRepository;
import repository.ClinicianRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * MainFrame
 * ---------
 * Main GUI window for the Healthcare Referral System.
 *
 * VIEW layer in MVC:
 *  - Displays Patients, Clinicians, Prescriptions, Referrals
 *  - Captures user actions
 *  - Delegates logic + persistence to repositories / managers
 *
 * NO business logic is implemented here.
 */
public class MainFrame extends JFrame {

    /* =======================
       PATIENT COMPONENTS
       ======================= */
    private JTable patientTable;
    private DefaultTableModel patientTableModel;
    private PatientRepository patientRepository;

    /* =======================
       CLINICIAN COMPONENTS
       ======================= */
    private JTable clinicianTable;
    private DefaultTableModel clinicianTableModel;
    private ClinicianRepository clinicianRepository;

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
        setSize(1200, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialise MODEL layer repositories
        patientRepository = new PatientRepository();
        clinicianRepository = new ClinicianRepository();
        prescriptionRepository = new PrescriptionRepository();
        referralRepository = new ReferralRepository();

        // Tabbed layout
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", createPatientPanel());
        tabs.addTab("Clinicians", createClinicianPanel());
        tabs.addTab("Prescriptions", createPrescriptionPanel());
        tabs.addTab("Referrals", createReferralPanel());

        add(tabs, BorderLayout.CENTER);
    }

    /* =====================================================
       PATIENT TAB (CRUD)
       ===================================================== */

    private JPanel createPatientPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        patientTableModel = new DefaultTableModel(
                new String[]{"NHS", "First", "Last", "DOB", "Phone", "GP Surgery"}, 0
        );
        patientTable = new JTable(patientTableModel);

        loadPatients();

        JButton add = new JButton("Add");
        JButton delete = new JButton("Delete");

        add.addActionListener(e -> addPatient());
        delete.addActionListener(e -> deletePatient());

        JPanel buttons = new JPanel();
        buttons.add(add);
        buttons.add(delete);

        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPatients() {
        try {
            patientRepository.load("data/patients.csv");
            patientTableModel.setRowCount(0);
            for (Patient p : patientRepository.getAll()) {
                patientTableModel.addRow(new Object[]{
                        p.getNhsNumber(), p.getFirstName(), p.getLastName(),
                        p.getDateOfBirth(), p.getPhoneNumber(), p.getRegisteredGpSurgery()
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void addPatient() {
        try {
            Patient p = new Patient(
                    JOptionPane.showInputDialog(this, "NHS Number"),
                    JOptionPane.showInputDialog(this, "First Name"),
                    JOptionPane.showInputDialog(this, "Last Name"),
                    JOptionPane.showInputDialog(this, "DOB"),
                    JOptionPane.showInputDialog(this, "Phone"),
                    JOptionPane.showInputDialog(this, "GP Surgery")
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
       CLINICIAN TAB (ADD ONLY – SAFE)
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
                        c.getClinicianId(), c.getName(),
                        c.getRole(), c.getSpecialty(), c.getWorkplace()
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void addClinician() {
        try {
            Clinician c = new Clinician(
                    JOptionPane.showInputDialog(this, "Clinician ID"),
                    JOptionPane.showInputDialog(this, "Name"),
                    JOptionPane.showInputDialog(this, "Role"),
                    JOptionPane.showInputDialog(this, "Specialty"),
                    JOptionPane.showInputDialog(this, "Workplace")
            );
            clinicianRepository.add(c);
            loadClinicians();
        } catch (Exception e) {
            showError(e);
        }
    }

    /* =====================================================
       PRESCRIPTIONS TAB (ADD / EDIT / DELETE)
       ===================================================== */

    private JPanel createPrescriptionPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        prescriptionTableModel = new DefaultTableModel(
                new String[]{"ID", "Patient NHS", "Clinician", "Medication", "Dosage", "Pharmacy", "Status"}, 0
        );
        prescriptionTable = new JTable(prescriptionTableModel);

        loadPrescriptions();

        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");

        add.addActionListener(e -> addPrescription());
        edit.addActionListener(e -> editPrescription());
        delete.addActionListener(e -> deletePrescription());

        JPanel buttons = new JPanel();
        buttons.add(add);
        buttons.add(edit);
        buttons.add(delete);

        panel.add(new JScrollPane(prescriptionTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPrescriptions() {
        try {
            prescriptionRepository.load("data/prescriptions.csv");
            prescriptionTableModel.setRowCount(0);
            for (Prescription p : prescriptionRepository.getAll()) {
                prescriptionTableModel.addRow(new Object[]{
                        p.getPrescriptionId(), p.getPatientNhsNumber(),
                        p.getClinicianId(), p.getMedication(),
                        p.getDosage(), p.getPharmacy(), p.getCollectionStatus()
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void addPrescription() {
        try {
            Prescription p = new Prescription(
                    JOptionPane.showInputDialog(this, "ID"),
                    JOptionPane.showInputDialog(this, "Patient NHS"),
                    JOptionPane.showInputDialog(this, "Clinician ID"),
                    JOptionPane.showInputDialog(this, "Medication"),
                    JOptionPane.showInputDialog(this, "Dosage"),
                    JOptionPane.showInputDialog(this, "Pharmacy"),
                    JOptionPane.showInputDialog(this, "Status")
            );
            prescriptionRepository.addPrescription(p);
            loadPrescriptions();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void editPrescription() {
        int row = prescriptionTable.getSelectedRow();
        if (row == -1) return;

        try {
            Prescription updated = new Prescription(
                    prescriptionTableModel.getValueAt(row, 0).toString(),
                    JOptionPane.showInputDialog(this, "Patient NHS", prescriptionTableModel.getValueAt(row, 1)),
                    JOptionPane.showInputDialog(this, "Clinician ID", prescriptionTableModel.getValueAt(row, 2)),
                    JOptionPane.showInputDialog(this, "Medication", prescriptionTableModel.getValueAt(row, 3)),
                    JOptionPane.showInputDialog(this, "Dosage", prescriptionTableModel.getValueAt(row, 4)),
                    JOptionPane.showInputDialog(this, "Pharmacy", prescriptionTableModel.getValueAt(row, 5)),
                    JOptionPane.showInputDialog(this, "Status", prescriptionTableModel.getValueAt(row, 6))
            );

            prescriptionRepository.updatePrescription(updated);
            loadPrescriptions();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void deletePrescription() {
        int row = prescriptionTable.getSelectedRow();
        if (row == -1) return;

        try {
            prescriptionRepository.deletePrescription(
                    prescriptionTableModel.getValueAt(row, 0).toString()
            );
            loadPrescriptions();
        } catch (Exception e) {
            showError(e);
        }
    }

    /* =====================================================
       REFERRALS TAB (CSV LOAD + SINGLETON OUTPUT)
       ===================================================== */

    private JPanel createReferralPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        referralTableModel = new DefaultTableModel(
                new String[]{"ID", "Patient NHS", "From Facility", "To Facility", "Urgency", "Date", "Summary"}, 0
        );
        referralTable = new JTable(referralTableModel);

        loadReferrals();

        JButton create = new JButton("Create Referral");
        create.addActionListener(e -> createReferral());

        panel.add(new JScrollPane(referralTable), BorderLayout.CENTER);
        panel.add(create, BorderLayout.SOUTH);

        return panel;
    }

    private void loadReferrals() {
        try {
            referralRepository.load("data/referrals.csv");
            referralTableModel.setRowCount(0);
            for (Referral r : referralRepository.getAll()) {
                referralTableModel.addRow(new Object[]{
                        r.getReferralId(), r.getPatientNhsNumber(),
                        r.getFromFacilityId(), r.getToFacilityId(),
                        r.getUrgencyLevel(), r.getReferralDate(),
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
                    JOptionPane.showInputDialog(this, "Referral ID"),
                    JOptionPane.showInputDialog(this, "Patient NHS"),
                    JOptionPane.showInputDialog(this, "Referring Clinician ID"),
                    JOptionPane.showInputDialog(this, "From Facility"),
                    JOptionPane.showInputDialog(this, "To Facility"),
                    JOptionPane.showInputDialog(this, "Reason"),
                    JOptionPane.showInputDialog(this, "Clinical Summary"),
                    JOptionPane.showInputDialog(this, "Investigations"),
                    JOptionPane.showInputDialog(this, "Urgency"),
                    "New",
                    "",
                    java.time.LocalDate.now().toString()
            );

            // Singleton usage (rubric requirement)
            ReferralManager.getInstance().processReferral(r);
            loadReferrals();
        } catch (Exception e) {
            showError(e);
        }
    }

    /* ===================================================== */

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
