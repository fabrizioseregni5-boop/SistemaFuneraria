package modelo;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class InicializadorBD {

    public static void crearTablas() {
        // 1. Tabla de Clientes
        String sqlClientes = "CREATE TABLE IF NOT EXISTS clientes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "cedula TEXT NOT NULL UNIQUE, "
                + "nombre TEXT NOT NULL, "
                + "telefono TEXT, "
                + "direccion TEXT, "
                + "correo TEXT"
                + ");";

        // 2. Tabla de Inventario (CON PRECIO DE VENTA)
        String sqlInventario = "CREATE TABLE IF NOT EXISTS inventario ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "codigo TEXT NOT NULL UNIQUE, "
                + "descripcion TEXT NOT NULL, "
                + "categoria TEXT, "
                + "stock INTEGER NOT NULL, "
                + "precio REAL NOT NULL, "
                + "precio_venta REAL NOT NULL DEFAULT 0.0"
                + ");";

        // 3. Tabla de Configuración (PARA LAS TASAS)
        String sqlConfiguracion = "CREATE TABLE IF NOT EXISTS configuracion ("
                + "clave TEXT PRIMARY KEY, "
                + "valor REAL NOT NULL"
                + ");";

        // 4. Tabla de Familiares
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
        
        // 5. Tabla de Proveedores
        String sqlProveedores = "CREATE TABLE IF NOT EXISTS proveedores ("
                + "rif TEXT PRIMARY KEY NOT NULL, "
                + "nombre TEXT NOT NULL, "
                + "telefono TEXT, "
                + "direccion TEXT"
                + ");";

        // 6. Tabla de Facturas
        String sqlFacturas = "CREATE TABLE IF NOT EXISTS facturas ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "numero_factura TEXT NOT NULL UNIQUE, "
                + "cedula_cliente TEXT NOT NULL, "
                + "proveedor TEXT DEFAULT 'Particular', " 
                + "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "subtotal REAL NOT NULL, "
                + "iva REAL NOT NULL, "
                + "total REAL NOT NULL, "
                + "FOREIGN KEY (cedula_cliente) REFERENCES clientes(cedula) ON DELETE CASCADE"
                + ");";

        // 7. Tabla de Detalle de Facturas
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
        
        // 8. Tabla de Usuarios con Roles y Nombre Completo
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "nombre_completo TEXT, "
                + "password TEXT NOT NULL, "
                + "rol_id INTEGER NOT NULL DEFAULT 3"
                + ");";

        // 9. Tabla de Cuentas (Por Cobrar y Pagar)
        String sqlCuentas = "CREATE TABLE IF NOT EXISTS cuentas ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tipo TEXT NOT NULL, " // 'COBRAR' o 'PAGAR'
                + "referencia TEXT NOT NULL, " // Nro Factura o Nombre Proveedor
                + "persona TEXT NOT NULL, " // Cliente o Proveedor
                + "monto_total REAL NOT NULL, "
                + "abonado REAL DEFAULT 0.0, "
                + "saldo REAL NOT NULL, "
                + "estado TEXT DEFAULT 'Pendiente', " // Pendiente o Pagada
                + "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";

        // 10. Tabla de Logs (Historial de accesos)
        String sqlLogs = "CREATE TABLE IF NOT EXISTS logs_acceso ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL, "
                + "fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";
        
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement()) {
            
            // --- CREACIÓN DE TABLAS ---
            stmt.execute(sqlClientes);
            System.out.println("¡Tabla de 'clientes' creada o verificada con éxito!");

            stmt.execute(sqlInventario);
            System.out.println("¡Tabla de 'inventario' creada o verificada con éxito!");

            stmt.execute(sqlConfiguracion);
            System.out.println("¡Tabla de 'configuracion' creada o verificada con éxito!");
            
            stmt.execute(sqlFamiliares);
            System.out.println("¡Tabla de 'familiares' creada o verificada con éxito!");

            stmt.execute(sqlProveedores);
            System.out.println("¡Tabla de 'proveedores' creada o verificada con éxito!");

            stmt.execute(sqlFacturas);
            System.out.println("¡Tabla de 'facturas' creada o verificada con éxito!");

            stmt.execute(sqlDetalleFactura);
            System.out.println("¡Tabla de 'detalles_factura' creada o verificada con éxito!");

            stmt.execute(sqlUsuarios);
            System.out.println("¡Tabla de 'usuarios' creada o verificada con éxito!");

            // ¡NUEVAS TABLAS AGREGADAS AQUÍ!
            stmt.execute(sqlCuentas);
            System.out.println("¡Tabla de 'cuentas' creada o verificada con éxito!");

            stmt.execute(sqlLogs);
            System.out.println("¡Tabla de 'logs_acceso' creada o verificada con éxito!");
            
            // --- MIGRACIONES (Para bases de datos antiguas) ---
            try { 
                stmt.execute("ALTER TABLE usuarios ADD COLUMN rol_id INTEGER NOT NULL DEFAULT 3"); 
            } catch (SQLException e) { /* Ignorar si ya existe */ }
            
            try { 
                stmt.execute("ALTER TABLE usuarios ADD COLUMN nombre_completo TEXT"); 
            } catch (SQLException e) { /* Ignorar si ya existe */ }
            
            try {
                stmt.execute("ALTER TABLE facturas ADD COLUMN proveedor TEXT DEFAULT 'Particular'");
            } catch (SQLException e) { /* Ignorar si ya existe */ }
            
            try { 
                stmt.execute("ALTER TABLE inventario ADD COLUMN precio_venta REAL NOT NULL DEFAULT 0.0"); 
            } catch (SQLException e) { /* Ignorar si ya existe */ }

            // --- INSERCIÓN DE USUARIOS POR DEFECTO ---
            stmt.execute("INSERT OR IGNORE INTO usuarios (username, nombre_completo, password, rol_id) VALUES ('master', 'Usuario Master', 'Master123!', 1)");
            stmt.execute("INSERT OR IGNORE INTO usuarios (username, nombre_completo, password, rol_id) VALUES ('admin', 'Administrador', 'Admin123!', 2)");
            stmt.execute("INSERT OR IGNORE INTO usuarios (username, nombre_completo, password, rol_id) VALUES ('user', 'Usuario Normal', 'User123!', 3)");
            System.out.println("¡Usuarios por defecto (master, admin, user) verificados con éxito!");

        } catch (SQLException e) {
            System.out.println("Error al crear o actualizar las tablas: " + e.getMessage());
        }
        
    }

    public static void main(String[] args) {
        crearTablas();
    }
    
}