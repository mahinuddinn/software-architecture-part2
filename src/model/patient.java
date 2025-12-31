package model;

/**
 * Patient
 * -------
 * Domain model representing a patient.
 * MODEL layer in MVC.
 */
public class Patient {

    private String nhsNumber;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String phoneNumber;
    private String gender;
    private String registeredGpSurgery;

    public Patient(
            String nhsNumber,
            String firstName,
            String lastName,
            String dateOfBirth,
            String phoneNumber,
            String gender,
            String registeredGpSurgery
    ) {
        this.nhsNumber = nhsNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.registeredGpSurgery = registeredGpSurgery;
    }

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

    public String getGender() {
        return gender;
    }

    public String getRegisteredGpSurgery() {
        return registeredGpSurgery;
    }

    // =====================
// SETTERS (USED BY REPOSITORY FOR UPDATES)
// =====================

public void setFirstName(String firstName) {
    this.firstName = firstName;
}

public void setLastName(String lastName) {
    this.lastName = lastName;
}

public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
}

public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
}

public void setGender(String gender) {
    this.gender = gender;
}

public void setRegisteredGpSurgery(String registeredGpSurgery) {
    this.registeredGpSurgery = registeredGpSurgery;
}

}
