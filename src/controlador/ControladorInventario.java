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

        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnEditar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnVolver().addActionListener(this);
        this.vista.getBtnBuscar().addActionListener(this);
        this.vista.getTxtBuscar().addActionListener(this);
        this.vista.getBtnGuardarTasa().addActionListener(this); // Nuevo botón

        this.vista.getTablaInventario().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosEnFormulario();
            }
        });

        // Cargar las tasas guardadas al abrir
        double[] tasas = modeloDAO.obtenerTasas();
        vista.getTxtTasaBcv().setText(String.format("%.2f", tasas[0]));
        vista.getTxtTasaBinance().setText(String.format("%.2f", tasas[1]));
        vista.bloquearTasas(true); // Bloquearlas por defecto

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
            vista.getTablaInventario().clearSelection();
            cargarTabla("");
        } else if (e.getSource() == vista.getBtnVolver()) {
            vista.dispose();
        } else if (e.getSource() == vista.getBtnBuscar() || e.getSource() == vista.getTxtBuscar()) {
            cargarTabla(vista.getTxtBuscar().getText().trim());
        } else if (e.getSource() == vista.getBtnGuardarTasa()) {
            guardarTasas();
        }
    }

    private void guardarTasas() {
        try {
            double bcv = Double.parseDouble(vista.getTxtTasaBcv().getText().trim());
            double binance = Double.parseDouble(vista.getTxtTasaBinance().getText().trim());
            
            if (bcv <= 0 || binance <= 0) {
                JOptionPane.showMessageDialog(vista, "Las tasas deben ser mayores a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            modeloDAO.guardarTasas(bcv, binance);
            vista.bloquearTasas(true); // Volver a bloquear
            JOptionPane.showMessageDialog(vista, "Tasas guardadas correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Ingrese valores numéricos válidos para las tasas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarProducto() {
        if (!validarCampos(true)) return;

        String codigo = vista.getTxtCodigo().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        int cantidad = Integer.parseInt(vista.getTxtCantidad().getText().trim());
        double precioCompra = Double.parseDouble(vista.getTxtPrecioCompra().getText().trim());
        double precioVenta = Double.parseDouble(vista.getTxtPrecioVenta().getText().trim());

        boolean exito = modeloDAO.insertarProducto(codigo, nombre, cantidad, precioCompra, precioVenta);
        if (exito) {
            JOptionPane.showMessageDialog(vista, "¡Producto registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarCampos();
            cargarTabla("");
        } else {
            JOptionPane.showMessageDialog(vista, "Error al registrar. ¿El código ya existe?", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProducto() {
        String codigo = vista.getTxtCodigo().getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto de la tabla para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validarCampos(false)) return;

        String nombre = vista.getTxtNombre().getText().trim();
        int cantidad = Integer.parseInt(vista.getTxtCantidad().getText().trim());
        double precioCompra = Double.parseDouble(vista.getTxtPrecioCompra().getText().trim());
        double precioVenta = Double.parseDouble(vista.getTxtPrecioVenta().getText().trim());

        boolean exito = modeloDAO.actualizarProducto(codigo, nombre, cantidad, precioCompra, precioVenta);
        if (exito) {
            JOptionPane.showMessageDialog(vista, "¡Producto actualizado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarCampos();
            vista.getTablaInventario().clearSelection();
            cargarTabla("");
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProducto() {
        int fila = vista.getTablaInventario().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = vista.getTablaInventario().convertRowIndexToModel(fila);
        String codigo = vista.getModeloTabla().getValueAt(filaModelo, 0).toString();

        int confirmar = JOptionPane.showConfirmDialog(vista, "¿Eliminar producto " + codigo + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            if (modeloDAO.eliminarProducto(codigo)) {
                JOptionPane.showMessageDialog(vista, "¡Producto eliminado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarCampos();
                cargarTabla("");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar. Es posible que esté en uso en una factura.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarDatosEnFormulario() {
        int fila = vista.getTablaInventario().getSelectedRow();
        if (fila != -1) {
            int filaModelo = vista.getTablaInventario().convertRowIndexToModel(fila);
            vista.getTxtCodigo().setText(vista.getModeloTabla().getValueAt(filaModelo, 0).toString());
            vista.getTxtCodigo().setEnabled(false);
            vista.getTxtNombre().setText(vista.getModeloTabla().getValueAt(filaModelo, 1).toString());
            vista.getTxtCantidad().setText(vista.getModeloTabla().getValueAt(filaModelo, 2).toString());
            vista.getTxtPrecioCompra().setText(vista.getModeloTabla().getValueAt(filaModelo, 3).toString());
            vista.getTxtPrecioVenta().setText(vista.getModeloTabla().getValueAt(filaModelo, 4).toString());
        }
    }

    private void cargarTabla(String filtro) {
        DefaultTableModel modelo = vista.getModeloTabla();
        modelo.setRowCount(0);
        List<Object[]> lista = filtro.isEmpty() ? modeloDAO.obtenerInventario() : modeloDAO.buscarProductos(filtro);
        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
    }

    private boolean validarCampos(boolean esNuevo) {
        String codigo = vista.getTxtCodigo().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        String cantStr = vista.getTxtCantidad().getText().trim();
        String precCompraStr = vista.getTxtPrecioCompra().getText().trim();
        String precVentaStr = vista.getTxtPrecioVenta().getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || cantStr.isEmpty() || precCompraStr.isEmpty() || precVentaStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!codigo.matches("^[a-zA-Z0-9-]+$")) {
            JOptionPane.showMessageDialog(vista, "El Código solo puede contener letras, números y guiones.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (nombre.length() > 25 || !nombre.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$")) {
            JOptionPane.showMessageDialog(vista, "Nombre inválido (Máx 25 carácteres, sin símbolos).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int cantidad = Integer.parseInt(cantStr);
            double precioCompra = Double.parseDouble(precCompraStr);
            double precioVenta = Double.parseDouble(precVentaStr);

            if (cantidad < 1) {
                JOptionPane.showMessageDialog(vista, "La Cantidad debe ser mayor o igual a 1.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (precioCompra <= 0 || precioVenta <= 0) {
                JOptionPane.showMessageDialog(vista, "Los precios deben ser mayores a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Valores numéricos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (esNuevo && modeloDAO.existeCodigo(codigo)) {
            JOptionPane.showMessageDialog(vista, "El código de producto ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}