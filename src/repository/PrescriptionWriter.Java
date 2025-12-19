package repository;

import model.Prescription;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * PrescriptionWriter
 * ------------------
 * Responsible for generating a text file representing a prescription.
 * This is used to satisfy the "output text file" requirement in the rubric.
 *
 * Example output:
 * output/prescriptions/prescription_RX001.txt
 */
public class PrescriptionWriter {

    /** Output directory for prescription text files */
    private static final String OUTPUT_DIR = "output/prescriptions";

    /**
     * Writes a formatted prescription text file for the provided prescription.
     *
     * @param prescription the prescription to write
     * @throws IOException if file writing fails
     */
    public static void writePrescriptionTextFile(Prescription prescription) throws IOException {
        if (prescription == null) {
            throw new IllegalArgumentException("Prescription cannot be null");
        }

        // Ensure output directory exists
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = OUTPUT_DIR + "/prescription_" + prescription.getPrescriptionId() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write("PRESCRIPTION");
            writer.newLine();
            writer.write("----------------------------------");
            writer.newLine();

            writer.write("Prescription ID: " + prescription.getPrescriptionId());
            writer.newLine();

            writer.write("Patient NHS Number: " + prescription.getPatientNhsNumber());
            writer.newLine();

            writer.write("Clinician ID: " + prescription.getClinicianId());
            writer.newLine();

            writer.newLine();
            writer.write("Medication: " + prescription.getMedication());
            writer.newLine();

            writer.write("Dosage: " + prescription.getDosage());
            writer.newLine();

            writer.write("Pharmacy: " + prescription.getPharmacy());
            writer.newLine();

            writer.write("Collection Status: " + prescription.getCollectionStatus());
            writer.newLine();

            writer.newLine();
            writer.write("Generated: " + LocalDateTime.now());
        }
    }
}
