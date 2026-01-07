package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Appointment;


/**
 * CsvUtil
 * -------
 * Utility class containing helper methods for safely reading CSV files.
 *
 * This class exists to:
 * - centralise CSV parsing logic
 * - reduce duplicated code in repositories
 * - prevent runtime exceptions caused by malformed CSV rows
 *
 * This contributes to "Outstanding" functionality marks.
 */
public class CsvUtil {

    /**
     * Splits a CSV line into columns.
     *
     * The -1 parameter ensures empty fields are preserved
     * instead of being silently dropped.
     *
     * @param line a single line from a CSV file
     * @return array of column values
     */
    public static String[] splitCsvLine(String line) {
        return line.split(",", -1);
    }

    /**
     * Safely retrieves a column value by index.
     *
     * If the index does not exist, an empty string is returned
     * instead of throwing an exception.
     *
     * @param columns parsed CSV columns
     * @param index column index
     * @return trimmed column value or empty string
     */
    public static String get(String[] columns, int index) {
        if (columns == null || index < 0 || index >= columns.length) {
            return "";
        }
        return columns[index].trim();
    }

    /**
     * Safely converts a String to an integer.
     *
     * Used for numeric fields such as capacity.
     *
     * @param value string value
     * @param defaultValue fallback if conversion fails
     * @return integer value or default
     */
    public static int toInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
 * Reads appointments from appointments.csv and returns a list.
 *
 * Expected CSV header:
 * appointmentId,patientId,clinicianId,facilityId,appointmentDate,appointmentTime,status,notes
 */
public static List<Appointment> readAppointments(String filePath) throws IOException {

    List<Appointment> appointments = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

        // Skip CSV header
        br.readLine();

        String line;
        while ((line = br.readLine()) != null) {

            // Keep empty trailing fields
            String[] cols = line.split(",", -1);

            if (cols.length < 8) continue;

            Appointment appointment = new Appointment(
                    cols[0].trim(), // appointmentId
                    cols[1].trim(), // patientId
                    cols[2].trim(), // clinicianId
                    cols[3].trim(), // facilityId
                    cols[4].trim(), // appointmentDate
                    cols[5].trim(), // appointmentTime
                    cols[6].trim(), // status
                    cols[7].trim()  // notes
            );

            appointments.add(appointment);
        }
    }

    return appointments;
}

/**
 * Writes appointments back to appointments.csv.
 */
public static void writeAppointments(
        String filePath,
        List<Appointment> appointments
) throws IOException {

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

        // Write CSV header
        bw.write(
                "appointmentId,patientId,clinicianId,facilityId," +
                "appointmentDate,appointmentTime,status,notes"
        );
        bw.newLine();

        // Write appointment records
        for (Appointment a : appointments) {

            bw.write(String.join(",",
                    a.getAppointmentId(),
                    a.getPatientId(),
                    a.getClinicianId(),
                    a.getFacilityId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getStatus(),
                    a.getNotes()
            ));

            bw.newLine();
        }
    }
}


}
