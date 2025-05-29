package util;

import model.User;

public class SessionManager {
    private static User loggedInUser;

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public static void logout() {
        loggedInUser = null;
    }

    public static void clear() {
    }
}