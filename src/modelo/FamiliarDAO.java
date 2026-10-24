package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FamiliarDAO {

    /**
     * Registra un familiar asociado a la cédula del titular.
     * @return true si se insertó correctamente, false si hubo un error.
     */
    public boolean insertarFamiliar(String cedulaTitular, String nombres, String apellidos, String parentesco, String cedulaFamiliar, String direccion) {
        String sql = "INSERT INTO familiares (cedula_titular, nombres_familiar, apellidos_familiar, parentesco, cedula_familiar, direccion) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedulaTitular);
            pstmt.setString(2, nombres);
            pstmt.setString(3, apellidos);
            pstmt.setString(4, parentesco);
            pstmt.setString(5, cedulaFamiliar);
            pstmt.setString(6, direccion);
            
            // Comprobamos que efectivamente se insertó al menos una fila
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar familiar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene la lista de familiares de un titular específico.
     * @return Una lista de arreglos String con los datos del familiar.
     */
    public List<String[]> obtenerFamiliaresPorTitular(String cedulaTitular) {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, nombres_familiar, apellidos_familiar, parentesco, cedula_familiar, direccion FROM familiares WHERE cedula_titular = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedulaTitular);
            
            // MEJORA: Incluir el ResultSet en el try-with-resources para cerrarlo automáticamente
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] fam = {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombres_familiar"),
                        rs.getString("apellidos_familiar"),
                        rs.getString("parentesco"),
                        rs.getString("cedula_familiar"),
                        rs.getString("direccion")
                    };
                    lista.add(fam);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al consultar familiares: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza los datos de un familiar existente.
     * @return true si se modificó el registro, false si el ID no existía o hubo error.
     */
    public boolean actualizarFamiliar(int id, String nombres, String apellidos, String parentesco, String cedulaFamiliar, String direccion) {
        String sql = "UPDATE familiares SET nombres_familiar = ?, apellidos_familiar = ?, parentesco = ?, cedula_familiar = ?, direccion = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombres);
            pstmt.setString(2, apellidos);
            pstmt.setString(3, parentesco);
            pstmt.setString(4, cedulaFamiliar);
            pstmt.setString(5, direccion);
            pstmt.setInt(6, id);
            
            // Comprobamos que realmente se actualizó una fila
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar familiar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un familiar por su ID.
     * @return true si se eliminó correctamente, false si hubo error.
     */
    public boolean eliminarFamiliar(int id) {
        String sql = "DELETE FROM familiares WHERE id = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            // Comprobamos que realmente se borró el registro
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar familiar: " + e.getMessage());
            return false;
        }
    }
    // Verifica si la cédula del familiar ya existe (excluyendo el ID actual si se está editando)
    public boolean existeCedulaFamiliar(String cedulaFamiliar, int idExcluir) {
        if (cedulaFamiliar == null || cedulaFamiliar.trim().isEmpty()) return false;
        
        String sql = "SELECT COUNT(*) FROM familiares WHERE cedula_familiar = ? AND id != ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedulaFamiliar.trim());
            pstmt.setInt(2, idExcluir);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar cédula familiar: " + e.getMessage());
        }
        return false;
    }
}

