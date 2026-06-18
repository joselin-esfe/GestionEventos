package com.eventsecurityapp.modelo;

import java.time.LocalDateTime;

/**
 * Entidad que representa un evento de seguridad.
 */
public class Evento {
    private int id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fecha;
    private String lugar;
    private int cuposTotales;
    private int cuposDisponibles;
    private boolean status;

    /**
     * Constructor vacío.
     */
    public Evento() {
    }

    /**
     * Constructor completo.
     *
     * @param id               Identificador único del evento.
     * @param nombre           Nombre del evento.
     * @param descripcion      Descripción o detalles del evento.
     * @param fecha            Fecha y hora de realización.
     * @param lugar            Lugar físico o dirección.
     * @param cuposTotales     Capacidad máxima de cupos.
     * @param cuposDisponibles Cantidad de cupos libres actualmente.
     * @param status           Estado del evento (activo/inactivo/cancelado).
     */
    public Evento(int id, String nombre, String descripcion, LocalDateTime fecha, String lugar, int cuposTotales, int cuposDisponibles, boolean status) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.lugar = lugar;
        this.cuposTotales = cuposTotales;
        this.cuposDisponibles = cuposDisponibles;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public int getCuposTotales() {
        return cuposTotales;
    }

    public void setCuposTotales(int cuposTotales) {
        this.cuposTotales = cuposTotales;
    }

    public int getCuposDisponibles() {
        return cuposDisponibles;
    }

    public void setCuposDisponibles(int cuposDisponibles) {
        this.cuposDisponibles = cuposDisponibles;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fecha=" + fecha +
                ", lugar='" + lugar + '\'' +
                ", cuposTotales=" + cuposTotales +
                ", cuposDisponibles=" + cuposDisponibles +
                ", status=" + status +
                '}';
    }
}
