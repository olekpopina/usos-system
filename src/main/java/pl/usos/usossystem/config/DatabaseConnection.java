package pl.usos.usossystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/usos_db";
    private static final String USER = "root";
    private static final String PASSWORD = "usos";

    public static Connection connect() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Polaczono z baza danych MySQL.");
            return connection;
        } catch (SQLException e) {
            System.out.println("Blad polaczenia z baza danych.");
            e.printStackTrace();
            return null;
        }
    }
}