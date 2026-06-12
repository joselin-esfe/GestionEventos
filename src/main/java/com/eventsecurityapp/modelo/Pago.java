package com.eventsecurityapp.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la información de pago asociada a una venta.
 */
public class Pago {
    private int id;
    private int ventaId;
    private String metodoPago;
    private BigDecimal monto;
    private LocalDateTime fecha;

    /**
     * Constructor vacío.
     */
    public Pago() {
    }

    /**
     * Constructor completo.
     *
     * @param id          Identificador único del pago.
     * @param ventaId     ID de la venta asociada.
     * @param metodoPago  Método de pago utilizado (ej. Efectivo, Tarjeta, Transferencia).
     * @param monto       Monto del pago.
     * @param fecha       Fecha y hora del pago.
     */
    public Pago(int id, int ventaId, String metodoPago, BigDecimal monto, LocalDateTime fecha) {
        this.id = id;
        this.ventaId = ventaId;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVentaId() {
        return ventaId;
    }

    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", ventaId=" + ventaId +
                ", metodoPago='" + metodoPago + '\'' +
                ", monto=" + monto +
                ", fecha=" + fecha +
                '}';
    }
}
