package model;

/**
 * Prescription
 * ------------
 * Represents a medical prescription issued to a patient by a clinician.
 *
 * Prescriptions can be exported to a text file to simulate pharmacy processing.
 */
public class Prescription {

    /** Unique prescription identifier */
    private String prescriptionId;

    /** Patient NHS number */
    private String patientNhsNumber;

    /** Issuing clinician */
    private String clinicianId;

    /** Name of medication */
    private String medication;

    /** Dosage instructions */
    private String dosage;

    /** Pharmacy where prescription is collected */
    private String pharmacy;

    /** Collection status (e.g. Pending, Collected) */
    private String collectionStatus;

    /**
     * Constructs a Prescription object.
     */
    public Prescription(String prescriptionId,
                        String patientNhsNumber,
                        String clinicianId,
                        String medication,
                        String dosage,
                        String pharmacy,
                        String collectionStatus) {
        this.prescriptionId = prescriptionId;
        this.patientNhsNumber = patientNhsNumber;
        this.clinicianId = clinicianId;
        this.medication = medication;
        this.dosage = dosage;
        this.pharmacy = pharmacy;
        this.collectionStatus = collectionStatus;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public String getPatientNhsNumber() {
        return patientNhsNumber;
    }

    public String getClinicianId() {
        return clinicianId;
    }

    public String getMedication() {
        return medication;
    }

    public String getDosage() {
        return dosage;
    }

    public String getPharmacy() {
        return pharmacy;
    }

    public String getCollectionStatus() {
        return collectionStatus;
    }
}
