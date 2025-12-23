package model;

/**
 * Referral
 * --------
 * Domain model representing a referral record.
 *
 * This class directly reflects the structure of referrals.csv
 * and is used by both:
 *  - ReferralRepository (loading/display)
 *  - ReferralManager (Singleton processing & output)
 */
public class Referral {

    private String referralId;
    private String referringClinicianId;
    private String referringFacilityId;
    private String referredToFacilityId;
    private String clinicalSummary;
    private String urgencyLevel;
    private String createdDate;

    public Referral(String referralId,
                    String referringClinicianId,
                    String referringFacilityId,
                    String referredToFacilityId,
                    String clinicalSummary,
                    String urgencyLevel,
                    String createdDate) {

        this.referralId = referralId;
        this.referringClinicianId = referringClinicianId;
        this.referringFacilityId = referringFacilityId;
        this.referredToFacilityId = referredToFacilityId;
        this.clinicalSummary = clinicalSummary;
        this.urgencyLevel = urgencyLevel;
        this.createdDate = createdDate;
    }

    public String getReferralId() {
        return referralId;
    }

    public String getReferringClinicianId() {
        return referringClinicianId;
    }

    public String getReferringFacilityId() {
        return referringFacilityId;
    }

    public String getReferredToFacilityId() {
        return referredToFacilityId;
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
