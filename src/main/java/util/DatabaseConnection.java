package util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection
{
    private static final Dotenv dotenv = Dotenv.load();
    private static final String url = dotenv.get("DB_URL");
    private static final String user = dotenv.get("DB_USER");
    private static final String password = dotenv.get("DB_PASSWORD");

// Ovanstående är final då de aldrig ändras
// De är static då de aldrig instansieras

    public static Connection getConnection()throws SQLException{
        return DriverManager.getConnection(url, user, password);
    }
    //metoden som kallas genom DatabaseConnection.getConnection i DAO-klasserna för att upprätta anslutning

}
