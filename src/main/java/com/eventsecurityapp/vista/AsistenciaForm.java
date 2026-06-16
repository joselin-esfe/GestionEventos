package com.eventsecurityapp.vista;

import com.eventsecurityapp.controlador.AsistenciaController;
import com.eventsecurityapp.modelo.AsistenciaDetalle;

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
 * Vista Swing para el Módulo de Control de Acceso y Asistencia de Eventos.
 * Rediseñado como un JPanel para integrarse en el CardLayout del Dashboard.
 */
public class AsistenciaForm extends JPanel {

    // Componentes del panel izquierdo (Verificador)
    private JTextField txtVentaId;
    private JButton btnVerificar;
    private JLabel lblEstadoVisual;
    private JButton btnLimpiarEstado;

    // Componentes del panel derecho (Consulta)
    private JTable tblAsistencias;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JButton btnCerrar;

    // Negocio y Helpers
    private final AsistenciaController asistenciaController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor del formulario. Inicializa los componentes, configura los eventos y carga el listado actual.
     */
    public AsistenciaForm() {
        this.asistenciaController = new AsistenciaController();
        inicializarComponentes();
        cargarDatosTabla();
    }

    /**
     * Inicializa los componentes visuales Swing con estilo premium.
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
        JLabel lblTitle = new JLabel("Control de Acceso y Seguridad");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ModernComponents.COLOR_PRIMARY);
        panelTitle.add(lblTitle, BorderLayout.WEST);

        // --- PANEL DE VERIFICACIÓN (IZQUIERDO) ---
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

        // Título del Panel de Control
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblRegTitle = new JLabel("Lector de Boletos (Ingreso)");
        lblRegTitle.setFont(fontTitulo);
        lblRegTitle.setForeground(ModernComponents.COLOR_PRIMARY);
        panelRegistro.add(lblRegTitle, gbc);

        // ID de la venta (Boleto)
        gbc.gridy = 1; gbc.insets = new Insets(12, 8, 2, 8);
        JLabel lblVenta = new JLabel("Ingrese ID del Boleto (Venta) (*):");
        lblVenta.setFont(fontEtiqueta);
        lblVenta.setForeground(ModernComponents.COLOR_SECONDARY);
        panelRegistro.add(lblVenta, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 8, 8, 8);
        txtVentaId = new JTextField();
        ModernComponents.styleTextField(txtVentaId);
        txtVentaId.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtVentaId.setHorizontalAlignment(JTextField.CENTER);
        txtVentaId.setPreferredSize(new Dimension(0, 36));
        panelRegistro.add(txtVentaId, gbc);

        // Botón Verificar
        gbc.gridy = 3; gbc.insets = new Insets(6, 8, 10, 8);
        btnVerificar = new ModernComponents.ModernButton("Verificar e Ingresar", ModernComponents.COLOR_PRIMARY, ModernComponents.COLOR_SECONDARY);
        btnVerificar.setPreferredSize(new Dimension(0, 36));
        panelRegistro.add(btnVerificar, gbc);

        // Indicador de Estado Visual Grande (Premium)
        gbc.gridy = 4; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 8, 10, 8);
        lblEstadoVisual = new JLabel("<html><center>LECTOR LISTO<br><br>Ingrese un código de boleto<br>y pulse Verificar.</center></html>", SwingConstants.CENTER);
        lblEstadoVisual.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstadoVisual.setOpaque(true);
        lblEstadoVisual.setBackground(new Color(240, 242, 245));
        lblEstadoVisual.setForeground(new Color(100, 100, 110));
        lblEstadoVisual.setBorder(BorderFactory.createLineBorder(new Color(210, 212, 215), 1));
        panelRegistro.add(lblEstadoVisual, gbc);

        // Botón Limpiar Pantalla
        gbc.gridy = 5; gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 0, 8);
        btnLimpiarEstado = new ModernComponents.ModernButton("Limpiar Lector", new Color(180, 180, 180), new Color(150, 150, 150));
        btnLimpiarEstado.setPreferredSize(new Dimension(0, 32));
        panelRegistro.add(btnLimpiarEstado, gbc);


        // --- PANEL DE CONSULTA (DERECHO) ---
        JPanel panelConsulta = new JPanel(new BorderLayout(15, 15));
        panelConsulta.setBackground(Color.WHITE);
        panelConsulta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Filtro de Búsqueda
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

        // Tabla de Asistencias
        tableModel = new DefaultTableModel(
                new Object[]{"ID Asistencia", "ID Venta", "Cliente", "Evento", "Fecha/Hora Ingreso"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblAsistencias = new JTable(tableModel);
        tblAsistencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernComponents.styleTable(tblAsistencias);

        tblAsistencias.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblAsistencias.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblAsistencias.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblAsistencias.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblAsistencias.getColumnModel().getColumn(4).setPreferredWidth(140);

        JScrollPane scrollTable = new JScrollPane(tblAsistencias);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235)));
        panelConsulta.add(scrollTable, BorderLayout.CENTER);

        // Panel de Botón Cerrar (Volver)
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelInferior.setOpaque(false);
        btnCerrar = new ModernComponents.ModernButton("Volver", new Color(120, 120, 130), new Color(100, 100, 110));
        btnCerrar.setPreferredSize(new Dimension(100, 36));
        panelInferior.add(btnCerrar);
        panelConsulta.add(panelInferior, BorderLayout.SOUTH);


        // --- Integrar paneles ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelRegistro, panelConsulta);
        splitPane.setDividerLocation(360);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setEnabled(false);

        // Armar el layout
        add(panelTitle, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Configurar listeners
        configurarEventos();
    }

    /**
     * Rellena la tabla gráfica con la lista de asistencias registradas.
     */
    private void cargarDatosTabla() {
        List<AsistenciaDetalle> asistencias = asistenciaController.obtenerAsistencias();
        llenarTabla(asistencias);
    }

    /**
     * Mapea los objetos de asistencia al modelo de la tabla.
     */
    private void llenarTabla(List<AsistenciaDetalle> lista) {
        tableModel.setRowCount(0);
        for (AsistenciaDetalle a : lista) {
            String fechaStr = a.getFechaIngreso() != null ? a.getFechaIngreso().format(DATE_FORMATTER) : "";
            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getVentaId(),
                    a.getClienteNombre(),
                    a.getEventoNombre(),
                    fechaStr
            });
        }
    }

    /**
     * Configura los controladores de eventos para los botones e ingresos de texto.
     */
    private void configurarEventos() {
        btnVerificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarIngreso();
            }
        });

        txtVentaId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarIngreso();
            }
        });

        btnLimpiarEstado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restablecerLector();
            }
        });

        // Búsqueda interactiva en la tabla
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { realizarBusqueda(); }
            @Override
            public void removeUpdate(DocumentEvent e) { realizarBusqueda(); }
            @Override
            public void changedUpdate(DocumentEvent e) { realizarBusqueda(); }
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
     * Valida el formato del ID de boleto y procesa la verificación de asistencia.
     * Actualiza el indicador visual en verde/rojo según el resultado.
     */
    private void procesarIngreso() {
        String idStr = txtVentaId.getText().trim();

        if (idStr.isEmpty()) {
            marcarDobleNegacion("Debe ingresar un ID de boleto.");
            return;
        }

        int ventaId;
        try {
            ventaId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            marcarDobleNegacion("ID de boleto inválido (debe ser numérico).");
            return;
        }

        try {
            AsistenciaDetalle ad = asistenciaController.verificarYRegistrarAsistencia(ventaId);
            
            // Éxito: Fondo verde
            lblEstadoVisual.setBackground(new Color(40, 167, 69)); // Verde éxito
            lblEstadoVisual.setForeground(Color.WHITE);
            String fechaStr = ad.getFechaIngreso() != null ? ad.getFechaIngreso().format(DATE_FORMATTER) : "";
            
            lblEstadoVisual.setText("<html><center>"
                    + "<font size='+1'><b>ACCESO AUTORIZADO</b></font><br><br>"
                    + "<b>Boleto:</b> Venta #" + ad.getVentaId() + "<br>"
                    + "<b>Cliente:</b> " + ad.getClienteNombre() + "<br>"
                    + "<b>Evento:</b> " + ad.getEventoNombre() + "<br><br>"
                    + "<b>Ingreso:</b> " + fechaStr
                    + "</center></html>");

            txtVentaId.setText("");
            cargarDatosTabla();
            txtVentaId.requestFocus();

        } catch (RuntimeException ex) {
            // Error: Fondo rojo
            marcarDobleNegacion(ex.getMessage());
        }
    }

    /**
     * Helper para pintar el indicador visual de color ROJO y detallar la razón.
     */
    private void marcarDobleNegacion(String mensaje) {
        lblEstadoVisual.setBackground(new Color(220, 53, 69)); // Rojo error
        lblEstadoVisual.setForeground(Color.WHITE);
        lblEstadoVisual.setText("<html><center>"
                + "<font size='+1'><b>ACCESO DENEGADO</b></font><br><br>"
                + mensaje
                + "</center></html>");
        txtVentaId.selectAll();
        txtVentaId.requestFocus();
    }

    /**
     * Restablece el lector a su estado inicial neutro.
     */
    private void restablecerLector() {
        txtVentaId.setText("");
        lblEstadoVisual.setBackground(new Color(240, 242, 245));
        lblEstadoVisual.setForeground(new Color(100, 100, 110));
        lblEstadoVisual.setText("<html><center>LECTOR LISTO<br><br>Ingrese un código de boleto<br>y pulse Verificar.</center></html>");
        txtVentaId.requestFocus();
    }

    /**
     * Realiza el filtrado dinámico de la JTable a medida que se ingresa texto en el buscador.
     */
    private void realizarBusqueda() {
        String texto = txtBuscar.getText().trim();
        List<AsistenciaDetalle> filtrado = asistenciaController.buscarAsistencias(texto);
        llenarTabla(filtrado);
    }

    /**
     * Permite visualizar e iniciar este panel individualmente en IntelliJ.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prueba AsistenciaForm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new AsistenciaForm());
            frame.setSize(1050, 620);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
