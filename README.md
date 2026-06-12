# EventSecurityApp

EventSecurityApp es una aplicación de escritorio diseñada para la gestión y control de seguridad en eventos, implementada utilizando **Java Swing** y estructurada mediante **Maven**.

## Requisitos Previos

Para ejecutar y compilar este proyecto, necesitas:

*   **Java Development Kit (JDK) 21** o superior.
*   **Apache Maven 3.9.x** o superior (opcional, o importado a través del IDE).
*   **IntelliJ IDEA** (u otro IDE compatible con Java y Maven).

## Estructura del Proyecto

El código fuente se organiza bajo el paquete raíz `com.eventsecurityapp` con los siguientes subpaquetes:

*   **`controlador`**: Clases controladoras para mediar la comunicación entre las vistas y los modelos.
*   **`modelo`**: Entidades del dominio y lógica de negocio.
*   **`persistencia`**: Clases encargadas de gestionar la base de datos o almacenamiento de información.
*   **`utils`**: Utilidades y clases auxiliares reutilizables.
*   **`vista`**: Interfaces gráficas de usuario desarrolladas en Swing.
*   **`enums`**: Enumeraciones para tipar valores constantes del dominio.

## Cómo Abrir y Compilar en IntelliJ IDEA

1.  Abre IntelliJ IDEA.
2.  Selecciona **Open** (Abrir) y navega hasta el directorio raíz del proyecto (`c:\Users\LG\Eventos`).
3.  Selecciona el archivo `pom.xml` y haz clic en **Open as Project** (Abrir como Proyecto).
4.  IntelliJ resolverá automáticamente las dependencias de Maven y configurará el proyecto.
5.  Asegúrate de que la versión del SDK del proyecto esté configurada en Java 21:
    *   Ve a `File` -> `Project Structure` -> `Project`.
    *   Establece **SDK** y **Language Level** en `21`.
6.  Para compilar desde la terminal o el panel de Maven de IntelliJ, ejecuta:
    ```bash
    mvn clean compile
    ```
7.  Para iniciar la aplicación, ejecuta la clase principal `com.eventsecurityapp.Main`.
