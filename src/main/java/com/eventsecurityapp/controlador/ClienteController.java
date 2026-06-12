package com.eventsecurityapp.controlador;

import com.eventsecurityapp.modelo.Cliente;
import com.eventsecurityapp.persistencia.ClienteDAO;

import java.util.List;

/**
 * Controlador para la gestión de Clientes.
 * Actúa como intermediario (mediador) entre la capa de interfaz gráfica (Vista)
 * y la capa de acceso a datos (Persistencia).
 */
public class ClienteController {

    private final ClienteDAO clienteDAO;

    /**
     * Constructor del controlador. Inicializa el acceso a datos.
     */
    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Registra un cliente nuevo en el sistema.
     *
     * @param cliente El cliente a registrar.
     * @return true si la inserción fue correcta, false en caso contrario.
     */
    public boolean registrarCliente(Cliente cliente) {
        return clienteDAO.insertar(cliente);
    }

    /**
     * Modifica los datos de un cliente existente.
     *
     * @param cliente El cliente con los datos actualizados.
     * @return true si el cambio se guardó, false de lo contrario.
     */
    public boolean modificarCliente(Cliente cliente) {
        return clienteDAO.actualizar(cliente);
    }

    /**
     * Inactiva lógicamente a un cliente.
     *
     * @param id Identificador único del cliente.
     * @return true si se desactivó correctamente, false de lo contrario.
     */
    public boolean inactivarCliente(int id) {
        return clienteDAO.inactivar(id);
    }

    /**
     * Obtiene todos los clientes registrados.
     *
     * @return Colección List de clientes.
     */
    public List<Cliente> obtenerClientes() {
        return clienteDAO.listar();
    }

    /**
     * Filtra los clientes por una coincidencia en el nombre.
     *
     * @param texto Criterio de búsqueda.
     * @return Colección de clientes filtrados.
     */
    public List<Cliente> buscarClientes(String texto) {
        return clienteDAO.buscarPorNombre(texto);
    }
}
