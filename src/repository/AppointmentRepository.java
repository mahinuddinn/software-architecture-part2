package repository;

import model.Appointment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentRepository
 * ---------------------
 * Loads appointment records from appointments.csv and stores them in memory.
 *
 * Appointments create an association between patients and clinicians
 * using NHS numbers and clinician IDs.
 */
public class AppointmentRepository {

    /**
     * In-memory list of appointments.
     */
    private final List<Appointment> appointments = new ArrayList<>();

    /**
     * Loads appointments from a CSV file.
     *
     * @param filePath path to appointments.csv (e.g. "data/appointments.csv")
     * @throws IOException if file cannot be read
     */
    public void load(String filePath) throws IOException {

        appointments.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Skip header row
            String header = reader.readLine();
            if (header == null) return;

            String line;
            while ((line = reader.readLine()) != null) {

                String[] cols = CsvUtil.splitCsvLine(line);

                /*
                 * Expected appointments.csv column order:
                 * 0 = appointmentId
                 * 1 = patientNhsNumber
                 * 2 = clinicianId
                 * 3 = dateTime
                 * 4 = reason
                 * 5 = status
                 */
                String appointmentId = CsvUtil.get(cols, 0);
                String patientNhs = CsvUtil.get(cols, 1);
                String clinicianId = CsvUtil.get(cols, 2);
                String dateTime = CsvUtil.get(cols, 3);
                String reason = CsvUtil.get(cols, 4);
                String status = CsvUtil.get(cols, 5);

                if (appointmentId.isEmpty()) continue;

                Appointment appointment = new Appointment(
                        appointmentId,
                        patientNhs,
                        clinicianId,
                        dateTime,
                        reason,
                        status
                );

                appointments.add(appointment);
            }
        }
    }

    /**
     * Returns a copy of all loaded appointments.
     */
    public List<Appointment> getAll() {
        return new ArrayList<>(appointments);
    }
}
