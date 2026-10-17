package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPrincipal extends JFrame {

    // Paleta de colores unificada
    private static final Color C_BG = new Color(245, 246, 250);
    private static final Color C_PRIMARY = new Color(44, 62, 80);
    private static final Color C_TEXT = new Color(50, 50, 50);
    
    private static final Color C_BLUE = new Color(41, 128, 185);
    private static final Color C_GREEN = new Color(39, 174, 96);
    private static final Color C_ORANGE = new Color(211, 84, 0);
    private static final Color C_PURPLE = new Color(142, 68, 173); // Color extra para proveedores
    private static final Color C_RED = new Color(192, 57, 43);

    public MenuPrincipal() {
        setTitle("Sistema Administrativo - Funeraria Mi Redentor");
        setSize(850, 650);
        setMinimumSize(new Dimension(750, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_BG);

        // ==========================================
        // 1. HEADER (Barra Superior)
        // ==========================================
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(C_PRIMARY);
        panelSuperior.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel lblTitulo = new JLabel("Funeraria Mi Redentor");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Panel de Administración y Gestión");
        lblSubtitulo.setForeground(new Color(189, 195, 199));
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelSuperior.add(lblTitulo);
        panelSuperior.add(Box.createVerticalStrut(5));
        panelSuperior.add(lblSubtitulo);
        
        add(panelSuperior, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTRO (Cuadrícula de Tarjetas)
        // ==========================================
        // Cambiado a 3 filas x 2 columnas para que quepan las 5 tarjetas
        JPanel panelTarjetas = new JPanel(new GridLayout(3, 2, 20, 20));
        panelTarjetas.setBackground(C_BG);
        panelTarjetas.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Crear las tarjetas
        RoundedPanel cardClientes = crearTarjeta("Clientes", "Gestión de titulares", C_BLUE, "👥");
        RoundedPanel cardInventario = crearTarjeta("Inventario", "Control de productos", C_GREEN, "📦");
        RoundedPanel cardFacturacion = crearTarjeta("Facturación", "Emitir nuevas facturas", C_ORANGE, "🧾");
        
        // NUEVA TARJETA DE PROVEEDORES
        RoundedPanel cardProveedores = crearTarjeta("Proveedores", "Gestión de Seguros", C_PURPLE, "🏢");
        
        RoundedPanel cardSalir = crearTarjeta("Salir", "Cerrar la aplicación", C_RED, "🚪");

        // Asignar acciones (Listeners)
        asignarAccion(cardClientes, () -> new VistaClientes().setVisible(true));
        asignarAccion(cardInventario, () -> new VistaInventario().setVisible(true));
        asignarAccion(cardFacturacion, () -> new VistaFacturacion().setVisible(true));
        asignarAccion(cardProveedores, () -> new VistaProveedores().setVisible(true)); // ACCIÓN PROVEEDORES
        asignarAccion(cardSalir, () -> System.exit(0));

        // Agregar al panel
        panelTarjetas.add(cardClientes);
        panelTarjetas.add(cardInventario);
        panelTarjetas.add(cardFacturacion);
        panelTarjetas.add(cardProveedores); // SE AGREGA AL GRID
        panelTarjetas.add(cardSalir);

        add(panelTarjetas, BorderLayout.CENTER);

        // ==========================================
        // 3. FOOTER (Pie de página)
        // ==========================================
        JLabel lblFooter = new JLabel("© 2026 Funeraria Mi Redentor. Todos los derechos reservados.");
        lblFooter.setForeground(new Color(127, 140, 141));
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    // ==========================================
    // MÉTODO PARA CREAR TARJETAS MODERNAS CON TEXTO E ICONOS
    // ==========================================
    private RoundedPanel crearTarjeta(String titulo, String subtitulo, Color color, String emoji) {
        RoundedPanel card = new RoundedPanel(Color.WHITE, new Color(220, 223, 230), color);
        card.setLayout(new GridBagLayout());
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. Icono (Emoji)
        JLabel lblIcono = new JLabel(emoji);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 55));
        card.add(lblIcono, gbc);

        // 2. Título
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 5, 10);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(color); 
        card.add(lblTitulo, gbc);

        // 3. Subtítulo
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 10, 10);
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(C_TEXT);
        card.add(lblSub, gbc);

        // Efectos Hover
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

    // ==========================================
    // CLASE INTERNA: PANEL CON ESQUINAS REDONDEADAS
    // ==========================================
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}