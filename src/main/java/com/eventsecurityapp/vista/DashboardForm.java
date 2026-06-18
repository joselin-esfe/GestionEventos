package com.eventsecurityapp.vista;

import com.eventsecurityapp.modelo.User;
import com.eventsecurityapp.modelo.VentaDetalle;
import com.eventsecurityapp.persistencia.ClienteDAO;
import com.eventsecurityapp.persistencia.EventoDAO;
import com.eventsecurityapp.persistencia.VentaDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard principal de la aplicación EventSecurityApp.
 * Proporciona una interfaz unificada mediante un panel de navegación lateral (Sidebar)
 * y un contenedor de módulos basado en CardLayout.
 */
public class DashboardForm extends JFrame {

    private final User usuarioLogueado;
    private JPanel panelCartas;
    private CardLayout cardLayout;

    // Paneles de los Módulos
    private EventoReadForm panelEventos;
    private ClienteReadForm panelClientes;
    private VentaForm panelVentas;
    private PagoForm panelPagos;
    private AsistenciaForm panelAsistencias;

    // Componentes del Panel de Estadísticas (Inicio)
    private JLabel lblStatEventos;
    private JLabel lblStatClientes;
    private JLabel lblStatVentas;
    private JLabel lblStatIngresos;

    // Botones del Sidebar
    private SidebarButton btnMenuInicio;
    private SidebarButton btnMenuEventos;
    private SidebarButton btnMenuClientes;
    private SidebarButton btnMenuVentas;
    private SidebarButton btnMenuPagos;
    private SidebarButton btnMenuAsistencias;
    private SidebarButton btnMenuPassword;
    private SidebarButton btnMenuLogout;

    /**
     * Constructor de la ventana del Dashboard.
     *
     * @param usuarioLogueado Sesión del usuario autenticado.
     */
    public DashboardForm(User usuarioLogueado) {
        super("Panel de Control - EventSecurityApp");
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("El usuario autenticado no puede ser nulo.");
        }
        this.usuarioLogueado = usuarioLogueado;
        inicializarComponentes();
        actualizarEstadisticas();
    }

    /**
     * Inicializa la interfaz gráfica con el Sidebar y el CardLayout principal.
     */
    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null); // Centrar en pantalla
        setMinimumSize(new Dimension(1050, 650));
        setLayout(new BorderLayout());

        // --- 1. BARRA LATERAL (SIDEBAR) ---
        JPanel panelSidebar = new JPanel();
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBackground(ModernComponents.COLOR_PRIMARY);
        panelSidebar.setPreferredSize(new Dimension(240, 0));
        panelSidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Título del Sistema
        JLabel lblAppTitle = new JLabel("EventSecurityApp");
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAppTitle.setForeground(Color.WHITE);
        lblAppTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAppTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        panelSidebar.add(lblAppTitle);

        // Subtítulo / Separador
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setForeground(new Color(255, 255, 255, 60));
        panelSidebar.add(Box.createVerticalStrut(10));
        panelSidebar.add(sep);
        panelSidebar.add(Box.createVerticalStrut(20));

        // Perfil del Usuario
        JLabel lblUserIcon = new JLabel("● Online");
        lblUserIcon.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUserIcon.setForeground(new Color(46, 204, 113));
        lblUserIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSidebar.add(lblUserIcon);

        JLabel lblUsername = new JLabel(usuarioLogueado.getNombre());
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUsername.setBorder(new EmptyBorder(4, 0, 20, 0));
        panelSidebar.add(lblUsername);

        // Opciones de navegación
        btnMenuInicio = new SidebarButton("Inicio", true);
        btnMenuEventos = new SidebarButton("Eventos", false);
        btnMenuClientes = new SidebarButton("Clientes", false);
        btnMenuVentas = new SidebarButton("Ventas", false);
        btnMenuPagos = new SidebarButton("Pagos", false);
        btnMenuAsistencias = new SidebarButton("Asistencias", false);
        btnMenuPassword = new SidebarButton("Cambiar contraseña", false);
        btnMenuLogout = new SidebarButton("Cerrar sesión", false);

        panelSidebar.add(btnMenuInicio);
        panelSidebar.add(btnMenuEventos);
        panelSidebar.add(btnMenuClientes);
        panelSidebar.add(btnMenuVentas);
        panelSidebar.add(btnMenuPagos);
        panelSidebar.add(btnMenuAsistencias);
        panelSidebar.add(Box.createVerticalGlue()); // Espaciador dinámico
        panelSidebar.add(btnMenuPassword);
        panelSidebar.add(btnMenuLogout);

        add(panelSidebar, BorderLayout.WEST);

        // --- 2. CONTENEDOR CENTRAL DE MÓDULOS (CardLayout) ---
        cardLayout = new CardLayout();
        panelCartas = new JPanel(cardLayout);
        panelCartas.setBackground(ModernComponents.COLOR_BACKGROUND);

        // Instanciar los módulos
        panelEventos = new EventoReadForm();
        panelClientes = new ClienteReadForm();
        panelVentas = new VentaForm();
        panelPagos = new PagoForm();
        panelAsistencias = new AsistenciaForm();

        // Agregar módulos al CardLayout
        panelCartas.add(crearPanelInicio(), "Inicio");
        panelCartas.add(panelEventos, "Eventos");
        panelCartas.add(panelClientes, "Clientes");
        panelCartas.add(panelVentas, "Ventas");
        panelCartas.add(panelPagos, "Pagos");
        panelCartas.add(panelAsistencias, "Asistencias");

        add(panelCartas, BorderLayout.CENTER);

        // Configurar Eventos del Sidebar
        configurarNavegacion();
    }

    /**
     * Crea el Panel de Inicio (Dashboard de Estadísticas y Bienvenida).
     *
     * @return JPanel con el diseño del Dashboard.
     */
    private JPanel crearPanelInicio() {
        JPanel panelInicio = new JPanel(new BorderLayout(20, 20));
        panelInicio.setBackground(ModernComponents.COLOR_BACKGROUND);
        panelInicio.setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- Panel Superior: Bienvenida ---
        JPanel panelWelcome = new JPanel(new GridBagLayout());
        panelWelcome.setBackground(Color.WHITE);
        panelWelcome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;

        JLabel lblGreeting = new JLabel("¡Hola de nuevo, " + usuarioLogueado.getNombre() + "!");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblGreeting.setForeground(ModernComponents.COLOR_PRIMARY);
        panelWelcome.add(lblGreeting, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(5, 0, 0, 0);
        JLabel lblDesc = new JLabel("Bienvenido al panel de control integrado de EventSecurityApp.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(ModernComponents.COLOR_MUTED);
        panelWelcome.add(lblDesc, gbc);

        panelInicio.add(panelWelcome, BorderLayout.NORTH);

        // --- Panel Central: Tarjetas de Estadísticas (Grid) ---
        JPanel panelStatsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        panelStatsGrid.setOpaque(false);

        // Tarjeta 1: Eventos Totales
        lblStatEventos = new JLabel("0", SwingConstants.CENTER);
        panelStatsGrid.add(crearCardEstadistica("Eventos Registrados", lblStatEventos, new Color(41, 128, 185)));

        // Tarjeta 2: Clientes Registrados
        lblStatClientes = new JLabel("0", SwingConstants.CENTER);
        panelStatsGrid.add(crearCardEstadistica("Clientes Registrados", lblStatClientes, new Color(39, 174, 96)));

        // Tarjeta 3: Ventas Realizadas
        lblStatVentas = new JLabel("0", SwingConstants.CENTER);
        panelStatsGrid.add(crearCardEstadistica("Ventas de Entradas", lblStatVentas, new Color(241, 196, 15)));

        // Tarjeta 4: Total Recaudado ($)
        lblStatIngresos = new JLabel("$0.00", SwingConstants.CENTER);
        panelStatsGrid.add(crearCardEstadistica("Ingresos Totales", lblStatIngresos, new Color(155, 89, 182)));

        panelInicio.add(panelStatsGrid, BorderLayout.CENTER);

        return panelInicio;
    }

    /**
     * Helper para crear una tarjeta de estadística visualmente premium.
     */
    private JPanel crearCardEstadistica(String titulo, JLabel lblValor, Color colorAcento) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dibujar línea de acento superior
                g.setColor(colorAcento);
                g.fillRect(0, 0, getWidth(), 5);
            }
        };
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(ModernComponents.COLOR_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(ModernComponents.COLOR_TEXT);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    /**
     * Realiza consultas a los DAOs en segundo plano para actualizar los indicadores numéricos del Inicio.
     */
    public void actualizarEstadisticas() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private int totalEventos = 0;
            private int totalClientes = 0;
            private int totalVentas = 0;
            private BigDecimal totalRecaudado = BigDecimal.ZERO;

            @Override
            protected Void doInBackground() {
                try {
                    totalEventos = new EventoDAO().listar().size();
                    totalClientes = new ClienteDAO().listar().size();
                    
                    List<VentaDetalle> ventas = new VentaDAO().listar();
                    totalVentas = ventas.size();
                    for (VentaDetalle v : ventas) {
                        if (v.isStatus() && v.getTotal() != null) {
                            totalRecaudado = totalRecaudado.add(v.getTotal());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error al cargar estadísticas en segundo plano: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                lblStatEventos.setText(String.valueOf(totalEventos));
                lblStatClientes.setText(String.valueOf(totalClientes));
                lblStatVentas.setText(String.valueOf(totalVentas));
                lblStatIngresos.setText(String.format("$%,.2f", totalRecaudado));
            }
        };
        worker.execute();
    }

    /**
     * Asocia la lógica de CardLayout a las opciones de navegación lateral del Sidebar.
     */
    private void configurarNavegacion() {
        btnMenuInicio.addActionListener(e -> {
            marcarSeleccionado(btnMenuInicio);
            actualizarEstadisticas();
            cardLayout.show(panelCartas, "Inicio");
        });

        btnMenuEventos.addActionListener(e -> {
            marcarSeleccionado(btnMenuEventos);
            panelEventos.cargarDatos();
            cardLayout.show(panelCartas, "Eventos");
        });

        btnMenuClientes.addActionListener(e -> {
            marcarSeleccionado(btnMenuClientes);
            panelClientes.cargarDatos();
            cardLayout.show(panelCartas, "Clientes");
        });

        btnMenuVentas.addActionListener(e -> {
            marcarSeleccionado(btnMenuVentas);
            cardLayout.show(panelCartas, "Ventas");
        });

        btnMenuPagos.addActionListener(e -> {
            marcarSeleccionado(btnMenuPagos);
            cardLayout.show(panelCartas, "Pagos");
        });

        btnMenuAsistencias.addActionListener(e -> {
            marcarSeleccionado(btnMenuAsistencias);
            cardLayout.show(panelCartas, "Asistencias");
        });

        btnMenuPassword.addActionListener(e -> {
            // Se abre como un JDialog modal pasando este Frame como parent
            PasswordForm pwdForm = new PasswordForm(DashboardForm.this, usuarioLogueado);
            pwdForm.setVisible(true);
        });

        btnMenuLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(DashboardForm.this,
                    "¿Desea cerrar la sesión actual?",
                    "Cerrar Sesión",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
            }
        });
    }

    /**
     * Resalta visualmente el botón seleccionado en la barra lateral.
     */
    private void marcarSeleccionado(SidebarButton seleccionado) {
        SidebarButton[] botones = {btnMenuInicio, btnMenuEventos, btnMenuClientes, btnMenuVentas, btnMenuPagos, btnMenuAsistencias};
        for (SidebarButton btn : botones) {
            btn.setSelected(btn == seleccionado);
        }
    }

    /**
     * Componente gráfico personalizado para los botones del menú lateral.
     */
    private static class SidebarButton extends JButton {
        private boolean selected;
        private static final Color COLOR_HOVER = new Color(62, 88, 121);
        private static final Color COLOR_SELECTED = new Color(33, 53, 85);

        public SidebarButton(String text, boolean selected) {
            super(text);
            this.selected = selected;
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(220, 40));
            setPreferredSize(new Dimension(220, 40));

            // Margen izquierdo para el texto
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, 25, 0, 0));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isSelected()) {
                        setBackground(COLOR_HOVER);
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isSelected()) {
                        setBackground(null);
                        repaint();
                    }
                }
            });
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (selected) {
                // Dibujar fondo seleccionado
                g2.setColor(COLOR_HOVER);
                g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 8, 8);
                // Dibujar línea de acento izquierda
                g2.setColor(ModernComponents.COLOR_ACCENT);
                g2.fillRect(12, 10, 4, getHeight() - 20);
            } else if (getBackground() != null) {
                // Dibujar hover
                g2.setColor(getBackground());
                g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 8, 8);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
