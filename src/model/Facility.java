package model;

/**
 * Facility
 * --------
 * Domain model representing a healthcare facility.
 *
 * MVC ROLE:
 *  - MODEL layer
 *  - Stores facility data only
 *  - NO file I/O
 *  - NO GUI logic
 *
 * CSV FORMAT:
 * facilityId,name,type,address,phoneNumber
 */
public class Facility {

    /* =========================
       CORE FIELDS
       ========================= */

    /** Unique facility identifier (e.g. H001, S001) */
    private String facilityId;

    /** Facility name (e.g. City Hospital, Conway Medical Centre) */
    private String name;

    /** Facility type (Hospital, Surgery, Clinic, etc.) */
    private String type;

    /** Full address of the facility */
    private String address;

    /** Contact phone number */
    private String phoneNumber;

    /* =========================
       CONSTRUCTOR
       ========================= */

    /**
     * Full constructor.
     * MUST match:
     *  - CSV column order
     *  - FacilityRepository
     *  - MainFrame GUI
     */
    public Facility(
            String facilityId,
            String name,
            String type,
            String address,
            String phoneNumber
    ) {
        this.facilityId = facilityId;
        this.name = name;
        this.type = type;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    /* =========================
       GETTERS
       ========================= */

    public String getFacilityId() {
        return facilityId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /* =========================
       SETTERS (for Edit)
       ========================= */

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /* =========================
       DEBUG / DISPLAY
       ========================= */

    @Override
    public String toString() {
        return facilityId + " | " + name + " | " + type;
    }
}
