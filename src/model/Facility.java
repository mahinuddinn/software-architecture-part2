package model;

/**
 * Facility
 * --------
 * Represents a healthcare facility such as a GP surgery or hospital.
 *
 * Facilities are referenced by clinicians, referrals, and appointments.
 */
public class Facility {

    /** Unique identifier for the facility */
    private String facilityId;

    /** Name of the facility */
    private String name;

    /** Type of facility (e.g. GP Surgery, Hospital) */
    private String type;

    /** Contact details (phone/email) */
    private String contactDetails;

    /** Maximum capacity of the facility */
    private int capacity;

    /**
     * Constructs a Facility object.
     */
    public Facility(String facilityId,
                    String name,
                    String type,
                    String contactDetails,
                    int capacity) {
        this.facilityId = facilityId;
        this.name = name;
        this.type = type;
        this.contactDetails = contactDetails;
        this.capacity = capacity;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public int getCapacity() {
        return capacity;
    }
}
