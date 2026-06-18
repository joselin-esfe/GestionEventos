package com.eventsecurityapp.persistencia;

import com.eventsecurityapp.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Cliente.
 * Gestiona las operaciones de persistencia en la tabla 'clientes' de SQL Server.
 */
public class ClienteDAO {

    /**
     * Inserta un nuevo cliente en la base de datos.
     *
     * @param cliente El objeto Cliente a guardar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertar(Cliente cliente) {
        if (cliente == null) {
            return false;
        }

        String sql = "INSERT INTO dbo.clientes (dni, nombre, email, telefono, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getDni());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefono());
            ps.setBoolean(5, cliente.isStatus());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente El objeto Cliente con datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizar(Cliente cliente) {
        if (cliente == null) {
            return false;
        }

        String sql = "UPDATE dbo.clientes SET dni = ?, nombre = ?, email = ?, telefono = ?, status = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getDni());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefono());
            ps.setBoolean(5, cliente.isStatus());
            ps.setInt(6, cliente.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inactiva lógicamente un cliente cambiando su estado a inactivo (status = 0).
     *
     * @param id El identificador único del cliente.
     * @return true si la inactivación fue exitosa, false en caso contrario.
     */
    public boolean inactivar(int id) {
        String sql = "UPDATE dbo.clientes SET status = 0 WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al inactivar cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lista todos los clientes registrados.
     *
     * @return Una lista de objetos Cliente.
     */
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, dni, nombre, email, telefono, status FROM dbo.clientes";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = mapearCliente(rs);
                lista.add(cliente);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca clientes cuyo nombre coincida o contenga el texto buscado.
     *
     * @param texto El texto a buscar en el nombre del cliente.
     * @return Una lista de clientes que coinciden con el filtro.
     */
    public List<Cliente> buscarPorNombre(String texto) {
        List<Cliente> lista = new ArrayList<>();
        if (texto == null) {
            return lista;
        }

        String sql = "SELECT id, dni, nombre, email, telefono, status FROM dbo.clientes WHERE nombre LIKE ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = mapearCliente(rs);
                    lista.add(cliente);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Helper para mapear una fila del ResultSet a un objeto Cliente.
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setDni(rs.getString("dni"));
        c.setNombre(rs.getString("nombre"));
        c.setEmail(rs.getString("email"));
        c.setTelefono(rs.getString("telefono"));
        c.setStatus(rs.getBoolean("status"));
        return c;
    }
}
