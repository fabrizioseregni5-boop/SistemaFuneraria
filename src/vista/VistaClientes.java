package vista;

import controlador.ControladorClientes;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class VistaClientes extends JFrame {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_BG      = new Color(245, 246, 250);
    private static final Color C_ACCENT  = new Color(39, 174, 96);
    private static final Color C_INFO    = new Color(41, 128, 185);
    private static final Color C_DANGER  = new Color(192, 57, 43);
    private static final Color C_WARN    = new Color(230, 126, 34);
    private static final Color C_PURPLE  = new Color(142, 68, 173);
    private static final Color C_MUTED   = new Color(127, 140, 141);

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

        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", fuenteGeneral);
        UIManager.put("TextField.font", fuenteGeneral);
        UIManager.put("Button.font", fuenteGeneral);
        UIManager.put("Table.font", fuenteGeneral);

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

        JLabel lblSub = new JLabel("Datos del Cliente");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(C_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelFormulario.add(lblSub, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Cédula / Documento:"), gbc);
        txtCedula = crearTextFieldEstilizado();
        txtCedula.setText("V-");
        
        // FILTRO PARA CÉDULA: Evita que borren el "V-"
        ((AbstractDocument) txtCedula.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                // Solo permite "V-" seguido de números
                if (newText.matches("^V-\\d*$")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                // Evita que borren la "V" o el "-"
                if (offset >= 2) {
                    super.remove(fb, offset, length);
                }
            }
        });
        
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtCedula, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Nombre Completo:"), gbc);
        txtNombre = crearTextFieldEstilizado();
        gbc.gridx = 1; gbc.weightx = 1;
        panelFormulario.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        txtTelefono = crearTextFieldEstilizado();
        
        // FILTRO PARA TELÉFONO: Solo números
        // FILTRO PARA TELÉFONO: Solo números y máximo 11 dígitos
        ((AbstractDocument) txtTelefono.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                
                // Permite solo números y limita la longitud a 11
                if (text.matches("\\d*") && newText.length() <= 11) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        
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

        gbc.gridy = 7;
        panelFormulario.add(Box.createVerticalStrut(10), gbc);

        btnFamiliares = crearBotonEstilizado("Gestionar Familiares", C_PURPLE, Color.WHITE);
        gbc.gridy = 8;
        panelFormulario.add(btnFamiliares, gbc);

        btnVolver = crearBotonEstilizado("Volver al Menú", C_MUTED, Color.WHITE);
        gbc.gridy = 9;
        panelFormulario.add(btnVolver, gbc);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);
        panelIzquierdo.setPreferredSize(new Dimension(340, 0));
        add(panelIzquierdo, BorderLayout.WEST);

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

        btnFamiliares.addActionListener(e -> {
            int filaSeleccionada = tablaClientes.getSelectedRow();
            String cedulaTitular = "";

            if (filaSeleccionada != -1) {
                cedulaTitular = tablaClientes.getValueAt(filaSeleccionada, 0).toString();
            } else {
                cedulaTitular = txtCedula.getText().trim();
            }

            if (cedulaTitular.isEmpty() || cedulaTitular.equals("V-")) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor, seleccione un cliente de la tabla o ingrese su cédula para gestionar sus familiares.", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            new VistaFamiliares(cedulaTitular).setVisible(true);
        });

        new ControladorClientes(this);
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
                l.setBackground(C_PRIMARY);
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

        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(250);
    }

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
        txtCedula.setText("V-"); // Restablece el prefijo
        txtCedula.setEnabled(true); 
        txtNombre.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtBuscar.setText("");
        txtCedula.requestFocus(); 
    }
}