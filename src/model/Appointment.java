package model;

/**
 * Appointment
 * -----------
 * Model class representing a single appointment record.
 *
 * This class maps directly to ONE row in appointments.csv.
 * Each attribute corresponds to a CSV column.
 *
 * MVC Role:
 * - MODEL: Holds appointment data only
 * - Contains NO GUI logic
 * - Contains NO file I/O
 */
public class Appointment {

    // Unique identifier for the appointment
    private String appointmentId;

    // Foreign key referencing Patient
    private String patientId;

    // Foreign key referencing Clinician
    private String clinicianId;

    // Foreign key referencing Facility
    private String facilityId;

    // Date of appointment (YYYY-MM-DD)
    private String appointmentDate;

    // Time of appointment (HH:MM)
    private String appointmentTime;

    // Status of appointment (New, Pending, In Progress, Completed)
    private String status;

    // Additional notes or comments
    private String notes;

    /**
     * Full constructor used when loading data from CSV
     * or creating a new appointment from the GUI.
     */
    public Appointment(String appointmentId, String patientId, String clinicianId,
                       String facilityId, String appointmentDate, String appointmentTime,
                       String status, String notes) {

        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.facilityId = facilityId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
    }

    // Getter methods (used by View & Repository)

    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getClinicianId() { return clinicianId; }
    public String getFacilityId() { return facilityId; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
}
