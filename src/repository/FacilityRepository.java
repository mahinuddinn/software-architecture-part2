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
 * CSV FORMAT:
 * facilityId,name,type,address,phoneNumber
 */
public class FacilityRepository {

    /** In-memory list of facilities */
    private final List<Facility> facilityList = new ArrayList<>();

    /** CSV file path */
    private String sourceFilePath;

    /**
     * Load facilities from CSV.
     */
    public void load(String filePath) throws IOException {
        this.sourceFilePath = filePath;
        facilityList.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String header = br.readLine(); // skip header
            if (header == null) return;

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] cols = line.split(",", -1);
                if (cols.length < 5) continue;

                Facility facility = new Facility(
                        cols[0].trim(), // facilityId
                        cols[1].trim(), // name
                        cols[2].trim(), // type
                        cols[3].trim(), // address
                        cols[4].trim()  // phoneNumber
                );

                facilityList.add(facility);
            }
        }
    }

    /**
     * Return all facilities.
     */
    public List<Facility> getAll() {
        return new ArrayList<>(facilityList);
    }

    /**
     * Find facility by ID.
     */
    public Facility findById(String facilityId) {
        for (Facility f : facilityList) {
            if (f.getFacilityId().equalsIgnoreCase(facilityId)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Add new facility.
     */
    public void addFacility(Facility facility) throws IOException {

        if (findById(facility.getFacilityId()) != null) {
            throw new IllegalArgumentException(
                    "Facility already exists: " + facility.getFacilityId());
        }

        facilityList.add(facility);
        saveToCsv();
    }

    /**
     * Update existing facility.
     */
    public void updateFacility(Facility updated) throws IOException {

        boolean found = false;

        for (int i = 0; i < facilityList.size(); i++) {
            if (facilityList.get(i).getFacilityId()
                    .equalsIgnoreCase(updated.getFacilityId())) {

                facilityList.set(i, updated);
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

        boolean removed = facilityList.removeIf(
                f -> f.getFacilityId().equalsIgnoreCase(facilityId));

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
            throw new IllegalStateException("Call load() before saving.");
        }

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(sourceFilePath))) {

            writer.write("facilityId,name,type,address,phoneNumber");
            writer.newLine();

            for (Facility f : facilityList) {
                writer.write(String.join(",",
                        safe(f.getFacilityId()),
                        safe(f.getName()),
                        safe(f.getType()),
                        safe(f.getAddress()),
                        safe(f.getPhoneNumber())
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
}
