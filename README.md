# 🎮 Game Inventory Management - Módulo Games

Este proyecto es una aplicación de gestión de inventario de videojuegos que utiliza una arquitectura **Offline-First** y sincronización híbrida entre múltiples fuentes de datos.

---

## 🏗️ Arquitectura del Sistema
El módulo sigue los principios de **Clean Architecture** y el patrón **MVVM**:

* **UI Layer**: Desarrollada íntegramente en **Jetpack Compose** con un diseño "Deep Black & Gamer Red".
* **Domain/Data Layer**: Implementa el patrón **Repository** para abstraer el origen de los datos (Local vs Remoto).
* **Local Data Source**: Base de datos **Room** que actúa como la "fuente de verdad" única.
* **Remote Data Sources**:
    * **Steam/RAWG API**: Para la importación masiva de catálogos.
    * **MockAPI**: Nube personal para persistencia y sincronización de cambios del usuario.

---

## 🚀 Funcionalidades Clave

### 1. Sincronización Híbrida Inteligente
La aplicación utiliza un sistema de banderas en la base de datos local para gestionar el estado de los juegos:
* **Importación Masiva**: Permite descargar 51 juegos de un solo clic desde la API externa.
* **Marcas de Estado**: Los juegos nuevos se marcan como `pendingSync` (mostrando un icono rojo en la UI) hasta que se suben exitosamente a la nube personal.
* **Lógica Upsert**: El repositorio decide automáticamente si debe crear un juego nuevo (POST) o actualizar uno existente (PUT) al sincronizar.

### 2. Seguridad de Credenciales
Se ha implementado una capa de seguridad mediante **Secrets Gradle Plugin**:
* La **API Key** se almacena en el archivo local `local.properties`.
* El archivo `build.gradle.kts` genera automáticamente la clase `BuildConfig` para inyectar la clave en el código de forma segura, evitando su exposición en repositorios públicos.

---

## 🛠️ Stack Tecnológico
* **Kotlin & Coroutines**: Para una programación asíncrona fluida.
* **Retrofit & Kotlin Serialization**: Gestión de peticiones HTTP y parseo de JSON complejo (como el objeto `results` de RAWG).
* **Room Database**: Persistencia local robusta.
* **Coil 3**: Carga eficiente de imágenes de portadas desde URLs externas.
* **Material Design 3**: Componentes de interfaz modernos y accesibles.

---

## 📦 Instalación y Configuración
1.  Clonar el repositorio.
2.  En la raíz del proyecto, crear o editar el archivo `local.properties`.
3.  Añadir tu clave de API:
    ```properties
    STEAM_API_KEY=tu_clave_aqui
    ```
4.  Sincronizar el proyecto con Gradle y ejecutar en un emulador o dispositivo físico.

---

##
AUTORA: Silvia García Bouza
<img width="595" height="1264" alt="image" src="https://github.com/user-attachments/assets/e054b9fe-92c3-4162-bdb8-1e7c53f9ab35" />
