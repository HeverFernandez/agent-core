# AGENTS.md - Nuvanta App Store Core

## Project Overview
**La aplicación será para un agente bancario que ofrece operaciones de: retiros, depósitos y pagos de servicios (agua, luz, teléfono, pensión de enseñanza, etc).  El agente bancario es un intermediario entre el banco y los clientes, y su objetivo es facilitar el acceso a los servicios financieros a personas que no tienen acceso a una sucursal bancaria.**

## 1. Technology Stack
- **Framework**: Spring Boot 4.1.0 (parent POM)
- **Build Tool**: Maven 3.6+ (with Maven Wrapper `mvnw`/`mvnw.cmd`)
- **Language**: Java 21
- **Runtime**: Standalone JAR packaging (`spring-boot-maven-plugin`)
- **Database**: PostgreSQL 15+ (accessed via Spring Data JPA)
- **ORM**: Hibernate 5.6+ (JPA implementation)
- **Mapping**: MapStruct 1.5+ para conversiones Entity ↔ DTO
- **Validation**: Bean Validation (spring-boot-starter-validation)
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Documentation**: Swagger UI en /swagger-ui.html (springdoc-openapi-starter-webmvc-ui)
- **Migrations**: Flyway (scripts en src/main/resources/db/migration) (flyway-core + flyway-database-postgresql)
- **Logging**: SLF4J + Logback (configuración personalizada en src/main/resources/logback-spring.xml)
- **API Response Format**: Todas las respuestas HTTP siguen un formato estándar definido por el DTO ApiResponse<T>.
- **Paginación**: Todos los endpoints de listado son paginados usando Spring Data Pageable. El tamaño máximo de página es 100 para evitar respuestas excesivamente grandes.
- **Seguridad**: En esta fase no se implementa seguridad. Todos los endpoints son públicos. En fases futuras se añadirá autenticación JWT y autorización basada en roles.
- **Convenciones de código**: Se siguen las convenciones estándar de Java y Spring Boot, con una estructura de paquetes clara y modular. Se aplican principios SOLID y buenas prácticas de diseño.

## 2. Principios de desarrollo obligatorios
Se debe de respetar estos principios en cada clase, método y decisión de diseño que tome. No son sugerencias: son restricciones de arquitectura.

### 2.1. Principios SOLID
#### S — Single Responsibility Principle
Cada clase tiene una única razón para cambiar. Ejemplos concretos:
*	ProductServiceImpl: solo orquesta lógica de negocio de productos. No hace consultas SQL directas ni formatea respuestas.
*	ProductMapper: solo convierte entre Entity y DTO. No valida ni persiste.
*	ProductController: solo recibe HTTP, delega al servicio y retorna la respuesta envuelta en ApiResponse.
*	GlobalExceptionHandler: solo maneja excepciones. No contiene lógica de negocio.

#### O — Open/Closed Principle
Las clases genéricas están abiertas a extensión (herencia/sobreescritura) y cerradas a modificación directa.
*	GenericServiceImpl provee hooks vacíos validateBeforeCreate() y validateBeforeUpdate() para que las subclases añadan validaciones sin tocar el genérico.
*	GenericController expone sus endpoints; los controladores específicos añaden endpoints propios sin modificar el padre.

#### L — Liskov Substitution Principle
Los módulos específicos deben poder sustituir a su clase genérica sin romper el contrato.
*	ProductService extiende GenericService<Product, ProductRequest, ProductResponse>. Cualquier código que use GenericService puede recibir ProductService sin sorpresas.
*	Regla: nunca lanzar excepciones no declaradas en la interfaz base ni cambiar la semántica del retorno.

#### I — Interface Segregation Principle
Las interfaces son pequeñas y focalizadas.
*	GenericService declara solo las operaciones CRUD y findAll comunes a todo módulo.
*	ProductService extiende GenericService y agrega únicamente los métodos propios del módulo (findBySku, search). No hereda métodos que no usa.

#### D — Dependency Inversion Principle
Las capas superiores dependen de abstracciones, no de implementaciones concretas.
*	ProductController depende de la interfaz ProductService, no de ProductServiceImpl.
*	ProductServiceImpl recibe ProductRepository (interfaz JPA) por constructor, inyectado por Spring.
*	Nunca instanciar dependencias con new dentro de servicios o controladores.

### 2.2. Principios SOLID
#### Nomenclatura
*	Clases: PascalCase. Métodos y variables: camelCase. Constantes: UPPER_SNAKE_CASE.
*	Los nombres son autoexplicativos. Prohibido: data, info, obj, tmp, x, aux.
*	Verbos para métodos: find, create, update, delete, validate, calculate, resolve, build.
*	Sustantivos para clases: Product, Category, Sale, Stock. Sufijos que indican rol: Service, Controller, Repository, Mapper, Request, Response, Exception.

#### Funciones y métodos
*	Máximo 20 líneas por método. Si un método crece, extraer lógica a métodos privados con nombre descriptivo.
*	Máximo 3 parámetros por método. Si se necesitan más, encapsular en un objeto o DTO.
*	No usar comentarios para explicar qué hace el código; el nombre del método ya lo dice. Solo usar Javadoc en interfaces públicas.
*	Evitar números mágicos: extraer a constantes con nombre (ejemplo: MAX_STOCK_ALERT_QUANTITY = 5).

#### Manejo de errores
* Nunca retornar null. Usar Optional<T> cuando un valor puede no existir.
* Nunca capturar Exception genérica. Usar excepciones específicas del dominio.
* Lanzar EntityNotFoundException cuando un registro no existe. Lanzar BusinessException para reglas de negocio violadas.
* El GlobalExceptionHandler es el único punto donde las excepciones se transforman en respuestas HTTP.

### 2.2. Otros principios
* DRY (Don't Repeat Yourself): si la misma lógica aparece en dos lugares, extraerla al módulo GENERIC o a un método privado.
* YAGNI (You Aren't Gonna Need It): no implementar funcionalidad que no se haya solicitado explícitamente. No agregar campos 'por si acaso'.
* Fail Fast: validar entradas lo antes posible (en el controlador con @Valid). No dejar que datos inválidos lleguen al servicio.
* Immutability: los DTOs de respuesta son objetos inmutables. Usar @Value de Lombok o records de Java 21 si aplica.

## 3. Arquitectura y estructura del proyecto
### 3.1. Estructura en capas
El proyecto sigue una arquitectura en capas estricta. Las dependencias solo fluyen hacia abajo: Controller → Service → Repository → Entity. Ninguna capa puede saltarse otra.

- `controller/` - Recibe HTTP, valida entrada, delega al servicio, retorna ApiResponse
- `service/` - Orquesta la lógica de negocio, transacciones, validaciones
- `entity/` - Modelo de datos mapeado a tablas PostgreSQL
- `repository/` - Acceso a base de datos, consultas JPA/JPQL
- `config/` - Spring configuration beans
- `VO/` - Value Objects para transferencia de datos
- `DTO (Transferencia)/` - Objetos de entrada (Request) y salida (Response) por HTTP
- `mapper (Conversión)/` - Convierte entre Entity y DTO sin lógica de negocio
- `constants` - Constantes globales del módulo
- `exception` - Excepciones específicas del módulo y del dominio
- `util` - Clases utilitarias de propósito general (no de negocio)
- `resources/` - Configuración de Spring Boot, scripts Flyway, mensajes de error, logback.xml
- `application.yml` - Configuración de Spring Boot (datasource, logging, etc.)
- `README.md` - Documentación del módulo
- `LICENSE` - Licencia del proyecto
- `swagger-ui.html` - Documentación de API generada automáticamente por Springdoc OpenAPI

## 4. Módulos del sistema
### 4.1. Entidad Financiera
El módulo EntidadFinanciera se encarga de gestionar la información de las entidades financieras con las que el agente bancario tiene convenio. Permite registrar, actualizar, eliminar y consultar entidades financieras, así como gestionar sus datos de contacto y los servicios que ofrecen. Este módulo es crucial para que el agente bancario pueda ofrecer servicios de pago y transferencia a sus clientes.
  - **Entidad:** EntidadFinanciera
  - **Atributos:** id, tipoEntidad (banco, servicio), denominacion, descripcion, codigoEntidad(abreviatura o código interno), estado (activo, inactivo)
  - **Relaciones:** Una EntidadFinanciera puede tener múltiples Saldos y Operaciones asociadas.

### 4.2. Asignacion de saldos
- Permitir distribuir o asignar saldos al inicio del día, a cada fondo de una entidad financiera, para que el agente pueda operar con ellos.
- Los saldos serán la base para préstamos y retiros, y deben ser gestionados cuidadosamente para evitar sobregiros o errores en las transacciones.
- Validar disponibilidad de saldo antes de cada operación, y actualizar el saldo restante después de cada transacción. 
  - **Entidad:**  Saldo  
  - **Atributos:** id(PK), entidadFinancieraId(FK → EntidadFinanciera), montoInicial, montoDisponible, fechaAsignacion, fechaVencimiento, estado (activo, inactivo), usuarioAsignador, usuarioActualizador, fechaActualizacion
  - **Relaciones:** Un Saldo pertenece a una EntidadFinanciera y puede estar asociado a múltiples Operaciones.

### 4.3. Registro de operaciones
- Gestionar las transacciones de los clientes, incluyendo retiros, depósitos y pagos de servicios.
- Registrar cada transacción como deposito, retiro o pago de servicio, con un número de referencia único para cada operación, y asociarla al saldo correspondiente de la entidad financiera.
- Implementar validaciones para asegurar que las transacciones se realicen correctamente, como verificar la disponibilidad de saldo.
- Permitir la consulta del historial de transacciones por cliente, por tipo de operación y por fecha, para facilitar la auditoría y el seguimiento de las operaciones realizadas.
- Implementar mecanismos de seguridad y auditoría para garantizar la integridad y confidencialidad de las transacciones, incluyendo el registro de quién realizó cada operación y cuándo.
  - **Entidad:** Operacion
  - **Atributos:** id(PK), tipoOperacion(retiro, deposito, pago de servicio), montoOperacion, descripcionOperacion, numeroReferencia(voucher, comprobante, código transacción), fechaHora, idEntidadFinanciera(FK → EntidadFinanciera), usuarioId, estadoOperacion(pendiente, completada, anulada, fallida)
  - **Relaciones:** Una Operacion pertenece a una EntidadFinanciera y puede estar asociada a un Saldo.
  - **Validaciones:** Antes de registrar una operación, se debe validar que el saldo disponible en la EntidadFinanciera sea suficiente para cubrir el monto de la operación.
  - **Filtro de operacion** Permitir filtrar por tipoEntidad (banco, servicio), si selecciona banco solo listar los bancos, si selecciona servicio solo listar los servicios. Permitir filtrar por tipoOperacion (retiro, deposito, pago de servicio), fechaHora, idEntidadFinanciera y usuarioId.
  - **Filtros de consulta:** Permitir filtrar las operaciones por tipo de operación, fecha, entidad financiera y cliente, para facilitar la búsqueda y el análisis de las transacciones realizadas.
  - **Auditoría:** Cada operación debe registrar el usuario que la realizó y la fecha y hora de la transacción. Esto es crucial para mantener un historial completo de las operaciones y facilitar la auditoría en caso de disputas o revisiones regulatorias.
  - **Seguridad:** Implementar medidas de seguridad para proteger la información de las transacciones, incluyendo cifrado de datos sensibles y control de acceso basado en roles. Solo usuarios autorizados deben poder realizar operaciones y acceder a la información de los clientes.
  - **Reportes:** Generar reportes detallados de las operaciones realizadas, incluyendo filtros por tipo de operación, fecha, entidad financiera y cliente. Esto permitirá al agente bancario analizar el desempeño de sus operaciones y tomar decisiones informadas sobre la gestión de su negocio.

### 4.4. Gestión de usuarios y roles
- Gestionar los usuarios del sistema, incluyendo la creación, actualización, eliminación y consulta de usuarios, así como la asignación de roles y permisos.
- Implementar un sistema de roles diferenciados para controlar el acceso a las funcionalidades del sistema, asegurando que solo los usuarios autorizados puedan realizar ciertas operaciones.
- Los roles pueden incluir ADMIN, VENDEDOR, ALMACENERO, y otros según las necesidades del negocio. Cada rol tendrá permisos específicos que determinan qué acciones puede realizar dentro del sistema.
- Implementar autenticación y autorización para garantizar que los usuarios solo puedan acceder a las funcionalidades y datos que les corresponden según su rol. Esto es crucial para mantener la seguridad y la integridad del sistema, así como para proteger la información sensible de los clientes y las entidades financieras.
- **Entidad:** Usuario
  - **Atributos:** id(PK), nombre, apellido, correoElectronico, contrasena, rol(ADMIN, VENDEDOR, ALMACENERO), estado (activo, inactivo), fechaCreacion, fechaActualizacion
  - **Relaciones:** Un Usuario puede estar asociado a múltiples Operaciones y Saldos.
  - **Validaciones:** Validar que el correo electrónico sea único y que la contraseña cumpla con los requisitos de seguridad antes de crear o actualizar un usuario.
  - **Seguridad:** Implementar cifrado de contraseñas y control de acceso basado en roles para proteger la información de los usuarios y garantizar que solo los usuarios autorizados puedan realizar ciertas acciones dentro del sistema.

### 4.5. Reportes y análisis
- Generar reportes detallados de las operaciones realizadas, incluyendo filtros por tipo de operación, fecha, entidad financiera y cliente. Esto permitirá al agente bancario analizar el desempeño de sus operaciones y tomar decisiones informadas sobre la gestión de su negocio.
- Implementar análisis de datos para identificar tendencias y patrones en las transacciones, como los tipos de operaciones más frecuentes, los clientes más activos y los períodos de mayor actividad. Esto ayudará al agente bancario a optimizar sus operaciones y mejorar la experiencia del cliente.
- Permitir la exportación de reportes en formatos comunes como PDF y Excel, para facilitar la presentación de información a las entidades financieras y a los clientes.
- Implementar un sistema de alertas y notificaciones para informar al agente bancario sobre eventos importantes, como saldos bajos, operaciones fallidas o disputas de clientes. Esto permitirá al agente tomar medidas rápidas para resolver problemas y mantener la satisfacción del cliente.
- **Entidad:** Reporte
  - **Atributos:** id(PK), tipoReporte(operaciones, saldos, usuarios), fechaGeneracion, filtrosAplicados, datosReporte, usuarioId
  - **Relaciones:** Un Reporte puede estar asociado a múltiples Operaciones, Saldos y Usuarios.

## 5. DTOs, Value Objects y paginaciónn
### 5.1. Regla de DTOs
- Los DTOs de Request nunca se usan como DTOs de Response y viceversa. Son clases separadas.
- Los DTOs de Request tienen validaciones (@NotNull, @NotBlank, @Positive, @Size). Los de Response no.
- Los DTOs no tienen lógica de negocio. Solo son contenedores de datos.
- Nunca exponer la entidad JPA directamente como respuesta HTTP. Siempre pasar por un ResponseDTO.
- Los DTOs no tienen referencias a otras entidades JPA. Solo tipos primitivos, String, BigDecimal, LocalDateTime, ZonedDateTime y otros DTOs simples.
### 5.2. Value Objects (VO)
el agente debe crear VOs cuando detecte los siguientes patrones:
- Un valor con lógica de validación propia. Ejemplo: monto de operación que sea positivo y nunca negativo, o un número de referencia que siga un formato específico.
- Los VOs son clases anotadas con @Embeddable (para uso en entidades JPA) o Java records (para uso solo en DTOs).
- Los VOs son inmutables: todos sus campos son final y no tienen setters.  
- Los VOs encapsulan la lógica de validación y formateo de sus valores. Por ejemplo, un VO MontoOperacion puede validar que el monto sea positivo y formatearlo a dos decimales.
- Los VOs se usan en las entidades JPA y en los DTOs, pero nunca se exponen directamente como respuesta HTTP. Siempre se convierten a un DTO de Response antes de enviarlos al cliente.

### 6.3. Paginación - Decisión de diseño
*Regla fundamental de paginación*:
El método findAll(Pageable) SIEMPRE es paginado. No existe un findAll() sin paginar. Esto garantiza que ningún módulo pueda retornar miles de registros en una sola respuesta, incluso si el programador olvida agregar paginación.
- El endpoint GET siempre recibe page, size, sortBy y direction como query params con valores por defecto.
- El endpoint GET /all retorna List<RES> sin paginar, pero solo registros activos. Es exclusivo para cargar combos y selectores en el frontend donde se necesita la lista completa.
- Los endpoints de búsqueda específicos (GET /search en ProductController) también son paginados y retornan PageResponse<RES>.
- El tamaño máximo de página permitido es 100. Si el frontend envía size > 100, el controlador debe aplicar Math.min(size, 100) antes de crear el Pageable.

## 7. Base de datos y migraciones Flyway
### 7.1. Configuración PostgreSQL
- Nombre de la base de datos: agent_db.
- Todas las credenciales se leen de variables de entorno: DB_URL, DB_USER, DB_PASS. Nunca hardcodear en application.yml.
- Dialect: org.hibernate.dialect.PostgreSQLDialect.
- ddl-auto: validate. Flyway es el único responsable de crear y modificar el esquema.

### 7.2. Convenciones de esquema
- Nombres de tablas en snake_case plural: operaciones, saldos, entidades_financieras, usuarios.
- Nombres de columnas en snake_case: sale_price, category_id, created_at, updated_at, created_by, updated_by.
- Todas las tablas tienen las columnas de auditoría: created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100).
- Todas las tablas tienen la columna: active BOOLEAN NOT NULL DEFAULT TRUE.
- Las claves primarias son BIGSERIAL (autoincremental).
- Las claves foráneas tienen nombre explícito: fk_product_category, fk_product_unit.

## 9. Flujo de una petición HTTP — ejemplos
### POST /api/operaciones
**Request:**
```json
{
  "tipoOperacion": "retiro",
  "montoOperacion": 100.50,
  "descripcionOperacion": "Retiro de efectivo",
  "numeroReferencia": "ABC123456",
  "idEntidadFinanciera": 1,
  "usuarioId": 42
}
```
**Response:**
```json
{
  "status": "success",
  "message": "Operación registrada exitosamente",
  "data": {
    "id": 101,
    "tipoOperacion": "retiro",
    "montoOperacion": 100.50,
    "descripcionOperacion": "Retiro de efectivo",
    "numeroReferencia": "ABC123456",
    "fechaHora": "2024-06-15T10:30:00Z",
    "idEntidadFinanciera": 1,
    "usuarioId": 42,
    "estadoOperacion": "completada"
  }
}   
```
