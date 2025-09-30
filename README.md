# Digital Money House desafio-profesional-backend

¡Bienvenido al proyecto de especialización en Backend! Este repositorio contiene la implementación de una billetera virtual, desarrollada utilizando una arquitectura de microservicios para garantizar la escalabilidad y el mantenimiento.

# 🚀 Arquitectura y Tecnologías
La base de este proyecto es una arquitectura de microservicios, donde cada funcionalidad clave se representa por un servicio pequeño y autónomo. Esto nos permite desarrollar, desplegar y escalar cada componente de manera independiente.

# 💻 Tecnologías 

Lenguaje de Programación: Java 21 

Framework: Spring Boot 3.3.4

Persistencia: Spring Data JPA 

Base de Datos: MySQL 

Descubrimiento de Servicios: Eureka Server 

API Gateway: Spring Cloud Gateway 

Comunicación entre Microservicios: Feign 

Seguridad: Spring Security con JWT

Control de Versiones: Git + GitHub 

Documentación de API: Swagger/OpenAPI 

Pruebas: JUnit 

# 🏗️ Estructura del Proyecto: Microservicios
El proyecto está organizado en los siguientes microservicios, cada uno con una responsabilidad específica:

Eureka Server (Eureka): Actúa como un registro de servicios, permitiendo que los microservicios se encuentren y se comuniquen entre sí.

API Gateway (ApiGateway): Punto de entrada único para todas las solicitudes del cliente. Se encarga del enrutamiento a los servicios correspondientes.

Servicio de Autenticación (Auth Service): Gestiona la autenticación y autorización de usuarios, y genera los tokens de acceso.

Servicio de Usuarios (Users Service): Administra los datos personales de los usuarios. Permite el registro, la consulta y la actualización del perfil.

Servicio de Cuentas (Accounts Service): Gestiona todas las operaciones de las cuentas, con sus transacciones y tarjetas asociadas.

# 🚦 Flujo de Trabajo y Ramas
El flujo de trabajo se gestiona con Git, utilizando un modelo de ramas bien definido:

main: Rama principal para el código en producción.

dev: Rama de desarrollo donde se integra el trabajo de todas las funcionalidades.

test: Rama para las pruebas de integración antes de un despliegue.

# 🎯 Plan de Desarrollo por Sprints
-> Sprint 1: Estructura y Autenticación

Objetivo: Crear la estructura de microservicios y desarrollar el módulo de registro, inicio de sesión y cierre de sesión.

Funcionalidades:

Endpoint para registrar usuarios (POST /users/register).

Endpoint para iniciar sesión (POST /auth/login).

Endpoint para cerrar sesión POST /users/logout).

-> Sprint 2: Dashboard, Perfil y Tarjetas

Objetivo: Desarrollar las funcionalidades iniciales de la Cuenta Digital (Home/Dashboard), la gestión del Perfil de usuario (CVU y Alias) y el ciclo de vida completo (CRUD) para el registro de tarjetas bancarias.

Funcionalidades: 

Endpoint para Consulta de Alias, CVU y datos personales (GET /users/{ID})

Endpoint Consulta de saldo (GET /accounts/{ID}) 

Endpoint para Consulta de últimos movimientos (GET /accounts/{ID}/transactions)

Endpint para Listar tarjetas (GET /accounts/{ID}/cards)

Endpoint para Agregar tarjeta (POST /accounts/{ID}/cards)

Endpoint para eliminar tarjeta (DELETE /accounts/{ID}/cards/{ID tarjeta})

# 👨‍🚀 Postman Collection
En la raíz del proyecto está la colección para importar a postman: digital-money.postman_collection

# 📃 Docuentación en Swagger
Debe estar corriendo Eureka + Gateway + el servicio

user-service
http://localhost:8081/users/swagger-ui/index.html

auth-service
http://localhost:8081/auth/swagger-ui/index.html

account-service
http://localhost:8081/accounts/swagger-ui/index.html

# 🧪 Documentación de casos de prueba
Link: https://docs.google.com/spreadsheets/d/1vFgbRadI4NBE_NcyxIBOFO04Y3TLUwkvEXbB3hmcCmQ/edit?usp=sharing
