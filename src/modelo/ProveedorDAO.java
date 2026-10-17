package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public boolean insertarProveedor(String rif, String nombre, String telefono, String direccion) {
        String sql = "INSERT INTO proveedores (rif, nombre, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rif);
            pstmt.setString(2, nombre);
            pstmt.setString(3, telefono);
            pstmt.setString(4, direccion);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> obtenerProveedores() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT rif, nombre, telefono, direccion FROM proveedores";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            return mapearResultSet(rs);
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener proveedores: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarProveedor(String rif, String nombre, String telefono, String direccion) {
        String sql = "UPDATE proveedores SET nombre = ?, telefono = ?, direccion = ? WHERE rif = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, direccion);
            pstmt.setString(4, rif);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProveedor(String rif) {
        String sql = "DELETE FROM proveedores WHERE rif = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rif);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> buscarProveedores(String filtro) {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT rif, nombre, telefono, direccion FROM proveedores WHERE rif LIKE ? OR nombre LIKE ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String param = "%" + filtro + "%";
            pstmt.setString(1, param);
            pstmt.setString(2, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                return mapearResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar proveedores: " + e.getMessage());
        }
        return lista;
    }

    private List<String[]> mapearResultSet(ResultSet rs) throws SQLException {
        List<String[]> lista = new ArrayList<>();
        while (rs.next()) {
            String[] prov = {
                rs.getString("rif"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("direccion")
            };
            lista.add(prov);
        }
        return lista;
    }
}