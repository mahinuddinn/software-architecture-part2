package model;

/**
 * UserSession
 * -----------
 * Holds the currently logged-in user's identity and role.
 * Acts as a simple session manager for role-based access control.
 */
public class UserSession {

    private static String userId;
    private static String role;

    /** Log a user into the system */
    public static void login(String id, String userRole) {
        userId = id;
        role = userRole;
    }

    /** Clear session (logout) */
    public static void logout() {
        userId = null;
        role = null;
    }

    /** Check if someone is logged in */
    public static boolean isLoggedIn() {
        return userId != null;
    }

    /** Get logged-in user ID */
    public static String getUserId() {
        return userId;
    }

    /** Get logged-in role */
    public static String getRole() {
        return role;
    }

    /** Role helpers (clean + readable) */
    public static boolean isPatient() {
        return "PATIENT".equalsIgnoreCase(role);
    }

    public static boolean isClinician() {
        return "CLINICIAN".equalsIgnoreCase(role);
    }

    public static boolean isDoctor() {
        return "DOCTOR".equalsIgnoreCase(role);
    }

    public static boolean isStaff() {
        return "STAFF".equalsIgnoreCase(role);
    }
}
