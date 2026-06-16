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

    // Configuración por defecto para SQL Server
    private static final String URL = "jdbc:sqlserver://DESKTOP-8MR9C2K:1433;" +
            "databaseName=EventSecurityDB;" +
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
     * Obtiene una nueva conexión a la base de datos SQL Server.
     * Cada llamada crea una conexión independiente para evitar conflictos
     * de concurrencia entre hilos (SwingWorker) y el hilo de despacho de eventos (EDT).
     * El llamador es responsable de cerrar la conexión después de usarla
     * (idealmente con try-with-resources).
     *
     * @return Objeto Connection nuevo y activo.
     * @throws SQLException si ocurre un error en la conexión.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
