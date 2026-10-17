package vista;

import controlador.ControladorProveedores;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class VistaProveedores extends JFrame {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_BG      = new Color(245, 246, 250);
    private static final Color C_ACCENT  = new Color(39, 174, 96);
    private static final Color C_INFO    = new Color(41, 128, 185);
    private static final Color C_DANGER  = new Color(192, 57, 43);
    private static final Color C_WARN    = new Color(230, 126, 34);
    private static final Color C_MUTED   = new Color(127, 140, 141);

    private JTextField txtRif, txtNombre, txtTelefono, txtDireccion, txtBuscar;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnVolver, btnBuscar;
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;

    public VistaProveedores() {
        setTitle("Gestión de Proveedores / Seguros - Funeraria Mi Redentor");
        setSize(950, 580);
        setMinimumSize(new Dimension(850, 560));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_BG);

        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", fuenteGeneral);
        UIManager.put("TextField.font", fuenteGeneral);
        UIManager.put("Button.font", fuenteGeneral);

        // Header
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(C_PRIMARY);
        panelSuperior.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("Módulo de Proveedores y Aseguradoras");
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

        // Formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 223, 230)), 
                new EmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSub = new JLabel("Datos del Proveedor");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(C_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelFormulario.add(lblSub, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulario.add(new JLabel("RIF / Cédula:"), gbc);
        txtRif = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtRif, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Nombre / Empresa:"), gbc);
        txtNombre = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        txtTelefono = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Dirección:"), gbc);
        txtDireccion = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtDireccion, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panelFormulario.add(Box.createVerticalStrut(10), gbc);

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setOpaque(false);
        btnGuardar = crearBotonEstilizado("Guardar", C_ACCENT, Color.WHITE);
        btnEditar = crearBotonEstilizado("Editar", C_INFO, Color.WHITE);
        btnEliminar = crearBotonEstilizado("Eliminar", C_DANGER, Color.WHITE);
        btnLimpiar = crearBotonEstilizado("Limpiar", C_WARN, Color.WHITE);
        panelBotones.add(btnGuardar); panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar); panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(panelBotones, gbc);

        btnVolver = crearBotonEstilizado("Volver al Menú", C_MUTED, Color.WHITE);
        gbc.gridy = 7; gbc.insets = new Insets(20, 5, 0, 5);
        panelFormulario.add(btnVolver, gbc);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);
        panelIzquierdo.setPreferredSize(new Dimension(340, 0));
        add(panelIzquierdo, BorderLayout.WEST);

        // Tabla
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(C_BG);
        panelTabla.setBorder(new EmptyBorder(20, 20, 20, 20));

        modeloTabla = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("RIF");
        modeloTabla.addColumn("Nombre / Empresa");
        modeloTabla.addColumn("Teléfono");
        modeloTabla.addColumn("Dirección");

        tablaProveedores = new JTable(modeloTabla);
        estilizarTabla(tablaProveedores);
        
        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        new ControladorProveedores(this);
    }

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
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(41, 128, 185, 50));
        tabla.setSelectionForeground(Color.BLACK);

        JTableHeader header = tabla.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setBackground(C_PRIMARY); l.setForeground(Color.WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setOpaque(true); l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        });
    }

    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnVolver() { return btnVolver; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JTextField getTxtRif() { return txtRif; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtTelefono() { return txtTelefono; }
    public JTextField getTxtDireccion() { return txtDireccion; }
    public JTextField getTxtBuscar() { return txtBuscar; }
    public JTable getTablaProveedores() { return tablaProveedores; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    public void limpiarCampos() {
        txtRif.setText("");
        txtRif.setEnabled(true);
        txtNombre.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtBuscar.setText("");
        txtRif.requestFocus();
    }
}