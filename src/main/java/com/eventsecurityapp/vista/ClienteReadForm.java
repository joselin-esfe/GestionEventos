package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.ClienteController;
import com.eventsecurityapp.modelo.Cliente;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Vista para la consulta y búsqueda de clientes.
 * Se conecta a la persistencia mediante ClienteController.
 */
public class ClienteReadForm extends JFrame {

    private JTable tblClientes;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    private ClienteController controller;

    /**
     * Constructor de la ventana.
     */
    public ClienteReadForm() {
        super("Administración de Clientes - EventSecurityApp");
        this.controller = new ClienteController();
        inicializarComponentes();
        cargarDatos();
    }

    /**
     * Inicializa componentes Swing.
     */
    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 450);
        setLocationRelativeTo(null);

        // Panel de búsqueda superior
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        panelBusqueda.add(new JLabel("Buscar por nombre:"));
        txtBuscar = new JTextField(30);
        panelBusqueda.add(txtBuscar);

        // Tabla de datos
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "DNI", "Nombre", "Email", "Teléfono", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblClientes = new JTable(tableModel);
        tblClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ocultar ID
        tblClientes.getColumnModel().getColumn(0).setMinWidth(0);
        tblClientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tblClientes.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(tblClientes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de botones inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);

        setLayout(new BorderLayout());
        add(panelBusqueda, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        configurarEventos();
    }

    /**
     * Define los manejadores de eventos.
     */
    private void configurarEventos() {
        // Búsqueda interactiva
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        // Botón Nuevo
        btnNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormularioEscritura(null);
            }
        });

        // Botón Editar
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblClientes.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ClienteReadForm.this,
                            "Seleccione un cliente para editar.",
                            "Atención",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Cliente cliente = obtenerClienteDeFila(selectedRow);
                abrirFormularioEscritura(cliente);
            }
        });

        // Botón Eliminar (Inactivar lógicamente)
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblClientes.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ClienteReadForm.this,
                            "Seleccione el cliente que desea inactivar.",
                            "Atención",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String nombre = (String) tableModel.getValueAt(selectedRow, 2);

                int confirm = JOptionPane.showConfirmDialog(ClienteReadForm.this,
                        "¿Seguro que desea inactivar al cliente: " + nombre + "?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = controller.inactivarCliente(id);
                    if (success) {
                        JOptionPane.showMessageDialog(ClienteReadForm.this,
                                "El cliente se inactivo con éxito.",
                                "Inactivación exitosa",
                                JOptionPane.INFORMATION_MESSAGE);
                        cargarDatos();
                    } else {
                        JOptionPane.showMessageDialog(ClienteReadForm.this,
                                "Ocurrió un error al desactivar al cliente.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Botón Cerrar
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * Carga todos los registros de clientes en el JTable.
     */
    public void cargarDatos() {
        tableModel.setRowCount(0);
        List<Cliente> clientes = controller.obtenerClientes();
        for (Cliente c : clientes) {
            agregarFila(c);
        }
    }

    /**
     * Filtra los clientes según el texto del buscador.
     */
    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        tableModel.setRowCount(0);
        List<Cliente> clientes = controller.buscarClientes(texto);
        for (Cliente c : clientes) {
            agregarFila(c);
        }
    }

    /**
     * Mapea un cliente y lo ingresa a la tabla.
     */
    private void agregarFila(Cliente c) {
        tableModel.addRow(new Object[]{
                c.getId(),
                c.getDni(),
                c.getNombre(),
                c.getEmail(),
                c.getTelefono(),
                c.isStatus() ? "Activo" : "Inactivo"
        });
    }

    /**
     * Reconstruye el objeto Cliente de la fila seleccionada.
     */
    private Cliente obtenerClienteDeFila(int row) {
        Cliente c = new Cliente();
        c.setId((int) tableModel.getValueAt(row, 0));
        c.setDni((String) tableModel.getValueAt(row, 1));
        c.setNombre((String) tableModel.getValueAt(row, 2));
        c.setEmail((String) tableModel.getValueAt(row, 3));
        c.setTelefono((String) tableModel.getValueAt(row, 4));
        c.setStatus("Activo".equals(tableModel.getValueAt(row, 5)));
        return c;
    }

    /**
     * Llama al formulario de escritura.
     */
    private void abrirFormularioEscritura(Cliente cliente) {
        ClienteWriteForm writeForm = new ClienteWriteForm(this, cliente);
        writeForm.setVisible(true);
    }
}
