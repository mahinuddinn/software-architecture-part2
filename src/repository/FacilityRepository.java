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
private final List<Facility> facilities = new ArrayList<>();

    /**
 * Returns all facilities currently loaded in memory.
 * Used by the View layer to populate tables.
 */
public List<Facility> getAllFacilities() {
    return facilities;
}



    private static final String DELIMITER = ",";


    /** In-memory list of facilities */
    private final List<Facility> facilityList = new ArrayList<>();

    /** CSV file path */
    private String sourceFilePath;

    /**
     * Load facilities from CSV.
     */
public void load(String filePath) throws IOException {

    facilities.clear();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

        // Skip header
        br.readLine();

        String line;
        while ((line = br.readLine()) != null) {

            String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);


            // SAFELY parse capacity
            int capacity = 0;
            String capacityRaw = cols[10].replace("\"", "").trim();
            if (!capacityRaw.isEmpty() && capacityRaw.matches("\\d+")) {
                capacity = Integer.parseInt(capacityRaw);
            }

            Facility facility = new Facility(
        cols[0].replace("\"", "").trim(),   // facility_id
        cols[1].replace("\"", "").trim(),   // facility_name
        cols[2].replace("\"", "").trim(),   // facility_type
        cols[3].replace("\"", "").trim(),   // address
        cols[4].replace("\"", "").trim(),   // postcode
        cols[5].replace("\"", "").trim(),   // phone_number
        cols[6].replace("\"", "").trim(),   // email
        cols[7].replace("\"", "").trim(),   // opening_hours
        cols[8].replace("\"", "").trim(),   // manager_name
        Integer.parseInt(cols[9].replace("\"", "").trim()), // capacity
        cols[10].replace("\"", "").trim()   // specialities_offered
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
}
