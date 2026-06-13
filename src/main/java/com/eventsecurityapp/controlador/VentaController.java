package com.eventsecurityapp.controlador;

import com.eventsecurityapp.modelo.Cliente;
import com.eventsecurityapp.modelo.Evento;
import com.eventsecurityapp.modelo.Pago;
import com.eventsecurityapp.modelo.Venta;
import com.eventsecurityapp.persistencia.ClienteDAO;
import com.eventsecurityapp.persistencia.EventoDAO;
import com.eventsecurityapp.persistencia.VentaDAO;

import java.util.List;

/**
 * Controlador para gestionar la lógica de las ventas.
 * Se comunica con VentaDAO, ClienteDAO y EventoDAO para integrar la vista de Venta.
 */
public class VentaController {

    private final VentaDAO ventaDAO;
    private final ClienteDAO clienteDAO;
    private final EventoDAO eventoDAO;

    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.clienteDAO = new ClienteDAO();
        this.eventoDAO = new EventoDAO();
    }

    /**
     * Registra una venta verificando la transacción.
     *
     * @param venta Objeto Venta a registrar.
     * @param pago Objeto Pago asociado a la venta.
     * @return true si se registra exitosamente, false si ocurre algún error o validación falla.
     */
    public boolean registrarVenta(Venta venta, Pago pago) {
        return ventaDAO.registrarVenta(venta, pago);
    }

    /**
     * Obtiene la lista de todas las ventas.
     *
     * @return Lista de ventas.
     */
    public List<Venta> listarVentas() {
        return ventaDAO.listar();
    }

    /**
     * Busca ventas según un texto.
     *
     * @param texto Criterio de búsqueda (nombre de cliente o evento).
     * @return Lista de ventas que coinciden.
     */
    public List<Venta> buscarVentas(String texto) {
        return ventaDAO.buscar(texto);
    }

    /**
     * Obtiene la lista de clientes activos para los selectores de la vista.
     *
     * @return Lista de clientes.
     */
    public List<Cliente> listarClientes() {
        return clienteDAO.listar();
    }

    /**
     * Obtiene la lista de eventos activos para los selectores de la vista.
     *
     * @return Lista de eventos.
     */
    public List<Evento> listarEventos() {
        return eventoDAO.listar();
    }
}
