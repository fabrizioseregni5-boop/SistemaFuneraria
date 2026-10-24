package controlador;

import modelo.ClienteDAO;
import vista.VistaClientes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ControladorClientes implements ActionListener {

    private VistaClientes vista;
    private ClienteDAO modeloDAO;

    public ControladorClientes(VistaClientes vista) {
        this.vista = vista;
        this.modeloDAO = new ClienteDAO();

        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnEditar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnVolver().addActionListener(this);
        this.vista.getBtnBuscar().addActionListener(this);
        this.vista.getTxtBuscar().addActionListener(this);

        this.vista.getTablaClientes().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosEnFormulario();
            }
        });

        cargarTablaClientes("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGuardar()) {
            guardarCliente();
        } else if (e.getSource() == vista.getBtnEditar()) {
            editarCliente();
        } else if (e.getSource() == vista.getBtnEliminar()) {
            eliminarCliente();
        } else if (e.getSource() == vista.getBtnLimpiar()) {
            vista.limpiarCampos();
            vista.getTablaClientes().clearSelection(); 
            cargarTablaClientes("");
        } else if (e.getSource() == vista.getBtnVolver()) {
            vista.dispose();
        } else if (e.getSource() == vista.getBtnBuscar() || e.getSource() == vista.getTxtBuscar()) {
            String filtro = vista.getTxtBuscar().getText().trim();
            cargarTablaClientes(filtro);
        }
    }

    private void guardarCliente() {
        if (!validarCampos(true)) return;

        String cedula = vista.getTxtCedula().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String direccion = vista.getTxtDireccion().getText().trim();

        boolean exito = modeloDAO.insertarCliente(cedula, nombre, telefono, direccion);

        if (exito) {
            JOptionPane.showMessageDialog(vista, "¡Cliente registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarCampos();
            cargarTablaClientes("");
        } else {
            JOptionPane.showMessageDialog(vista, "Error al registrar. Verifique si la cédula ya está registrada.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarCliente() {
        String cedula = vista.getTxtCedula().getText().trim();

        if (cedula.isEmpty() || cedula.equals("V-")) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente de la tabla para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarCampos(false)) return; // false porque estamos editando (no validamos si la cédula existe para él mismo)

        String nombre = vista.getTxtNombre().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String direccion = vista.getTxtDireccion().getText().trim();

        boolean exito = modeloDAO.actualizarCliente(cedula, nombre, telefono, direccion);

        if (exito) {
            JOptionPane.showMessageDialog(vista, "¡Cliente actualizado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarCampos();
            vista.getTablaClientes().clearSelection();
            cargarTablaClientes("");
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        // 1. Leemos la fila directamente de la tabla
        int filaSeleccionada = vista.getTablaClientes().getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione de la tabla el cliente que desea eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Obtenemos la cédula de esa fila
        int filaModelo = vista.getTablaClientes().convertRowIndexToModel(filaSeleccionada);
        String cedula = vista.getModeloTabla().getValueAt(filaModelo, 0).toString();

        int confirmar = JOptionPane.showConfirmDialog(vista, 
                "¿Está seguro de eliminar al cliente con cédula " + cedula + "?\nEsta acción no se puede deshacer.", 
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmar == JOptionPane.YES_OPTION) {
            boolean exito = modeloDAO.eliminarCliente(cedula);
            if (exito) {
                JOptionPane.showMessageDialog(vista, "¡Cliente eliminado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarCampos();
                vista.getTablaClientes().clearSelection();
                cargarTablaClientes("");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar. Es posible que el cliente tenga familiares o facturas asociadas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarDatosEnFormulario() {
        int filaSeleccionada = vista.getTablaClientes().getSelectedRow();
        if (filaSeleccionada != -1) {
            int filaModelo = vista.getTablaClientes().convertRowIndexToModel(filaSeleccionada);
            
            vista.getTxtCedula().setText(vista.getModeloTabla().getValueAt(filaModelo, 0).toString());
            vista.getTxtCedula().setEnabled(false); 
            
            vista.getTxtNombre().setText(String.valueOf(vista.getModeloTabla().getValueAt(filaModelo, 1)));
            vista.getTxtTelefono().setText(String.valueOf(vista.getModeloTabla().getValueAt(filaModelo, 2)));
            vista.getTxtDireccion().setText(String.valueOf(vista.getModeloTabla().getValueAt(filaModelo, 3)));
        }
    }

    private void cargarTablaClientes(String filtro) {
        DefaultTableModel modeloTabla = vista.getModeloTabla();
        modeloTabla.setRowCount(0);

        List<String[]> lista;
        if (filtro.isEmpty()) {
            lista = modeloDAO.obtenerClientes();
        } else {
            lista = modeloDAO.buscarClientes(filtro);
        }

        for (String[] fila : lista) {
            modeloTabla.addRow(fila);
        }
    }

    // ==========================================
    // MÉTODOS DE VALIDACIÓN
    // ==========================================
    private boolean validarCampos(boolean esNuevo) {
        String cedula = vista.getTxtCedula().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String direccion = vista.getTxtDireccion().getText().trim();

        // 1. Validar Cédula (V- seguido de 7 u 8 números)
        if (!cedula.matches("^V-\\d{7,8}$")) {
            JOptionPane.showMessageDialog(vista, 
                "Cédula inválida.\nDebe comenzar con 'V-' seguido de 7 u 8 números (Ej: V-12345678).", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Validar Nombre (Solo letras y espacios)
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            JOptionPane.showMessageDialog(vista, 
                "Nombre inválido.\nSolo se permiten letras.", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. Validar Teléfono (Exactamente 11 números)
        if (!telefono.matches("^\\d{11}$")) {
            JOptionPane.showMessageDialog(vista, 
                "Teléfono inválido.\nDebe contener exactamente 11 números (Ej: 04121234567).", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 4. Validar Dirección (Letras, números y puntos)
        if (!direccion.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ.\\s]+$")) {
            JOptionPane.showMessageDialog(vista, 
                "Dirección inválida.\nSolo se permiten letras, números y puntos.", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 5. Validar Unicidad (Solo si es un registro nuevo)
        if (esNuevo && modeloDAO.existeCedula(cedula)) {
            JOptionPane.showMessageDialog(vista, "La cédula ya está registrada en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 6. Validar Teléfono Único (Se excluye la misma cédula en caso de estar editando)
        if (modeloDAO.existeTelefono(telefono, esNuevo ? null : cedula)) {
            JOptionPane.showMessageDialog(vista, "El número de teléfono ya pertenece a otro cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}