package com.eventsecurityapp.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa los detalles completos de una venta de entradas, incluyendo
 * información descriptiva del cliente y del evento asociado.
 * Extiende la clase base {@link Venta} para su representación y visualización.
 */
public class VentaDetalle extends Venta {
    private String clienteNombre;
    private String clienteDni;
    private String eventoNombre;

    /**
     * Constructor vacío.
     */
    public VentaDetalle() {
        super();
    }

    /**
     * Constructor completo que incluye las propiedades de la venta y sus detalles.
     *
     * @param id             Identificador único de la venta.
     * @param fecha          Fecha y hora en que se procesó la venta.
     * @param clienteId      ID del cliente asociado.
     * @param eventoId       ID del evento asociado.
     * @param cantidad       Cantidad de entradas compradas.
     * @param total          Monto total pagado.
     * @param status         Estado de la venta.
     * @param clienteNombre  Nombre del cliente asociado.
     * @param clienteDni     Documento Nacional de Identidad (DNI) del cliente.
     * @param eventoNombre   Nombre del evento asociado.
     */
    public VentaDetalle(int id, LocalDateTime fecha, int clienteId, int eventoId, int cantidad, BigDecimal total, boolean status,
                        String clienteNombre, String clienteDni, String eventoNombre) {
        super(id, fecha, clienteId, eventoId, cantidad, total, status);
        this.clienteNombre = clienteNombre;
        this.clienteDni = clienteDni;
        this.eventoNombre = eventoNombre;
    }

    /**
     * Obtiene el nombre del cliente.
     *
     * @return El nombre del cliente.
     */
    public String getClienteNombre() {
        return clienteNombre;
    }

    /**
     * Establece el nombre del cliente.
     *
     * @param clienteNombre El nombre del cliente.
     */
    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    /**
     * Obtiene el DNI del cliente.
     *
     * @return El DNI del cliente.
     */
    public String getClienteDni() {
        return clienteDni;
    }

    /**
     * Establece el DNI del cliente.
     *
     * @param clienteDni El DNI del cliente.
     */
    public void setClienteDni(String clienteDni) {
        this.clienteDni = clienteDni;
    }

    /**
     * Obtiene el nombre del evento.
     *
     * @return El nombre del evento.
     */
    public String getEventoNombre() {
        return eventoNombre;
    }

    /**
     * Establece el nombre del evento.
     *
     * @param eventoNombre El nombre del evento.
     */
    public void setEventoNombre(String eventoNombre) {
        this.eventoNombre = eventoNombre;
    }

    @Override
    public String toString() {
        return "VentaDetalle{" +
                "id=" + getId() +
                ", fecha=" + getFecha() +
                ", clienteId=" + getClienteId() +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", clienteDni='" + clienteDni + '\'' +
                ", eventoId=" + getEventoId() +
                ", eventoNombre='" + eventoNombre + '\'' +
                ", cantidad=" + getCantidad() +
                ", total=" + getTotal() +
                ", status=" + isStatus() +
                '}';
    }
}
