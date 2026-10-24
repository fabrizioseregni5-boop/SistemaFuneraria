package vista;

import modelo.ConexionBD;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VistaReportes extends JFrame {
    
    public VistaReportes() {
        setTitle("Centro de Reportes - Mi Redentor");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("Historial de Accesos", crearPanelLogs());
        tabs.addTab("Facturas y Documentos", crearPanelFacturas());

        add(tabs, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnVolver = new JButton("Volver al Menú Principal");
        btnVolver.addActionListener(e -> dispose());
        panelInferior.add(btnVolver);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelLogs() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Usuario", "Fecha y Hora de Ingreso"}, 0);
        JTable tabla = new JTable(modelo);
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, username, fecha_hora FROM logs_acceso ORDER BY id DESC");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                modelo.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("fecha_hora")});
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelFacturas() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Haz doble clic para abrir el PDF de la factura."));
        panel.add(top, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> lista = new JList<>(listModel);
        
        // Buscar todos los PDFs en la carpeta del proyecto
        File directorio = new File(".");
        File[] archivos = directorio.listFiles((dir, name) -> name.startsWith("Factura_") && name.endsWith(".pdf"));
        if (archivos != null) {
            for (File f : archivos) {
                listModel.addElement(f.getName());
            }
        }

        // Doble clic para abrir el PDF
        lista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String archivo = lista.getSelectedValue();
                    if (archivo != null) {
                        try {
                            File pdf = new File(archivo);
                            if (pdf.exists() && Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().open(pdf);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "No se pudo abrir el PDF.");
                        }
                    }
                }
            }
        });

        panel.add(new JScrollPane(lista), BorderLayout.CENTER);
        return panel;
    }
}