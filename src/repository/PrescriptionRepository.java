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
 * Handles loading and persisting prescription records.
 *
 * This repository supports creation of new prescriptions and
 * writing them back to prescriptions.csv.
 */
public class PrescriptionRepository {

    /**
     * In-memory list of prescriptions.
     */
    private final List<Prescription> prescriptions = new ArrayList<>();

    /**
     * Stores original CSV file path for persistence.
     */
    private String sourceFilePath;

    /**
     * Loads prescriptions from a CSV file.
     *
     * @param filePath path to prescriptions.csv
     * @throws IOException if file cannot be read
     */
    public void load(String filePath) throws IOException {

        this.sourceFilePath = filePath;
        prescriptions.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Skip header
            String header = reader.readLine();
            if (header == null) return;

            String line;
            while ((line = reader.readLine()) != null) {

                String[] cols = CsvUtil.splitCsvLine(line);

                /*
                 * Expected prescriptions.csv order:
                 * 0 = prescriptionId
                 * 1 = patientNhsNumber
                 * 2 = clinicianId
                 * 3 = medication
                 * 4 = dosage
                 * 5 = pharmacy
                 * 6 = collectionStatus
                 */
                String id = CsvUtil.get(cols, 0);
                String patientNhs = CsvUtil.get(cols, 1);
                String clinicianId = CsvUtil.get(cols, 2);
                String medication = CsvUtil.get(cols, 3);
                String dosage = CsvUtil.get(cols, 4);
                String pharmacy = CsvUtil.get(cols, 5);
                String status = CsvUtil.get(cols, 6);

                if (id.isEmpty()) continue;

                Prescription p = new Prescription(
                        id,
                        patientNhs,
                        clinicianId,
                        medication,
                        dosage,
                        pharmacy,
                        status
                );

                prescriptions.add(p);
            }
        }
    }

    /**
     * Returns all prescriptions.
     */
    public List<Prescription> getAll() {
        return new ArrayList<>(prescriptions);
    }

    /**
     * Adds a new prescription and persists it to CSV.
     *
     * @param prescription Prescription to add
     * @throws IOException if file cannot be written
     */
    public void addPrescription(Prescription prescription) throws IOException {

        if (prescription == null) {
            throw new IllegalArgumentException("Prescription cannot be null");
        }

        prescriptions.add(prescription);
        saveToCsv();
    }

    /**
     * Writes all prescriptions back to the CSV file.
     */
    private void saveToCsv() throws IOException {

        if (sourceFilePath == null || sourceFilePath.isEmpty()) {
            throw new IllegalStateException("CSV file path not set. Call load() first.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            // Header must match prescriptions.csv
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
     * Sanitises CSV values.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
