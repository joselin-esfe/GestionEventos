package com.eventsecurityapp.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Administrador de conexiones a la base de datos SQL Server.
 * Implementa el patrón de diseño Singleton para asegurar una única instancia
 * de conexión activa o un único punto de acceso a la base de datos.
 */
public class ConnectionManager {

    private static ConnectionManager instance;
    private Connection connection;

    // Configuración por defecto para SQL Server
    private static final String URL = "jdbc:sqlserver://localhost;" +
            "instanceName=SQLEXPRESS;" +
            "databaseName=EventSecurityDB;" +
            "integratedSecurity=false;" +
            "encrypt=true;" +
            "trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "Eventos2026*";

    /**
     * Constructor privado para evitar la instanciación externa.
     */
    private ConnectionManager() {
        // Cargar el driver explícitamente (opcional en JDBC 4+)
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC de SQL Server.");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la única instancia del ConnectionManager.
     *
     * @return Instancia única de ConnectionManager.
     */
    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    /**
     * Obtiene una conexión activa a la base de datos SQL Server.
     * Si la conexión actual es nula o está cerrada, crea una nueva.
     *
     * @return Objeto Connection activo.
     * @throws SQLException si ocurre un error en la conexión.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /**
     * Cierra la conexión actual a la base de datos si se encuentra abierta.
     */
    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión de la base de datos.");
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }
}
