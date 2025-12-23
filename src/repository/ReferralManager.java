package repository;

import model.Referral;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * ReferralManager (Singleton)
 * ---------------------------
 * Handles referral processing and output generation.
 *
 * Demonstrates correct use of the Singleton pattern
 * as required by the assignment.
 */
public class ReferralManager {

    private static ReferralManager instance;

    private static final String OUTPUT_DIR = "output/referrals";

    private ReferralManager() {}

    /**
     * Returns the single instance of ReferralManager.
     */
    public static ReferralManager getInstance() {
        if (instance == null) {
            instance = new ReferralManager();
        }
        return instance;
    }

    /**
     * Processes a referral by generating a text file
     * representing a referral notification.
     */
    public void processReferral(Referral referral) throws IOException {

        if (referral == null) {
            throw new IllegalArgumentException("Referral cannot be null");
        }

        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) dir.mkdirs();

        String fileName = OUTPUT_DIR + "/referral_" +
                referral.getReferralId() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            writer.write("REFERRAL NOTICE");
            writer.newLine();
            writer.write("----------------------------");
            writer.newLine();

            writer.write("Referral ID: " + referral.getReferralId());
            writer.newLine();
            writer.write("Referring Clinician: " + referral.getReferringClinicianId());
            writer.newLine();
            writer.write("From Facility: " + referral.getReferringFacilityId());
            writer.newLine();
            writer.write("To Facility: " + referral.getReferredToFacilityId());
            writer.newLine();
            writer.write("Urgency: " + referral.getUrgencyLevel());
            writer.newLine();
            writer.newLine();

            writer.write("Clinical Summary:");
            writer.newLine();
            writer.write(referral.getClinicalSummary());
            writer.newLine();
            writer.newLine();

            writer.write("Created Date: " + referral.getCreatedDate());
        }
    }
}
