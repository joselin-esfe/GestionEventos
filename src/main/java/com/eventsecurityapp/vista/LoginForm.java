package com.eventsecurityapp.vista;

import com.eventsecurityapp.modelo.User;
import com.eventsecurityapp.persistencia.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Formulario de inicio de sesión (Login) rediseñado de forma moderna y limpia.
 * Sigue la paleta de colores corporativos del sistema y utiliza fuentes premium.
 */
public class LoginForm extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnCancelar;
    private UserDAO userDAO;

    // Colores corporativos
    private static final Color COLOR_PRIMARY = new Color(33, 53, 85);      // #213555
    private static final Color COLOR_SECONDARY = new Color(62, 88, 121);  // #3E5879
    private static final Color COLOR_ACCENT = new Color(93, 156, 236);     // #5D9CEC
    private static final Color COLOR_BACKGROUND = new Color(245, 247, 250); // #F5F7FA
    private static final Color COLOR_TEXT = new Color(40, 40, 40);         // #282828

    /**
     * Constructor del formulario de inicio de sesión.
     */
    public LoginForm() {
        super("Iniciar Sesión - EventSecurityApp");
        this.userDAO = new UserDAO();
        inicializarComponentes();
    }

    /**
     * Inicializa y organiza los componentes visuales en la interfaz.
     */
    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 450);
        setLocationRelativeTo(null); // Centrar en pantalla
        setResizable(false);

        // Panel principal con fondo general
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_BACKGROUND);

        // --- Panel Superior (Banner/Encabezado) ---
        JPanel panelHeader = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, COLOR_PRIMARY, getWidth(), getHeight(), COLOR_SECONDARY);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelHeader.setPreferredSize(new Dimension(0, 100));
        panelHeader.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblHeaderTitle = new JLabel("EventSecurityApp", SwingConstants.CENTER);
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeaderTitle.setForeground(Color.WHITE);
        panelHeader.add(lblHeaderTitle, BorderLayout.CENTER);

        panelPrincipal.add(panelHeader, BorderLayout.NORTH);

        // --- Panel Central (Formulario) ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Título de Bienvenida
        gbc.gridy = 0;
        JLabel lblBienvenido = new JLabel("Bienvenido al Sistema", SwingConstants.CENTER);
        lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBienvenido.setForeground(COLOR_TEXT);
        panelForm.add(lblBienvenido, gbc);

        // Campo: Correo Electrónico
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 0, 2, 0);
        JLabel lblEmail = new JLabel("Correo Electrónico");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEmail.setForeground(COLOR_SECONDARY);
        panelForm.add(lblEmail, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEmail.setPreferredSize(new Dimension(0, 32));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        panelForm.add(txtEmail, gbc);

        // Campo: Contraseña
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 2, 0);
        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(COLOR_SECONDARY);
        panelForm.add(lblPassword, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 20, 0);
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(0, 32));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        panelForm.add(txtPassword, gbc);

        // Panel de botones (Ingresar y Cancelar)
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 15, 0));
        panelBotones.setOpaque(false);

        btnIngresar = new ModernButton("Ingresar", COLOR_PRIMARY, COLOR_SECONDARY);
        btnIngresar.setPreferredSize(new Dimension(0, 36));

        btnCancelar = new ModernButton("Cancelar", new Color(180, 180, 180), new Color(150, 150, 150));
        btnCancelar.setPreferredSize(new Dimension(0, 36));

        panelBotones.add(btnIngresar);
        panelBotones.add(btnCancelar);
        panelForm.add(panelBotones, gbc);

        panelPrincipal.add(panelForm, BorderLayout.CENTER);
        add(panelPrincipal);

        configurarEventos();
    }

    /**
     * Configura los controladores de eventos para los botones de acción.
     */
    private void configurarEventos() {
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarLogin();
            }
        });

        // Permitir presionar ENTER en el password field para ingresar
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarLogin();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * Valida los campos y procesa la autenticación a través del UserDAO.
     */
    private void procesarLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos.",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean autenticado = userDAO.autenticar(email, password);

            if (autenticado) {
                User user = userDAO.obtenerPorEmail(email);
                String nombreUsuario = (user != null) ? user.getNombre() : "Usuario";

                JOptionPane.showMessageDialog(this,
                        "¡Bienvenido, " + nombreUsuario + "!",
                        "Login Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                this.dispose();

                // Abrir el Dashboard principal en el hilo EDT de Swing
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new DashboardForm(user).setVisible(true);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this,
                        "Credenciales incorrectas o usuario inactivo.",
                        "Error de Autenticación",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Componente JButton personalizado de forma premium para la estética moderna.
     */
    private static class ModernButton extends JButton {
        private final Color bgNormal;
        private final Color bgHover;

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
