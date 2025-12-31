package repository;

import model.Staff;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StaffRepository
 * ----------------
 * Handles loading, adding, editing, deleting staff records.
 *
 * CSV format:
 * staffId,name,role,department
 */
public class StaffRepository {

    private final List<Staff> staffList = new ArrayList<>();
    private String sourceFilePath;

    public void load(String filePath) throws IOException {

        sourceFilePath = filePath;
        staffList.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",", -1);
                if (cols.length < 4) continue;

                staffList.add(new Staff(
                        cols[0].trim(),
                        cols[1].trim(),
                        cols[2].trim(),
                        cols[3].trim()
                ));
            }
        }
    }

    public List<Staff> getAll() {
        return new ArrayList<>(staffList);
    }

    public void addStaff(Staff staff) throws IOException {
        staffList.add(staff);
        saveToCsv();
    }

    public void updateStaff(Staff updated) throws IOException {

        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getStaffId().equalsIgnoreCase(updated.getStaffId())) {
                staffList.set(i, updated);
                saveToCsv();
                return;
            }
        }
        throw new IllegalArgumentException("Staff not found");
    }

    public void deleteStaff(String staffId) throws IOException {
        staffList.removeIf(s -> s.getStaffId().equalsIgnoreCase(staffId));
        saveToCsv();
    }

    private void saveToCsv() throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            writer.write("staffId,name,role,department");
            writer.newLine();

            for (Staff s : staffList) {
                writer.write(String.join(",",
                        safe(s.getStaffId()),
                        safe(s.getName()),
                        safe(s.getRole()),
                        safe(s.getDepartment())
                ));
                writer.newLine();
            }
        }
    }

    private String safe(String v) {
        return v == null ? "" : v.replace(",", " ");
    }
}
