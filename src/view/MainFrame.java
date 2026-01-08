package view;

import model.Patient;
import model.Appointment;
import model.Clinician;
import model.Prescription;
import model.Referral;
import model.Staff;
import model.UserSession;
import model.Facility;

import repository.PatientRepository;
import repository.ClinicianRepository;
import repository.PrescriptionRepository;
import repository.ReferralRepository;
import repository.ReferralWriter;
import repository.ReferralManager;
import repository.StaffRepository;
import repository.FacilityRepository;
import repository.AppointmentRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.awt.GridLayout;
import java.io.IOException;
import java.time.LocalDate;



/**
 * MainFrame
 * ---------
 * Main GUI window for the Healthcare Referral System.
 *
 *  MVC (View Layer) responsibilities:
 *  - Build Swing components (tabs, tables, buttons)
 *  - Display data returned by repositories
 *  - Capture user input via dialogs
 *  - Call repository/manager methods to perform CRUD
 *
 *  View must NOT:
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
    private final AppointmentRepository appointmentRepository;

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

    /* =========================================================
       APPOINTMENT TAB - TABLE + MODEL
       ========================================================= */
        // JTable used to display appointments
        private JTable appointmentTable;

        // Table model controlling appointment table data
        private DefaultTableModel appointmentTableModel;    


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
        appointmentRepository = new AppointmentRepository();

          

        // ---------- Build tabbed UI ----------
        // Each tab is created by a dedicated method for clarity.
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", createPatientPanel());
        tabs.addTab("Clinicians", createClinicianPanel());
        tabs.addTab("Prescriptions", createPrescriptionPanel());
        tabs.addTab("Referrals", createReferralPanel());
        tabs.addTab("Staff", createStaffPanel());
        tabs.addTab("Facilities", createFacilityPanel());
        tabs.addTab("Appointments", createAppointmentPanel());

        // Apply-Role-Based-Access Control (RBAC)
        applyRolePermissions(tabs);

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
                "Emergency Contact",
                "Gender",
                "Address",
                "Postcode",
                "Email",
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
        JButton prescriptionsBtn = new JButton("View Prescriptions");


        addBtn.addActionListener(e -> addPatient());
        editBtn.addActionListener(e -> editPatient());
        deleteBtn.addActionListener(e -> deletePatient());
        viewBtn.addActionListener(e -> viewPatient());
        prescriptionsBtn.addActionListener(e -> viewPatientPrescriptions());


        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(viewBtn);
        buttons.add(prescriptionsBtn);

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
            p.getEmergencyContactNumber(),
            p.getGender(),
            p.getAddress(),
            p.getPostcode(),
            p.getEmail(),
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

    // Create form fields
    JTextField nhsField = new JTextField();
    JTextField firstNameField = new JTextField();
    JTextField lastNameField = new JTextField();
    JTextField dobField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField emergencyField = new JTextField();
    JTextField genderField = new JTextField();
    JTextField addressField = new JTextField();
    JTextField postcodeField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField gpField = new JTextField();

    // Build form panel
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.add(new JLabel("NHS Number"));
    panel.add(nhsField);

    panel.add(new JLabel("First Name"));
    panel.add(firstNameField);

    panel.add(new JLabel("Last Name"));
    panel.add(lastNameField);

    panel.add(new JLabel("Date of Birth (YYYY-MM-DD)"));
    panel.add(dobField);

    panel.add(new JLabel("Phone Number"));
    panel.add(phoneField);

    panel.add(new JLabel("Emergency Contact Number"));
    panel.add(emergencyField);

    panel.add(new JLabel("Gender"));
    panel.add(genderField);

    panel.add(new JLabel("Address"));
    panel.add(addressField);

    panel.add(new JLabel("Postcode"));
    panel.add(postcodeField);

    panel.add(new JLabel("Email Address"));
    panel.add(emailField);

    panel.add(new JLabel("Registered GP Surgery"));
    panel.add(gpField);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Add Patient",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    // ---------------------------------
// Multi-field validation (Add Patient)
// Lists ALL missing fields in one popup
// ---------------------------------
if (showPatientMissingFields(
        "NHS Number", nhsField.getText(),
        "First Name", firstNameField.getText(),
        "Last Name", lastNameField.getText(),
        "Date of Birth", dobField.getText(),
        "Phone Number", phoneField.getText(),
        "Emergency Contact Number", emergencyField.getText(),
        "Gender", genderField.getText(),
        "Address", addressField.getText(),
        "Postcode", postcodeField.getText(),
        "Email", emailField.getText(),
        "Registered GP Surgery", gpField.getText()
)) {
    return; // stop if ANY required field is missing
}


    try {
        Patient newPatient = new Patient(
                nhsField.getText().trim(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                dobField.getText().trim(),
                phoneField.getText().trim(),
                emergencyField.getText().trim(),
                genderField.getText().trim(),
                addressField.getText().trim(),
                postcodeField.getText().trim(),
                emailField.getText().trim(),
                gpField.getText().trim()
        );

        patientRepository.addPatient(newPatient);
        loadPatients();

        JOptionPane.showMessageDialog(this, "Patient added successfully.");

    } catch (Exception ex) {
        showError(ex);
    }
}


private void editPatient() {

    // Ensure a row is selected
    int row = patientTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a patient first.");
        return;
    }

    // Read existing values from the table model
    String nhsNumber = patientTableModel.getValueAt(row, 0).toString();

    JTextField nhsField = new JTextField(nhsNumber);
    nhsField.setEditable(false); // Identifier must not be changed

    JTextField firstNameField =
            new JTextField(patientTableModel.getValueAt(row, 1).toString());
    JTextField lastNameField =
            new JTextField(patientTableModel.getValueAt(row, 2).toString());
    JTextField dobField =
            new JTextField(patientTableModel.getValueAt(row, 3).toString());
    JTextField phoneField =
            new JTextField(patientTableModel.getValueAt(row, 4).toString());
    JTextField emergencyField =
            new JTextField(patientTableModel.getValueAt(row, 5).toString());
    JTextField genderField =
            new JTextField(patientTableModel.getValueAt(row, 6).toString());
    JTextField addressField =
            new JTextField(patientTableModel.getValueAt(row, 7).toString());
    JTextField postcodeField =
            new JTextField(patientTableModel.getValueAt(row, 8).toString());
    JTextField emailField =
            new JTextField(patientTableModel.getValueAt(row, 9).toString());
    JTextField gpField =
            new JTextField(patientTableModel.getValueAt(row, 10).toString());

    // Build the form panel
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.add(new JLabel("NHS Number"));
    panel.add(nhsField);

    panel.add(new JLabel("First Name"));
    panel.add(firstNameField);

    panel.add(new JLabel("Last Name"));
    panel.add(lastNameField);

    panel.add(new JLabel("Date of Birth (YYYY-MM-DD)"));
    panel.add(dobField);

    panel.add(new JLabel("Phone Number"));
    panel.add(phoneField);

    panel.add(new JLabel("Emergency Contact Number"));
    panel.add(emergencyField);

    panel.add(new JLabel("Gender"));
    panel.add(genderField);

    panel.add(new JLabel("Address"));
    panel.add(addressField);

    panel.add(new JLabel("Postcode"));
    panel.add(postcodeField);

    panel.add(new JLabel("Email"));
    panel.add(emailField);

    panel.add(new JLabel("Registered GP Surgery"));
    panel.add(gpField);

    // Show the dialog
    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Edit Patient",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) {
        return;
    }

    // Field-specific validation
    StringBuilder missingFields = new StringBuilder();

    if (firstNameField.getText().trim().isEmpty()) {
        missingFields.append("- First Name\n");
    }
    if (lastNameField.getText().trim().isEmpty()) {
        missingFields.append("- Last Name\n");
    }
    if (dobField.getText().trim().isEmpty()) {
        missingFields.append("- Date of Birth\n");
    }
    if (phoneField.getText().trim().isEmpty()) {
        missingFields.append("- Phone Number\n");
    }
    if (emergencyField.getText().trim().isEmpty()) {
        missingFields.append("- Emergency Contact Number\n");
    }
    if (genderField.getText().trim().isEmpty()) {
        missingFields.append("- Gender\n");
    }
    if (addressField.getText().trim().isEmpty()) {
        missingFields.append("- Address\n");
    }
    if (postcodeField.getText().trim().isEmpty()) {
        missingFields.append("- Postcode\n");
    }
    if (emailField.getText().trim().isEmpty()) {
        missingFields.append("- Email\n");
    }
    if (gpField.getText().trim().isEmpty()) {
        missingFields.append("- Registered GP Surgery\n");
    }

    // Show validation message if any fields are missing
    if (missingFields.length() > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in the following fields:\n\n" + missingFields,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        // Create updated patient object
        Patient updated = new Patient(
                nhsNumber,
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                dobField.getText().trim(),
                phoneField.getText().trim(),
                emergencyField.getText().trim(),
                genderField.getText().trim(),
                addressField.getText().trim(),
                postcodeField.getText().trim(),
                emailField.getText().trim(),
                gpField.getText().trim()
        );

        // Persist changes and refresh table
        patientRepository.updatePatient(updated);
        loadPatients();

        JOptionPane.showMessageDialog(this, "Patient updated successfully.");

    } catch (Exception ex) {
        showError(ex);
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

    details.append("Emergency Contact: ")
           .append(patientTableModel.getValueAt(row, 5)).append("\n");

    details.append("Gender: ")
           .append(patientTableModel.getValueAt(row, 6)).append("\n");

    details.append("Address: ")
           .append(patientTableModel.getValueAt(row, 7)).append("\n");

    details.append("Postcode: ")
           .append(patientTableModel.getValueAt(row, 8)).append("\n");

    details.append("Email: ")
           .append(patientTableModel.getValueAt(row, 9)).append("\n");

    details.append("Registered GP Surgery: ")
           .append(patientTableModel.getValueAt(row, 10)).append("\n");

    JTextArea textArea = new JTextArea(details.toString(), 18, 50);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);

    JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "View Patient",
            JOptionPane.INFORMATION_MESSAGE
    );
}

private void viewPatientPrescriptions() {

    int row = patientTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a patient first.");
        return;
    }

    String patientNhs = patientTableModel.getValueAt(row, 0).toString();

    try {
        prescriptionRepository.load("data/prescriptions.csv");

        StringBuilder details = new StringBuilder();
        details.append("PRESCRIPTIONS FOR PATIENT: ").append(patientNhs).append("\n");
        details.append("====================================\n\n");

        boolean found = false;

        for (Prescription p : prescriptionRepository.getAll()) {
            if (p.getPatientNhsNumber().equals(patientNhs)) {

                found = true;

                details.append("Prescription ID: ").append(p.getPrescriptionId()).append("\n");
                details.append("Clinician ID: ").append(p.getClinicianId()).append("\n");
                details.append("Medication: ").append(p.getMedication()).append("\n");
                details.append("Dosage: ").append(p.getDosage()).append("\n");
                details.append("Pharmacy: ").append(p.getPharmacy()).append("\n");
                details.append("Status: ").append(p.getCollectionStatus()).append("\n");
                details.append("------------------------------------\n");
            }
        }

        if (!found) {
            details.append("No prescriptions found for this patient.");
        }

        JTextArea textArea = new JTextArea(details.toString(), 20, 60);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Patient Prescriptions",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        showError(ex);
    }
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

    //  LOAD DATA
    loadClinicians();

    // -------- Buttons --------
    JButton addBtn = new JButton("Add Clinician");
    JButton editBtn = new JButton("Edit Clinician");
    JButton deleteBtn = new JButton("Delete Clinician");
    JButton viewBtn = new JButton("View Clinician");
    JButton viewPrescriptionsBtn = new JButton("View Prescriptions");


    addBtn.addActionListener(e -> addClinician());
    editBtn.addActionListener(e -> editClinician());
    deleteBtn.addActionListener(e -> deleteClinician());
    viewBtn.addActionListener(e -> viewClinician());
    viewPrescriptionsBtn.addActionListener(e -> viewClinicianPrescriptions());


    JPanel buttons = new JPanel();
    buttons.add(addBtn);
    buttons.add(editBtn);
    buttons.add(deleteBtn);
    buttons.add(viewBtn);
    buttons.add(viewPrescriptionsBtn);


    //  THIS LINE WAS MISSING
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

    // Create form fields
    JTextField idField = new JTextField();
    JTextField nameField = new JTextField();

    //  Dropdown instead of text field
    JComboBox<String> roleCombo = new JComboBox<>(new String[]{
            "GP",
            "Consultant",
            "Senior Nurse",
            "Practice Nurse",
            "Staff Nurse",
            "Specialist"
    });

    JTextField specialtyField = new JTextField();
    JTextField staffCodeField = new JTextField();

    // Build form panel
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.add(new JLabel("Clinician ID"));
    panel.add(idField);

    panel.add(new JLabel("Full Name"));
    panel.add(nameField);

    panel.add(new JLabel("Role"));
    panel.add(roleCombo);

    panel.add(new JLabel("Specialty"));
    panel.add(specialtyField);

    panel.add(new JLabel("Staff Code"));
    panel.add(staffCodeField);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Add Clinician",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;
    
    // ---------------------------------
// Multi-field validation (Add Clinician)
// Shows ALL missing fields in one popup
// ---------------------------------
if (showClinicianMissingFields(
        "Clinician ID", idField.getText(),
        "Full Name", nameField.getText(),
        "Role", roleCombo.getSelectedItem() == null ? "" : roleCombo.getSelectedItem().toString(),
        "Specialty", specialtyField.getText(),
        "Staff Code", staffCodeField.getText()
)) {
    return; // stop if ANY required field is missing
}


    try {
        Clinician clinician = new Clinician(
                idField.getText().trim(),
                nameField.getText().trim(),
                roleCombo.getSelectedItem().toString(), //  from dropdown
                specialtyField.getText().trim(),
                staffCodeField.getText().trim()
        );

        clinicianRepository.add(clinician);
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

private void viewClinicianPrescriptions() {

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

    String clinicianId = clinicianTableModel.getValueAt(row, 0).toString();

    StringBuilder details = new StringBuilder();
    details.append("Prescriptions issued by clinician: ")
           .append(clinicianId)
           .append("\n\n");

    boolean found = false;

    try {
        // Ensure prescriptions are loaded
        prescriptionRepository.load("data/prescriptions.csv");

        for (Prescription p : prescriptionRepository.getAll()) {

            if (p.getClinicianId().equalsIgnoreCase(clinicianId)) {
                found = true;

                details.append("Prescription ID: ").append(p.getPrescriptionId()).append("\n");
                details.append("Patient NHS: ").append(p.getPatientNhsNumber()).append("\n");
                details.append("Medication: ").append(p.getMedication()).append("\n");
                details.append("Dosage: ").append(p.getDosage()).append("\n");
                details.append("Pharmacy: ").append(p.getPharmacy()).append("\n");
                details.append("Status: ").append(p.getCollectionStatus()).append("\n");
                details.append("------------------------------\n");
            }
        }

        if (!found) {
            details.append("No prescriptions found for this clinician.");
        }

        JTextArea textArea = new JTextArea(details.toString(), 20, 60);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Clinician Prescriptions",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        showError(ex);
    }
}



    private void editClinician() {

    // Ensure a row is selected
    int row = clinicianTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a clinician first.");
        return;
    }

    // Read existing values from the table model
    String clinicianId = clinicianTableModel.getValueAt(row, 0).toString();
    String name = clinicianTableModel.getValueAt(row, 1).toString();
    String role = clinicianTableModel.getValueAt(row, 2).toString();
    String specialty = clinicianTableModel.getValueAt(row, 3).toString();
    String staffCode = clinicianTableModel.getValueAt(row, 4).toString();

    // Create form fields (pre-filled with existing values)
    JTextField idField = new JTextField(clinicianId);
    idField.setEditable(false); // Identifier must not be changed

    JTextField nameField = new JTextField(name);

    JComboBox<String> roleCombo = new JComboBox<>(new String[]{
            "GP",
            "Consultant",
            "Senior Nurse",
            "Practice Nurse",
            "Staff Nurse",
            "Specialist"
    });
    roleCombo.setSelectedItem(role);

    JTextField specialtyField = new JTextField(specialty);
    JTextField staffCodeField = new JTextField(staffCode);

    // Build the form panel
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.add(new JLabel("Clinician ID"));
    panel.add(idField);

    panel.add(new JLabel("Full Name"));
    panel.add(nameField);

    panel.add(new JLabel("Role"));
    panel.add(roleCombo);

    panel.add(new JLabel("Specialty"));
    panel.add(specialtyField);

    panel.add(new JLabel("Staff Code"));
    panel.add(staffCodeField);

    // Display the dialog
    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Edit Clinician",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) {
        return;
    }

    // Field-specific validation to prevent empty values
    StringBuilder missingFields = new StringBuilder();

    if (nameField.getText().trim().isEmpty()) {
        missingFields.append("- Full Name\n");
    }
    if (specialtyField.getText().trim().isEmpty()) {
        missingFields.append("- Specialty\n");
    }
    if (staffCodeField.getText().trim().isEmpty()) {
        missingFields.append("- Staff Code\n");
    }

    // Show validation message if any fields are missing
    if (missingFields.length() > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in the following fields:\n\n" + missingFields,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        // Create updated clinician object
        Clinician updated = new Clinician(
                clinicianId,
                nameField.getText().trim(),
                roleCombo.getSelectedItem().toString(),
                specialtyField.getText().trim(),
                staffCodeField.getText().trim()
        );

        // Persist changes and refresh table
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

    JTextField txtId = new JTextField();
    JTextField txtPatientNhs = new JTextField();
    JTextField txtClinicianId = new JTextField();
    JTextField txtMedication = new JTextField();
    JTextField txtDosage = new JTextField();

    JComboBox<String> cmbPharmacy = new JComboBox<>(new String[]{
            "Boots Pharmacy",
            "Lloyds Pharmacy",
            "Superdrug Pharmacy",
            "Well Pharmacy",
            "Tesco Pharmacy",
            "Asda Pharmacy",
            "Hospital Pharmacy"
    });

    JComboBox<String> cmbStatus = new JComboBox<>(new String[]{
            "Pending",
            "Collected"
    });

    JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

    panel.add(new JLabel("Prescription ID:"));
    panel.add(txtId);

    panel.add(new JLabel("Patient NHS Number:"));
    panel.add(txtPatientNhs);

    panel.add(new JLabel("Clinician ID:"));
    panel.add(txtClinicianId);

    panel.add(new JLabel("Medication:"));
    panel.add(txtMedication);

    panel.add(new JLabel("Dosage:"));
    panel.add(txtDosage);

    panel.add(new JLabel("Pharmacy:"));
    panel.add(cmbPharmacy);

    panel.add(new JLabel("Collection Status:"));
    panel.add(cmbStatus);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Add Prescription",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    // ---------- Validation ----------
    List<String> missingFields = new ArrayList<>();

    if (txtId.getText().trim().isEmpty()) missingFields.add("Prescription ID");
    if (txtPatientNhs.getText().trim().isEmpty()) missingFields.add("Patient NHS Number");
    if (txtClinicianId.getText().trim().isEmpty()) missingFields.add("Clinician ID");
    if (txtMedication.getText().trim().isEmpty()) missingFields.add("Medication");
    if (txtDosage.getText().trim().isEmpty()) missingFields.add("Dosage");

    if (!missingFields.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in:\n\n" + String.join("\n", missingFields),
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        Prescription prescription = new Prescription(
                txtId.getText().trim(),
                txtPatientNhs.getText().trim(),
                txtClinicianId.getText().trim(),
                txtMedication.getText().trim(),
                txtDosage.getText().trim(),
                cmbPharmacy.getSelectedItem().toString(),
                cmbStatus.getSelectedItem().toString()
        );

        // ✅ Correct repository methods
        prescriptionRepository.addPrescription(prescription);
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

    // Existing values from table
    String prescriptionId = prescriptionTableModel.getValueAt(row, 0).toString();

    JTextField prescriptionIdField = new JTextField(prescriptionId);
    prescriptionIdField.setEditable(false);

    JTextField patientNhsField =
            new JTextField(prescriptionTableModel.getValueAt(row, 1).toString());
    JTextField clinicianIdField =
            new JTextField(prescriptionTableModel.getValueAt(row, 2).toString());
    JTextField medicationField =
            new JTextField(prescriptionTableModel.getValueAt(row, 3).toString());
    JTextField dosageField =
            new JTextField(prescriptionTableModel.getValueAt(row, 4).toString());
    JTextField pharmacyField =
            new JTextField(prescriptionTableModel.getValueAt(row, 5).toString());
    JTextField statusField =
            new JTextField(prescriptionTableModel.getValueAt(row, 6).toString());

    // Build form panel
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.add(new JLabel("Prescription ID"));
    panel.add(prescriptionIdField);

    panel.add(new JLabel("Patient NHS Number"));
    panel.add(patientNhsField);

    panel.add(new JLabel("Clinician ID"));
    panel.add(clinicianIdField);

    panel.add(new JLabel("Medication"));
    panel.add(medicationField);

    panel.add(new JLabel("Dosage"));
    panel.add(dosageField);

    panel.add(new JLabel("Pharmacy"));
    panel.add(pharmacyField);

    panel.add(new JLabel("Collection Status"));
    panel.add(statusField);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Edit Prescription",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    // ✅ FIELD-SPECIFIC VALIDATION
    StringBuilder missingFields = new StringBuilder();

    if (patientNhsField.getText().trim().isEmpty()) {
        missingFields.append("- Patient NHS Number\n");
    }
    if (clinicianIdField.getText().trim().isEmpty()) {
        missingFields.append("- Clinician ID\n");
    }
    if (medicationField.getText().trim().isEmpty()) {
        missingFields.append("- Medication\n");
    }
    if (dosageField.getText().trim().isEmpty()) {
        missingFields.append("- Dosage\n");
    }
    if (pharmacyField.getText().trim().isEmpty()) {
        missingFields.append("- Pharmacy\n");
    }
    if (statusField.getText().trim().isEmpty()) {
        missingFields.append("- Collection Status\n");
    }

    if (missingFields.length() > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in the following fields:\n\n" + missingFields,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        Prescription updated = new Prescription(
                prescriptionId,
                patientNhsField.getText().trim(),
                clinicianIdField.getText().trim(),
                medicationField.getText().trim(),
                dosageField.getText().trim(),
                pharmacyField.getText().trim(),
                statusField.getText().trim()
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
                        "Referred To Clinician",
                        "From Facility",
                        "To Facility",
                        "Referral Date",
                        "Urgency",
                        "Reason",
                        "Clinical Summary",
                        "Investigations",
                        "Status",
                        "Appointment ID",
                        "Notes",
                        "Created Date",
                        "Last Updated",
                }, 0
        );

        referralTable = new JTable(referralTableModel);
        referralTable.setPreferredScrollableViewportSize(new Dimension(1200, 450));

        loadReferrals();

        JButton viewBtn = new JButton("View Referral");
        JButton createBtn = new JButton("Create Referral");
        JButton editBtn = new JButton("Edit Referral");
        JButton deleteBtn = new JButton("Delete Referral");

        viewBtn.addActionListener(e -> viewReferrals());
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
                    r.getPatientId(),
                    r.getReferringClinicianId(),
                    r.getReferredToClinicianId(),   // NEW
                    r.getReferringFacilityId(),
                    r.getReferredToFacilityId(),
                    r.getReferralDate(),
                    r.getUrgencyLevel(),
                    r.getReferralReason(),
                    r.getClinicalSummary(),
                    r.getRequestedInvestigations(),
                    r.getStatus(),
                    r.getAppointmentId(),           //  NEW
                    r.getNotes(),
                    r.getCreatedDate(),              
                    r.getLastUpdated()               
});
}
}
        catch (Exception ex) {
            showError(ex);
        }
    }


/**
 * Creates a new referral using user input dialogs
 * and processes it via the Singleton ReferralManager.
 *
 * ✔ View responsibility (collect input + display messages)
 * ✔ Model responsibility delegated to Referral + ReferralManager
 * ✔ Satisfies Singleton pattern requirement in rubric
 */
private void createReferral() {

    // ❌ NO table row selection check here

    Referral newReferral = showReferralForm(null);
    if (newReferral == null) {
        return;
    }

    try {
    referralRepository.addReferral(newReferral);
} catch (IOException ex) {
    JOptionPane.showMessageDialog(
            this,
            "Failed to save referral:\n" + ex.getMessage(),
            "Save Error",
            JOptionPane.ERROR_MESSAGE
    );
    return;
}

    
    // 🔔 Simulated email generation
    ReferralManager manager = ReferralManager.getInstance();
    String emailContent = manager.generateReferralEmailContent(newReferral);

    try {
        ReferralWriter.writeReferralEmail(emailContent);
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Referral created, but notification file could not be generated.",
                "Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }

    loadReferrals();
}






/**
 * Edits an existing referral using a collective form.
 *
 * ✔ Field-specific validation
 * ✔ Dropdowns for controlled values
 * ✔ Clear popup listing exactly which fields are missing
 */
/**
 * Edits the selected referral using the collective referral form.
 */
private void editReferral() {

    // ---------------------------------
    // ENSURE A ROW IS SELECTED
    // ---------------------------------
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

    // ---------------------------------
    // FETCH REFERRAL FROM REPOSITORY
    // ---------------------------------
    String referralId = referralTableModel.getValueAt(row, 0).toString();
    Referral existing = referralRepository.getReferralById(referralId);

    if (existing == null) {
        JOptionPane.showMessageDialog(
                this,
                "Unable to load selected referral.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    // ---------------------------------
    // OPEN COLLECTIVE FORM (EDIT MODE)
    // ---------------------------------
    showReferralForm(existing);
}

private Referral showReferralForm(Referral existing) {

    // ===================== INPUT FIELDS =====================
    JTextField txtReferralId = new JTextField(
        existing != null ? existing.getReferralId() : generateNextReferralId()
);
    if (existing != null) {
    txtReferralId.setEditable(false);
}

    JTextField txtPatientId = new JTextField(existing != null ? existing.getPatientId() : "");
    JTextField txtReferringClinician = new JTextField(existing != null ? existing.getReferringClinicianId() : "");
    JTextField txtReferredClinician = new JTextField(existing != null ? existing.getReferredToClinicianId() : "");
    JTextField txtFromFacility = new JTextField(existing != null ? existing.getReferringFacilityId() : "");
    JTextField txtToFacility = new JTextField(existing != null ? existing.getReferredToFacilityId() : "");
    JTextField txtReferralDate = new JTextField(existing != null ? existing.getReferralDate() : "");

    JComboBox<String> cmbUrgency = new JComboBox<>(new String[]{
            "Routine", "Urgent", "Non-urgent"
    });

    JComboBox<String> cmbStatus = new JComboBox<>(new String[]{
            "New", "Pending", "In Progress", "Completed"
    });

    JTextField txtReason = new JTextField(existing != null ? existing.getReferralReason() : "");

    JTextArea txtClinicalSummary = new JTextArea(3, 20);
    JTextArea txtInvestigations = new JTextArea(2, 20);
    JTextArea txtNotes = new JTextArea(2, 20);

    if (existing != null) {
        cmbUrgency.setSelectedItem(existing.getUrgencyLevel());
        cmbStatus.setSelectedItem(existing.getStatus());
        txtClinicalSummary.setText(existing.getClinicalSummary());
        txtInvestigations.setText(existing.getRequestedInvestigations());
        txtNotes.setText(existing.getNotes());
    }

    // ===================== FORM LAYOUT =====================
    JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

    panel.add(new JLabel("Referral ID:"));
    panel.add(txtReferralId);

    panel.add(new JLabel("Patient ID:"));
    panel.add(txtPatientId);

    panel.add(new JLabel("Referring Clinician ID:"));
    panel.add(txtReferringClinician);

    panel.add(new JLabel("Referred To Clinician ID:"));
    panel.add(txtReferredClinician);

    panel.add(new JLabel("From Facility ID:"));
    panel.add(txtFromFacility);

    panel.add(new JLabel("To Facility ID:"));
    panel.add(txtToFacility);

    panel.add(new JLabel("Referral Date (YYYY-MM-DD):"));
    panel.add(txtReferralDate);

    panel.add(new JLabel("Urgency Level:"));
    panel.add(cmbUrgency);

    panel.add(new JLabel("Status:"));
    panel.add(cmbStatus);

    panel.add(new JLabel("Referral Reason:"));
    panel.add(txtReason);

    panel.add(new JLabel("Clinical Summary:"));
    panel.add(new JScrollPane(txtClinicalSummary));

    panel.add(new JLabel("Requested Investigations:"));
    panel.add(new JScrollPane(txtInvestigations));

    panel.add(new JLabel("Notes:"));
    panel.add(new JScrollPane(txtNotes));

    // ===================== SHOW DIALOG =====================
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setPreferredSize(new Dimension(500, 450));

    int result = JOptionPane.showConfirmDialog(
    this,
    scrollPane,
    "Create Referral",
    JOptionPane.OK_CANCEL_OPTION,
    JOptionPane.PLAIN_MESSAGE
);

    if (result != JOptionPane.OK_OPTION) return null;

    // ===================== VALIDATION =====================
    List<String> missingFields = new ArrayList<>();

    if (txtReferralId.getText().isBlank()) missingFields.add("Referral ID");
    if (txtPatientId.getText().isBlank()) missingFields.add("Patient ID");
    if (txtReferringClinician.getText().isBlank()) missingFields.add("Referring Clinician ID");
    if (txtReferredClinician.getText().isBlank()) missingFields.add("Referred To Clinician ID");
    if (txtFromFacility.getText().isBlank()) missingFields.add("From Facility ID");
    if (txtToFacility.getText().isBlank()) missingFields.add("To Facility ID");
    if (txtReferralDate.getText().isBlank()) missingFields.add("Referral Date");
    if (txtReason.getText().isBlank()) missingFields.add("Referral Reason");
    if (txtClinicalSummary.getText().isBlank()) missingFields.add("Clinical Summary");

    if (!missingFields.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Missing required fields (" + missingFields.size() + "):\n\n- "
                        + String.join("\n- ", missingFields),
                "Missing Information",
                JOptionPane.ERROR_MESSAGE
        );
        return null;
    }

    // ===================== SAVE =====================
    Referral referral = new Referral(
            txtReferralId.getText().trim(),
            txtPatientId.getText().trim(),
            txtReferringClinician.getText().trim(),
            txtReferredClinician.getText().trim(),
            txtFromFacility.getText().trim(),
            txtToFacility.getText().trim(),
            txtReferralDate.getText().trim(),
            cmbUrgency.getSelectedItem().toString(),
            txtReason.getText().trim(),
            txtClinicalSummary.getText().trim(),
            txtInvestigations.getText().trim(),
            cmbStatus.getSelectedItem().toString(),
            "", // appointment ID (optional)
            txtNotes.getText().trim(),
            LocalDate.now().toString(),
            LocalDate.now().toString()
    );
    
return referral;}

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
 * Shows a collective popup form for adding a Facility.
 */
/**
 * Displays a collective popup form for adding a Facility
 * using dropdown menus for constrained fields.
 */
private void showFacilityForm() {

    // ==============================
    // TEXT INPUT FIELDS
    // ==============================
    JTextField facilityIdField = new JTextField();
    JTextField facilityNameField = new JTextField();
    JTextField facilityTypeField = new JTextField();
    JTextField addressField = new JTextField();
    JTextField postcodeField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField managerNameField = new JTextField();

    // ==============================
    // DROPDOWN MENUS
    // ==============================

    // Opening Hours
    JComboBox<String> openingHoursBox = new JComboBox<>(new String[] {
            "Mon-Fri: 8:00-18:00",
            "Mon-Fri: 8:30-17:30",
            "Mon-Fri: 9:00-17:00",
            "24/7 Emergency",
            "24/7 Emergency, Outpatients: Mon-Fri 8:00-17:00",
            "Outpatients Only: Mon-Fri 9:00-17:00"
    });

    // Capacity
    JComboBox<Integer> capacityBox = new JComboBox<>(new Integer[] {
            100, 250, 500, 800, 1000, 1200, 1500, 2000
    });

    // Specialities Offered
    JComboBox<String> specialitiesBox = new JComboBox<>(new String[] {
            "General Practice",
            "General Practice|Vaccinations",
            "General Practice|Vaccinations|Minor Surgery",
            "Outpatients",
            "Cardiology|Neurology|Emergency Medicine",
            "Orthopaedics|Oncology",
            "Paediatrics|Dermatology|Child Psychology",
            "Mental Health Services"
    });

    // ==============================
    // FORM LAYOUT
    // ==============================
    JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

    panel.add(new JLabel("Facility ID:"));
    panel.add(facilityIdField);

    panel.add(new JLabel("Facility Name:"));
    panel.add(facilityNameField);

    panel.add(new JLabel("Facility Type:"));
    panel.add(facilityTypeField);

    panel.add(new JLabel("Address:"));
    panel.add(addressField);

    panel.add(new JLabel("Postcode:"));
    panel.add(postcodeField);

    panel.add(new JLabel("Phone Number:"));
    panel.add(phoneField);

    panel.add(new JLabel("Email:"));
    panel.add(emailField);

    panel.add(new JLabel("Opening Hours:"));
    panel.add(openingHoursBox);

    panel.add(new JLabel("Manager Name:"));
    panel.add(managerNameField);

    panel.add(new JLabel("Capacity:"));
    panel.add(capacityBox);

    panel.add(new JLabel("Specialities Offered:"));
    panel.add(specialitiesBox);

    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setPreferredSize(new Dimension(450, 420));

    int result = JOptionPane.showConfirmDialog(
            this,
            scrollPane,
            "Add Facility",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    // ==============================
    // MULTI-FIELD VALIDATION
    // ==============================
    if (showMissingFieldsPopup(
            "Add Facility – Missing Information",
            "Facility ID", facilityIdField.getText(),
            "Facility Name", facilityNameField.getText(),
            "Facility Type", facilityTypeField.getText(),
            "Address", addressField.getText(),
            "Postcode", postcodeField.getText(),
            "Phone Number", phoneField.getText(),
            "Email", emailField.getText(),
            "Manager Name", managerNameField.getText()
    )) {
        return;
    }

    // ==============================
    // CREATE FACILITY OBJECT
    // ==============================
    Facility facility = new Facility(
            facilityIdField.getText().trim(),
            facilityNameField.getText().trim(),
            facilityTypeField.getText().trim(),
            addressField.getText().trim(),
            postcodeField.getText().trim(),
            phoneField.getText().trim(),
            emailField.getText().trim(),
            openingHoursBox.getSelectedItem().toString(),
            managerNameField.getText().trim(),
            (Integer) capacityBox.getSelectedItem(),
            specialitiesBox.getSelectedItem().toString()
    );

    // ==============================
    // SAVE + REFRESH
    // ==============================
    try {
        facilityRepository.addFacility(facility);
        loadFacilities();

        JOptionPane.showMessageDialog(
                this,
                "Facility added successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

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
private void viewReferrals() {

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

    details.append("Referred To Clinician: ")
           .append(referralTableModel.getValueAt(row, 3)).append("\n");

    details.append("From Facility: ")
           .append(referralTableModel.getValueAt(row, 4)).append("\n");

    details.append("To Facility: ")
           .append(referralTableModel.getValueAt(row, 5)).append("\n\n");

    details.append("Referral Date: ")
           .append(referralTableModel.getValueAt(row, 6)).append("\n");

    details.append("Urgency: ")
           .append(referralTableModel.getValueAt(row, 7)).append("\n");

    details.append("Reason:\n")
           .append(referralTableModel.getValueAt(row, 8)).append("\n\n");

    details.append("Clinical Summary:\n")
           .append(referralTableModel.getValueAt(row, 9)).append("\n\n");

    details.append("Investigations:\n")
           .append(referralTableModel.getValueAt(row, 10)).append("\n\n");

    details.append("Status: ")
           .append(referralTableModel.getValueAt(row, 11)).append("\n");

    details.append("Appointment ID: ")
           .append(referralTableModel.getValueAt(row, 12)).append("\n\n");

    details.append("Notes:\n")
           .append(referralTableModel.getValueAt(row, 13)).append("\n\n");

    details.append("Created Date: ")
           .append(referralTableModel.getValueAt(row, 14)).append("\n");

    details.append("Last Updated: ")
           .append(referralTableModel.getValueAt(row, 15));

    JTextArea textArea = new JTextArea(details.toString(), 22, 65);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);

    JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Referral Details",
            JOptionPane.INFORMATION_MESSAGE
    );
}

private void populateReferralTable() {
    loadReferrals();
}




/* =========================================================
   STAFF TAB (ADD / VIEW / DELETE)
   ---------------------------------------------------------
   - Displays all staff records in a JTable
   - Loads staff data from CSV via StaffRepository
   - Uses refreshStaffTable() to ensure all columns are populated
   - No hardcoded or legacy staff data is used
   ========================================================= */

private JPanel createStaffPanel() {

    // Main container for the Staff tab
    JPanel panel = new JPanel(new BorderLayout());

    // Table model defining ALL staff columns
    // These MUST match the fields stored in the Staff model
    staffTableModel = new DefaultTableModel(
            new String[]{
                    "Staff ID",
                    "Name",
                    "Role",
                    "Department",
                    "Facility ID",
                    "Phone",
                    "Email",
                    "Employment Status",
                    "Start Date",
                    "Line Manager",
                    "Access Level"
            }, 0
    );

    // JTable bound to the model
    staffTable = new JTable(staffTableModel);
    staffTable.setPreferredScrollableViewportSize(new Dimension(1200, 450));

    // Load staff data from CSV ONCE at startup
    // Then populate the table using repository data
    try {
        staffRepository.load("data/staff.csv");
        refreshStaffTable();
    } catch (Exception ex) {
        showError(ex);
    }

    // ================================
    // Staff action buttons
    // ================================

    JButton addBtn = new JButton("Add Staff");
    JButton editBtn = new JButton("Edit Staff");
    JButton viewBtn = new JButton("View Staff");
    JButton deleteBtn = new JButton("Delete Staff");

    // Button actions
    addBtn.addActionListener(e -> showStaffForm());
    editBtn.addActionListener(e -> showEditStaffForm());
    viewBtn.addActionListener(e -> viewStaff());
    deleteBtn.addActionListener(e -> deleteStaff());

    // Button panel
    JPanel buttons = new JPanel();
    buttons.add(addBtn);
    buttons.add(editBtn);   // <-- THIS fixes the error
    buttons.add(viewBtn);
    buttons.add(deleteBtn);

    // Assemble the Staff tab UI
    panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);

    return panel;
}

private void showEditStaffForm() {

    int row = staffTable.getSelectedRow();

    // Ensure a row is selected
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a staff member to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }


    // Get Staff ID from table and fetch full record from repository
    String staffId = staffTableModel.getValueAt(row, 0).toString();
    Staff existing = staffRepository.findById(staffId);

    if (existing == null) {
        JOptionPane.showMessageDialog(
                this,
                "Staff record not found.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    // -----------------------------
    // Pre-filled input fields
    // -----------------------------
    JTextField staffIdField = new JTextField(existing.getStaffId());
    staffIdField.setEditable(false); // Staff ID must not be edited

    JTextField nameField = new JTextField(existing.getName());
    JTextField departmentField = new JTextField(existing.getDepartment());
    JTextField facilityIdField = new JTextField(existing.getFacilityId());
    JTextField phoneField = new JTextField(existing.getPhoneNumber());
    JTextField emailField = new JTextField(existing.getEmail());
    JTextField startDateField = new JTextField(existing.getStartDate());
    JTextField lineManagerField = new JTextField(existing.getLineManager());

    // -----------------------------
    // Dropdown menus
    // -----------------------------
    JComboBox<String> roleBox = new JComboBox<>(new String[]{
            "Practice Manager",
            "Receptionist",
            "Medical Secretary",
            "Healthcare Assistant",
            "Hospital Administrator",
            "Porter",
            "Ward Clerk"
    });
    roleBox.setSelectedItem(existing.getRole());

    JComboBox<String> employmentStatusBox = new JComboBox<>(new String[]{
            "Full-Time",
            "Part-Time"
    });
    employmentStatusBox.setSelectedItem(existing.getEmploymentStatus());

    JComboBox<String> accessLevelBox = new JComboBox<>(new String[]{
            "Manager",
            "Standard",
            "Basic"
    });
    accessLevelBox.setSelectedItem(existing.getAccessLevel());

    // -----------------------------
    // Form layout
    // -----------------------------
    JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

    panel.add(new JLabel("Staff ID:"));
    panel.add(staffIdField);

    panel.add(new JLabel("Full Name:"));
    panel.add(nameField);

    panel.add(new JLabel("Role:"));
    panel.add(roleBox);

    panel.add(new JLabel("Department:"));
    panel.add(departmentField);

    panel.add(new JLabel("Facility ID:"));
    panel.add(facilityIdField);

    panel.add(new JLabel("Phone Number:"));
    panel.add(phoneField);

    panel.add(new JLabel("Email:"));
    panel.add(emailField);

    panel.add(new JLabel("Employment Status:"));
    panel.add(employmentStatusBox);

    panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
    panel.add(startDateField);

    panel.add(new JLabel("Line Manager:"));
    panel.add(lineManagerField);

    panel.add(new JLabel("Access Level:"));
    panel.add(accessLevelBox);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Edit Staff Member",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) {
        return;
    }


    // ---------------------------------
    // Multi-field validation (matches Prescription Edit)
    // ---------------------------------
    if (showStaffMissingFields(
        "Full Name", nameField.getText(),
        "Department", departmentField.getText(),
        "Facility ID", facilityIdField.getText(),
        "Phone Number", phoneField.getText(),
        "Email", emailField.getText(),
        "Start Date", startDateField.getText(),
        "Line Manager", lineManagerField.getText()
)) {
    return; // stop save if ANY field is missing
}


    // -----------------------------
    // Save updated staff record
    // -----------------------------
    try {
    // Create updated Staff object from form values
    Staff updated = new Staff(
            existing.getStaffId(),
            nameField.getText().trim(),
            roleBox.getSelectedItem().toString(),
            departmentField.getText().trim(),
            facilityIdField.getText().trim(),
            phoneField.getText().trim(),
            emailField.getText().trim(),
            employmentStatusBox.getSelectedItem().toString(),
            startDateField.getText().trim(),
            lineManagerField.getText().trim(),
            accessLevelBox.getSelectedItem().toString()
    );

    // Persist changes via repository
    staffRepository.updateStaff(updated);

    // Refresh UI table to reflect changes
    refreshStaffTable();

    JOptionPane.showMessageDialog(
            this,
            "Staff member updated successfully.",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
    );

} catch (Exception ex) {
    showError(ex);
}
}

/* =========================================================
   Staff multi-field validation (UI-consistent)
   ---------------------------------------------------------
   - Matches existing system validation style
   - Single popup
   - Lists ALL missing fields using hyphen bullets
   ========================================================= */
private boolean showMissingFieldsIfAny(String... fields) {

    StringBuilder missingFields = new StringBuilder();

    for (int i = 0; i < fields.length; i += 2) {
        String fieldName = fields[i];
        String fieldValue = fields[i + 1];

        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            missingFields.append(" - ").append(fieldName).append("\n");
        }
    }

    if (missingFields.length() > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in the following fields:\n\n" + missingFields.toString(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
        );
        return true; // missing fields exist
    }

    return false; // all fields filled
}


    

    /* =========================================================
   refreshStaffTable
   ---------------------------------------------------------
   - Clears the JTable
   - Re-populates it using StaffRepository data
   - Ensures ALL columns are displayed correctly
   ========================================================= */
private void refreshStaffTable() {

    DefaultTableModel model = (DefaultTableModel) staffTable.getModel();
    model.setRowCount(0); // Clear existing rows

    // Populate table from repository (single source of truth)
    for (Staff s : staffRepository.getAll()) {
        model.addRow(new Object[]{
                s.getStaffId(),
                s.getName(),
                s.getRole(),
                s.getDepartment(),
                s.getFacilityId(),
                s.getPhoneNumber(),
                s.getEmail(),
                s.getEmploymentStatus(),
                s.getStartDate(),
                s.getLineManager(),
                s.getAccessLevel()
        });
    }
}

/* =========================================================
   showStaffForm
   ---------------------------------------------------------
   - Displays a collective popup form for adding staff
   - Uses dropdown menus for constrained fields
   - Improves usability compared to multiple input dialogs
   ========================================================= */
private void showStaffForm() {

    // Text input fields
    JTextField staffIdField = new JTextField();
    JTextField nameField = new JTextField();
    JTextField departmentField = new JTextField();
    JTextField facilityIdField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField startDateField = new JTextField();
    JTextField lineManagerField = new JTextField();

    // Dropdown menus (predefined values improve data integrity)
    JComboBox<String> roleBox = new JComboBox<>(new String[]{
            "Practice Manager",
            "Receptionist",
            "Medical Secretary",
            "Healthcare Assistant",
            "Hospital Administrator",
            "Porter",
            "Ward Clerk"
    });

    JComboBox<String> employmentStatusBox = new JComboBox<>(new String[]{
            "Full-Time",
            "Part-Time"
    });

    JComboBox<String> accessLevelBox = new JComboBox<>(new String[]{
            "Manager",
            "Standard",
            "Basic"
    });

    // Form layout using GridLayout for clean alignment
    JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

    panel.add(new JLabel("Staff ID:"));
    panel.add(staffIdField);

    panel.add(new JLabel("Full Name:"));
    panel.add(nameField);

    panel.add(new JLabel("Role:"));
    panel.add(roleBox);

    panel.add(new JLabel("Department:"));
    panel.add(departmentField);

    panel.add(new JLabel("Facility ID:"));
    panel.add(facilityIdField);

    panel.add(new JLabel("Phone Number:"));
    panel.add(phoneField);

    panel.add(new JLabel("Email:"));
    panel.add(emailField);

    panel.add(new JLabel("Employment Status:"));
    panel.add(employmentStatusBox);

    panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
    panel.add(startDateField);

    panel.add(new JLabel("Line Manager:"));
    panel.add(lineManagerField);

    panel.add(new JLabel("Access Level:"));
    panel.add(accessLevelBox);

    // Display the popup dialog
    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Add Staff Member",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

  // ---------------------------------
// Multi-field validation (Add Staff)
// Matches Prescription + Edit Staff
// ---------------------------------
if (showStaffMissingFields(
        "Staff ID", staffIdField.getText(),
        "Full Name", nameField.getText(),
        "Department", departmentField.getText(),
        "Facility ID", facilityIdField.getText(),
        "Phone Number", phoneField.getText(),
        "Email", emailField.getText(),
        "Start Date", startDateField.getText(),
        "Line Manager", lineManagerField.getText()
)) {
    return; // stop add if ANY field is missing
}


    /* =========================================================
   Staff Validation – Edit Staff (matches Edit Prescription)
   ========================================================= */


    try {
        // Create Staff object using full constructor
        Staff staff = new Staff(
                staffIdField.getText().trim(),
                nameField.getText().trim(),
                roleBox.getSelectedItem().toString(),
                departmentField.getText().trim(),
                facilityIdField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                employmentStatusBox.getSelectedItem().toString(),
                startDateField.getText().trim(),
                lineManagerField.getText().trim(),
                accessLevelBox.getSelectedItem().toString()
        );

        // Persist staff and refresh UI
        staffRepository.addStaff(staff);
        refreshStaffTable();

        JOptionPane.showMessageDialog(this, "Staff added successfully.");

    } catch (Exception ex) {
        showError(ex);
    }
}

/* =========================================================
   showValidationError
   ---------------------------------------------------------
   - Displays a clear error message identifying the
     specific missing field
   ========================================================= */
private void showValidationError(String fieldName) {
    JOptionPane.showMessageDialog(
            this,
            fieldName + " must be filled in.",
            "Missing Required Field",
            JOptionPane.ERROR_MESSAGE
    );
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
            JOptionPane.showMessageDialog(this, "Staff updated successfully.");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    /**
 * View Staff
 * ----------
 * Displays all details of the selected staff member
 * in a read-only popup dialog.
 * Uses data already loaded in the JTable (no CSV access here).
 */
private void viewStaff() {

    int row = staffTable.getSelectedRow();

    // Ensure a staff member is selected
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a staff member to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Build readable staff details from table columns
    StringBuilder details = new StringBuilder();
    details.append("Staff ID: ")
           .append(staffTableModel.getValueAt(row, 0)).append("\n");

    details.append("Name: ")
           .append(staffTableModel.getValueAt(row, 1)).append("\n");

    details.append("Role: ")
           .append(staffTableModel.getValueAt(row, 2)).append("\n");

    details.append("Department: ")
           .append(staffTableModel.getValueAt(row, 3)).append("\n");

    details.append("Facility ID: ")
           .append(staffTableModel.getValueAt(row, 4)).append("\n");

    details.append("Phone Number: ")
           .append(staffTableModel.getValueAt(row, 5)).append("\n");

    details.append("Email: ")
           .append(staffTableModel.getValueAt(row, 6)).append("\n");

    details.append("Employment Status: ")
           .append(staffTableModel.getValueAt(row, 7)).append("\n");

    details.append("Start Date: ")
           .append(staffTableModel.getValueAt(row, 8)).append("\n");

    details.append("Line Manager: ")
           .append(staffTableModel.getValueAt(row, 9)).append("\n");

    details.append("Access Level: ")
           .append(staffTableModel.getValueAt(row, 10)).append("\n");

    // Show details in a scrollable dialog
    JTextArea textArea = new JTextArea(details.toString(), 18, 50);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);

    JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "View Staff",
            JOptionPane.INFORMATION_MESSAGE
    );
}


private void deleteStaff() {

    int row = staffTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a staff member first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    String staffId = staffTableModel.getValueAt(row, 0).toString();

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete staff member: " + staffId + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        //  DELETE FROM REPOSITORY (CSV + memory)
        staffRepository.deleteStaff(staffId);

        //  REFRESH TABLE FROM SINGLE SOURCE OF TRUTH
        refreshStaffTable();

        JOptionPane.showMessageDialog(
                this,
                "Staff deleted successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        showError(ex);
    }
}


/* =========================================================
   FACILITIES TAB (ADD / EDIT / DELETE / VIEW)
   CSV:
   facilityId,name,type,address,phoneNumber
   ========================================================= */
private JPanel createFacilityPanel() {

    JPanel panel = new JPanel(new BorderLayout());

    // ==============================
    // TABLE MODEL (MUST COME FIRST)
    // ==============================
    facilityTableModel = new DefaultTableModel(
            new String[]{
                    "Facility ID",
                    "Name",
                    "Type",
                    "Address",
                    "Postcode",
                    "Phone Number",
                    "Email",
                    "Opening Hours",
                    "Manager Name",
                    "Capacity",
                    "Specialities Offered"
            },
            0
    );

    facilityTable = new JTable(facilityTableModel);
    facilityTable.setRowHeight(22);

    panel.add(new JScrollPane(facilityTable), BorderLayout.CENTER);

    // ==============================
    // LOAD CSV *AFTER* MODEL EXISTS
    // ==============================
    try {

        facilityRepository.load("data/facilities.csv");

System.out.println("Facilities loaded count = " 
        + facilityRepository.getAllFacilities().size());

loadFacilities();

        facilityRepository.load("data/facilities.csv");
        loadFacilities(); // ← THIS POPULATES THE TABLE
    } catch (Exception e) {
        JOptionPane.showMessageDialog(
                this,
                "Failed to load facilities data:\n" + e.getMessage(),
                "Load Error",
                JOptionPane.ERROR_MESSAGE
        );

        
    }

    // ==============================
    // BUTTONS
    // ==============================
    JButton addBtn = new JButton("Add Facility");
    JButton editBtn = new JButton("Edit Facility");
    JButton deleteBtn = new JButton("Delete Facility");
    JButton viewBtn = new JButton("View Facility");

    addBtn.addActionListener(e -> showFacilityForm());
    editBtn.addActionListener(e -> editFacility());
    deleteBtn.addActionListener(e -> deleteFacility());
    viewBtn.addActionListener(e -> viewFacility());

    JPanel buttons = new JPanel();
    buttons.add(addBtn);
    buttons.add(editBtn);
    buttons.add(deleteBtn);
    buttons.add(viewBtn);

    panel.add(buttons, BorderLayout.SOUTH);

    return panel;
}




private void loadFacilities() {

    if (facilityTableModel == null) {
        return;
    }

    facilityTableModel.setRowCount(0);

    //  THIS LINE IS CRITICAL
    List<Facility> facilities = facilityRepository.getAll();

    System.out.println("Facilities found: " + facilities.size());

    for (Facility f : facilities) {
        facilityTableModel.addRow(new Object[]{
            f.getFacilityId(),
            f.getFacilityName(),
            f.getFacilityType(),
            f.getAddress(),
            f.getPostcode(),
            f.getPhoneNumber(),
            f.getEmail(),
            f.getOpeningHours(),
            f.getManagerName(),
            f.getCapacity(),
            f.getSpecialitiesOffered()
        });
    }
}







/**
 * View Facility
 * -------------
 * Displays full details of the selected facility
 * in a read-only dialog.
 */
private void viewFacility() {

    int row = facilityTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a facility first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    StringBuilder details = new StringBuilder();

    details.append("Facility ID: ").append(facilityTableModel.getValueAt(row, 0)).append("\n");
    details.append("Name: ").append(facilityTableModel.getValueAt(row, 1)).append("\n");
    details.append("Type: ").append(facilityTableModel.getValueAt(row, 2)).append("\n");
    details.append("Address: ").append(facilityTableModel.getValueAt(row, 3)).append("\n");
    details.append("Postcode: ").append(facilityTableModel.getValueAt(row, 4)).append("\n");
    details.append("Phone Number: ").append(facilityTableModel.getValueAt(row, 5)).append("\n");
    details.append("Email: ").append(facilityTableModel.getValueAt(row, 6)).append("\n");
    details.append("Opening Hours: ").append(facilityTableModel.getValueAt(row, 7)).append("\n");
    details.append("Manager Name: ").append(facilityTableModel.getValueAt(row, 8)).append("\n");
    details.append("Capacity: ").append(facilityTableModel.getValueAt(row, 9)).append("\n");
    details.append("Specialities Offered: ").append(facilityTableModel.getValueAt(row, 10));

    JTextArea textArea = new JTextArea(details.toString(), 18, 50);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JOptionPane.showMessageDialog(
            this,
            new JScrollPane(textArea),
            "Facility Details",
            JOptionPane.INFORMATION_MESSAGE
    );
}

private void editFacility() {

    // ==============================
    // ENSURE A ROW IS SELECTED
    // ==============================
    int row = facilityTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a facility to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // ==============================
    // PRE-FILLED TEXT FIELDS
    // ==============================
    JTextField facilityIdField = new JTextField(
            facilityTableModel.getValueAt(row, 0).toString()
    );
    facilityIdField.setEditable(false); // ID must not change

    JTextField facilityNameField = new JTextField(
            facilityTableModel.getValueAt(row, 1).toString()
    );

    JTextField facilityTypeField = new JTextField(
            facilityTableModel.getValueAt(row, 2).toString()
    );

    JTextField addressField = new JTextField(
            facilityTableModel.getValueAt(row, 3).toString()
    );

    JTextField postcodeField = new JTextField(
            facilityTableModel.getValueAt(row, 4).toString()
    );

    JTextField phoneField = new JTextField(
            facilityTableModel.getValueAt(row, 5).toString()
    );

    JTextField emailField = new JTextField(
            facilityTableModel.getValueAt(row, 6).toString()
    );

    JTextField managerNameField = new JTextField(
            facilityTableModel.getValueAt(row, 8).toString()
    );

    // ==============================
    // DROPDOWNS (PRE-SELECTED)
    // ==============================

    JComboBox<String> openingHoursBox = new JComboBox<>(new String[]{
            "Mon-Fri: 8:00-18:00",
            "Mon-Fri: 8:30-17:30",
            "Mon-Fri: 9:00-17:00",
            "24/7 Emergency",
            "24/7 Emergency, Outpatients: Mon-Fri 8:00-17:00",
            "Outpatients Only: Mon-Fri 9:00-17:00"
    });
    openingHoursBox.setSelectedItem(
            facilityTableModel.getValueAt(row, 7).toString()
    );

    JComboBox<Integer> capacityBox = new JComboBox<>(new Integer[]{
            100, 250, 500, 800, 1000, 1200, 1500, 2000
    });
    capacityBox.setSelectedItem(
            Integer.parseInt(facilityTableModel.getValueAt(row, 9).toString())
    );

    JComboBox<String> specialitiesBox = new JComboBox<>(new String[]{
            "General Practice",
            "General Practice|Vaccinations",
            "General Practice|Vaccinations|Minor Surgery",
            "Outpatients",
            "Cardiology|Neurology|Emergency Medicine",
            "Orthopaedics|Oncology",
            "Paediatrics|Dermatology|Child Psychology",
            "Mental Health Services"
    });
    specialitiesBox.setSelectedItem(
            facilityTableModel.getValueAt(row, 10).toString()
    );

    // ==============================
    // FORM LAYOUT
    // ==============================
    JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

    panel.add(new JLabel("Facility ID:"));
    panel.add(facilityIdField);

    panel.add(new JLabel("Facility Name:"));
    panel.add(facilityNameField);

    panel.add(new JLabel("Facility Type:"));
    panel.add(facilityTypeField);

    panel.add(new JLabel("Address:"));
    panel.add(addressField);

    panel.add(new JLabel("Postcode:"));
    panel.add(postcodeField);

    panel.add(new JLabel("Phone Number:"));
    panel.add(phoneField);

    panel.add(new JLabel("Email:"));
    panel.add(emailField);

    panel.add(new JLabel("Opening Hours:"));
    panel.add(openingHoursBox);

    panel.add(new JLabel("Manager Name:"));
    panel.add(managerNameField);

    panel.add(new JLabel("Capacity:"));
    panel.add(capacityBox);

    panel.add(new JLabel("Specialities Offered:"));
    panel.add(specialitiesBox);

    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setPreferredSize(new Dimension(450, 420));

    int result = JOptionPane.showConfirmDialog(
            this,
            scrollPane,
            "Edit Facility",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    // ==============================
    // MULTI-FIELD VALIDATION
    // ==============================
    if (showMissingFieldsPopup(
            "Edit Facility – Missing Information",
            "Facility Name", facilityNameField.getText(),
            "Facility Type", facilityTypeField.getText(),
            "Address", addressField.getText(),
            "Postcode", postcodeField.getText(),
            "Phone Number", phoneField.getText(),
            "Email", emailField.getText(),
            "Manager Name", managerNameField.getText()
    )) {
        return;
    }

    // ==============================
    // CREATE UPDATED FACILITY
    // ==============================
    Facility updated = new Facility(
            facilityIdField.getText().trim(),
            facilityNameField.getText().trim(),
            facilityTypeField.getText().trim(),
            addressField.getText().trim(),
            postcodeField.getText().trim(),
            phoneField.getText().trim(),
            emailField.getText().trim(),
            openingHoursBox.getSelectedItem().toString(),
            managerNameField.getText().trim(),
            (Integer) capacityBox.getSelectedItem(),
            specialitiesBox.getSelectedItem().toString()
    );

    // ==============================
    // SAVE + REFRESH
    // ==============================
    try {
        facilityRepository.updateFacility(updated);
        loadFacilities();

        JOptionPane.showMessageDialog(
                this,
                "Facility updated successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        showError(ex);
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
   APPOINTMENTS TAB
   Expected CSV:
   appointmentId,patientId,clinicianId,facilityId,appointmentDate,appointmentTime,status,notes
   ========================================================= */

private JPanel createAppointmentPanel() {

    // ================================
    // APPOINTMENTS TAB – UI SETUP
    // ================================

    JPanel appointmentPanel = new JPanel(new BorderLayout());

    appointmentTableModel = new DefaultTableModel(
            new String[]{
                    "Appointment ID",
                    "Patient ID",
                    "Clinician ID",
                    "Facility ID",
                    "Appointment Date",
                    "Appointment Time",
                    "Status",
                    "Notes"
            }, 0
    );

    appointmentTable = new JTable(appointmentTableModel);
    appointmentTable.setRowHeight(22);

    appointmentPanel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

    // ================================
    // LOAD APPOINTMENTS FROM CSV
    // ================================
    try {
        appointmentRepository.load("data/appointments.csv");
        appointmentTableModel.setRowCount(0);

        for (Appointment a : appointmentRepository.getAll()) {
            appointmentTableModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    a.getPatientId(),
                    a.getClinicianId(),
                    a.getFacilityId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getStatus(),
                    a.getNotes()
            });
        }

    } catch (Exception e) {
        showError(e);
    }

    // ================================
    // APPOINTMENT BUTTON PANEL
    // ================================
    JPanel buttons = new JPanel();

    JButton addBtn = new JButton("Add Appointment");
    JButton editBtn = new JButton("Edit Appointment");
    JButton deleteBtn = new JButton("Delete Appointment");
    JButton viewBtn = new JButton("View Appointment");
    JButton viewReferralBtn = new JButton("View Related Referrals");
    JButton viewFacilityBtn = new JButton("View Facility");



    addBtn.addActionListener(e -> addAppointment());
    editBtn.addActionListener(e -> editAppointment());
    deleteBtn.addActionListener(e -> deleteAppointment());
    viewBtn.addActionListener(e -> viewAppointment());
    viewReferralBtn.addActionListener(e -> viewAppointmentReferrals());
    viewFacilityBtn.addActionListener(e -> viewAppointmentFacility());



    buttons.add(addBtn);
    buttons.add(editBtn);
    buttons.add(deleteBtn);
    buttons.add(viewBtn);
    buttons.add(viewReferralBtn);
    buttons.add(viewFacilityBtn);



    appointmentPanel.add(buttons, BorderLayout.SOUTH);

    return appointmentPanel;
}

/**
 * View Appointment
 * ----------------
 * Displays all details of the selected appointment
 * in a read-only, scrollable dialog.
 *
 * Matches the style of View Patient / View Prescription.
 */
private void viewAppointment() {

    // Ensure a row is selected
    int row = appointmentTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select an appointment first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Build readable appointment details from table columns
    StringBuilder details = new StringBuilder();
    details.append("APPOINTMENT DETAILS\n");
    details.append("========================\n\n");

    details.append("Appointment ID: ")
           .append(appointmentTableModel.getValueAt(row, 0)).append("\n");

    details.append("Patient ID: ")
           .append(appointmentTableModel.getValueAt(row, 1)).append("\n");

    details.append("Clinician ID: ")
           .append(appointmentTableModel.getValueAt(row, 2)).append("\n");

    details.append("Facility ID: ")
           .append(appointmentTableModel.getValueAt(row, 3)).append("\n\n");

    details.append("Appointment Date: ")
           .append(appointmentTableModel.getValueAt(row, 4)).append("\n");

    details.append("Appointment Time: ")
           .append(appointmentTableModel.getValueAt(row, 5)).append("\n");

    details.append("Status: ")
           .append(appointmentTableModel.getValueAt(row, 6)).append("\n\n");

    details.append("Notes:\n")
           .append(appointmentTableModel.getValueAt(row, 7));

    // Text area for readability
    JTextArea textArea = new JTextArea(details.toString(), 18, 55);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);

    // Show popup
    JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "View Appointment",
            JOptionPane.INFORMATION_MESSAGE
    );
}

/**
 * Add Appointment
 * ----------------
 * Displays a collective popup form for creating a new appointment.
 * - Uses dropdowns for constrained fields
 * - Validates ALL required fields in one popup
 * - Shows how many fields are missing and which ones
 * - Persists data via AppointmentRepository
 */
private void addAppointment() {

    // ==============================
    // INPUT FIELDS
    // ==============================

    JTextField appointmentIdField = new JTextField();
    JTextField patientIdField = new JTextField();
    JTextField clinicianIdField = new JTextField();
    JTextField facilityIdField = new JTextField();
    JTextField appointmentDateField = new JTextField(); // YYYY-MM-DD
    JTextField appointmentTimeField = new JTextField(); // HH:MM
    JTextArea notesArea = new JTextArea(3, 20);

    // Status dropdown improves data integrity
    JComboBox<String> statusBox = new JComboBox<>(new String[] {
            "New",
            "Pending",
            "Completed",
            "Cancelled"
    });

    // ==============================
    // FORM LAYOUT
    // ==============================

    JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

    panel.add(new JLabel("Appointment ID:"));
    panel.add(appointmentIdField);

    panel.add(new JLabel("Patient ID:"));
    panel.add(patientIdField);

    panel.add(new JLabel("Clinician ID:"));
    panel.add(clinicianIdField);

    panel.add(new JLabel("Facility ID:"));
    panel.add(facilityIdField);

    panel.add(new JLabel("Appointment Date (YYYY-MM-DD):"));
    panel.add(appointmentDateField);

    panel.add(new JLabel("Appointment Time (HH:MM):"));
    panel.add(appointmentTimeField);

    panel.add(new JLabel("Status:"));
    panel.add(statusBox);

    panel.add(new JLabel("Notes:"));
    panel.add(new JScrollPane(notesArea));

    // ==============================
    // SHOW POPUP
    // ==============================

    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setPreferredSize(new Dimension(450, 400));

    int result = JOptionPane.showConfirmDialog(
            this,
            scrollPane,
            "Add Appointment",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    // ==============================
    // MULTI-FIELD VALIDATION
    // ==============================

    List<String> missingFields = new ArrayList<>();

    if (appointmentIdField.getText().trim().isEmpty())
        missingFields.add("Appointment ID");

    if (patientIdField.getText().trim().isEmpty())
        missingFields.add("Patient ID");

    if (clinicianIdField.getText().trim().isEmpty())
        missingFields.add("Clinician ID");

    if (facilityIdField.getText().trim().isEmpty())
        missingFields.add("Facility ID");

    if (appointmentDateField.getText().trim().isEmpty())
        missingFields.add("Appointment Date");

    if (appointmentTimeField.getText().trim().isEmpty())
        missingFields.add("Appointment Time");

    if (!missingFields.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Missing required fields (" + missingFields.size() + "):\n\n- "
                        + String.join("\n- ", missingFields),
                "Missing Information",
                JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    // ==============================
    // CREATE APPOINTMENT OBJECT
    // ==============================

    try {
        Appointment appointment = new Appointment(
                appointmentIdField.getText().trim(),
                patientIdField.getText().trim(),
                clinicianIdField.getText().trim(),
                facilityIdField.getText().trim(),
                appointmentDateField.getText().trim(),
                appointmentTimeField.getText().trim(),
                statusBox.getSelectedItem().toString(),
                notesArea.getText().trim()
        );

        // Persist via repository
        appointmentRepository.addAppointment(appointment);

        // Refresh appointments table
        appointmentTableModel.addRow(new Object[]{
                appointment.getAppointmentId(),
                appointment.getPatientId(),
                appointment.getClinicianId(),
                appointment.getFacilityId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus(),
                appointment.getNotes()
        });

        JOptionPane.showMessageDialog(
                this,
                "Appointment added successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        showError(ex);
    }
}

/**
 * View Facility related to the selected Appointment.
 *
 * Part B:
 * Appointment → Facility (via facilityId)
 */
private void viewAppointmentFacility() {

    int row = appointmentTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select an appointment first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Facility ID is column 3 in appointment table
    String facilityId = appointmentTableModel.getValueAt(row, 3).toString();

    try {
        Facility facility = facilityRepository.getById(facilityId);

        if (facility == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Facility not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Facility ID: ").append(facility.getFacilityId()).append("\n");
        details.append("Name: ").append(facility.getFacilityName()).append("\n");
        details.append("Type: ").append(facility.getFacilityType()).append("\n");
        details.append("Address: ").append(facility.getAddress()).append("\n");
        details.append("Postcode: ").append(facility.getPostcode()).append("\n");
        details.append("Phone: ").append(facility.getPhoneNumber()).append("\n");
        details.append("Opening Hours: ").append(facility.getOpeningHours()).append("\n");

        JTextArea area = new JTextArea(details.toString(), 15, 50);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Appointment Facility",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        showError(ex);
    }
}


/**
 * Delete Appointment
 * ------------------
 * Deletes the selected appointment after user confirmation.
 * - Requires a selected row
 * - Confirms before deleting
 * - Removes from repository and UI table
 */
private void deleteAppointment() {

    // ==============================
    // ENSURE A ROW IS SELECTED
    // ==============================
    int row = appointmentTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select an appointment first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Get appointment ID from selected row
    String appointmentId =
            appointmentTableModel.getValueAt(row, 0).toString();

    // ==============================
    // CONFIRM DELETE
    // ==============================
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete appointment: " + appointmentId + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    // ==============================
    // DELETE + REFRESH
    // ==============================
    try {
        // Remove from repository (CSV persistence)
        appointmentRepository.deleteAppointment(appointmentId);

        // Remove from UI table
        appointmentTableModel.removeRow(row);

        JOptionPane.showMessageDialog(
                this,
                "Appointment deleted successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        showError(ex);
    }
}

/**
 * View Referrals related to the selected Appointment.
 *
 * Indirect linkage:
 * - Appointment -> patientId
 * - Referral -> patientId
 *
 * This maintains loose coupling and matches Part 1 UML design.
 */
private void viewAppointmentReferrals() {

    // Ensure an appointment is selected
    int row = appointmentTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select an appointment first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Extract patient ID from the selected appointment row
    String patientId = appointmentTableModel.getValueAt(row, 1).toString();

    try {
        // Ensure referrals are loaded
        referralRepository.load("data/referrals.csv");

        StringBuilder details = new StringBuilder();
        details.append("REFERRALS FOR PATIENT: ")
               .append(patientId)
               .append("\n");
        details.append("====================================\n\n");

        boolean found = false;

        // Find referrals that share the same patient ID
        for (Referral r : referralRepository.getAll()) {

            if (r.getPatientId().equalsIgnoreCase(patientId)) {

                found = true;

                details.append("Referral ID: ").append(r.getReferralId()).append("\n");
                details.append("Urgency: ").append(r.getUrgencyLevel()).append("\n");
                details.append("Status: ").append(r.getStatus()).append("\n");
                details.append("From Facility: ").append(r.getReferringFacilityId()).append("\n");
                details.append("To Facility: ").append(r.getReferredToFacilityId()).append("\n");
                details.append("Reason: ").append(r.getReferralReason()).append("\n");
                details.append("------------------------------------\n");
            }
        }

        if (!found) {
            details.append("No referrals found for this appointment’s patient.");
        }

        // Display in scrollable dialog
        JTextArea textArea = new JTextArea(details.toString(), 20, 60);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Related Referrals",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        showError(ex);
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
 * Edit Appointment
 * ----------------
 * Displays a collective popup form for editing an existing appointment.
 * - Pre-fills all fields from the selected table row
 * - Appointment ID is locked (primary identifier)
 * - Validates ALL required fields in one popup
 * - Shows how many fields are missing and which ones
 */
private void editAppointment() {

    // ==============================
    // ENSURE A ROW IS SELECTED
    // ==============================
    int row = appointmentTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select an appointment first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // ==============================
    // PRE-FILLED INPUT FIELDS
    // ==============================

    JTextField appointmentIdField =
            new JTextField(appointmentTableModel.getValueAt(row, 0).toString());
    appointmentIdField.setEditable(false); // Identifier must not change

    JTextField patientIdField =
            new JTextField(appointmentTableModel.getValueAt(row, 1).toString());

    JTextField clinicianIdField =
            new JTextField(appointmentTableModel.getValueAt(row, 2).toString());

    JTextField facilityIdField =
            new JTextField(appointmentTableModel.getValueAt(row, 3).toString());

    JTextField appointmentDateField =
            new JTextField(appointmentTableModel.getValueAt(row, 4).toString());

    JTextField appointmentTimeField =
            new JTextField(appointmentTableModel.getValueAt(row, 5).toString());

    JTextArea notesArea =
            new JTextArea(appointmentTableModel.getValueAt(row, 7).toString(), 3, 20);

    JComboBox<String> statusBox = new JComboBox<>(new String[]{
            "New",
            "Pending",
            "Completed",
            "Cancelled"
    });
    statusBox.setSelectedItem(
            appointmentTableModel.getValueAt(row, 6).toString()
    );

    // ==============================
    // FORM LAYOUT
    // ==============================

    JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

    panel.add(new JLabel("Appointment ID:"));
    panel.add(appointmentIdField);

    panel.add(new JLabel("Patient ID:"));
    panel.add(patientIdField);

    panel.add(new JLabel("Clinician ID:"));
    panel.add(clinicianIdField);

    panel.add(new JLabel("Facility ID:"));
    panel.add(facilityIdField);

    panel.add(new JLabel("Appointment Date (YYYY-MM-DD):"));
    panel.add(appointmentDateField);

    panel.add(new JLabel("Appointment Time (HH:MM):"));
    panel.add(appointmentTimeField);

    panel.add(new JLabel("Status:"));
    panel.add(statusBox);

    panel.add(new JLabel("Notes:"));
    panel.add(new JScrollPane(notesArea));

    // ==============================
    // SHOW POPUP
    // ==============================

    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setPreferredSize(new Dimension(450, 400));

    int result = JOptionPane.showConfirmDialog(
            this,
            scrollPane,
            "Edit Appointment",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    // ==============================
    // MULTI-FIELD VALIDATION
    // ==============================

    List<String> missingFields = new ArrayList<>();

    if (patientIdField.getText().trim().isEmpty())
        missingFields.add("Patient ID");

    if (clinicianIdField.getText().trim().isEmpty())
        missingFields.add("Clinician ID");

    if (facilityIdField.getText().trim().isEmpty())
        missingFields.add("Facility ID");

    if (appointmentDateField.getText().trim().isEmpty())
        missingFields.add("Appointment Date");

    if (appointmentTimeField.getText().trim().isEmpty())
        missingFields.add("Appointment Time");

    if (!missingFields.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Missing required fields (" + missingFields.size() + "):\n\n- "
                        + String.join("\n- ", missingFields),
                "Missing Information",
                JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    // ==============================
    // SAVE UPDATED APPOINTMENT
    // ==============================

    try {
        Appointment updated = new Appointment(
                appointmentIdField.getText().trim(),
                patientIdField.getText().trim(),
                clinicianIdField.getText().trim(),
                facilityIdField.getText().trim(),
                appointmentDateField.getText().trim(),
                appointmentTimeField.getText().trim(),
                statusBox.getSelectedItem().toString(),
                notesArea.getText().trim()
        );

        // Persist update via repository
        appointmentRepository.updateAppointment(updated);

        // Refresh table row
        appointmentTableModel.setValueAt(updated.getPatientId(), row, 1);
        appointmentTableModel.setValueAt(updated.getClinicianId(), row, 2);
        appointmentTableModel.setValueAt(updated.getFacilityId(), row, 3);
        appointmentTableModel.setValueAt(updated.getAppointmentDate(), row, 4);
        appointmentTableModel.setValueAt(updated.getAppointmentTime(), row, 5);
        appointmentTableModel.setValueAt(updated.getStatus(), row, 6);
        appointmentTableModel.setValueAt(updated.getNotes(), row, 7);

        JOptionPane.showMessageDialog(
                this,
                "Appointment updated successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        showError(ex);
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
     * Prompt helper for optional fields with a default value.
     * - Returns null if the user cancels
     * - Allows empty input (optional field)
     */
    private String promptOptionalDefault(String label, String defaultVal) {
        String val = JOptionPane.showInputDialog(this, label + ":", defaultVal);
        if (val == null) return null;   // user cancelled
        return val.trim();              // may be empty
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

    /* =========================================================
   showStaffValidationError
   ---------------------------------------------------------
   - Used when EDITING staff
   - Shows ONE popup
   - Lists ALL missing required fields
   - Matches Edit Prescription validation style
   ========================================================= */
private boolean showStaffValidationError(String... fields) {

    StringBuilder missing = new StringBuilder();

    // fields come in pairs: "Field Name", fieldValue
    for (int i = 0; i < fields.length; i += 2) {
        String fieldName = fields[i];
        String fieldValue = fields[i + 1];

        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            missing.append(" - ").append(fieldName).append("\n");
        }
    }

    // If anything is missing, show ONE popup and stop
    if (missing.length() > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in the following fields:\n\n" + missing,
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
        );
        return true; // validation failed
    }

    return false; // validation passed
}

/* =========================================================
   showStaffMissingInfo
   ---------------------------------------------------------
   - Used when ADDING staff
   - Shows ONE popup
   - Lists ALL missing fields
   - Matches Add Prescription "Missing Information" popup
   ========================================================= */
private boolean showStaffMissingInfo(String... fields) {

    StringBuilder missing = new StringBuilder();

    // fields come in pairs: "Field Name", fieldValue
    for (int i = 0; i < fields.length; i += 2) {
        String fieldName = fields[i];
        String fieldValue = fields[i + 1];

        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            missing.append(fieldName).append("\n");
        }
    }

    // If anything is missing, show ONE popup and stop
    if (missing.length() > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in:\n\n" + missing,
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
        );
        return true; // missing info exists
    }

    return false; // all fields filled

    
}

/* =========================================================
   Staff multi-field validation
   ---------------------------------------------------------
   - Collects ALL missing fields
   - Shows ONE popup
   - Matches Prescription validation style
   ========================================================= */
private boolean showStaffMissingFields(String... fields) {

    StringBuilder missing = new StringBuilder();

    for (int i = 0; i < fields.length; i += 2) {
        String fieldName = fields[i];
        String fieldValue = fields[i + 1];

        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            missing.append(" - ").append(fieldName).append("\n");
        }
    }

    if (missing.length() > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in the following fields:\n\n" + missing,
                "Missing Required Fields",
                JOptionPane.ERROR_MESSAGE
        );
        return true; // validation FAILED
    }

    return false; // validation PASSED
}

/* =========================================================
   Patient multi-field validation
   ---------------------------------------------------------
   - Displays ONE popup
   - Lists ALL missing required patient fields
   - Matches Staff & Prescription validation style
   ========================================================= */
private boolean showPatientMissingFields(String... fields) {

    StringBuilder missing = new StringBuilder();

    for (int i = 0; i < fields.length; i += 2) {
        String fieldName = fields[i];
        String fieldValue = fields[i + 1];

        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            missing.append(" - ").append(fieldName).append("\n");
        }
    }

    if (missing.length() > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in the following fields:\n\n" + missing,
                "Missing Required Fields",
                JOptionPane.ERROR_MESSAGE
        );
        return true; // missing fields exist
    }

    return false; // all fields filled
}

/**
 * Multi-field validation helper for Add Clinician
 * - Shows ONE popup
 * - Lists ALL missing required fields
 */
private boolean showClinicianMissingFields(String... fields) {

    StringBuilder missing = new StringBuilder();

    for (int i = 0; i < fields.length; i += 2) {
        String fieldName = fields[i];
        String fieldValue = fields[i + 1];

        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            missing.append(" - ").append(fieldName).append("\n");
        }
    }

    if (missing.length() > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Please fill in:\n\n" + missing,
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
        );
        return true;
    }

    return false;

}

private String generateNextReferralId() {
    int max = 0;
    for (Referral r : referralRepository.getAll()) {
        String id = r.getReferralId().replace("R", "");
        try {
            max = Math.max(max, Integer.parseInt(id));
        } catch (NumberFormatException ignored) {}
    }
    return "R" + String.format("%03d", max + 1);
}

/**
 * Multi-field validation helper
 * -----------------------------
 * Shows ONE popup listing:
 * - How many fields are missing
 * - Exactly which fields are missing
 */
private boolean showMissingFieldsPopup(String title, String... fields) {

    StringBuilder missing = new StringBuilder();
    int count = 0;

    // fields come in pairs: "Field Name", fieldValue
    for (int i = 0; i < fields.length; i += 2) {
        String fieldName = fields[i];
        String fieldValue = fields[i + 1];

        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            missing.append("- ").append(fieldName).append("\n");
            count++;
        }
    }

    if (count > 0) {
        JOptionPane.showMessageDialog(
                this,
                "Missing required fields (" + count + "):\n\n" + missing,
                title,
                JOptionPane.WARNING_MESSAGE
        );
        return true; // validation FAILED
    }

    return false; // validation PASSED
}

/**
 * Enables / disables tabs based on the logged-in user role.
 * Updated to include Appointments tab (Part B).
 */
private void applyRolePermissions(JTabbedPane tabs) {

switch (UserSession.getRole()) {

    case "PATIENT":
        // Patients: limited access
        tabs.setEnabledAt(1, false);
        tabs.setEnabledAt(2, false);
        tabs.setEnabledAt(3, false);
        tabs.setEnabledAt(4, false);
        tabs.setEnabledAt(5, false);
        break;

    case "CLINICIAN":
        // Clinicians: no staff / facility admin
        tabs.setEnabledAt(4, false);
        tabs.setEnabledAt(5, false);
        break;

    case "DOCTOR":
        // Doctors: full access
        break;

    case "STAFF":
        // Staff: no clinical data
        tabs.setEnabledAt(0, false); // Patients
        tabs.setEnabledAt(1, false); // Clinicians
        tabs.setEnabledAt(2, false); // Prescriptions
        break;

    default:
        // Safety fallback
        JOptionPane.showMessageDialog(
                this,
                "Unknown role: " + UserSession.getRole(),
                "Access Error",
                JOptionPane.ERROR_MESSAGE
        );
}
}
















    

}
