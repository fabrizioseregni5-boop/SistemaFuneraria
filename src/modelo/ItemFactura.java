package modelo;

public class ItemFactura {
    
    // Los campos son 'final' para hacer la clase inmutable (segura para hilos)
    private final String codigoProducto;
    private final String descripcion;
    private final int cantidad;
    private final double precioUnitario;
    private final double subtotal;

    public ItemFactura(String codigoProducto, String descripcion, int cantidad, double precioUnitario) {
        // Validaciones básicas para evitar datos corruptos
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }

        this.codigoProducto = codigoProducto;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        
        // Redondeamos a 2 decimales para evitar problemas de precisión con 'double'
        this.subtotal = Math.round((cantidad * precioUnitario) * 100.0) / 100.0;
    }

    public String getCodigoProducto() { return codigoProducto; }
    public String getDescripcion() { return descripcion; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }

    // Agregar toString() es muy útil para depurar (debug) en la consola
    @Override
    public String toString() {
        return "ItemFactura{" +
                "codigo='" + codigoProducto + '\'' +
                ", cantidad=" + cantidad +
                ", precio=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}