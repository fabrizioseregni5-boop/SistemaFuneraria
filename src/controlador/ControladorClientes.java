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

        // Escuchadores de botones
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnEditar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnVolver().addActionListener(this);
        this.vista.getBtnBuscar().addActionListener(this);

        // ¡NUEVO! Permite buscar presionando "Enter" en el campo de búsqueda
        this.vista.getTxtBuscar().addActionListener(this);

        // Evento para seleccionar una fila de la tabla y cargarla en el formulario
        this.vista.getTablaClientes().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosEnFormulario();
            }
        });

        // Cargar todos los clientes al iniciar
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
            vista.getTablaClientes().clearSelection(); // Quitar selección de la tabla
            cargarTablaClientes("");
        } else if (e.getSource() == vista.getBtnVolver()) {
            vista.dispose();
        } else if (e.getSource() == vista.getBtnBuscar() || e.getSource() == vista.getTxtBuscar()) {
            // Si presiona el botón o presiona Enter en el campo de texto
            String filtro = vista.getTxtBuscar().getText().trim();
            cargarTablaClientes(filtro);
        }
    }

    private void guardarCliente() {
        if (camposObligatoriosVacios()) {
            JOptionPane.showMessageDialog(vista, "La cédula y el nombre son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

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

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente de la tabla para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (camposObligatoriosVacios()) {
            JOptionPane.showMessageDialog(vista, "El nombre no puede estar vacío al editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

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
        String cedula = vista.getTxtCedula().getText().trim();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione de la tabla el cliente que desea eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(vista, 
                "¿Está seguro de eliminar al cliente con cédula " + cedula + "?\nEsta acción no se puede deshacer.", 
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmar == JOptionPane.YES_OPTION) {
            boolean exito = modeloDAO.eliminarCliente(cedula);
            if (exito) {
                JOptionPane.showMessageDialog(vista, "¡Cliente eliminado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarCampos();
                cargarTablaClientes("");
            } else {
                // Mensaje mejorado por si el cliente tiene facturas asociadas (Llave Foránea)
                JOptionPane.showMessageDialog(vista, "Error al eliminar. Es posible que el cliente tenga familiares o facturas asociadas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarDatosEnFormulario() {
        int filaSeleccionada = vista.getTablaClientes().getSelectedRow();
        if (filaSeleccionada != -1) {
            // Convertir el índice de la vista al índice del modelo (protege contra futuras ordenaciones)
            int filaModelo = vista.getTablaClientes().convertRowIndexToModel(filaSeleccionada);
            
            vista.getTxtCedula().setText(vista.getModeloTabla().getValueAt(filaModelo, 0).toString());
            vista.getTxtCedula().setEnabled(false); // No permitimos cambiar la cédula primaria al editar
            
            vista.getTxtNombre().setText(String.valueOf(vista.getModeloTabla().getValueAt(filaModelo, 1)));
            vista.getTxtTelefono().setText(String.valueOf(vista.getModeloTabla().getValueAt(filaModelo, 2)));
            vista.getTxtDireccion().setText(String.valueOf(vista.getModeloTabla().getValueAt(filaModelo, 3)));
        }
    }

    private void cargarTablaClientes(String filtro) {
        DefaultTableModel modeloTabla = vista.getModeloTabla();
        modeloTabla.setRowCount(0); // Limpiar tabla

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

    // Método auxiliar para mantener el código limpio (DRY)
    private boolean camposObligatoriosVacios() {
        return vista.getTxtCedula().getText().trim().isEmpty() || 
               vista.getTxtNombre().getText().trim().isEmpty();
    }
}