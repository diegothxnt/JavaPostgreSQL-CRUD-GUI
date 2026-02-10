# CRUDApp: Interfaz Dinámica para PostgreSQL
Este proyecto es una aplicación de escritorio desarrollada en Java Swing que permite la gestión de datos mediante una interfaz gráfica adaptable. A diferencia de aplicaciones estáticas, este sistema utiliza metadatos de la base de datos para generar formularios y tablas de manera dinámica, permitiendo interactuar con cualquier tabla de PostgreSQL sin modificar el código fuente.

## Funcionalidades Principales

- Generación Dinámica de Interfaz: El sistema consulta la estructura de la tabla en tiempo real para crear campos de entrada y columnas según el esquema de la base de datos.

- Gestión de Tipos de Datos: Manejo preciso de valores numéricos mediante BigDecimal para evitar errores de redondeo en precios y validación automática de campos enteros.

## Operaciones CRUD:

- Crear: Formulario inteligente que ignora automáticamente campos autoincrementales (SERIAL) y fechas de sistema.

- Leer: Motor de búsqueda con soporte para filtros de texto y casting numérico.

- Actualizar: Edición basada en la selección de registros con precarga de datos.

- Eliminar: Borrado seguro de registros con confirmación de usuario.

- Diseño Intuitivo: Código de colores en botones para mejorar la experiencia de usuario y facilitar la identificación de acciones.

## Estructura del Proyecto
Para que la aplicación funcione correctamente, se recomienda organizar los archivos de la siguiente manera:

/ <br>
├── bin/                       # Archivos de clases compiladas <br>
└── src/                       # Código fuente y librerías <br>
    ├── CRUDApp.java           # Clase principal <br>
    └── postgresql-42.7.9.jar  # Driver JDBC de PostgreSQL <br>

## Guía de Instalación y Ejecución
Siga estas instrucciones desde su terminal para preparar el entorno y ejecutar la aplicación.

### Compilación
Utilice el siguiente comando para compilar el código fuente:    

`javac -cp "src/postgresql-42.7.9.jar" -d bin src/CRUDApp.java`

### Ejecución
Una vez finalizada la compilación, puede iniciar la aplicación con el siguiente comando:

`java -cp "bin;src/postgresql-42.7.9.jar" CRUDApp`

### Integrantes:

- Diego Rojas, C.I: 31326600
- Samer Ghattas, C.I: 31887714
