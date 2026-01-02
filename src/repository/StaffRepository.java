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
 */
public class StaffRepository {

    /** In-memory storage of staff records */
    private final List<Staff> staffList = new ArrayList<>();

    /** Path to the CSV file used for persistence */
    private String sourceFilePath;

    /**
     * Load staff records from CSV into memory.
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

                // Skip completely empty lines (your files sometimes include these)
                if (line.trim().isEmpty()) continue;

                String[] cols = line.split(",", -1);

                // We expect 5 columns: id, first, last, role, dept
                if (cols.length < 5) continue;

                String staffId = cols[0].trim();
                String firstName = cols[1].trim();
                String lastName = cols[2].trim();
                String role = cols[3].trim();
                String department = cols[4].trim();

                // GUI "Name" column = firstName + lastName
                String fullName = (firstName + " " + lastName).trim();

                Staff staff = new Staff(staffId, fullName, role, department);
                staffList.add(staff);
            }
        }
    }

    /**
     * Return all staff as a defensive copy.
     */
    public List<Staff> getAll() {
        return new ArrayList<>(staffList);
    }

    /**
     * Add new staff record and persist to CSV.
     */
    public void addStaff(Staff staff) throws IOException {
        if (staff == null || staff.getStaffId() == null || staff.getStaffId().isBlank()) {
            throw new IllegalArgumentException("Staff ID is required.");
        }

        // Prevent duplicate IDs
        if (findById(staff.getStaffId()) != null) {
            throw new IllegalArgumentException("Staff ID already exists: " + staff.getStaffId());
        }

        staffList.add(staff);
        saveToCsv();
    }

    /**
     * Update existing staff record (matched by staffId) and persist.
     */
    public void updateStaff(Staff updated) throws IOException {
        if (updated == null || updated.getStaffId() == null || updated.getStaffId().isBlank()) {
            throw new IllegalArgumentException("Staff ID is required.");
        }

        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getStaffId().equalsIgnoreCase(updated.getStaffId())) {
                staffList.set(i, updated);
                saveToCsv();
                return;
            }
        }

        throw new IllegalArgumentException("Staff not found: " + updated.getStaffId());
    }

    /**
     * Delete staff by staffId and persist.
     */
    public void deleteStaff(String staffId) throws IOException {
        boolean removed = staffList.removeIf(s -> s.getStaffId().equalsIgnoreCase(staffId));
        if (!removed) throw new IllegalArgumentException("Staff not found: " + staffId);
        saveToCsv();
    }

    /**
     * Find a staff record by ID.
     */
    public Staff findById(String staffId) {
        for (Staff s : staffList) {
            if (s.getStaffId().equalsIgnoreCase(staffId)) return s;
        }
        return null;
    }

    /**
     * Save current in-memory staff list back to the CSV.
     *
     * IMPORTANT:
     * Your Staff model stores fullName, so we split it back into first/last.
     * If a name only has one word, lastName becomes blank.
     */
    private void saveToCsv() throws IOException {
        if (sourceFilePath == null) {
            throw new IllegalStateException("CSV file path not set. Call load() first.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            // Header must match the real CSV structure
            writer.write("staffId,firstName,lastName,role,department");
            writer.newLine();

            for (Staff s : staffList) {
                String[] nameParts = splitName(s.getName());
                String firstName = nameParts[0];
                String lastName = nameParts[1];

                writer.write(String.join(",",
                        safe(s.getStaffId()),
                        safe(firstName),
                        safe(lastName),
                        safe(s.getRole()),
                        safe(s.getDepartment())
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Split a full name into first + last.
     * - "Michelle Adams" -> ["Michelle", "Adams"]
     * - "Mary Jane Smith" -> ["Mary Jane", "Smith"] (last word treated as last name)
     * - "Michelle" -> ["Michelle", ""]
     */
    private String[] splitName(String fullName) {
        if (fullName == null) return new String[]{"", ""};
        String n = fullName.trim();
        if (n.isEmpty()) return new String[]{"", ""};

        int lastSpace = n.lastIndexOf(' ');
        if (lastSpace == -1) return new String[]{n, ""};

        String first = n.substring(0, lastSpace).trim();
        String last = n.substring(lastSpace + 1).trim();
        return new String[]{first, last};
    }

    /**
     * Prevent commas breaking CSV structure.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
