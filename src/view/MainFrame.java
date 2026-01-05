package view;

import model.Patient;
import model.Clinician;
import model.Prescription;
import model.Referral;
import model.Staff;
import model.Facility;

import repository.PatientRepository;
import repository.ClinicianRepository;
import repository.PrescriptionRepository;
import repository.ReferralRepository;
import repository.ReferralManager;
import repository.StaffRepository;
import repository.FacilityRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * MainFrame
 * ---------
 * Main GUI window for the Healthcare Referral System.
 *
 * ✅ MVC (View Layer) responsibilities:
 *  - Build Swing components (tabs, tables, buttons)
 *  - Display data returned by repositories
 *  - Capture user input via dialogs
 *  - Call repository/manager methods to perform CRUD
 *
 * ❌ View must NOT:
 *  - Parse CSV directly
 *  - Perform file I/O
 *  - Contain business rules (validation beyond basic required-field checks)
 *
 * Notes for marking:
 *  - Repositories = Model/Data layer (CSV persistence)
 *  - ReferralManager = Singleton pattern requirement (creates referral + writes output text file)
 *  - This class is intentionally verbose and explicitly separated by sections for coursework clarity.
 */
public class MainFrame extends JFrame {

    /* =========================================================
       REPOSITORIES / MANAGERS (MODEL LAYER ACCESS)
       ========================================================= */

    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ReferralRepository referralRepository;
    private final StaffRepository staffRepository;
    private final FacilityRepository facilityRepository;

    /* =========================================================
       PATIENT TAB - TABLE + MODEL
       ========================================================= */

    private JTable patientTable;
    private DefaultTableModel patientTableModel;

    /* =========================================================
       CLINICIAN TAB - TABLE + MODEL
       ========================================================= */

    private JTable clinicianTable;
    private DefaultTableModel clinicianTableModel;

    /* =========================================================
       PRESCRIPTION TAB - TABLE + MODEL
       ========================================================= */

    private JTable prescriptionTable;
    private DefaultTableModel prescriptionTableModel;

    /* =========================================================
       REFERRAL TAB - TABLE + MODEL
       ========================================================= */

    private JTable referralTable;
    private DefaultTableModel referralTableModel;

    /* =========================================================
       STAFF TAB - TABLE + MODEL
       ========================================================= */

    private JTable staffTable;
    private DefaultTableModel staffTableModel;

     /* =========================================================
       FACILITY TAB - TABLE + MODEL
       ========================================================= */
    private JTable facilityTable;
    private DefaultTableModel facilityTableModel;

    /**
     * Constructs the main application window.
     * - Initialise repositories
     * - Build tabs
     * - Load data into each tab table
     */
    public MainFrame() {

        // ---------- Window configuration ----------
        setTitle("Healthcare Referral System");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- Repository initialisation (Model layer) ----------
        // These repositories handle CSV load/save and keep in-memory lists.
        patientRepository = new PatientRepository();
        clinicianRepository = new ClinicianRepository();
        prescriptionRepository = new PrescriptionRepository();
        referralRepository = new ReferralRepository();
        staffRepository = new StaffRepository();
        facilityRepository = new FacilityRepository();

        // ---------- Build tabbed UI ----------
        // Each tab is created by a dedicated method for clarity.
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", createPatientPanel());
        tabs.addTab("Clinicians", createClinicianPanel());
        tabs.addTab("Prescriptions", createPrescriptionPanel());
        tabs.addTab("Referrals", createReferralPanel());
        tabs.addTab("Staff", createStaffPanel());
        tabs.addTab("Facilities", createFacilityPanel());

        add(tabs, BorderLayout.CENTER);
    }

    /* =========================================================
       PATIENT TAB (CSV columns must match your patients.csv)
       Expected CSV (your updated format):
       nhsNumber,firstName,lastName,dateOfBirth,phoneNumber,gender,registeredGpSurgery
       ========================================================= */

    private JPanel createPatientPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        // Table model column order should match what you want displayed
        patientTableModel = new DefaultTableModel(
                new String[]{
                        "NHS Number",
                        "First Name",
                        "Last Name",
                        "Date of Birth",
                        "Phone Number",
                        "Gender",
                        "Registered GP Surgery"
                }, 0
        );

        patientTable = new JTable(patientTableModel);
        patientTable.setPreferredScrollableViewportSize(new Dimension(1200, 450));

        // Load initial data from CSV (via repository)
        loadPatients();

        // Button panel
        // -------- Buttons --------
        JButton addBtn = new JButton("Add Patient");
        JButton editBtn = new JButton("Edit Patient");
        JButton deleteBtn = new JButton("Delete Patient");
        JButton viewBtn = new JButton("View Patient");

        addBtn.addActionListener(e -> addPatient());
        editBtn.addActionListener(e -> editPatient());
        deleteBtn.addActionListener(e -> deletePatient());
        viewBtn.addActionListener(e -> viewPatient());

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(viewBtn);

        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads patients from patients.csv via repository and populates table.
     */
    private void loadPatients() {
        try {
            patientRepository.load("data/patients.csv");

            // Clear UI table
            patientTableModel.setRowCount(0);

            // Populate UI from repository objects
            for (Patient p : patientRepository.getAll()) {
                patientTableModel.addRow(new Object[]{
                        p.getNhsNumber(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getDateOfBirth(),
                        p.getPhoneNumber(),
                        p.getGender(),
                        p.getRegisteredGpSurgery()
                });
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    /**
     * Adds a new patient.
     * - Collect input via dialogs
     * - Construct Patient model
     * - Call repository add (persists to CSV)
     * - Reload table
     */
    private void addPatient() {
        try {
            String nhs = promptRequired("NHS Number");
            if (nhs == null) return;

            String first = promptRequired("First Name");
            if (first == null) return;

            String last = promptRequired("Last Name");
            if (last == null) return;

            String dob = promptRequired("Date of Birth (YYYY-MM-DD)");
            if (dob == null) return;

            String phone = promptRequired("Phone Number");
            if (phone == null) return;

            String gender = promptRequired("Gender (M/F)");
            if (gender == null) return;

            String gp = promptRequired("Registered GP Surgery");
            if (gp == null) return;

            // IMPORTANT: This must match your Patient constructor
            Patient newPatient = new Patient(nhs, first, last, dob, phone, gender, gp);

            patientRepository.addPatient(newPatient);

            // Refresh UI
            loadPatients();

            JOptionPane.showMessageDialog(this, "Patient added successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void editPatient() {

    int row = patientTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a patient first.");
        return;
    }

    try {
        String nhs = patientTableModel.getValueAt(row, 0).toString();

        String first = promptRequiredDefault("First Name", patientTableModel.getValueAt(row, 1).toString());
        String last = promptRequiredDefault("Last Name", patientTableModel.getValueAt(row, 2).toString());
        String dob = promptRequiredDefault("Date of Birth", patientTableModel.getValueAt(row, 3).toString());
        String phone = promptRequiredDefault("Phone Number", patientTableModel.getValueAt(row, 4).toString());
        String gender = promptRequiredDefault("Gender", patientTableModel.getValueAt(row, 5).toString());
        String gp = promptRequiredDefault("Registered GP Surgery", patientTableModel.getValueAt(row, 6).toString());

        Patient updated = new Patient(
                nhs,
                first,
                last,
                dob,
                phone,
                gender,
                gp
        );

        patientRepository.updatePatient(updated);
        loadPatients();

        JOptionPane.showMessageDialog(this, "Patient updated successfully.");

    } catch (Exception e) {
        showError(e);
    }
}

private void viewPatient() {

    int row = patientTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a patient first.");
        return;
    }

    StringBuilder details = new StringBuilder();
    details.append("PATIENT DETAILS\n");
    details.append("====================\n\n");

    details.append("NHS Number: ")
           .append(patientTableModel.getValueAt(row, 0)).append("\n");

    details.append("First Name: ")
           .append(patientTableModel.getValueAt(row, 1)).append("\n");

    details.append("Last Name: ")
           .append(patientTableModel.getValueAt(row, 2)).append("\n");

    details.append("Date of Birth: ")
           .append(patientTableModel.getValueAt(row, 3)).append("\n");

    details.append("Phone Number: ")
           .append(patientTableModel.getValueAt(row, 4)).append("\n");

    details.append("Gender: ")
           .append(patientTableModel.getValueAt(row, 5)).append("\n");

    details.append("Registered GP Surgery: ")
           .append(patientTableModel.getValueAt(row, 6)).append("\n");

    JTextArea textArea = new JTextArea(details.toString());
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "View Patient",
            JOptionPane.INFORMATION_MESSAGE
    );
}


    /**
     * Deletes selected patient (by NHS number).
     */
    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient row first.");
            return;
        }

        try {
            String nhs = patientTableModel.getValueAt(row, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete patient with NHS: " + nhs + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            patientRepository.deletePatient(nhs);
            loadPatients();

            JOptionPane.showMessageDialog(this, "Patient deleted successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    /* =========================================================
       CLINICIAN TAB
       Expected CSV header:
       clinicianId,name,role,specialty,workplace
       ========================================================= */

    private JPanel createClinicianPanel() {

    JPanel panel = new JPanel(new BorderLayout());

    clinicianTableModel = new DefaultTableModel(
            new String[]{"Clinician ID", "Name", "Role", "Specialty", "Staff Code"}, 0
    );

    clinicianTable = new JTable(clinicianTableModel);

    // ✅ LOAD DATA
    loadClinicians();

    // -------- Buttons --------
    JButton addBtn = new JButton("Add Clinician");
    JButton editBtn = new JButton("Edit Clinician");
    JButton deleteBtn = new JButton("Delete Clinician");
    JButton viewBtn = new JButton("View Clinician");

    addBtn.addActionListener(e -> addClinician());
    editBtn.addActionListener(e -> editClinician());
    deleteBtn.addActionListener(e -> deleteClinician());
    viewBtn.addActionListener(e -> viewClinician());

    JPanel buttons = new JPanel();
    buttons.add(addBtn);
    buttons.add(editBtn);
    buttons.add(deleteBtn);
    buttons.add(viewBtn);

    // ✅ THIS LINE WAS MISSING
    panel.add(new JScrollPane(clinicianTable), BorderLayout.CENTER);

    panel.add(buttons, BorderLayout.SOUTH);

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

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addClinician() {
        try {
            String id = promptRequired("Clinician ID");
            if (id == null) return;

            String name = promptRequired("Full Name");
            if (name == null) return;

            String role = promptRequired("Role (Doctor/Nurse/Specialist)");
            if (role == null) return;

            String specialty = promptRequired("Specialty");
            if (specialty == null) return;

            String workplace = promptRequired("Workplace");
            if (workplace == null) return;

            Clinician c = new Clinician(id, name, role, specialty, workplace);

            clinicianRepository.add(c);
            loadClinicians();

            JOptionPane.showMessageDialog(this, "Clinician added successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void viewClinician() {

    int row = clinicianTable.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a clinician first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    StringBuilder details = new StringBuilder();

    details.append("Clinician ID: ")
           .append(clinicianTableModel.getValueAt(row, 0)).append("\n\n");

    details.append("Name:\n")
           .append(clinicianTableModel.getValueAt(row, 1)).append("\n\n");

    details.append("Role:\n")
           .append(clinicianTableModel.getValueAt(row, 2)).append("\n\n");

    details.append("Specialty:\n")
           .append(clinicianTableModel.getValueAt(row, 3)).append("\n\n");

    details.append("Workplace / Staff Code:\n")
           .append(clinicianTableModel.getValueAt(row, 4));

    JTextArea textArea = new JTextArea(details.toString(), 16, 55);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(textArea);

    JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Clinician Details",
            JOptionPane.INFORMATION_MESSAGE
    );
}


    private void editClinician() {
        int row = clinicianTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a clinician row first.");
            return;
        }

        try {
            String id = clinicianTableModel.getValueAt(row, 0).toString();

            String name = promptRequiredDefault("Full Name", clinicianTableModel.getValueAt(row, 1).toString());
            if (name == null) return;

            String role = promptRequiredDefault("Role", clinicianTableModel.getValueAt(row, 2).toString());
            if (role == null) return;

            String specialty = promptRequiredDefault("Specialty", clinicianTableModel.getValueAt(row, 3).toString());
            if (specialty == null) return;

            String workplace = promptRequiredDefault("Workplace", clinicianTableModel.getValueAt(row, 4).toString());
            if (workplace == null) return;

            Clinician updated = new Clinician(id, name, role, specialty, workplace);

            clinicianRepository.update(updated);
            loadClinicians();

            JOptionPane.showMessageDialog(this, "Clinician updated successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteClinician() {
        int row = clinicianTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a clinician row first.");
            return;
        }

        try {
            String id = clinicianTableModel.getValueAt(row, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete clinician: " + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            clinicianRepository.delete(id);
            loadClinicians();

            JOptionPane.showMessageDialog(this, "Clinician deleted successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    /* =========================================================
       PRESCRIPTION TAB
       Expected CSV header:
       prescriptionId,patientNhsNumber,clinicianId,medication,dosage,pharmacy,collectionStatus
       ========================================================= */

private JPanel createPrescriptionPanel() {

    JPanel panel = new JPanel(new BorderLayout());

    prescriptionTableModel = new DefaultTableModel(
            new String[]{
                    "Prescription ID",
                    "Patient NHS",
                    "Clinician ID",
                    "Medication",
                    "Dosage",
                    "Pharmacy",
                    "Collection Status"
            }, 0
    );

    prescriptionTable = new JTable(prescriptionTableModel);
    prescriptionTable.setRowHeight(22);

    loadPrescriptions();

    // -------- Buttons --------
    JButton addBtn = new JButton("Add Prescription");
    JButton editBtn = new JButton("Edit Prescription");
    JButton deleteBtn = new JButton("Delete Prescription");
    JButton viewBtn = new JButton("View Prescription"); // ✅ THIS IS THE BUTTON

    addBtn.addActionListener(e -> addPrescription());
    editBtn.addActionListener(e -> editPrescription());
    deleteBtn.addActionListener(e -> deletePrescription());
    viewBtn.addActionListener(e -> viewPrescription());

    JPanel buttons = new JPanel();
    buttons.add(addBtn);
    buttons.add(editBtn);
    buttons.add(deleteBtn);
    buttons.add(viewBtn); // ✅ MUST BE HERE

    panel.add(new JScrollPane(prescriptionTable), BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);

    return panel;
}


    private void deletePrescription() {
    int row = prescriptionTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a prescription first.");
        return;
    }

    try {
        String id = prescriptionTableModel.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete prescription " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            prescriptionRepository.deletePrescription(id);
            loadPrescriptions();
        }

    } catch (Exception ex) {
        showError(ex);
    }
}

/**
 * Displays all details of the selected prescription
 * in a readable, scrollable dialog.
 */
private void viewPrescription() {

    int row = prescriptionTable.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a prescription first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    StringBuilder details = new StringBuilder();

    details.append("Prescription ID: ")
           .append(prescriptionTableModel.getValueAt(row, 0)).append("\n");
    details.append("Patient NHS: ")
           .append(prescriptionTableModel.getValueAt(row, 1)).append("\n");
    details.append("Clinician ID: ")
           .append(prescriptionTableModel.getValueAt(row, 2)).append("\n\n");

    details.append("Medication:\n")
           .append(prescriptionTableModel.getValueAt(row, 3)).append("\n\n");

    details.append("Dosage:\n")
           .append(prescriptionTableModel.getValueAt(row, 4)).append("\n\n");

    details.append("Pharmacy:\n")
           .append(prescriptionTableModel.getValueAt(row, 5)).append("\n\n");

    details.append("Collection Status:\n")
           .append(prescriptionTableModel.getValueAt(row, 6));

    JTextArea textArea = new JTextArea(details.toString(), 18, 60);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(textArea);

    JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Prescription Details",
            JOptionPane.INFORMATION_MESSAGE
    );
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

        } catch (Exception ex) {
            showError(ex);
        }
    }

private void addPrescription() {
    try {
        String id = promptRequired("Prescription ID");
        if (id == null) return;

        String patientNhs = promptRequired("Patient NHS Number");
        if (patientNhs == null) return;

        String clinicianId = promptRequired("Clinician ID");
        if (clinicianId == null) return;

        String medication = promptRequired("Medication");
        if (medication == null) return;

        String dosage = promptRequired("Dosage");
        if (dosage == null) return;

        String pharmacy = promptRequired("Pharmacy");
        if (pharmacy == null) return;

        String status = promptRequired("Collection Status (e.g. Pending)");
        if (status == null) return;

        // ✅ CORRECT constructor order
        Prescription p = new Prescription(
                id,
                patientNhs,
                clinicianId,
                medication,
                dosage,
                pharmacy,
                status
        );

        prescriptionRepository.addPrescription(p);
        loadPrescriptions();

        JOptionPane.showMessageDialog(this, "Prescription added successfully.");

    } catch (Exception ex) {
        showError(ex);
    }
}


private void editPrescription() {
    int row = prescriptionTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a prescription row first.");
        return;
    }

    try {
        String id = prescriptionTableModel.getValueAt(row, 0).toString();

        String patientNhs = promptRequiredDefault(
                "Patient NHS",
                prescriptionTableModel.getValueAt(row, 1).toString()
        );

        String clinicianId = promptRequiredDefault(
                "Clinician ID",
                prescriptionTableModel.getValueAt(row, 2).toString()
        );

        String medication = promptRequiredDefault(
                "Medication",
                prescriptionTableModel.getValueAt(row, 3).toString()
        );

        String dosage = promptRequiredDefault(
                "Dosage",
                prescriptionTableModel.getValueAt(row, 4).toString()
        );

        String pharmacy = promptRequiredDefault(
                "Pharmacy",
                prescriptionTableModel.getValueAt(row, 5).toString()
        );

        String status = promptRequiredDefault(
                "Collection Status",
                prescriptionTableModel.getValueAt(row, 6).toString()
        );

        // ✅ CORRECT order
        Prescription updated = new Prescription(
                id,
                patientNhs,
                clinicianId,
                medication,
                dosage,
                pharmacy,
                status
        );

        prescriptionRepository.updatePrescription(updated);
        loadPrescriptions();

        JOptionPane.showMessageDialog(this, "Prescription updated successfully.");

    } catch (Exception ex) {
        showError(ex);
    }
}


    /* =========================================================
       REFERRAL TAB (CSV LOAD + SINGLETON OUTPUT + CRUD)
       Your referrals.csv has MANY columns. Your ReferralRepository maps the important ones.
       We will DISPLAY more columns here to match your CSV better.
       ========================================================= */

    private JPanel createReferralPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        // Expanded table to reflect more CSV info
        referralTableModel = new DefaultTableModel(
                new String[]{
                        "Referral ID",
                        "Patient ID",
                        "Referring Clinician",
                        "From Facility",
                        "To Facility",
                        "Referral Date",
                        "Urgency",
                        "Reason",
                        "Clinical Summary",
                        "Investigations",
                        "Status",
                        "Notes"
                }, 0
        );

        referralTable = new JTable(referralTableModel);
        referralTable.setPreferredScrollableViewportSize(new Dimension(1200, 450));

        loadReferrals();

        JButton viewBtn = new JButton("View");
        JButton createBtn = new JButton("Create Referral (Singleton)");
        JButton editBtn = new JButton("Edit Referral");
        JButton deleteBtn = new JButton("Delete Referral");

        viewBtn.addActionListener(e -> viewReferral());
        createBtn.addActionListener(e -> createReferral());
        editBtn.addActionListener(e -> editReferral());
        deleteBtn.addActionListener(e -> deleteReferral());

        JPanel buttons = new JPanel();
        buttons.add(viewBtn);
        buttons.add(createBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);

        panel.add(new JScrollPane(referralTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

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
                        r.getReferralDate(),
                        r.getUrgencyLevel(),
                        r.getReferralReason(),
                        r.getClinicalSummary(),
                        r.getRequestedInvestigations(),
                        r.getStatus(),
                        r.getNotes()
                });
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }


    /**
     * Creates a referral and processes it using the Singleton ReferralManager.
     * This satisfies the rubric requirement for Singleton.
     *
     * IMPORTANT:
     *  - ReferralManager should append to referrals.csv AND generate a referral text file.
     *  - After calling manager, we reload the table from CSV.
     */
    private void createReferral() {
        try {
            String referralId = promptRequired("Referral ID (e.g., R011)");
            if (referralId == null) return;

            String patientId = promptRequired("Patient ID (e.g., P001)");
            if (patientId == null) return;

            String referringClinician = promptRequired("Referring Clinician ID (e.g., C001)");
            if (referringClinician == null) return;

            String fromFacility = promptRequired("From Facility ID (e.g., S001)");
            if (fromFacility == null) return;

            String toFacility = promptRequired("To Facility ID (e.g., H001)");
            if (toFacility == null) return;

            String referralDate = promptRequired("Referral Date (YYYY-MM-DD)");
            if (referralDate == null) return;

            String urgency = promptRequired("Urgency Level (Routine/Urgent/Non-urgent)");
            if (urgency == null) return;

            String reason = promptRequired("Referral Reason");
            if (reason == null) return;

            String summary = promptRequired("Clinical Summary");
            if (summary == null) return;

            String investigations = promptOptional("Requested Investigations (optional)");
            if (investigations == null) investigations = "";

            String status = promptRequiredDefault("Status", "New");
            if (status == null) return;

            String notes = promptOptional("Notes (optional)");
            if (notes == null) notes = "";

            // IMPORTANT: Must match your Referral constructor EXACTLY (12 parameters)
            Referral r = new Referral(
                    referralId,
                    patientId,
                    referringClinician,
                    fromFacility,
                    toFacility,
                    summary,
                    urgency,
                    referralDate,
                    reason,
                    investigations,
                    status,
                    notes
            );

            // ✅ SINGLETON usage (rubric)
            ReferralManager.getInstance().processReferral(r);

            loadReferrals();

            JOptionPane.showMessageDialog(this,
                    "Referral created successfully.\nA referral text file should also be generated by ReferralManager.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void editReferral() {
        int row = referralTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a referral row first.");
            return;
        }

        try {
            String referralId = referralTableModel.getValueAt(row, 0).toString();

            String patientId = promptRequiredDefault("Patient ID", referralTableModel.getValueAt(row, 1).toString());
            if (patientId == null) return;

            String referringClinician = promptRequiredDefault("Referring Clinician ID", referralTableModel.getValueAt(row, 2).toString());
            if (referringClinician == null) return;

            String fromFacility = promptRequiredDefault("From Facility ID", referralTableModel.getValueAt(row, 3).toString());
            if (fromFacility == null) return;

            String toFacility = promptRequiredDefault("To Facility ID", referralTableModel.getValueAt(row, 4).toString());
            if (toFacility == null) return;

            String referralDate = promptRequiredDefault("Referral Date (YYYY-MM-DD)", referralTableModel.getValueAt(row, 5).toString());
            if (referralDate == null) return;

            String urgency = promptRequiredDefault("Urgency Level", referralTableModel.getValueAt(row, 6).toString());
            if (urgency == null) return;

            String reason = promptRequiredDefault("Referral Reason", referralTableModel.getValueAt(row, 7).toString());
            if (reason == null) return;

            String summary = promptRequiredDefault("Clinical Summary", referralTableModel.getValueAt(row, 8).toString());
            if (summary == null) return;

            String investigations = promptOptionalDefault("Requested Investigations", referralTableModel.getValueAt(row, 9).toString());
            if (investigations == null) investigations = "";

            String status = promptRequiredDefault("Status", referralTableModel.getValueAt(row, 10).toString());
            if (status == null) return;

            String notes = promptOptionalDefault("Notes", referralTableModel.getValueAt(row, 11).toString());
            if (notes == null) notes = "";

            Referral updated = new Referral(
                    referralId,
                    patientId,
                    referringClinician,
                    fromFacility,
                    toFacility,
                    summary,
                    urgency,
                    referralDate,
                    reason,
                    investigations,
                    status,
                    notes
            );

            referralRepository.updateReferral(updated);
            loadReferrals();

            JOptionPane.showMessageDialog(this, "Referral updated successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteReferral() {
        int row = referralTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a referral row first.");
            return;
        }

        try {
            String referralId = referralTableModel.getValueAt(row, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete referral: " + referralId + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            referralRepository.deleteReferral(referralId);
            loadReferrals();

            JOptionPane.showMessageDialog(this, "Referral deleted successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

        /**
 * Displays all referral details for the selected row in a read-only dialog.
 * This avoids column truncation and allows viewing the full referral content.
 */
/**
 * Displays all referral details for the selected row
 * in a readable, scrollable dialog.
 */
private void viewReferral() {

    int row = referralTable.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a referral first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    StringBuilder details = new StringBuilder();

    details.append("Referral ID: ")
           .append(referralTableModel.getValueAt(row, 0)).append("\n");
    details.append("Patient ID: ")
           .append(referralTableModel.getValueAt(row, 1)).append("\n");
    details.append("Referring Clinician: ")
           .append(referralTableModel.getValueAt(row, 2)).append("\n");
    details.append("From Facility: ")
           .append(referralTableModel.getValueAt(row, 3)).append("\n");
    details.append("To Facility: ")
           .append(referralTableModel.getValueAt(row, 4)).append("\n\n");

    details.append("Referral Date: ")
           .append(referralTableModel.getValueAt(row, 5)).append("\n");
    details.append("Urgency: ")
           .append(referralTableModel.getValueAt(row, 6)).append("\n");
    details.append("Reason:\n")
           .append(referralTableModel.getValueAt(row, 7)).append("\n\n");

    details.append("Clinical Summary:\n")
           .append(referralTableModel.getValueAt(row, 8)).append("\n\n");

    details.append("Investigations:\n")
           .append(referralTableModel.getValueAt(row, 9)).append("\n\n");

    details.append("Status: ")
           .append(referralTableModel.getValueAt(row, 10)).append("\n\n");

    details.append("Notes:\n")
           .append(referralTableModel.getValueAt(row, 11));

    JTextArea textArea = new JTextArea(details.toString(), 20, 60);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(textArea);

    JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Referral Details",
            JOptionPane.INFORMATION_MESSAGE
    );
}


    /* =========================================================
       STAFF TAB (ADD / EDIT / DELETE)
       Expected staff.csv header depends on your file.
       This UI assumes: staffId,name,role,department
       ========================================================= */

    private JPanel createStaffPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        staffTableModel = new DefaultTableModel(
                new String[]{"Staff ID", "Name", "Role", "Department"}, 0
        );

        staffTable = new JTable(staffTableModel);
        staffTable.setPreferredScrollableViewportSize(new Dimension(1200, 450));

        loadStaff();

        JButton addBtn = new JButton("Add Staff");
        JButton editBtn = new JButton("Edit Staff");
        JButton deleteBtn = new JButton("Delete Staff");

        addBtn.addActionListener(e -> addStaff());
        editBtn.addActionListener(e -> editStaff());
        deleteBtn.addActionListener(e -> deleteStaff());

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);

        panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private void loadStaff() {
        try {
            staffRepository.load("data/staff.csv");
            staffTableModel.setRowCount(0);

            for (Staff s : staffRepository.getAll()) {
                staffTableModel.addRow(new Object[]{
                        s.getStaffId(),
                        s.getName(),
                        s.getRole(),
                        s.getDepartment()
                });
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addStaff() {
        try {
            String id = promptRequired("Staff ID");
            if (id == null) return;

            String name = promptRequired("Name");
            if (name == null) return;

            String role = promptRequired("Role");
            if (role == null) return;

            String dept = promptRequired("Department");
            if (dept == null) return;

            Staff s = new Staff(id, name, role, dept);

            staffRepository.addStaff(s);
            loadStaff();

            JOptionPane.showMessageDialog(this, "Staff added successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void editStaff() {
        int row = staffTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a staff row first.");
            return;
        }

        try {
            String id = staffTableModel.getValueAt(row, 0).toString();

            String name = promptRequiredDefault("Name", staffTableModel.getValueAt(row, 1).toString());
            if (name == null) return;

            String role = promptRequiredDefault("Role", staffTableModel.getValueAt(row, 2).toString());
            if (role == null) return;

            String dept = promptRequiredDefault("Department", staffTableModel.getValueAt(row, 3).toString());
            if (dept == null) return;

            Staff updated = new Staff(id, name, role, dept);

            staffRepository.updateStaff(updated);
            loadStaff();

            JOptionPane.showMessageDialog(this, "Staff updated successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteStaff() {
        int row = staffTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a staff row first.");
            return;
        }

        try {
            String id = staffTableModel.getValueAt(row, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete staff: " + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            staffRepository.deleteStaff(id);
            loadStaff();

            JOptionPane.showMessageDialog(this, "Staff deleted successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    /* =========================================================
   FACILITIES TAB (ADD / EDIT / DELETE)
   ========================================================= */

private JPanel createFacilityPanel() {

    JPanel panel = new JPanel(new BorderLayout());

    facilityTableModel = new DefaultTableModel(
            new String[]{"Facility ID", "Name", "Type", "Location"}, 0
    );
    facilityTable = new JTable(facilityTableModel);

    loadFacilities();

    JButton add = new JButton("Add");
    JButton edit = new JButton("Edit");
    JButton delete = new JButton("Delete");

    add.addActionListener(e -> addFacility());
    edit.addActionListener(e -> editFacility());
    delete.addActionListener(e -> deleteFacility());

    JPanel buttons = new JPanel();
    buttons.add(add);
    buttons.add(edit);
    buttons.add(delete);

    panel.add(new JScrollPane(facilityTable), BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);

    return panel;
}

private void loadFacilities() {
    try {
        facilityRepository.load("data/facilities.csv");
        facilityTableModel.setRowCount(0);

        for (Facility f : facilityRepository.getAll()) {
            facilityTableModel.addRow(new Object[]{
                    f.getFacilityId(),
                    f.getFacilityName(),
                    f.getFacilityType(),
                    f.getLocation()
            });
        }
    } catch (Exception e) {
        showError(e);
    }
}

private void addFacility() {
    try {
        Facility f = new Facility(
                JOptionPane.showInputDialog(this, "Facility ID"),
                JOptionPane.showInputDialog(this, "Facility Name"),
                JOptionPane.showInputDialog(this, "Facility Type"),
                JOptionPane.showInputDialog(this, "Location")
        );

        facilityRepository.addFacility(f);
        loadFacilities();

    } catch (Exception e) {
        showError(e);
    }
}

private void editFacility() {

    int row = facilityTable.getSelectedRow();
    if (row == -1) return;

    try {
        Facility updated = new Facility(
                facilityTableModel.getValueAt(row, 0).toString(),
                JOptionPane.showInputDialog(this, "Facility Name", facilityTableModel.getValueAt(row, 1)),
                JOptionPane.showInputDialog(this, "Facility Type", facilityTableModel.getValueAt(row, 2)),
                JOptionPane.showInputDialog(this, "Location", facilityTableModel.getValueAt(row, 3))
        );

        facilityRepository.updateFacility(updated);
        loadFacilities();

    } catch (Exception e) {
        showError(e);
    }
}

private void deleteFacility() {

    int row = facilityTable.getSelectedRow();
    if (row == -1) return;

    try {
        facilityRepository.deleteFacility(
                facilityTableModel.getValueAt(row, 0).toString()
        );
        loadFacilities();

    } catch (Exception e) {
        showError(e);
    }
}


    /* =========================================================
       HELPER METHODS (UI QUALITY / SAFETY)
       ========================================================= */

    /**
     * Prompt helper for required fields.
     * - Returns null if user cancels
     * - Forces non-empty input
     */
    private String promptRequired(String label) {
        while (true) {
            String val = JOptionPane.showInputDialog(this, label + ":");
            if (val == null) return null;               // user cancelled
            val = val.trim();
            if (!val.isEmpty()) return val;             // valid
            JOptionPane.showMessageDialog(this, label + " is required.");
        }
    }

    /**
     * Prompt helper for required fields with default value prefilled.
     */
    private String promptRequiredDefault(String label, String defaultVal) {
        while (true) {
            String val = JOptionPane.showInputDialog(this, label + ":", defaultVal);
            if (val == null) return null;
            val = val.trim();
            if (!val.isEmpty()) return val;
            JOptionPane.showMessageDialog(this, label + " is required.");
        }
    }

    /**
     * Prompt helper for optional fields (may be blank).
     * Returns null if cancel.
     */
    private String promptOptional(String label) {
        String val = JOptionPane.showInputDialog(this, label + ":");
        if (val == null) return null;
        return val.trim();
    }

    /**
     * Prompt helper for optional fields with default.
     * Returns null if cancel.
     */
    private String promptOptionalDefault(String label, String defaultVal) {
        String val = JOptionPane.showInputDialog(this, label + ":", defaultVal);
        if (val == null) return null;
        return val.trim();
    }

    /**
     * Centralised error popup to keep UI code clean and consistent.
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
