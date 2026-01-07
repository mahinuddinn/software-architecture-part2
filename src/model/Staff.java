package model;

/**
 * Staff
 * -----
 * Domain model representing a staff member.
 *
 * This is a simple DATA class (Model layer).
 * No logic, only fields + constructors + getters.
 */
public class Staff {

    // Core identifiers
    private String staffId;
    private String name;

    // Role & organisational details
    private String role;
    private String department;
    private String facilityId;
    private String lineManager;
    private String accessLevel;

    // Contact & employment details
    private String phoneNumber;
    private String email;
    private String employmentStatus;
    private String startDate;

    /**
     * Full constructor (USED by repository & forms)
     */
    public Staff(
            String staffId,
            String name,
            String role,
            String department,
            String facilityId,
            String phoneNumber,
            String email,
            String employmentStatus,
            String startDate,
            String lineManager,
            String accessLevel
    ) {
        this.staffId = staffId;
        this.name = name;
        this.role = role;
        this.department = department;
        this.facilityId = facilityId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
        this.lineManager = lineManager;
        this.accessLevel = accessLevel;
    }

    /**
     * Legacy constructor
     * (kept to avoid breaking existing code)
     */
    public Staff(String staffId, String name, String role, String department) {
        this.staffId = staffId;
        this.name = name;
        this.role = role;
        this.department = department;
    }

    // --------------------
    // Getters
    // --------------------

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

    public String getFacilityId() {
        return facilityId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getLineManager() {
        return lineManager;
    }

    public String getAccessLevel() {
        return accessLevel;
    }
}
