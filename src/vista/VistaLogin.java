package vista;

import controlador.ControladorLogin;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VistaLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar, btnVerPass;
    private char defaultEchoChar;

    public VistaLogin() {
        setTitle("Iniciar Sesión - Funeraria Mi Redentor");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(44, 62, 80)); // Fondo oscuro

        // Tarjeta blanca central
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Logo
        JLabel lblLogo = new JLabel();
        try {
            ImageIcon iconoOriginal = new ImageIcon("logo.png");
            // Escalamos la imagen para que se vea nítida
            Image img = iconoOriginal.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblLogo.setText("Logo de la empresa");
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblLogo.setForeground(new Color(44, 62, 80));
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Título
        JLabel lblTitulo = new JLabel("Bienvenido");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(44, 62, 80));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Ingresa tus credenciales para continuar");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(Color.GRAY);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- CAMPO USUARIO ---
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(50, 50, 50));
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsuario = new JTextField(15);
        txtUsuario.setMaximumSize(new Dimension(300, 40));
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                new EmptyBorder(8, 10, 8, 10))); // Padding interior moderno

        // --- CAMPO CONTRASEÑA CON OJITO ---
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(50, 50, 50));
        lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelPass = new JPanel(new BorderLayout(5, 0));
        panelPass.setOpaque(false);
        panelPass.setMaximumSize(new Dimension(300, 40));

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                new EmptyBorder(8, 10, 8, 10)));
        defaultEchoChar = txtPassword.getEchoChar();

        // Botón del ojito (Ver/Ocultar)
        btnVerPass = new JButton("Ver");
        btnVerPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerPass.setBackground(new Color(240, 240, 240));
        btnVerPass.setForeground(Color.DARK_GRAY);
        btnVerPass.setFocusPainted(false);
        btnVerPass.setBorderPainted(false);
        btnVerPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerPass.setPreferredSize(new Dimension(50, 0));
        
        btnVerPass.addActionListener(e -> {
            if (txtPassword.getEchoChar() == 0) {
                txtPassword.setEchoChar(defaultEchoChar);
                btnVerPass.setText("Ver");
            } else {
                txtPassword.setEchoChar((char) 0);
                btnVerPass.setText("Ocultar");
            }
        });

        panelPass.add(txtPassword, BorderLayout.CENTER);
        panelPass.add(btnVerPass, BorderLayout.EAST);

        // --- BOTÓN INGRESAR ---
        btnIngresar = new JButton("INGRESAR AL SISTEMA");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setBackground(new Color(39, 174, 96)); // Verde
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setMaximumSize(new Dimension(300, 45));
        btnIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Efecto hover para el botón (se oscurece un poco)
        btnIngresar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnIngresar.setBackground(new Color(33, 150, 83));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnIngresar.setBackground(new Color(39, 174, 96));
            }
        });

        // ==========================================
        // Agregar todo al panel con espaciado
        // ==========================================
        card.add(lblLogo);
        card.add(Box.createVerticalStrut(10));
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(5));
        card.add(lblSubtitulo);
        card.add(Box.createVerticalStrut(30));
        
        card.add(lblUser);
        card.add(Box.createVerticalStrut(5));
        card.add(txtUsuario);
        
        card.add(Box.createVerticalStrut(15));
        
        card.add(lblPass);
        card.add(Box.createVerticalStrut(5));
        card.add(panelPass);
        
        card.add(Box.createVerticalStrut(30));
        card.add(btnIngresar);

        add(card);

        // Iniciar Controlador
        new ControladorLogin(this);

        // Permite presionar "Enter" en la contraseña para iniciar sesión
        txtPassword.addActionListener(e -> btnIngresar.doClick());
    }

    // Getters
    public String getUsuario() { return txtUsuario.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public JButton getBtnIngresar() { return btnIngresar; }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Acceso", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new VistaLogin().setVisible(true));
    }
}