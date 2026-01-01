package repository;

import model.Prescription;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PrescriptionRepository
 * ----------------------
 * Loads, stores, updates and deletes prescriptions.
 * Correctly maps CSV columns to model fields.
 */
public class PrescriptionRepository {

    private final List<Prescription> prescriptions = new ArrayList<>();
    private String sourceFilePath;

    /**
     * Loads prescriptions from CSV.
     */
    public void load(String filePath) throws IOException {

        this.sourceFilePath = filePath;
        prescriptions.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {

                String[] cols = line.split(",", -1);
                if (cols.length < 7) continue;

                // ✅ EXACT CSV → MODEL MAPPING
                Prescription p = new Prescription(
                        cols[0].trim(), // prescriptionId
                        cols[1].trim(), // patientNhsNumber
                        cols[2].trim(), // clinicianId
                        cols[3].trim(), // medication
                        cols[4].trim(), // dosage
                        cols[5].trim(), // pharmacy
                        cols[6].trim()  // collectionStatus
                );

                prescriptions.add(p);
            }
        }
    }

    public List<Prescription> getAll() {
        return new ArrayList<>(prescriptions);
    }

    public void addPrescription(Prescription p) throws IOException {
        prescriptions.add(p);
        saveToCsv();
    }

    public void updatePrescription(Prescription updated) throws IOException {
        for (int i = 0; i < prescriptions.size(); i++) {
            if (prescriptions.get(i).getPrescriptionId()
                    .equalsIgnoreCase(updated.getPrescriptionId())) {
                prescriptions.set(i, updated);
                saveToCsv();
                return;
            }
        }
        throw new IllegalArgumentException("Prescription not found.");
    }

    public void deletePrescription(String id) throws IOException {
        prescriptions.removeIf(p -> p.getPrescriptionId().equalsIgnoreCase(id));
        saveToCsv();
    }

    private void saveToCsv() throws IOException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(sourceFilePath))) {

            bw.write("prescriptionId,patientNhsNumber,clinicianId,medication,dosage,pharmacy,collectionStatus");
            bw.newLine();

            for (Prescription p : prescriptions) {
                bw.write(String.join(",",
                        safe(p.getPrescriptionId()),
                        safe(p.getPatientNhsNumber()),
                        safe(p.getClinicianId()),
                        safe(p.getMedication()),
                        safe(p.getDosage()),
                        safe(p.getPharmacy()),
                        safe(p.getCollectionStatus())
                ));
                bw.newLine();
            }
        }
    }

    private String safe(String v) {
        return v == null ? "" : v.replace(",", " ");
    }
}
