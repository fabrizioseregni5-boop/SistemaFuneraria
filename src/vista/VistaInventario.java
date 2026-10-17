package vista;

import controlador.ControladorInventario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class VistaInventario extends JFrame {

    // Paleta de colores (idéntica a las otras ventanas para consistencia)
    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_BG      = new Color(245, 246, 250);
    private static final Color C_ACCENT  = new Color(39, 174, 96);  // Verde (Guardar)
    private static final Color C_INFO    = new Color(41, 128, 185); // Azul (Editar)
    private static final Color C_DANGER  = new Color(192, 57, 43);  // Rojo (Eliminar)
    private static final Color C_WARN    = new Color(230, 126, 34); // Naranja (Limpiar)
    private static final Color C_MUTED   = new Color(127, 140, 141); // Gris (Volver)

    private JTextField txtCodigo, txtNombre, txtCantidad, txtPrecio, txtBuscar;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnVolver, btnBuscar;
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;

    public VistaInventario() {
        setTitle("Gestión de Inventario - Funeraria Mi Redentor");
        setSize(950, 580); // Un poco más ancho para que respire mejor
        setMinimumSize(new Dimension(850, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_BG);

        // Aplicar fuente global un poco más moderna
        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", fuenteGeneral);
        UIManager.put("TextField.font", fuenteGeneral);
        UIManager.put("Button.font", fuenteGeneral);
        UIManager.put("Table.font", fuenteGeneral);

        // --- PANEL SUPERIOR: Título y Buscador ---
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

        // --- PANEL IZQUIERDO: Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 223, 230)), // Borde derecho sutil
                new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título del Formulario
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
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtCodigo, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Nombre / Artículo:"), gbc);
        txtNombre = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtNombre, gbc);

        // Cantidad
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Cantidad:"), gbc);
        txtCantidad = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtCantidad, gbc);

        // Precio
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Precio ($):"), gbc);
        txtPrecio = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtPrecio, gbc);

        // Espaciador
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panelFormulario.add(Box.createVerticalStrut(10), gbc);

        // Botones (Cuadrícula 2x2)
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setOpaque(false);
        
        btnGuardar = crearBotonEstilizado("Guardar", C_ACCENT, Color.WHITE);
        btnEditar = crearBotonEstilizado("Editar", C_INFO, Color.WHITE);
        btnEliminar = crearBotonEstilizado("Eliminar", C_DANGER, Color.WHITE);
        btnLimpiar = crearBotonEstilizado("Limpiar", C_WARN, Color.WHITE);
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(panelBotones, gbc);

        // Botón Volver (abajo del todo)
        btnVolver = crearBotonEstilizado("Volver al Menú", C_MUTED, Color.WHITE);
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 5, 0, 5); // Más margen arriba
        panelFormulario.add(btnVolver, gbc);

        // Contenedor para empujar el formulario hacia arriba
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);
        panelIzquierdo.setPreferredSize(new Dimension(320, 0));
        add(panelIzquierdo, BorderLayout.WEST);

        // --- PANEL DERECHO: Tabla ---
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(C_BG);
        panelTabla.setBorder(new EmptyBorder(20, 20, 20, 20));

        modeloTabla = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("Código");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Cantidad");
        modeloTabla.addColumn("Precio ($)");

        tablaInventario = new JTable(modeloTabla);
        estilizarTabla(tablaInventario);
        
        JScrollPane scrollPane = new JScrollPane(tablaInventario);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        // Inicializar Controlador (SE MANTIENE INTACTO)
        new ControladorInventario(this);
    }

    // ==========================================
    // MÉTODOS AUXILIARES DE DISEÑO
    // ==========================================
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

        // Centrar ciertas columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Código
        tabla.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Cantidad
        
        // Alinear a la derecha la columna de precio
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tabla.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Precio

        // Anchos
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
    }

    // ==========================================
    // GETTERS Y MÉTODOS PÚBLICOS (INTACTOS)
    // ==========================================
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnVolver() { return btnVolver; }
    public JButton getBtnBuscar() { return btnBuscar; }
    
    public JTextField getTxtCodigo() { return txtCodigo; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtCantidad() { return txtCantidad; }
    public JTextField getTxtPrecio() { return txtPrecio; }
    public JTextField getTxtBuscar() { return txtBuscar; }
    
    public JTable getTablaInventario() { return tablaInventario; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    public void limpiarCampos() {
        txtCodigo.setText("");
        txtCodigo.setEnabled(true);
        txtNombre.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtBuscar.setText("");
        txtCodigo.requestFocus(); // Un plus: pone el cursor en el código al limpiar
    }
}