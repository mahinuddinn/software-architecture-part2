package repository;

import model.Clinician;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClinicianRepository
 * -------------------
 * Handles loading, storing, and persisting Clinician records.
 *
 * This is part of the MODEL layer in MVC.
 */
public class ClinicianRepository {

    /** In-memory storage of clinicians */
    private final List<Clinician> clinicians = new ArrayList<>();

    /** CSV file path (set on load) */
    private String sourceFilePath;

    /**
     * Loads clinicians from a CSV file.
     *
     * Expected header:
     * clinicianId,name,role,specialty,workplace
     */
    public void load(String filePath) throws IOException {

        this.sourceFilePath = filePath;
        clinicians.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Skip header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {

                String[] cols = line.split(",", -1);

                if (cols.length < 5) continue;

                Clinician clinician = new Clinician(
                        cols[0].trim(),
                        cols[1].trim(),
                        cols[2].trim(),
                        cols[3].trim(),
                        cols[4].trim()
                );

                clinicians.add(clinician);
            }
        }
    }

    /**
     * Returns all clinicians.
     */
    public List<Clinician> getAll() {
        return new ArrayList<>(clinicians);
    }

    /**
     * Adds a clinician and saves to CSV.
     */
    public void add(Clinician clinician) throws IOException {

        if (clinician == null || clinician.getClinicianId().isBlank()) {
            throw new IllegalArgumentException("Clinician ID is required.");
        }

        clinicians.add(clinician);
        saveToCsv();
    }

    /**
     * Persists clinicians back to CSV.
     */
    private void saveToCsv() throws IOException {

        if (sourceFilePath == null) {
            throw new IllegalStateException("CSV file path not set. Call load() first.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            writer.write("clinicianId,name,role,specialty,workplace");
            writer.newLine();

            for (Clinician c : clinicians) {
                writer.write(String.join(",",
                        safe(c.getClinicianId()),
                        safe(c.getName()),
                        safe(c.getRole()),
                        safe(c.getSpecialty()),
                        safe(c.getWorkplace())
                ));
                writer.newLine();
            }
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
