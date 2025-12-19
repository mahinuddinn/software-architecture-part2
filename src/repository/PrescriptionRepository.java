package repository;

import model.Prescription;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PrescriptionRepository
 * ----------------------
 * Handles loading, storing, creating, and persisting prescriptions.
 *
 * This repository:
 *  - Loads prescriptions from prescriptions.csv
 *  - Allows new prescriptions to be added
 *  - Saves prescriptions back to CSV
 *  - Generates a prescription text file (output requirement)
 *
 * Part of the Model layer in MVC.
 */
public class PrescriptionRepository {

    /** In-memory list of prescriptions */
    private final List<Prescription> prescriptions = new ArrayList<>();

    /** Original CSV file path (used for persistence) */
    private String sourceFilePath;

    /**
     * Loads prescriptions from a CSV file.
     *
     * @param filePath path to prescriptions.csv
     */
    public void load(String filePath) throws IOException {
        this.sourceFilePath = filePath;
        prescriptions.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Skip header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");

                /*
                 * Expected CSV format:
                 * 0 prescriptionId
                 * 1 patientNhsNumber
                 * 2 clinicianId
                 * 3 medication
                 * 4 dosage
                 * 5 pharmacy
                 * 6 collectionStatus
                 */
                Prescription p = new Prescription(
                        cols[0],
                        cols[1],
                        cols[2],
                        cols[3],
                        cols[4],
                        cols[5],
                        cols[6]
                );

                prescriptions.add(p);
            }
        }
    }

    /**
     * Returns all prescriptions (copy to protect internal list).
     */
    public List<Prescription> getAll() {
        return new ArrayList<>(prescriptions);
    }

    /**
     * Adds a new prescription.
     * This method:
     *  1. Adds to memory
     *  2. Saves to CSV
     *  3. Generates prescription text file
     *
     * @param prescription new prescription
     */
    public void addPrescription(Prescription prescription) throws IOException {

        if (prescription == null) {
            throw new IllegalArgumentException("Prescription cannot be null");
        }

        prescriptions.add(prescription);

        // Persist to CSV
        saveToCsv();

        // Generate output text file
        writePrescriptionTextFile(prescription);
    }

    /**
     * Saves all prescriptions back to the CSV file.
     */
    private void saveToCsv() throws IOException {

        if (sourceFilePath == null) {
            throw new IllegalStateException("CSV source path not set. Call load() first.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            // CSV header
            writer.write("prescriptionId,patientNhsNumber,clinicianId,medication,dosage,pharmacy,collectionStatus");
            writer.newLine();

            for (Prescription p : prescriptions) {
                writer.write(String.join(",",
                        safe(p.getPrescriptionId()),
                        safe(p.getPatientNhsNumber()),
                        safe(p.getClinicianId()),
                        safe(p.getMedication()),
                        safe(p.getDosage()),
                        safe(p.getPharmacy()),
                        safe(p.getCollectionStatus())
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Prevents commas from breaking CSV structure.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
   
        /**
     * Generates a formatted prescription text file.
     * Output example: output/prescriptions/prescription_RX001.txt
     */
    private void writePrescriptionTextFile(Prescription prescription) throws IOException {

        String outputDir = "output/prescriptions";
        java.io.File dir = new java.io.File(outputDir);

        // Ensure output directory exists
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = outputDir + "/prescription_" + prescription.getPrescriptionId() + ".txt";

        try (java.io.BufferedWriter writer =
                     new java.io.BufferedWriter(new java.io.FileWriter(filename))) {

            writer.write("PRESCRIPTION");
            writer.newLine();
            writer.write("----------------------------------");
            writer.newLine();

            writer.write("Prescription ID: " + safe(prescription.getPrescriptionId()));
            writer.newLine();
            writer.write("Patient NHS Number: " + safe(prescription.getPatientNhsNumber()));
            writer.newLine();
            writer.write("Clinician ID: " + safe(prescription.getClinicianId()));
            writer.newLine();

            writer.newLine();
            writer.write("Medication: " + safe(prescription.getMedication()));
            writer.newLine();
            writer.write("Dosage: " + safe(prescription.getDosage()));
            writer.newLine();
            writer.write("Pharmacy: " + safe(prescription.getPharmacy()));
            writer.newLine();
            writer.write("Collection Status: " + safe(prescription.getCollectionStatus()));
            writer.newLine();

            writer.newLine();
            writer.write("Generated: " + java.time.LocalDateTime.now());
        }
    }

}
