package controlador;

import modelo.InventarioDAO;
import vista.VistaInventario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ControladorInventario implements ActionListener {

    private VistaInventario vista;
    private InventarioDAO modeloDAO;

    public ControladorInventario(VistaInventario vista) {
        this.vista = vista;
        this.modeloDAO = new InventarioDAO();

        // Asignar eventos a los botones
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnEditar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnVolver().addActionListener(this);
        this.vista.getBtnBuscar().addActionListener(this);

        // ¡NUEVO! Permite buscar presionando "Enter" en el campo de búsqueda
        this.vista.getTxtBuscar().addActionListener(this);

        // Evento para cargar datos al hacer clic en una fila de la tabla
        this.vista.getTablaInventario().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosEnFormulario();
            }
        });

        // Cargar tabla al iniciar
        cargarTabla("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGuardar()) {
            guardarProducto();
        } else if (e.getSource() == vista.getBtnEditar()) {
            editarProducto();
        } else if (e.getSource() == vista.getBtnEliminar()) {
            eliminarProducto();
        } else if (e.getSource() == vista.getBtnLimpiar()) {
            vista.limpiarCampos();
            vista.getTablaInventario().clearSelection(); // Quitar selección de la tabla
            cargarTabla("");
        } else if (e.getSource() == vista.getBtnVolver()) {
            vista.dispose();
        } else if (e.getSource() == vista.getBtnBuscar() || e.getSource() == vista.getTxtBuscar()) {
            // Si presiona el botón o presiona Enter en el campo de texto
            cargarTabla(vista.getTxtBuscar().getText().trim());
        }
    }

    private void guardarProducto() {
        if (camposVacios()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String codigo = vista.getTxtCodigo().getText().trim();
            String nombre = vista.getTxtNombre().getText().trim();
            int cantidad = Integer.parseInt(vista.getTxtCantidad().getText().trim());
            double precio = Double.parseDouble(vista.getTxtPrecio().getText().trim());

            // Validación extra: Cantidades y precios negativos
            if (cantidad < 0 || precio < 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad y el precio no pueden ser negativos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean exito = modeloDAO.insertarProducto(codigo, nombre, cantidad, precio);
            if (exito) {
                JOptionPane.showMessageDialog(vista, "¡Producto registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarCampos();
                cargarTabla("");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al registrar. ¿El código ya existe?", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser un número entero y el precio un número decimal.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProducto() {
        String codigo = vista.getTxtCodigo().getText().trim();
        
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto de la tabla para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (camposVacios()) {
            JOptionPane.showMessageDialog(vista, "No puede dejar campos vacíos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String nombre = vista.getTxtNombre().getText().trim();
            int cantidad = Integer.parseInt(vista.getTxtCantidad().getText().trim());
            double precio = Double.parseDouble(vista.getTxtPrecio().getText().trim());

            if (cantidad < 0 || precio < 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad y el precio no pueden ser negativos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean exito = modeloDAO.actualizarProducto(codigo, nombre, cantidad, precio);
            if (exito) {
                JOptionPane.showMessageDialog(vista, "¡Producto actualizado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarCampos();
                vista.getTablaInventario().clearSelection();
                cargarTabla("");
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Verifique los formatos numéricos de Cantidad y Precio.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProducto() {
        String codigo = vista.getTxtCodigo().getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(vista, 
                "¿Está seguro que desea eliminar el producto con código " + codigo + "?", 
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmar == JOptionPane.YES_OPTION) {
            if (modeloDAO.eliminarProducto(codigo)) {
                JOptionPane.showMessageDialog(vista, "¡Producto eliminado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarCampos();
                cargarTabla("");
            } else {
                // Aquí podrías tener un problema si el producto ya está en una factura (Foreign Key)
                JOptionPane.showMessageDialog(vista, "Error al eliminar. Es posible que el producto esté en uso en una factura.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarDatosEnFormulario() {
        int fila = vista.getTablaInventario().getSelectedRow();
        if (fila != -1) {
            // Convertir el índice de la vista al índice del modelo (por si ordenas la tabla luego)
            int filaModelo = vista.getTablaInventario().convertRowIndexToModel(fila);
            
            vista.getTxtCodigo().setText(vista.getModeloTabla().getValueAt(filaModelo, 0).toString());
            vista.getTxtCodigo().setEnabled(false); // Bloquea el código porque es la PK
            vista.getTxtNombre().setText(vista.getModeloTabla().getValueAt(filaModelo, 1).toString());
            vista.getTxtCantidad().setText(vista.getModeloTabla().getValueAt(filaModelo, 2).toString());
            vista.getTxtPrecio().setText(vista.getModeloTabla().getValueAt(filaModelo, 3).toString());
        }
    }

    private void cargarTabla(String filtro) {
        DefaultTableModel modelo = vista.getModeloTabla();
        modelo.setRowCount(0); // Limpiar tabla

        List<Object[]> lista = filtro.isEmpty() ? modeloDAO.obtenerInventario() : modeloDAO.buscarProductos(filtro);
        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
    }
    
    // Método auxiliar para no repetir la validación de campos vacíos
    private boolean camposVacios() {
        return vista.getTxtCodigo().getText().trim().isEmpty() ||
               vista.getTxtNombre().getText().trim().isEmpty() ||
               vista.getTxtCantidad().getText().trim().isEmpty() ||
               vista.getTxtPrecio().getText().trim().isEmpty();
    }
}