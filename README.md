# ShoppingListCyE – Lista de la compra con Jetpack Compose

## Descripción del proyecto:
**ShoppingListCyE** es una aplicación Android desarrollada con **Jetpack Compose** que permite gestionar una lista de la compra de forma sencilla e intuitiva.  
El usuario puede añadir productos, marcarlos como comprados, eliminarlos y filtrar la lista según su estado, todo ello con una interfaz clara y cuidada.

**Integrantes del grupo:** Carlos Lara y Estela Martos.

---

## Objetivos:
- Aplicar los conceptos fundamentales de **Jetpack Compose**.
- Diseñar una interfaz moderna siguiendo **Material Design 3**.
- Gestionar correctamente el estado y la recomposición.
- Implementar navegación entre pantallas.
- Crear una aplicación funcional y bien estructurada.
- Proporcionar feedback visual al usuario mediante componentes de interfaz.

---

## Funcionalidades:
La aplicación cumple todos los requisitos de la opción **ShoppingList – Lista de la compra**:

- Visualización de una lista de productos (usando lazy column).
- Añadir nuevos productos (con float button).
- Marcar productos como comprados.
- Eliminar productos.
- Filtro de productos:
  - Todos.
  - Pendientes.
  - Comprados.
- Contador de productos pendientes visible en pantalla.
- Diálogo de confirmación antes de eliminar un producto.
- Visualización de una **Snackbar** al añadir un producto, con opción de **deshacer** la acción.

---

## Pantallas de la aplicación:

### 1️ Pantalla principal – Lista de productos:
Incluye:
- **TopAppBar** con el título de la aplicación.
- **LazyColumn** para mostrar la lista de productos.
- Cada producto muestra:
  - Nombre.
  - Checkbox para marcar como comprado.
  - Icono para eliminar.
- **FloatingActionButton** para añadir nuevos productos.
- Botones segmentados para filtrar la lista.
- Contador de productos pendientes.
- **Snackbar** informativa tras añadir un producto.

### 2 Pantalla de añadir producto:
Incluye:
- Campo de texto para introducir el nombre del producto.
- Botón **Guardar**.
- Botón **Cancelar**.
- Al guardar:
  - Se añade el producto a la lista.
  - Se vuelve a la pantalla principal, donde se muestra la Snackbar de confirmación.

---

## Aspectos técnicos:

### Jetpack Compose:
- Uso exclusivo de **Jetpack Compose** (sin XML para interfaces).
- Uso de:
  - `@Composable`.
  - `remember` y `mutableStateOf`.
  - `Scaffold`.
  - Componentes **Material 3**.

### Navegación:
- Implementación con **Navigation Compose**.
- Dos destinos:
  - Pantalla de lista de productos.
  - Pantalla de añadir producto.

### Gestión del estado:
- La lista de productos se gestiona mediante `mutableStateListOf`.
- La interfaz se recompone automáticamente al modificar el estado.
- Filtros y contador se actualizan en tiempo real.
- El estado de la Snackbar se gestiona de forma reactiva.

---

## Decisiones de diseño:
- Uso de una paleta de colores **suaves y claros** para mejorar la experiencia visual.
- Fondo con imagen y Scaffold transparente para dar sensación de profundidad.
- Colores definidos en `colors.xml` para facilitar el mantenimiento.
- Uso de **MaterialTheme** personalizado.
- Interfaz sencilla y clara, priorizando la usabilidad y el feedback inmediato.

---
