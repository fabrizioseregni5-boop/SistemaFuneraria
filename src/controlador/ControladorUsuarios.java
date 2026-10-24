package controlador;

import modelo.Sesion;
import modelo.UsuarioDAO;
import modelo.ValidadorPassword;
import vista.VistaUsuarios;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ControladorUsuarios implements ActionListener {
    private VistaUsuarios vista;
    private UsuarioDAO dao = new UsuarioDAO();

    public ControladorUsuarios(VistaUsuarios vista) {
        this.vista = vista;
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnActualizar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);
        this.vista.btnVolverMenu.addActionListener(this);

        this.vista.txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { cargarTabla(); }
            public void removeUpdate(DocumentEvent e) { cargarTabla(); }
            public void changedUpdate(DocumentEvent e) { cargarTabla(); }
        });

        this.vista.tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cargarDatos(); }
        });
        
        cargarTabla();
        actualizarKPIs();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardar) guardar();
        else if (e.getSource() == vista.btnActualizar) actualizar();
        else if (e.getSource() == vista.btnEliminar) eliminar();
        else if (e.getSource() == vista.btnLimpiar) vista.limpiar();
        else if (e.getSource() == vista.btnVolverMenu) vista.dispose();
    }

    private void guardar() {
        if (!validarCampos() || !passwordEsSegura()) return;

        int rolId = vista.cmbRol.getSelectedIndex() + 1;
        if (Sesion.rolActual == 2 && rolId == 1) {
            JOptionPane.showMessageDialog(vista, "No tienes permisos para crear un Master.");
            return;
        }

        if (dao.insertarUsuario(vista.txtUser.getText(), vista.txtNombre.getText(), new String(vista.txtPass.getPassword()), rolId)) {
            JOptionPane.showMessageDialog(vista, "Usuario guardado con éxito.");
            vista.limpiar();
            cargarTabla();
            actualizarKPIs();
        } else {
            JOptionPane.showMessageDialog(vista, "Error: El nombre de usuario ya existe.");
        }
    }

    private void actualizar() {
        if (vista.idSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un usuario de la tabla para actualizar.");
            return;
        }
        if (!validarCampos() || !passwordEsSegura()) return;

        int rolId = vista.cmbRol.getSelectedIndex() + 1;
        
        // Validar que el admin no modifique a un master
        if (Sesion.rolActual == 2 && vista.tabla.getSelectedRow() != -1) {
            int filaModelo = vista.tabla.convertRowIndexToModel(vista.tabla.getSelectedRow());
            String rolOriginal = vista.modelo.getValueAt(filaModelo, 4).toString();
            if (rolOriginal.equals("Master")) {
                JOptionPane.showMessageDialog(vista, "No puedes editar a un usuario Master.");
                return;
            }
        }

        if (dao.actualizarUsuario(vista.idSeleccionado, vista.txtUser.getText(), vista.txtNombre.getText(), new String(vista.txtPass.getPassword()), rolId)) {
            JOptionPane.showMessageDialog(vista, "Usuario actualizado.");
            vista.limpiar();
            cargarTabla();
            actualizarKPIs();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al actualizar.");
        }
    }

    private void eliminar() {
        if (vista.idSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un usuario para eliminar.");
            return;
        }
        
        // NUEVA REGLA: No puedes borrar el usuario con el que estás conectado
        if (vista.txtUser.getText().equals(Sesion.nombreUsuario)) {
            JOptionPane.showMessageDialog(vista, "No puedes eliminar tu propio usuario mientras estás en sesión.");
            return;
        }

        // Los administradores no pueden borrar Masters
        if (Sesion.rolActual == 2) {
            int row = vista.tabla.getSelectedRow();
            if (row != -1) {
                int filaModelo = vista.tabla.convertRowIndexToModel(row);
                if (vista.modelo.getValueAt(filaModelo, 4).toString().equals("Master")) {
                    JOptionPane.showMessageDialog(vista, "No puedes eliminar a un Master.");
                    return;
                }
            }
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.eliminarUsuario(vista.idSeleccionado)) {
                JOptionPane.showMessageDialog(vista, "Usuario eliminado.");
                vista.limpiar();
                cargarTabla();
                actualizarKPIs();
            }
        }
    }

    private void cargarTabla() {
        vista.modelo.setRowCount(0);
        String filtro = vista.txtBuscar.getText().trim();
        List<Object[]> lista = dao.obtenerUsuarios(filtro);
        
        for (Object[] u : lista) {
            int rolIdUser = Integer.parseInt(u[4].toString());
            String rolStr = rolIdUser == 1 ? "Master" : rolIdUser == 2 ? "Administrador" : "Usuario";
            
            String passMostrar = u[3].toString();

            // Si soy Administrador y estoy viendo a un Master, oculto su contraseña
            if (Sesion.rolActual == 2 && rolIdUser == 1) {
                passMostrar = "********";
            }

            // Columnas: ID, Usuario, Nombre, Password, Rol
            vista.modelo.addRow(new Object[]{u[0], u[1], u[2], passMostrar, rolStr});
        }
    }

    private void actualizarKPIs() {
        vista.lblKpiTotal.setText(String.valueOf(dao.contarPorRol(-1)));
        vista.lblKpiMaster.setText(String.valueOf(dao.contarPorRol(1)));
        vista.lblKpiAdmin.setText(String.valueOf(dao.contarPorRol(2)));
        vista.lblKpiUser.setText(String.valueOf(dao.contarPorRol(3)));
    }

    private void cargarDatos() {
        int filaVista = vista.tabla.getSelectedRow();
        if (filaVista != -1) {
            int filaModelo = vista.tabla.convertRowIndexToModel(filaVista);
            
            vista.idSeleccionado = Integer.parseInt(vista.modelo.getValueAt(filaModelo, 0).toString());
            vista.txtUser.setText(vista.modelo.getValueAt(filaModelo, 1).toString());
            vista.txtUser.setEditable(false);
            vista.txtNombre.setText(vista.modelo.getValueAt(filaModelo, 2) != null ? vista.modelo.getValueAt(filaModelo, 2).toString() : "");
            
            String passMostrada = vista.modelo.getValueAt(filaModelo, 3).toString();
            if (passMostrada.equals("********")) {
                vista.txtPass.setText(""); // Si está oculta, limpiamos el campo
                vista.txtPass.setEnabled(false);
            } else {
                vista.txtPass.setText(passMostrada);
                vista.txtPass.setEnabled(true);
            }
            
            String rolStr = vista.modelo.getValueAt(filaModelo, 4).toString();
            vista.cmbRol.setSelectedItem(rolStr);
        }
    }

    private boolean validarCampos() {
        String user = vista.txtUser.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String pass = new String(vista.txtPass.getPassword()).trim();

        if (user.isEmpty() || nombre.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!user.matches("^[a-zA-Z0-9]+$") || user.length() > 20) {
            JOptionPane.showMessageDialog(vista, "El Usuario solo permite letras y números (Máx 20 carácteres).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            JOptionPane.showMessageDialog(vista, "El Nombre completo solo permite letras.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean passwordEsSegura() {
        String estado = ValidadorPassword.evaluar(new String(vista.txtPass.getPassword()));
        if (!estado.equals("Fuerte")) {
            JOptionPane.showMessageDialog(vista, "La contraseña debe ser 'Fuerte' (Mín. 8 caracteres, 1 mayúscula, 1 número y 1 símbolo).", "Contraseña insegura", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}