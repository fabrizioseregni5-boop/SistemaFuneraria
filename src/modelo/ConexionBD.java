package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    // Esto creará un archivo llamado "funeraria.db" automáticamente en la carpeta de tu proyecto
    private static final String URL = "jdbc:sqlite:funeraria.db";

    public static Connection conectar() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("¡Conexión exitosa a la base de datos de la Funeraria!");
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        }
        return conn;
    }
    
    // Método principal para probar la conexión directamente
    public static void main(String[] args) {
        conectar();
    }
}