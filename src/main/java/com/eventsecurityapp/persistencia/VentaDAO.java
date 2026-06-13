package com.eventsecurityapp.persistencia;

import com.eventsecurityapp.modelo.Pago;
import com.eventsecurityapp.modelo.Venta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Venta.
 * Gestiona las operaciones de persistencia y transacciones.
 */
public class VentaDAO {

    /**
     * Registra una venta de entradas mediante una transacción JDBC.
     * Reto Técnico Obligatorio:
     * - Validar cupos disponibles.
     * - Registrar venta.
     * - Registrar pago.
     * - Reducir cupos del evento.
     *
     * @param venta El objeto Venta con los datos.
     * @param pago El objeto Pago asociado.
     * @return true si la transacción fue exitosa, false en caso contrario.
     */
    public boolean registrarVenta(Venta venta, Pago pago) {
        if (venta == null || pago == null) {
            return false;
        }

        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Validar y reducir cupos disponibles
            String updateEventoSql = "UPDATE dbo.eventos SET cupos_disponibles = cupos_disponibles - ? WHERE id = ? AND cupos_disponibles >= ?";
            try (PreparedStatement psEvento = conn.prepareStatement(updateEventoSql)) {
                psEvento.setInt(1, venta.getCantidad());
                psEvento.setInt(2, venta.getEventoId());
                psEvento.setInt(3, venta.getCantidad());
                
                int rowsAffected = psEvento.executeUpdate();
                if (rowsAffected == 0) {
                    // No hay cupos suficientes o evento no existe
                    conn.rollback();
                    return false;
                }
            }

            // 2. Registrar la venta
            String insertVentaSql = "INSERT INTO dbo.ventas (fecha, cliente_id, evento_id, cantidad, total, status) VALUES (?, ?, ?, ?, ?, ?)";
            int ventaId = 0;
            try (PreparedStatement psVenta = conn.prepareStatement(insertVentaSql, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setTimestamp(1, Timestamp.valueOf(venta.getFecha()));
                psVenta.setInt(2, venta.getClienteId());
                psVenta.setInt(3, venta.getEventoId());
                psVenta.setInt(4, venta.getCantidad());
                psVenta.setBigDecimal(5, venta.getTotal());
                psVenta.setBoolean(6, venta.isStatus());

                psVenta.executeUpdate();
                
                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        ventaId = rs.getInt(1);
                        venta.setId(ventaId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 3. Registrar el pago asociado
            String insertPagoSql = "INSERT INTO dbo.pagos (venta_id, metodo_pago, monto, fecha) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psPago = conn.prepareStatement(insertPagoSql)) {
                psPago.setInt(1, ventaId);
                psPago.setString(2, pago.getMetodoPago());
                psPago.setBigDecimal(3, pago.getMonto());
                psPago.setTimestamp(4, Timestamp.valueOf(pago.getFecha()));
                
                psPago.executeUpdate();
            }

            // 4. Confirmar transacción
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar la venta: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al revertir la transacción: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error al restablecer auto-commit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Lista todas las ventas registradas.
     *
     * @return Una lista de objetos Venta.
     */
    public List<Venta> listar() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT id, fecha, cliente_id, evento_id, cantidad, total, status FROM dbo.ventas";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venta venta = mapearVenta(rs);
                lista.add(venta);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca ventas relacionando con el nombre del cliente o del evento.
     *
     * @param texto El texto de búsqueda.
     * @return Lista de ventas encontradas.
     */
    public List<Venta> buscar(String texto) {
        List<Venta> lista = new ArrayList<>();
        if (texto == null) {
            return lista;
        }

        String sql = "SELECT v.id, v.fecha, v.cliente_id, v.evento_id, v.cantidad, v.total, v.status " +
                     "FROM dbo.ventas v " +
                     "INNER JOIN dbo.clientes c ON v.cliente_id = c.id " +
                     "INNER JOIN dbo.eventos e ON v.evento_id = e.id " +
                     "WHERE c.nombre LIKE ? OR e.nombre LIKE ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String likeParam = "%" + texto + "%";
            ps.setString(1, likeParam);
            ps.setString(2, likeParam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Venta venta = mapearVenta(rs);
                    lista.add(venta);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar ventas: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getInt("id"));
        
        Timestamp timestamp = rs.getTimestamp("fecha");
        if (timestamp != null) {
            v.setFecha(timestamp.toLocalDateTime());
        }
        
        v.setClienteId(rs.getInt("cliente_id"));
        v.setEventoId(rs.getInt("evento_id"));
        v.setCantidad(rs.getInt("cantidad"));
        v.setTotal(rs.getBigDecimal("total"));
        v.setStatus(rs.getBoolean("status"));
        return v;
    }
}
