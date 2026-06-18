package com.eventsecurityapp.modelo;

import java.time.LocalDateTime;

/**
 * Representa los detalles completos de un ingreso o asistencia registrada en un evento.
 * Extiende la clase base {@link Asistencia} incorporando los nombres descriptivos
 * del cliente y del evento asociado, optimizando la visualización en la interfaz.
 */
public class AsistenciaDetalle extends Asistencia {
    private String clienteNombre;
    private String eventoNombre;

    /**
     * Constructor vacío.
     */
    public AsistenciaDetalle() {
        super();
    }

    /**
     * Constructor completo que inicializa los atributos de asistencia y sus detalles descriptivos.
     *
     * @param id             Identificador único del registro de asistencia.
     * @param ventaId        ID de la venta asociada al boleto verificado.
     * @param fechaIngreso   Fecha y hora de registro de ingreso.
     * @param clienteNombre  Nombre del cliente que asiste al evento.
     * @param eventoNombre   Nombre del evento al cual se asiste.
     */
    public AsistenciaDetalle(int id, int ventaId, LocalDateTime fechaIngreso, String clienteNombre, String eventoNombre) {
        super(id, ventaId, fechaIngreso);
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
        return "AsistenciaDetalle{" +
                "id=" + getId() +
                ", ventaId=" + getVentaId() +
                ", fechaIngreso=" + getFechaIngreso() +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", eventoNombre='" + eventoNombre + '\'' +
                '}';
    }
}
