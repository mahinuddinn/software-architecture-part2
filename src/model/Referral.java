package model;

/**
 * Referral
 * --------
 * Represents a referral from primary care to secondary care.
 *
 * This is a PURE DOMAIN MODEL (MVC - Model layer).
 * It contains ONLY data + getters.
 */
public class Referral {

    private String referralId;
    private String patientNhsNumber;
    private String fromFacility;
    private String toFacility;
    private String clinicalSummary;
    private String urgencyLevel;
    private String createdDate;

    /**
     * Full constructor used by:
     *  - ReferralRepository (CSV loading)
     *  - MainFrame (GUI creation)
     *  - ReferralManager (Singleton processing)
     */
    public Referral(
            String referralId,
            String patientNhsNumber,
            String fromFacility,
            String toFacility,
            String clinicalSummary,
            String urgencyLevel,
            String createdDate
    ) {
        this.referralId = referralId;
        this.patientNhsNumber = patientNhsNumber;
        this.fromFacility = fromFacility;
        this.toFacility = toFacility;
        this.clinicalSummary = clinicalSummary;
        this.urgencyLevel = urgencyLevel;
        this.createdDate = createdDate;
    }

    public String getReferralId() {
        return referralId;
    }

    public String getPatientNhsNumber() {
        return patientNhsNumber;
    }

    public String getFromFacility() {
        return fromFacility;
    }

    public String getToFacility() {
        return toFacility;
    }

    public String getClinicalSummary() {
        return clinicalSummary;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public String getCreatedDate() {
        return createdDate;
    }
}
