package com.eventsecurityapp.controlador;

import com.eventsecurityapp.modelo.Cliente;
import com.eventsecurityapp.modelo.Evento;
import com.eventsecurityapp.modelo.Venta;
import com.eventsecurityapp.modelo.VentaDetalle;
import com.eventsecurityapp.persistencia.ClienteDAO;
import com.eventsecurityapp.persistencia.EventoDAO;
import com.eventsecurityapp.persistencia.VentaDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de Ventas de Entradas.
 * Actúa como mediador entre la interfaz gráfica (Vista) y la capa de acceso a datos (Persistencia).
 * Ofrece métodos para el registro, la consulta interactiva, el filtrado de ventas
 * y la carga estructurada de catálogos activos (clientes y eventos).
 */
public class VentaController {

    private final VentaDAO ventaDAO;
    private final ClienteDAO clienteDAO;
    private final EventoDAO eventoDAO;

    /**
     * Constructor del controlador. Inicializa las instancias necesarias de acceso a datos.
     */
    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.clienteDAO = new ClienteDAO();
        this.eventoDAO = new EventoDAO();
    }

    /**
     * Registra una venta en el sistema y su correspondiente pago automático de forma transaccional.
     * Reduce transaccionalmente la disponibilidad de cupos.
     *
     * @param venta      Objeto Venta con los datos ingresados por el usuario.
     * @param metodoPago El método de pago elegido (Efectivo, Tarjeta, Transferencia).
     * @return true si el registro fue exitoso, false en caso contrario.
     * @throws RuntimeException si se violan reglas de negocio (ej. falta de cupos o evento inactivo).
     */
    public boolean registrarVenta(Venta venta, String metodoPago) {
        return ventaDAO.insertar(venta, metodoPago);
    }

    /**
     * Recupera el listado completo de ventas registradas en el sistema.
     *
     * @return Lista de objetos {@link VentaDetalle} con nombres de clientes y eventos resueltos.
     */
    public List<VentaDetalle> obtenerVentas() {
        return ventaDAO.listar();
    }

    /**
     * Busca y filtra las ventas por una coincidencia en el nombre del cliente o evento.
     *
     * @param texto Criterio de búsqueda ingresado por el usuario.
     * @return Lista de objetos {@link VentaDetalle} filtrados por coincidencia.
     */
    public List<VentaDetalle> buscarVentas(String texto) {
        return ventaDAO.buscarPorClienteOEvento(texto);
    }

    /**
     * Carga y devuelve únicamente los clientes con estado activo.
     *
     * @return Lista de clientes activos.
     */
    public List<Cliente> obtenerClientesActivos() {
        List<Cliente> todos = clienteDAO.listar();
        List<Cliente> activos = new ArrayList<>();
        for (Cliente c : todos) {
            if (c.isStatus()) {
                activos.add(c);
            }
        }
        return activos;
    }

    /**
     * Carga y devuelve únicamente los eventos con estado activo.
     *
     * @return Lista de eventos activos.
     */
    public List<Evento> obtenerEventosActivos() {
        List<Evento> todos = eventoDAO.listar();
        List<Evento> activos = new ArrayList<>();
        for (Evento e : todos) {
            if (e.isStatus()) {
                activos.add(e);
            }
        }
        return activos;
    }
}
