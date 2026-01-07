package model;

/**
 * Referral
 * --------
 * Domain model representing a referral from primary care
 * to secondary/specialist care.
 *
 * This class is part of the MODEL layer in MVC.
 * ✔ Contains ONLY data and getters
 * ✔ NO business logic
 * ✔ NO file handling
 */
public class Referral {

    /* =========================
       CORE IDENTIFIERS
       ========================= */

// Unique identifier for the referral
private String referralId;

// Identifier for the patient being referred (e.g. NHS number or internal ID)
private String patientId;

// ID of the clinician who initiated the referral
private String referringClinicianId;

// ID of the clinician to whom the patient is being referred
private String referredToClinicianId;

// ID of the facility where the referral originated
private String referringFacilityId;

// ID of the facility receiving the referral
private String referredToFacilityId;

// Date the referral was made
private String referralDate;

// Urgency level of the referral (e.g. Routine, Urgent, Emergency)
private String urgencyLevel;

// Reason for making the referral
private String referralReason;

// Clinical summary describing the patient's condition
private String clinicalSummary;

// Any investigations requested as part of the referral
private String requestedInvestigations;

// Current status of the referral (e.g. Pending, Accepted, Completed)
private String status;

// Associated appointment ID, if an appointment has been scheduled
private String appointmentId;

// Additional notes added by clinicians or administrators
private String notes;

// Date the referral record was created
private String createdDate;

// Date the referral record was last updated
private String lastUpdated;


    /**
     * FULL CONSTRUCTOR
     * ----------------
     * This constructor matches:
     *  - referrals.csv structure
     *  - ReferralRepository loading logic
     *  - MainFrame referral creation
     */
    public Referral(
        String referralId,
        String patientId,
        String referringClinicianId,
        String referredToClinicianId,
        String referringFacilityId,
        String referredToFacilityId,
        String referralDate,
        String urgencyLevel,
        String referralReason,
        String clinicalSummary,
        String requestedInvestigations,
        String status,
        String appointmentId,
        String notes,
        String createdDate,
        String lastUpdated
) {
        this.referralId = referralId;
        this.patientId = patientId;
        this.referringClinicianId = referringClinicianId;
        this.referredToClinicianId = referredToClinicianId;
        this.referringFacilityId = referringFacilityId;
        this.referredToFacilityId = referredToFacilityId;
        this.referralDate = referralDate;
        this.urgencyLevel = urgencyLevel;
        this.referralReason = referralReason;
        this.clinicalSummary = clinicalSummary;
        this.requestedInvestigations = requestedInvestigations;
        this.status = status;
        this.appointmentId = appointmentId;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
}


    /* =========================
       GETTERS (USED BY VIEW)
       ========================= */

/* =========================
   GETTERS (USED BY VIEW)
   ========================= */

public String getReferralId() {
    return referralId;
}

public String getPatientId() {
    return patientId;
}

public String getReferringClinicianId() {
    return referringClinicianId;
}

public String getReferredToClinicianId() {
    return referredToClinicianId;
}

public String getReferringFacilityId() {
    return referringFacilityId;
}

public String getReferredToFacilityId() {
    return referredToFacilityId;
}

public String getReferralDate() {
    return referralDate;
}

public String getUrgencyLevel() {
    return urgencyLevel;
}

public String getReferralReason() {
    return referralReason;
}

public String getClinicalSummary() {
    return clinicalSummary;
}

public String getRequestedInvestigations() {
    return requestedInvestigations;
}

public String getStatus() {
    return status;
}

public String getAppointmentId() {
    return appointmentId;
}

public String getNotes() {
    return notes;
}

public String getCreatedDate() {
    return createdDate;
}

public String getLastUpdated() {
    return lastUpdated;
}
}