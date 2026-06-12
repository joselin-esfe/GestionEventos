package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.ClienteController;
import com.eventsecurityapp.modelo.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diálogo modal para registrar y modificar clientes en EventSecurityApp.
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
    private Cliente clienteOriginal; // Null para inserción, contiene datos para edición
    private ClienteController controller;

    /**
     * Constructor del formulario.
     *
     * @param parentForm Ventana de lectura de origen.
     * @param cliente    Cliente a editar o null para crear uno nuevo.
     */
    public ClienteWriteForm(ClienteReadForm parentForm, Cliente cliente) {
        super(parentForm, cliente == null ? "Registrar Cliente" : "Modificar Cliente", true);
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
        setSize(400, 320);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // DNI
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("DNI (*):"), gbc);
        txtDni = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        panel.add(txtDni, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Nombre (*):"), gbc);
        txtNombre = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        panel.add(txtNombre, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        panel.add(txtEmail, gbc);

        // Teléfono
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        panel.add(new JLabel("Teléfono:"), gbc);
        txtTelefono = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        panel.add(txtTelefono, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        panel.add(new JLabel("Estado:"), gbc);
        chkStatus = new JCheckBox("Activo", true);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        panel.add(chkStatus, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weightx = 1.0;
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
        txtEmail.setText(clienteOriginal.getEmail());
        txtTelefono.setText(clienteOriginal.getTelefono());
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

        // 1. Validar campos obligatorios
        if (dni.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Los campos DNI y Nombre son obligatorios.",
                    "Campos requeridos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Construir objeto
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

            parentForm.cargarDatos(); // Refrescar listado
            dispose(); // Cerrar modal
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al guardar el cliente en la base de datos.",
                    "Error al Guardar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
