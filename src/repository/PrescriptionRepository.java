package repository;

import model.Prescription;
import model.Clinician;

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
 *  - Links prescriptions to clinicians via ClinicianRepository
 *
 * Part of the Model layer in MVC.
 */
public class PrescriptionRepository {

    /** In-memory list of prescriptions */
    private final List<Prescription> prescriptions = new ArrayList<>();

    /** Original CSV file path (used for persistence) */
    private String sourceFilePath;

    /** Repository used to resolve clinician IDs to Clinician objects */
    private ClinicianRepository clinicianRepository;

    /**
     * Injects ClinicianRepository (association).
     * This enables clinician lookups without tight coupling.
     */
    public void setClinicianRepository(ClinicianRepository clinicianRepository) {
        this.clinicianRepository = clinicianRepository;
    }

    /**
     * Loads prescriptions from a CSV file.
     *
     * @param filePath path to prescriptions.csv
     */
    public void load(String filePath) throws IOException {

        this.sourceFilePath = filePath;   // ✅ IMPORTANT FIX
        prescriptions.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String header = br.readLine(); // skip header
            if (header == null) return;

            String line;
            while ((line = br.readLine()) != null) {

                String[] cols = line.split(",", -1);
                if (cols.length < 7) continue;

                Prescription p = new Prescription(
                        cols[0].trim(), // prescriptionId
                        cols[1].trim(), // patientNhsNumber
                        cols[2].trim(), // clinicianId
                        cols[3].trim(), // medication
                        cols[4].trim(), // dosage
                        cols[5].trim(), // pharmacy
                        cols[6].trim()  // collectionStatus
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
     * Resolves the clinician responsible for a prescription.
     * Demonstrates association between Prescription and Clinician.
     *
     * @param prescription prescription record
     * @return Clinician object or null if not found
     */
    public Clinician getClinicianForPrescription(Prescription prescription) {

        if (clinicianRepository == null || prescription == null) {
            return null;
        }

        return clinicianRepository.findById(prescription.getClinicianId());
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write("PRESCRIPTION");
            writer.newLine();
            writer.write("----------------------------------");
            writer.newLine();

            writer.write("Prescription ID: " + safe(prescription.getPrescriptionId()));
            writer.newLine();
            writer.write("Patient NHS Number: " + safe(prescription.getPatientNhsNumber()));
            writer.newLine();

            // 🔗 Clinician linking (human-readable)
            if (clinicianRepository != null) {
                Clinician clinician = clinicianRepository.findById(prescription.getClinicianId());
                if (clinician != null) {
                    writer.write("Clinician: " + clinician.getName()
                            + " (" + clinician.getSpecialty() + ")");
                } else {
                    writer.write("Clinician ID: " + safe(prescription.getClinicianId()));
                }
            } else {
                writer.write("Clinician ID: " + safe(prescription.getClinicianId()));
            }
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

    /**
     * Prevents commas from breaking CSV structure.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
