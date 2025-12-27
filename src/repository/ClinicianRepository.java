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
 * Handles loading, storing, updating, and persisting Clinician records.
 *
 * MODEL layer in MVC.
 *
 * CSV format:
 * clinicianId,name,role,specialty,workplace
 */
public class ClinicianRepository {

    /** In-memory list of clinicians */
    private final List<Clinician> clinicians = new ArrayList<>();

    /** CSV file path (set when load() is called) */
    private String sourceFilePath;

    /**
     * Loads clinicians from CSV file.
     *
     * @param filePath path to clinicians.csv
     */
    public void load(String filePath) throws IOException {

        clinicians.clear();
        sourceFilePath = filePath;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Skip CSV header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {

                String[] cols = line.split(",", -1);
                if (cols.length < 5) continue;

                Clinician clinician = new Clinician(
                        cols[0].trim(), // clinicianId
                        cols[1].trim(), // name
                        cols[2].trim(), // role
                        cols[3].trim(), // specialty
                        cols[4].trim()  // workplace
                );

                clinicians.add(clinician);
            }
        }
    }

    /**
     * Returns a copy of all clinicians.
     */
    public List<Clinician> getAll() {
        return new ArrayList<>(clinicians);
    }

    /**
     * Finds a clinician by ID.
     *
     * @param clinicianId clinician identifier
     * @return Clinician if found, otherwise null
     */
    public Clinician findById(String clinicianId) {

        for (Clinician c : clinicians) {
            if (c.getClinicianId().equalsIgnoreCase(clinicianId)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Adds a new clinician and saves to CSV.
     */
    public void add(Clinician clinician) throws IOException {

        if (clinician == null || clinician.getClinicianId().isBlank()) {
            throw new IllegalArgumentException("Clinician ID is required.");
        }

        clinicians.add(clinician);
        saveToCsv();
    }

    /**
     * Updates an existing clinician (matched by ID).
     */
    public void update(Clinician updatedClinician) throws IOException {

        for (int i = 0; i < clinicians.size(); i++) {
            if (clinicians.get(i).getClinicianId()
                    .equalsIgnoreCase(updatedClinician.getClinicianId())) {

                clinicians.set(i, updatedClinician);
                saveToCsv();
                return;
            }
        }

        throw new IllegalArgumentException("Clinician not found: " +
                updatedClinician.getClinicianId());
    }

    /**
     * Deletes a clinician by ID.
     */
    public void delete(String clinicianId) throws IOException {

        boolean removed = clinicians.removeIf(
                c -> c.getClinicianId().equalsIgnoreCase(clinicianId)
        );

        if (!removed) {
            throw new IllegalArgumentException("Clinician not found: " + clinicianId);
        }

        saveToCsv();
    }

    /**
     * Writes clinicians back to CSV.
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

    /**
     * Prevents commas from breaking CSV structure.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
