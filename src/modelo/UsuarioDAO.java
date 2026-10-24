package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public int validarUsuario(String username, String password) {
        String sql = "SELECT rol_id FROM usuarios WHERE username = ? AND password = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("rol_id");
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return -1;
    }

    public List<Object[]> obtenerUsuarios(String filtro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id, username, nombre_completo, password, rol_id FROM usuarios "
                   + "WHERE username LIKE ? OR nombre_completo LIKE ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String param = "%" + filtro + "%";
            pstmt.setString(1, param);
            pstmt.setString(2, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id"), 
                        rs.getString("username"), 
                        rs.getString("nombre_completo"),
                        rs.getString("password"),
                        rs.getInt("rol_id")
                    });
                }
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return lista;
    }

    public boolean insertarUsuario(String user, String nombre, String pass, int rol) {
        String sql = "INSERT INTO usuarios (username, nombre_completo, password, rol_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user); pstmt.setString(2, nombre); pstmt.setString(3, pass); pstmt.setInt(4, rol);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean actualizarUsuario(int id, String user, String nombre, String pass, int rol) {
        String sql = "UPDATE usuarios SET username = ?, nombre_completo = ?, password = ?, rol_id = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user); pstmt.setString(2, nombre); pstmt.setString(3, pass); pstmt.setInt(4, rol); pstmt.setInt(5, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // Método para contar cuántos usuarios hay de un rol específico (para las tarjetas de arriba)
    public int contarPorRol(int rolId) {
        String sql = rolId == -1 ? "SELECT COUNT(*) FROM usuarios" : "SELECT COUNT(*) FROM usuarios WHERE rol_id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (rolId != -1) pstmt.setInt(1, rolId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }
}