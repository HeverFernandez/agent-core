# Guía de Inicio Rápido - Agent Core

## Requisitos Previos

1. **Java 21** instalado
   ```bash
   java -version
   ```

2. **PostgreSQL 15+** instalado y ejecutándose
   ```bash
   psql --version
   ```

## Configuración de Base de Datos

### Paso 1: Crear la base de datos

Conectarse a PostgreSQL y ejecutar:

```sql
CREATE DATABASE agent_db;
```

### Paso 2: Configurar variables de entorno

**En Windows (PowerShell)**:

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="agent_db"
$env:DB_USER="postgres"
$env:DB_PASS="tu_contraseña"
$env:SERVER_PORT="8080"
```

**En Linux/Mac (Bash)**:

```bash
export DB_HOST="localhost"
export DB_PORT="5432"
export DB_NAME="agent_db"
export DB_USER="postgres"
export DB_PASS="tu_contraseña"
export SERVER_PORT="8080"
```

O crear un archivo `.env` en la raíz del proyecto:

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=agent_db
DB_USER=postgres
DB_PASS=tu_contraseña
SERVER_PORT=8080
```

## Ejecución

### Opción 1: Con Maven

```bash
cd agent-core
./mvnw spring-boot:run
```

**En Windows**:
```powershell
cd agent-core
mvnw.cmd spring-boot:run
```

### Opción 2: Con Java (después de compilar)

```bash
cd agent-core
mvn clean package -DskipTests
java -jar target/agent-core-0.0.1-SNAPSHOT.jar
```

## Verificar que la aplicación está ejecutándose

Una vez iniciada, verifica en la consola un mensaje similar a:

```
Tomcat started on port(s): 8080 (http)
```

## Acceder a la documentación de la API

Abre en tu navegador:

```
http://localhost:8080/swagger-ui.html
```

La documentación interactiva de Swagger debería cargar, mostrando todos los endpoints disponibles.

## Estructura de carpetas creada

```
agent-core/
├── src/main/java/com/aitamh/agent/core/
│   ├── common/
│   │   ├── dto/
│   │   │   ├── ApiResponse.java
│   │   │   └── PageResponse.java
│   │   ├── exception/
│   │   │   ├── EntityNotFoundException.java
│   │   │   ├── BusinessException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── vo/
│   │   │   ├── MontoOperacion.java
│   │   │   └── NumeroReferencia.java
│   ├── config/
│   │   └── OpenApiConfig.java
│   ├── entidadfinanciera/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── repository/
│   │   ├── mapper/
│   │   ├── service/
│   │   ├── controller/
│   │   └── constants/
│   ├── saldo/                    (Estructura similar)
│   ├── operacion/                (Estructura similar)
│   ├── usuario/                  (Estructura similar)
│   └── reporte/                  (Estructura similar)
├── src/main/resources/
│   ├── application.yaml
│   ├── logback-spring.xml
│   └── db/migration/
│       ├── V1__Create_entidades_financieras.sql
│       ├── V2__Create_saldos.sql
│       ├── V3__Create_operaciones.sql
│       ├── V4__Create_usuarios.sql
│       └── V5__Create_reportes.sql
├── pom.xml
├── README.md
└── QUICK_START.md (este archivo)
```

## Pruebas de Endpoints

### Crear una EntidadFinanciera

```bash
curl -X POST http://localhost:8080/api/entidades-financieras \
  -H "Content-Type: application/json" \
  -d '{
    "tipoEntidad": "banco",
    "denominacion": "Banco XYZ",
    "descripcion": "Banco principal",
    "codigoEntidad": "BANCO001",
    "estado": true
  }'
```

### Listar EntidadesFinancieras

```bash
curl http://localhost:8080/api/entidades-financieras
```

### Crear un Usuario

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "correoElectronico": "juan@example.com",
    "contrasena": "password123",
    "rol": "VENDEDOR",
    "estado": true
  }'
```

## Solución de problemas

### La aplicación no inicia

1. Verifica que PostgreSQL está ejecutándose
2. Verifica las credenciales de base de datos
3. Asegúrate de que la base de datos `agent_db` existe
4. Revisa el archivo de log: `logs/agent-core.log`

### Error de conexión a base de datos

```
Connection to database failed
```

Soluciones:
- Verifica que PostgreSQL está en ejecución
- Verifica los valores de `DB_USER` y `DB_PASS`
- Intenta conectarte manualmente: `psql -h localhost -U postgres -d agent_db`

### Puerto 8080 ya está en uso

Cambia el puerto con:

```bash
export SERVER_PORT=8081
```

O en Windows:
```powershell
$env:SERVER_PORT="8081"
```

## Próximos pasos

1. Revisa la documentación en `README.md`
2. Explore los endpoints en Swagger UI
3. Crea pruebas para los endpoints
4. Implementa autenticación JWT
5. Añade autorización basada en roles

## Contacto y soporte

Para problemas o preguntas, consulta la documentación en `README.md` o contacta al equipo de desarrollo.

