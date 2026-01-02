package repository;

import model.Referral;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ReferralRepository
 * ------------------
 * Handles loading, storing, updating, and deleting referrals.
 *
 * Responsibilities:
 *  - Load referrals from referrals.csv
 *  - Provide access to referral data
 *  - Persist changes back to CSV
 *
 * PART OF MODEL LAYER (MVC)
 */
public class ReferralRepository {

    /** In-memory referral list */
    private final List<Referral> referrals = new ArrayList<>();

    /** CSV source path (set when load() is called) */
    private String sourceFilePath;

    /* =====================================================
       LOAD
       ===================================================== */

    /**
     * Loads referrals from CSV into memory.
     */
public void load(String filePath) throws IOException {

    referrals.clear();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

        String header = br.readLine(); // skip header
        if (header == null) return;

        String line;
        while ((line = br.readLine()) != null) {

            String[] cols = line.split(",", -1);

            // Must have at least 12 columns
            if (cols.length < 12) continue;

            Referral referral = new Referral(
                    cols[0].trim(),  // referralId
                    cols[1].trim(),  // patientId
                    cols[2].trim(),  // referringClinicianId
                    cols[3].trim(),  // fromFacilityId
                    cols[4].trim(),  // toFacilityId
                    cols[8].trim(),  // clinicalSummary
                    cols[6].trim(),  // urgency
                    cols[5].trim(),  // referralDate
                    cols[7].trim(),  // referralReason
                    cols[9].trim(),  // investigations
                    cols[10].trim(), // status
                    cols[11].trim()  // notes
            );

            referrals.add(referral);
        }
    }
}


    /* =====================================================
       ACCESS
       ===================================================== */

    /**
     * Returns all referrals (defensive copy).
     */
    public List<Referral> getAll() {
        return new ArrayList<>(referrals);
    }

    /* =====================================================
       UPDATE
       ===================================================== */

    /**
     * Updates an existing referral (matched by referralId).
     */
    public void updateReferral(Referral updated) throws IOException {

        for (int i = 0; i < referrals.size(); i++) {
            if (referrals.get(i).getReferralId()
                    .equalsIgnoreCase(updated.getReferralId())) {

                referrals.set(i, updated);
                saveToCsv();
                return;
            }
        }

        throw new IllegalArgumentException("Referral not found.");
    }

    /* =====================================================
       DELETE
       ===================================================== */

    /**
     * Deletes a referral by referral ID.
     */
    public void deleteReferral(String referralId) throws IOException {

        boolean removed = referrals.removeIf(r ->
                r.getReferralId().equalsIgnoreCase(referralId));

        if (!removed) {
            throw new IllegalArgumentException("Referral not found.");
        }

        saveToCsv();
    }

    /* =====================================================
       CSV PERSISTENCE
       ===================================================== */

    /**
     * Persists all referrals back to the CSV file.
     */
    private void saveToCsv() throws IOException {

        if (sourceFilePath == null) {
            throw new IllegalStateException("CSV file path not set. Call load() first.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath))) {

            // Header MUST match original CSV
            writer.write(
                "referral_id,patient_id,referring_clinician_id,referred_to_clinician_id," +
                "referring_facility_id,referred_to_facility_id,referral_date,urgency_level," +
                "referral_reason,clinical_summary,requested_investigations,status," +
                "appointment_id,notes,created_date,last_updated"
            );
            writer.newLine();

            for (Referral r : referrals) {
                writer.write(String.join(",",
                        safe(r.getReferralId()),
                        safe(r.getPatientNhsNumber()),
                        safe(r.getReferringClinicianId()),
                        "", // referred_to_clinician_id (optional)
                        safe(r.getFromFacilityId()),
                        safe(r.getToFacilityId()),
                        safe(r.getReferralDate()),
                        safe(r.getUrgencyLevel()),
                        safe(r.getReferralReason()),
                        safe(r.getClinicalSummary()),
                        safe(r.getRequestedInvestigations()),
                        safe(r.getStatus()),
                        "", // appointment_id
                        safe(r.getNotes()),
                        safe(r.getReferralDate()), // created_date
                        ""  // last_updated
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Prevents commas from breaking CSV structure.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
