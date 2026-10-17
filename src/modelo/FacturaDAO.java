package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class FacturaDAO {

    public boolean registrarFactura(String numFactura, String cedulaCliente, String proveedor, 
                                    double subtotal, double iva, double total, List<ItemFactura> items) {
        
        String sqlFactura = "INSERT INTO facturas (numero_factura, cedula_cliente, proveedor, subtotal, iva, total) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_factura (numero_factura, codigo_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE inventario SET stock = stock - ? WHERE codigo = ?";

        // Uso de try-with-resources para asegurar el cierre de la conexión
        try (Connection conn = ConexionBD.conectar()) {
            
            if (conn == null) {
                System.out.println("Error: No se pudo establecer conexión a la base de datos.");
                return false;
            }

            conn.setAutoCommit(false); // Iniciar transacción

            try {
                // 1. Guardar cabecera de factura
                try (PreparedStatement pstmtFac = conn.prepareStatement(sqlFactura)) {
                    pstmtFac.setString(1, numFactura);
                    pstmtFac.setString(2, cedulaCliente);
                    pstmtFac.setString(3, proveedor);
                    pstmtFac.setDouble(4, subtotal);
                    pstmtFac.setDouble(5, iva);
                    pstmtFac.setDouble(6, total);
                    pstmtFac.executeUpdate();
                }

                // 2. Guardar detalles y restar stock
                try (PreparedStatement pstmtDet = conn.prepareStatement(sqlDetalle);
                     PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock)) {
                    
                    for (ItemFactura item : items) {
                        // Insertar detalle
                        pstmtDet.setString(1, numFactura);
                        pstmtDet.setString(2, item.getCodigoProducto());
                        pstmtDet.setInt(3, item.getCantidad());
                        pstmtDet.setDouble(4, item.getPrecioUnitario());
                        pstmtDet.setDouble(5, item.getSubtotal());
                        pstmtDet.executeUpdate();

                        // Descontar del inventario
                        pstmtStock.setInt(1, item.getCantidad());
                        pstmtStock.setString(2, item.getCodigoProducto());
                        pstmtStock.executeUpdate();
                    }
                }

                conn.commit(); // Confirmar cambios
                return true;

            } catch (SQLException e) {
                // Si algo falla, revertimos los cambios
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error crítico en rollback: " + ex.getMessage());
                }
                System.out.println("Error al procesar la factura: " + e.getMessage());
                return false;
            } finally {
                // Restaurar el autocommit para futuras operaciones
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {}
            }

        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}