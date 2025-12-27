package model;

/**
 * Referral
 * --------
 * Domain model representing a referral from primary care
 * to secondary care.
 *
 * This class is part of the MODEL layer in MVC.
 * It contains only data + getters (NO logic).
 */
public class Referral {

    /* =========================
       CORE REFERRAL FIELDS
       ========================= */

    private String referralId;
    private String patientNhsNumber;

    /** Clinician who made the referral */
    private String referringClinicianId;

    /** Facilities involved */
    private String fromFacilityId;
    private String toFacilityId;

    /** Clinical information */
    private String clinicalSummary;
    private String urgencyLevel;

    /** Date referral was created */
    private String referralDate;

    /**
     * FULL constructor
     * ----------------
     * This constructor EXACTLY matches how MainFrame creates referrals.
     */
    public Referral(
            String referralId,
            String patientNhsNumber,
            String referringClinicianId,
            String fromFacilityId,
            String toFacilityId,
            String clinicalSummary,
            String urgencyLevel,
            String referralDate
    ) {
        this.referralId = referralId;
        this.patientNhsNumber = patientNhsNumber;
        this.referringClinicianId = referringClinicianId;
        this.fromFacilityId = fromFacilityId;
        this.toFacilityId = toFacilityId;
        this.clinicalSummary = clinicalSummary;
        this.urgencyLevel = urgencyLevel;
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

    public String getClinicalSummary() {
        return clinicalSummary;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public String getReferralDate() {
        return referralDate;
    }
}
