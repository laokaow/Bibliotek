package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOFactory {
    private static Connection connection;
    private static UserDAO userDAO;

    public static void init(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        userDAO = new UserDAO(connection);
    }

    public static UserDAO getUserDAO() {
        return userDAO;
    }
}