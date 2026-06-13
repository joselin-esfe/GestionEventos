package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.AsistenciaController;
import com.eventsecurityapp.controlador.VentaController;
import com.eventsecurityapp.modelo.Asistencia;
import com.eventsecurityapp.modelo.Venta;
import com.eventsecurityapp.utils.CBOption;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista para el control de asistencia a los eventos.
 * Permite buscar una venta y registrar el ingreso del asistente,
 * previniendo ingresos duplicados.
 */
public class AsistenciaForm extends JFrame {

    private JTextField txtBuscarVenta;
    private JButton btnBuscarVenta;
    private JComboBox<CBOption> cbVentas;
    private JButton btnRegistrarAsistencia;

    private JTable tblAsistencias;
    private DefaultTableModel tableModel;

    private AsistenciaController asistenciaController;
    private VentaController ventaController;

    public AsistenciaForm() {
        super("Control de Asistencia - EventSecurityApp");
        this.asistenciaController = new AsistenciaController();
        this.ventaController = new VentaController();

        inicializarComponentes();
        cargarTodasLasVentas();
        cargarDatosTabla();
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Superior: Formulario de Asistencia
        JPanel panelRegistro = new JPanel(new GridBagLayout());
        panelRegistro.setBorder(BorderFactory.createTitledBorder("Registrar Ingreso"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Búsqueda de venta
        gbc.gridx = 0; gbc.gridy = 0;
        panelRegistro.add(new JLabel("Buscar Venta:"), gbc);
        txtBuscarVenta = new JTextField(20);
        gbc.gridx = 1;
        panelRegistro.add(txtBuscarVenta, gbc);

        btnBuscarVenta = new JButton("Buscar");
        gbc.gridx = 2;
        panelRegistro.add(btnBuscarVenta, gbc);

        // Resultados de Venta
        gbc.gridx = 0; gbc.gridy = 1;
        panelRegistro.add(new JLabel("Seleccionar Venta:"), gbc);
        cbVentas = new JComboBox<>();
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelRegistro.add(cbVentas, gbc);

        // Registrar Asistencia
        btnRegistrarAsistencia = new JButton("Registrar Asistencia");
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panelRegistro.add(btnRegistrarAsistencia, gbc);

        add(panelRegistro, BorderLayout.NORTH);

        // Panel Central: Tabla
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Asistencias Registradas"));

        tableModel = new DefaultTableModel(
                new Object[]{"ID Asistencia", "ID Venta", "Fecha y Hora de Ingreso"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAsistencias = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblAsistencias);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        add(panelTabla, BorderLayout.CENTER);

        configurarEventos();
    }

    private void cargarTodasLasVentas() {
        cbVentas.removeAllItems();
        List<Venta> ventas = ventaController.listarVentas();
        for (Venta v : ventas) {
            if (v.isStatus()) {
                cbVentas.addItem(new CBOption(v.getId(), "Venta #" + v.getId() + " - Entradas: " + v.getCantidad()));
            }
        }
    }

    private void cargarDatosTabla() {
        tableModel.setRowCount(0);
        List<Asistencia> asistencias = asistenciaController.listarAsistencias();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Asistencia a : asistencias) {
            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getVentaId(),
                    a.getFechaIngreso().format(formatter)
            });
        }
    }

    private void configurarEventos() {
        btnBuscarVenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarVenta();
            }
        });

        btnRegistrarAsistencia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarAsistencia();
            }
        });
    }

    private void buscarVenta() {
        String texto = txtBuscarVenta.getText().trim();
        cbVentas.removeAllItems();

        if (texto.isEmpty()) {
            cargarTodasLasVentas();
        } else {
            List<Venta> ventas = ventaController.buscarVentas(texto);
            for (Venta v : ventas) {
                if (v.isStatus()) {
                    cbVentas.addItem(new CBOption(v.getId(), "Venta #" + v.getId() + " - Entradas: " + v.getCantidad()));
                }
            }
            if (cbVentas.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron ventas con ese criterio.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void registrarAsistencia() {
        if (cbVentas.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para registrar asistencia.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CBOption ventaOpt = (CBOption) cbVentas.getSelectedItem();
        LocalDateTime fechaActual = LocalDateTime.now();

        Asistencia asistencia = new Asistencia();
        asistencia.setVentaId(ventaOpt.getId());
        asistencia.setFechaIngreso(fechaActual);

        boolean exito = asistenciaController.registrarAsistencia(asistencia);

        if (exito) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String mensaje = "Asistencia registrada exitosamente.\nFecha y Hora de Ingreso: " + fechaActual.format(formatter);
            JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatosTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la asistencia. Es posible que el ticket ya haya sido utilizado (Duplicado).", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
        }
    }
}
