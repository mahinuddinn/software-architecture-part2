package repository;

import model.Patient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PatientRepository
 * -----------------
 * Responsible for loading, storing, searching, modifying, and persisting
 * Patient records.
 *
 * This class represents the Repository (data access) layer of the MVC
 * architecture. It isolates all CSV file handling logic from controllers
 * and the GUI, ensuring a clean separation of concerns.
 */
public class PatientRepository {

    /**
     * In-memory list of all patients.
     * Used for iteration, display in GUI tables, and writing back to CSV.
     */
    private final List<Patient> patients = new ArrayList<>();

    /**
     * Fast lookup structure keyed by NHS number.
     * Prevents duplicates and enables O(1) search.
     */
    private final Map<String, Patient> patientByNhs = new HashMap<>();

    /**
     * Stores the original CSV file path so that changes
     * can be persisted back to the same file.
     */
    private String sourceFilePath;

    /**
     * Loads patient records from a CSV file into memory.
     *
     * @param filePath path to patients.csv (e.g. "data/patients.csv")
     * @throws IOException if file cannot be read
     */
    public void load(String filePath) throws IOException {
        this.sourceFilePath = filePath;

        patients.clear();
        patientByNhs.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Read and ignore the header row
            String header = br.readLine();
            if (header == null) {
                return; // Empty file
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = CsvUtil.splitCsvLine(line);

                /*
                 * Column order must match patients.csv header:
                 * 0 = NHS number
                 * 1 = First name
                 * 2 = Last name
                 * 3 = Date of birth
                 * 4 = Phone number
                 * 5 = Registered GP surgery
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
     * Returns a copy of all loaded patients.
     * A copy is returned to protect internal data structures.
     */
    public List<Patient> getAll() {
        return new ArrayList<>(patients);
    }

    /**
     * Finds a patient by NHS number.
     *
     * @param nhsNumber NHS number to search for
     * @return Patient if found, otherwise null
     */
    public Patient findByNhs(String nhsNumber) {
        return patientByNhs.get(nhsNumber);
    }

    /**
     * Adds a new patient to the repository and persists the change to CSV.
     *
     * @param patient Patient object to add
     * @throws IOException if writing to file fails
     */
    public void addPatient(Patient patient) throws IOException {

        if (patient == null || patient.getNhsNumber() == null || patient.getNhsNumber().isBlank()) {
            throw new IllegalArgumentException("Invalid patient: NHS number is required.");
        }

        if (patientByNhs.containsKey(patient.getNhsNumber())) {
            throw new IllegalArgumentException(
                    "Patient already exists with NHS: " + patient.getNhsNumber()
            );
        }

        patients.add(patient);
        patientByNhs.put(patient.getNhsNumber(), patient);

        saveToCsv();
    }

    /**
     * Deletes a patient using their NHS number and persists the change.
     *
     * @param nhsNumber NHS number of the patient to delete
     * @throws IOException if writing to file fails
     */
    public void deletePatient(String nhsNumber) throws IOException {

        if (nhsNumber == null || nhsNumber.isBlank()) return;

        Patient removed = patientByNhs.remove(nhsNumber);
        if (removed == null) return; // Nothing to delete

        patients.remove(removed);

        saveToCsv();
    }

    /**
     * Updates the phone number of an existing patient
     * and persists the change to CSV.
     *
     * @param nhsNumber NHS number of the patient
     * @param newPhoneNumber updated phone number
     * @throws IOException if writing to file fails
     */
    public void updatePatientPhone(String nhsNumber, String newPhoneNumber) throws IOException {

        Patient existing = patientByNhs.get(nhsNumber);
        if (existing == null) {
            throw new IllegalArgumentException("Patient not found with NHS: " + nhsNumber);
        }

        existing.setPhoneNumber(newPhoneNumber);
        saveToCsv();
    }

    /**
     * Writes all in-memory patient records back to the original CSV file.
     * This ensures that create, update, and delete operations persist.
     *
     * @throws IOException if file cannot be written
     */
    private void saveToCsv() throws IOException {

        if (sourceFilePath == null || sourceFilePath.isEmpty()) {
            throw new IllegalStateException("Source CSV file path not set. Call load() first.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            // CSV header must match original patients.csv format
            writer.write("nhsNumber,firstName,lastName,dateOfBirth,phoneNumber,registeredGpSurgery");
            writer.newLine();

            for (Patient p : patients) {
                writer.write(String.join(",",
                        safe(p.getNhsNumber()),
                        safe(p.getFirstName()),
                        safe(p.getLastName()),
                        safe(p.getDateOfBirth()),
                        safe(p.getPhoneNumber()),
                        safe(p.getRegisteredGpSurgery())
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Sanitises values before writing to CSV to prevent
     * commas from corrupting column structure.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
