package com.eventsecurityapp.vista;

import com.eventsecurityapp.modelo.Evento;
import com.eventsecurityapp.persistencia.EventoDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Formulario para la creación o modificación de un Evento.
 * Se presenta como un diálogo modal.
 */
public class EventoWriteForm extends JDialog {

    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtFecha;
    private JTextField txtLugar;
    private JTextField txtCuposTotales;
    private JTextField txtCuposDisponibles;
    private JCheckBox chkStatus;

    private JButton btnGuardar;
    private JButton btnCancelar;

    private EventoReadForm parentForm;
    private Evento eventoOriginal; // Null en caso de creación, con datos en edición
    private EventoDAO eventoDAO;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructor del diálogo de escritura.
     *
     * @param parentForm Ventana de lectura padre.
     * @param evento     Objeto Evento a editar o null para crear uno nuevo.
     */
    public EventoWriteForm(EventoReadForm parentForm, Evento evento) {
        super(parentForm, evento == null ? "Registrar Evento" : "Modificar Evento", true);
        this.parentForm = parentForm;
        this.eventoOriginal = evento;
        this.eventoDAO = new EventoDAO();
        inicializarComponentes();
        if (evento != null) {
            cargarCampos();
        }
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private void inicializarComponentes() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 380);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("Nombre (*):"), gbc);
        txtNombre = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        panel.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        panel.add(txtDescripcion, gbc);

        // Fecha
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Fecha (yyyy-MM-dd HH:mm) (*):"), gbc);
        txtFecha = new JTextField(20);
        txtFecha.setToolTipText("Ejemplo: 2026-12-31 23:59");
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        panel.add(txtFecha, gbc);

        // Lugar
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        panel.add(new JLabel("Lugar (*):"), gbc);
        txtLugar = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        panel.add(txtLugar, gbc);

        // Cupos Totales
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        panel.add(new JLabel("Cupos Totales (*):"), gbc);
        txtCuposTotales = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        panel.add(txtCuposTotales, gbc);

        // Cupos Disponibles
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        panel.add(new JLabel("Cupos Disponibles (*):"), gbc);
        txtCuposDisponibles = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.7;
        panel.add(txtCuposDisponibles, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        panel.add(new JLabel("Estado:"), gbc);
        chkStatus = new JCheckBox("Activo", true);
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 0.7;
        panel.add(chkStatus, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(panelBotones, gbc);

        add(panel);

        configurarEventos();
    }

    /**
     * Rellena los campos con la información del evento seleccionado.
     */
    private void cargarCampos() {
        txtNombre.setText(eventoOriginal.getNombre());
        txtDescripcion.setText(eventoOriginal.getDescripcion());
        txtFecha.setText(eventoOriginal.getFecha() != null ? eventoOriginal.getFecha().format(DATE_FORMATTER) : "");
        txtLugar.setText(eventoOriginal.getLugar());
        txtCuposTotales.setText(String.valueOf(eventoOriginal.getCuposTotales()));
        txtCuposDisponibles.setText(String.valueOf(eventoOriginal.getCuposDisponibles()));
        chkStatus.setSelected(eventoOriginal.isStatus());
    }

    /**
     * Define los oyentes de eventos.
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
     * Valida las entradas e inserta o actualiza el evento.
     */
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String fechaStr = txtFecha.getText().trim();
        String lugar = txtLugar.getText().trim();
        String cuposTotalesStr = txtCuposTotales.getText().trim();
        String cuposDisponiblesStr = txtCuposDisponibles.getText().trim();
        boolean status = chkStatus.isSelected();

        // 1. Validar que los campos obligatorios no estén vacíos
        if (nombre.isEmpty() || fechaStr.isEmpty() || lugar.isEmpty() || cuposTotalesStr.isEmpty() || cuposDisponiblesStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos marcados con (*) son obligatorios.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validar formato de Fecha
        LocalDateTime fecha;
        try {
            fecha = LocalDateTime.parse(fechaStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "La fecha no tiene el formato correcto (yyyy-MM-dd HH:mm).\nEjemplo: 2026-12-31 18:30",
                    "Formato de Fecha Incorrecto",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Validar números enteros en cupos
        int cuposTotales;
        int cuposDisponibles;
        try {
            cuposTotales = Integer.parseInt(cuposTotalesStr);
            cuposDisponibles = Integer.parseInt(cuposDisponiblesStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Los campos de cupos deben ser números enteros válidos.",
                    "Formato Numérico Incorrecto",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Crear o actualizar objeto Evento
        Evento evento = new Evento();
        evento.setNombre(nombre);
        evento.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        evento.setFecha(fecha);
        evento.setLugar(lugar);
        evento.setCuposTotales(cuposTotales);
        evento.setCuposDisponibles(cuposDisponibles);
        evento.setStatus(status);

        boolean exito;
        if (eventoOriginal == null) {
            // Modo inserción
            exito = eventoDAO.insertar(evento);
        } else {
            // Modo edición
            evento.setId(eventoOriginal.getId());
            exito = eventoDAO.actualizar(evento);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "El evento ha sido guardado exitosamente.",
                    "Guardado Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            parentForm.cargarDatos(); // Refrescar tabla en la vista de consulta
            dispose(); // Cerrar diálogo modal
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al intentar guardar el evento en la base de datos.",
                    "Error al Guardar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
