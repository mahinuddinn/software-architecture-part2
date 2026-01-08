package repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * ReferralWriter
 * --------------
 * Persists simulated referral email content to a text file.
 */
public class ReferralWriter {

    private static final String OUTPUT_FILE = "referral_notifications.txt";

    public static void writeReferralEmail(String content) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(OUTPUT_FILE, true))) {

            writer.write(content);
        }
    }

    
}
