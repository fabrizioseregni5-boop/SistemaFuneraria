package vista;

import modelo.Sesion;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPrincipal extends JFrame {

    private static final Color C_BG = new Color(245, 246, 250);
    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_TEXT = new Color(50, 50, 50);
    
    private static final Color C_BLUE = new Color(41, 128, 185);
    private static final Color C_GREEN = new Color(39, 174, 96);
    private static final Color C_ORANGE = new Color(211, 84, 0);
    private static final Color C_PURPLE = new Color(142, 68, 173);
    private static final Color C_RED = new Color(192, 57, 43);
    private static final Color C_TEAL = new Color(26, 188, 156); // Para Cobrar
    private static final Color C_DARK = new Color(52, 73, 94);   // Para Pagar

    public MenuPrincipal() {
        setTitle("Sistema Administrativo - Funeraria Mi Redentor");
        setSize(1050, 800); // Ventana un poco más alta para que quepa todo
        setMinimumSize(new Dimension(900, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_BG);

        // HEADER
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(C_PRIMARY);
        panelSuperior.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblTitulo = new JLabel("Funeraria Mi Redentor");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String rolTexto = Sesion.rolActual == 1 ? "Master" : (Sesion.rolActual == 2 ? "Administrador" : "Usuario");
        JLabel lblSubtitulo = new JLabel("Bienvenido: " + Sesion.nombreUsuario + " | Rol: " + rolTexto);
        lblSubtitulo.setForeground(new Color(189, 195, 199));
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelSuperior.add(lblTitulo);
        panelSuperior.add(Box.createVerticalStrut(5));
        panelSuperior.add(lblSubtitulo);
        add(panelSuperior, BorderLayout.NORTH);

        // TARJETAS
        JPanel panelTarjetas = new JPanel();
        panelTarjetas.setBackground(C_BG);
        panelTarjetas.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Crear Tarjetas Básicas
        RoundedPanel cardClientes = crearTarjeta("Clientes", "Gestión de titulares", C_BLUE, "👥");
        RoundedPanel cardInventario = crearTarjeta("Inventario", "Control de productos", C_GREEN, "📦");
        RoundedPanel cardFacturacion = crearTarjeta("Facturación", "Emitir nuevas facturas", C_ORANGE, "🧾");
        RoundedPanel cardProveedores = crearTarjeta("Proveedores", "Gestión de Seguros", C_PURPLE, "🏢");
        
        // Crear Nuevas Tarjetas (Cuentas y Reportes)
        RoundedPanel cardCobrar = crearTarjeta("Ctas. por Cobrar", "Ingresos pendientes", C_TEAL, "💰");
        RoundedPanel cardPagar = crearTarjeta("Ctas. por Pagar", "Egresos pendientes", C_DARK, "💸");
        RoundedPanel cardReportes = crearTarjeta("Reportes", "Auditoría y Facturas", new Color(155, 89, 182), "📊");
        
        // Crear Tarjetas Especiales
        RoundedPanel cardBackup = crearTarjeta("Backup", "Copia de Seguridad", new Color(149, 165, 166), "💾");
        RoundedPanel cardUsuarios = crearTarjeta("Usuarios", "Roles y Contraseñas", new Color(230, 126, 34), "🔒");
        RoundedPanel cardSalir = crearTarjeta("Cerrar Sesión", "Volver al Login", C_RED, "🚪");

        // Asignar acciones
        asignarAccion(cardClientes, () -> new VistaClientes().setVisible(true));
        asignarAccion(cardInventario, () -> new VistaInventario().setVisible(true));
        asignarAccion(cardFacturacion, () -> new VistaFacturacion().setVisible(true));
        asignarAccion(cardProveedores, () -> new VistaProveedores().setVisible(true));
        asignarAccion(cardCobrar, () -> new VistaCuentas("COBRAR").setVisible(true));
        asignarAccion(cardPagar, () -> new VistaCuentas("PAGAR").setVisible(true));
        asignarAccion(cardReportes, () -> new VistaReportes().setVisible(true));
        asignarAccion(cardBackup, () -> new VistaBackup().setVisible(true));
        asignarAccion(cardUsuarios, () -> new VistaUsuarios().setVisible(true));
        
        asignarAccion(cardSalir, () -> {
            Sesion.idUsuario = 0; Sesion.nombreUsuario = ""; Sesion.rolActual = 3; 
            this.dispose();
            new VistaLogin().setVisible(true);
        });

        // ==========================================
        // LÓGICA DE ROLES Y ACOMODO DE CUADRÍCULA
        // ==========================================
        if (Sesion.rolActual == 3) { 
            // USUARIO NORMAL: 4 tarjetas (Clientes, Facturación, Reportes, Salir) -> Cuadrícula 2x2
            panelTarjetas.setLayout(new GridLayout(2, 2, 20, 20)); 
            panelTarjetas.add(cardClientes);
            panelTarjetas.add(cardFacturacion);
            panelTarjetas.add(cardReportes);
            panelTarjetas.add(cardSalir);
        } else if (Sesion.rolActual == 2) { 
            // ADMINISTRADOR: 9 tarjetas (Todo menos Backup) -> Cuadrícula 3x3
            panelTarjetas.setLayout(new GridLayout(3, 3, 15, 15)); 
            panelTarjetas.add(cardClientes);
            panelTarjetas.add(cardInventario);
            panelTarjetas.add(cardFacturacion);
            panelTarjetas.add(cardProveedores);
            panelTarjetas.add(cardCobrar);
            panelTarjetas.add(cardPagar);
            panelTarjetas.add(cardReportes);
            panelTarjetas.add(cardUsuarios);
            panelTarjetas.add(cardSalir);
        } else { 
            // MASTER: 10 tarjetas (Todas) -> Cuadrícula 4x3 (Para que quepa el botón de salir)
            panelTarjetas.setLayout(new GridLayout(4, 3, 15, 15));
            panelTarjetas.add(cardClientes);
            panelTarjetas.add(cardInventario);
            panelTarjetas.add(cardFacturacion);
            panelTarjetas.add(cardProveedores);
            panelTarjetas.add(cardCobrar);
            panelTarjetas.add(cardPagar);
            panelTarjetas.add(cardReportes);
            panelTarjetas.add(cardBackup);
            panelTarjetas.add(cardUsuarios);
            panelTarjetas.add(cardSalir); // <--- ESTA ERA LA LÍNEA QUE FALTABA
        }

        add(panelTarjetas, BorderLayout.CENTER);

        // FOOTER
        JLabel lblFooter = new JLabel("© 2026 Funeraria Mi Redentor. Todos los derechos reservados.");
        lblFooter.setForeground(new Color(127, 140, 141));
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    private RoundedPanel crearTarjeta(String titulo, String subtitulo, Color color, String emoji) {
        RoundedPanel card = new RoundedPanel(Color.WHITE, new Color(220, 223, 230), color);
        card.setLayout(new GridBagLayout());
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblIcono = new JLabel(emoji);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        card.add(lblIcono, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 2, 5);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(color); 
        card.add(lblTitulo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 5, 5, 5);
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(C_TEXT);
        card.add(lblSub, gbc);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBgColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
                card.setBorderColor(color); 
                card.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBgColor(Color.WHITE);
                card.setBorderColor(new Color(220, 223, 230));
                card.repaint();
            }
        });
        return card;
    }

    private void asignarAccion(JPanel tarjeta, Runnable accion) {
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                accion.run();
            }
        });
    }

    class RoundedPanel extends JPanel {
        private Color bgColor;
        private Color borderColor;

        public RoundedPanel(Color bgColor, Color borderColor, Color hoverColor) {
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            setOpaque(false); 
        }

        public void setBgColor(Color bgColor) { this.bgColor = bgColor; }
        public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bgColor);
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2)); 
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

            g2.dispose();
        }
    }
}