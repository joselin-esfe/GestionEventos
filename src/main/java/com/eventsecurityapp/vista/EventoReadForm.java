package com.eventsecurityapp.vista;

import com.eventsecurityapp.modelo.Evento;
import com.eventsecurityapp.persistencia.EventoDAO;

import javax.swing.*;
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
 * Desarrollado utilizando componentes estándar de Java Swing.
 */
public class EventoReadForm extends JFrame {

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
     * Constructor del formulario.
     */
    public EventoReadForm() {
        super("Administración de Eventos - EventSecurityApp");
        this.eventoDAO = new EventoDAO();
        inicializarComponentes();
        cargarDatos();
    }

    /**
     * Inicializa los componentes de la interfaz de usuario.
     */
    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);

        // Panel superior para la búsqueda
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        panelSuperior.add(new JLabel("Buscar por nombre:"));
        txtBuscar = new JTextField(30);
        panelSuperior.add(txtBuscar);

        // Tabla de eventos
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción", "Fecha/Hora", "Lugar", "Cupos Totales", "Cupos Disp.", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Celdas no editables directamente
            }
        };

        tblEventos = new JTable(tableModel);
        tblEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ocultar columna ID (columna 0) de la vista pero mantenerla en el modelo
        tblEventos.getColumnModel().getColumn(0).setMinWidth(0);
        tblEventos.getColumnModel().getColumn(0).setMaxWidth(0);
        tblEventos.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(tblEventos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel lateral/inferior para los botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);

        // Distribución en el contenedor
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
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
            public void insertUpdate(DocumentEvent e) {
                realizarBusqueda();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                realizarBusqueda();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                realizarBusqueda();
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

        // Botón Eliminar (Inactivar)
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

        // Botón Cerrar
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
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
}
