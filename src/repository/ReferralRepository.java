package repository;

import model.Referral;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * ReferralRepository
 * ------------------
 * Loads referral records from referrals.csv into memory
 * for display in the GUI.
 *
 * This class belongs to the MODEL layer of MVC.
 */
public class ReferralRepository {

    private final List<Referral> referrals = new ArrayList<>();

    /**
     * Loads referrals from the CSV file.
     *
     * @param filePath path to referrals.csv
     */
    public void load(String filePath) throws Exception {

        referrals.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // Skip header
            String header = br.readLine();
            if (header == null) return;

            String line;
            while ((line = br.readLine()) != null) {

                if (line.isBlank()) continue;

                String[] cols = CsvUtil.splitCsvLine(line);

                /*
                 * referrals.csv column mapping (based on provided file):
                 * 0  referral_id
                 * 1  referring_clinician_id
                 * 2  referred_to_clinician_id (ignored)
                 * 3  referring_facility_id
                 * 4  referred_to_facility_id
                 * 5  referral_date (ignored)
                 * 6  urgency_level
                 * 7  referral_reason (ignored)
                 * 8  clinical_summary
                 * ...
                 * 13 created_date
                 */

                Referral referral = new Referral(
                        CsvUtil.get(cols, 0),
                        CsvUtil.get(cols, 1),
                        CsvUtil.get(cols, 3),
                        CsvUtil.get(cols, 4),
                        CsvUtil.get(cols, 8),
                        CsvUtil.get(cols, 6),
                        CsvUtil.get(cols, 13)
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
