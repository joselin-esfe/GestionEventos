package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.VentaController;
import com.eventsecurityapp.modelo.Cliente;
import com.eventsecurityapp.modelo.Evento;
import com.eventsecurityapp.modelo.Venta;
import com.eventsecurityapp.modelo.VentaDetalle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formulario principal para la gestión de Ventas de Entradas.
 * Rediseñado como un JPanel para integrarse en el CardLayout del Dashboard.
 */
public class VentaForm extends JPanel {

    // Componentes del Panel de Registro (Izquierdo)
    private JComboBox<Cliente> cmbClientes;
    private JComboBox<Evento> cmbEventos;
    private JTextField txtCantidad;
    private JTextField txtPrecioUnitario;
    private JTextField txtTotal;
    private JComboBox<String> cmbMetodosPago;
    private JCheckBox chkStatus;
    private JButton btnRegistrar;
    private JButton btnLimpiar;

    // Componentes del Panel de Consulta (Derecho)
    private JTable tblVentas;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JButton btnCerrar;

    // Negocio y Helpers
    private final VentaController ventaController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructor del formulario. Inicializa los componentes, carga los catálogos y llena la tabla.
     */
    public VentaForm() {
        this.ventaController = new VentaController();
        inicializarComponentes();
        cargarCatalogos();
        cargarDatosTabla();
    }

    /**
     * Inicializa y organiza los componentes visuales del módulo.
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ModernComponents.COLOR_BACKGROUND);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        Font fontEtiqueta = new Font("Segoe UI", Font.BOLD, 12);
        Font fontTitulo = new Font("Segoe UI", Font.BOLD, 16);

        // --- Encabezado del Módulo ---
        JPanel panelTitle = new JPanel(new BorderLayout());
        panelTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("Módulo de Ventas de Entradas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ModernComponents.COLOR_PRIMARY);
        panelTitle.add(lblTitle, BorderLayout.WEST);

        // --- PANEL DE REGISTRO (IZQUIERDO) ---
        JPanel panelRegistro = new JPanel(new GridBagLayout());
        panelRegistro.setBackground(Color.WHITE);
        panelRegistro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;

        // Título del Panel de Registro
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblRegTitle = new JLabel("Registrar Nueva Venta");
        lblRegTitle.setFont(fontTitulo);
        lblRegTitle.setForeground(ModernComponents.COLOR_PRIMARY);
        panelRegistro.add(lblRegTitle, gbc);

        // Cliente
        gbc.gridy = 1; gbc.insets = new Insets(10, 6, 2, 6);
        JLabel lblCliente = new JLabel("Cliente (*):");
        lblCliente.setFont(fontEtiqueta);
        lblCliente.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblCliente, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 6, 6, 6);
        cmbClientes = new JComboBox<>();
        cmbClientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbClientes.setPreferredSize(new Dimension(0, 30));
        cmbClientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    Cliente c = (Cliente) value;
                    setText(c.getNombre() + " (DNI: " + c.getDni() + ")");
                }
                return this;
            }
        });
        panelRegistro.add(cmbClientes, gbc);

        // Evento
        gbc.gridy = 3; gbc.insets = new Insets(6, 6, 2, 6);
        JLabel lblEvento = new JLabel("Evento (*):");
        lblEvento.setFont(fontEtiqueta);
        lblEvento.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblEvento, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 6, 6, 6);
        cmbEventos = new JComboBox<>();
        cmbEventos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbEventos.setPreferredSize(new Dimension(0, 30));
        cmbEventos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Evento) {
                    Evento e = (Evento) value;
                    setText(e.getNombre() + " (Cupos: " + e.getCuposDisponibles() + ")");
                }
                return this;
            }
        });
        panelRegistro.add(cmbEventos, gbc);

        // Cantidad y Precio (En la misma fila)
        gbc.gridwidth = 1; gbc.weightx = 0.5;

        gbc.gridx = 0; gbc.gridy = 5; gbc.insets = new Insets(6, 6, 2, 6);
        JLabel lblCantidad = new JLabel("Cantidad (*):");
        lblCantidad.setFont(fontEtiqueta);
        lblCantidad.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblCantidad, gbc);

        gbc.gridx = 1;
        JLabel lblPrecio = new JLabel("Precio Unitario ($) (*):");
        lblPrecio.setFont(fontEtiqueta);
        lblPrecio.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblPrecio, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.insets = new Insets(0, 6, 6, 6);
        txtCantidad = new JTextField("1");
        ModernComponents.styleTextField(txtCantidad);
        txtCantidad.setPreferredSize(new Dimension(0, 30));
        panelRegistro.add(txtCantidad, gbc);

        gbc.gridx = 1;
        txtPrecioUnitario = new JTextField("50.00");
        ModernComponents.styleTextField(txtPrecioUnitario);
        txtPrecioUnitario.setPreferredSize(new Dimension(0, 30));
        panelRegistro.add(txtPrecioUnitario, gbc);

        // Total
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.insets = new Insets(6, 6, 2, 6);
        JLabel lblTotal = new JLabel("Costo Total Calculado:");
        lblTotal.setFont(fontEtiqueta);
        lblTotal.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblTotal, gbc);

        gbc.gridy = 8; gbc.insets = new Insets(0, 6, 6, 6);
        txtTotal = new JTextField("50.00");
        txtTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTotal.setEditable(false);
        txtTotal.setBackground(new Color(240, 245, 250));
        txtTotal.setForeground(ModernComponents.COLOR_PRIMARY);
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        txtTotal.setPreferredSize(new Dimension(0, 32));
        panelRegistro.add(txtTotal, gbc);

        // Método de Pago
        gbc.gridy = 9; gbc.insets = new Insets(6, 6, 2, 6);
        JLabel lblMetodoPago = new JLabel("Método de Pago (*):");
        lblMetodoPago.setFont(fontEtiqueta);
        lblMetodoPago.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblMetodoPago, gbc);

        gbc.gridy = 10; gbc.insets = new Insets(0, 6, 10, 6);
        cmbMetodosPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        cmbMetodosPago.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbMetodosPago.setPreferredSize(new Dimension(0, 30));
        panelRegistro.add(cmbMetodosPago, gbc);

        // Estado
        gbc.gridy = 11; gbc.insets = new Insets(6, 6, 10, 6);
        chkStatus = new JCheckBox("Venta Activa (Validada)", true);
        chkStatus.setOpaque(false);
        chkStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelRegistro.add(chkStatus, gbc);

        // Espaciador vertical
        gbc.gridy = 12; gbc.weighty = 1.0;
        panelRegistro.add(Box.createVerticalGlue(), gbc);

        // Botones del formulario
        gbc.gridy = 13; gbc.weighty = 0.0; gbc.insets = new Insets(10, 6, 0, 6);
        JPanel panelBotonesReg = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotonesReg.setOpaque(false);

        btnRegistrar = new ModernComponents.ModernButton("Registrar");
        btnRegistrar.setPreferredSize(new Dimension(0, 36));

        btnLimpiar = new ModernComponents.ModernButton("Limpiar", new Color(180, 180, 180), new Color(150, 150, 150));
        btnLimpiar.setPreferredSize(new Dimension(0, 36));

        panelBotonesReg.add(btnRegistrar);
        panelBotonesReg.add(btnLimpiar);
        panelRegistro.add(panelBotonesReg, gbc);


        // --- PANEL DE CONSULTA (DERECHO) ---
        JPanel panelConsulta = new JPanel(new BorderLayout(15, 15));
        panelConsulta.setBackground(Color.WHITE);
        panelConsulta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Búsqueda interactiva
        JPanel panelBusqueda = new JPanel(new GridBagLayout());
        panelBusqueda.setOpaque(false);
        GridBagConstraints gbcBusq = new GridBagConstraints();
        gbcBusq.fill = GridBagConstraints.HORIZONTAL;
        gbcBusq.insets = new Insets(0, 5, 0, 5);

        JLabel lblBuscar = new JLabel("Buscar por Cliente o Evento:");
        ModernComponents.styleLabel(lblBuscar);
        gbcBusq.gridx = 0; gbcBusq.gridy = 0; gbcBusq.weightx = 0.0;
        panelBusqueda.add(lblBuscar, gbcBusq);

        txtBuscar = new JTextField();
        ModernComponents.styleTextField(txtBuscar);
        gbcBusq.gridx = 1; gbcBusq.gridy = 0; gbcBusq.weightx = 1.0;
        panelBusqueda.add(txtBuscar, gbcBusq);

        panelConsulta.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla de Ventas
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Fecha/Hora", "Cliente (DNI)", "Evento", "Cantidad", "Total ($)", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblVentas = new JTable(tableModel);
        tblVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernComponents.styleTable(tblVentas);

        tblVentas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblVentas.getColumnModel().getColumn(1).setPreferredWidth(130);
        tblVentas.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblVentas.getColumnModel().getColumn(3).setPreferredWidth(180);
        tblVentas.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblVentas.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblVentas.getColumnModel().getColumn(6).setPreferredWidth(80);

        JScrollPane scrollTable = new JScrollPane(tblVentas);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235)));
        panelConsulta.add(scrollTable, BorderLayout.CENTER);

        // Panel inferior para cerrar (Volver)
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelInferior.setOpaque(false);
        btnCerrar = new ModernComponents.ModernButton("Volver", new Color(120, 120, 130), new Color(100, 100, 110));
        btnCerrar.setPreferredSize(new Dimension(100, 36));
        panelInferior.add(btnCerrar);
        panelConsulta.add(panelInferior, BorderLayout.SOUTH);


        // --- Integrar los paneles izquierdo y derecho ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelRegistro, panelConsulta);
        splitPane.setDividerLocation(360);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setEnabled(false);

        // Armar el Layout
        add(panelTitle, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        configurarEventos();
    }

    /**
     * Carga las listas de Clientes y Eventos activos en los ComboBox.
     */
    private void cargarCatalogos() {
        cmbClientes.removeAllItems();
        cmbEventos.removeAllItems();

        List<Cliente> clientes = ventaController.obtenerClientesActivos();
        for (Cliente c : clientes) {
            cmbClientes.addItem(c);
        }

        List<Evento> eventos = ventaController.obtenerEventosActivos();
        for (Evento e : eventos) {
            cmbEventos.addItem(e);
        }
    }

    /**
     * Llena el JTable con la lista completa de ventas registradas en el sistema.
     */
    private void cargarDatosTabla() {
        List<VentaDetalle> ventas = ventaController.obtenerVentas();
        llenarTabla(ventas);
    }

    /**
     * Rellena el table model con los datos proporcionados.
     */
    private void llenarTabla(List<VentaDetalle> lista) {
        tableModel.setRowCount(0);
        for (VentaDetalle v : lista) {
            String fechaStr = v.getFecha() != null ? v.getFecha().format(DATE_FORMATTER) : "";
            tableModel.addRow(new Object[]{
                    v.getId(),
                    fechaStr,
                    v.getClienteNombre() + " (" + v.getClienteDni() + ")",
                    v.getEventoNombre(),
                    v.getCantidad(),
                    String.format("%.2f", v.getTotal()),
                    v.isStatus() ? "Activa" : "Cancelada"
            });
        }
    }

    /**
     * Configura los controladores de eventos para los campos interactivos.
     */
    private void configurarEventos() {
        // Cálculo de Total automático
        DocumentListener totalCalculator = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { calcularTotal(); }
            @Override
            public void removeUpdate(DocumentEvent e) { calcularTotal(); }
            @Override
            public void changedUpdate(DocumentEvent e) { calcularTotal(); }
        };
        txtCantidad.getDocument().addDocumentListener(totalCalculator);
        txtPrecioUnitario.getDocument().addDocumentListener(totalCalculator);

        // Búsqueda interactiva
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { realizarBusqueda(); }
            @Override
            public void removeUpdate(DocumentEvent e) { realizarBusqueda(); }
            @Override
            public void changedUpdate(DocumentEvent e) { realizarBusqueda(); }
        });

        // Botón Limpiar
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarFormulario();
            }
        });

        // Botón Registrar Venta
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarVenta();
            }
        });

        // Botón Cerrar (Volver al Inicio en el CardLayout del Dashboard)
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
     * Realiza el cálculo automático del precio total y lo muestra en el campo txtTotal.
     */
    private void calcularTotal() {
        String cantStr = txtCantidad.getText().trim();
        String precioStr = txtPrecioUnitario.getText().trim();

        if (cantStr.isEmpty() || precioStr.isEmpty()) {
            txtTotal.setText("0.00");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantStr);
            BigDecimal precio = new BigDecimal(precioStr);
            if (cantidad < 0 || precio.compareTo(BigDecimal.ZERO) < 0) {
                txtTotal.setText("0.00");
                return;
            }
            BigDecimal total = precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
            txtTotal.setText(total.toString());
        } catch (NumberFormatException e) {
            txtTotal.setText("0.00");
        }
    }

    /**
     * Filtra la JTable de ventas en tiempo real basado en el texto ingresado en el buscador.
     */
    private void realizarBusqueda() {
        String texto = txtBuscar.getText().trim();
        List<VentaDetalle> filtrado = ventaController.buscarVentas(texto);
        llenarTabla(filtrado);
    }

    /**
     * Procesa la solicitud de venta de entradas y la envía al controlador.
     */
    private void procesarVenta() {
        Cliente cliente = (Cliente) cmbClientes.getSelectedItem();
        Evento evento = (Evento) cmbEventos.getSelectedItem();
        String cantStr = txtCantidad.getText().trim();
        String precioStr = txtPrecioUnitario.getText().trim();
        String metodoPago = (String) cmbMetodosPago.getSelectedItem();
        boolean status = chkStatus.isSelected();

        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente activo.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un evento activo.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (metodoPago == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un método de pago.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad;
        BigDecimal precio;
        try {
            cantidad = Integer.parseInt(cantStr);
            precio = new BigDecimal(precioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad y el precio unitario deben ser números válidos.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad de boletos debe ser mayor a 0.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "El precio unitario debe ser mayor a 0.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cantidad > evento.getCuposDisponibles()) {
            JOptionPane.showMessageDialog(this, 
                    "No hay suficientes cupos disponibles para el evento.\n" +
                    "Cupos libres: " + evento.getCuposDisponibles() + "\n" +
                    "Cantidad solicitada: " + cantidad, 
                    "Cupos Insuficientes", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setClienteId(cliente.getId());
        venta.setEventoId(evento.getId());
        venta.setCantidad(cantidad);
        venta.setTotal(precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP));
        venta.setStatus(status);

        try {
            boolean exito = ventaController.registrarVenta(venta, metodoPago);
            if (exito) {
                JOptionPane.showMessageDialog(this, 
                        "¡Venta y pago automático registrados con éxito!\nCódigo de Venta: " + venta.getId(), 
                        "Registro Exitoso", 
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarCatalogos(); // Recargar combo para actualizar cupos
                cargarDatosTabla(); // Recargar tabla
            } else {
                JOptionPane.showMessageDialog(this, 
                        "No se pudo registrar la venta ni el pago.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Error al registrar la venta y su pago automático:\n" + ex.getMessage(), 
                    "Error de Transacción", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Limpia los campos del formulario de registro restableciendo los valores predeterminados.
     */
    private void limpiarFormulario() {
        if (cmbClientes.getItemCount() > 0) cmbClientes.setSelectedIndex(0);
        if (cmbEventos.getItemCount() > 0) cmbEventos.setSelectedIndex(0);
        if (cmbMetodosPago.getItemCount() > 0) cmbMetodosPago.setSelectedIndex(0);
        txtCantidad.setText("1");
        txtPrecioUnitario.setText("50.00");
        txtTotal.setText("50.00");
        chkStatus.setSelected(true);
    }

    /**
     * Permite visualizar e iniciar este panel individualmente en IntelliJ.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prueba VentaForm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new VentaForm());
            frame.setSize(1050, 620);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
