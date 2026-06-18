# Plan de Implementación - EventSecurityApp

Este documento describe la estrategia de desarrollo, arquitectura y fases del proyecto para completar las funcionalidades de **EventSecurityApp** de acuerdo con los requerimientos establecidos.

---

## Fase 1: Resolución de Conflictos e Infraestructura Base
* **1.1. Limpieza de Conflictos**: Resolver los marcadores de conflicto de Git en `README.md` para estabilizar la documentación del proyecto.
* **1.2. Verificación de la Base de Datos**: Ejecutar `create_database.sql` en SQL Server y comprobar la conectividad del componente `ConnectionManager.java`.

## Fase 2: Implementación de la Capa de Datos (DAO)
Se crearán las clases encargadas de la persistencia para las entidades que aún no lo tienen:

1. **[VentaDAO.java](file:///c:/Users/LG/Eventos/src/main/java/com/eventsecurityapp/persistencia/VentaDAO.java)**:
   * Método para registrar una venta en la tabla `ventas`.
   * Lógica transaccional para descontar `cupos_disponibles` de la tabla `eventos` de forma segura.
   * Método para listar ventas y buscar por cliente/evento.
2. **[PagoDAO.java](file:///c:/Users/LG/Eventos/src/main/java/com/eventsecurityapp/persistencia/PagoDAO.java)**:
   * Método para insertar un pago asociado a una venta.
3. **[AsistenciaDAO.java](file:///c:/Users/LG/Eventos/src/main/java/com/eventsecurityapp/persistencia/AsistenciaDAO.java)**:
   * Método para registrar el ingreso a un evento en la tabla `asistencias`.
   * Consulta para verificar si un ID de venta ya registró asistencia previa.

## Fase 3: Controladores (MVC)
Separar la lógica de negocio y mediación:
* **[EventoController.java](file:///c:/Users/LG/Eventos/src/main/java/com/eventsecurityapp/controlador/EventoController.java)**: Desacoplar la vista de eventos de su DAO.
* **[VentaController.java](file:///c:/Users/LG/Eventos/src/main/java/com/eventsecurityapp/controlador/VentaController.java)**: Coordinar el registro transaccional de venta y pago.
* **[AsistenciaController.java](file:///c:/Users/LG/Eventos/src/main/java/com/eventsecurityapp/controlador/AsistenciaController.java)**: Gestionar validación de boletos y registro de entradas.

## Fase 4: Interfaces Gráficas Premium (Java Swing)
Desarrollo de las interfaces visuales pendientes bajo una estética premium coherente:

1. **[DashboardForm.java](file:///c:/Users/LG/Eventos/src/main/java/com/eventsecurityapp/vista/DashboardForm.java)** (Menú Principal):
   * Contenedor principal para navegar entre módulos: Eventos, Clientes, Ventas y Seguridad.
   * Diseño pulido con colores modernos y panel de estadísticas breves en tiempo real.
2. **[VentaWriteForm.java](file:///c:/Users/LG/Eventos/src/main/java/com/eventsecurityapp/vista/VentaWriteForm.java)** (Registro de Ventas):
   * Selección amigable de Clientes y Eventos usando `JComboBox` interactivos.
   * Campos de cantidad y selección de método de pago (Efectivo, Tarjeta, Transferencia).
3. **[AsistenciaForm.java](file:///c:/Users/LG/Eventos/src/main/java/com/eventsecurityapp/vista/AsistenciaForm.java)** (Módulo de Seguridad):
   * Campo de entrada para el ID de Boleto/Venta.
   * Indicador visual grande de estado: **VERDE** (Autorizado) / **ROJO** (Denegado).

## Fase 5: Pruebas y Validación
* **Pruebas Unitarias**: Configurar JUnit 5 para probar las operaciones DAO en modo transaccional seguro.
* **Pruebas de Integración Manuales**: Ejecutar la aplicación para verificar los flujos de login, venta con decremento de cupo, y control de doble asistencia en la puerta de seguridad.
