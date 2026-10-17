package modelo;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class InicializadorBD {

    public static void crearTablas() {
        // Sentencia SQL para la tabla de Clientes
        String sqlClientes = "CREATE TABLE IF NOT EXISTS clientes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "cedula TEXT NOT NULL UNIQUE, "
                + "nombre TEXT NOT NULL, "
                + "telefono TEXT, "
                + "direccion TEXT, "
                + "correo TEXT"
                + ");";

        // Sentencia SQL para la tabla de Inventario
        String sqlInventario = "CREATE TABLE IF NOT EXISTS inventario ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "codigo TEXT NOT NULL UNIQUE, "
                + "descripcion TEXT NOT NULL, "
                + "categoria TEXT, "
                + "stock INTEGER NOT NULL, "
                + "precio REAL NOT NULL"
                + ");";

        // Sentencia SQL para la tabla de Familiares
        String sqlFamiliares = "CREATE TABLE IF NOT EXISTS familiares ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "cedula_titular TEXT NOT NULL, "
                + "nombres_familiar TEXT NOT NULL, "
                + "apellidos_familiar TEXT NOT NULL, "
                + "parentesco TEXT NOT NULL, "
                + "cedula_familiar TEXT, "
                + "direccion TEXT, "
                + "FOREIGN KEY (cedula_titular) REFERENCES clientes(cedula) ON DELETE CASCADE"
                + ");";
        
        // NUEVA: Sentencia SQL para la tabla de Proveedores
        String sqlProveedores = "CREATE TABLE IF NOT EXISTS proveedores ("
                + "rif TEXT PRIMARY KEY NOT NULL, "
                + "nombre TEXT NOT NULL, "
                + "telefono TEXT, "
                + "direccion TEXT"
                + ");";

        // Sentencia SQL para la tabla de Facturas
        String sqlFacturas = "CREATE TABLE IF NOT EXISTS facturas ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "numero_factura TEXT NOT NULL UNIQUE, "
                + "cedula_cliente TEXT NOT NULL, "
                + "proveedor TEXT DEFAULT 'Particular', " // Columna agregada aquí
                + "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "subtotal REAL NOT NULL, "
                + "iva REAL NOT NULL, "
                + "total REAL NOT NULL, "
                + "FOREIGN KEY (cedula_cliente) REFERENCES clientes(cedula) ON DELETE CASCADE"
                + ");";

        // Sentencia SQL para el Detalle de Facturas
        String sqlDetalleFactura = "CREATE TABLE IF NOT EXISTS detalles_factura ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "numero_factura TEXT NOT NULL, "
                + "codigo_producto TEXT NOT NULL, "
                + "cantidad INTEGER NOT NULL, "
                + "precio_unitario REAL NOT NULL, "
                + "subtotal REAL NOT NULL, "
                + "FOREIGN KEY (numero_factura) REFERENCES facturas(numero_factura) ON DELETE CASCADE, "
                + "FOREIGN KEY (codigo_producto) REFERENCES inventario(codigo)"
                + ");";
        
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement()) {
            
            // 1. Ejecutamos primero la creación de clientes (tabla principal)
            stmt.execute(sqlClientes);
            System.out.println("¡Tabla de 'clientes' creada o verificada con éxito!");

            // 2. Luego la tabla de inventario
            stmt.execute(sqlInventario);
            System.out.println("¡Tabla de 'inventario' creada o verificada con éxito!");

            // 3. La de familiares (que depende de clientes)
            stmt.execute(sqlFamiliares);
            System.out.println("¡Tabla de 'familiares' creada o verificada con éxito!");

            // 4. La NUEVA tabla de Proveedores
            stmt.execute(sqlProveedores);
            System.out.println("¡Tabla de 'proveedores' creada o verificada con éxito!");

            // 5. La de facturas (que depende de clientes)
            stmt.execute(sqlFacturas);
            System.out.println("¡Tabla de 'facturas' creada o verificada con éxito!");

            // 6. Por último el detalle de facturas
            stmt.execute(sqlDetalleFactura);
            System.out.println("¡Tabla de 'detalles_factura' creada o verificada con éxito!");

            // ==============================================================
            // MIGRACIÓN: Arreglar la tabla facturas existente
            // ==============================================================
            // Si tu base de datos ya existía de antes, la tabla facturas no tiene
            // la columna proveedor. Este bloque intenta agregarla.
            // Si ya existe, dará un error que ignoraremos.
            try {
                stmt.execute("ALTER TABLE facturas ADD COLUMN proveedor TEXT DEFAULT 'Particular'");
                System.out.println("¡Columna 'proveedor' agregada a la tabla facturas!");
            } catch (SQLException e) {
                // Si el error dice que la columna ya existe, no hacemos nada.
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println("Nota sobre migración: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al crear las tablas: " + e.getMessage());
        }
        
    }

    public static void main(String[] args) {
        crearTablas();
    }
    
}