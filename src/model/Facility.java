package model;

/**
 * Facility
 * --------
 * Domain model representing a healthcare facility.
 *
 * MODEL layer in MVC.
 * Stores data only — no file I/O, no GUI logic.
 */
public class Facility {

    /** Unique facility identifier (e.g. S001, H001) */
    private String facilityId;

    /** Facility name (e.g. Conway Medical Centre) */
    private String facilityName;

    /** Facility type (GP Surgery, Hospital, Clinic, etc.) */
    private String facilityType;

    /** Physical location or area */
    private String location;

    /**
     * Full constructor.
     * Must match CSV column order and repository usage.
     */
    public Facility(String facilityId, String facilityName, String facilityType, String location) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.location = location;
    }

    /* ========== GETTERS ========== */

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public String getLocation() {
        return location;
    }

    /* ========== SETTERS (for Edit) ========== */

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
