package model;

/**
 * Clinician
 * ---------
 * Domain model representing a clinician such as a doctor, nurse, or specialist.
 *
 * Clinicians may be associated with appointments, prescriptions, and referrals.
 */
public class Clinician {

    /** Unique identifier for the clinician */
    private String clinicianId;

    /** Clinician's full name */
    private String name;

    /** Role (e.g. Doctor, Nurse, Specialist) */
    private String role;

    /** Medical specialty */
    private String specialty;

    /** Workplace (e.g. hospital or GP surgery) */
    private String workplace;

    /**
     * Constructs a Clinician object.
     */
    public Clinician(String clinicianId,
                     String name,
                     String role,
                     String specialty,
                     String workplace) {
        this.clinicianId = clinicianId;
        this.name = name;
        this.role = role;
        this.specialty = specialty;
        this.workplace = workplace;
    }

    public String getClinicianId() {
        return clinicianId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getWorkplace() {
        return workplace;
    }

    @Override
    public String toString() {
        return name + " (" + specialty + ")";
    }
}
