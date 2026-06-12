package com.eventsecurityapp.persistencia;

import com.eventsecurityapp.modelo.Evento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Evento.
 * Gestiona las operaciones de persistencia en la tabla 'eventos' de SQL Server.
 */
public class EventoDAO {

    /**
     * Inserta un nuevo evento en la base de datos.
     *
     * @param evento El objeto Evento con los datos a guardar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertar(Evento evento) {
        if (evento == null) {
            return false;
        }

        String sql = "INSERT INTO dbo.eventos (nombre, descripcion, fecha, lugar, cupos_totales, cupos_disponibles, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, evento.getNombre());
            ps.setString(2, evento.getDescripcion());
            ps.setTimestamp(3, evento.getFecha() != null ? Timestamp.valueOf(evento.getFecha()) : null);
            ps.setString(4, evento.getLugar());
            ps.setInt(5, evento.getCuposTotales());
            ps.setInt(6, evento.getCuposDisponibles());
            ps.setBoolean(7, evento.isStatus());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar evento: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza los datos de un evento existente en la base de datos.
     *
     * @param evento El objeto Evento con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizar(Evento evento) {
        if (evento == null) {
            return false;
        }

        String sql = "UPDATE dbo.eventos SET nombre = ?, descripcion = ?, fecha = ?, lugar = ?, cupos_totales = ?, cupos_disponibles = ?, status = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, evento.getNombre());
            ps.setString(2, evento.getDescripcion());
            ps.setTimestamp(3, evento.getFecha() != null ? Timestamp.valueOf(evento.getFecha()) : null);
            ps.setString(4, evento.getLugar());
            ps.setInt(5, evento.getCuposTotales());
            ps.setInt(6, evento.getCuposDisponibles());
            ps.setBoolean(7, evento.isStatus());
            ps.setInt(8, evento.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar evento: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inactiva lógicamente un evento cambiando su estado a inactivo (status = 0).
     *
     * @param id El identificador único del evento a inactivar.
     * @return true si el estado fue actualizado correctamente, false en caso contrario.
     */
    public boolean inactivar(int id) {
        String sql = "UPDATE dbo.eventos SET status = 0 WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al inactivar evento: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lista todos los eventos registrados en la base de datos.
     *
     * @return Una lista de objetos Evento.
     */
    public List<Evento> listar() {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, fecha, lugar, cupos_totales, cupos_disponibles, status FROM dbo.eventos";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Evento evento = mapearEvento(rs);
                lista.add(evento);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar eventos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca eventos cuyo nombre contenga el texto proporcionado.
     *
     * @param texto El texto de búsqueda para filtrar por nombre.
     * @return Una lista de eventos que coinciden con el criterio de búsqueda.
     */
    public List<Evento> buscarPorNombre(String texto) {
        List<Evento> lista = new ArrayList<>();
        if (texto == null) {
            return lista;
        }

        String sql = "SELECT id, nombre, descripcion, fecha, lugar, cupos_totales, cupos_disponibles, status FROM dbo.eventos WHERE nombre LIKE ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evento evento = mapearEvento(rs);
                    lista.add(evento);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar eventos por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Mapea el registro actual del ResultSet a un objeto Evento.
     *
     * @param rs ResultSet posicionado en la fila actual.
     * @return Objeto Evento mapeado.
     * @throws SQLException si ocurre un error al leer del ResultSet.
     */
    private Evento mapearEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getInt("id"));
        evento.setNombre(rs.getString("nombre"));
        evento.setDescripcion(rs.getString("descripcion"));

        Timestamp timestamp = rs.getTimestamp("fecha");
        if (timestamp != null) {
            evento.setFecha(timestamp.toLocalDateTime());
        }

        evento.setLugar(rs.getString("lugar"));
        evento.setCuposTotales(rs.getInt("cupos_totales"));
        evento.setCuposDisponibles(rs.getInt("cupos_disponibles"));
        evento.setStatus(rs.getBoolean("status"));
        return evento;
    }
}
