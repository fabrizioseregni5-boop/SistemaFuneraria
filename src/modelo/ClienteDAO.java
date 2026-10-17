package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    /**
     * Registra un cliente nuevo en la base de datos.
     * @return true si se insertó correctamente, false si hubo error.
     */
    public boolean insertarCliente(String cedula, String nombre, String telefono, String direccion) {
        String sql = "INSERT INTO clientes (cedula, nombre, telefono, direccion) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedula);
            pstmt.setString(2, nombre);
            pstmt.setString(3, telefono);
            pstmt.setString(4, direccion);
            
            // Comprobamos que efectivamente se insertó al menos una fila
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene la lista completa de todos los clientes registrados.
     */
    public List<String[]> obtenerClientes() {
        List<String[]> listaClientes = new ArrayList<>();
        String sql = "SELECT cedula, nombre, telefono, direccion FROM clientes";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            listaClientes = mapearResultSet(rs);

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener clientes: " + e.getMessage());
        }

        return listaClientes;
    }

    /**
     * Actualiza los datos de un cliente existente basado en su cédula.
     */
    public boolean actualizarCliente(String cedula, String nombre, String telefono, String direccion) {
        String sql = "UPDATE clientes SET nombre = ?, telefono = ?, direccion = ? WHERE cedula = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, direccion);
            pstmt.setString(4, cedula);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un cliente de la base de datos por su cédula.
     */
    public boolean eliminarCliente(String cedula) {
        String sql = "DELETE FROM clientes WHERE cedula = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedula);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca clientes cuyo nombre o cédula coincidan parcial o totalmente con el filtro.
     */
    public List<String[]> buscarClientes(String filtro) {
        List<String[]> listaClientes = new ArrayList<>();
        String sql = "SELECT cedula, nombre, telefono, direccion FROM clientes WHERE cedula LIKE ? OR nombre LIKE ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String parametro = "%" + filtro + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);
            
            // MEJORA: Aseguramos el cierre del ResultSet con try-with-resources
            try (ResultSet rs = pstmt.executeQuery()) {
                listaClientes = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar clientes: " + e.getMessage());
        }

        return listaClientes;
    }

    // ==========================================
    // MÉTODO AUXILIAR (Para evitar repetir código)
    // ==========================================
    /**
     * Convierte las filas de un ResultSet en una Lista de arreglos String.
     */
    private List<String[]> mapearResultSet(ResultSet rs) throws SQLException {
        List<String[]> lista = new ArrayList<>();
        while (rs.next()) {
            String[] cliente = {
                rs.getString("cedula"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("direccion")
            };
            lista.add(cliente);
        }
        return lista;
    }
}