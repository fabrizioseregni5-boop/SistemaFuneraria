package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean insertarCliente(String cedula, String nombre, String telefono, String direccion) {
        String sql = "INSERT INTO clientes (cedula, nombre, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            pstmt.setString(2, nombre);
            pstmt.setString(3, telefono);
            pstmt.setString(4, direccion);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

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

    public List<String[]> buscarClientes(String filtro) {
        List<String[]> listaClientes = new ArrayList<>();
        String sql = "SELECT cedula, nombre, telefono, direccion FROM clientes WHERE cedula LIKE ? OR nombre LIKE ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String parametro = "%" + filtro + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);
            try (ResultSet rs = pstmt.executeQuery()) {
                listaClientes = mapearResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar clientes: " + e.getMessage());
        }
        return listaClientes;
    }

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

    // ==========================================
    // MÉTODOS NUEVOS DE VALIDACIÓN DE UNICIDAD
    // ==========================================
    
    public boolean existeCedula(String cedula) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE cedula = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }

    public boolean existeTelefono(String telefono, String cedulaExcluir) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE telefono = ? AND cedula != ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, telefono);
            pstmt.setString(2, cedulaExcluir == null ? "" : cedulaExcluir);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }
}