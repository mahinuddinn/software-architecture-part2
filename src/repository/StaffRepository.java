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
 * CSV expected format:
 * staffId,firstName,lastName,role,department
 *
 * GUI expected display:
 * Staff ID | Name (first + last) | Role | Department
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
                if (cols.length < 5) continue;

                String staffId = cols[0].trim();
                String firstName = cols[1].trim();
                String lastName = cols[2].trim();
                String role = cols[3].trim();
                String department = cols[4].trim();

                String fullName = (firstName + " " + lastName).trim();

                staffList.add(new Staff(staffId, fullName, role, department));
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
     * Used by "View Staff" button logic.
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
     * Delete staff by staff ID and persist.
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
     * Staff model stores fullName, so we split it
     * back into first + last name.
     */
    private void saveToCsv() throws IOException {

        if (sourceFilePath == null) {
            throw new IllegalStateException("CSV file path not set. Call load() first.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            writer.write("staffId,firstName,lastName,role,department");
            writer.newLine();

            for (Staff s : staffList) {

                String[] nameParts = splitName(s.getName());

                writer.write(String.join(",",
                        safe(s.getStaffId()),
                        safe(nameParts[0]),
                        safe(nameParts[1]),
                        safe(s.getRole()),
                        safe(s.getDepartment())
                ));
                writer.newLine();
            }
        }
    }

    /* =====================================================
       HELPERS
       ===================================================== */

    /**
     * Split a full name into first + last.
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
}
