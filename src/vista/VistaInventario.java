package vista;

import controlador.ControladorInventario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class VistaInventario extends JFrame {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_BG      = new Color(245, 246, 250);
    private static final Color C_ACCENT  = new Color(39, 174, 96);
    private static final Color C_INFO    = new Color(41, 128, 185);
    private static final Color C_DANGER  = new Color(192, 57, 43);
    private static final Color C_WARN    = new Color(230, 126, 34);
    private static final Color C_MUTED   = new Color(127, 140, 141);

    private JTextField txtCodigo, txtNombre, txtCantidad, txtPrecioCompra, txtPrecioVenta, txtBuscar;
    private JTextField txtTasaBcv, txtTasaBinance;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnVolver, btnBuscar, btnGuardarTasa;
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;

    private JLabel lblMontoBcv, lblMontoBinance;

    public VistaInventario() {
        setTitle("Gestión de Inventario - Funeraria Mi Redentor");
        setSize(1000, 700); 
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_BG);

        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", fuenteGeneral);
        UIManager.put("TextField.font", fuenteGeneral);
        UIManager.put("Button.font", fuenteGeneral);
        UIManager.put("Table.font", fuenteGeneral);

        // --- HEADER ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(C_PRIMARY);
        panelSuperior.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("Módulo de Control de Inventario");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBuscador.setOpaque(false);
        JLabel lblFiltrar = new JLabel("Filtrar:");
        lblFiltrar.setForeground(new Color(189, 195, 199));
        panelBuscador.add(lblFiltrar);
        txtBuscar = new JTextField(15);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230)),
                new EmptyBorder(5, 8, 5, 8)));
        panelBuscador.add(txtBuscar);
        btnBuscar = crearBotonEstilizado("Buscar", C_INFO, Color.WHITE);
        panelBuscador.add(btnBuscar);
        
        panelSuperior.add(panelBuscador, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // --- FORMULARIO IZQUIERDO ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 223, 230)), 
                new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSub = new JLabel("Datos del Producto");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(C_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelFormulario.add(lblSub, gbc);
        gbc.gridwidth = 1;

        // Código
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Código:"), gbc);
        txtCodigo = crearTextFieldEstilizado();
        ((AbstractDocument) txtCodigo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[a-zA-Z0-9-]*")) super.replace(fb, offset, length, text, attrs);
            }
        });
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtCodigo, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Nombre / Artículo:"), gbc);
        txtNombre = crearTextFieldEstilizado();
        ((AbstractDocument) txtNombre.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (newText.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]*$") && newText.length() <= 25) super.replace(fb, offset, length, text, attrs);
            }
        });
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtNombre, gbc);

        // Cantidad
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Cantidad:"), gbc);
        txtCantidad = crearTextFieldEstilizado();
        ((AbstractDocument) txtCantidad.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtCantidad, gbc);

        // Precio Compra
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Precio de Compra ($):"), gbc);
        txtPrecioCompra = crearTextFieldEstilizado();
        ((AbstractDocument) txtPrecioCompra.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*\\.?\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtPrecioCompra, gbc);

        // Precio Venta
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Precio de Venta ($):"), gbc);
        txtPrecioVenta = crearTextFieldEstilizado();
        ((AbstractDocument) txtPrecioVenta.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*\\.?\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtPrecioVenta, gbc);

        // --- PANEL DE TASAS Y CONVERSIÓN ---
        JPanel panelConversion = new JPanel(new GridLayout(4, 2, 5, 8));
        panelConversion.setBackground(new Color(248, 249, 250));
        panelConversion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        panelConversion.add(new JLabel("Tasa BCV (Bs):"));
        JPanel panelBcv = new JPanel(new BorderLayout());
        panelBcv.setOpaque(false);
        txtTasaBcv = new JTextField("0.00");
        txtTasaBcv.setHorizontalAlignment(JTextField.RIGHT);
        panelBcv.add(txtTasaBcv, BorderLayout.CENTER);
        panelConversion.add(panelBcv);

        panelConversion.add(new JLabel("Monto Venta Bs (BCV):"));
        lblMontoBcv = new JLabel("0.00 Bs.");
        lblMontoBcv.setForeground(C_ACCENT);
        lblMontoBcv.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelConversion.add(lblMontoBcv);

        panelConversion.add(new JLabel("Tasa Binance (Bs):"));
        JPanel panelBinance = new JPanel(new BorderLayout());
        panelBinance.setOpaque(false);
        txtTasaBinance = new JTextField("0.00");
        txtTasaBinance.setHorizontalAlignment(JTextField.RIGHT);
        panelBinance.add(txtTasaBinance, BorderLayout.CENTER);
        panelConversion.add(panelBinance);

        panelConversion.add(new JLabel("Monto Venta Bs (Binance):"));
        lblMontoBinance = new JLabel("0.00 Bs.");
        lblMontoBinance.setForeground(C_INFO);
        lblMontoBinance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelConversion.add(lblMontoBinance);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        panelFormulario.add(panelConversion, gbc);

        // Botón para guardar la tasa (junto con el de editar)
        gbc.gridy = 7;
        btnGuardarTasa = crearBotonEstilizado("Guardar / Editar Tasa", new Color(241, 196, 15), Color.BLACK);
        panelFormulario.add(btnGuardarTasa, gbc);

        // Espaciador
        gbc.gridy = 8;
        panelFormulario.add(Box.createVerticalStrut(5), gbc);

        // Botones Acción
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setOpaque(false);
        btnGuardar = crearBotonEstilizado("Guardar Prod.", C_ACCENT, Color.WHITE);
        btnEditar = crearBotonEstilizado("Editar", C_INFO, Color.WHITE);
        btnEliminar = crearBotonEstilizado("Eliminar", C_DANGER, Color.WHITE);
        btnLimpiar = crearBotonEstilizado("Limpiar", C_WARN, Color.WHITE);
        panelBotones.add(btnGuardar); panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar); panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(panelBotones, gbc);

        btnVolver = crearBotonEstilizado("Volver al Menú", C_MUTED, Color.WHITE);
        gbc.gridy = 10; gbc.insets = new Insets(15, 5, 0, 5);
        panelFormulario.add(btnVolver, gbc);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);
        panelIzquierdo.setPreferredSize(new Dimension(380, 0));
        add(panelIzquierdo, BorderLayout.WEST);

        // --- TABLA DERECHA ---
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(C_BG);
        panelTabla.setBorder(new EmptyBorder(20, 20, 20, 20));

        modeloTabla = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("Código");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Cantidad");
        modeloTabla.addColumn("P. Compra ($)");
        modeloTabla.addColumn("P. Venta ($)"); // Nueva columna

        tablaInventario = new JTable(modeloTabla);
        estilizarTabla(tablaInventario);
        
        JScrollPane scrollPane = new JScrollPane(tablaInventario);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        // Eventos de cálculo
        DocumentListener listenerCalculo = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calcularConversion(); }
            public void removeUpdate(DocumentEvent e) { calcularConversion(); }
            public void changedUpdate(DocumentEvent e) { calcularConversion(); }
        };
        txtPrecioVenta.getDocument().addDocumentListener(listenerCalculo); // Se calcula en base al precio de venta
        txtTasaBcv.getDocument().addDocumentListener(listenerCalculo);
        txtTasaBinance.getDocument().addDocumentListener(listenerCalculo);

        // Lógica para el botón de editar/guardar tasas
        btnGuardarTasa.addActionListener(e -> {
            if (txtTasaBcv.isEditable()) {
                // Si está editable, el botón dice "Guardar"
                btnGuardarTasa.setText("Guardar Tasa");
            } else {
                // Si está bloqueado, el botón dice "Editar Tasa"
                bloquearTasas(false);
                btnGuardarTasa.setText("Guardar Tasa");
            }
        });

        new ControladorInventario(this);
    }

    public void bloquearTasas(boolean bloquear) {
        txtTasaBcv.setEditable(!bloquear);
        txtTasaBinance.setEditable(!bloquear);
        if (bloquear) {
            txtTasaBcv.setBackground(new Color(240, 240, 240));
            txtTasaBinance.setBackground(new Color(240, 240, 240));
            btnGuardarTasa.setText("Editar Tasa");
        } else {
            txtTasaBcv.setBackground(Color.WHITE);
            txtTasaBinance.setBackground(Color.WHITE);
            btnGuardarTasa.setText("Guardar Tasa");
        }
    }

    private void calcularConversion() {
        try {
            String precioStr = txtPrecioVenta.getText().trim();
            String tasaBcvStr = txtTasaBcv.getText().trim();
            String tasaBinStr = txtTasaBinance.getText().trim();

            if (precioStr.isEmpty() || precioStr.equals(".")) {
                lblMontoBcv.setText("0.00 Bs.");
                lblMontoBinance.setText("0.00 Bs.");
                return;
            }

            double precio = Double.parseDouble(precioStr);
            double tasaBcv = tasaBcvStr.isEmpty() || tasaBcvStr.equals(".") ? 0 : Double.parseDouble(tasaBcvStr);
            double tasaBinance = tasaBinStr.isEmpty() || tasaBinStr.equals(".") ? 0 : Double.parseDouble(tasaBinStr);

            lblMontoBcv.setText(String.format("%,.2f Bs.", precio * tasaBcv));
            lblMontoBinance.setText(String.format("%,.2f Bs.", precio * tasaBinance));
        } catch (NumberFormatException ignored) {}
    }

    // --- GETTERS PARA EL CONTROLADOR ---
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnVolver() { return btnVolver; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnGuardarTasa() { return btnGuardarTasa; }
    
    public JTextField getTxtCodigo() { return txtCodigo; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtCantidad() { return txtCantidad; }
    public JTextField getTxtPrecioCompra() { return txtPrecioCompra; }
    public JTextField getTxtPrecioVenta() { return txtPrecioVenta; }
    public JTextField getTxtTasaBcv() { return txtTasaBcv; }
    public JTextField getTxtTasaBinance() { return txtTasaBinance; }
    public JTextField getTxtBuscar() { return txtBuscar; }
    
    public JTable getTablaInventario() { return tablaInventario; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    public void limpiarCampos() {
        txtCodigo.setText("");
        txtCodigo.setEnabled(true);
        txtNombre.setText("");
        txtCantidad.setText("");
        txtPrecioCompra.setText("");
        txtPrecioVenta.setText("");
        txtBuscar.setText("");
        lblMontoBcv.setText("0.00 Bs.");
        lblMontoBinance.setText("0.00 Bs.");
        txtCodigo.requestFocus(); 
    }

    // --- MÉTODOS AUXILIARES DE DISEÑO ---
    private JTextField crearTextFieldEstilizado() {
        JTextField tf = new JTextField(15);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    private JButton crearBotonEstilizado(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 35));
        return btn;
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(30);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(41, 128, 185, 50));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tabla.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setBackground(new Color(44, 62, 80));
                l.setForeground(Color.WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setOpaque(true);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(new EmptyBorder(0, 8, 0, 8));
                return l;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
        tabla.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); 

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tabla.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); 
        tabla.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); 

        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
    }
}