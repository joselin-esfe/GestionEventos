package com.eventsecurityapp.vista;

import com.eventsecurityapp.modelo.User;
import com.eventsecurityapp.persistencia.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Formulario para el cambio de contraseña del usuario autenticado en EventSecurityApp.
 * Desarrollado con componentes estándar de Java Swing.
 */
public class PasswordForm extends JDialog {

    private JTextField txtEmail;
    private JPasswordField txtPasswordActual;
    private JPasswordField txtPasswordNueva;
    private JPasswordField txtPasswordNuevaConfirmar;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private User usuarioLogueado;
    private UserDAO userDAO;

    /**
     * Constructor del formulario de cambio de contraseña.
     *
     * @param parent Ventana padre que invoca este diálogo modal.
     * @param usuarioLogueado Objeto User del usuario que actualmente tiene sesión iniciada.
     */
    public PasswordForm(Frame parent, User usuarioLogueado) {
        super(parent, "Cambiar Contraseña - EventSecurityApp", true); // Diálogo modal
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("El usuario autenticado no puede ser nulo.");
        }
        this.usuarioLogueado = usuarioLogueado;
        this.userDAO = new UserDAO();
        inicializarComponentes();
    }

    /**
     * Inicializa y distribuye los elementos visuales utilizando GridBagLayout.
     */
    private void inicializarComponentes() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 280);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // 1. Email (Solo lectura)
        JLabel lblEmail = new JLabel("Correo Electrónico:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(lblEmail, gbc);

        txtEmail = new JTextField(usuarioLogueado.getEmail(), 20);
        txtEmail.setEditable(false); // Campo de solo lectura
        txtEmail.setEnabled(false); // Deshabilitar visualmente para denotar solo lectura
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        panel.add(txtEmail, gbc);

        // 2. Contraseña Actual
        JLabel lblPasswordActual = new JLabel("Contraseña Actual:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(lblPasswordActual, gbc);

        txtPasswordActual = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        panel.add(txtPasswordActual, gbc);

        // 3. Nueva Contraseña
        JLabel lblPasswordNueva = new JLabel("Nueva Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        panel.add(lblPasswordNueva, gbc);

        txtPasswordNueva = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.7;
        panel.add(txtPasswordNueva, gbc);

        // 4. Confirmar Nueva Contraseña
        JLabel lblPasswordNuevaConfirmar = new JLabel("Confirmar Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        panel.add(lblPasswordNuevaConfirmar, gbc);

        txtPasswordNuevaConfirmar = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.7;
        panel.add(txtPasswordNuevaConfirmar, gbc);

        // 5. Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(panelBotones, gbc);

        add(panel);

        configurarEventos();
    }

    /**
     * Define los eventos para los botones Guardar y Cancelar.
     */
    private void configurarEventos() {
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarCambioPassword();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * Valida y ejecuta el cambio de contraseña en la base de datos.
     */
    private void procesarCambioPassword() {
        String passwordActual = new String(txtPasswordActual.getPassword()).trim();
        String passwordNueva = new String(txtPasswordNueva.getPassword()).trim();
        String passwordConfirmar = new String(txtPasswordNuevaConfirmar.getPassword()).trim();

        // 1. Validar que los campos no estén vacíos
        if (passwordActual.isEmpty() || passwordNueva.isEmpty() || passwordConfirmar.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos de contraseña son obligatorios.",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Verificar que la contraseña actual sea correcta (se autentica contra el UserDAO)
        boolean actualCorrecta = false;
        try {
            actualCorrecta = userDAO.autenticar(usuarioLogueado.getEmail(), passwordActual);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Contraseña Incorrecta",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!actualCorrecta) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña actual ingresada es incorrecta.",
                    "Contraseña Incorrecta",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Validar que la nueva contraseña y su confirmación coincidan
        if (!passwordNueva.equals(passwordConfirmar)) {
            JOptionPane.showMessageDialog(this,
                    "La nueva contraseña y su confirmación no coinciden.",
                    "Discrepancia de Contraseñas",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. Actualizar la contraseña usando UserDAO
        boolean exito = userDAO.cambiarPassword(usuarioLogueado.getId(), passwordNueva);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña se ha actualizado correctamente.\nDeberá volver a iniciar sesión.",
                    "Cambio Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose(); // Cerrar el formulario de contraseña

            // Si hay una ventana padre activa, la cerramos para forzar el re-login
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow != null) {
                parentWindow.dispose();
            }

            // Abrir un nuevo formulario de Login
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginForm().setVisible(true);
                }
            });

        } else {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error inesperado al actualizar la contraseña.",
                    "Error Interno",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
