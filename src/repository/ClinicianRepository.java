package repository;

import model.Clinician;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ClinicianRepository
 * -------------------
 * Handles loading, storing, editing, and deleting clinicians.
 *
 * MODEL layer (MVC).
 */
public class ClinicianRepository {

    /** In-memory list */
    private final List<Clinician> clinicians = new ArrayList<>();

    /** CSV source path */
    private String sourceFilePath;

    /* =====================================================
       LOAD
       ===================================================== */

public void load(String filePath) throws IOException {

    this.sourceFilePath = filePath;
    clinicians.clear();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

        // Skip header
        br.readLine();

        String line;
        while ((line = br.readLine()) != null) {

            String[] cols = line.split(",", -1);

            /*
             * Supported formats:
             * OLD: clinicianId,firstName,lastName,role,specialty,workplace (6 cols)
             * NEW: clinicianId,name,role,specialty,workplace (5 cols)
             */

            if (cols.length == 6) {
                // OLD format
                String fullName = cols[1].trim() + " " + cols[2].trim();

                clinicians.add(new Clinician(
                        cols[0].trim(),
                        fullName,
                        cols[3].trim(),
                        cols[4].trim(),
                        cols[5].trim()
                ));

            } else if (cols.length == 5) {
                // NEW format (current save format)
                clinicians.add(new Clinician(
                        cols[0].trim(),
                        cols[1].trim(),
                        cols[2].trim(),
                        cols[3].trim(),
                        cols[4].trim()
                ));
            }
            // Ignore malformed rows safely
        }
    }
}



    /* =====================================================
       READ
       ===================================================== */

    public List<Clinician> getAll() {
        return new ArrayList<>(clinicians);
    }

    public Clinician findById(String clinicianId) {
        for (Clinician c : clinicians) {
            if (c.getClinicianId().equalsIgnoreCase(clinicianId)) {
                return c;
            }
        }
        return null;
    }

    /* =====================================================
       CREATE
       ===================================================== */

    public void add(Clinician clinician) throws IOException {

        if (clinician == null || clinician.getClinicianId().isBlank()) {
            throw new IllegalArgumentException("Clinician ID is required.");
        }

        if (findById(clinician.getClinicianId()) != null) {
            throw new IllegalArgumentException("Clinician ID already exists.");
        }

        clinicians.add(clinician);
        saveToCsv();
    }

    /* =====================================================
       UPDATE
       ===================================================== */

    public void update(Clinician updated) throws IOException {

        for (int i = 0; i < clinicians.size(); i++) {
            if (clinicians.get(i).getClinicianId()
                    .equalsIgnoreCase(updated.getClinicianId())) {

                clinicians.set(i, updated);
                saveToCsv();
                return;
            }
        }

        throw new IllegalArgumentException("Clinician not found.");
    }

    /* =====================================================
       DELETE
       ===================================================== */

    public void delete(String clinicianId) throws IOException {

        boolean removed = clinicians.removeIf(c ->
                c.getClinicianId().equalsIgnoreCase(clinicianId));

        if (!removed) {
            throw new IllegalArgumentException("Clinician not found.");
        }

        saveToCsv();
    }

    /* =====================================================
       CSV SAVE
       ===================================================== */

    private void saveToCsv() throws IOException {

        if (sourceFilePath == null) {
            throw new IllegalStateException("CSV path not set. Call load() first.");
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

    public boolean existsById(String clinicianId) {
    return clinicians.stream()
            .anyMatch(c -> c.getClinicianId().equalsIgnoreCase(clinicianId));
}

}
