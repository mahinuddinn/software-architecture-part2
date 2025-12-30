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
 * Loads referrals from referrals.csv and stores them in memory.
 *
 * This repository is READ-ONLY (CSV → memory).
 * New referrals are handled via ReferralManager (Singleton).
 */
public class ReferralRepository {

    /** In-memory referral list */
    private final List<Referral> referrals = new ArrayList<>();

    /**
     * Loads referrals from CSV.
     */
    public void load(String filePath) throws IOException {

        referrals.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String header = br.readLine(); // skip header
            if (header == null) return;

            String line;
            while ((line = br.readLine()) != null) {

                String[] cols = line.split(",", -1);

                // Safety check (CSV has many columns)
                if (cols.length < 11) continue;

        Referral referral = new Referral(
            cols[0].trim(),  // referral_id
            cols[1].trim(),  // patient_id
            cols[2].trim(),  // referring_clinician_id
            cols[4].trim(),  // referring_facility_id
            cols[5].trim(),  // referred_to_facility_id
            cols[9].trim(),  // clinical_summary
            cols[7].trim(),  // urgency_level
            cols[6].trim(),  // referral_date
            cols[8].trim(),  // referral_reason
            cols[10].trim(), // requested_investigations
            cols[11].trim(), // status
            cols[13].trim()  // notes
);


                referrals.add(referral);
            }
        }
    }

    /**
     * Returns all referrals.
     */
    public List<Referral> getAll() {
        return new ArrayList<>(referrals);
    }
}
