package repository;

import model.Patient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PatientRepository
 * -----------------
 * Loads, stores, and provides access to Patient records.
 *
 * This class is part of the "data access" layer (Model side of MVC).
 * It reads patient data from data/patients.csv and stores it in memory
 * so that controllers and the GUI can use it.
 */
public class PatientRepository {

    /** In-memory storage for iteration (e.g., display in JTable) */
    private final List<Patient> patients = new ArrayList<>();

    /** Fast lookup by NHS number (prevents duplicates, quick search) */
    private final Map<String, Patient> patientByNhs = new HashMap<>();

    /** Stores the original file path so we can save back later */
    private String sourceFilePath;

    /**
     * Loads patient records from a CSV file.
     * @param filePath path to patients.csv (e.g., "data/patients.csv")
     */
    public void load(String filePath) throws IOException {
        this.sourceFilePath = filePath;

        patients.clear();
        patientByNhs.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Read and ignore the header row
            String header = br.readLine();
            if (header == null) {
                return; // empty file
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = CsvUtil.splitCsvLine(line);

                /**
                 * IMPORTANT:
                 * These column indexes must match your patients.csv header order.
                 * For now we assume:
                 * 0 nhsNumber, 1 firstName, 2 lastName, 3 dateOfBirth, 4 phoneNumber, 5 registeredGpSurgery
                 */
                String nhs = CsvUtil.get(cols, 0);
                String first = CsvUtil.get(cols, 1);
                String last = CsvUtil.get(cols, 2);
                String dob = CsvUtil.get(cols, 3);
                String phone = CsvUtil.get(cols, 4);
                String gp = CsvUtil.get(cols, 5);

                // Skip invalid rows
                if (nhs.isEmpty()) continue;

                Patient p = new Patient(nhs, first, last, dob, phone, gp);

                patients.add(p);
                patientByNhs.put(nhs, p);
            }
        }
    }

    /**
     * Returns all patients (copy to protect internal list).
     */
    public List<Patient> getAll() {
        return new ArrayList<>(patients);
    }

    /**
     * Finds a patient using their NHS number.
     */
    public Patient findByNhs(String nhsNumber) {
        return patientByNhs.get(nhsNumber);
    }
}
