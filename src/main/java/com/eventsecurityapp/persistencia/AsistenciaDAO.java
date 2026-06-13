package com.eventsecurityapp.persistencia;

import com.eventsecurityapp.modelo.Asistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Asistencia.
 * Administra el registro de ingresos y prevenciones de duplicados.
 */
public class AsistenciaDAO {

    /**
     * Verifica si una venta ya tiene registrada una asistencia,
     * para evitar registrar asistencia duplicada.
     *
     * @param ventaId Identificador de la venta.
     * @return true si ya existe, false en caso contrario.
     */
    public boolean existeAsistencia(int ventaId) {
        String sql = "SELECT COUNT(*) FROM dbo.asistencias WHERE venta_id = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ventaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar asistencia duplicada: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Registra una nueva asistencia mostrando la fecha y hora de ingreso.
     * Evita registrar la asistencia si ya existe.
     *
     * @param asistencia Objeto Asistencia a registrar.
     * @return true si fue registrada, false si falló o ya existía.
     */
    public boolean insertar(Asistencia asistencia) {
        if (asistencia == null) {
            return false;
        }
        if (existeAsistencia(asistencia.getVentaId())) {
            // Ya se registró la asistencia para esta venta
            return false;
        }

        String sql = "INSERT INTO dbo.asistencias (venta_id, fecha_ingreso) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, asistencia.getVentaId());
            ps.setTimestamp(2, Timestamp.valueOf(asistencia.getFechaIngreso()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar asistencia: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Consulta todas las asistencias registradas.
     *
     * @return Una lista de objetos Asistencia.
     */
    public List<Asistencia> listar() {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT id, venta_id, fecha_ingreso FROM dbo.asistencias";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setVentaId(rs.getInt("venta_id"));
                a.setFechaIngreso(rs.getTimestamp("fecha_ingreso").toLocalDateTime());
                lista.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar asistencias: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
