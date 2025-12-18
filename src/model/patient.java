package model;

/**
 * Patient
 * -------
 * Domain model representing a patient registered within the healthcare system.
 *
 * This class belongs to the Model layer of the MVC architecture and is responsible
 * only for holding patient-related data. It contains no business logic, file access,
 * or user interface code.
 *
 * Each Patient object corresponds to a single record in patients.csv.
 */
public class Patient {

    /**
     * NHS number is a unique identifier for a patient.
     * It is marked final to ensure it cannot be changed after creation.
     */
    private final String nhsNumber;

    /** Patient's given name */
    private String firstName;

    /** Patient's family name */
    private String lastName;

    /** Date of birth (stored as String to match CSV format) */
    private String dateOfBirth;

    /** Contact telephone number */
    private String phoneNumber;

    /** GP surgery where the patient is registered */
    private String registeredGpSurgery;

    /**
     * Constructs a Patient object.
     *
     * This constructor is typically called by the PatientRepository
     * when loading data from patients.csv.
     */
    public Patient(String nhsNumber,
                   String firstName,
                   String lastName,
                   String dateOfBirth,
                   String phoneNumber,
                   String registeredGpSurgery) {

        this.nhsNumber = nhsNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.registeredGpSurgery = registeredGpSurgery;
    }

    // ---------- Accessor methods ----------

    public String getNhsNumber() {
        return nhsNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRegisteredGpSurgery() {
        return registeredGpSurgery;
    }

    // ---------- Mutator methods ----------

    /**
     * Updates the patient's phone number.
     * Used when editing patient details via the GUI.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns a short readable representation of the patient.
     * Useful for displaying in GUI tables or logs.
     */
    @Override
    public String toString() {
        return nhsNumber + " - " + firstName + " " + lastName;
    }
}
