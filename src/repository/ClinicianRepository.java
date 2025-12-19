package repository;

import model.Clinician;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClinicianRepository
 * -------------------
 * Responsible for loading clinician records from a CSV file
 * and storing them in memory for use by controllers and the GUI.
 *
 * This repository is intentionally read-only, as clinicians are
 * not created or modified by end users within the system.
 */
public class ClinicianRepository {

    /**
     * In-memory list of clinicians.
     */
    private final List<Clinician> clinicians = new ArrayList<>();

    /**
     * Loads clinicians from a CSV file.
     *
     * @param filePath path to clinicians.csv (e.g. "data/clinicians.csv")
     * @throws IOException if file cannot be read
     */
    public void load(String filePath) throws IOException {

        clinicians.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Skip header row
            String header = reader.readLine();
            if (header == null) return;

            String line;
            while ((line = reader.readLine()) != null) {

                String[] cols = CsvUtil.splitCsvLine(line);

                /*
                 * Expected clinicians.csv column order:
                 * 0 = clinicianId
                 * 1 = name
                 * 2 = role
                 * 3 = specialty
                 * 4 = workplace
                 */
                String id = CsvUtil.get(cols, 0);
                String name = CsvUtil.get(cols, 1);
                String role = CsvUtil.get(cols, 2);
                String specialty = CsvUtil.get(cols, 3);
                String workplace = CsvUtil.get(cols, 4);

                if (id.isEmpty()) continue;

                Clinician clinician =
                        new Clinician(id, name, role, specialty, workplace);

                clinicians.add(clinician);
            }
        }
    }

    /**
     * Returns a copy of all clinicians.
     */
    public List<Clinician> getAll() {
        return new ArrayList<>(clinicians);
    }
}
