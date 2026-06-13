package com.eventsecurityapp.controlador;

import com.eventsecurityapp.modelo.Asistencia;
import com.eventsecurityapp.persistencia.AsistenciaDAO;

import java.util.List;

/**
 * Controlador para el registro y gestión de asistencias (ingresos a eventos).
 * Conecta AsistenciaForm con AsistenciaDAO.
 */
public class AsistenciaController {

    private final AsistenciaDAO asistenciaDAO;

    public AsistenciaController() {
        this.asistenciaDAO = new AsistenciaDAO();
    }

    /**
     * Registra la asistencia verificando que no sea duplicada.
     *
     * @param asistencia Objeto asistencia.
     * @return true si es registrado exitosamente, false si ya estaba registrado o hubo un error.
     */
    public boolean registrarAsistencia(Asistencia asistencia) {
        return asistenciaDAO.insertar(asistencia);
    }

    /**
     * Obtiene la lista completa de asistencias.
     *
     * @return Lista de objetos Asistencia.
     */
    public List<Asistencia> listarAsistencias() {
        return asistenciaDAO.listar();
    }
}
