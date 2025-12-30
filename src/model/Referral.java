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

    /** Unique referral identifier (e.g. R001) */
    private String referralId;

    /** NHS number of the patient */
    private String patientNhsNumber;

    /** Clinician who initiated the referral */
    private String referringClinicianId;

    /* =========================
       FACILITY DETAILS
       ========================= */

    /** Facility referring from (e.g. GP surgery / clinic) */
    private String fromFacilityId;

    /** Facility referred to (e.g. hospital / department) */
    private String toFacilityId;

    /* =========================
       CLINICAL DETAILS
       ========================= */

    /** Short reason for referral */
    private String referralReason;

    /** Detailed clinical summary */
    private String clinicalSummary;

    /** Requested tests or investigations */
    private String requestedInvestigations;

    /** Urgency level (Routine / Urgent / Non-urgent) */
    private String urgencyLevel;

    /** Current referral status (Pending / Completed / New) */
    private String status;

    /** Free-text notes */
    private String notes;

    /* =========================
       DATES
       ========================= */

    /** Date the referral was created */
    private String referralDate;

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
            String patientNhsNumber,
            String referringClinicianId,
            String fromFacilityId,
            String toFacilityId,
            String referralReason,
            String clinicalSummary,
            String requestedInvestigations,
            String urgencyLevel,
            String status,
            String notes,
            String referralDate
    ) {
        this.referralId = referralId;
        this.patientNhsNumber = patientNhsNumber;
        this.referringClinicianId = referringClinicianId;
        this.fromFacilityId = fromFacilityId;
        this.toFacilityId = toFacilityId;
        this.referralReason = referralReason;
        this.clinicalSummary = clinicalSummary;
        this.requestedInvestigations = requestedInvestigations;
        this.urgencyLevel = urgencyLevel;
        this.status = status;
        this.notes = notes;
        this.referralDate = referralDate;
    }

    /* =========================
       GETTERS (USED BY VIEW)
       ========================= */

    public String getReferralId() {
        return referralId;
    }

    public String getPatientNhsNumber() {
        return patientNhsNumber;
    }

    public String getReferringClinicianId() {
        return referringClinicianId;
    }

    public String getFromFacilityId() {
        return fromFacilityId;
    }

    public String getToFacilityId() {
        return toFacilityId;
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

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getReferralDate() {
        return referralDate;
    }
}
