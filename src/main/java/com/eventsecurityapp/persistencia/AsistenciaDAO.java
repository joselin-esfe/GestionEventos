package com.eventsecurityapp.persistencia;

import com.eventsecurityapp.modelo.Asistencia;
import com.eventsecurityapp.modelo.AsistenciaDetalle;
import com.eventsecurityapp.modelo.VentaDetalle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Asistencia.
 * Administra las consultas y el registro de entradas en la tabla 'asistencias' de SQL Server.
 */
public class AsistenciaDAO {

    /**
     * Inserta un nuevo registro de asistencia (ingreso) en la base de datos.
     * Asigna el ID autogenerado al objeto Asistencia.
     *
     * @param asistencia El objeto Asistencia a insertar.
     * @return true si la inserción fue correcta, false en caso contrario.
     */
    public boolean insertar(Asistencia asistencia) {
        if (asistencia == null) {
            return false;
        }

        String sql = "INSERT INTO dbo.asistencias (venta_id, fecha_ingreso) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, asistencia.getVentaId());
            ps.setTimestamp(2, Timestamp.valueOf(asistencia.getFechaIngreso()));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        asistencia.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar asistencia: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Comprueba si ya existe un registro de asistencia previo para el ID de venta indicado.
     *
     * @param ventaId Identificador único de la venta.
     * @return true si ya existe asistencia registrada para esa venta, false de lo contrario.
     */
    public boolean existeAsistenciaParaVenta(int ventaId) {
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
            System.err.println("Error al comprobar existencia de asistencia: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene el detalle descriptivo de una venta específica (ID, estado y nombres de cliente/evento).
     *
     * @param ventaId Identificador de la venta.
     * @return Objeto {@link VentaDetalle} con la información de la venta, o null si no existe.
     */
    public VentaDetalle obtenerVentaDetalle(int ventaId) {
        String sql = "SELECT v.id, v.status, v.cantidad, v.total, v.fecha, " +
                "c.nombre AS cliente_nombre, c.dni AS cliente_dni, " +
                "e.nombre AS evento_nombre " +
                "FROM dbo.ventas v " +
                "INNER JOIN dbo.clientes c ON v.cliente_id = c.id " +
                "INNER JOIN dbo.eventos e ON v.evento_id = e.id " +
                "WHERE v.id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ventaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    VentaDetalle vd = new VentaDetalle();
                    vd.setId(rs.getInt("id"));
                    vd.setStatus(rs.getBoolean("status"));
                    vd.setCantidad(rs.getInt("cantidad"));
                    vd.setTotal(rs.getBigDecimal("total"));
                    
                    Timestamp t = rs.getTimestamp("fecha");
                    if (t != null) {
                        vd.setFecha(t.toLocalDateTime());
                    }

                    vd.setClienteNombre(rs.getString("cliente_nombre"));
                    vd.setClienteDni(rs.getString("cliente_dni"));
                    vd.setEventoNombre(rs.getString("evento_nombre"));
                    return vd;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al recuperar detalle de la venta: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos los ingresos registrados de forma ordenada por ID descendente.
     *
     * @return Lista de objetos {@link AsistenciaDetalle}.
     */
    public List<AsistenciaDetalle> listar() {
        List<AsistenciaDetalle> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.venta_id, a.fecha_ingreso, " +
                "c.nombre AS cliente_nombre, " +
                "e.nombre AS evento_nombre " +
                "FROM dbo.asistencias a " +
                "INNER JOIN dbo.ventas v ON a.venta_id = v.id " +
                "INNER JOIN dbo.clientes c ON v.cliente_id = c.id " +
                "INNER JOIN dbo.eventos e ON v.evento_id = e.id " +
                "ORDER BY a.id DESC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AsistenciaDetalle detalle = mapearAsistenciaDetalle(rs);
                lista.add(detalle);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar asistencias: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca y filtra las asistencias registradas por coincidencia en el cliente o evento.
     *
     * @param texto Criterio de búsqueda.
     * @return Lista de objetos {@link AsistenciaDetalle} que coinciden con el filtro.
     */
    public List<AsistenciaDetalle> buscarPorClienteOEvento(String texto) {
        List<AsistenciaDetalle> lista = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return listar();
        }

        String sql = "SELECT a.id, a.venta_id, a.fecha_ingreso, " +
                "c.nombre AS cliente_nombre, " +
                "e.nombre AS evento_nombre " +
                "FROM dbo.asistencias a " +
                "INNER JOIN dbo.ventas v ON a.venta_id = v.id " +
                "INNER JOIN dbo.clientes c ON v.cliente_id = c.id " +
                "INNER JOIN dbo.eventos e ON v.evento_id = e.id " +
                "WHERE c.nombre LIKE ? OR e.nombre LIKE ? " +
                "ORDER BY a.id DESC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String buscarParam = "%" + texto.trim() + "%";
            ps.setString(1, buscarParam);
            ps.setString(2, buscarParam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AsistenciaDetalle detalle = mapearAsistenciaDetalle(rs);
                    lista.add(detalle);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar asistencias: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Helper para mapear una fila de ResultSet a un objeto {@link AsistenciaDetalle}.
     */
    private AsistenciaDetalle mapearAsistenciaDetalle(ResultSet rs) throws SQLException {
        AsistenciaDetalle ad = new AsistenciaDetalle();
        ad.setId(rs.getInt("id"));
        ad.setVentaId(rs.getInt("venta_id"));
        
        Timestamp timestamp = rs.getTimestamp("fecha_ingreso");
        if (timestamp != null) {
            ad.setFechaIngreso(timestamp.toLocalDateTime());
        }

        ad.setClienteNombre(rs.getString("cliente_nombre"));
        ad.setEventoNombre(rs.getString("evento_nombre"));
        return ad;
    }
}
