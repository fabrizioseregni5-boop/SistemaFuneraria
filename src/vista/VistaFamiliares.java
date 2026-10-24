package vista;

import modelo.ConexionBD;
import modelo.FamiliarDAO;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class VistaFamiliares extends JFrame {

    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_BG      = new Color(245, 246, 250);
    private static final Color C_ACCENT  = new Color(39, 174, 96);
    private static final Color C_INFO    = new Color(41, 128, 185);
    private static final Color C_DANGER  = new Color(192, 57, 43);
    private static final Color C_MUTED   = new Color(127, 140, 141);

    private JTextField txtCedulaTitular, txtNombres, txtApellidos, txtParentesco, txtCedulaFamiliar, txtDireccion, txtBuscarFamiliar;
    private JTable tablaFamiliares;
    private DefaultTableModel modeloTabla;
    private FamiliarDAO familiarDAO;
    private JButton btnGuardar, btnEditar, btnEliminar, btnVolverClientes;
    private int idFamiliarSeleccionado = -1;

    public VistaFamiliares(String cedulaTitularPrevia) {
        familiarDAO = new FamiliarDAO();
        
        setTitle("Gestión de Familiares / Beneficiarios del Seguro");
        setSize(980, 680); 
        setMinimumSize(new Dimension(850, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_BG);

        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", fuenteGeneral);
        UIManager.put("TextField.font", fuenteGeneral);
        UIManager.put("Button.font", fuenteGeneral);
        UIManager.put("Table.font", fuenteGeneral);

        // --- 1. HEADER ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(C_PRIMARY);
        panelSuperior.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("Gestión de Familiares del Seguro");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        // Obtener el nombre del titular
        String nombreTitular = obtenerNombreTitular(cedulaTitularPrevia);
        JLabel lblTitularActual = new JLabel("Titular: " + cedulaTitularPrevia + " - " + nombreTitular);
        lblTitularActual.setForeground(new Color(189, 195, 199));
        lblTitularActual.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelSuperior.add(lblTitularActual, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // --- 2. PANEL PRINCIPAL ---
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(C_BG);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 2.1 Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)), 
                        " Datos del Familiar ", 0, 0, new Font("Segoe UI", Font.BOLD, 13), C_PRIMARY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Cédula del Titular:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtCedulaTitular = crearTextFieldEstilizado();
        txtCedulaTitular.setText(cedulaTitularPrevia);
        txtCedulaTitular.setEditable(false);
        txtCedulaTitular.setBackground(new Color(240, 240, 240));
        panelFormulario.add(txtCedulaTitular, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Nombres:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtNombres = crearTextFieldEstilizado();
        aplicarFiltroLetras(txtNombres);
        panelFormulario.add(txtNombres, gbc);

        // Fila 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtApellidos = crearTextFieldEstilizado();
        aplicarFiltroLetras(txtApellidos);
        panelFormulario.add(txtApellidos, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Parentesco (Ej. Cónyuge):"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtParentesco = crearTextFieldEstilizado();
        aplicarFiltroLetras(txtParentesco);
        panelFormulario.add(txtParentesco, gbc);

        // Fila 2
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Cédula del Familiar:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtCedulaFamiliar = crearTextFieldEstilizado();
        
        // CONFIGURACIÓN DE FILTRO PARA CÉDULA
        txtCedulaFamiliar.setText("V-");
        ((AbstractDocument) txtCedulaFamiliar.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (newText.matches("^V-\\d{0,8}$")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                if (offset >= 2) {
                    super.remove(fb, offset, length);
                }
            }
        });
        
        panelFormulario.add(txtCedulaFamiliar, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panelFormulario.add(new JLabel("Dirección / Ubicación:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtDireccion = crearTextFieldEstilizado();
        panelFormulario.add(txtDireccion, gbc);

        // Fila 3: Botones de Acción
        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotonesAccion.setOpaque(false);
        
        btnGuardar = crearBotonEstilizado("Guardar Nuevo", C_ACCENT, Color.WHITE);
        btnEditar = crearBotonEstilizado("Actualizar Cambios", C_INFO, Color.WHITE);
        btnEditar.setEnabled(false);
        
        panelBotonesAccion.add(btnGuardar);
        panelBotonesAccion.add(btnEditar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 10, 0, 10);
        panelFormulario.add(panelBotonesAccion, gbc);

        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);

        // --- 2.2 Tabla y Filtro ---
        JPanel panelTablaContenedor = new JPanel(new BorderLayout(0, 10));
        panelTablaContenedor.setOpaque(false);

        JPanel panelHerramientas = new JPanel(new BorderLayout());
        panelHerramientas.setOpaque(false);

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelFiltro.setOpaque(false);
        panelFiltro.add(new JLabel("Buscar Familiar:"));
        txtBuscarFamiliar = crearTextFieldEstilizado();
        txtBuscarFamiliar.setPreferredSize(new Dimension(250, 30));
        panelFiltro.add(txtBuscarFamiliar);
        panelHerramientas.add(panelFiltro, BorderLayout.WEST);

        // Botón Volver a Clientes y Eliminar
        JPanel panelBotonesDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotonesDer.setOpaque(false);
        
        btnVolverClientes = crearBotonEstilizado("Volver a Clientes", C_MUTED, Color.WHITE);
        btnEliminar = crearBotonEstilizado("Eliminar Seleccionado", C_DANGER, Color.WHITE);
        
        panelBotonesDer.add(btnVolverClientes);
        panelBotonesDer.add(btnEliminar);
        
        panelHerramientas.add(panelBotonesDer, BorderLayout.EAST);

        panelTablaContenedor.add(panelHerramientas, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombres", "Apellidos", "Parentesco", "Cédula", "Dirección"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaFamiliares = new JTable(modeloTabla);
        estilizarTabla(tablaFamiliares);
        tablaFamiliares.removeColumn(tablaFamiliares.getColumnModel().getColumn(0)); 

        JScrollPane scroll = new JScrollPane(tablaFamiliares);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
        scroll.getViewport().setBackground(Color.WHITE);
        
        panelTablaContenedor.add(scroll, BorderLayout.CENTER);
        panelPrincipal.add(panelTablaContenedor, BorderLayout.CENTER);

        add(panelPrincipal, BorderLayout.CENTER);

        // --- EVENTOS ---
        if (cedulaTitularPrevia != null && !cedulaTitularPrevia.isEmpty()) {
            cargarTabla(cedulaTitularPrevia, "");
        }

        tablaFamiliares.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int fila = tablaFamiliares.getSelectedRow();
                if (fila != -1) {
                    int filaModelo = tablaFamiliares.convertRowIndexToModel(fila);
                    idFamiliarSeleccionado = Integer.parseInt(modeloTabla.getValueAt(filaModelo, 0).toString());
                    
                    txtNombres.setText(modeloTabla.getValueAt(filaModelo, 1).toString());
                    txtApellidos.setText(modeloTabla.getValueAt(filaModelo, 2).toString());
                    txtParentesco.setText(modeloTabla.getValueAt(filaModelo, 3).toString());
                    
                    Object cedFam = modeloTabla.getValueAt(filaModelo, 4);
                    txtCedulaFamiliar.setText(cedFam != null ? cedFam.toString() : "");

                    Object dirFam = modeloTabla.getValueAt(filaModelo, 5);
                    txtDireccion.setText(dirFam != null ? dirFam.toString() : "");

                    btnGuardar.setEnabled(false);
                    btnEditar.setEnabled(true);
                }
            }
        });

        // --- ACCIÓN GUARDAR ---
        btnGuardar.addActionListener(e -> {
            String tit = txtCedulaTitular.getText().trim();
            String nom = txtNombres.getText().trim();
            String ape = txtApellidos.getText().trim();
            String par = txtParentesco.getText().trim();
            String ced = txtCedulaFamiliar.getText().trim();
            String dir = txtDireccion.getText().trim();

            if (tit.isEmpty() || nom.isEmpty() || par.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar al menos los nombres y el parentesco.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // VALIDACIÓN EXTRA: Letras en Nombres, Apellidos y Parentesco
            if (!nom.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$") || 
                !ape.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$") || 
                !par.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
                JOptionPane.showMessageDialog(this, "Los Nombres, Apellidos y Parentesco solo deben contener letras.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ced.isEmpty() && !ced.equals("V-")) {
                if (!ced.matches("^V-\\d{7,8}$")) {
                    JOptionPane.showMessageDialog(this, "La cédula del familiar es inválida. Debe tener entre 7 y 8 números después del V-.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (familiarDAO.existeCedulaFamiliar(ced, -1)) {
                    JOptionPane.showMessageDialog(this, "Esta cédula ya está registrada para otro familiar.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            List<String[]> actuales = familiarDAO.obtenerFamiliaresPorTitular(tit);
            if (actuales.size() >= 10) {
                JOptionPane.showMessageDialog(this, "⚠️ Este titular ya cuenta con el límite máximo de 10 familiares registrados.", "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean exito = familiarDAO.insertarFamiliar(tit, nom, ape, par, ced, dir);
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Familiar agregado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla(tit, "");
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el familiar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- ACCIÓN EDITAR ---
        btnEditar.addActionListener(e -> {
            if (idFamiliarSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un familiar de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nom = txtNombres.getText().trim();
            String ape = txtApellidos.getText().trim();
            String par = txtParentesco.getText().trim();
            String ced = txtCedulaFamiliar.getText().trim();
            String dir = txtDireccion.getText().trim();

            if (nom.isEmpty() || par.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar al menos los nombres y el parentesco.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // VALIDACIÓN EXTRA: Letras en Nombres, Apellidos y Parentesco
            if (!nom.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$") || 
                !ape.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$") || 
                !par.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
                JOptionPane.showMessageDialog(this, "Los Nombres, Apellidos y Parentesco solo deben contener letras.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ced.isEmpty() && !ced.equals("V-")) {
                if (!ced.matches("^V-\\d{7,8}$")) {
                    JOptionPane.showMessageDialog(this, "La cédula del familiar es inválida. Debe tener entre 7 y 8 números después del V-.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (familiarDAO.existeCedulaFamiliar(ced, idFamiliarSeleccionado)) {
                    JOptionPane.showMessageDialog(this, "Esta cédula ya está registrada para otro familiar.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            boolean exito = familiarDAO.actualizarFamiliar(idFamiliarSeleccionado, nom, ape, par, ced, dir);
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Familiar actualizado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla(cedulaTitularPrevia, "");
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el familiar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- ACCIÓN ELIMINAR ---
        btnEliminar.addActionListener(e -> {
            int fila = tablaFamiliares.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione de la tabla el familiar que desea eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar este familiar del seguro?", 
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                int filaModelo = tablaFamiliares.convertRowIndexToModel(fila);
                int idEliminar = Integer.parseInt(modeloTabla.getValueAt(filaModelo, 0).toString());

                boolean exito = familiarDAO.eliminarFamiliar(idEliminar);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Familiar eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarTabla(cedulaTitularPrevia, "");
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al intentar eliminar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnVolverClientes.addActionListener(e -> dispose());

        txtBuscarFamiliar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                cargarTabla(cedulaTitularPrevia, txtBuscarFamiliar.getText().trim());
            }
        });
    }

    private String obtenerNombreTitular(String cedula) {
        if (cedula == null || cedula.isEmpty()) return "";
        String sql = "SELECT nombre FROM clientes WHERE cedula = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar nombre del titular: " + e.getMessage());
        }
        return "Desconocido";
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
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 35));
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
    }

    private void cargarTabla(String cedulaTitular, String filtro) {
        modeloTabla.setRowCount(0);
        List<String[]> lista = familiarDAO.obtenerFamiliaresPorTitular(cedulaTitular);
        for (String[] fam : lista) {
            if (filtro.isEmpty() || 
                fam[1].toLowerCase().contains(filtro.toLowerCase()) || 
                fam[2].toLowerCase().contains(filtro.toLowerCase()) || 
                fam[3].toLowerCase().contains(filtro.toLowerCase()) || 
                (fam[4] != null && fam[4].toLowerCase().contains(filtro.toLowerCase())) ||
                (fam[5] != null && fam[5].toLowerCase().contains(filtro.toLowerCase()))) {
                modeloTabla.addRow(fam);
            }
        }
    }

    private void limpiarFormulario() {
        txtNombres.setText("");
        txtApellidos.setText("");
        txtParentesco.setText("");
        txtCedulaFamiliar.setText("V-");
        txtDireccion.setText("");
        idFamiliarSeleccionado = -1;
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        tablaFamiliares.clearSelection();
        txtNombres.requestFocus();
    }
    
    // Método para que solo se puedan escribir letras y espacios
    private void aplicarFiltroLetras(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                
                // Solo permite letras (mayúsculas, minúsculas, acentos, ñ) y espacios
                if (newText.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}