package com.eventsecurityapp.persistencia;

import com.eventsecurityapp.modelo.Pago;
import com.eventsecurityapp.modelo.PagoDetalle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la gestión de persistencia de la entidad Pago.
 * Administra las operaciones CRUD en la tabla 'pagos' de la base de datos SQL Server
 * a través de JDBC y ConnectionManager.
 */
public class PagoDAO {

    /**
     * Inserta un nuevo registro de pago en la base de datos.
     * Recupera el ID autogenerado y lo asigna al objeto Pago.
     *
     * @param pago El objeto Pago a insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertar(Pago pago) {
        if (pago == null) {
            return false;
        }

        String sql = "INSERT INTO dbo.pagos (venta_id, metodo_pago, monto, fecha) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pago.getVentaId());
            ps.setString(2, pago.getMetodoPago());
            ps.setBigDecimal(3, pago.getMonto());
            ps.setTimestamp(4, Timestamp.valueOf(pago.getFecha()));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pago.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar pago: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lista todos los pagos registrados con detalles descriptivos de cliente y evento.
     *
     * @return Lista de objetos {@link PagoDetalle}.
     */
    public List<PagoDetalle> listar() {
        List<PagoDetalle> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.venta_id, p.metodo_pago, p.monto, p.fecha, " +
                "c.nombre AS cliente_nombre, " +
                "e.nombre AS evento_nombre " +
                "FROM dbo.pagos p " +
                "INNER JOIN dbo.ventas v ON p.venta_id = v.id " +
                "INNER JOIN dbo.clientes c ON v.cliente_id = c.id " +
                "INNER JOIN dbo.eventos e ON v.evento_id = e.id " +
                "ORDER BY p.id DESC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PagoDetalle detalle = mapearPagoDetalle(rs);
                lista.add(detalle);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar pagos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca y filtra los pagos registrados por cliente, evento o método de pago.
     *
     * @param texto Criterio de búsqueda.
     * @return Lista de objetos {@link PagoDetalle} que coinciden con el filtro.
     */
    public List<PagoDetalle> buscarPorClienteOEventoOMetodo(String texto) {
        List<PagoDetalle> lista = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return listar();
        }

        String sql = "SELECT p.id, p.venta_id, p.metodo_pago, p.monto, p.fecha, " +
                "c.nombre AS cliente_nombre, " +
                "e.nombre AS evento_nombre " +
                "FROM dbo.pagos p " +
                "INNER JOIN dbo.ventas v ON p.venta_id = v.id " +
                "INNER JOIN dbo.clientes c ON v.cliente_id = c.id " +
                "INNER JOIN dbo.eventos e ON v.evento_id = e.id " +
                "WHERE c.nombre LIKE ? OR e.nombre LIKE ? OR p.metodo_pago LIKE ? " +
                "ORDER BY p.id DESC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String buscarParam = "%" + texto.trim() + "%";
            ps.setString(1, buscarParam);
            ps.setString(2, buscarParam);
            ps.setString(3, buscarParam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PagoDetalle detalle = mapearPagoDetalle(rs);
                    lista.add(detalle);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar pagos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Helper para mapear una fila de ResultSet a un objeto {@link PagoDetalle}.
     */
    private PagoDetalle mapearPagoDetalle(ResultSet rs) throws SQLException {
        PagoDetalle pd = new PagoDetalle();
        pd.setId(rs.getInt("id"));
        pd.setVentaId(rs.getInt("venta_id"));
        pd.setMetodoPago(rs.getString("metodo_pago"));
        pd.setMonto(rs.getBigDecimal("monto"));
        
        Timestamp timestamp = rs.getTimestamp("fecha");
        if (timestamp != null) {
            pd.setFecha(timestamp.toLocalDateTime());
        }

        pd.setClienteNombre(rs.getString("cliente_nombre"));
        pd.setEventoNombre(rs.getString("evento_nombre"));
        return pd;
    }
}
