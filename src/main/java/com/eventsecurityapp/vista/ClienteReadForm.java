package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.ClienteController;
import com.eventsecurityapp.modelo.Cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Vista para la consulta y búsqueda de clientes.
 * Rediseñado como un JPanel para integrarse en el CardLayout del Dashboard.
 */
public class ClienteReadForm extends JPanel {

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
        this.controller = new ClienteController();
        inicializarComponentes();
        cargarDatos();
    }

    /**
     * Inicializa componentes Swing con diseño premium.
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ModernComponents.COLOR_BACKGROUND);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- Encabezado del Módulo ---
        JPanel panelTitle = new JPanel(new BorderLayout());
        panelTitle.setOpaque(false);

        JLabel lblTitle = new JLabel("Gestión de Clientes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ModernComponents.COLOR_PRIMARY);
        panelTitle.add(lblTitle, BorderLayout.WEST);

        // --- Panel de búsqueda superior ---
        JPanel panelBusqueda = new JPanel(new GridBagLayout());
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);

        JLabel lblBuscar = new JLabel("Buscar por nombre:");
        ModernComponents.styleLabel(lblBuscar);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panelBusqueda.add(lblBuscar, gbc);

        txtBuscar = new JTextField();
        ModernComponents.styleTextField(txtBuscar);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        panelBusqueda.add(txtBuscar, gbc);

        // --- Tabla de datos ---
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
        ModernComponents.styleTable(tblClientes);

        // Ocultar ID
        tblClientes.getColumnModel().getColumn(0).setMinWidth(0);
        tblClientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tblClientes.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(tblClientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235)));

        // --- Panel de botones inferior ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnNuevo = new ModernComponents.ModernButton("Nuevo");
        btnNuevo.setPreferredSize(new Dimension(100, 36));

        btnEditar = new ModernComponents.ModernButton("Editar", ModernComponents.COLOR_SECONDARY, ModernComponents.COLOR_PRIMARY);
        btnEditar.setPreferredSize(new Dimension(100, 36));

        btnEliminar = new ModernComponents.ModernButton("Eliminar", new Color(180, 50, 50), new Color(150, 30, 30));
        btnEliminar.setPreferredSize(new Dimension(100, 36));

        btnCerrar = new ModernComponents.ModernButton("Volver", new Color(120, 120, 130), new Color(100, 100, 110));
        btnCerrar.setPreferredSize(new Dimension(100, 36));

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);

        // Panel agrupador norte
        JPanel panelNorte = new JPanel(new BorderLayout(10, 10));
        panelNorte.setOpaque(false);
        panelNorte.add(panelTitle, BorderLayout.NORTH);
        panelNorte.add(panelBusqueda, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(panelNorte, BorderLayout.NORTH);
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
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }
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

        // Botón Cerrar (Volver)
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parent = getParent();
                if (parent != null && parent.getLayout() instanceof CardLayout) {
                    CardLayout cl = (CardLayout) parent.getLayout();
                    cl.show(parent, "Inicio");
                }
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
                c.getEmail() != null ? c.getEmail() : "",
                c.getTelefono() != null ? c.getTelefono() : "",
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

    /**
     * Permite visualizar e iniciar este módulo individualmente en IntelliJ.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prueba ClienteReadForm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new ClienteReadForm());
            frame.setSize(800, 450);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
