package com.eventsecurityapp.controlador;

import com.eventsecurityapp.modelo.Pago;
import com.eventsecurityapp.persistencia.PagoDAO;

import java.util.List;

/**
 * Controlador para la gestión de pagos.
 * Sirve de intermediario entre la vista PagoForm y la persistencia PagoDAO.
 */
public class PagoController {

    private final PagoDAO pagoDAO;

    public PagoController() {
        this.pagoDAO = new PagoDAO();
    }

    /**
     * Registra un pago de forma individual.
     * @param pago Objeto Pago a registrar.
     * @return true si es exitoso.
     */
    public boolean registrarPago(Pago pago) {
        return pagoDAO.insertar(pago);
    }

    /**
     * Consulta la lista de todos los pagos registrados.
     * @return Lista de objetos Pago.
     */
    public List<Pago> listarPagos() {
        return pagoDAO.listar();
    }
}
