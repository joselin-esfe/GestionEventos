package com.eventsecurityapp.vista;

import com.eventsecurityapp.modelo.User;
import com.eventsecurityapp.persistencia.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Formulario de inicio de sesión (Login) para EventSecurityApp.
 * Desarrollado utilizando componentes estándar de Java Swing.
 */
public class LoginForm extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnCancelar;
    private UserDAO userDAO;

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
        // Configuración básica de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 220);
        setLocationRelativeTo(null); // Centrar en pantalla
        setResizable(false);

        // Panel principal con diseño GridBagLayout para centrado estructurado
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8); // Márgenes internos

        // Etiqueta y campo de correo electrónico
        JLabel lblEmail = new JLabel("Correo Electrónico:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(lblEmail, gbc);

        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        panel.add(txtEmail, gbc);

        // Etiqueta y campo de contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        panel.add(txtPassword, gbc);

        // Panel inferior para los botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnIngresar = new JButton("Ingresar");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnIngresar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Ocupa ambas columnas
        gbc.weightx = 1.0;
        panel.add(panelBotones, gbc);

        // Agregar el panel principal a la ventana
        add(panel);

        // Configurar los listeners
        configurarEventos();
    }

    /**
     * Configura los controladores de eventos para los botones de acción.
     */
    private void configurarEventos() {
        // Evento para el botón Ingresar
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarLogin();
            }
        });

        // Evento para el botón Cancelar
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Si el usuario presiona Cancelar, se cierra la aplicación
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

        // 1. Validar que los campos no estén vacíos
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos.",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 2. Autenticar utilizando UserDAO
            boolean autenticado = userDAO.autenticar(email, password);

            if (autenticado) {
                // Obtener datos del usuario autenticado
                User user = userDAO.obtenerPorEmail(email);
                String nombreUsuario = (user != null) ? user.getNombre() : "Usuario";

                // Mostrar mensaje de bienvenida
                JOptionPane.showMessageDialog(this,
                        "¡Bienvenido, " + nombreUsuario + "!",
                        "Login Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                // Cerrar el formulario de Login
                this.dispose();

                // Abrir la ventana principal (EventoReadForm)
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new EventoReadForm().setVisible(true);
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
}
