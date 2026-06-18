package com.eventsecurityapp.vista;

import com.eventsecurityapp.modelo.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Menú Principal (Dashboard) de EventSecurityApp.
 * Actúa como el centro neurálgico del sistema, integrando de forma estética
 * y modular los accesos a los formularios de Gestión, Seguridad y Ayuda.
 */
public class MainMenuForm extends JFrame {

    private final User usuarioLogueado;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor del menú principal.
     *
     * @param usuarioLogueado El usuario autenticado que inició sesión.
     */
    public MainMenuForm(User usuarioLogueado) {
        super("Menú Principal - EventSecurityApp");
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("El usuario logueado no puede ser nulo.");
        }
        this.usuarioLogueado = usuarioLogueado;
        inicializarComponentes();
    }

    /**
     * Inicializa los componentes visuales de la ventana y el menú superior.
     */
    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null); // Centrar en pantalla
        setLayout(new BorderLayout());

        // --- 1. MENÚ SUPERIOR (JMenuBar) ---
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(240, 240, 245));
        menuBar.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // Menú Gestión
        JMenu menuGestion = new JMenu("Gestión");
        menuGestion.setFont(new Font("SansSerif", Font.BOLD, 13));
        menuGestion.setForeground(new Color(50, 50, 50));

        JMenuItem itemEventos = new JMenuItem("Eventos");
        itemEventos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JMenuItem itemClientes = new JMenuItem("Clientes");
        itemClientes.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JMenuItem itemVentas = new JMenuItem("Ventas");
        itemVentas.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JMenuItem itemPagos = new JMenuItem("Pagos");
        itemPagos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JMenuItem itemAsistencia = new JMenuItem("Asistencia (Seguridad)");
        itemAsistencia.setFont(new Font("SansSerif", Font.PLAIN, 12));

        menuGestion.add(itemEventos);
        menuGestion.add(itemClientes);
        menuGestion.addSeparator();
        menuGestion.add(itemVentas);
        menuGestion.add(itemPagos);
        menuGestion.addSeparator();
        menuGestion.add(itemAsistencia);

        // Menú Seguridad
        JMenu menuSeguridad = new JMenu("Seguridad");
        menuSeguridad.setFont(new Font("SansSerif", Font.BOLD, 13));
        menuSeguridad.setForeground(new Color(50, 50, 50));

        JMenuItem itemPassword = new JMenuItem("Cambiar Contraseña");
        itemPassword.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JMenuItem itemLogout = new JMenuItem("Cerrar Sesión");
        itemLogout.setFont(new Font("SansSerif", Font.PLAIN, 12));

        menuSeguridad.add(itemPassword);
        menuSeguridad.addSeparator();
        menuSeguridad.add(itemLogout);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setFont(new Font("SansSerif", Font.BOLD, 13));
        menuAyuda.setForeground(new Color(50, 50, 50));

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");
        itemAcercaDe.setFont(new Font("SansSerif", Font.PLAIN, 12));
        menuAyuda.add(itemAcercaDe);

        // Agregar menús a la barra de menús
        menuBar.add(menuGestion);
        menuBar.add(menuSeguridad);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);

        // --- 2. CONTENIDO PRINCIPAL (Diseño Premium) ---
        // Panel con gradiente y estilo moderno en el centro
        JPanel panelCentral = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradiente suave premium
                GradientPaint gp = new GradientPaint(0, 0, new Color(28, 40, 51), getWidth(), getHeight(), new Color(44, 62, 80));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelCentral.setLayout(new GridBagLayout());
        panelCentral.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Título de bienvenida
        JLabel lblTitulo = new JLabel("EventSecurityApp");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 38));
        lblTitulo.setForeground(new Color(236, 240, 241));
        panelCentral.add(lblTitulo, gbc);

        // Subtítulo
        gbc.gridy = 1;
        JLabel lblSubtitulo = new JLabel("Sistema Integrado de Ventas, Control de Acceso y Seguridad");
        lblSubtitulo.setFont(new Font("SansSerif", Font.ITALIC, 16));
        lblSubtitulo.setForeground(new Color(189, 195, 199));
        panelCentral.add(lblSubtitulo, gbc);

        // Cuadro de información del usuario actual (Efecto Glassmorphism)
        gbc.gridy = 2;
        gbc.insets = new Insets(40, 10, 10, 10);
        JPanel panelUsuario = new JPanel(new GridLayout(3, 1, 8, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                // Fondo semi-transparente para dar efecto premium
                g.setColor(new Color(255, 255, 255, 15));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g.setColor(new Color(255, 255, 255, 30));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        panelUsuario.setOpaque(false);
        panelUsuario.setBorder(new EmptyBorder(20, 30, 20, 30));
        panelUsuario.setPreferredSize(new Dimension(450, 130));

        JLabel lblSesion = new JLabel("SESIÓN ACTIVA", SwingConstants.CENTER);
        lblSesion.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSesion.setForeground(new Color(52, 152, 219));

        JLabel lblNombre = new JLabel("Operador: " + usuarioLogueado.getNombre(), SwingConstants.CENTER);
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNombre.setForeground(Color.WHITE);

        JLabel lblEmail = new JLabel("Correo: " + usuarioLogueado.getEmail(), SwingConstants.CENTER);
        lblEmail.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblEmail.setForeground(new Color(200, 200, 200));

        panelUsuario.add(lblSesion);
        panelUsuario.add(lblNombre);
        panelUsuario.add(lblEmail);

        panelCentral.add(panelUsuario, gbc);

        add(panelCentral, BorderLayout.CENTER);

        // --- 3. BARRA DE ESTADO INFERIOR (Footer) ---
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(230, 230, 235));
        panelFooter.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel lblFooterIzquierda = new JLabel("Conectado a SQL Server (EventSecurityDB)");
        lblFooterIzquierda.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblFooterIzquierda.setForeground(new Color(100, 100, 100));

        JLabel lblFooterDerecha = new JLabel("Fecha de Ingreso: " + LocalDateTime.now().format(TIME_FORMATTER));
        lblFooterDerecha.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblFooterDerecha.setForeground(new Color(100, 100, 100));

        panelFooter.add(lblFooterIzquierda, BorderLayout.WEST);
        panelFooter.add(lblFooterDerecha, BorderLayout.EAST);

        add(panelFooter, BorderLayout.SOUTH);

        // Configurar los listeners
        configurarEventos(itemEventos, itemClientes, itemVentas, itemPagos, itemAsistencia, itemPassword, itemLogout, itemAcercaDe);
    }

    /**
     * Asocia los oyentes de eventos a las opciones del menú principal.
     */
    private void configurarEventos(JMenuItem itemEventos, JMenuItem itemClientes, JMenuItem itemVentas, JMenuItem itemPagos,
                                   JMenuItem itemAsistencia, JMenuItem itemPassword, JMenuItem itemLogout, JMenuItem itemAcercaDe) {
        
        // Gestión -> Eventos
        itemEventos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new EventoReadForm().setVisible(true);
                    }
                });
            }
        });

        // Gestión -> Clientes
        itemClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new ClienteReadForm().setVisible(true);
                    }
                });
            }
        });

        // Gestión -> Ventas
        itemVentas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new VentaForm().setVisible(true);
                    }
                });
            }
        });

        // Gestión -> Pagos
        itemPagos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new PagoForm().setVisible(true);
                    }
                });
            }
        });

        // Gestión -> Asistencia
        itemAsistencia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new AsistenciaForm().setVisible(true);
                    }
                });
            }
        });

        // Seguridad -> Cambiar Contraseña
        itemPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        PasswordForm pwdForm = new PasswordForm(MainMenuForm.this, usuarioLogueado);
                        pwdForm.setVisible(true);
                    }
                });
            }
        });

        // Seguridad -> Cerrar Sesión
        itemLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainMenuForm.this,
                        "¿Está seguro de que desea cerrar la sesión actual?",
                        "Cerrar Sesión",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    dispose(); // Cerrar menú principal
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new LoginForm().setVisible(true); // Re-abrir inicio de sesión
                        }
                    });
                }
            }
        });

        // Ayuda -> Acerca de
        itemAcercaDe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainMenuForm.this,
                        "EventSecurityApp - Versión 1.0\n\n"
                                + "Sistema de gestión integral y control de seguridad en eventos.\n"
                                + "Desarrollado en Java Swing premium con arquitectura limpia (MVC/DAO).\n\n"
                                + "© 2026 Todos los derechos reservados.",
                        "Acerca de EventSecurityApp",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}
