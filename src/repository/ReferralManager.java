package repository;

import model.Referral;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * ReferralManager (Singleton)
 * ---------------------------
 * Responsible for managing patient referrals between
 * primary and secondary care.
 *
 * This class uses the Singleton pattern to ensure that
 * only one referral manager exists during application runtime.
 * This prevents duplicate referrals and ensures consistent
 * referral processing and audit trails.
 */
public class ReferralManager {

    /** The single instance of ReferralManager */
    private static ReferralManager instance;

    /** Path to referrals CSV file */
    private final String referralCsvPath = "data/referrals.csv";

    /**
     * Private constructor prevents external instantiation.
     */
    private ReferralManager() {
    }

    /**
     * Returns the single instance of ReferralManager.
     *
     * @return ReferralManager instance
     */
    public static ReferralManager getInstance() {
        if (instance == null) {
            instance = new ReferralManager();
        }
        return instance;
    }

    /**
     * Creates a referral, writes it to referrals.csv,
     * and generates a text file representing the referral email.
     *
     * @param referral Referral to process
     * @throws IOException if file writing fails
     */
    public void processReferral(Referral referral) throws IOException {

        if (referral == null) {
            throw new IllegalArgumentException("Referral cannot be null");
        }

        appendReferralToCsv(referral);
        generateReferralTextFile(referral);
    }

    /**
     * Appends a referral record to referrals.csv.
     */
    private void appendReferralToCsv(Referral referral) throws IOException {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(referralCsvPath, true))) {

            writer.write(String.join(",",
                    safe(referral.getReferralId()),
                    safe(referral.getPatientNhsNumber()),
                    safe(referral.getFromFacility()),
                    safe(referral.getToFacility()),
                    safe(referral.getClinicalSummary()),
                    safe(referral.getUrgencyLevel()),
                    LocalDateTime.now().toString()
            ));
            writer.newLine();
        }
    }

    /**
     * Generates a text file representing a referral email.
     * This simulates electronic referral communication.
     */
    private void generateReferralTextFile(Referral referral) throws IOException {

        String filename = "referral_" + referral.getReferralId() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write("REFERRAL NOTICE");
            writer.newLine();
            writer.write("----------------------------");
            writer.newLine();
            writer.write("Referral ID: " + referral.getReferralId());
            writer.newLine();
            writer.write("Patient NHS Number: " + referral.getPatientNhsNumber());
            writer.newLine();
            writer.write("From Facility: " + referral.getFromFacility());
            writer.newLine();
            writer.write("Referred To: " + referral.getToFacility());
            writer.newLine();
            writer.write("Urgency Level: " + referral.getUrgencyLevel());
            writer.newLine();
            writer.newLine();
            writer.write("Clinical Summary:");
            writer.newLine();
            writer.write(referral.getClinicalSummary());
        }
    }

    /**
     * Sanitises values before writing to CSV.
     */
    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
