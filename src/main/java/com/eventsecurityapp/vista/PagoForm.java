package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.PagoController;
import com.eventsecurityapp.modelo.Pago;
import com.eventsecurityapp.modelo.PagoDetalle;
import com.eventsecurityapp.modelo.VentaDetalle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formulario Swing para la gestión de Pagos de Ventas.
 * Rediseñado como un JPanel para integrarse en el CardLayout del Dashboard.
 */
public class PagoForm extends JPanel {

    // Componentes del panel izquierdo (Registro)
    private JComboBox<VentaDetalle> cmbVentas;
    private JComboBox<String> cmbMetodos;
    private JTextField txtMonto;
    private JButton btnRegistrar;
    private JButton btnLimpiar;

    // Componentes del panel derecho (Consulta)
    private JTable tblPagos;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JButton btnCerrar;

    // Negocio y Helpers
    private final PagoController pagoController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructor del formulario. Inicializa la ventana, carga los catálogos y llena la tabla.
     */
    public PagoForm() {
        this.pagoController = new PagoController();
        inicializarComponentes();
        cargarCatalogos();
        cargarDatosTabla();
    }

    /**
     * Inicializa todos los componentes de la interfaz de usuario Swing.
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
        JLabel lblTitle = new JLabel("Módulo de Registro de Pagos");
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
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        // Título del Panel de Registro
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblRegTitle = new JLabel("Registrar Pago");
        lblRegTitle.setFont(fontTitulo);
        lblRegTitle.setForeground(ModernComponents.COLOR_PRIMARY);
        panelRegistro.add(lblRegTitle, gbc);

        // Selección de Venta
        gbc.gridy = 1; gbc.insets = new Insets(12, 8, 2, 8);
        JLabel lblVenta = new JLabel("Seleccione Venta (*):");
        lblVenta.setFont(fontEtiqueta);
        lblVenta.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblVenta, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 8, 8, 8);
        cmbVentas = new JComboBox<>();
        cmbVentas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbVentas.setPreferredSize(new Dimension(0, 30));
        cmbVentas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof VentaDetalle) {
                    VentaDetalle v = (VentaDetalle) value;
                    setText("Venta #" + v.getId() + " - " + v.getClienteNombre() + " ($" + String.format("%.2f", v.getTotal()) + ")");
                }
                return this;
            }
        });
        panelRegistro.add(cmbVentas, gbc);

        // Método de Pago
        gbc.gridy = 3; gbc.insets = new Insets(6, 8, 2, 8);
        JLabel lblMetodo = new JLabel("Método de Pago (*):");
        lblMetodo.setFont(fontEtiqueta);
        lblMetodo.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblMetodo, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 8, 8, 8);
        cmbMetodos = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        cmbMetodos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbMetodos.setPreferredSize(new Dimension(0, 30));
        panelRegistro.add(cmbMetodos, gbc);

        // Monto del Pago
        gbc.gridy = 5; gbc.insets = new Insets(6, 8, 2, 8);
        JLabel lblMonto = new JLabel("Monto a Pagar ($) (*):");
        lblMonto.setFont(fontEtiqueta);
        lblMonto.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblMonto, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(0, 8, 10, 8);
        txtMonto = new JTextField();
        ModernComponents.styleTextField(txtMonto);
        txtMonto.setPreferredSize(new Dimension(0, 32));
        txtMonto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtMonto.setForeground(ModernComponents.COLOR_PRIMARY);
        panelRegistro.add(txtMonto, gbc);

        // Espaciador vertical
        gbc.gridy = 7; gbc.weighty = 1.0;
        panelRegistro.add(Box.createVerticalGlue(), gbc);

        // Botones
        gbc.gridy = 8; gbc.weighty = 0.0; gbc.insets = new Insets(10, 8, 0, 8);
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

        // Barra de Búsqueda
        JPanel panelBusqueda = new JPanel(new GridBagLayout());
        panelBusqueda.setOpaque(false);
        GridBagConstraints gbcBusq = new GridBagConstraints();
        gbcBusq.fill = GridBagConstraints.HORIZONTAL;
        gbcBusq.insets = new Insets(0, 5, 0, 5);

        JLabel lblBuscar = new JLabel("Buscar por Cliente, Evento o Método:");
        ModernComponents.styleLabel(lblBuscar);
        gbcBusq.gridx = 0; gbcBusq.gridy = 0; gbcBusq.weightx = 0.0;
        panelBusqueda.add(lblBuscar, gbcBusq);

        txtBuscar = new JTextField();
        ModernComponents.styleTextField(txtBuscar);
        gbcBusq.gridx = 1; gbcBusq.gridy = 0; gbcBusq.weightx = 1.0;
        panelBusqueda.add(txtBuscar, gbcBusq);

        panelConsulta.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla de Pagos
        tableModel = new DefaultTableModel(
                new Object[]{"ID Pago", "ID Venta", "Cliente", "Evento", "Método Pago", "Monto ($)", "Fecha Pago"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPagos = new JTable(tableModel);
        tblPagos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernComponents.styleTable(tblPagos);

        tblPagos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblPagos.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblPagos.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblPagos.getColumnModel().getColumn(3).setPreferredWidth(180);
        tblPagos.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblPagos.getColumnModel().getColumn(5).setPreferredWidth(90);
        tblPagos.getColumnModel().getColumn(6).setPreferredWidth(130);

        JScrollPane scrollTable = new JScrollPane(tblPagos);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235)));
        panelConsulta.add(scrollTable, BorderLayout.CENTER);

        // Panel de Botón Cerrar (Volver)
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelInferior.setOpaque(false);
        btnCerrar = new ModernComponents.ModernButton("Volver", new Color(120, 120, 130), new Color(100, 100, 110));
        btnCerrar.setPreferredSize(new Dimension(100, 36));
        panelInferior.add(btnCerrar);
        panelConsulta.add(panelInferior, BorderLayout.SOUTH);


        // --- Integrar los paneles ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelRegistro, panelConsulta);
        splitPane.setDividerLocation(360);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setEnabled(false);

        // Armar panel principal
        add(panelTitle, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Configurar los listeners
        configurarEventos();
    }

    /**
     * Carga el catálogo de ventas activas en el ComboBox.
     */
    private void cargarCatalogos() {
        cmbVentas.removeAllItems();
        List<VentaDetalle> ventas = pagoController.obtenerVentasActivas();
        for (VentaDetalle v : ventas) {
            cmbVentas.addItem(v);
        }
        autocompletarMonto();
    }

    /**
     * Llena el JTable con la lista completa de pagos de la base de datos.
     */
    private void cargarDatosTabla() {
        List<PagoDetalle> pagos = pagoController.obtenerPagos();
        llenarTabla(pagos);
    }

    /**
     * Mapea y rellena la tabla con la lista de pagos suministrada.
     */
    private void llenarTabla(List<PagoDetalle> lista) {
        tableModel.setRowCount(0);
        for (PagoDetalle p : lista) {
            String fechaStr = p.getFecha() != null ? p.getFecha().format(DATE_FORMATTER) : "";
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getVentaId(),
                    p.getClienteNombre(),
                    p.getEventoNombre(),
                    p.getMetodoPago(),
                    String.format("%.2f", p.getMonto()),
                    fechaStr
            });
        }
    }

    /**
     * Define los oyentes de eventos interactivos para la vista.
     */
    private void configurarEventos() {
        // Al seleccionar una venta diferente, autocompletar el monto de forma interactiva
        cmbVentas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autocompletarMonto();
            }
        });

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

        // Botón Registrar Pago
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarPago();
            }
        });

        // Botón Cerrar (Volver al Inicio en CardLayout)
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
     * Completa el campo de texto de monto con el total de la venta actualmente seleccionada.
     */
    private void autocompletarMonto() {
        VentaDetalle seleccionada = (VentaDetalle) cmbVentas.getSelectedItem();
        if (seleccionada != null) {
            txtMonto.setText(seleccionada.getTotal().toString());
        } else {
            txtMonto.setText("");
        }
    }

    /**
     * Ejecuta el filtro dinámico de la tabla a medida que se escribe en el cuadro de búsqueda.
     */
    private void realizarBusqueda() {
        String texto = txtBuscar.getText().trim();
        List<PagoDetalle> filtrado = pagoController.buscarPagos(texto);
        llenarTabla(filtrado);
    }

    /**
     * Valida el formulario y realiza el registro del pago a través del controlador.
     */
    private void procesarPago() {
        VentaDetalle venta = (VentaDetalle) cmbVentas.getSelectedItem();
        String metodo = (String) cmbMetodos.getSelectedItem();
        String montoStr = txtMonto.getText().trim();

        if (venta == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una venta activa.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (metodo == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un método de pago.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el monto del pago.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal monto;
        try {
            monto = new BigDecimal(montoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El monto ingresado debe ser un número decimal válido.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "El monto del pago debe ser mayor a 0.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pago pago = new Pago();
        pago.setVentaId(venta.getId());
        pago.setMetodoPago(metodo);
        pago.setMonto(monto);
        pago.setFecha(LocalDateTime.now());

        boolean exito = pagoController.registrarPago(pago);
        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "¡Pago registrado exitosamente!\nID del Pago: " + pago.getId(),
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarCatalogos();
            cargarDatosTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al registrar el pago en el sistema.",
                    "Error al Registrar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Restablece los campos del formulario a sus valores predeterminados.
     */
    private void limpiarFormulario() {
        if (cmbVentas.getItemCount() > 0) cmbVentas.setSelectedIndex(0);
        if (cmbMetodos.getItemCount() > 0) cmbMetodos.setSelectedIndex(0);
        autocompletarMonto();
    }

    /**
     * Permite visualizar e iniciar este panel individualmente en IntelliJ.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prueba PagoForm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new PagoForm());
            frame.setSize(1050, 620);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
