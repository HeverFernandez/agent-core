# Guía de CORS (Cross-Origin Resource Sharing)

## ¿Qué es CORS?

CORS es un mecanismo de seguridad que permite a aplicaciones web (como Angular) acceder a recursos de un servidor en un dominio diferente. El error que recibiste es la protección del navegador bloqueando la solicitud.

## Solución Implementada

Se creó la clase `CorsConfig.java` en `src/main/java/com/aitamh/agent/core/config/` que configura globalmente CORS en la API Spring Boot.

### Archivo: CorsConfig.java

Esta clase proporciona dos formas de configurar CORS:

#### 1. **Configuración mediante WebMvcConfigurer** (Método 1)
Implementa `WebMvcConfigurer` y sobrescribe el método `addCorsMappings()`:
- No requiere dependencia adicional de Spring Security
- Configura CORS a nivel de Servlet/Spring Web

#### 2. **Configuración mediante CorsConfigurationSource** (Método 2 - Bean)
Define un bean `CorsConfigurationSource`:
- Recomendado si usas Spring Security
- Proporciona control más granular

## Configuración Actual

### Orígenes Permitidos (allowedOrigins)
```
- http://localhost:4200    (Angular CLI - desarrollo)
- http://127.0.0.1:4200    (Variante alternativa)
- http://localhost:3000    (Otros puertos de desarrollo)
- http://localhost:5173    (Vite frontend)
```

### Métodos HTTP Permitidos
```
GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD
```

### Headers Permitidos
```
Todos (*) en solicitudes (allowedHeaders)
Específicos en respuestas (exposedHeaders):
  - Authorization (para JWT, Bearer tokens)
  - Content-Type
  - X-Total-Count (paginación)
  - X-Page-Number (paginación)
  - X-Page-Size (paginación)
```

### Credenciales
```
allowCredentials = true (permite cookies y autenticación)
```

### Cacheo de Preflight
```
maxAge = 3600 segundos (1 hora)
El navegador cachea la respuesta preflight durante 1 hora
```

## Cómo Funciona

### Solicitud Preflight (OPTIONS)
Cuando Angular hace una solicitud desde `http://localhost:4200` a `http://localhost:8080`:

1. El navegador envía una solicitud **OPTIONS** (preflight) automáticamente
2. La solicitud incluye headers como:
   - `Origin: http://localhost:4200`
   - `Access-Control-Request-Method: GET` (o POST, etc.)
3. El servidor responde con headers CORS
4. Si el navegador ve `Access-Control-Allow-Origin: http://localhost:4200`, permite la solicitud real

### Headers de Respuesta CORS
```
Access-Control-Allow-Origin: http://localhost:4200
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD
Access-Control-Allow-Headers: [los headers permitidos]
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

## Uso en Angular

### Ejemplo 1: GET simple
```typescript
// app.service.ts
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class SaldoService {
  private apiUrl = 'http://localhost:8080/api/saldos';

  constructor(private http: HttpClient) {}

  getSaldos(page = 0, size = 100): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'idSaldo,asc');

    return this.http.get<any>(`${this.apiUrl}`, { params });
  }
}
```

### Ejemplo 2: POST con autenticación
```typescript
crearSaldo(saldo: any): Observable<any> {
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${this.token}`
  });

  return this.http.post<any>(`${this.apiUrl}`, saldo, { headers });
}
```

## Configuración Productiva

**IMPORTANTE:** Para producción, cambia los orígenes permitidos a domios reales:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "https://tudominio.com",
    "https://app.tudominio.com",
    "https://www.tudominio.com"
));
```

### Opción: Usar variables de entorno

Modifica `CorsConfig.java` para leer orígenes desde propiedades:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed-origins:http://localhost:4200,http://localhost:3000}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(allowedOrigins.split(","))
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

Y en `application.yaml`:

```yaml
cors:
  allowed-origins: http://localhost:4200,http://localhost:3000
```

O en `application-prod.yaml`:

```yaml
cors:
  allowed-origins: https://tudominio.com,https://app.tudominio.com
```

## Resolver el Error Original

El error:
```
Access to XMLHttpRequest at 'http://localhost:8080/api/saldos...' 
from origin 'http://localhost:4200' has been blocked by CORS policy
```

Se resolvió con:

✅ Crear `CorsConfig.java` con configuración de CORS
✅ Permitir origen `http://localhost:4200`
✅ Permitir métodos necesarios (GET, POST, PUT, DELETE)
✅ Permitir headers necesarios

## Verificación

### 1. En el navegador (DevTools)
```
F12 → Network → Click en tu solicitud GET/POST
Headers → Response Headers
```

Deberías ver:
```
Access-Control-Allow-Origin: http://localhost:4200
Access-Control-Allow-Credentials: true
```

### 2. Prueba simple en Angular

```typescript
// component.ts
ngOnInit() {
  this.http.get('http://localhost:8080/api/saldos').subscribe(
    (data) => console.log('✅ CORS OK:', data),
    (error) => console.error('❌ CORS Error:', error)
  );
}
```

## Seguridad

⚠️ **Advertencia:** La configuración actual permite:
- `allowCredentials: true` + `allowedOrigins: *` = **INSEGURO en producción**

**Para producción:**
1. Especifica orígenes exactos (no uses `*`)
2. Set `allowCredentials: false` si no necesitas cookies/autenticación
3. Usa HTTPS en lugar de HTTP
4. Considera usar un gateway API (API Gateway, Kong, etc.)

## Debug: ¿Todavía no funciona?

### Checklist:
- [ ] ¿Compilaste la aplicación? (`mvn clean compile`)
- [ ] ¿Reiniciaste el servidor Spring Boot?
- [ ] ¿Angular está en `http://localhost:4200`?
- [ ] ¿API está en `http://localhost:8080`?
- [ ] ¿DevTools muestra `Access-Control-Allow-Origin` en respuesta?

### Logs:
Habilita logs de CORS en `application.yaml`:
```yaml
logging:
  level:
    org.springframework.web.cors: DEBUG
```

Reinicia la app y mira los logs sobre CORS.

## Referencias

- [MDN - CORS](https://developer.mozilla.org/es/docs/Web/HTTP/CORS)
- [Spring Boot CORS Documentation](https://spring.io/guides/gs/rest-service-cors/)

