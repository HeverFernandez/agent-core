# SOLUCIÓN DE CORS - Guía de Implementación Rápida

## ✅ Estado: COMPLETADO

Se creó la clase **`CorsConfig.java`** que configura automáticamente CORS para permitir que tu proyecto Angular en puerto 4200 se conecte a la API en puerto 8080.

---

## 📁 Archivo Creado

**Ruta:** `src/main/java/com/aitamh/agent/core/config/CorsConfig.java`

**Estado:** ✅ Compilado sin errores

---

## 🚀 Instrucciones de Uso

### Paso 1: Compilar la aplicación

```powershell
cd E:\JAVAWEB\agent-core
mvn clean compile
```

✅ Si ves `BUILD SUCCESS`, la clase CorsConfig se compiló correctamente.

### Paso 2: Empaquetar la aplicación (opcional)

```powershell
mvn clean package -DskipTests
```

### Paso 3: Ejecutar la aplicación Spring Boot

```powershell
mvn spring-boot:run
```

O si ya empaquetaste:

```powershell
java -jar target\agent-core-0.0.1-SNAPSHOT.jar
```

Deberías ver en los logs algo como:
```
2026-08-15 14:00:00 - Starting AgentCoreApplication v0.0.1-SNAPSHOT
2026-08-15 14:00:05 - Started AgentCoreApplication in X.XXX seconds
```

### Paso 4: Ejecutar Angular

En otra terminal:

```powershell
cd tu-proyecto-angular
ng serve
```

O si usas npm:

```powershell
npm start
```

Angular debería estar disponible en: `http://localhost:4200`

---

## 🔗 Estructura de Conexión

```
┌─────────────────────────┐
│  Angular (localhost:4200) │
└────────────┬─────────────┘
             │ HTTP Request
             │ Origin: http://localhost:4200
             ↓
┌─────────────────────────────────────────┐
│  Spring Boot API (localhost:8080)       │
│  ├─ CorsConfig ✅ ACTIVO               │
│  ├─ Permitidos: GET, POST, PUT, DELETE │
│  ├─ Headers: Authorization, Content... │
│  └─ Credenciales: Sí                  │
└─────────────────────────┬───────────────┘
             ↑
             │ HTTP Response
             │ Access-Control-Allow-Origin: http://localhost:4200
             │
        ✅ CORS PERMITIDO
```

---

## 📊 Qué configura CorsConfig.java

| Aspecto | Configuración |
|---------|--------------|
| **Orígenes permitidos** | `localhost:4200`, `localhost:3000`, `localhost:5173` |
| **Métodos HTTP** | GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD |
| **Headers permitidos** | Todos (`*`) |
| **Headers expuestos** | Authorization, Content-Type, X-Total-Count, X-Page-Number |
| **Credenciales** | ✅ Permitidas (cookies, JWT) |
| **Cacheo preflight** | 3600 segundos (1 hora) |

---

## 🧪 Verificar que CORS funciona

### Opción 1: Prueba en Angular

Crea este componente en Angular:

```typescript
// app.component.ts
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  resultado: any = null;
  error: string | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.testearCORS();
  }

  testearCORS() {
    this.http.get('http://localhost:8080/api/saldos?page=0&size=10').subscribe(
      (data) => {
        console.log('✅ CORS OK! Datos recibidos:', data);
        this.resultado = data;
      },
      (error) => {
        console.error('❌ CORS Error:', error);
        this.error = 'Error de CORS o conexión: ' + error.message;
      }
    );
  }
}
```

**HTML:**
```html
<div *ngIf="error" style="color: red;">{{ error }}</div>
<div *ngIf="resultado" style="color: green;">✅ CORS Funcionando: {{ resultado | json }}</div>
```

### Opción 2: DevTools del navegador (F12)

1. Abre DevTools: **F12**
2. Ve a **Network**
3. Haz una solicitud GET en Angular a `http://localhost:8080/api/saldos`
4. Click en la solicitud
5. Busca **Response Headers**
6. Verifica que exista:
   ```
   Access-Control-Allow-Origin: http://localhost:4200
   Access-Control-Allow-Credentials: true
   ```

### Opción 3: Console del navegador (F12 → Console)

```javascript
fetch('http://localhost:8080/api/saldos?page=0&size=10')
  .then(r => r.json())
  .then(d => console.log('✅ OK:', d))
  .catch(e => console.error('❌ Error:', e));
```

---

## 📚 Documentación Adicional

Se crearon 2 archivos más de referencia:

### 1. **CORS_GUIDE.md**
- Explicación detallada de CORS
- Cómo funciona el mecanismo preflight
- Configuración para producción
- Troubleshooting

**Leer en:** `E:\JAVAWEB\agent-core\CORS_GUIDE.md`

### 2. **ANGULAR_HTTP_EXAMPLES.md**
- Ejemplos de servicios HttpClient
- Componentes listos para copiar/pegar
- Interceptores para autenticación
- Manejo de errores

**Leer en:** `E:\JAVAWEB\agent-core\ANGULAR_HTTP_EXAMPLES.md`

---

## ⚠️ Importante: Seguridad en Producción

### ❌ NO hagas esto en producción:

```yaml
allowCredentials: true + allowedOrigins: ['*']
```

Esta combinación es insegura.

### ✅ Haz esto en producción:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "https://tudominio.com",      // Tu dominio real
    "https://app.tudominio.com"   // Subdominio si lo necesitas
));
```

O variables de entorno:

```yaml
# application-prod.yaml
cors:
  allowed-origins: https://tudominio.com,https://app.tudominio.com
```

---

## 🔧 Solucionar Problemas

### Problema: Sigue dando error de CORS

**Checklist:**

- [ ] ¿Recompilaste la app? `mvn clean compile`
- [ ] ¿Reiniciaste el servidor Spring Boot?
- [ ] ¿Angular está en `http://localhost:4200`?
- [ ] ¿API está en `http://localhost:8080`?
- [ ] ¿En DevTools aparece `Access-Control-Allow-Origin`?

**Habilitar logs de CORS:**

En `application.yaml`:
```yaml
logging:
  level:
    org.springframework.web.cors: DEBUG
```

Reinicia y mira los logs.

### Problema: Solo funciona con ciertos métodos

CorsConfig permite: `GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD`

Si tu endpoint usa otro método, agrega más a `allowedMethods()` en `CorsConfig.java`.

### Problema: No me deja enviar ciertos headers

En `CorsConfig.java`, modifica:
```java
configuration.setAllowedHeaders(Collections.singletonList("*"));
```

O especifica headers exactos:
```java
configuration.setAllowedHeaders(Arrays.asList(
    "Authorization",
    "Content-Type",
    "X-Custom-Header"
));
```

---

## 🎯 Endpoints disponibles para pruebas

Una vez que CORS funciona, puedes llamar a:

### Saldos
```
GET http://localhost:8080/api/saldos?page=0&size=10
POST http://localhost:8080/api/saldos
PUT http://localhost:8080/api/saldos/{id}
DELETE http://localhost:8080/api/saldos/{id}
```

### Entidades Financieras
```
GET http://localhost:8080/api/entidades-financieras?page=0&size=10
POST http://localhost:8080/api/entidades-financieras
PUT http://localhost:8080/api/entidades-financieras/{id}
DELETE http://localhost:8080/api/entidades-financieras/{id}
```

### Operaciones
```
GET http://localhost:8080/api/operaciones?page=0&size=10
POST http://localhost:8080/api/operaciones
PUT http://localhost:8080/api/operaciones/{id}
DELETE http://localhost:8080/api/operaciones/{id}
```

### Usuarios
```
GET http://localhost:8080/api/usuarios?page=0&size=10
POST http://localhost:8080/api/usuarios
PUT http://localhost:8080/api/usuarios/{id}
DELETE http://localhost:8080/api/usuarios/{id}
```

### Reportes
```
GET http://localhost:8080/api/reportes?page=0&size=10
POST http://localhost:8080/api/reportes
PUT http://localhost:8080/api/reportes/{id}
DELETE http://localhost:8080/api/reportes/{id}
```

---

## ✅ Resumen de la Solución

| Paso | Estado | Descripción |
|------|--------|------------|
| 1 | ✅ | Creada clase `CorsConfig.java` |
| 2 | ✅ | Configurados orígenes: `localhost:4200` y otros |
| 3 | ✅ | Permitidos métodos HTTP: GET, POST, PUT, DELETE, PATCH |
| 4 | ✅ | Permitidos headers: `*` (todos) |
| 5 | ✅ | Expuestos headers: Authorization, Content-Type, etc. |
| 6 | ✅ | Credenciales: Permitidas |
| 7 | ✅ | Compilación: Exitosa |
| 8 | 📖 | Guías: CORS_GUIDE.md + ANGULAR_HTTP_EXAMPLES.md |

---

## 📞 Próximos pasos

1. **Compila y ejecuta:** `mvn spring-boot:run`
2. **Arranca Angular:** `ng serve`
3. **Prueba la conexión** usando los ejemplos en `ANGULAR_HTTP_EXAMPLES.md`
4. **Verifica CORS** en DevTools → Network → Headers
5. **Revisa logs** si hay problemas

Si algo no funciona, revisa `CORS_GUIDE.md` para troubleshooting detallado.

---

**¡Listo! Tu API Spring Boot ahora está CORS-habilitada para Angular. 🎉**

