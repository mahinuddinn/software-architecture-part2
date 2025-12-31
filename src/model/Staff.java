package model;

/**
 * Staff
 * -----
 * Domain model representing a staff member.
 *
 * This is a simple DATA class (Model layer).
 * No logic, only fields + getters.
 */
public class Staff {

    private String staffId;
    private String name;
    private String role;
    private String department;

    public Staff(String staffId, String name, String role, String department) {
        this.staffId = staffId;
        this.name = name;
        this.role = role;
        this.department = department;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }
}
