package com.eventsecurityapp.enums;

/**
 * Enumeración para representar las operaciones básicas de persistencia
 * o estados de acción dentro de la aplicación (Create, Read, Update, Delete).
 */
public enum CRUD {
    /**
     * Operación de Creación (Insertar nuevos registros).
     */
    CREATE,

    /**
     * Operación de Lectura (Consultar o buscar registros).
     */
    READ,

    /**
     * Operación de Actualización (Modificar registros existentes).
     */
    UPDATE,

    /**
     * Operación de Eliminación (Borrar o desactivar registros).
     */
    DELETE
}
