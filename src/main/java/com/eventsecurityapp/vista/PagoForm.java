package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.PagoController;
import com.eventsecurityapp.controlador.VentaController;
import com.eventsecurityapp.modelo.Pago;
import com.eventsecurityapp.modelo.Venta;
import com.eventsecurityapp.utils.CBOption;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Vista para la gestión de pagos.
 * Permite registrar pagos sueltos asociados a ventas y listar los pagos.
 */
public class PagoForm extends JFrame {

    private JComboBox<CBOption> cbVentas;
    private JComboBox<String> cbMetodoPago;
    private JTextField txtMonto;
    private JButton btnRegistrar;

    private JTable tblPagos;
    private DefaultTableModel tableModel;

    private PagoController pagoController;
    private VentaController ventaController;

    public PagoForm() {
        super("Gestión de Pagos - EventSecurityApp");
        this.pagoController = new PagoController();
        this.ventaController = new VentaController();

        inicializarComponentes();
        cargarVentas();
        cargarDatosTabla();
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Izquierdo: Formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Registrar Pago Adicional"));
        panelForm.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Venta
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Venta Asociada:"), gbc);
        cbVentas = new JComboBox<>();
        gbc.gridx = 1;
        panelForm.add(cbVentas, gbc);

        // Método Pago
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Método de Pago:"), gbc);
        cbMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        gbc.gridx = 1;
        panelForm.add(cbMetodoPago, gbc);

        // Monto
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Monto:"), gbc);
        txtMonto = new JTextField();
        gbc.gridx = 1;
        panelForm.add(txtMonto, gbc);

        // Registrar
        btnRegistrar = new JButton("Registrar Pago");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(btnRegistrar, gbc);

        add(panelForm, BorderLayout.WEST);

        // Panel Derecho: Tabla
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Pagos Registrados"));

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "ID Venta", "Método Pago", "Monto", "Fecha"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPagos = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblPagos);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        add(panelTabla, BorderLayout.CENTER);

        configurarEventos();
    }

    private void cargarVentas() {
        cbVentas.removeAllItems();
        List<Venta> ventas = ventaController.listarVentas();
        for (Venta v : ventas) {
            if (v.isStatus()) {
                cbVentas.addItem(new CBOption(v.getId(), "Venta #" + v.getId() + " - Total: $" + v.getTotal()));
            }
        }
    }

    private void cargarDatosTabla() {
        tableModel.setRowCount(0);
        List<Pago> pagos = pagoController.listarPagos();
        for (Pago p : pagos) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getVentaId(),
                    p.getMetodoPago(),
                    p.getMonto(),
                    p.getFecha()
            });
        }
    }

    private void configurarEventos() {
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarPago();
            }
        });
    }

    private void registrarPago() {
        if (cbVentas.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una venta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CBOption ventaOpt = (CBOption) cbVentas.getSelectedItem();
        BigDecimal monto;

        try {
            monto = new BigDecimal(txtMonto.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pago pago = new Pago();
        pago.setVentaId(ventaOpt.getId());
        pago.setMetodoPago(cbMetodoPago.getSelectedItem().toString());
        pago.setMonto(monto);
        pago.setFecha(LocalDateTime.now());

        boolean exito = pagoController.registrarPago(pago);
        if (exito) {
            JOptionPane.showMessageDialog(this, "Pago registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtMonto.setText("");
            cargarDatosTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al registrar el pago.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
