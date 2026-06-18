package com.eventsecurityapp.utils;

/**
 * Clase utilitaria para mapear opciones de componentes visuales (como JComboBox).
 * Permite almacenar una clave/ID asociado a una descripción textual amigable para el usuario.
 */
public class CBOption {

    private int id;
    private String descripcion;

    /**
     * Constructor vacío.
     */
    public CBOption() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param id El identificador único de la opción (usualmente el ID de la base de datos).
     * @param descripcion La descripción o texto a mostrar en el componente visual.
     */
    public CBOption(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el identificador único.
     *
     * @return El ID de la opción.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador único.
     *
     * @param id El nuevo ID de la opción.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene la descripción de la opción.
     *
     * @return La descripción en texto.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la opción.
     *
     * @param descripcion La nueva descripción.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Sobrescribe el método toString() para que los componentes visuales de Java Swing
     * (como JComboBox) muestren únicamente la descripción textual en la interfaz de usuario.
     *
     * @return La descripción de la opción.
     */
    @Override
    public String toString() {
        return descripcion;
    }
}
