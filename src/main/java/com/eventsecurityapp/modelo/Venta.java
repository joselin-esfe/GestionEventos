package com.eventsecurityapp.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la venta de entradas para un evento.
 */
public class Venta {
    private int id;
    private LocalDateTime fecha;
    private int clienteId;
    private int eventoId;
    private int cantidad;
    private BigDecimal total;
    private boolean status;

    /**
     * Constructor vacío.
     */
    public Venta() {
    }

    /**
     * Constructor completo.
     *
     * @param id        Identificador único de la venta.
     * @param fecha     Fecha y hora en que se procesó la venta.
     * @param clienteId ID del cliente asociado.
     * @param eventoId  ID del evento asociado.
     * @param cantidad  Cantidad de entradas compradas.
     * @param total     Monto total pagado (representado como BigDecimal para mayor precisión monetaria).
     * @param status    Estado de la venta (activa, cancelada, devuelta).
     */
    public Venta(int id, LocalDateTime fecha, int clienteId, int eventoId, int cantidad, BigDecimal total, boolean status) {
        this.id = id;
        this.fecha = fecha;
        this.clienteId = clienteId;
        this.eventoId = eventoId;
        this.cantidad = cantidad;
        this.total = total;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getEventoId() {
        return eventoId;
    }

    public void setEventoId(int eventoId) {
        this.eventoId = eventoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", clienteId=" + clienteId +
                ", eventoId=" + eventoId +
                ", cantidad=" + cantidad +
                ", total=" + total +
                ", status=" + status +
                '}';
    }
}
