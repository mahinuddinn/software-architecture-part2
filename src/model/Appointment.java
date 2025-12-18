package model;

/**
 * Appointment
 * -----------
 * Represents an appointment between a patient and a clinician.
 *
 * This class establishes an association between Patient and Clinician
 * using their respective identifiers.
 */
public class Appointment {

    /** Unique appointment identifier */
    private String appointmentId;

    /** NHS number of the patient attending */
    private String patientNhsNumber;

    /** Clinician responsible for the appointment */
    private String clinicianId;

    /** Date and time of the appointment */
    private String dateTime;

    /** Reason for the visit */
    private String reason;

    /** Status (e.g. Scheduled, Completed, Cancelled) */
    private String status;

    /**
     * Constructs an Appointment object.
     */
    public Appointment(String appointmentId,
                       String patientNhsNumber,
                       String clinicianId,
                       String dateTime,
                       String reason,
                       String status) {
        this.appointmentId = appointmentId;
        this.patientNhsNumber = patientNhsNumber;
        this.clinicianId = clinicianId;
        this.dateTime = dateTime;
        this.reason = reason;
        this.status = status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientNhsNumber() {
        return patientNhsNumber;
    }

    public String getClinicianId() {
        return clinicianId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }
}
