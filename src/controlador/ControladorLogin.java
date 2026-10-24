package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import modelo.ConexionBD;
import modelo.Sesion;
import modelo.UsuarioDAO;
import vista.MenuPrincipal;
import vista.VistaLogin;

public class ControladorLogin implements ActionListener {

    private VistaLogin vista;
    private UsuarioDAO modeloDAO;

    public ControladorLogin(VistaLogin vista) {
        this.vista = vista;
        this.modeloDAO = new UsuarioDAO();
        this.vista.getBtnIngresar().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String user = vista.getUsuario();
        String pass = vista.getPassword();

        // 1. Validar que no estén vacíos
        if (user.isEmpty() || pass.isEmpty()) {
            vista.mostrarError("Por favor, ingrese usuario y contraseña.");
            return;
        }

        // 2. Validar en la base de datos y obtener el rol (-1 si es incorrecto)
        int rol = modeloDAO.validarUsuario(user, pass);

        if (rol != -1) {
            // 3. Credenciales correctas: Guardamos la sesión
            Sesion.nombreUsuario = user;
            Sesion.rolActual = rol;
            
            // 4. REGISTRAR EL ACCESO EN LA BASE DE DATOS (Para el módulo de reportes)
            try (Connection conn = ConexionBD.conectar();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO logs_acceso (username) VALUES (?)")) {
                
                pstmt.setString(1, user);
                pstmt.executeUpdate();
                
            } catch (SQLException ex) {
                System.err.println("Error al guardar log de acceso: " + ex.getMessage());
            }
            
            // 5. Abrir el menú principal y cerrar la ventana de login
            new MenuPrincipal().setVisible(true);
            vista.dispose();
            
        } else {
            // 6. Credenciales incorrectas
            vista.mostrarError("Usuario o contraseña incorrectos.");
        }
    }
}