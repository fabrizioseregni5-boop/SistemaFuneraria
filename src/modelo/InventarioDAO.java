package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    public boolean insertarProducto(String codigo, String descripcion, int stock, double precioCompra, double precioVenta) {
        String sql = "INSERT INTO inventario (codigo, descripcion, stock, precio, precio_venta) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            pstmt.setString(2, descripcion);
            pstmt.setInt(3, stock);
            pstmt.setDouble(4, precioCompra);
            pstmt.setDouble(5, precioVenta);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> obtenerInventario() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT codigo, descripcion, stock, precio, precio_venta FROM inventario";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            lista = mapearResultSet(rs);
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener inventario: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarProducto(String codigo, String descripcion, int stock, double precioCompra, double precioVenta) {
        String sql = "UPDATE inventario SET descripcion = ?, stock = ?, precio = ?, precio_venta = ? WHERE codigo = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, descripcion);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, precioCompra);
            pstmt.setDouble(4, precioVenta);
            pstmt.setString(5, codigo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProducto(String codigo) {
        String sql = "DELETE FROM inventario WHERE codigo = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> buscarProductos(String filtro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT codigo, descripcion, stock, precio, precio_venta FROM inventario WHERE codigo LIKE ? OR descripcion LIKE ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String param = "%" + filtro + "%";
            pstmt.setString(1, param);
            pstmt.setString(2, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                lista = mapearResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar productos: " + e.getMessage());
        }
        return lista;
    }

    public boolean existeCodigo(String codigo) {
        String sql = "SELECT COUNT(*) FROM inventario WHERE codigo = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }

    // ==========================================
    // MÉTODOS PARA GUARDAR LAS TASAS
    // ==========================================
    public void guardarTasas(double bcv, double binance) {
        String sql = "INSERT OR REPLACE INTO configuracion (clave, valor) VALUES ('tasa_bcv', ?), ('tasa_binance', ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, bcv);
            pstmt.setDouble(2, binance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar tasas: " + e.getMessage());
        }
    }

    public double[] obtenerTasas() {
        double[] tasas = {0.0, 0.0};
        String sql = "SELECT clave, valor FROM configuracion WHERE clave IN ('tasa_bcv', 'tasa_binance')";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                if (rs.getString("clave").equals("tasa_bcv")) tasas[0] = rs.getDouble("valor");
                if (rs.getString("clave").equals("tasa_binance")) tasas[1] = rs.getDouble("valor");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tasas: " + e.getMessage());
        }
        return tasas;
    }

    private List<Object[]> mapearResultSet(ResultSet rs) throws SQLException {
        List<Object[]> lista = new ArrayList<>();
        while (rs.next()) {
            Object[] fila = {
                rs.getString("codigo"),
                rs.getString("descripcion"),
                rs.getInt("stock"),
                rs.getDouble("precio"),
                rs.getDouble("precio_venta")
            };
            lista.add(fila);
        }
        return lista;
    }
}