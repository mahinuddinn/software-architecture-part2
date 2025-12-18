package model;

/**
 * Referral
 * --------
 * Represents a referral from primary care to secondary care.
 *
 * Referral creation and processing is managed by a Singleton ReferralManager
 * to ensure consistency, prevent duplication, and maintain audit trails.
 */
public class Referral {

    /** Unique referral identifier */
    private String referralId;

    /** Patient NHS number */
    private String patientNhsNumber;

    /** Originating facility */
    private String fromFacility;

    /** Destination facility */
    private String toFacility;

    /** Clinical summary explaining reason for referral */
    private String clinicalSummary;

    /** Urgency level (Routine, Urgent, Emergency) */
    private String urgencyLevel;

    /** Date referral was created */
    private String createdDate;

    /**
     * Constructs a Referral object.
     */
    public Referral(String referralId,
                    String patientNhsNumber,
                    String fromFacility,
                    String toFacility,
                    String clinicalSummary,
                    String urgencyLevel,
                    String createdDate) {
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
