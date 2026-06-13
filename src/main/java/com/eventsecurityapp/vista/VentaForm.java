package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.VentaController;
import com.eventsecurityapp.modelo.Cliente;
import com.eventsecurityapp.modelo.Evento;
import com.eventsecurityapp.modelo.Pago;
import com.eventsecurityapp.modelo.Venta;
import com.eventsecurityapp.utils.CBOption;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Vista para el registro y consulta de Ventas.
 * Permite seleccionar cliente, evento, registrar la venta y el pago,
 * y consultar ventas previas.
 */
public class VentaForm extends JFrame {

    private JComboBox<CBOption> cbClientes;
    private JComboBox<CBOption> cbEventos;
    private JTextField txtCantidad;
    private JTextField txtPrecio;
    private JTextField txtTotal;
    private JComboBox<String> cbMetodoPago;
    private JButton btnCalcular;
    private JButton btnRegistrar;

    private JTable tblVentas;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;

    private VentaController controller;

    public VentaForm() {
        super("Módulo de Ventas de Entradas - EventSecurityApp");
        this.controller = new VentaController();
        inicializarComponentes();
        cargarCombos();
        cargarDatosTabla();
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Izquierdo: Formulario de Registro
        JPanel panelRegistro = new JPanel(new GridBagLayout());
        panelRegistro.setBorder(BorderFactory.createTitledBorder("Registrar Nueva Venta"));
        panelRegistro.setPreferredSize(new Dimension(350, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cliente
        gbc.gridx = 0; gbc.gridy = 0;
        panelRegistro.add(new JLabel("Cliente:"), gbc);
        cbClientes = new JComboBox<>();
        gbc.gridx = 1;
        panelRegistro.add(cbClientes, gbc);

        // Evento
        gbc.gridx = 0; gbc.gridy = 1;
        panelRegistro.add(new JLabel("Evento:"), gbc);
        cbEventos = new JComboBox<>();
        gbc.gridx = 1;
        panelRegistro.add(cbEventos, gbc);

        // Cantidad
        gbc.gridx = 0; gbc.gridy = 2;
        panelRegistro.add(new JLabel("Cantidad Entradas:"), gbc);
        txtCantidad = new JTextField();
        gbc.gridx = 1;
        panelRegistro.add(txtCantidad, gbc);

        // Precio Unitario
        gbc.gridx = 0; gbc.gridy = 3;
        panelRegistro.add(new JLabel("Precio Unitario:"), gbc);
        txtPrecio = new JTextField();
        gbc.gridx = 1;
        panelRegistro.add(txtPrecio, gbc);

        // Calcular Total Button
        btnCalcular = new JButton("Calcular Total");
        gbc.gridx = 1; gbc.gridy = 4;
        panelRegistro.add(btnCalcular, gbc);

        // Total
        gbc.gridx = 0; gbc.gridy = 5;
        panelRegistro.add(new JLabel("Total:"), gbc);
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtTotal.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        panelRegistro.add(txtTotal, gbc);

        // Método Pago
        gbc.gridx = 0; gbc.gridy = 6;
        panelRegistro.add(new JLabel("Método de Pago:"), gbc);
        cbMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        gbc.gridx = 1;
        panelRegistro.add(cbMetodoPago, gbc);

        // Registrar Button
        btnRegistrar = new JButton("Registrar Venta");
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panelRegistro.add(btnRegistrar, gbc);

        add(panelRegistro, BorderLayout.WEST);

        // Panel Derecho: Consulta y Búsqueda
        JPanel panelConsulta = new JPanel(new BorderLayout());
        
        // Buscador superior
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        panelBusqueda.add(new JLabel("Buscar (Cliente/Evento):"));
        txtBuscar = new JTextField(30);
        panelBusqueda.add(txtBuscar);

        panelConsulta.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        tableModel = new DefaultTableModel(
                new Object[]{"ID Venta", "Fecha", "ID Cliente", "ID Evento", "Cantidad", "Total", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblVentas = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblVentas);
        panelConsulta.add(scrollPane, BorderLayout.CENTER);

        add(panelConsulta, BorderLayout.CENTER);

        configurarEventos();
    }

    private void cargarCombos() {
        // Cargar clientes
        cbClientes.removeAllItems();
        List<Cliente> clientes = controller.listarClientes();
        for (Cliente c : clientes) {
            if (c.isStatus()) {
                cbClientes.addItem(new CBOption(c.getId(), c.getNombre() + " (" + c.getDni() + ")"));
            }
        }

        // Cargar eventos
        cbEventos.removeAllItems();
        List<Evento> eventos = controller.listarEventos();
        for (Evento e : eventos) {
            if (e.isStatus() && e.getCuposDisponibles() > 0) {
                cbEventos.addItem(new CBOption(e.getId(), e.getNombre() + " (Cupos: " + e.getCuposDisponibles() + ")"));
            }
        }
    }

    private void cargarDatosTabla() {
        tableModel.setRowCount(0);
        List<Venta> ventas = controller.listarVentas();
        for (Venta v : ventas) {
            agregarFila(v);
        }
    }

    private void agregarFila(Venta v) {
        tableModel.addRow(new Object[]{
                v.getId(),
                v.getFecha(),
                v.getClienteId(),
                v.getEventoId(),
                v.getCantidad(),
                v.getTotal(),
                v.isStatus() ? "Activa" : "Cancelada"
        });
    }

    private void configurarEventos() {
        // Calcular Total
        btnCalcular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calcularTotal();
            }
        });

        // Registrar Venta
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarVenta();
            }
        });

        // Búsqueda interactiva
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
    }

    private void calcularTotal() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            BigDecimal total = precio.multiply(new BigDecimal(cantidad));
            txtTotal.setText(total.toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos para Cantidad y Precio.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarVenta() {
        if (cbClientes.getSelectedItem() == null || cbEventos.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente y un evento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CBOption clienteOpt = (CBOption) cbClientes.getSelectedItem();
        CBOption eventoOpt = (CBOption) cbEventos.getSelectedItem();

        int cantidad;
        BigDecimal total;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            // Forzar calcular total si no está calculado o si cambió
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            total = precio.multiply(new BigDecimal(cantidad));
            txtTotal.setText(total.toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos para Cantidad y Precio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad de entradas debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear objeto Venta
        Venta venta = new Venta();
        venta.setClienteId(clienteOpt.getId());
        venta.setEventoId(eventoOpt.getId());
        venta.setCantidad(cantidad);
        venta.setTotal(total);
        venta.setFecha(LocalDateTime.now());
        venta.setStatus(true);

        // Crear objeto Pago
        Pago pago = new Pago();
        pago.setMetodoPago(cbMetodoPago.getSelectedItem().toString());
        pago.setMonto(total);
        pago.setFecha(LocalDateTime.now());
        // El venta_id será asignado dentro de la transacción

        // Procesar Transacción
        boolean exito = controller.registrarVenta(venta, pago);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Venta registrada exitosamente. Pago procesado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarCombos(); // Recargar para actualizar cupos disponibles
            cargarDatosTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta. Verifique que hayan cupos suficientes o inténtelo de nuevo. Los cambios han sido revertidos.", "Error en la transacción", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        cbClientes.setSelectedIndex(-1);
        cbEventos.setSelectedIndex(-1);
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtTotal.setText("");
        cbMetodoPago.setSelectedIndex(0);
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        tableModel.setRowCount(0);
        List<Venta> ventas = controller.buscarVentas(texto);
        for (Venta v : ventas) {
            agregarFila(v);
        }
    }
}
