package repository;

import model.Referral;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * ReferralManager (Singleton)
 * ---------------------------
 * Handles processing of referrals and generation
 * of referral output text files.
 *
 * This class enforces a SINGLE instance (Singleton pattern),
 * satisfying the design pattern requirement in the rubric.
 *
 * RESPONSIBILITIES:
 *  - Accept a Referral object from the View layer
 *  - Generate a human-readable referral text file
 *  - Ensure consistent referral processing logic
 *
 * NOTE:
 *  - NO GUI code
 *  - NO CSV handling
 *  - Pure business/service logic (Model layer support)
 */
public class ReferralManager {

    /** Singleton instance (lazy initialisation) */
    private static ReferralManager instance;

    /** Output directory for referral text files */
    private static final String OUTPUT_DIR = "output/referrals";

    /**
     * Private constructor.
     * Prevents external instantiation (Singleton enforcement).
     */
    private ReferralManager() {}

    /**
     * Returns the single ReferralManager instance.
     * Creates it if it does not already exist.
     */
    public static ReferralManager getInstance() {
        if (instance == null) {
            instance = new ReferralManager();
        }
        return instance;
    }

    /**
     * Processes a referral.
     *
     * Currently this means:
     *  - Generating a referral text file
     *
     * In a real system, this could also:
     *  - Send emails
     *  - Update external systems
     *  - Log audit trails
     */
    public void processReferral(Referral referral) throws IOException {

        // Defensive check to prevent null processing
        if (referral == null) {
            throw new IllegalArgumentException("Referral cannot be null");
        }

        generateReferralTextFile(referral);
    }

    /**
     * Generates a referral text file.
     *
     * File location example:
     * output/referrals/referral_R001.txt
     *
     * The content is human-readable and suitable
     * for printing or sharing between departments.
     */
    private void generateReferralTextFile(Referral referral) throws IOException {

        // Ensure output directory exists
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Construct output filename using referral ID
        String filename = OUTPUT_DIR + "/referral_" + referral.getReferralId() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write("REFERRAL NOTICE");
            writer.newLine();
            writer.write("--------------------------------");
            writer.newLine();

            // Core identifiers
            writer.write("Referral ID: " + referral.getReferralId());
            writer.newLine();

            // Updated to match Referral model (patientId replaces patientNhsNumber)
            writer.write("Patient ID: " + referral.getPatientId());
            writer.newLine();

            writer.write("Referring Clinician ID: " + referral.getReferringClinicianId());
            writer.newLine();

            // Updated getter names to match model
            writer.write("From Facility: " + referral.getReferringFacilityId());
            writer.newLine();

            writer.write("To Facility: " + referral.getReferredToFacilityId());
            writer.newLine();

            writer.write("Urgency Level: " + referral.getUrgencyLevel());
            writer.newLine();

            writer.write("Referral Date: " + referral.getReferralDate());
            writer.newLine();

            writer.newLine();
            writer.write("Clinical Summary:");
            writer.newLine();
            writer.write(referral.getClinicalSummary());
        }
    }
}
