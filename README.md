# Agent Core - API Bancaria para Agentes

## Descripción

API de operaciones bancarias para agentes que ofrecen servicios de retiros, depósitos y pagos de servicios. La aplicación facilitará el acceso a servicios financieros a personas que no tienen acceso a una sucursal bancaria.

## Características Principales

- **Gestión de Entidades Financieras**: Registro y control de bancos y servicios
- **Asignación de Saldos**: Distribución de fondos a inicio del día
- **Registro de Operaciones**: Control de retiros, depósitos y pagos de servicios
- **Gestión de Usuarios**: Manejo de usuarios con roles diferenciados (ADMIN, VENDEDOR, ALMACENERO)
- **Reportes**: Generación de reportes detallados de operaciones y saldos
- **Auditoría**: Registro completo de todas las operaciones realizadas

## Requisitos

- **Java**: 21 o superior
- **Maven**: 3.6+
- **PostgreSQL**: 15+
- **Spring Boot**: 4.1.0

## Instalación

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd agent-core
```

### 2. Configurar variables de entorno

Crear un archivo `.env` o establecer las variables de entorno:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=agent_db
DB_USER=postgres
DB_PASS=your_password
SERVER_PORT=8080
```

### 3. Compilar el proyecto

```bash
./mvnw clean compile
```

### 4. Ejecutar pruebas

```bash
./mvnw test
```

### 5. Empaquetar la aplicación

```bash
./mvnw clean package
```

### 6. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

O con el JAR compilado:

```bash
java -jar target/agent-core-0.0.1-SNAPSHOT.jar
```

## Estructura del Proyecto

```
agent-core/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/aitamh/agent/core/
│   │   │       ├── common/           # Clases comunes (DTOs, excepciones, VOs)
│   │   │       ├── config/           # Configuraciones de Spring
│   │   │       ├── entidadfinanciera/ # Módulo EntidadFinanciera
│   │   │       ├── saldo/            # Módulo Saldo
│   │   │       ├── operacion/        # Módulo Operación
│   │   │       ├── usuario/          # Módulo Usuario
│   │   │       └── reporte/          # Módulo Reporte
│   │   └── resources/
│   │       ├── application.yaml      # Configuración de la aplicación
│   │       ├── logback-spring.xml    # Configuración de logging
│   │       └── db/migration/         # Scripts de Flyway
│   └── test/                         # Tests unitarios e integración
├── pom.xml                           # Dependencias Maven
└── README.md                         # Este archivo
```

## Arquitectura en Capas

La aplicación sigue una arquitectura en capas estricta:

```
Controller (API REST)
    ↓
Service (Lógica de negocio)
    ↓
Repository (Acceso a datos)
    ↓
Entity (Modelo de datos)
```

## Módulos

### 1. EntidadFinanciera

Gestiona los bancos y servicios con los que el agente tiene convenio.

**Endpoints**:
- `POST /api/entidades-financieras` - Crear
- `GET /api/entidades-financieras/{id}` - Obtener por ID
- `GET /api/entidades-financieras` - Listar (paginado)
- `GET /api/entidades-financieras/tipo/{tipoEntidad}` - Filtrar por tipo
- `GET /api/entidades-financieras/all/active` - Obtener activas
- `PUT /api/entidades-financieras/{id}` - Actualizar
- `DELETE /api/entidades-financieras/{id}` - Eliminar

### 2. Saldo

Asignación y control de saldos por entidad financiera.

**Endpoints**:
- `POST /api/saldos` - Crear
- `GET /api/saldos/{id}` - Obtener por ID
- `GET /api/saldos` - Listar (paginado)
- `GET /api/saldos/entidad/{entidadFinancieraId}` - Por entidad
- `GET /api/saldos/estado/{estado}` - Por estado
- `PUT /api/saldos/{id}` - Actualizar
- `DELETE /api/saldos/{id}` - Eliminar

### 3. Operación

Registro de retiros, depósitos y pagos de servicios.

**Endpoints**:
- `POST /api/operaciones` - Crear
- `GET /api/operaciones/{id}` - Obtener por ID
- `GET /api/operaciones` - Listar (paginado)
- `GET /api/operaciones/tipo/{tipoOperacion}` - Por tipo
- `GET /api/operaciones/entidad/{idEntidadFinanciera}` - Por entidad
- `GET /api/operaciones/usuario/{usuarioId}` - Por usuario
- `GET /api/operaciones/estado/{estadoOperacion}` - Por estado
- `GET /api/operaciones/fecha-range?inicio=...&fin=...` - Por rango de fechas
- `PUT /api/operaciones/{id}` - Actualizar
- `DELETE /api/operaciones/{id}` - Eliminar

### 4. Usuario

Gestión de usuarios del sistema con roles diferenciados.

**Endpoints**:
- `POST /api/usuarios` - Crear
- `GET /api/usuarios/{id}` - Obtener por ID
- `GET /api/usuarios/correo/{correoElectronico}` - Por correo
- `GET /api/usuarios` - Listar (paginado)
- `GET /api/usuarios/rol/{rol}` - Por rol
- `GET /api/usuarios/estado/{estado}` - Por estado
- `GET /api/usuarios/rol/{rol}/activos` - Activos por rol
- `PUT /api/usuarios/{id}` - Actualizar
- `DELETE /api/usuarios/{id}` - Eliminar

### 5. Reporte

Generación de reportes de operaciones, saldos y usuarios.

**Endpoints**:
- `POST /api/reportes` - Crear
- `GET /api/reportes/{id}` - Obtener por ID
- `GET /api/reportes` - Listar (paginado)
- `GET /api/reportes/tipo/{tipoReporte}` - Por tipo
- `GET /api/reportes/usuario/{usuarioId}` - Por usuario
- `GET /api/reportes/fecha-range?inicio=...&fin=...` - Por rango de fechas
- `PUT /api/reportes/{id}` - Actualizar
- `DELETE /api/reportes/{id}` - Eliminar

## Documentación de API

Accede a la documentación interactiva de Swagger en:

```
http://localhost:8080/swagger-ui.html
```

## Formato de Respuesta

Todas las respuestas HTTP siguen el siguiente formato estándar:

```json
{
  "status": "success",
  "message": "Operación completada exitosamente",
  "data": {
    // Datos de la respuesta
  },
  "timestamp": "2024-06-15T10:30:00Z"
}
```

### Respuesta de Error

```json
{
  "status": "error",
  "message": "Mensaje de error descriptivo",
  "timestamp": "2024-06-15T10:30:00Z"
}
```

## Validación

- Todos los DTOs de Request incluyen validaciones con `@Valid`
- Se validan entradas lo antes posible en los controladores
- Excepciones específicas del dominio se lanzan en servicios

## Seguridad

En esta fase inicial:
- ✅ Todos los endpoints son públicos
- 📋 En fases futuras se implementará:
  - Autenticación JWT
  - Autorización basada en roles
  - Cifrado de contraseñas

## Base de Datos

### Migraciones

Las migraciones se ejecutan automáticamente al iniciar la aplicación usando Flyway:

- `V1__Create_entidades_financieras.sql`
- `V2__Create_saldos.sql`
- `V3__Create_operaciones.sql`
- `V4__Create_usuarios.sql`
- `V5__Create_reportes.sql`

### Crear base de datos

```sql
CREATE DATABASE agent_db;
```

## Logging

El logging se configura a través de `logback-spring.xml`:

- **Archivo**: `logs/agent-core.log`
- **Máximo tamaño**: 10MB por archivo
- **Histórico**: Máximo 10 archivos
- **Nivel por defecto**: INFO
- **Nivel para com.aitamh.agent.core**: DEBUG

## Principios de Desarrollo

El proyecto sigue los principios SOLID:

- **S**ingle Responsibility Principle
- **O**pen/Closed Principle
- **L**iskov Substitution Principle
- **I**nterface Segregation Principle
- **D**ependency Inversion Principle

Además:
- DRY (Don't Repeat Yourself)
- YAGNI (You Aren't Gonna Need It)
- Fail Fast
- Immutability en DTOs

## Contribución

Para contribuir al proyecto:

1. Crear una rama para tu feature
2. Hacer commit de tus cambios
3. Empujar la rama
4. Crear un Pull Request

## Licencia

Apache License 2.0

## Contacto

Para consultas o problemas, contactar al equipo de desarrollo.

***********************
PENDIENTES:
***********************
# Un modulo para registrar ventas (se debe sumar a cuenta OTRAS VENTAS)
  - Venta por yape o efectivo
  - Descripcion de la venta
  - Monto de la venta
  - producto vendido
  - fecha de venta
  - 
# Agregar un campo opcional de comision por pago de servicios

# Agregar un campo opcional de comision a las operaciones de retiro y deposito

