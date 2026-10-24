package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class VistaBackup extends JFrame {
    public VistaBackup() {
        setTitle("Copia de Seguridad - Mi Redentor");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(44, 62, 80));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("Generar Copia de Seguridad");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><center>Selecciona una carpeta para guardar el archivo <b>funeraria.db</b><br>con toda la data del sistema.</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnBackup = new JButton("Seleccionar Carpeta y Guardar");
        btnBackup.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBackup.setBackground(new Color(39, 174, 96));
        btnBackup.setForeground(Color.WHITE);
        btnBackup.setFocusPainted(false);
        btnBackup.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBackup.setMaximumSize(new Dimension(250, 40));

        btnBackup.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Seleccione dónde guardar el Backup");
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File origen = new File("funeraria.db");
                    File destino = new File(chooser.getSelectedFile(), "Backup_Funeraria_" + System.currentTimeMillis() + ".db");
                    Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "¡Copia de seguridad guardada con éxito en:\n" + destino.getAbsolutePath(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al guardar copia: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVolver.addActionListener(e -> dispose());

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblDesc);
        panel.add(Box.createVerticalStrut(30));
        panel.add(btnBackup);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnVolver);

        add(panel, BorderLayout.CENTER);
    }
}