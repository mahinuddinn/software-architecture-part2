package repository;

import model.Appointment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentRepository
 * ---------------------
 * Responsible for loading, storing, updating, and persisting
 * Appointment records.
 *
 * This class represents the MODEL / DATA ACCESS layer
 * in the MVC architecture.
 *
 * ✔ Handles CSV load/save
 * ✔ Maintains in-memory list of appointments
 * ✔ Provides CRUD methods used by MainFrame
 * ✔ NO UI logic (Swing-free)
 */
public class AppointmentRepository {

    /* =========================================================
       IN-MEMORY DATA STORE
       ========================================================= */

    // List holding all appointments currently loaded
    private final List<Appointment> appointments = new ArrayList<>();

    // Path to appointments CSV file
    private String sourceFilePath;

    /* =========================================================
       LOAD
       ========================================================= */

    /**
     * Loads appointments from CSV into memory.
     * This should be called ONCE at application startup.
     */
    public void load(String filePath) throws IOException {

        this.sourceFilePath = filePath;
        appointments.clear();

        // Use CsvUtil to load appointments
        appointments.addAll(
                CsvUtil.readAppointments(filePath)
        );
    }

    /* =========================================================
       READ
       ========================================================= */

    /**
     * Returns ALL appointments currently loaded.
     * Used by MainFrame to populate JTable.
     */
    public List<Appointment> getAll() {
        return appointments;
    }

    /**
     * Finds a single appointment by Appointment ID.
     */
    public Appointment getById(String appointmentId) {

        for (Appointment a : appointments) {
            if (a.getAppointmentId().equals(appointmentId)) {
                return a;
            }
        }
        return null;
    }

    /* =========================================================
       CREATE
       ========================================================= */

    /**
     * Adds a new appointment and persists it to CSV.
     */
    public void addAppointment(Appointment appointment) throws IOException {

        appointments.add(appointment);
        save(); // persist to CSV
    }

    /* =========================================================
       UPDATE
       ========================================================= */

    /**
     * Updates an existing appointment based on Appointment ID.
     */
    public void updateAppointment(Appointment updated) throws IOException {

        for (int i = 0; i < appointments.size(); i++) {

            if (appointments.get(i).getAppointmentId()
                    .equals(updated.getAppointmentId())) {

                appointments.set(i, updated);
                save(); // persist changes
                return;
            }
        }

        // Safety check (should never happen if UI is correct)
        throw new IllegalArgumentException(
                "Appointment not found: " + updated.getAppointmentId()
        );
    }

    /* =========================================================
       DELETE
       ========================================================= */

    /**
     * Deletes an appointment by Appointment ID.
     */
    public void deleteAppointment(String appointmentId) throws IOException {

        boolean removed = appointments.removeIf(
                a -> a.getAppointmentId().equals(appointmentId)
        );

        if (!removed) {
            throw new IllegalArgumentException(
                    "Appointment not found: " + appointmentId
            );
        }

        save(); // persist deletion
    }

    /* =========================================================
       SAVE (CSV PERSISTENCE)
       ========================================================= */

    /**
     * Writes the in-memory appointment list back to CSV.
     */
    private void save() throws IOException {

        // Safety check
        if (sourceFilePath == null) {
            throw new IllegalStateException(
                    "CSV file path not set. Did you forget to call load()?"
            );
        }

        CsvUtil.writeAppointments(sourceFilePath, appointments);
    }
}
