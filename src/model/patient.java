package model;

/**
 * Patient
 * -------
 * Domain model representing a patient record.
 *
 * MODEL layer in MVC.
 * Stores patient data only – no file I/O or GUI logic.
 */
public class Patient {

    /* =========================
       CORE PATIENT FIELDS
       ========================= */

    private String nhsNumber;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String phoneNumber;
    private String emergencyContactNumber;
    private String gender;
    private String address;
    private String postcode;
    private String email;
    private String registeredGpSurgery;

    /* =========================
       CONSTRUCTOR (FULL)
       MUST MATCH CSV EXACTLY
       ========================= */

    public Patient(
            String nhsNumber,
            String firstName,
            String lastName,
            String dateOfBirth,
            String phoneNumber,
            String emergencyContactNumber,
            String gender,
            String address,
            String postcode,
            String email,
            String registeredGpSurgery
    ) {
        this.nhsNumber = nhsNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.emergencyContactNumber = emergencyContactNumber;
        this.gender = gender;
        this.address = address;
        this.postcode = postcode;
        this.email = email;
        this.registeredGpSurgery = registeredGpSurgery;
    }

    /* =========================
       GETTERS
       ========================= */

    public String getNhsNumber() { return nhsNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmergencyContactNumber() { return emergencyContactNumber; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getPostcode() { return postcode; }
    public String getEmail() { return email; }
    public String getRegisteredGpSurgery() { return registeredGpSurgery; }

    /* =========================
       SETTERS (FOR EDIT)
       ========================= */

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
