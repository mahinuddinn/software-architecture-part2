package model;

/**
 * Prescription
 * ------------
 * Domain model representing a medical prescription.
 *
 * This class belongs to the MODEL layer in the MVC architecture.
 * Its responsibility is to:
 *  - Store prescription data
 *  - Provide getters (and setters if needed)
 *
 * IMPORTANT:
 *  - NO file I/O
 *  - NO GUI logic
 *  - NO business rules
 *
 * The structure of this class aligns directly with:
 *  - prescriptions.csv
 *  - PrescriptionRepository
 *  - MainFrame (GUI table)
 */
public class Prescription {

    /* =========================
       CORE PRESCRIPTION FIELDS
       ========================= */

    /** Unique identifier for the prescription (e.g. RX001) */
    private String prescriptionId;

    /** NHS number of the patient receiving the prescription */
    private String patientNhsNumber;

    /** Clinician ID who issued the prescription */
    private String clinicianId;

    /**
     * Medication name or code.
     * IMPORTANT:
     *  - This is called "medication" (NOT medicationName)
     *  - MainFrame MUST call getMedication()
     */
    private String medication;

    /** Dosage instructions (e.g. 20mg, 500mg, 400mcg) */
    private String dosage;

    /** Pharmacy where prescription is collected (e.g. Boots Pharmacy) */
    private String pharmacy;

    /** Collection status (e.g. Pending, Collected, Issued) */
    private String collectionStatus;

    /* =========================
       CONSTRUCTOR
       ========================= */

    /**
     * Full constructor.
     *
     * This constructor matches:
     *  - CSV column order
     *  - Repository creation
     *  - GUI creation/editing
     */
    public Prescription(
            String prescriptionId,
            String patientNhsNumber,
            String clinicianId,
            String medication,
            String dosage,
            String pharmacy,
            String collectionStatus
    ) {
        this.prescriptionId = prescriptionId;
        this.patientNhsNumber = patientNhsNumber;
        this.clinicianId = clinicianId;
        this.medication = medication;
        this.dosage = dosage;
        this.pharmacy = pharmacy;
        this.collectionStatus = collectionStatus;
    }

    /* =========================
       GETTERS
       ========================= */

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public String getPatientNhsNumber() {
        return patientNhsNumber;
    }

    public String getClinicianId() {
        return clinicianId;
    }

    /**
     * Getter used by MainFrame.
     * DO NOT rename unless you update MainFrame too.
     */
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

    /* =========================
       SETTERS (OPTIONAL)
       =========================
       These are useful for editing prescriptions
       via the GUI (Edit button).
     */

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setPharmacy(String pharmacy) {
        this.pharmacy = pharmacy;
    }

    public void setCollectionStatus(String collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    /* =========================
       DEBUG / DISPLAY
       ========================= */

    @Override
    public String toString() {
        return prescriptionId + " | " + medication + " | " + dosage;
    }
}
