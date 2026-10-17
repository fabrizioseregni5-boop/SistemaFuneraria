package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    /**
     * Inserta un nuevo producto en el inventario.
     */
    public boolean insertarProducto(String codigo, String descripcion, int stock, double precio) {
        String sql = "INSERT INTO inventario (codigo, descripcion, stock, precio) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            pstmt.setString(2, descripcion);
            pstmt.setInt(3, stock);
            pstmt.setDouble(4, precio);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los productos del inventario.
     */
    public List<Object[]> obtenerInventario() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT codigo, descripcion, stock, precio FROM inventario";

        // MEJORA: Usamos PreparedStatement para mantener consistencia
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            lista = mapearResultSet(rs);

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener inventario: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza los datos de un producto existente.
     */
    public boolean actualizarProducto(String codigo, String descripcion, int stock, double precio) {
        String sql = "UPDATE inventario SET descripcion = ?, stock = ?, precio = ? WHERE codigo = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, descripcion);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, precio);
            pstmt.setString(4, codigo);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un producto por su código.
     */
    public boolean eliminarProducto(String codigo) {
        String sql = "DELETE FROM inventario WHERE codigo = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            // Si el producto está en una factura, la BD lanzará un error de Foreign Key aquí
            System.err.println("❌ Error al eliminar producto (Posible llave foránea en uso): " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca productos por código o descripción.
     */
    public List<Object[]> buscarProductos(String filtro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT codigo, descripcion, stock, precio FROM inventario WHERE codigo LIKE ? OR descripcion LIKE ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String param = "%" + filtro + "%";
            pstmt.setString(1, param);
            pstmt.setString(2, param);
            
            // MEJORA: Aseguramos el cierre del ResultSet con try-with-resources
            try (ResultSet rs = pstmt.executeQuery()) {
                lista = mapearResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar productos: " + e.getMessage());
        }
        return lista;
    }

    // ==========================================
    // MÉTODO AUXILIAR (DRY - Don't Repeat Yourself)
    // ==========================================
    /**
     * Convierte un ResultSet en una lista de arreglos de objetos.
     */
    private List<Object[]> mapearResultSet(ResultSet rs) throws SQLException {
        List<Object[]> lista = new ArrayList<>();
        while (rs.next()) {
            Object[] fila = {
                rs.getString("codigo"),
                rs.getString("descripcion"),
                rs.getInt("stock"),
                rs.getDouble("precio")
            };
            lista.add(fila);
        }
        return lista;
    }
}