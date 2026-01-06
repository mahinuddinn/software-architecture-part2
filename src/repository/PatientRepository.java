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
     * Expected CSV format:
     * nhsNumber,firstName,lastName,dateOfBirth,phoneNumber,gender,registeredGpSurgery
     *
     * @param filePath path to patients.csv (e.g. "data/patients.csv")
     * @throws IOException if file cannot be read
     */
    public void load(String filePath) throws IOException {

        this.sourceFilePath = filePath; // ✅ FIX: required for saveToCsv()

        patients.clear();
        patientByNhs.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String header = br.readLine(); // skip header
            if (header == null) return;

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] cols = line.split(",", -1);

                // Must match CSV structure exactly
                if (cols.length < 7) continue;

        Patient p = new Patient(
                cols[0].trim(),  // NHS
                cols[1].trim(),  // First
                cols[2].trim(),  // Last
                cols[3].trim(),  // DOB
                cols[4].trim(),  // Phone
                cols[5].trim(),  // Emergency Contact
                cols[6].trim(),  // Gender
                cols[7].trim(),  // Address
                cols[8].trim(),  // Postcode
                cols[9].trim(),  // Email
                cols[10].trim()  // GP Surgery
);

                patients.add(p);
                patientByNhs.put(p.getNhsNumber(), p); // ✅ FIX: keep map in sync
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
     * Updates an existing patient based on NHS number.
     *
     * @param updatedPatient the patient with updated details
     */
    public void updatePatient(Patient updatedPatient) throws IOException {

        boolean found = false;

        for (int i = 0; i < patients.size(); i++) {
            Patient existing = patients.get(i);

            if (existing.getNhsNumber()
                    .equalsIgnoreCase(updatedPatient.getNhsNumber())) {

                patients.set(i, updatedPatient);
                patientByNhs.put(updatedPatient.getNhsNumber(), updatedPatient);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Patient not found.");
        }

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
        if (removed == null) return;

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
     *
     * @throws IOException if file cannot be written
     */
    private void saveToCsv() throws IOException {

        if (sourceFilePath == null || sourceFilePath.isEmpty()) {
            throw new IllegalStateException("Source CSV file path not set. Call load() first.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            // ✅ FIXED header to match load()
            writer.write("nhsNumber,firstName,lastName,dateOfBirth,phoneNumber,"+"emergencyContactNumber,gender,address,postcode,email,registeredGpSurgery"
);

            writer.newLine();

            for (Patient p : patients) {
               writer.write(String.join(",",
                    safe(p.getNhsNumber()),
                    safe(p.getFirstName()),
                    safe(p.getLastName()),
                    safe(p.getDateOfBirth()),
                    safe(p.getPhoneNumber()),
                    safe(p.getEmergencyContactNumber()),
                    safe(p.getGender()),
                    safe(p.getAddress()),
                    safe(p.getPostcode()),
                    safe(p.getEmail()),
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
