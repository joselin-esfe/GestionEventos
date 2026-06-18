package com.eventsecurityapp.controlador;

import com.eventsecurityapp.modelo.Asistencia;
import com.eventsecurityapp.modelo.AsistenciaDetalle;
import com.eventsecurityapp.modelo.VentaDetalle;
import com.eventsecurityapp.persistencia.AsistenciaDAO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para la verificación y registro de Asistencia (Control de Acceso).
 * Intermedia entre la vista de seguridad y la persistencia de datos.
 * Aplica reglas de validación críticas para evitar boletos inválidos y doble entrada.
 */
public class AsistenciaController {

    private final AsistenciaDAO asistenciaDAO;

    /**
     * Constructor del controlador. Inicializa la persistencia de asistencia.
     */
    public AsistenciaController() {
        this.asistenciaDAO = new AsistenciaDAO();
    }

    /**
     * Verifica la validez de un boleto (venta) y registra el ingreso en el sistema.
     * Realiza validaciones críticas para evitar ingresos duplicados y ventas canceladas.
     *
     * @param ventaId Código identificador de la venta (boleto).
     * @return Objeto {@link AsistenciaDetalle} con la información del ingreso verificado.
     * @throws RuntimeException si el boleto es inexistente, cancelado o si ya se registró asistencia.
     */
    public AsistenciaDetalle verificarYRegistrarAsistencia(int ventaId) {
        // 1. Validar si la venta existe
        VentaDetalle venta = asistenciaDAO.obtenerVentaDetalle(ventaId);
        if (venta == null) {
            throw new RuntimeException("El boleto (Venta #" + ventaId + ") no está registrado.");
        }

        // 2. Validar que la venta esté activa
        if (!venta.isStatus()) {
            throw new RuntimeException("El boleto corresponde a una venta inactiva o cancelada.");
        }

        // 3. Validar prevención de doble ingreso (duplicados)
        if (asistenciaDAO.existeAsistenciaParaVenta(ventaId)) {
            throw new RuntimeException("¡ENTRADA DUPLICADA! Este boleto ya registró ingreso anteriormente.");
        }

        // 4. Instanciar e insertar registro de asistencia
        Asistencia asistencia = new Asistencia();
        asistencia.setVentaId(ventaId);
        asistencia.setFechaIngreso(LocalDateTime.now());

        boolean exito = asistenciaDAO.insertar(asistencia);
        if (!exito) {
            throw new RuntimeException("Ocurrió un error interno al registrar la asistencia en la base de datos.");
        }

        // 5. Retornar el detalle descriptivo del ingreso
        return new AsistenciaDetalle(
                asistencia.getId(),
                ventaId,
                asistencia.getFechaIngreso(),
                venta.getClienteNombre(),
                venta.getEventoNombre()
        );
    }

    /**
     * Devuelve la lista completa de asistencias registradas.
     *
     * @return Lista de {@link AsistenciaDetalle}.
     */
    public List<AsistenciaDetalle> obtenerAsistencias() {
        return asistenciaDAO.listar();
    }

    /**
     * Filtra los registros de asistencia por nombre de cliente o evento en tiempo real.
     *
     * @param texto Criterio de búsqueda.
     * @return Lista de registros filtrados.
     */
    public List<AsistenciaDetalle> buscarAsistencias(String texto) {
        return asistenciaDAO.buscarPorClienteOEvento(texto);
    }
}
