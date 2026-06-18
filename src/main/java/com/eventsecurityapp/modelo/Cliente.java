package com.eventsecurityapp.modelo;

/**
 * Entidad que representa a un cliente.
 */
public class Cliente {
    private int id;
    private String dni;
    private String nombre;
    private String email;
    private String telefono;
    private boolean status;

    /**
     * Constructor vacío.
     */
    public Cliente() {
    }

    /**
     * Constructor completo.
     *
     * @param id       Identificador único del cliente.
     * @param dni      Documento Nacional de Identidad único.
     * @param nombre   Nombre completo o razón social.
     * @param email    Dirección de correo electrónico.
     * @param telefono Número de contacto.
     * @param status   Estado activo/inactivo del cliente.
     */
    public Cliente(int id, String dni, String nombre, String email, String telefono, boolean status) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", status=" + status +
                '}';
    }
}
