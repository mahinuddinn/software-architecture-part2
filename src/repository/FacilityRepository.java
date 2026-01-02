package repository;

import model.Facility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FacilityRepository
 * ------------------
 * Handles loading, adding, updating, and deleting facilities.
 * CSV-backed repository (MODEL layer).
 */
public class FacilityRepository {

    private final List<Facility> facilities = new ArrayList<>();
    private String sourceFilePath;

    /**
     * Load facilities from CSV.
     */
    public void load(String filePath) throws IOException {

        this.sourceFilePath = filePath;
        facilities.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",", -1);
                if (cols.length < 4) continue;

                facilities.add(new Facility(
                        cols[0].trim(), // facilityId
                        cols[1].trim(), // facilityName
                        cols[2].trim(), // facilityType
                        cols[3].trim()  // location
                ));
            }
        }
    }

    public List<Facility> getAll() {
        return new ArrayList<>(facilities);
    }

    public void addFacility(Facility facility) throws IOException {
        facilities.add(facility);
        saveToCsv();
    }

    public void updateFacility(Facility updated) throws IOException {
        for (int i = 0; i < facilities.size(); i++) {
            if (facilities.get(i).getFacilityId().equalsIgnoreCase(updated.getFacilityId())) {
                facilities.set(i, updated);
                saveToCsv();
                return;
            }
        }
        throw new IllegalArgumentException("Facility not found.");
    }

    public void deleteFacility(String facilityId) throws IOException {
        facilities.removeIf(f -> f.getFacilityId().equalsIgnoreCase(facilityId));
        saveToCsv();
    }

    /* ================= CSV SAVE ================= */

    private void saveToCsv() throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            writer.write("facilityId,facilityName,facilityType,location");
            writer.newLine();

            for (Facility f : facilities) {
                writer.write(String.join(",",
                        safe(f.getFacilityId()),
                        safe(f.getFacilityName()),
                        safe(f.getFacilityType()),
                        safe(f.getLocation())
                ));
                writer.newLine();
            }
        }
    }

    private String safe(String v) {
        return v == null ? "" : v.replace(",", " ");
    }
}