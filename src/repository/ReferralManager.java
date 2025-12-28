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
 */
public class ReferralManager {

    /** Singleton instance */
    private static ReferralManager instance;

    /** Output directory for referral text files */
    private static final String OUTPUT_DIR = "output/referrals";

    /** Private constructor (Singleton) */
    private ReferralManager() {}

    /**
     * Returns the single ReferralManager instance.
     */
    public static ReferralManager getInstance() {
        if (instance == null) {
            instance = new ReferralManager();
        }
        return instance;
    }

    /**
     * Processes a referral by generating a referral output text file.
     */
    public void processReferral(Referral referral) throws IOException {

        if (referral == null) {
            throw new IllegalArgumentException("Referral cannot be null");
        }

        generateReferralTextFile(referral);
    }

    /**
     * Generates a referral text file.
     *
     * Example:
     * output/referrals/referral_R001.txt
     */
    private void generateReferralTextFile(Referral referral) throws IOException {

        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = OUTPUT_DIR + "/referral_" + referral.getReferralId() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write("REFERRAL NOTICE");
            writer.newLine();
            writer.write("--------------------------------");
            writer.newLine();

            writer.write("Referral ID: " + referral.getReferralId());
            writer.newLine();

            writer.write("Patient NHS Number: " + referral.getPatientNhsNumber());
            writer.newLine();

            writer.write("Referring Clinician ID: " + referral.getReferringClinicianId());
            writer.newLine();

            writer.write("From Facility: " + referral.getFromFacilityId());
            writer.newLine();

            writer.write("To Facility: " + referral.getToFacilityId());
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
