package com.eventsecurityapp.persistencia;

import com.eventsecurityapp.modelo.User;
import com.eventsecurityapp.utils.PasswordHash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) para la entidad User.
 * Gestiona las operaciones de persistencia en la tabla 'users' de SQL Server.
 */
public class UserDAO {

    /**
     * Autentica a un usuario verificando su email y contraseña.
     * Solo permite la autenticación si el usuario está activo (status = 1).
     *
     * @param email    Correo electrónico del usuario.
     * @param password Contraseña en texto plano a verificar.
     * @return true si las credenciales son válidas y el usuario está activo, false en caso contrario.
     * @throws RuntimeException si las credenciales son inválidas, el usuario no existe o está inactivo.
     */
    public boolean autenticar(String email, String password) {
        if (email == null || password == null) {
            return false;
        }

        String sql = "SELECT password, status FROM dbo.users WHERE LTRIM(RTRIM(email)) = LTRIM(RTRIM(?))";
        String passwordHash = PasswordHash.hash(password);

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean status = rs.getBoolean("status");
                    String dbPasswordHash = rs.getString("password");

                    if (dbPasswordHash != null) {
                        dbPasswordHash = dbPasswordHash.trim();
                    }

                    // 3. Imprimir mensajes de depuración en consola según formato exacto
                    System.out.println("Email ingresado: " + email);
                    System.out.println("Password ingresada: " + password);
                    System.out.println("Hash generado: " + passwordHash);
                    System.out.println("Hash almacenado: " + dbPasswordHash);
                    System.out.println("Status: " + status);

                    // 5. Si el hash no coincide, imprimir "[DEBUG] Contraseña incorrecta." y lanzar excepción
                    if (dbPasswordHash == null || !dbPasswordHash.equals(passwordHash)) {
                        System.out.println("[DEBUG] Contraseña incorrecta.");
                        throw new RuntimeException("Contraseña incorrecta.");
                    }

                    // 6. Si el usuario está inactivo, imprimir "[DEBUG] Usuario inactivo." y lanzar excepción
                    if (!status) {
                        System.out.println("[DEBUG] Usuario inactivo.");
                        throw new RuntimeException("Usuario inactivo.");
                    }

                    return true;
                } else {
                    // Si el usuario no fue encontrado, se imprimen los valores de entrada y null en el resto
                    System.out.println("Email ingresado: " + email);
                    System.out.println("Password ingresada: " + password);
                    System.out.println("Hash generado: " + passwordHash);
                    System.out.println("Hash almacenado: null");
                    System.out.println("Status: false");

                    // 4. Si el usuario no existe, imprimir "[DEBUG] Usuario no encontrado." y lanzar excepción
                    System.out.println("[DEBUG] Usuario no encontrado.");
                    throw new RuntimeException("Usuario no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en la conexión con la base de datos.");
        }
    }

    /**
     * Obtiene los datos de un usuario mediante su correo electrónico.
     *
     * @param email Correo electrónico a buscar.
     * @return El objeto User correspondiente o null si no se encuentra o hay un error.
     */
    public User obtenerPorEmail(String email) {
        if (email == null) {
            return null;
        }

        String sql = "SELECT id, nombre, email, password, status FROM dbo.users WHERE LTRIM(RTRIM(email)) = LTRIM(RTRIM(?))";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNombre(rs.getString("nombre"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setStatus(rs.getBoolean("status"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Actualiza la contraseña de un usuario en la base de datos.
     * Cifra la nueva contraseña utilizando SHA-256 antes de guardarla.
     *
     * @param userId        Identificador del usuario.
     * @param nuevaPassword Nueva contraseña en texto plano.
     * @return true si la contraseña se actualizó correctamente, false en caso contrario.
     */
    public boolean cambiarPassword(int userId, String nuevaPassword) {
        if (nuevaPassword == null) {
            return false;
        }

        String sql = "UPDATE dbo.users SET password = ? WHERE id = ?";
        String nuevaPasswordHash = PasswordHash.hash(nuevaPassword);

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevaPasswordHash);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña del usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
