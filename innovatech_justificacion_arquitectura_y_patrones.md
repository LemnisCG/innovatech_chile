# Justificación de Arquitectura y Guía de Patrones - Innovatech Solutions

Este documento tiene como objetivo explicar las decisiones tomadas en el diseño inicial de la arquitectura para el caso Innovatech Solutions, así como proporcionar una guía de investigación comparativa de las arquitecturas implementadas frente a sus alternativas.

---

## 1. Justificación de los Microservicios Propuestos

En la propuesta inicial se definieron estratégicamente **dos microservicios core** (Project y Resource) junto con un **API Gateway** y un **BFF**. 

### ¿Por qué no un microservicio dedicado a Monitoreo y Analítica?
En arquitecturas iniciales, el monitoreo a menudo consiste en leer información estructurada desde otros servicios. Proponer un microservicio puro de "Analítica" requeriría de entrada lidiar con arquitecturas complejas de sincronización de datos o eventos (ej. Kafka) para mantener una copia de los datos de proyectos y recursos. En su lugar, utilizamos el **BFF (Backend for Frontend)** para interceptar las peticiones del dashboard en el UI, recolectar datos en paralelo desde `ProjectService` y `ResourceService`, y consolidarlos al vuelo para mostrarlos en pantalla. Según el requerimiento del caso de estudio, se requiere *un BFF y dos microservicios*, encajando perfectamente este patrón (el BFF asume el rol agregador de la analítica en tiempo real). Si a futuro el volumen crece masivamente, el BFF daría paso a una base de consultas tipo *Data Warehouse*.

### ¿Por qué no un microservicio dedicado a la Creación de Usuarios (IAM / Auth Service)?
Históricamente, los sistemas solían tener un "User Service", sin embargo, delegar la creación de credenciales, inicios de sesión y autenticación a la capa de dominio es una mala práctica moderna. Actualmente, ese problema se absorbe en el nivel de infraestructura perimetral. Es común utilizar un *Identity Provider* (Ej. Keycloak, AWS Cognito, Auth0) integrado directamente con el **API Gateway**. El Gateway valida si el usuario es válido, y luego el `Resource Service` solo manipula su "perfil profesional corporativo" de acuerdo al token, no su contraseña o inicio de sesión nativo. Por simpleza metodológica de este caso de estudio, evitamos diseñar la gestión de cuentas e identidades desde rasguños, asumiéndolo parte del perímetro.

---

## 2. Guía de Investigación de Patrones y su Competencia

A continuación, presento los patrones utilizados, qué debes buscar para entenderlos, y cuál es la "competencia" directa para que analices sus pro y contras.

### A. API Gateway
*   **¿De qué trata?** Es el único punto de entrada público para tus clientes. Enruta llamadas al microservicio correcto, valida tokens (seguridad) y provee protección (Rate Limit).
*   **Temas a investigar:** `API Gateway Pattern Microservices`, `Spring Cloud Gateway`.
*   **La Competencia Alternativa:** 
    *   **Direct Client-to-Microservice:** El frontend llama directo a cada microservicio. (Malo para seguridad y acopla al cliente interno).
    *   **Service Mesh (ej. Istio):** En sistemas enormemente complejos y masivos con microservicios escritos en cientos de lenguajes, Service Mesh maneja la red entre ellos de forma más declarativa que un Gateway único en el borde, delegando validaciones a sidecars junto a cada servicio.

### B. Backend For Frontend (BFF)
*   **¿De qué trata?** En sistemas grandes, el Frontend de Web requiere datos diferentes al Frontend Móvil. El BFF es un servidor a medida para un cliente específico (ej. un BFF solo para el Dashboard Web), que junta y formatea solo los datos necesarios para evitar que el navegador del usuario final deba hacer cálculos pesados o 10 llamadas distintas asíncronas.
*   **Temas a investigar:** `BFF Pattern (Backend for Frontend) Sam Newman`, `API Composition`.
*   **La Competencia Alternativa:**
    *   **GraphQL:** GraphQL permite exponer una única API muy maleable donde el *cliente web* pregunta exactamente la relación de entidades que requiere. A menudo, GraphQL *reemplaza* la necesidad estricta de construir BFFs individualizados manuales por cada necesidad porque el frontend configura la consulta a demanda.
    *   **API Gateway Universal:** Usar el mismo API gateway para tratar de agrupar todo, volviéndolo un cuello de botella lleno de lógica de UI, un antipatrón conocido.

### C. Database per Service
*   **¿De qué trata?** Exige que la tabla de proyectos esté aislada de la tabla de profesionales. Cada microservicio es dueño absoluto de sus datos, garantizando aislamiento si un servicio cae.
*   **Temas a investigar:** `Database per service pattern`, `Microservices data consistency Saga Pattern`.
*   **La Competencia Alternativa:**
    *   **Shared Database (BBDD Compartida):** Todos los microservicios apuntan a la misma gran base MySQL clásica. Al principio es ultra productiva, las uniones `JOIN` son sencillas, pero destruye la promesa de escalar sistemas verticalmente ante gran congestión, siendo el punto de fallo único organizativo.

### D. Patrones de Diseño de Código (A implementar en etapa de código)
*   **Temas a investigar:** 
    *   `Repository Pattern C# / Java`: Separa la lógica de la base de datos de la lógica de negocio.
    *   `Circuit Breaker Pattern (Resilience4j)`: La competencia directa no existe como tal, pero busca evitar llamadas estancadas a un servicio muerto para no encolar el sistema e interrumpir la comunicación grácil.
    *   `Factory Method`: Ideal para delegar cómo se construyen instancias complejas, como los componentes internos o los comandos de las peticiones. Su competencia alterna u opuesta es instanciar todo acoplado con la clase explícita.
