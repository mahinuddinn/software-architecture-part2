package repository;

import model.Referral;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ReferralRepository
 * ------------------
 * Loads referral records from referrals.csv
 * and stores them in memory for GUI display.
 *
 * This is part of the MODEL layer in MVC.
 */
public class ReferralRepository {

    /** In-memory list of referrals */
    private final List<Referral> referrals = new ArrayList<>();

    /**
     * Loads referrals from CSV file.
     *
     * @param filePath path to referrals.csv
     */
    public void load(String filePath) throws IOException {

        referrals.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Skip header
            String header = br.readLine();
            if (header == null) return;

            String line;
            while ((line = br.readLine()) != null) {

                // 🔑 THIS LINE WAS MISSING BEFORE
                String[] cols = line.split(",", -1);

                // Defensive check (ignore broken rows)
                if (cols.length < 10) continue;

                /*
                 CSV order:
                 0 referral_id
                 1 patient_id
                 4 referring_facility_id
                 5 referred_to_facility_id
                 6 referral_date
                 7 urgency_level
                 9 clinical_summary
                */

                Referral referral = new Referral(
                        cols[0], // referral_id
                        cols[1], // patient_id
                        cols[4], // from_facility
                        cols[5], // to_facility
                        cols[9], // clinical_summary
                        cols[7], // urgency_level
                        cols[6]  // referral_date
                );

                referrals.add(referral);
            }
        }
    }

    /**
     * Returns all loaded referrals.
     */
    public List<Referral> getAll() {
        return new ArrayList<>(referrals);
    }
}
