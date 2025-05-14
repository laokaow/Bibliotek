package service;

import dao.UserDAO;
import model.User;

public class LoginService {
    private final UserDAO userDAO;

    public LoginService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String email, String password) {
        User user = userDAO.authenticateUser(email, password);
        if (user != null) {
            System.out.println("Login successful!");
            return user;
        } else {
            System.out.println("Invalid credentials.");
            return null;
        }
    }
}