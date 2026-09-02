# Script de Prueba de CORS y Conectividad API

## Opción 1: Usando PowerShell (Windows)

Crea un archivo `test-cors.ps1`:

```powershell
# test-cors.ps1

Write-Host "=== PRUEBA DE CORS Y CONECTIVIDAD ==="
Write-Host ""

# Variables
$apiUrl = "http://localhost:8080"
$endpoint = "/api/saldos"
$port = 8080

# 1. Verificar que la API está activa
Write-Host "[1/3] Verificando que la API está activa en puerto $port..."
try {
    $response = Invoke-WebRequest -Uri "$apiUrl/swagger-ui.html" -TimeoutSec 5 -SkipHttpErrorCheck
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ API Activa en $apiUrl"
    } else {
        Write-Host "⚠️  API respondió con código $($response.StatusCode)"
    }
} catch {
    Write-Host "❌ API no responde. ¿Está ejecutándose en puerto $port?"
    Write-Host "   Ejecuta: mvn spring-boot:run"
    exit
}

Write-Host ""

# 2. Probar solicitud sin verificación de origen (servidor)
Write-Host "[2/3] Probando solicitud a $endpoint..."
try {
    $response = Invoke-WebRequest -Uri "$apiUrl$endpoint?page=0&size=10" -TimeoutSec 5 -SkipHttpErrorCheck
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Solicitud exitosa (Status: $($response.StatusCode))"
        Write-Host "   Primeros 200 caracteres de la respuesta:"
        Write-Host "   $($response.Content.Substring(0, [Math]::Min(200, $response.Content.Length)))"
    } else {
        Write-Host "⚠️  Status: $($response.StatusCode)"
    }
} catch {
    Write-Host "❌ Error en la solicitud: $_"
}

Write-Host ""

# 3. Verificar headers CORS
Write-Host "[3/3] Verificando headers CORS..."
try {
    $response = Invoke-WebRequest -Uri "$apiUrl$endpoint?page=0&size=10" -TimeoutSec 5
    
    $corsHeader = $response.Headers.'Access-Control-Allow-Origin'
    $credentialsHeader = $response.Headers.'Access-Control-Allow-Credentials'
    $methodsHeader = $response.Headers.'Access-Control-Allow-Methods'
    
    if ($corsHeader) {
        Write-Host "✅ CORS Configurado"
        Write-Host "   Access-Control-Allow-Origin: $corsHeader"
        Write-Host "   Access-Control-Allow-Credentials: $credentialsHeader"
        Write-Host "   Access-Control-Allow-Methods: $methodsHeader"
    } else {
        Write-Host "⚠️  No se encontró header Access-Control-Allow-Origin"
        Write-Host "   Asegúrate que CorsConfig.java está en el proyecto"
    }
} catch {
    Write-Host "❌ Error al obtener headers: $_"
}

Write-Host ""
Write-Host "=== RESUMEN ==="
Write-Host "✅ Si todo está en verde, Angular en localhost:4200 puede acceder a la API"
Write-Host "❌ Si hay errores, revisa la consola de Spring Boot"
Write-Host ""
Write-Host "Para ejecutar este script:"
Write-Host "  .\test-cors.ps1"
```

**Ejecutar:**
```powershell
cd E:\JAVAWEB\agent-core
.\test-cors.ps1
```

---

## Opción 2: Usando Git Bash o PowerShell con curl

### Probar conectividad básica

```bash
curl -v http://localhost:8080/api/saldos?page=0&size=10
```

Busca en la salida:
```
* Connected to localhost (127.0.0.1) port 8080
< HTTP/1.1 200
< Access-Control-Allow-Origin: http://localhost:4200
```

### Probar solicitud OPTIONS (preflight)

```bash
curl -X OPTIONS \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v http://localhost:8080/api/saldos
```

Resultado esperado:
```
< HTTP/1.1 200
< Access-Control-Allow-Origin: http://localhost:4200
< Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD
< Access-Control-Allow-Headers: *
< Access-Control-Allow-Credentials: true
< Access-Control-Max-Age: 3600
```

---

## Opción 3: Test completo en Node.js

Crea un archivo `test-cors.js`:

```javascript
// test-cors.js
const http = require('http');

console.log('=== PRUEBA DE CORS Y CONECTIVIDAD ===\n');

const options = {
  hostname: 'localhost',
  port: 8080,
  path: '/api/saldos?page=0&size=10',
  method: 'GET',
  headers: {
    'Origin': 'http://localhost:4200'
  }
};

const req = http.request(options, (res) => {
  console.log('[✓] Conexión exitosa\n');
  console.log(`Status: ${res.statusCode}\n`);
  
  console.log('Headers CORS encontrados:');
  console.log(`  Access-Control-Allow-Origin: ${res.headers['access-control-allow-origin']}`);
  console.log(`  Access-Control-Allow-Methods: ${res.headers['access-control-allow-methods']}`);
  console.log(`  Access-Control-Allow-Credentials: ${res.headers['access-control-allow-credentials']}`);
  
  if (res.headers['access-control-allow-origin'] === 'http://localhost:4200') {
    console.log('\n✅ CORS está correctamente configurado');
  } else {
    console.log('\n⚠️  CORS no parece estar configurado');
  }

  let data = '';
  res.on('data', chunk => { data += chunk; });
  res.on('end', () => {
    console.log('\n📊 Primeros 200 caracteres de la respuesta:');
    console.log(data.substring(0, 200));
  });
});

req.on('error', (e) => {
  console.log(`❌ Error de conexión: ${e.message}`);
  console.log('¿API no está corriendo? Ejecuta: mvn spring-boot:run');
});

req.end();
```

**Ejecutar:**
```bash
node test-cors.js
```

---

## Opción 4: Test directo en Angular

Crea un componente test-cors:

```typescript
// test-cors.component.ts
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-test-cors',
  templateUrl: './test-cors.component.html',
  styleUrls: ['./test-cors.component.css']
})
export class TestCorsComponent implements OnInit {

  resultado: any = {
    conexion: null,
    headers: null,
    datos: null,
    error: null
  };

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.testearCORS();
  }

  testearCORS() {
    console.log('[1/3] Intentando conectar a http://localhost:8080/api/saldos...');

    this.http.get('http://localhost:8080/api/saldos?page=0&size=10', {
      observe: 'response',
      headers: new HttpHeaders({
        'Origin': 'http://localhost:4200'
      })
    }).subscribe(
      (response: any) => {
        console.log('[✓] Conexión exitosa');
        console.log('[✓] Status:', response.status);

        this.resultado.conexion = '✅ Conexión exitosa';
        this.resultado.headers = {
          'Access-Control-Allow-Origin': response.headers.get('Access-Control-Allow-Origin'),
          'Access-Control-Allow-Methods': response.headers.get('Access-Control-Allow-Methods'),
          'Access-Control-Allow-Credentials': response.headers.get('Access-Control-Allow-Credentials')
        };
        this.resultado.datos = response.body;

        console.log('[✓] CORS OK');
      },
      (error: any) => {
        console.error('[❌] Error:', error);
        this.resultado.error = `Error: ${error.message}`;
        this.resultado.conexion = '❌ Error de CORS o conexión';
      }
    );
  }
}
```

**HTML:**
```html
<div class="container mt-5">
  <h2>Test de CORS</h2>

  <div *ngIf="resultado.conexion" [ngClass]="{
    'alert alert-success': resultado.conexion?.includes('✅'),
    'alert alert-danger': resultado.conexion?.includes('❌')
  }">
    {{ resultado.conexion }}
  </div>

  <div *ngIf="resultado.headers" class="card mt-3">
    <div class="card-header">Headers CORS</div>
    <div class="card-body">
      <pre>{{ resultado.headers | json }}</pre>
    </div>
  </div>

  <div *ngIf="resultado.datos" class="card mt-3">
    <div class="card-header">Datos de la API</div>
    <div class="card-body">
      <pre>{{ resultado.datos | json }}</pre>
    </div>
  </div>

  <div *ngIf="resultado.error" class="alert alert-danger mt-3">
    {{ resultado.error }}
  </div>
</div>
```

---

## Checklist de Pruebas

Ejecuta en este orden:

- [ ] **Paso 1:** Compila Spring Boot
  ```powershell
  mvn clean compile
  ```

- [ ] **Paso 2:** Ejecuta Spring Boot
  ```powershell
  mvn spring-boot:run
  ```
  Espera a ver: `Started AgentCoreApplication`

- [ ] **Paso 3:** Prueba conectividad
  ```powershell
  # Opción 1: PowerShell
  .\test-cors.ps1
  
  # Opción 2: curl
  curl http://localhost:8080/api/saldos?page=0&size=10
  ```

- [ ] **Paso 4:** Arranca Angular
  ```powershell
  ng serve
  ```

- [ ] **Paso 5:** Abre Angular en el navegador
  ```
  http://localhost:4200
  ```

- [ ] **Paso 6:** Abre DevTools (F12) y verifica en Console
  ```javascript
  // En la consola del navegador
  fetch('http://localhost:8080/api/saldos?page=0&size=10')
    .then(r => r.json())
    .then(d => console.log('✅ CORS OK:', d))
    .catch(e => console.error('❌ Error:', e));
  ```

---

## Resultado Esperado

Cuando CORS funciona correctamente:

```
✅ API está activa en http://localhost:8080
✅ Solicitud exitosa (Status: 200)
✅ CORS Configurado
   Access-Control-Allow-Origin: http://localhost:4200
   Access-Control-Allow-Credentials: true
   Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD
```

---

## Troubleshooting Rápido

| Problema | Solución |
|----------|----------|
| "Connection refused" | API no está corriendo → `mvn spring-boot:run` |
| No hay headers CORS | CorsConfig.java no se compiló → `mvn clean compile` |
| Status 500 error | Revisa logs de Spring Boot |
| CORS aún bloqueado | Reinicia API y Angular, limpia caché del navegador |


