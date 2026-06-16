package com.eventsecurityapp.controlador;

import com.eventsecurityapp.modelo.Pago;
import com.eventsecurityapp.modelo.PagoDetalle;
import com.eventsecurityapp.modelo.VentaDetalle;
import com.eventsecurityapp.persistencia.PagoDAO;
import com.eventsecurityapp.persistencia.VentaDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de Pagos en el sistema.
 * Actúa como intermediario (mediador) entre la capa gráfica (Vista)
 * y la capa de acceso a datos (Persistencia).
 */
public class PagoController {

    private final PagoDAO pagoDAO;
    private final VentaDAO ventaDAO;

    /**
     * Constructor del controlador. Inicializa las clases de acceso a datos necesarias.
     */
    public PagoController() {
        this.pagoDAO = new PagoDAO();
        this.ventaDAO = new VentaDAO();
    }

    /**
     * Registra un nuevo pago en el sistema.
     *
     * @param pago El pago a registrar.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean registrarPago(Pago pago) {
        return pagoDAO.insertar(pago);
    }

    /**
     * Obtiene el listado de todos los pagos registrados con sus detalles.
     *
     * @return Lista de objetos {@link PagoDetalle}.
     */
    public List<PagoDetalle> obtenerPagos() {
        return pagoDAO.listar();
    }

    /**
     * Filtra los pagos registrados que coincidan con el texto de búsqueda.
     *
     * @param texto Criterio de búsqueda (cliente, evento o método de pago).
     * @return Lista de objetos {@link PagoDetalle} filtrados.
     */
    public List<PagoDetalle> buscarPagos(String texto) {
        return pagoDAO.buscarPorClienteOEventoOMetodo(texto);
    }

    /**
     * Obtiene el listado de ventas activas en el sistema para asociarlas a un pago.
     * Filtra las ventas que tengan estado activo (status = true).
     *
     * @return Lista de ventas activas.
     */
    public List<VentaDetalle> obtenerVentasActivas() {
        List<VentaDetalle> todas = ventaDAO.listar();
        List<VentaDetalle> activas = new ArrayList<>();
        for (VentaDetalle v : todas) {
            if (v.isStatus()) {
                activas.add(v);
            }
        }
        return activas;
    }
}
