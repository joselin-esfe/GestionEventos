package com.eventsecurityapp.modelo;

/**
 * Entidad que representa a un usuario del sistema.
 */
public class User {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private boolean status;

    /**
     * Constructor vacío.
     */
    public User() {
    }

    /**
     * Constructor completo.
     *
     * @param id       Identificador único del usuario.
     * @param nombre   Nombre del usuario.
     * @param email    Correo electrónico único.
     * @param password Contraseña cifrada en hash.
     * @param status   Estado activo/inactivo.
     */
    public User(int id, String nombre, String email, String password, boolean status) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
