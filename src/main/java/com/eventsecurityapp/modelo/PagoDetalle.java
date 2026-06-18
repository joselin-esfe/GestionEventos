package com.eventsecurityapp.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa los detalles completos de un pago asociado a una venta de entradas.
 * Extiende la clase base {@link Pago} incorporando los nombres descriptivos
 * del cliente y del evento asociado a la venta, facilitando su visualización.
 */
public class PagoDetalle extends Pago {
    private String clienteNombre;
    private String eventoNombre;

    /**
     * Constructor vacío.
     */
    public PagoDetalle() {
        super();
    }

    /**
     * Constructor completo que inicializa los atributos del pago y sus detalles relacionados.
     *
     * @param id             Identificador único del pago.
     * @param ventaId        ID de la venta asociada.
     * @param metodoPago     Método de pago (Efectivo, Tarjeta, Transferencia).
     * @param monto          Monto pagado.
     * @param fecha          Fecha y hora en que se registró el pago.
     * @param clienteNombre  Nombre del cliente asociado a la venta.
     * @param eventoNombre   Nombre del evento asociado a la venta.
     */
    public PagoDetalle(int id, int ventaId, String metodoPago, BigDecimal monto, LocalDateTime fecha,
                       String clienteNombre, String eventoNombre) {
        super(id, ventaId, metodoPago, monto, fecha);
        this.clienteNombre = clienteNombre;
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
        return "PagoDetalle{" +
                "id=" + getId() +
                ", ventaId=" + getVentaId() +
                ", metodoPago='" + getMetodoPago() + '\'' +
                ", monto=" + getMonto() +
                ", fecha=" + getFecha() +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", eventoNombre='" + eventoNombre + '\'' +
                '}';
    }
}
