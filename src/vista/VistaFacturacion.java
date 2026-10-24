package vista;

import modelo.ConexionBD;
import modelo.FacturaDAO;
import modelo.ItemFactura;
import modelo.FamiliarDAO;
import modelo.InventarioDAO; // IMPORTANTE: Para obtener la tasa

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// --- IMPORTS PARA EL PDF (OpenPDF) ---
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
// -------------------------------------

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VistaFacturacion extends JFrame {

    private JTextField txtNumero, txtFecha, txtCedula, txtNombre, txtUbicacion, txtCodigoArt, txtCantidad;
    private JComboBox<String> cmbProveedor, cmbFallecido; 
    
    private JTable tablaDetalle;
    private DefaultTableModel modeloTabla;
    private List<ItemFactura> listaItems;
    
    private JLabel lblSubtotalVal, lblIvaVal, lblTotalVal, lblTotalBs;
    private JTextField txtPorcentajeIva;

    private double subtotalGeneral = 0.0;
    private double ivaGeneral = 0.0;
    private double totalGeneral = 0.0;
    
    // Variable para la tasa del BCV
    private double tasaBcv = 0.0;

    public VistaFacturacion() {
        listaItems = new ArrayList<>();
        
        // Obtenemos la tasa BCV de la base de datos al iniciar
        double[] tasas = new InventarioDAO().obtenerTasas();
        tasaBcv = tasas[0];

        setTitle("Pedidos / Facturación - Funeraria Mi Redentor");
        setSize(1150, 720); // Un poco más alto para que quepa el monto en Bs
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        
        Font fuenteClasica = new Font("Tahoma", Font.PLAIN, 11);
        UIManager.put("Label.font", fuenteClasica);
        UIManager.put("TextField.font", fuenteClasica);
        UIManager.put("ComboBox.font", fuenteClasica);
        UIManager.put("Button.font", fuenteClasica);
        UIManager.put("Table.font", fuenteClasica);
        UIManager.put("TableHeader.font", new Font("Tahoma", Font.BOLD, 11));

        add(buildPanelEncabezado(), BorderLayout.NORTH);
        add(buildPanelDetalle(), BorderLayout.CENTER);
        add(buildPanelInferior(), BorderLayout.SOUTH);

        generarNuevaFactura();
        
        txtCedula.addActionListener(e -> buscarCliente());
        txtCodigoArt.addActionListener(e -> agregarItem());
    }

    private JPanel buildPanelEncabezado() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Encabezado", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11), Color.BLUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: Número, Fecha, Proveedor
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelDatos.add(new JLabel("Número:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.15;
        txtNumero = new JTextField(8); txtNumero.setEditable(false); txtNumero.setBackground(new Color(240, 240, 240));
        panelDatos.add(txtNumero, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panelDatos.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.15;
        txtFecha = new JTextField(10); txtFecha.setEditable(false); txtFecha.setBackground(new Color(240, 240, 240));
        panelDatos.add(txtFecha, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        panelDatos.add(new JLabel("Proveedor:"), gbc);
        gbc.gridx = 5; gbc.weightx = 0.3;
        cmbProveedor = new JComboBox<>();
        panelDatos.add(cmbProveedor, gbc);
        cargarProveedoresEnCombo();

        // Fila 1: Cliente
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelDatos.add(new JLabel("Titular (Cédula):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.15;
        JPanel panelCli = new JPanel(new BorderLayout(2, 0));
        txtCedula = new JTextField("V-");
        JButton btnBuscar = new JButton("...");
        btnBuscar.setMargin(new Insets(0, 2, 0, 2));
        btnBuscar.addActionListener(e -> buscarCliente());
        panelCli.add(txtCedula, BorderLayout.CENTER);
        panelCli.add(btnBuscar, BorderLayout.EAST);
        panelDatos.add(panelCli, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panelDatos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3; gbc.weightx = 0.85;
        txtNombre = new JTextField(); txtNombre.setEditable(false); txtNombre.setBackground(new Color(240, 240, 240));
        panelDatos.add(txtNombre, gbc);

        // Fila 2: Fallecido y Ubicación
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0;
        panelDatos.add(new JLabel("Fallecido:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.2;
        cmbFallecido = new JComboBox<>();
        cmbFallecido.setBackground(Color.WHITE);
        panelDatos.add(cmbFallecido, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1; gbc.weightx = 0;
        panelDatos.add(new JLabel("Ubicación:"), gbc);
        gbc.gridx = 4; gbc.gridwidth = 2; gbc.weightx = 0.8;
        txtUbicacion = new JTextField();
        panelDatos.add(txtUbicacion, gbc);

        panelPrincipal.add(panelDatos, BorderLayout.CENTER);
        return panelPrincipal;
    }

    private JPanel buildPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Barra de captura rápida
        JPanel panelCaptura = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelCaptura.add(new JLabel("Artículo (Código):"));
        
        txtCodigoArt = new JTextField(10);
        panelCaptura.add(txtCodigoArt);
        
        JButton btnBuscarArt = new JButton("Buscar");
        btnBuscarArt.setToolTipText("Buscar Artículo en el Inventario");
        btnBuscarArt.setMargin(new Insets(0, 5, 0, 5));
        btnBuscarArt.addActionListener(e -> abrirBuscadorInventario());
        panelCaptura.add(btnBuscarArt);
        
        panelCaptura.add(new JLabel("Cant:"));
        txtCantidad = new JTextField("1.00", 5);
        panelCaptura.add(txtCantidad);
        
        JButton btnAgregar = new JButton("Agregar Renglón");
        btnAgregar.addActionListener(e -> agregarItem());
        panelCaptura.add(btnAgregar);
        
        JButton btnEliminar = new JButton("Eliminar Renglón");
        btnEliminar.addActionListener(e -> eliminarItem());
        panelCaptura.add(btnEliminar);

        panel.add(panelCaptura, BorderLayout.NORTH);

        // Tabla de Detalle
        String[] columnas = {"Reng", "Artículo", "Cant.", "Unid", "Precio", "% Desc", "I.V.A.", "Neto", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDetalle = new JTable(modeloTabla);
        
        tablaDetalle.setShowGrid(true);
        tablaDetalle.setGridColor(Color.LIGHT_GRAY);
        tablaDetalle.setRowHeight(22);
        tablaDetalle.getTableHeader().setBackground(new Color(230, 230, 230));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
        tablaDetalle.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tablaDetalle.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tablaDetalle.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tablaDetalle.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

        JScrollPane scroll = new JScrollPane(tablaDetalle);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Detalle", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11), Color.BLUE));
        
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPanelInferior() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 0));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton btnGuardar = new JButton("Guardar Venta");
        btnGuardar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnGuardar.setBackground(new Color(200, 230, 200));
        btnGuardar.addActionListener(e -> guardarFactura());
        
        JButton btnNuevo = new JButton("Nueva Factura");
        btnNuevo.addActionListener(e -> limpiarFormulario());
        
        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnVolver);
        
        panelPrincipal.add(panelBotones, BorderLayout.WEST);

        JPanel panelTotales = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelTotales.add(new JLabel("IVA %:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.1;
        txtPorcentajeIva = new JTextField("16", 4);
        panelTotales.add(txtPorcentajeIva, gbc);
        
        JButton btnActIva = new JButton("Recalcular");
        btnActIva.addActionListener(e -> recalcularTotales());
        gbc.gridx = 2; gbc.weightx = 0;
        panelTotales.add(btnActIva, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panelTotales.add(new JLabel("Subtotal ($):", JLabel.RIGHT), gbc);
        gbc.gridx = 2; gbc.gridwidth = 1; gbc.weightx = 0.3;
        lblSubtotalVal = new JLabel("0.00", JLabel.RIGHT);
        lblSubtotalVal.setBorder(BorderFactory.createEtchedBorder());
        lblSubtotalVal.setBackground(Color.WHITE);
        lblSubtotalVal.setOpaque(true);
        panelTotales.add(lblSubtotalVal, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelTotales.add(new JLabel("I.V.A. ($):", JLabel.RIGHT), gbc);
        gbc.gridx = 2; gbc.gridwidth = 1;
        lblIvaVal = new JLabel("0.00", JLabel.RIGHT);
        lblIvaVal.setBorder(BorderFactory.createEtchedBorder());
        lblIvaVal.setBackground(Color.WHITE);
        lblIvaVal.setOpaque(true);
        panelTotales.add(lblIvaVal, gbc);

        // Total en Dólares
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel lblTotalTit = new JLabel("Total ($):", JLabel.RIGHT);
        lblTotalTit.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelTotales.add(lblTotalTit, gbc);
        gbc.gridx = 2; gbc.gridwidth = 1;
        lblTotalVal = new JLabel("0.00", JLabel.RIGHT);
        lblTotalVal.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblTotalVal.setBorder(BorderFactory.createEtchedBorder());
        lblTotalVal.setBackground(new Color(240, 240, 240));
        lblTotalVal.setOpaque(true);
        panelTotales.add(lblTotalVal, gbc);
        
        // Total en Bolívares (NUEVO)
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel lblTotalBsTit = new JLabel("Total (Bs):", JLabel.RIGHT);
        lblTotalBsTit.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelTotales.add(lblTotalBsTit, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 1;
        lblTotalBs = new JLabel("0.00", JLabel.RIGHT);
        lblTotalBs.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblTotalBs.setForeground(new Color(39, 174, 96)); // Color verde para resaltar
        lblTotalBs.setBorder(BorderFactory.createEtchedBorder());
        lblTotalBs.setBackground(new Color(230, 255, 230)); // Fondo verde suave
        lblTotalBs.setOpaque(true);
        panelTotales.add(lblTotalBs, gbc);

        panelPrincipal.add(panelTotales, BorderLayout.EAST);
        return panelPrincipal;
    }

    private void abrirBuscadorInventario() {
        JDialog dialog = new JDialog(this, "Seleccionar Artículo del Inventario", true);
        dialog.setSize(750, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols = {"Código", "Descripción", "P. Compra ($)", "P. Venta ($)", "Stock"};
        DefaultTableModel mod = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(mod);
        
        InventarioDAO dao = new InventarioDAO();
        List<Object[]> lista = dao.obtenerInventario();
        
        for (Object[] fila : lista) {
            Object[] filaCorregida = { fila[0], fila[1], fila[3], fila[4], fila[2] };
            mod.addRow(filaCorregida);
        }

        JScrollPane scroll = new JScrollPane(tbl);
        dialog.add(scroll, BorderLayout.CENTER);

        tbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tbl.getSelectedRow();
                    if (fila != -1) {
                        String codigo = mod.getValueAt(fila, 0).toString();
                        txtCodigoArt.setText(codigo);
                        dialog.dispose();
                        agregarItem(); 
                    }
                }
            }
        });

        JLabel lblInstruccion = new JLabel("Haz doble clic en un artículo para agregarlo a la factura.", JLabel.CENTER);
        lblInstruccion.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        dialog.add(lblInstruccion, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void buscarCliente() {
        String cedula = txtCedula.getText().trim();
        String sql = "SELECT nombre FROM clientes WHERE cedula = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                txtNombre.setText(rs.getString("nombre"));
                cargarFamiliares(cedula);
            } else {
                txtNombre.setText("No encontrado");
                cmbFallecido.removeAllItems();
                cmbFallecido.addItem("Titular no registrado");
                JOptionPane.showMessageDialog(this, "Cliente no registrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error BD: " + e.getMessage());
        }
    }

    private void cargarFamiliares(String cedulaTitular) {
        cmbFallecido.removeAllItems();
        FamiliarDAO familiarDAO = new FamiliarDAO();
        List<String[]> listaFamiliares = familiarDAO.obtenerFamiliaresPorTitular(cedulaTitular);
        
        if (listaFamiliares.isEmpty()) {
            cmbFallecido.addItem("-- Sin familiares registrados --");
        } else {
            cmbFallecido.addItem("-- Seleccione el fallecido --");
            for (String[] fam : listaFamiliares) {
                String nombreCompleto = fam[1] + " " + fam[2];
                if (fam[4] != null && !fam[4].isEmpty()) {
                    nombreCompleto += " (" + fam[4] + ")";
                }
                cmbFallecido.addItem(nombreCompleto);
            }
        }
    }

    private void agregarItem() {
        String codigo = txtCodigoArt.getText().trim();
        String cantStr = txtCantidad.getText().trim();

        if (codigo.isEmpty()) return;

        try {
            double cantidad = Double.parseDouble(cantStr);
            String sql = "SELECT descripcion, precio_venta, stock FROM inventario WHERE codigo = ?";
            
            try (Connection conn = ConexionBD.conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, codigo);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    if (cantidad > stock) {
                        JOptionPane.showMessageDialog(this, "Stock insuficiente. Quedan: " + stock);
                        return;
                    }

                    String desc = rs.getString("descripcion");
                    double precio = rs.getDouble("precio_venta"); 
                    
                    if (precio == 0) {
                        JOptionPane.showMessageDialog(this, "Advertencia: El precio de venta es 0. Configúralo en el Inventario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }

                    double subtotalRenglon = cantidad * precio;

                    ItemFactura item = new ItemFactura(codigo, desc, (int) cantidad, precio);
                    listaItems.add(item);

                    int renglon = modeloTabla.getRowCount() + 1;
                    modeloTabla.addRow(new Object[]{
                        renglon, codigo, String.format("%.2f", cantidad), "UND", 
                        String.format("%.2f", precio), "0.00", "1", 
                        String.format("%.2f", subtotalRenglon), desc
                    });

                    recalcularTotales();
                    txtCodigoArt.setText("");
                    txtCantidad.setText("1.00");
                    txtCodigoArt.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(this, "Artículo no existe.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar. Verifique la cantidad.");
        }
    }

    private void eliminarItem() {
        int fila = tablaDetalle.getSelectedRow();
        if (fila != -1) {
            modeloTabla.removeRow(fila);
            listaItems.remove(fila);
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                modeloTabla.setValueAt(i + 1, i, 0);
            }
            recalcularTotales();
        }
    }

    private void recalcularTotales() {
        subtotalGeneral = 0.0;
        for (ItemFactura item : listaItems) {
            subtotalGeneral += item.getSubtotal();
        }

        double ivaPct = 16.0;
        try {
            ivaPct = Double.parseDouble(txtPorcentajeIva.getText().trim());
        } catch (Exception ignored) {}

        ivaGeneral = subtotalGeneral * (ivaPct / 100.0);
        totalGeneral = subtotalGeneral + ivaGeneral;

        // Calcular el total en Bolívares
        double totalBs = totalGeneral * tasaBcv;

        lblSubtotalVal.setText(String.format("%,.2f", subtotalGeneral));
        lblIvaVal.setText(String.format("%,.2f", ivaGeneral));
        lblTotalVal.setText(String.format("%,.2f", totalGeneral));
        
        // Actualizar la etiqueta de Bolívares
        lblTotalBs.setText(String.format("%,.2f Bs.", totalBs));
    }

    private void guardarFactura() {
        if (txtNombre.getText().isEmpty() || listaItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe buscar un cliente válido y agregar al menos un artículo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (cmbFallecido.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar el familiar fallecido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numFac = "FAC-" + txtNumero.getText().trim();
        String cedula = txtCedula.getText().trim();
        String proveedor = cmbProveedor.getSelectedItem().toString();

        FacturaDAO dao = new FacturaDAO();
        boolean exito = dao.registrarFactura(numFac, cedula, proveedor, subtotalGeneral, ivaGeneral, totalGeneral, listaItems);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Factura guardada exitosamente en la Base de Datos.");
            generarPDF(numFac, cedula, txtNombre.getText(), cmbFallecido.getSelectedItem().toString(), txtUbicacion.getText());
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la factura en BD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarPDF(String numFactura, String cedula, String nombre, String fallecido, String ubicacion) {
        String nombreArchivo = "Factura_" + numFactura + ".pdf";
        
        try {
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            com.lowagie.text.Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new java.awt.Color(44, 62, 80));
            com.lowagie.text.Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 11, java.awt.Color.DARK_GRAY);
            com.lowagie.text.Font fontNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, java.awt.Color.BLACK);
            com.lowagie.text.Font fontBlanca = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, java.awt.Color.WHITE);
            
            java.awt.Color colorCabeceraTabla = new java.awt.Color(44, 62, 80);

            PdfPTable tablaHeader = new PdfPTable(2);
            tablaHeader.setWidthPercentage(100);
            tablaHeader.setWidths(new float[]{30f, 70f}); 

            PdfPCell celdaLogo = new PdfPCell();
            celdaLogo.setBorder(Rectangle.NO_BORDER);
            try {
                Image logo = Image.getInstance("logo.png"); 
                logo.scaleToFit(120, 120); 
                celdaLogo.addElement(logo);
            } catch (Exception e) {
                celdaLogo.addElement(new Paragraph("(No se encontró logo.png)", fontSubtitulo));
            }
            tablaHeader.addCell(celdaLogo);

            PdfPCell celdaDatos = new PdfPCell();
            celdaDatos.setBorder(Rectangle.NO_BORDER);
            celdaDatos.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            Paragraph pEmpresa = new Paragraph("Servicios Funerarios Mi Redentor", fontTitulo);
            pEmpresa.setAlignment(Element.ALIGN_RIGHT);
            
            Paragraph pContacto = new Paragraph("redentor7842@gmail.com\n0426-5626222", fontSubtitulo);
            pContacto.setAlignment(Element.ALIGN_RIGHT);
            
            celdaDatos.addElement(pEmpresa);
            celdaDatos.addElement(new Paragraph(" ", fontSubtitulo));
            celdaDatos.addElement(pContacto);
            tablaHeader.addCell(celdaDatos);

            documento.add(tablaHeader);
            
            documento.add(new Paragraph(" "));
            Paragraph linea = new Paragraph("__________________________________________________________________________________", fontSubtitulo);
            linea.setAlignment(Element.ALIGN_CENTER);
            documento.add(linea);
            documento.add(new Paragraph(" "));

            PdfPTable tablaDatos = new PdfPTable(2);
            tablaDatos.setWidthPercentage(100);
            tablaDatos.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            tablaDatos.addCell(new Paragraph("Factura N°: " + numFactura, fontNegrita));
            tablaDatos.addCell(new Paragraph("Fecha: " + txtFecha.getText(), fontNegrita));
            tablaDatos.addCell(new Paragraph("Titular: " + nombre + " (C.I: " + cedula + ")"));
            tablaDatos.addCell(new Paragraph("Fallecido: " + fallecido));
            tablaDatos.addCell(new Paragraph("Proveedor: " + cmbProveedor.getSelectedItem().toString()));
            tablaDatos.addCell(new Paragraph("Ubicación: " + ubicacion));

            documento.add(tablaDatos);
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(4); 
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{15f, 45f, 20f, 20f}); 

            PdfPCell cCant = new PdfPCell(new Phrase("CANT.", fontBlanca));
            cCant.setBackgroundColor(colorCabeceraTabla);
            cCant.setHorizontalAlignment(Element.ALIGN_CENTER);
            cCant.setPadding(8);
            tabla.addCell(cCant);

            PdfPCell cDesc = new PdfPCell(new Phrase("DESCRIPCIÓN", fontBlanca));
            cDesc.setBackgroundColor(colorCabeceraTabla);
            cDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
            cDesc.setPadding(8);
            tabla.addCell(cDesc);

            PdfPCell cPrecio = new PdfPCell(new Phrase("P. UNITARIO", fontBlanca));
            cPrecio.setBackgroundColor(colorCabeceraTabla);
            cPrecio.setHorizontalAlignment(Element.ALIGN_CENTER);
            cPrecio.setPadding(8);
            tabla.addCell(cPrecio);

            PdfPCell cSub = new PdfPCell(new Phrase("SUBTOTAL", fontBlanca));
            cSub.setBackgroundColor(colorCabeceraTabla);
            cSub.setHorizontalAlignment(Element.ALIGN_CENTER);
            cSub.setPadding(8);
            tabla.addCell(cSub);

            for (ItemFactura item : listaItems) {
                PdfPCell cell;
                
                cell = new PdfPCell(new Phrase(String.valueOf(item.getCantidad())));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                tabla.addCell(cell);
                
                cell = new PdfPCell(new Phrase(item.getDescripcion()));
                cell.setPadding(6);
                tabla.addCell(cell);
                
                cell = new PdfPCell(new Phrase(String.format("$%.2f", item.getPrecioUnitario())));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(6);
                tabla.addCell(cell);
                
                cell = new PdfPCell(new Phrase(String.format("$%.2f", item.getSubtotal())));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(6);
                tabla.addCell(cell);
            }

            documento.add(tabla);
            documento.add(new Paragraph(" "));

            PdfPTable tablaFinal = new PdfPTable(2);
            tablaFinal.setWidthPercentage(100);
            tablaFinal.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell celdaFirma = new PdfPCell();
            celdaFirma.setBorder(Rectangle.NO_BORDER);
            celdaFirma.addElement(new Paragraph("\n\n\n\n_____________________________________", fontSubtitulo));
            celdaFirma.addElement(new Paragraph("Firma del Titular / Responsable", fontSubtitulo));
            tablaFinal.addCell(celdaFirma);

            PdfPCell celdaTotales = new PdfPCell();
            celdaTotales.setBorder(Rectangle.NO_BORDER);
            
            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidthPercentage(100);
            tablaTotales.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            tablaTotales.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

            tablaTotales.addCell(new Paragraph("Subtotal:", fontNegrita));
            tablaTotales.addCell(new Paragraph(String.format("$%.2f", subtotalGeneral), fontNegrita));
            
            tablaTotales.addCell(new Paragraph("IVA (16%):", fontNegrita));
            tablaTotales.addCell(new Paragraph(String.format("$%.2f", ivaGeneral), fontNegrita));
            
            PdfPCell cellTotalTit = new PdfPCell(new Paragraph("TOTAL ($):", fontTitulo));
            cellTotalTit.setBorder(Rectangle.TOP);
            cellTotalTit.setBorderColor(colorCabeceraTabla);
            tablaTotales.addCell(cellTotalTit);
            
            PdfPCell cellTotalVal = new PdfPCell(new Paragraph(String.format("$%.2f", totalGeneral), fontTitulo));
            cellTotalVal.setBorder(Rectangle.TOP);
            cellTotalVal.setBorderColor(colorCabeceraTabla);
            cellTotalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.addCell(cellTotalVal);

            celdaTotales.addElement(tablaTotales);
            tablaFinal.addCell(celdaTotales);

            documento.add(tablaFinal);
            documento.close();

            int abrir = JOptionPane.showConfirmDialog(this, 
                    "PDF generado con éxito: " + nombreArchivo + "\n¿Desea abrirlo ahora?", 
                    "PDF Creado", JOptionPane.YES_NO_OPTION);
            
            if (abrir == JOptionPane.YES_OPTION) {
                File pdfFile = new File(nombreArchivo);
                if (pdfFile.exists()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(pdfFile);
                    } else {
                        JOptionPane.showMessageDialog(this, "No se puede abrir el PDF automáticamente en este sistema.");
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void generarNuevaFactura() {
        txtNumero.setText(obtenerSiguienteNumeroFactura());
        txtFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtCedula.setText("V-");
        txtNombre.setText("");
        txtUbicacion.setText("");
        
        cmbFallecido.removeAllItems();
        cmbFallecido.addItem("-- Busque un titular primero --");
        
        modeloTabla.setRowCount(0);
        listaItems.clear();
        subtotalGeneral = 0; ivaGeneral = 0; totalGeneral = 0;
        recalcularTotales();
        txtCedula.requestFocus();
    }

    private void limpiarFormulario() {
        // Refrescamos la tasa BCV por si se cambió en el inventario recientemente
        double[] tasas = new InventarioDAO().obtenerTasas();
        tasaBcv = tasas[0];

        generarNuevaFactura();
    }

    private void cargarProveedoresEnCombo() {
        cmbProveedor.removeAllItems();
        cmbProveedor.addItem("Particular / Sin Seguro");
        modelo.ProveedorDAO pDao = new modelo.ProveedorDAO();
        List<String[]> lista = pDao.obtenerProveedores();
        for (String[] p : lista) {
            cmbProveedor.addItem(p[1]); 
        }
    }

    private String obtenerSiguienteNumeroFactura() {
        String siguienteNumero = "0001";
        String sql = "SELECT MAX(CAST(SUBSTR(numero_factura, 5) AS INTEGER)) AS max_num FROM facturas";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                int nuevoNum = (maxNum == 0) ? 1 : maxNum + 1;
                siguienteNumero = String.format("%04d", nuevoNum);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el siguiente número de factura: " + e.getMessage());
        }
        return siguienteNumero;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new VistaFacturacion().setVisible(true));
    }
}