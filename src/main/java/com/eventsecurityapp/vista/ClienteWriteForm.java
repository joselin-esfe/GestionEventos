package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.ClienteController;
import com.eventsecurityapp.modelo.Cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diálogo modal para registrar y modificar clientes en EventSecurityApp.
 * Estilizado con los componentes y la paleta de colores premium.
 */
public class ClienteWriteForm extends JDialog {

    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JCheckBox chkStatus;

    private JButton btnGuardar;
    private JButton btnCancelar;

    private ClienteReadForm parentForm;
    private Cliente clienteOriginal;
    private ClienteController controller;

    /**
     * Constructor del formulario.
     *
     * @param parentForm Ventana de lectura de origen.
     * @param cliente    Cliente a editar o null para crear uno nuevo.
     */
    public ClienteWriteForm(ClienteReadForm parentForm, Cliente cliente) {
        super((Frame) SwingUtilities.getWindowAncestor(parentForm), cliente == null ? "Registrar Cliente" : "Modificar Cliente", true);
        this.parentForm = parentForm;
        this.clienteOriginal = cliente;
        this.controller = new ClienteController();
        inicializarComponentes();
        if (cliente != null) {
            cargarCampos();
        }
    }

    /**
     * Inicializa la distribución de elementos de la interfaz.
     */
    private void inicializarComponentes() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(420, 340);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // DNI
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblDni = new JLabel("DNI (*):");
        ModernComponents.styleLabel(lblDni);
        panel.add(lblDni, gbc);

        txtDni = new JTextField();
        ModernComponents.styleTextField(txtDni);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        panel.add(txtDni, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblNombre = new JLabel("Nombre (*):");
        ModernComponents.styleLabel(lblNombre);
        panel.add(lblNombre, gbc);

        txtNombre = new JTextField();
        ModernComponents.styleTextField(txtNombre);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        panel.add(txtNombre, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblEmail = new JLabel("Email:");
        ModernComponents.styleLabel(lblEmail);
        panel.add(lblEmail, gbc);

        txtEmail = new JTextField();
        ModernComponents.styleTextField(txtEmail);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        panel.add(txtEmail, gbc);

        // Teléfono
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblTel = new JLabel("Teléfono:");
        ModernComponents.styleLabel(lblTel);
        panel.add(lblTel, gbc);

        txtTelefono = new JTextField();
        ModernComponents.styleTextField(txtTelefono);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        panel.add(txtTelefono, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        JLabel lblEstado = new JLabel("Estado:");
        ModernComponents.styleLabel(lblEstado);
        panel.add(lblEstado, gbc);

        chkStatus = new JCheckBox("Activo", true);
        chkStatus.setOpaque(false);
        chkStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        panel.add(chkStatus, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnGuardar = new ModernComponents.ModernButton("Guardar");
        btnGuardar.setPreferredSize(new Dimension(100, 34));

        btnCancelar = new ModernComponents.ModernButton("Cancelar", new Color(180, 180, 180), new Color(150, 150, 150));
        btnCancelar.setPreferredSize(new Dimension(100, 34));

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 8, 0, 8);
        panel.add(panelBotones, gbc);

        add(panel);

        configurarEventos();
    }

    /**
     * Carga la información en los controles si está en modo edición.
     */
    private void cargarCampos() {
        txtDni.setText(clienteOriginal.getDni());
        txtNombre.setText(clienteOriginal.getNombre());
        txtEmail.setText(clienteOriginal.getEmail() != null ? clienteOriginal.getEmail() : "");
        txtTelefono.setText(clienteOriginal.getTelefono() != null ? clienteOriginal.getTelefono() : "");
        chkStatus.setSelected(clienteOriginal.isStatus());
    }

    /**
     * Define los listeners.
     */
    private void configurarEventos() {
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar();
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
     * Valida los datos y guarda al cliente.
     */
    private void guardar() {
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        boolean status = chkStatus.isSelected();

        if (dni.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Los campos DNI y Nombre son obligatorios.",
                    "Campos requeridos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setDni(dni);
        cliente.setNombre(nombre);
        cliente.setEmail(email.isEmpty() ? null : email);
        cliente.setTelefono(telefono.isEmpty() ? null : telefono);
        cliente.setStatus(status);

        boolean exito;
        if (clienteOriginal == null) {
            exito = controller.registrarCliente(cliente);
        } else {
            cliente.setId(clienteOriginal.getId());
            exito = controller.modificarCliente(cliente);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "Cliente guardado de forma exitosa.",
                    "Guardado Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            parentForm.cargarDatos();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al guardar el cliente en la base de datos.",
                    "Error al Guardar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
