package com.retailerp.util;

import com.retailerp.model.User;

/* Holds the currently logged-in user session.*/

public class SessionManager {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static int getUserId() {
        return currentUser != null ? currentUser.getUserId() : 0;
    }

    public static int getBranchId() {
        return currentUser != null ? currentUser.getBranchId() : 0;
    }

    public static String getRoleName() {
        return currentUser != null ? currentUser.getRoleName() : "";
    }

    public static boolean isAdmin() {
        return "Admin".equals(getRoleName());
    }

    public static boolean isManager() {
        return "Manager".equals(getRoleName());
    }

    public static void logout() {
        currentUser = null;
    }
}
