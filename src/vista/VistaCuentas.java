package vista;

import modelo.ConexionBD;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VistaCuentas extends JFrame {
    private String tipo; // "COBRAR" o "PAGAR"
    private JTable tabla;
    private DefaultTableModel modelo;

    public VistaCuentas(String tipo) {
        this.tipo = tipo;
        setTitle("Cuentas por " + tipo + " - Mi Redentor");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblTitulo = new JLabel("Gestión de Cuentas por " + tipo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(lblTitulo, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Tabla
        modelo = new DefaultTableModel(new String[]{"ID", "Referencia", "Persona", "Total ($)", "Abonado ($)", "Saldo ($)", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.removeColumn(tabla.getColumnModel().getColumn(0));
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnAbonar = new JButton("Registrar Abono");
        btnAbonar.setBackground(new Color(39, 174, 96));
        btnAbonar.setForeground(Color.WHITE);
        btnAbonar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> dispose());

        panelInferior.add(btnAbonar);
        panelInferior.add(btnVolver);
        add(panelInferior, BorderLayout.SOUTH);

        // Lógica de Abonar
        btnAbonar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una cuenta de la tabla.");
                return;
            }
            int filaModelo = tabla.convertRowIndexToModel(fila);
            int idCuenta = Integer.parseInt(modelo.getValueAt(filaModelo, 0).toString());
            double saldoActual = Double.parseDouble(modelo.getValueAt(filaModelo, 5).toString());
            
            String input = JOptionPane.showInputDialog(this, "Saldo pendiente: $" + saldoActual + "\nIngrese el monto a abonar:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    double abono = Double.parseDouble(input);
                    if (abono <= 0) throw new NumberFormatException();
                    
                    registrarAbono(idCuenta, abono, saldoActual);
                    cargarTabla();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cargarTabla();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        String sql = "SELECT id, referencia, persona, monto_total, abonado, saldo, estado FROM cuentas WHERE tipo = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipo);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("referencia"), rs.getString("persona"),
                    String.format("%.2f", rs.getDouble("monto_total")),
                    String.format("%.2f", rs.getDouble("abonado")),
                    String.format("%.2f", rs.getDouble("saldo")),
                    rs.getString("estado")
                });
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }

    private void registrarAbono(int id, double abono, double saldoActual) {
        if (abono > saldoActual) abono = saldoActual;
        double nuevoAbonado = (saldoActual - abono); // Lógica simplificada
        String estado = (abono >= saldoActual) ? "Pagada" : "Pendiente";

        String sql = "UPDATE cuentas SET abonado = abonado + ?, saldo = saldo - ?, estado = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, abono);
            pstmt.setDouble(2, abono);
            pstmt.setString(3, estado);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Abono registrado con éxito.");
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }
}