# Ejemplos de Uso de la API Agent Core

## Base URL
```
http://localhost:8080/api
```

## Autenticación
Actualmente, todos los endpoints son públicos (sin autenticación).

---

## 1. ENTIDADES FINANCIERAS

### Crear una EntidadFinanciera
**POST** `/entidades-financieras`

Request:
```json
{
  "tipoEntidad": "banco",
  "denominacion": "Banco de la Nación",
  "descripcion": "Banco estatal principal",
  "codigoEntidad": "BAN001",
  "estado": true
}
```

Response:
```json
{
  "status": "success",
  "message": "EntidadFinanciera creada exitosamente",
  "data": {
    "id": 1,
    "tipoEntidad": "banco",
    "denominacion": "Banco de la Nación",
    "descripcion": "Banco estatal principal",
    "codigoEntidad": "BAN001",
    "estado": true,
    "createdAt": "2024-06-15T10:30:00",
    "updatedAt": "2024-06-15T10:30:00"
  },
  "timestamp": "2024-06-15T10:30:00Z"
}
```

### Obtener una EntidadFinanciera
**GET** `/entidades-financieras/{id}`

Response:
```json
{
  "status": "success",
  "message": "EntidadFinanciera obtenida",
  "data": {
    "id": 1,
    "tipoEntidad": "banco",
    "denominacion": "Banco de la Nación",
    "codigoEntidad": "BAN001"
  }
}
```

### Listar EntidadesFinancieras (paginado)
**GET** `/entidades-financieras?page=0&size=20&sortBy=id&direction=DESC`

Response:
```json
{
  "status": "success",
  "message": "Listado de EntidadesFinancieras",
  "data": {
    "content": [
      {
        "id": 1,
        "tipoEntidad": "banco",
        "denominacion": "Banco de la Nación"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 1,
    "totalPages": 1,
    "isFirst": true,
    "isLast": true
  }
}
```

### Filtrar por Tipo
**GET** `/entidades-financieras/tipo/banco?page=0&size=20`

### Obtener Todas Activas
**GET** `/entidades-financieras/all/active`

### Actualizar
**PUT** `/entidades-financieras/{id}`

Request:
```json
{
  "denominacion": "Banco de la Nación Actualizado",
  "estado": true
}
```

### Eliminar
**DELETE** `/entidades-financieras/{id}`

---

## 2. SALDOS

### Crear un Saldo
**POST** `/saldos`

Request:
```json
{
  "entidadFinancieraId": 1,
  "montoInicial": 50000.00,
  "montoDisponible": 50000.00,
  "fechaAsignacion": "2024-06-15T08:00:00",
  "fechaVencimiento": "2024-06-30T23:59:59",
  "estado": "activo",
  "usuarioAsignador": "admin@example.com"
}
```

Response:
```json
{
  "status": "success",
  "message": "Saldo creado exitosamente",
  "data": {
    "id": 1,
    "entidadFinancieraId": 1,
    "montoInicial": 50000.00,
    "montoDisponible": 50000.00,
    "fechaAsignacion": "2024-06-15T08:00:00",
    "estado": "activo"
  }
}
```

### Listar Saldos por Entidad
**GET** `/saldos/entidad/1?page=0&size=20`

### Listar por Estado
**GET** `/saldos/estado/activo?page=0&size=20`

---

## 3. OPERACIONES

### Crear una Operación (Retiro)
**POST** `/operaciones`

Request:
```json
{
  "tipoOperacion": "retiro",
  "montoOperacion": 1000.50,
  "descripcionOperacion": "Retiro de efectivo en sucursal",
  "numeroReferencia": "OP20240615001",
  "idEntidadFinanciera": 1,
  "usuarioId": 1
}
```

Response:
```json
{
  "status": "success",
  "message": "Operación registrada exitosamente",
  "data": {
    "id": 1,
    "tipoOperacion": "retiro",
    "montoOperacion": 1000.50,
    "descripcionOperacion": "Retiro de efectivo en sucursal",
    "numeroReferencia": "OP20240615001",
    "fechaHora": "2024-06-15T10:30:00",
    "idEntidadFinanciera": 1,
    "usuarioId": 1,
    "estadoOperacion": "completada"
  }
}
```

### Crear Operación (Depósito)
**POST** `/operaciones`

Request:
```json
{
  "tipoOperacion": "deposito",
  "montoOperacion": 5000.00,
  "descripcionOperacion": "Depósito de cliente",
  "numeroReferencia": "OP20240615002",
  "idEntidadFinanciera": 1,
  "usuarioId": 1
}
```

### Crear Operación (Pago de Servicio)
**POST** `/operaciones`

Request:
```json
{
  "tipoOperacion": "pago de servicio",
  "montoOperacion": 85.50,
  "descripcionOperacion": "Pago de recibo de agua",
  "numeroReferencia": "PAG20240615001",
  "idEntidadFinanciera": 2,
  "usuarioId": 1
}
```

### Listar Operaciones por Tipo
**GET** `/operaciones/tipo/retiro?page=0&size=20`

### Listar por Usuario
**GET** `/operaciones/usuario/1?page=0&size=20`

### Listar por Estado
**GET** `/operaciones/estado/completada?page=0&size=20`

### Filtrar por Rango de Fechas
**GET** `/operaciones/fecha-range?inicio=2024-06-15T00:00:00&fin=2024-06-15T23:59:59&page=0&size=20`

### Filtrar por Tipo y Entidad
**GET** `/operaciones/filtro?tipoOperacion=retiro&idEntidadFinanciera=1&page=0&size=20`

---

## 4. USUARIOS

### Crear un Usuario
**POST** `/usuarios`

Request:
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "correoElectronico": "juan.perez@example.com",
  "contrasena": "SecurePassword123!",
  "rol": "VENDEDOR",
  "estado": true
}
```

Response:
```json
{
  "status": "success",
  "message": "Usuario creado exitosamente",
  "data": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "correoElectronico": "juan.perez@example.com",
    "rol": "VENDEDOR",
    "estado": true,
    "createdAt": "2024-06-15T10:30:00"
  }
}
```

### Obtener Usuario por Correo
**GET** `/usuarios/correo/juan.perez@example.com`

### Listar Usuarios por Rol
**GET** `/usuarios/rol/VENDEDOR?page=0&size=20`

### Obtener Usuarios Activos por Rol
**GET** `/usuarios/rol/VENDEDOR/activos`

### Listar por Estado
**GET** `/usuarios/estado/true?page=0&size=20`

### Crear Usuario ADMIN
**POST** `/usuarios`

Request:
```json
{
  "nombre": "Admin",
  "apellido": "Sistema",
  "correoElectronico": "admin@example.com",
  "contrasena": "AdminPass123!",
  "rol": "ADMIN",
  "estado": true
}
```

### Crear Usuario ALMACENERO
**POST** `/usuarios`

Request:
```json
{
  "nombre": "Almacén",
  "apellido": "Manager",
  "correoElectronico": "almacen@example.com",
  "contrasena": "AlmacenPass123!",
  "rol": "ALMACENERO",
  "estado": true
}
```

---

## 5. REPORTES

### Crear Reporte de Operaciones
**POST** `/reportes`

Request:
```json
{
  "tipoReporte": "operaciones",
  "filtrosAplicados": "{\"tipo\":\"retiro\",\"fecha\":\"2024-06-15\"}",
  "datosReporte": "{\"total_operaciones\":125,\"monto_total\":50000.00}",
  "usuarioId": 1
}
```

Response:
```json
{
  "status": "success",
  "message": "Reporte creado exitosamente",
  "data": {
    "id": 1,
    "tipoReporte": "operaciones",
    "fechaGeneracion": "2024-06-15T10:30:00",
    "filtrosAplicados": "{\"tipo\":\"retiro\",\"fecha\":\"2024-06-15\"}",
    "usuarioId": 1
  }
}
```

### Crear Reporte de Saldos
**POST** `/reportes`

Request:
```json
{
  "tipoReporte": "saldos",
  "filtrosAplicados": "{\"estado\":\"activo\"}",
  "datosReporte": "{\"saldos_totales\":500000.00,\"entidades\":5}",
  "usuarioId": 1
}
```

### Crear Reporte de Usuarios
**POST** `/reportes`

Request:
```json
{
  "tipoReporte": "usuarios",
  "filtrosAplicados": "{\"rol\":\"VENDEDOR\"}",
  "datosReporte": "{\"total_usuarios\":15,\"activos\":12}",
  "usuarioId": 1
}
```

### Listar Reportes por Tipo
**GET** `/reportes/tipo/operaciones?page=0&size=20`

### Listar Reportes por Usuario
**GET** `/reportes/usuario/1?page=0&size=20`

### Filtrar por Rango de Fechas
**GET** `/reportes/fecha-range?inicio=2024-06-01T00:00:00&fin=2024-06-30T23:59:59&page=0&size=20`

---

## Códigos de Respuesta HTTP

| Código | Significado |
|--------|------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado |
| 400 | Bad Request - Error de validación |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error del servidor |

---

## Manejo de Errores

### Validación de Entrada Fallida
**Response 400:**
```json
{
  "status": "error",
  "message": "Error de validación",
  "data": {
    "montoOperacion": "El monto debe ser positivo",
    "numeroReferencia": "El número de referencia es requerido"
  },
  "timestamp": "2024-06-15T10:30:00Z"
}
```

### Recurso No Encontrado
**Response 404:**
```json
{
  "status": "error",
  "message": "EntidadFinanciera no encontrada: 999",
  "timestamp": "2024-06-15T10:30:00Z"
}
```

### Violación de Regla de Negocio
**Response 400:**
```json
{
  "status": "error",
  "message": "El código de entidad ya existe",
  "timestamp": "2024-06-15T10:30:00Z"
}
```

---

## Parámetros Comunes

### Paginación
- `page`: Número de página (0-indexed), default: 0
- `size`: Tamaño de página (máx. 100), default: 20
- `sortBy`: Campo para ordenar, default: "id"
- `direction`: ASC o DESC, default: "DESC"

### Ejemplo:
```
GET /api/operaciones?page=1&size=50&sortBy=fechaHora&direction=ASC
```

---

## Scripts cURL Útiles

### Crear datos de prueba
```bash
# Crear entidad financiera
curl -X POST http://localhost:8080/api/entidades-financieras \
  -H "Content-Type: application/json" \
  -d '{"tipoEntidad":"banco","denominacion":"Banco XYZ","codigoEntidad":"BXZ001","estado":true}'

# Crear usuario
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test","apellido":"User","correoElectronico":"test@test.com","contrasena":"pass","rol":"VENDEDOR","estado":true}'
```

---

## Swagger/OpenAPI

Accede a la documentación interactiva en:
```
http://localhost:8080/swagger-ui.html
```

O al JSON de OpenAPI:
```
http://localhost:8080/v3/api-docs
```

---

**Última actualización**: 2024-06-15
**Versión API**: 1.0.0

