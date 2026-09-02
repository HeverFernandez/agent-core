# Ejemplos de Configuración de Cliente HTTP en Angular

## Paso 1: Importar HttpClient en el módulo

```typescript
// app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule  // ← Agrega esto
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

---

## Paso 2: Crear un servicio para la API

```typescript
// services/saldo.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SaldoService {
  
  private apiUrl = 'http://localhost:8080/api/saldos';

  constructor(private http: HttpClient) { }

  /**
   * Obtener saldos con paginación
   * @param page número de página (comienza en 0)
   * @param size cantidad de registros por página
   * @param sort formato: 'campo,asc' o 'campo,desc'
   * @returns Observable con los saldos paginados
   */
  getSaldos(page: number = 0, size: number = 100, sort: string = 'idSaldo,asc'): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<any>(this.apiUrl, { params });
  }

  /**
   * Obtener un saldo por ID
   */
  getSaldoById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  /**
   * Crear un nuevo saldo
   */
  crearSaldo(saldo: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, saldo);
  }

  /**
   * Actualizar un saldo
   */
  actualizarSaldo(id: number, saldo: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, saldo);
  }

  /**
   * Eliminar un saldo
   */
  eliminarSaldo(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
```

---

## Paso 3: Usar el servicio en un componente

### Ejemplo 1: Listar saldos

```typescript
// components/saldo-list/saldo-list.component.ts
import { Component, OnInit } from '@angular/core';
import { SaldoService } from '../../services/saldo.service';

@Component({
  selector: 'app-saldo-list',
  templateUrl: './saldo-list.component.html',
  styleUrls: ['./saldo-list.component.css']
})
export class SaldoListComponent implements OnInit {
  
  saldos: any[] = [];
  loading: boolean = false;
  error: string | null = null;
  page: number = 0;
  size: number = 10;

  constructor(private saldoService: SaldoService) { }

  ngOnInit(): void {
    this.cargarSaldos();
  }

  cargarSaldos(): void {
    this.loading = true;
    this.error = null;

    this.saldoService.getSaldos(this.page, this.size).subscribe({
      next: (response: any) => {
        console.log('✅ Saldos cargados:', response);
        this.saldos = response.content || response;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('❌ Error al cargar saldos:', err);
        this.error = 'No se pudieron cargar los saldos. Verifica que la API está activa.';
        this.loading = false;
      }
    });
  }

  siguientePagina(): void {
    this.page++;
    this.cargarSaldos();
  }

  paginaAnterior(): void {
    if (this.page > 0) {
      this.page--;
      this.cargarSaldos();
    }
  }
}
```

**HTML (saldo-list.component.html):**

```html
<div class="container">
  <h2>Listado de Saldos</h2>

  <!-- Mostrar error si existe -->
  <div *ngIf="error" class="alert alert-danger" role="alert">
    {{ error }}
  </div>

  <!-- Mostrar spinner mientras carga -->
  <div *ngIf="loading" class="spinner-border" role="status">
    <span class="sr-only">Cargando...</span>
  </div>

  <!-- Tabla de saldos -->
  <table *ngIf="!loading && saldos.length > 0" class="table table-striped">
    <thead>
      <tr>
        <th>ID</th>
        <th>Entidad Financiera ID</th>
        <th>Monto Inicial</th>
        <th>Monto Disponible</th>
        <th>Estado</th>
        <th>Acciones</th>
      </tr>
    </thead>
    <tbody>
      <tr *ngFor="let saldo of saldos">
        <td>{{ saldo.idSaldo }}</td>
        <td>{{ saldo.idEntidadFinanciera }}</td>
        <td>${{ saldo.montoInicial }}</td>
        <td>${{ saldo.montoDisponible }}</td>
        <td>{{ saldo.estado }}</td>
        <td>
          <button (click)="editarSaldo(saldo.idSaldo)" class="btn btn-sm btn-warning">Editar</button>
          <button (click)="eliminarSaldo(saldo.idSaldo)" class="btn btn-sm btn-danger">Eliminar</button>
        </td>
      </tr>
    </tbody>
  </table>

  <!-- Mensaje cuando no hay datos -->
  <div *ngIf="!loading && saldos.length === 0" class="alert alert-info">
    No hay saldos disponibles
  </div>

  <!-- Controles de paginación -->
  <nav *ngIf="!loading && saldos.length > 0">
    <button (click)="paginaAnterior()" [disabled]="page === 0" class="btn btn-primary">Anterior</button>
    <span class="mx-2">Página {{ page + 1 }}</span>
    <button (click)="siguientePagina()" class="btn btn-primary">Siguiente</button>
  </nav>
</div>
```

---

### Ejemplo 2: Crear saldo

```typescript
// components/saldo-form/saldo-form.component.ts
import { Component } from '@angular/core';
import { SaldoService } from '../../services/saldo.service';

@Component({
  selector: 'app-saldo-form',
  templateUrl: './saldo-form.component.html',
  styleUrls: ['./saldo-form.component.css']
})
export class SaldoFormComponent {
  
  saldo = {
    idEntidadFinanciera: null,
    montoInicial: 0,
    montoDisponible: 0,
    estado: 'ACTIVO'
  };

  loading: boolean = false;
  mensaje: string = '';
  tipoMensaje: string = ''; // 'success' o 'danger'

  constructor(private saldoService: SaldoService) { }

  crearSaldo(): void {
    if (!this.saldo.idEntidadFinanciera || this.saldo.montoInicial <= 0) {
      this.mostrarMensaje('Por favor completa todos los campos correctamente', 'danger');
      return;
    }

    this.loading = true;

    this.saldoService.crearSaldo(this.saldo).subscribe({
      next: (response: any) => {
        console.log('✅ Saldo creado:', response);
        this.mostrarMensaje('Saldo creado exitosamente', 'success');
        this.limpiarFormulario();
        this.loading = false;
      },
      error: (err: any) => {
        console.error('❌ Error al crear saldo:', err);
        this.mostrarMensaje('Error al crear el saldo: ' + err.error?.message, 'danger');
        this.loading = false;
      }
    });
  }

  limpiarFormulario(): void {
    this.saldo = {
      idEntidadFinanciera: null,
      montoInicial: 0,
      montoDisponible: 0,
      estado: 'ACTIVO'
    };
  }

  mostrarMensaje(msg: string, tipo: string): void {
    this.mensaje = msg;
    this.tipoMensaje = tipo;
    setTimeout(() => {
      this.mensaje = '';
    }, 3000);
  }
}
```

**HTML (saldo-form.component.html):**

```html
<div class="container mt-5">
  <div class="card">
    <div class="card-header bg-primary text-white">
      <h5>Crear Nuevo Saldo</h5>
    </div>
    <div class="card-body">
      <!-- Mensajes de feedback -->
      <div *ngIf="mensaje" [ngClass]="'alert alert-' + tipoMensaje" role="alert">
        {{ mensaje }}
      </div>

      <!-- Formulario -->
      <form (ngSubmit)="crearSaldo()" #form="ngForm">
        <div class="mb-3">
          <label for="entidad" class="form-label">Entidad Financiera ID:</label>
          <input 
            type="number" 
            class="form-control" 
            id="entidad"
            [(ngModel)]="saldo.idEntidadFinanciera"
            name="idEntidadFinanciera"
            required>
        </div>

        <div class="mb-3">
          <label for="montoInicial" class="form-label">Monto Inicial:</label>
          <input 
            type="number" 
            class="form-control" 
            id="montoInicial"
            [(ngModel)]="saldo.montoInicial"
            name="montoInicial"
            min="0"
            step="0.01"
            required>
        </div>

        <div class="mb-3">
          <label for="montoDisponible" class="form-label">Monto Disponible:</label>
          <input 
            type="number" 
            class="form-control" 
            id="montoDisponible"
            [(ngModel)]="saldo.montoDisponible"
            name="montoDisponible"
            min="0"
            step="0.01"
            required>
        </div>

        <div class="mb-3">
          <label for="estado" class="form-label">Estado:</label>
          <select class="form-control" id="estado" [(ngModel)]="saldo.estado" name="estado">
            <option>ACTIVO</option>
            <option>INACTIVO</option>
            <option>VENCIDO</option>
          </select>
        </div>

        <button 
          type="submit" 
          class="btn btn-success"
          [disabled]="loading || !form.valid">
          <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
          {{ loading ? 'Creando...' : 'Crear Saldo' }}
        </button>
        <button type="button" (click)="limpiarFormulario()" class="btn btn-secondary ms-2">
          Limpiar
        </button>
      </form>
    </div>
  </div>
</div>
```

---

### Ejemplo 3: Usar HttpInterceptor para agregar token (Autenticación)

```typescript
// interceptors/auth.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Obtener token del localStorage (guárdalo después de login)
    const token = localStorage.getItem('access_token');

    if (token) {
      // Clonar la solicitud y añadir header Authorization
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
    }

    return next.handle(req);
  }
}
```

**Registrar el interceptor en el módulo:**

```typescript
// app.module.ts
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';

@NgModule({
  imports: [HttpClientModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})
export class AppModule { }
```

---

## Prueba Rápida en la Consola del Navegador

Si quieres probar sin un servicio completo:

```javascript
// En DevTools Console (F12)
fetch('http://localhost:8080/api/saldos?page=0&size=10')
  .then(response => response.json())
  .then(data => console.log('✅ Datos:', data))
  .catch(error => console.error('❌ Error:', error));
```

---

## Checklist: Angular + API Spring Boot

- [ ] Importé `HttpClientModule` en `app.module.ts`
- [ ] Creé un servicio con `HttpClient`
- [ ] El servicio usa la URL correcta: `http://localhost:8080/api/...`
- [ ] La API tiene `CorsConfig.java` configurado
- [ ] La API está corriendo en puerto 8080
- [ ] Angular está corriendo en puerto 4200
- [ ] En DevTools → Network veo headers CORS en la respuesta
- [ ] No hay error "No 'Access-Control-Allow-Origin' header"

---

## Referencias

- [Angular HttpClient Documentation](https://angular.io/guide/http)
- [Angular HttpClientModule](https://angular.io/api/common/http/HttpClientModule)
- [RxJS Observable](https://rxjs.dev/guide/observable)

