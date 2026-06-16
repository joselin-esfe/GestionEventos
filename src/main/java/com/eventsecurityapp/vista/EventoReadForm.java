package com.eventsecurityapp.vista;

import com.eventsecurityapp.modelo.Evento;
import com.eventsecurityapp.persistencia.EventoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formulario para visualizar, buscar y gestionar la lista de eventos.
 * Rediseñado como un JPanel para integrarse en el CardLayout del Dashboard.
 */
public class EventoReadForm extends JPanel {

    private JTable tblEventos;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    private EventoDAO eventoDAO;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructor del panel de consulta de eventos.
     */
    public EventoReadForm() {
        this.eventoDAO = new EventoDAO();
        inicializarComponentes();
        cargarDatos();
    }

    /**
     * Inicializa los componentes de la interfaz de usuario con diseño premium.
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ModernComponents.COLOR_BACKGROUND);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- Encabezado del Módulo ---
        JPanel panelTitle = new JPanel(new BorderLayout());
        panelTitle.setOpaque(false);

        JLabel lblTitle = new JLabel("Gestión de Eventos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ModernComponents.COLOR_PRIMARY);
        panelTitle.add(lblTitle, BorderLayout.WEST);

        // --- Panel Superior (Filtro de Búsqueda) ---
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

        // --- JTable y Scroll Pane ---
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción", "Fecha/Hora", "Lugar", "Cupos Totales", "Cupos Disp.", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblEventos = new JTable(tableModel);
        tblEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernComponents.styleTable(tblEventos);

        // Ocultar la columna ID (pero mantener el valor)
        tblEventos.getColumnModel().getColumn(0).setMinWidth(0);
        tblEventos.getColumnModel().getColumn(0).setMaxWidth(0);
        tblEventos.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(tblEventos);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235)));

        // --- Panel de Acciones Inferior ---
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

        // Contenedor Norte: Título + Buscador
        JPanel panelNorte = new JPanel(new BorderLayout(10, 10));
        panelNorte.setOpaque(false);
        panelNorte.add(panelTitle, BorderLayout.NORTH);
        panelNorte.add(panelBusqueda, BorderLayout.SOUTH);

        // Armar el panel principal
        add(panelNorte, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        configurarEventos();
    }

    /**
     * Configura los oyentes de eventos.
     */
    private void configurarEventos() {
        // Buscar en tiempo real
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { realizarBusqueda(); }
            @Override
            public void removeUpdate(DocumentEvent e) { realizarBusqueda(); }
            @Override
            public void changedUpdate(DocumentEvent e) { realizarBusqueda(); }
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
                int selectedRow = tblEventos.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(EventoReadForm.this,
                            "Por favor, seleccione un evento de la tabla para editar.",
                            "Atención",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Evento evento = obtenerEventoDeLaFila(selectedRow);
                abrirFormularioEscritura(evento);
            }
        });

        // Botón Eliminar
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblEventos.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(EventoReadForm.this,
                            "Seleccione un evento de la tabla para inactivar.",
                            "Atención",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String nombre = (String) tableModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(EventoReadForm.this,
                        "¿Está seguro de que desea inactivar el evento: " + nombre + "?",
                        "Confirmar Inactivación",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = eventoDAO.inactivar(id);
                    if (success) {
                        JOptionPane.showMessageDialog(EventoReadForm.this,
                                "El evento ha sido inactivado con éxito.",
                                "Inactivación Exitosa",
                                JOptionPane.INFORMATION_MESSAGE);
                        cargarDatos();
                    } else {
                        JOptionPane.showMessageDialog(EventoReadForm.this,
                                "Ocurrió un error al inactivar el evento.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Botón Cerrar (vuelve a la pestaña Inicio del CardLayout del Dashboard)
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
     * Llena el JTable con los datos de todos los eventos.
     */
    public void cargarDatos() {
        tableModel.setRowCount(0);
        List<Evento> eventos = eventoDAO.listar();
        for (Evento e : eventos) {
            agregarFila(e);
        }
    }

    /**
     * Realiza la búsqueda de eventos filtrada por el texto de búsqueda.
     */
    private void realizarBusqueda() {
        String texto = txtBuscar.getText().trim();
        tableModel.setRowCount(0);
        List<Evento> eventos = eventoDAO.buscarPorNombre(texto);
        for (Evento e : eventos) {
            agregarFila(e);
        }
    }

    /**
     * Añade un evento al modelo de la tabla.
     */
    private void agregarFila(Evento e) {
        String fechaStr = e.getFecha() != null ? e.getFecha().format(DATE_FORMATTER) : "";
        tableModel.addRow(new Object[]{
                e.getId(),
                e.getNombre(),
                e.getDescripcion(),
                fechaStr,
                e.getLugar(),
                e.getCuposTotales(),
                e.getCuposDisponibles(),
                e.isStatus() ? "Activo" : "Inactivo"
        });
    }

    /**
     * Reconstruye el objeto Evento de la fila seleccionada en el JTable.
     */
    private Evento obtenerEventoDeLaFila(int row) {
        Evento e = new Evento();
        e.setId((int) tableModel.getValueAt(row, 0));
        e.setNombre((String) tableModel.getValueAt(row, 1));
        e.setDescripcion((String) tableModel.getValueAt(row, 2));

        String fechaStr = (String) tableModel.getValueAt(row, 3);
        if (!fechaStr.isEmpty()) {
            e.setFecha(java.time.LocalDateTime.parse(fechaStr, DATE_FORMATTER));
        }

        e.setLugar((String) tableModel.getValueAt(row, 4));
        e.setCuposTotales((int) tableModel.getValueAt(row, 5));
        e.setCuposDisponibles((int) tableModel.getValueAt(row, 6));
        e.setStatus("Activo".equals(tableModel.getValueAt(row, 7)));
        return e;
    }

    /**
     * Abre el formulario de escritura (creación/edición) de eventos.
     */
    private void abrirFormularioEscritura(Evento evento) {
        EventoWriteForm writeForm = new EventoWriteForm(this, evento);
        writeForm.setVisible(true);
    }

    /**
     * Permite ejecutar y visualizar este panel en IntelliJ de manera independiente.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prueba EventoReadForm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new EventoReadForm());
            frame.setSize(850, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
