package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class datos {

    private static final String URL = "jdbc:mysql://localhost:3306/amazon";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    public static Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver no encontrado: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Error conexión: " + e.getMessage());
            return null;
        }
    }
}
