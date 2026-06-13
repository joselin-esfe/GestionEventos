package com.eventsecurityapp.persistencia;

import com.eventsecurityapp.modelo.Pago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Pago.
 * Permite registrar y consultar pagos en la base de datos.
 */
public class PagoDAO {

    /**
     * Registra un nuevo pago en la base de datos.
     *
     * @param pago El objeto Pago a registrar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertar(Pago pago) {
        if (pago == null) {
            return false;
        }
        String sql = "INSERT INTO dbo.pagos (venta_id, metodo_pago, monto, fecha) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pago.getVentaId());
            ps.setString(2, pago.getMetodoPago());
            ps.setBigDecimal(3, pago.getMonto());
            ps.setTimestamp(4, Timestamp.valueOf(pago.getFecha()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar pago: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Consulta todos los pagos registrados.
     *
     * @return Una lista de objetos Pago.
     */
    public List<Pago> listar() {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT id, venta_id, metodo_pago, monto, fecha FROM dbo.pagos";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pago p = new Pago();
                p.setId(rs.getInt("id"));
                p.setVentaId(rs.getInt("venta_id"));
                p.setMetodoPago(rs.getString("metodo_pago"));
                p.setMonto(rs.getBigDecimal("monto"));
                p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pagos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
