package controlador;

import modelo.ProveedorDAO;
import vista.VistaProveedores;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ControladorProveedores implements ActionListener {
    private VistaProveedores vista;
    private ProveedorDAO modeloDAO;

    public ControladorProveedores(VistaProveedores vista) {
        this.vista = vista;
        this.modeloDAO = new ProveedorDAO();

        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnEditar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnVolver().addActionListener(this);
        this.vista.getBtnBuscar().addActionListener(this);
        this.vista.getTxtBuscar().addActionListener(this);

        this.vista.getTablaProveedores().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { cargarDatosEnFormulario(); }
        });

        cargarTabla("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGuardar()) guardar();
        else if (e.getSource() == vista.getBtnEditar()) editar();
        else if (e.getSource() == vista.getBtnEliminar()) eliminar();
        else if (e.getSource() == vista.getBtnLimpiar()) { vista.limpiarCampos(); cargarTabla(""); }
        else if (e.getSource() == vista.getBtnVolver()) vista.dispose();
        else if (e.getSource() == vista.getBtnBuscar() || e.getSource() == vista.getTxtBuscar()) {
            cargarTabla(vista.getTxtBuscar().getText().trim());
        }
    }

    private void guardar() {
        if (!validarCampos()) return; // Si la validación falla, se detiene aquí

        boolean exito = modeloDAO.insertarProveedor(
                vista.getTxtRif().getText().trim(),
                vista.getTxtNombre().getText().trim(),
                vista.getTxtTelefono().getText().trim(),
                vista.getTxtDireccion().getText().trim()
        );
        if (exito) {
            JOptionPane.showMessageDialog(vista, "¡Proveedor registrado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarCampos();
            cargarTabla("");
        } else {
            JOptionPane.showMessageDialog(vista, "Error. ¿El RIF ya existe?", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        String rif = vista.getTxtRif().getText().trim();
        if (rif.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un proveedor de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validarCampos()) return; // Si la validación falla, se detiene aquí

        boolean exito = modeloDAO.actualizarProveedor(
                rif,
                vista.getTxtNombre().getText().trim(),
                vista.getTxtTelefono().getText().trim(),
                vista.getTxtDireccion().getText().trim()
        );
        if (exito) {
            JOptionPane.showMessageDialog(vista, "¡Proveedor actualizado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarCampos();
            cargarTabla("");
        } else {
            JOptionPane.showMessageDialog(vista, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        String rif = vista.getTxtRif().getText().trim();
        if (rif.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un proveedor.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(vista, "¿Eliminar proveedor " + rif + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (modeloDAO.eliminarProveedor(rif)) {
                JOptionPane.showMessageDialog(vista, "Proveedor eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarCampos();
                cargarTabla("");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar. Puede estar en uso.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarDatosEnFormulario() {
        int fila = vista.getTablaProveedores().getSelectedRow();
        if (fila != -1) {
            int filaModelo = vista.getTablaProveedores().convertRowIndexToModel(fila);
            vista.getTxtRif().setText(vista.getModeloTabla().getValueAt(filaModelo, 0).toString());
            vista.getTxtRif().setEnabled(false); // Bloquear RIF al editar
            vista.getTxtNombre().setText(vista.getModeloTabla().getValueAt(filaModelo, 1).toString());
            vista.getTxtTelefono().setText(vista.getModeloTabla().getValueAt(filaModelo, 2).toString());
            vista.getTxtDireccion().setText(vista.getModeloTabla().getValueAt(filaModelo, 3).toString());
        }
    }

    private void cargarTabla(String filtro) {
        DefaultTableModel modelo = vista.getModeloTabla();
        modelo.setRowCount(0);
        List<String[]> lista = filtro.isEmpty() ? modeloDAO.obtenerProveedores() : modeloDAO.buscarProveedores(filtro);
        for (String[] fila : lista) {
            modelo.addRow(fila);
        }
    }

    // ==========================================
    // MÉTODO DE VALIDACIÓN CON EXPRESIONES REGULARES
    // ==========================================
    private boolean validarCampos() {
        String rif = vista.getTxtRif().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String direccion = vista.getTxtDireccion().getText().trim();

        // 1. Validar RIF: Letra, guion, 8 números, guion, 1 número (Ej: J-12345678-0)
        if (!rif.matches("^[A-Za-z]-\\d{8}-\\d{1}$")) {
            JOptionPane.showMessageDialog(vista, 
                "Formato de RIF inválido.\nDebe tener el formato: J-12345678-0\n(Sin espacios)", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Validar Nombre: Letras, números y puntos (Ej: Funeraria Mi Redentor C.A.)
        if (!nombre.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ.\\s]+$")) {
            JOptionPane.showMessageDialog(vista, 
                "Nombre / Empresa inválido.\nSolo se permiten letras, números y puntos.", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. Validar Teléfono: Solo números
        if (!telefono.matches("^\\d+$")) {
            JOptionPane.showMessageDialog(vista, 
                "Teléfono inválido.\nSolo se permiten números (sin guiones ni espacios).", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 4. Validar Dirección: Letras, números y puntos
        if (!direccion.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ.\\s]+$")) {
            JOptionPane.showMessageDialog(vista, 
                "Dirección inválida.\nSolo se permiten letras, números y puntos.", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validar que no haya campos vacíos obligatorios
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El nombre es obligatorio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}