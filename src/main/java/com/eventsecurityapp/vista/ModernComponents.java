package com.eventsecurityapp.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Componentes gráficos y utilidades de estilo premium reutilizables.
 * Implementa la paleta de colores y la tipografía Segoe UI solicitada.
 */
public class ModernComponents {

    // Paleta de Colores
    public static final Color COLOR_PRIMARY = new Color(33, 53, 85);       // #213555
    public static final Color COLOR_SECONDARY = new Color(62, 88, 121);   // #3E5879
    public static final Color COLOR_ACCENT = new Color(93, 156, 236);      // #5D9CEC
    public static final Color COLOR_BACKGROUND = new Color(245, 247, 250);  // #F5F7FA
    public static final Color COLOR_TEXT = new Color(40, 40, 40);          // #282828
    public static final Color COLOR_MUTED = new Color(120, 120, 130);

    /**
     * Aplica estilos modernos a un JTable de forma uniforme.
     *
     * @param table Tabla a la que aplicar estilos.
     */
    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setGridColor(new Color(230, 230, 235));
        table.setSelectionBackground(COLOR_ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setShowVerticalLines(false);

        // Cabecera de la tabla
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(COLOR_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 32));

        // Alineación y bordes de la celda de cabecera
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    /**
     * Aplica el estilo moderno Segoe UI y padding a un campo de texto.
     *
     * @param field Campo de texto a estilizar.
     */
    public static void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 204), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    /**
     * Aplica estilos de etiqueta premium.
     *
     * @param label Etiqueta a estilizar.
     */
    public static void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_SECONDARY);
    }

    /**
     * Botón Swing Premium con bordes redondeados y efectos hover.
     */
    public static class ModernButton extends JButton {
        private final Color bgNormal;
        private final Color bgHover;

        public ModernButton(String text) {
            this(text, COLOR_PRIMARY, COLOR_SECONDARY);
        }

        public ModernButton(String text, Color bgNormal, Color bgHover) {
            super(text);
            this.bgNormal = bgNormal;
            this.bgHover = bgHover;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBackground(bgNormal);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(bgHover);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(bgNormal);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
