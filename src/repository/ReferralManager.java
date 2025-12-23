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
 * Ensures only one instance exists (Singleton pattern).
 */
public class ReferralManager {

    private static ReferralManager instance;

    private static final String OUTPUT_DIR = "output/referrals";
    private static final String CSV_PATH = "data/referrals.csv";

    /** Private constructor (Singleton) */
    private ReferralManager() {}

    /** Returns the single instance */
    public static ReferralManager getInstance() {
        if (instance == null) {
            instance = new ReferralManager();
        }
        return instance;
    }

    /**
     * Processes a referral:
     *  - Appends to referrals.csv
     *  - Generates a referral text file
     */
    public void processReferral(Referral referral) throws IOException {

        if (referral == null) {
            throw new IllegalArgumentException("Referral cannot be null");
        }

        appendToCsv(referral);
        generateReferralTextFile(referral);
    }

    /** Append referral to CSV */
    private void appendToCsv(Referral r) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_PATH, true))) {
            writer.write(String.join(",",
                    safe(r.getReferralId()),
                    safe(r.getPatientNhsNumber()),
                    safe(r.getFromFacility()),
                    safe(r.getToFacility()),
                    safe(r.getUrgencyLevel()),
                    safe(r.getClinicalSummary()),
                    safe(r.getCreatedDate())
            ));
            writer.newLine();
        }
    }

    /** Generate referral output text file */
    private void generateReferralTextFile(Referral r) throws IOException {

        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) dir.mkdirs();

        String fileName = OUTPUT_DIR + "/referral_" + r.getReferralId() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            writer.write("REFERRAL NOTICE");
            writer.newLine();
            writer.write("---------------------------");
            writer.newLine();

            writer.write("Referral ID: " + r.getReferralId());
            writer.newLine();
            writer.write("Patient NHS: " + r.getPatientNhsNumber());
            writer.newLine();
            writer.write("From Facility: " + r.getFromFacility());
            writer.newLine();
            writer.write("To Facility: " + r.getToFacility());
            writer.newLine();
            writer.write("Urgency: " + r.getUrgencyLevel());
            writer.newLine();
            writer.newLine();
            writer.write("Clinical Summary:");
            writer.newLine();
            writer.write(r.getClinicalSummary());
            writer.newLine();
            writer.newLine();
            writer.write("Created: " + r.getCreatedDate());
        }
    }

    /** CSV safety */
    private String safe(String v) {
        return v == null ? "" : v.replace(",", " ");
    }
}
