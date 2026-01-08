package repository;

import model.Staff;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StaffRepository
 * ---------------
 * Loads and persists Staff records from/to a CSV file.
 *
 * EXTENDED CSV format:
 * staffId,firstName,lastName,role,department,facilityId,phoneNumber,email,
 * employmentStatus,startDate,lineManager,accessLevel
 *
 * GUI expected display:
 * Staff ID | Name | Role | Department | Facility | Phone | Email |
 * Employment Status | Start Date | Line Manager | Access Level
 *
 * This class belongs to the MODEL layer in MVC.
 * It performs:
 *  - CSV loading
 *  - In-memory storage
 *  - CSV persistence
 *
 * NO GUI logic is allowed here.
 */
public class StaffRepository {

    /** In-memory storage of staff records */
    private final List<Staff> staffList = new ArrayList<>();

    /** Path to the CSV file used for persistence */
    private String sourceFilePath;

    /* =====================================================
       LOAD
       ===================================================== */

    /**
     * Load staff records from CSV into memory.
     *
     * @param filePath path to staff.csv
     */
    public void load(String filePath) throws IOException {
        this.sourceFilePath = filePath;
        staffList.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String header = br.readLine(); // skip header
            if (header == null) return;

            String line;
            while ((line = br.readLine()) != null) {

                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                String[] cols = line.split(",", -1);

                // Defensive check to avoid malformed CSV rows
                if (cols.length < 12) continue;

                String staffId = cols[0].trim();
                String firstName = cols[1].trim();
                String lastName = cols[2].trim();
                String role = cols[3].trim();
                String department = cols[4].trim();
                String facilityId = cols[5].trim();
                String phoneNumber = cols[6].trim();
                String email = cols[7].trim();
                String employmentStatus = cols[8].trim();
                String startDate = cols[9].trim();
                String lineManager = cols[10].trim();
                String accessLevel = cols[11].trim();

                String fullName = (firstName + " " + lastName).trim();

                staffList.add(new Staff(
                        staffId,
                        fullName,
                        role,
                        department,
                        facilityId,
                        phoneNumber,
                        email,
                        employmentStatus,
                        startDate,
                        lineManager,
                        accessLevel
                ));
            }
        }
    }

    /* =====================================================
       READ (VIEW)
       ===================================================== */

    /**
     * Return all staff records (used by GUI table).
     */
    public List<Staff> getAll() {
        return new ArrayList<>(staffList);
    }

    /**
     * Find a staff member by staff ID.
     * Used by View / Edit / Delete logic.
     */
    public Staff findById(String staffId) {
        for (Staff s : staffList) {
            if (s.getStaffId().equalsIgnoreCase(staffId)) {
                return s;
            }
        }
        return null;
    }

    /* =====================================================
       CREATE
       ===================================================== */

    /**
     * Add new staff record and persist to CSV.
     *
     * Validation is intentionally minimal here.
     * Detailed field validation is handled in the VIEW layer.
     */
    public void addStaff(Staff staff) throws IOException {

        if (staff == null || staff.getStaffId() == null || staff.getStaffId().isBlank()) {
            throw new IllegalArgumentException("Staff ID is required.");
        }

        if (findById(staff.getStaffId()) != null) {
            throw new IllegalArgumentException("Staff ID already exists: " + staff.getStaffId());
        }

        staffList.add(staff);
        saveToCsv();
    }

    /* =====================================================
       UPDATE
       ===================================================== */

    /**
     * Update an existing staff member (matched by staff ID).
     */
    public void updateStaff(Staff updatedStaff) throws IOException {

        boolean found = false;

        for (int i = 0; i < staffList.size(); i++) {
            Staff existing = staffList.get(i);

            if (existing.getStaffId()
                    .equalsIgnoreCase(updatedStaff.getStaffId())) {

                staffList.set(i, updatedStaff);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Staff member not found.");
        }

        saveToCsv();
    }

    /* =====================================================
       DELETE
       ===================================================== */

    /**
     * Delete staff by staff ID and persist changes.
     */
    public void deleteStaff(String staffId) throws IOException {

        boolean removed = staffList.removeIf(
                s -> s.getStaffId().equalsIgnoreCase(staffId)
        );

        if (!removed) {
            throw new IllegalArgumentException("Staff not found: " + staffId);
        }

        saveToCsv();
    }

    /* =====================================================
       CSV SAVE
       ===================================================== */

    /**
     * Save current in-memory staff list back to the CSV.
     *
     * Staff model stores full name, so it is split
     * back into first and last name for CSV storage.
     */
    private void saveToCsv() throws IOException {

        if (sourceFilePath == null) {
            throw new IllegalStateException("CSV file path not set. Call load() first.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            // Updated CSV header
            writer.write("staffId,firstName,lastName,role,department,facilityId,phoneNumber,email,employmentStatus,startDate,lineManager,accessLevel");
            writer.newLine();

            for (Staff s : staffList) {

                String[] nameParts = splitName(s.getName());

                writer.write(String.join(",",
                        safe(s.getStaffId()),
                        safe(nameParts[0]),
                        safe(nameParts[1]),
                        safe(s.getRole()),
                        safe(s.getDepartment()),
                        safe(s.getFacilityId()),
                        safe(s.getPhoneNumber()),
                        safe(s.getEmail()),
                        safe(s.getEmploymentStatus()),
                        safe(s.getStartDate()),
                        safe(s.getLineManager()),
                        safe(s.getAccessLevel())
                ));
                writer.newLine();
            }
        }
    }

    /* =====================================================
       HELPERS
       ===================================================== */

    /**
     * Split a full name into first + last name.
     */
    private String[] splitName(String fullName) {

        if (fullName == null || fullName.isBlank()) {
            return new String[]{"", ""};
        }

        int lastSpace = fullName.lastIndexOf(' ');
        if (lastSpace == -1) {
            return new String[]{fullName, ""};
        }

        return new String[]{
                fullName.substring(0, lastSpace).trim(),
                fullName.substring(lastSpace + 1).trim()
        };
    }

    /**
     * Prevent commas breaking CSV structure.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }

    public boolean existsById(String staffId) {
    return staffList.stream()
            .anyMatch(s -> s.getStaffId().equalsIgnoreCase(staffId));
}

}
