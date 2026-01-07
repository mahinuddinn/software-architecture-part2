package model;

/**
 * Stores the currently logged-in user.
 * Used for role-based access control across the system.
 */
public class UserSession {

    private static String userId;
    private static UserRole role;

    public static void login(String id, UserRole userRole) {
        userId = id;
        role = userRole;
    }

    public static String getUserId() {
        return userId;
    }

    public static UserRole getRole() {
        return role;
    }

    public static void logout() {
        userId = null;
        role = null;
    }
}
