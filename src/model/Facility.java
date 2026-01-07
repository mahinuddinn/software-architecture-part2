package model;

/**
 * Facility
 * --------
 * Domain model representing a healthcare facility such as
 * a GP surgery or hospital.
 *
 * MVC ROLE:
 *  - MODEL layer only
 *  - Stores facility data
 *  - NO file I/O
 *  - NO GUI logic
 *  - NO validation logic
 *
 * DATA SOURCE:
 *  - facilities.csv
 *
 * Each Facility object represents ONE row in the CSV file.
 */
public class Facility {

    /* =====================================================
       CORE IDENTIFIERS
       ===================================================== */

    /** Unique facility identifier (e.g. S001, H001) */
    private String facilityId;

    /** Name of the facility (e.g. Birmingham Central GP Surgery) */
    private String facilityName;

    /** Type of facility (GP Surgery, Hospital, Clinic, etc.) */
    private String facilityType;

    /* =====================================================
       CONTACT & LOCATION DETAILS
       ===================================================== */

    /** Full street address of the facility */
    private String address;

    /** UK postcode */
    private String postcode;

    /** Main contact phone number */
    private String phoneNumber;

    /** Contact email address */
    private String email;

    /* =====================================================
       OPERATIONAL DETAILS
       ===================================================== */

    /** Opening hours description (human-readable) */
    private String openingHours;

    /** Name of facility manager or lead clinician */
    private String managerName;

    /** Approximate patient capacity */
    private int capacity;

    /** Services/specialities offered (pipe-separated list) */
    private String specialitiesOffered;

    /* =====================================================
       CONSTRUCTOR
       ===================================================== */

    /**
     * Full Facility constructor.
     *
     * PURPOSE:
     *  - Used when loading data from facilities.csv
     *  - Used when creating a new Facility via the GUI
     *
     * DESIGN NOTES:
     *  - Constructor parameter order MUST match:
     *      • CSV column order
     *      • FacilityRepository parsing logic
     *      • GUI form submission
     *
     * This ensures consistency across the MVC layers
     * and prevents data misalignment bugs.
     *
     * @param facilityId           unique facility identifier
     * @param facilityName         name of the facility
     * @param facilityType         type of facility
     * @param address              street address
     * @param postcode             postcode
     * @param phoneNumber          contact phone number
     * @param email                contact email
     * @param openingHours         opening hours description
     * @param managerName          facility manager name
     * @param capacity             patient capacity
     * @param specialitiesOffered  services offered (pipe-delimited)
     */
    public Facility(
            String facilityId,
            String facilityName,
            String facilityType,
            String address,
            String postcode,
            String phoneNumber,
            String email,
            String openingHours,
            String managerName,
            int capacity,
            String specialitiesOffered
    ) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.address = address;
        this.postcode = postcode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.openingHours = openingHours;
        this.managerName = managerName;
        this.capacity = capacity;
        this.specialitiesOffered = specialitiesOffered;
    }

    /* =====================================================
       GETTERS
       ===================================================== */

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public String getAddress() {
        return address;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getManagerName() {
        return managerName;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getSpecialitiesOffered() {
        return specialitiesOffered;
    }

    /* =====================================================
       SETTERS (USED FOR EDIT OPERATIONS)
       ===================================================== */

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setSpecialitiesOffered(String specialitiesOffered) {
        this.specialitiesOffered = specialitiesOffered;
    }

    /* =====================================================
       DEBUG / DISPLAY
       ===================================================== */

    /**
     * Human-readable summary used for debugging
     * and dropdown displays.
     */
    @Override
    public String toString() {
        return facilityId + " | " + facilityName + " | " + facilityType;
    }
}
