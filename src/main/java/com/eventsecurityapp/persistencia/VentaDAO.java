package com.eventsecurityapp.persistencia;

import com.eventsecurityapp.modelo.Venta;
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
 * Data Access Object (DAO) para la gestión de persistencia de la entidad Venta.
 * Administra las transacciones en la tabla 'ventas' y el decremento de cupos
 * en la tabla 'eventos' de la base de datos SQL Server de manera segura.
 */
public class VentaDAO {

    /**
     * Registra una nueva venta de entradas y su pago correspondiente en la base de datos de manera transaccional.
     * Decrementa los cupos disponibles del evento correspondiente si hay suficientes.
     * Si no hay cupos o ocurre un error de base de datos, realiza un rollback completo.
     *
     * @param venta      El objeto Venta con la información a registrar.
     * @param metodoPago El método de pago utilizado (Efectivo, Tarjeta, Transferencia).
     * @return true si la venta, el pago y la actualización de cupos fueron exitosos, false en caso contrario.
     * @throws RuntimeException Si la transacción falla o no hay cupos disponibles.
     */
    public boolean insertar(Venta venta, String metodoPago) {
        if (venta == null || metodoPago == null) {
            return false;
        }

        String sqlSelectEvento = "SELECT cupos_disponibles, status FROM dbo.eventos WHERE id = ?";
        String sqlInsertVenta = "INSERT INTO dbo.ventas (fecha, cliente_id, evento_id, cantidad, total, status) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlInsertPago = "INSERT INTO dbo.pagos (venta_id, metodo_pago, monto, fecha) VALUES (?, ?, ?, ?)";
        String sqlUpdateEvento = "UPDATE dbo.eventos SET cupos_disponibles = cupos_disponibles - ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            int cuposDisponibles = 0;
            boolean eventoActivo = false;

            // 1. Validar disponibilidad de cupos y estado del evento
            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelectEvento)) {
                psSelect.setInt(1, venta.getEventoId());
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        cuposDisponibles = rs.getInt("cupos_disponibles");
                        eventoActivo = rs.getBoolean("status");
                    } else {
                        throw new SQLException("El evento especificado no existe.");
                    }
                }
            }

            if (!eventoActivo) {
                throw new SQLException("El evento seleccionado no está activo.");
            }

            if (cuposDisponibles < venta.getCantidad()) {
                throw new SQLException("Cupos insuficientes. Solicitados: " + venta.getCantidad() 
                        + ", Disponibles: " + cuposDisponibles);
            }

            // 2. Registrar la venta
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsertVenta, Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setTimestamp(1, Timestamp.valueOf(venta.getFecha()));
                psInsert.setInt(2, venta.getClienteId());
                psInsert.setInt(3, venta.getEventoId());
                psInsert.setInt(4, venta.getCantidad());
                psInsert.setBigDecimal(5, venta.getTotal());
                psInsert.setBoolean(6, venta.isStatus());

                int rowsInserted = psInsert.executeUpdate();
                if (rowsInserted == 0) {
                    throw new SQLException("No se pudo registrar la venta.");
                }

                // Obtener el ID autogenerado
                try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        venta.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("No se pudo obtener el identificador generado para la venta.");
                    }
                }
            }

            // 3. Registrar automáticamente el pago asociado
            try (PreparedStatement psInsertPago = conn.prepareStatement(sqlInsertPago)) {
                psInsertPago.setInt(1, venta.getId());
                psInsertPago.setString(2, metodoPago);
                psInsertPago.setBigDecimal(3, venta.getTotal());
                psInsertPago.setTimestamp(4, Timestamp.valueOf(venta.getFecha()));

                int rowsPagoInserted = psInsertPago.executeUpdate();
                if (rowsPagoInserted == 0) {
                    throw new SQLException("No se pudo registrar el pago automático de la venta.");
                }
            }

            // 4. Reducir los cupos disponibles del evento
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateEvento)) {
                psUpdate.setInt(1, venta.getCantidad());
                psUpdate.setInt(2, venta.getEventoId());
                int rowsUpdated = psUpdate.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("No se pudo actualizar los cupos del evento.");
                }
            }

            conn.commit(); // Confirmar transacción si todo es exitoso
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir todos los cambios si ocurre cualquier excepción
                } catch (SQLException ex) {
                    System.err.println("Error al realizar rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            System.err.println("Error en transacción de venta y pago: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    if (!conn.isClosed()) {
                        conn.setAutoCommit(true); // Restaurar autoCommit en el bloque finally
                        conn.close(); // Liberar la conexión
                    }
                } catch (SQLException e) {
                    System.err.println("Error al restaurar autoCommit o cerrar conexión: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lista todas las ventas registradas con sus respectivos detalles de cliente y evento.
     *
     * @return Una lista de objetos {@link VentaDetalle} con la información detallada.
     */
    public List<VentaDetalle> listar() {
        List<VentaDetalle> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.fecha, v.cliente_id, v.evento_id, v.cantidad, v.total, v.status, " +
                "c.nombre AS cliente_nombre, c.dni AS cliente_dni, " +
                "e.nombre AS evento_nombre " +
                "FROM dbo.ventas v " +
                "INNER JOIN dbo.clientes c ON v.cliente_id = c.id " +
                "INNER JOIN dbo.eventos e ON v.evento_id = e.id " +
                "ORDER BY v.id DESC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                VentaDetalle detalle = mapearVentaDetalle(rs);
                lista.add(detalle);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar ventas con detalle: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca ventas registradas filtrando por nombre de cliente o nombre de evento.
     *
     * @param texto El criterio de búsqueda.
     * @return Una lista de objetos {@link VentaDetalle} que coinciden con el filtro.
     */
    public List<VentaDetalle> buscarPorClienteOEvento(String texto) {
        List<VentaDetalle> lista = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return listar();
        }

        String sql = "SELECT v.id, v.fecha, v.cliente_id, v.evento_id, v.cantidad, v.total, v.status, " +
                "c.nombre AS cliente_nombre, c.dni AS cliente_dni, " +
                "e.nombre AS evento_nombre " +
                "FROM dbo.ventas v " +
                "INNER JOIN dbo.clientes c ON v.cliente_id = c.id " +
                "INNER JOIN dbo.eventos e ON v.evento_id = e.id " +
                "WHERE c.nombre LIKE ? OR e.nombre LIKE ? " +
                "ORDER BY v.id DESC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String buscarParam = "%" + texto.trim() + "%";
            ps.setString(1, buscarParam);
            ps.setString(2, buscarParam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VentaDetalle detalle = mapearVentaDetalle(rs);
                    lista.add(detalle);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar ventas con detalle: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Helper para mapear una fila de ResultSet a un objeto {@link VentaDetalle}.
     */
    private VentaDetalle mapearVentaDetalle(ResultSet rs) throws SQLException {
        VentaDetalle vd = new VentaDetalle();
        vd.setId(rs.getInt("id"));
        
        Timestamp timestamp = rs.getTimestamp("fecha");
        if (timestamp != null) {
            vd.setFecha(timestamp.toLocalDateTime());
        }
        
        vd.setClienteId(rs.getInt("cliente_id"));
        vd.setEventoId(rs.getInt("evento_id"));
        vd.setCantidad(rs.getInt("cantidad"));
        vd.setTotal(rs.getBigDecimal("total"));
        vd.setStatus(rs.getBoolean("status"));
        vd.setClienteNombre(rs.getString("cliente_nombre"));
        vd.setClienteDni(rs.getString("cliente_dni"));
        vd.setEventoNombre(rs.getString("evento_nombre"));
        return vd;
    }
}
