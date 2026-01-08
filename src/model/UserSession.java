package model;

/**
 * UserSession
 * ----------
 * Maintains details of the currently logged-in user.
 * Acts as a simple session holder (Singleton-style usage).
 */
public class UserSession {

    private static String userId;
    private static String role;
    private static boolean loggedIn = false;

    // Prevent instantiation
    private UserSession() {}

    /**
     * Starts a new user session
     */
    public static void startSession(String id, String userRole) {
        userId = id;
        role = userRole;
        loggedIn = true;
    }

    /**
     * Ends the current session
     */
    public static void endSession() {
        userId = null;
        role = null;
        loggedIn = false;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getRole() {
        return role;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }
}
