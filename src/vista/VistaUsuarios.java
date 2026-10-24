package vista;

import controlador.ControladorUsuarios;
import modelo.Sesion;
import modelo.ValidadorPassword;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class VistaUsuarios extends JFrame {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_BG      = new Color(245, 246, 250);
    private static final Color C_ACCENT  = new Color(39, 174, 96);
    private static final Color C_INFO    = new Color(41, 128, 185);
    private static final Color C_DANGER  = new Color(192, 57, 43);
    private static final Color C_WARN    = new Color(230, 126, 34);
    private static final Color C_MUTED   = new Color(127, 140, 141);

    public JTextField txtUser, txtNombre;
    public JPasswordField txtPass;
    public JComboBox<String> cmbRol;
    public JLabel lblComplejidad;
    public JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar, btnVerPass, btnVolverMenu;
    public int idSeleccionado = -1;
    public char defaultEchoChar;

    public JTable tabla;
    public DefaultTableModel modelo;
    public JTextField txtBuscar;
    
    public JLabel lblKpiTotal, lblKpiMaster, lblKpiAdmin, lblKpiUser;

    public VistaUsuarios() {
        setTitle("Gestión de Usuarios y Roles");
        setSize(1150, 720);
        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_BG);

        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", fuenteGeneral);
        UIManager.put("TextField.font", fuenteGeneral);
        UIManager.put("Button.font", fuenteGeneral);

        // ==========================================
        // 1. PANEL SUPERIOR (KPIs)
        // ==========================================
        JPanel panelKpis = new JPanel(new GridLayout(1, 4, 15, 0));
        panelKpis.setBackground(C_BG);
        panelKpis.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblKpiTotal = new JLabel("0");
        lblKpiMaster = new JLabel("0");
        lblKpiAdmin = new JLabel("0");
        lblKpiUser = new JLabel("0");

        panelKpis.add(crearTarjetaKpi("Total usuarios", lblKpiTotal, C_INFO));
        panelKpis.add(crearTarjetaKpi("Masters", lblKpiMaster, new Color(142, 68, 173)));
        panelKpis.add(crearTarjetaKpi("Administradores", lblKpiAdmin, C_ACCENT));
        panelKpis.add(crearTarjetaKpi("Usuarios Normales", lblKpiUser, C_WARN));

        add(panelKpis, BorderLayout.NORTH);

        // ==========================================
        // 2. PANEL CENTRAL (Formulario + Tabla)
        // ==========================================
        JPanel panelCentral = new JPanel(new BorderLayout(15, 0));
        panelCentral.setBackground(C_BG);
        panelCentral.setBorder(new EmptyBorder(0, 20, 20, 20));

        // --- 2.1 FORMULARIO (Izquierda) ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setPreferredSize(new Dimension(340, 0));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230)),
                new EmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;

        JLabel lblTitForm = new JLabel("Datos del Usuario");
        lblTitForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitForm.setForeground(C_PRIMARY);
        panelForm.add(lblTitForm, g); g.gridy++;

        // --- FILTRO USUARIO ---
        panelForm.add(new JLabel("Usuario:"), g); g.gridy++;
        txtUser = new JTextField(15);
        ((AbstractDocument) txtUser.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                // Solo letras, números y máximo 20 caracteres
                if (newText.matches("^[a-zA-Z0-9]*$") && newText.length() <= 20) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        panelForm.add(txtUser, g); g.gridy++;

        // --- FILTRO NOMBRE COMPLETO ---
        panelForm.add(new JLabel("Nombre completo:"), g); g.gridy++;
        txtNombre = new JTextField(15);
        ((AbstractDocument) txtNombre.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                // Solo letras y espacios
                if (newText.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        panelForm.add(txtNombre, g); g.gridy++;

        panelForm.add(new JLabel("Contraseña:"), g); g.gridy++;
        JPanel panelPass = new JPanel(new BorderLayout(5, 0));
        panelPass.setOpaque(false);
        txtPass = new JPasswordField(15);
        defaultEchoChar = txtPass.getEchoChar();
        btnVerPass = new JButton("Ver");
        btnVerPass.setMargin(new Insets(0, 5, 0, 5));
        btnVerPass.setFocusPainted(false);
        btnVerPass.addActionListener(e -> {
            if (txtPass.getEchoChar() == 0) {
                txtPass.setEchoChar(defaultEchoChar);
                btnVerPass.setText("Ver");
            } else {
                txtPass.setEchoChar((char) 0);
                btnVerPass.setText("Ocultar");
            }
        });
        panelPass.add(txtPass, BorderLayout.CENTER);
        panelPass.add(btnVerPass, BorderLayout.EAST);
        panelForm.add(panelPass, g); g.gridy++;

        lblComplejidad = new JLabel("Seguridad: Esperando...");
        lblComplejidad.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblComplejidad.setForeground(Color.GRAY);
        panelForm.add(lblComplejidad, g); g.gridy++;

        txtPass.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarMedidor(); }
            public void removeUpdate(DocumentEvent e) { actualizarMedidor(); }
            public void changedUpdate(DocumentEvent e) { actualizarMedidor(); }
        });

        panelForm.add(new JLabel("Rol:"), g); g.gridy++;
        cmbRol = new JComboBox<>(new String[]{"Master", "Administrador", "Usuario"});
        if (Sesion.rolActual == 2) cmbRol.removeItemAt(0);
        panelForm.add(cmbRol, g); g.gridy++;

        g.insets = new Insets(15, 5, 5, 5);
        JPanel panelBotones1 = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones1.setOpaque(false);
        btnGuardar = crearBoton("Guardar", C_ACCENT);
        btnActualizar = crearBoton("Actualizar", C_INFO);
        btnEliminar = crearBoton("Eliminar", C_DANGER);
        btnLimpiar = crearBoton("Limpiar", C_MUTED);
        panelBotones1.add(btnGuardar); panelBotones1.add(btnActualizar); 
        panelBotones1.add(btnEliminar); panelBotones1.add(btnLimpiar);
        panelForm.add(panelBotones1, g); g.gridy++;

        g.insets = new Insets(15, 5, 5, 5);
        btnVolverMenu = crearBoton("Volver al Menú Principal", new Color(100, 100, 100));
        panelForm.add(btnVolverMenu, g);

        panelCentral.add(panelForm, BorderLayout.WEST);

        // --- 2.2 TABLA (Derecha) ---
        JPanel panelTabla = new JPanel(new BorderLayout(0, 10));
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel panelTablaHeader = new JPanel(new BorderLayout());
        panelTablaHeader.setOpaque(false);
        
        JLabel lblTitTabla = new JLabel("Usuarios Registrados");
        lblTitTabla.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitTabla.setForeground(C_PRIMARY);
        panelTablaHeader.add(lblTitTabla, BorderLayout.WEST);

        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBuscar.setOpaque(false);
        panelBuscar.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(15);
        panelBuscar.add(txtBuscar);
        panelTablaHeader.add(panelBuscar, BorderLayout.EAST);

        panelTabla.add(panelTablaHeader, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Usuario", "Nombre completo", "Contraseña", "Rol"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        estilizarTabla();
        
        // Ocultamos solo el ID
        tabla.removeColumn(tabla.getColumnModel().getColumn(0));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        panelTabla.add(scroll, BorderLayout.CENTER);

        panelCentral.add(panelTabla, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        new ControladorUsuarios(this);
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, color),
                new EmptyBorder(10, 15, 10, 15)
        ));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        JLabel lblTit = new JLabel(titulo);
        lblTit.setForeground(Color.GRAY);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValor.setForeground(color);
        
        textPanel.add(lblTit);
        textPanel.add(lblValor);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }

    private JButton crearBoton(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return btn;
    }

    private void estilizarTabla() {
        tabla.setRowHeight(30);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(230, 230, 230));
        
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 35));
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(C_PRIMARY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                return label;
            }
        });
    }

    private void actualizarMedidor() {
        String pass = new String(txtPass.getPassword());
        String estado = ValidadorPassword.evaluar(pass);
        switch (estado) {
            case "Vacia" -> { lblComplejidad.setText("Seguridad:"); lblComplejidad.setForeground(Color.GRAY); }
            case "Debil" -> { lblComplejidad.setText("Seguridad: Débil"); lblComplejidad.setForeground(C_DANGER); }
            case "Media" -> { lblComplejidad.setText("Seguridad: Media"); lblComplejidad.setForeground(C_WARN); }
            case "Fuerte" -> { lblComplejidad.setText("Seguridad: Fuerte ¡Correcta!"); lblComplejidad.setForeground(C_ACCENT); }
        }
    }

    public void limpiar() {
        txtUser.setText(""); txtNombre.setText(""); txtPass.setText("");
        idSeleccionado = -1;
        if (cmbRol.getItemCount() > 0) cmbRol.setSelectedIndex(0);
        lblComplejidad.setText("Seguridad: Esperando...");
        lblComplejidad.setForeground(Color.GRAY);
        tabla.clearSelection();
        txtUser.setEditable(true);
    }
}