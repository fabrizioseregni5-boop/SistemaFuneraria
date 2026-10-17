package vista;

import controlador.ControladorClientes;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class VistaClientes extends JFrame {

    // Paleta de colores unificada para todo el sistema
    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_BG      = new Color(245, 246, 250);
    private static final Color C_ACCENT  = new Color(39, 174, 96);  // Verde (Guardar)
    private static final Color C_INFO    = new Color(41, 128, 185); // Azul (Editar)
    private static final Color C_DANGER  = new Color(192, 57, 43);  // Rojo (Eliminar)
    private static final Color C_WARN    = new Color(230, 126, 34); // Naranja (Limpiar)
    private static final Color C_PURPLE  = new Color(142, 68, 173); // Morado (Familiares)
    private static final Color C_MUTED   = new Color(127, 140, 141); // Gris (Volver)

    private JTextField txtCedula, txtNombre, txtTelefono, txtDireccion, txtBuscar;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnVolver, btnBuscar, btnFamiliares;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;

    public VistaClientes() {
        setTitle("Gestión de Clientes - Funeraria Mi Redentor");
        setSize(950, 580); 
        setMinimumSize(new Dimension(850, 560));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_BG);

        // Aplicar fuente global moderna
        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", fuenteGeneral);
        UIManager.put("TextField.font", fuenteGeneral);
        UIManager.put("Button.font", fuenteGeneral);
        UIManager.put("Table.font", fuenteGeneral);

        // --- PANEL SUPERIOR: Título y Buscador ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(C_PRIMARY);
        panelSuperior.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("Módulo de Registro y Control de Clientes");
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

        // --- PANEL IZQUIERDO: Formulario de Datos ---
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

        // Título del Formulario
        JLabel lblSub = new JLabel("Datos del Cliente");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(C_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelFormulario.add(lblSub, gbc);
        gbc.gridwidth = 1;

        // Cédula
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Cédula / Documento:"), gbc);
        txtCedula = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtCedula, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Nombre Completo:"), gbc);
        txtNombre = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtNombre, gbc);

        // Teléfono
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        txtTelefono = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtTelefono, gbc);

        // Dirección
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Dirección:"), gbc);
        txtDireccion = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtDireccion, gbc);

        // Espaciador
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panelFormulario.add(Box.createVerticalStrut(10), gbc);

        // Botones de Acción Principales (Cuadrícula 2x2)
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

        // Espaciador
        gbc.gridy = 7;
        panelFormulario.add(Box.createVerticalStrut(10), gbc);

        // --- BOTÓN: Gestionar Familiares ---
        btnFamiliares = crearBotonEstilizado("Gestionar Familiares", C_PURPLE, Color.WHITE);
        gbc.gridy = 8;
        panelFormulario.add(btnFamiliares, gbc);

        // Botón Volver independiente abajo
        btnVolver = crearBotonEstilizado("Volver al Menú", C_MUTED, Color.WHITE);
        gbc.gridy = 9;
        panelFormulario.add(btnVolver, gbc);

        // Contenedor para empujar el formulario hacia arriba
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);
        panelIzquierdo.setPreferredSize(new Dimension(340, 0));
        add(panelIzquierdo, BorderLayout.WEST);

        // --- PANEL DERECHO: Tabla para visualizar registros ---
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(C_BG);
        panelTabla.setBorder(new EmptyBorder(20, 20, 20, 20));

        modeloTabla = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("Cédula");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Teléfono");
        modeloTabla.addColumn("Dirección");

        tablaClientes = new JTable(modeloTabla);
        estilizarTabla(tablaClientes);
        
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        // ==========================================
        // LÓGICA INTACTA DEL BOTÓN FAMILIARES
        // ==========================================
        btnFamiliares.addActionListener(e -> {
            int filaSeleccionada = tablaClientes.getSelectedRow();
            String cedulaTitular = "";

            if (filaSeleccionada != -1) {
                cedulaTitular = tablaClientes.getValueAt(filaSeleccionada, 0).toString();
            } else {
                cedulaTitular = txtCedula.getText().trim();
            }

            if (cedulaTitular.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor, seleccione un cliente de la tabla o ingrese su cédula para gestionar sus familiares.", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Abrir la ventana de familiares pasando la cédula del titular
            new VistaFamiliares(cedulaTitular).setVisible(true);
        });

        // Inicializar el controlador de clientes
        new ControladorClientes(this);
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

        // Centrar cédula y teléfono
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
        tabla.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); 

        // Anchos
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(250);
    }

    // --- Métodos Getters públicos (INTACTOS) ---
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnVolver() { return btnVolver; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnFamiliares() { return btnFamiliares; }
    
    public JTextField getTxtCedula() { return txtCedula; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtTelefono() { return txtTelefono; }
    public JTextField getTxtDireccion() { return txtDireccion; }
    public JTextField getTxtBuscar() { return txtBuscar; }
    
    public JTable getTablaClientes() { return tablaClientes; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    public void limpiarCampos() {
        txtCedula.setText("");
        txtCedula.setEnabled(true); // Reactivar cédula al limpiar
        txtNombre.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtBuscar.setText("");
        txtCedula.requestFocus(); // Pone el cursor en la cédula automáticamente
    }
}