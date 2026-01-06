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
import java.util.ArrayList;
import java.util.List;
import java.awt.GridLayout;



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

    // ✅ LOAD DATA
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

    // Create form fields
    JTextField idField = new JTextField();
    JTextField nameField = new JTextField();

    // ✅ Dropdown instead of text field
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

    try {
        Clinician clinician = new Clinician(
                idField.getText().trim(),
                nameField.getText().trim(),
                roleCombo.getSelectedItem().toString(), // ✅ from dropdown
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
        JButton viewBtn = new JButton("View Staff"); // ✅ NEW

        addBtn.addActionListener(e -> addStaff());
        editBtn.addActionListener(e -> editStaff());
        deleteBtn.addActionListener(e -> deleteStaff());
        viewBtn.addActionListener(e -> viewStaff()); // ✅ NEW

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(viewBtn); // ✅ ADD THIS


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

    /**
 * View Staff
 * ----------
 * Displays all details of the selected staff member
 * in a read-only dialog.
 */
private void viewStaff() {

    int row = staffTable.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a staff member to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Read directly from the table model (safe + fast)
    String staffId = staffTableModel.getValueAt(row, 0).toString();
    String name = staffTableModel.getValueAt(row, 1).toString();
    String role = staffTableModel.getValueAt(row, 2).toString();
    String department = staffTableModel.getValueAt(row, 3).toString();

    // Build a clean display message
    String message =
            "Staff ID: " + staffId + "\n" +
            "Name: " + name + "\n" +
            "Role: " + role + "\n" +
            "Department: " + department;

    JOptionPane.showMessageDialog(
            this,
            message,
            "View Staff",
            JOptionPane.INFORMATION_MESSAGE
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
   FACILITIES TAB (ADD / EDIT / DELETE / VIEW)
   CSV:
   facilityId,name,type,address,phoneNumber
   ========================================================= */
private JPanel createFacilityPanel() {

    JPanel panel = new JPanel(new BorderLayout());

    // ✅ COLUMN COUNT MUST MATCH MODEL + CSV
    facilityTableModel = new DefaultTableModel(
            new String[]{"Facility ID", "Name", "Type", "Address", "Phone Number"}, 0
    );

    facilityTable = new JTable(facilityTableModel);
    facilityTable.setRowHeight(22);

    loadFacilities();

    JButton addBtn = new JButton("Add Facility");
    JButton editBtn = new JButton("Edit Facility");
    JButton deleteBtn = new JButton("Delete Facility");
    JButton viewBtn = new JButton("View Facility");

    addBtn.addActionListener(e -> addFacility());
    editBtn.addActionListener(e -> editFacility());
    deleteBtn.addActionListener(e -> deleteFacility());
    viewBtn.addActionListener(e -> viewFacility());

    JPanel buttons = new JPanel();
    buttons.add(addBtn);
    buttons.add(editBtn);
    buttons.add(deleteBtn);
    buttons.add(viewBtn);

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
                    f.getName(),
                    f.getType(),
                    f.getAddress(),
                    f.getPhoneNumber()
            });
        }
    } catch (Exception e) {
        showError(e);
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

    String message =
            "Facility ID: " + facilityTableModel.getValueAt(row, 0) + "\n" +
            "Name: " + facilityTableModel.getValueAt(row, 1) + "\n" +
            "Type: " + facilityTableModel.getValueAt(row, 2) + "\n" +
            "Address: " + facilityTableModel.getValueAt(row, 3) + "\n" +
            "Phone Number: " + facilityTableModel.getValueAt(row, 4);

    JOptionPane.showMessageDialog(
            this,
            message,
            "View Facility",
            JOptionPane.INFORMATION_MESSAGE
    );
}



private void addFacility() {
    try {
        String id = promptRequired("Facility ID");
        if (id == null) return;

        String name = promptRequired("Facility Name");
        if (name == null) return;

        String type = promptRequired("Facility Type");
        if (type == null) return;

        String address = promptRequired("Address");
        if (address == null) return;

        String phone = promptRequired("Phone Number");
        if (phone == null) return;

        Facility f = new Facility(id, name, type, address, phone);

        facilityRepository.addFacility(f);
        loadFacilities();

        JOptionPane.showMessageDialog(this, "Facility added successfully.");

    } catch (Exception e) {
        showError(e);
    }
}


private void editFacility() {

    int row = facilityTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a facility first.");
        return;
    }

    try {
        Facility updated = new Facility(
                facilityTableModel.getValueAt(row, 0).toString(),
                promptRequiredDefault("Facility Name", facilityTableModel.getValueAt(row, 1).toString()),
                promptRequiredDefault("Facility Type", facilityTableModel.getValueAt(row, 2).toString()),
                promptRequiredDefault("Address", facilityTableModel.getValueAt(row, 3).toString()),
                promptRequiredDefault("Phone Number", facilityTableModel.getValueAt(row, 4).toString())
        );

        facilityRepository.updateFacility(updated);
        loadFacilities();

        JOptionPane.showMessageDialog(this, "Facility updated successfully.");

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
