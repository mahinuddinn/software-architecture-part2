package repository;

import model.Facility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FacilityRepository
 * ------------------
 * Handles loading, storing, updating and deleting Facility records.
 *
 * MVC ROLE:
 *  - MODEL / DATA layer
 *  - CSV persistence only
 *  - NO GUI logic
 *  - NO Swing imports
 *
 * CSV FORMAT (11 columns):
 * facility_id,facility_name,facility_type,address,postcode,phone_number,email,opening_hours,manager_name,capacity,specialities_offered
 */
public class FacilityRepository {

    /** In-memory list of facilities (single source of truth) */
    private final List<Facility> facilities = new ArrayList<>();

    /** CSV delimiter */
    private static final String DELIMITER = ",";

    /** CSV file path (required for saving back to the same file) */
    private String sourceFilePath;

    /**
     * Returns all facilities currently loaded in memory.
     * Used by the View layer to populate tables.
     */
    public List<Facility> getAllFacilities() {
        return facilities;
    }

    /**
     * Return all facilities.
     * (Keeps compatibility with your MainFrame usage.)
     */
    public List<Facility> getAll() {
        return facilities;
    }

    /**
     * Load facilities from CSV.
     * MUST be called before add/update/delete so sourceFilePath is set.
     */
    public void load(String filePath) throws IOException {

        // Save file path so saveToCsv() can persist to the same location
        this.sourceFilePath = filePath;

        facilities.clear(); // clear the SAME list every time

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Skip CSV header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {

                // NOTE: This simple split assumes your CSV has no unescaped commas inside fields.
                // (Your sample data uses quotes sometimes. If you still get parsing issues, tell me and I'll
                //  give you a tiny CSV-safe parser without external libraries.)
                String[] cols = parseCsvLine(line);
                if (cols.length < 11) continue;

                Facility facility = new Facility(
                        cols[0].trim(),                   // facility_id
                        cols[1].trim(),                   // facility_name
                        cols[2].trim(),                   // facility_type
                        cols[3].trim(),                   // address
                        cols[4].trim(),                   // postcode
                        cols[5].trim(),                   // phone_number
                        cols[6].trim(),                   // email
                        cols[7].trim(),                   // opening_hours
                        cols[8].trim(),                   // manager_name
                        parseIntSafe(cols[9].trim()),     // capacity
                        cols[10].trim()                   // specialities_offered
                );

                facilities.add(facility);
            }
        }
    }

    /**
     * Retrieves a Facility by its ID.
     *
     * Used for indirect lookups such as:
     * Appointment → Facility (Part B)
     *
     * @param facilityId the facility identifier
     * @return matching Facility or null if not found
     */
    public Facility getById(String facilityId) {

        // Safety check
        if (facilityId == null) return null;

        // Search in-memory list
        for (Facility f : facilities) {
            if (f.getFacilityId().equalsIgnoreCase(facilityId)) {
                return f;
            }
        }

        return null; // not found
    }

    /**
     * Find facility by ID.
     * (Alias kept for compatibility with existing code.)
     */
    public Facility findById(String facilityId) {
        return getById(facilityId);
    }

    /**
     * Add new facility.
     */
    public void addFacility(Facility facility) throws IOException {

        if (facility == null) {
            throw new IllegalArgumentException("Facility cannot be null.");
        }

        if (findById(facility.getFacilityId()) != null) {
            throw new IllegalArgumentException(
                    "Facility already exists: " + facility.getFacilityId()
            );
        }

        facilities.add(facility);
        saveToCsv();
    }

    /**
     * Update existing facility.
     */
    public void updateFacility(Facility updated) throws IOException {

        if (updated == null) {
            throw new IllegalArgumentException("Updated facility cannot be null.");
        }

        boolean found = false;

        for (int i = 0; i < facilities.size(); i++) {
            if (facilities.get(i).getFacilityId()
                    .equalsIgnoreCase(updated.getFacilityId())) {

                facilities.set(i, updated);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Facility not found.");
        }

        saveToCsv();
    }

    /**
     * Delete facility by ID.
     */
    public void deleteFacility(String facilityId) throws IOException {

        boolean removed = facilities.removeIf(
                f -> f.getFacilityId().equalsIgnoreCase(facilityId)
        );

        if (!removed) {
            throw new IllegalArgumentException("Facility not found.");
        }

        saveToCsv();
    }

    /**
     * Persist facilities back to CSV.
     */
    private void saveToCsv() throws IOException {

        if (sourceFilePath == null) {
            // This is the error you kept seeing before
            throw new IllegalStateException("Call load() before saving.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            // ✅ Correct 11-column header (matches your facilities.csv)
            writer.write("facility_id,facility_name,facility_type,address,postcode,phone_number,email,opening_hours,manager_name,capacity,specialities_offered");
            writer.newLine();

            for (Facility f : facilities) {

                writer.write(String.join(DELIMITER,
                        safe(f.getFacilityId()),
                        safe(f.getFacilityName()),
                        safe(f.getFacilityType()),
                        safe(f.getAddress()),
                        safe(f.getPostcode()),
                        safe(f.getPhoneNumber()),
                        safe(f.getEmail()),
                        safe(f.getOpeningHours()),
                        safe(f.getManagerName()),
                        safe(String.valueOf(f.getCapacity())),
                        safe(f.getSpecialitiesOffered())
                ));

                writer.newLine();
            }
        }
    }

    /**
     * Prevent CSV breakage from commas.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }

    /**
     * Safe integer parsing for capacity column.
     * If blank/invalid, defaults to 0 instead of crashing the load.
     */
    private int parseIntSafe(String value) {
        try {
            if (value == null || value.isBlank()) return 0;
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
 * Splits a CSV line while respecting quoted commas.
 * Example: "100 High Street, Birmingham" stays as ONE column.
 */
private String[] parseCsvLine(String line) {

    List<String> tokens = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);

        if (c == '"') {
            inQuotes = !inQuotes; // toggle quoted section
        } else if (c == ',' && !inQuotes) {
            tokens.add(current.toString().trim().replace("\"", ""));
            current.setLength(0);
        } else {
            current.append(c);
        }
    }

    tokens.add(current.toString().trim().replace("\"", ""));
    return tokens.toArray(new String[0]);
}

}
