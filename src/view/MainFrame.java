package view;

import model.Patient;
import repository.PatientRepository;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * MainFrame
 * ---------
 * Main GUI window for the Healthcare Referral System.
 *
 * This class represents the View layer in the MVC architecture.
 * It is responsible only for displaying data and capturing user actions.
 * No business logic or file I/O should be handled here.
 */
public class MainFrame extends JFrame {

    /** Table used to display patient records */
    private JTable patientTable;

    /** Table model backing the patient table */
    private DefaultTableModel tableModel;

    /**
     * Constructs the main application window.
     */
    public MainFrame() {

        setTitle("Healthcare Referral System");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use BorderLayout to position table and buttons
        setLayout(new BorderLayout());

        initialisePatientTable();
        loadPatientData();

        // Add components to the frame
        add(new JScrollPane(patientTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * Initialises the patient JTable and its columns.
     */
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

    /**
     * Loads patient data from the repository and displays it in the table.
     */
    private void loadPatientData() {

        try {
            PatientRepository repository = new PatientRepository();
            repository.load("data/patients.csv");

            List<Patient> patients = repository.getAll();

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
            e.printStackTrace();
        }
    }

    /**
     * Creates a panel containing action buttons.
     * At this stage, buttons are present but have no logic attached.
     */
    private JPanel createButtonPanel() {

        JButton addButton = new JButton("Add Patient");
        JButton deleteButton = new JButton("Delete Patient");

        JPanel panel = new JPanel();
        panel.add(addButton);
        panel.add(deleteButton);

        return panel;
    }
}
