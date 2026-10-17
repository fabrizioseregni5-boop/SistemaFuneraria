# Sistema de Gestión - Funeraria Mi Redentor 🏢

Este es un sistema de escritorio desarrollado en **Java** para la gestión administrativa y facturación de la funeraria "Mi Redentor". El sistema permite administrar clientes, familiares asociados a seguros, inventario de productos y la emisión de facturas con generación de PDF.

## 📸 Capturas de Pantalla

*(Reemplaza los enlaces con las fotos de tu sistema)*
*Aquí puedes subir fotos de tu menú principal y tu PDF.*
<img width="831" height="640" alt="image" src="https://github.com/user-attachments/assets/2393c914-fa90-44cf-afeb-f17eb3dc9d98" />
<img width="932" height="567" alt="image" src="https://github.com/user-attachments/assets/b17ca50d-e0aa-42e0-9798-4b1444dd5030" />
<img width="962" height="661" alt="image" src="https://github.com/user-attachments/assets/71e49fff-5409-4485-9951-992ab078e62e" />
<img width="934" height="580" alt="image" src="https://github.com/user-attachments/assets/75238c1c-4679-4608-804d-304c58226bcc" />
<img width="936" height="570" alt="image" src="https://github.com/user-attachments/assets/110d6da3-e420-471a-adcc-2d58a80da76c" />
<img width="1130" height="688" alt="image" src="https://github.com/user-attachments/assets/dbcae26d-a1c2-43bf-a6ee-66264bd50744" />
<img width="591" height="555" alt="image" src="https://github.com/user-attachments/assets/e8b5a93f-4f12-4044-97bc-db291f13e733" />


## ✨ Características Principales

*   **Gestión de Clientes:** Registro, edición y búsqueda de titulares.
*   **Gestión de Familiares:** Asociación de beneficiarios/fallecidos al seguro del titular.
*   **Control de Inventario:** Administración de stock y precios de productos (urnas, café, etc.).
*   **Gestión de Proveedores:** Registro de aseguradoras y convenios.
*   **Módulo de Facturación:** 
    *   Generación automática de números de factura (correlativo desde la base de datos).
    *   Cálculo automático de IVA y subtotales.
    *   Exportación a PDF con diseño elegante, inclusión de logo corporativo y espacio para firmas.

## 🛠️ Tecnologías y Arquitectura

*   **Lenguaje:** Java (JDK 21)
*   **Interfaz Gráfica:** Java Swing (Diseño moderno basado en tarjetas)
*   **Base de Datos:** SQLite (Persistencia local con JDBC)
*   **Generación de PDF:** Librería OpenPDF
*   **Arquitectura:** Patrón de diseño **MVC** (Modelo - Vista - Controlador)

## 📂 Estructura del Proyecto

*   `src/modelo/`: Clases DAO para la conexión y consultas a la base de datos (SQLite).
*   `src/controlador/`: Clases que manejan la lógica entre la vista y el modelo.
*   `src/vista/`: Interfaces gráficas (JFrame) y componentes de Swing.
*   `lib/`: Librerías externas necesarias (OpenPDF y SQLite JDBC).

## 🚀 Cómo ejecutar el proyecto

1. Clona este repositorio o descárgalo como ZIP.
2. Abre **Eclipse IDE**.
3. Ve a `File -> Import -> General -> Existing Projects into Workspace`.
4. Selecciona la carpeta del proyecto. Eclipse cargará las librerías de la carpeta `lib` automáticamente.
5. Ejecuta la clase `MenuPrincipal.java` (ubicada en `src/vista/`).

*(Nota: El sistema requiere tener instalado Java 21 o superior).*

## 👨‍💻 Autor

*   **[Tu Nombre Completo]**
*   Estudiante de [Nombre de la Carrera / Universidad]
