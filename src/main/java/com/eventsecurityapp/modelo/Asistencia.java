package com.eventsecurityapp.modelo;

import java.time.LocalDateTime;

/**
 * Entidad que representa la asistencia o ingreso de un cliente al evento.
 */
public class Asistencia {
    private int id;
    private int ventaId;
    private LocalDateTime fechaIngreso;

    /**
     * Constructor vacío.
     */
    public Asistencia() {
    }

    /**
     * Constructor completo.
     *
     * @param id           Identificador único de la asistencia.
     * @param ventaId      ID de la venta asociada a la entrada escaneada/verificada.
     * @param fechaIngreso Fecha y hora del registro de ingreso.
     */
    public Asistencia(int id, int ventaId, LocalDateTime fechaIngreso) {
        this.id = id;
        this.ventaId = ventaId;
        this.fechaIngreso = fechaIngreso;
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

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @Override
    public String toString() {
        return "Asistencia{" +
                "id=" + id +
                ", ventaId=" + ventaId +
                ", fechaIngreso=" + fechaIngreso +
                '}';
    }
}
