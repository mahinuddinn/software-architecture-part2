package view;

import model.Patient;
import repository.PatientRepository;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * MainFrame
 * ---------
 * Main GUI window for the Healthcare Referral System.
 *
 * Displays patient data in a JTable using MVC principles.
 */
public class MainFrame extends JFrame {

    private JTable patientTable;
    private DefaultTableModel tableModel;

    public MainFrame() {

        setTitle("Healthcare Referral System");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initialisePatientTable();
        loadPatientData();
    }

    /**
     * Creates the patient table structure.
     */
    private void initialisePatientTable() {

        String[] columnNames = {
                "NHS Number",
                "First Name",
                "Last Name",
                "Date of Birth",
                "Phone",
                "GP Surgery"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        patientTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        add(scrollPane);
    }

    /**
     * Loads patient data from the repository and displays it.
     */
    private void loadPatientData() {

        try {
            PatientRepository repo = new PatientRepository();
            repo.load("data/patients.csv");

            List<Patient> patients = repo.getAll();

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
}
