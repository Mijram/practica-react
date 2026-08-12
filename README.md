# Curso de JPA / Hibernate con Spring Data

Curso práctico de **6 módulos** sobre un proyecto Spring Boot real con **arquitectura en capas** (`controller`, `model`, `repository`, `service`) y **PostgreSQL**, pensado para trabajar en **IntelliJ IDEA**.

Cada módulo trae **1 ejemplo funcionando y comentado línea por línea** y **2 ejercicios** que se resuelven dentro del propio proyecto.

---

## Temario

| # | Módulo | Duración | Ejercicios |
|---|---|---|---|
| 01 | JPA, Hibernate y tu primera entidad | 15 min | Completar `Proveedor` · Diseñar `Almacen` desde cero |
| 02 | Relaciones entre entidades | 20 min | Mapear `Resena` y su lado inverso · Cazar y eliminar un N+1 |
| 03 | Repositorios con Spring Data JPA | 15 min | Completar 4 métodos del servicio · 6 query methods + repositorio nuevo |
| 04 | Consultas con `@Query` y JPQL | 15 min | 10 consultas JPQL · Completar `PedidoService` y medir el coste |
| 05 | Paginación en práctica | 15 min | Buscador paginado completo · `Page` vs `Slice` y patrón de dos pasos |
| 06 | Checklist y proyecto integrador | 10 min | Panel de control (`ReporteService`) · Auditoría del proyecto |

---

## Puesta en marcha

**1. Crear la base de datos** (obligatorio: la app arranca con `ddl-auto=validate` y **no** crea las tablas sola):

```bash
createdb -U postgres tienda_jpa
psql -U postgres -d tienda_jpa -f sql/01_crear_base_datos.sql
psql -U postgres -d tienda_jpa -f sql/02_datos_iniciales.sql

# comprobación: debe dar 79
psql -U postgres -d tienda_jpa -c "SELECT COUNT(*) FROM pedidos;"
```

**2. Ajustar la conexión** en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tienda_jpa
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASENA
```

**3. Abrir en IntelliJ**: `File → Open…` y selecciona la **carpeta** `CursoJPA`. IntelliJ detecta el `pom.xml` y descarga las dependencias. Después abre `TiendaApplication.java` y pulsa el triángulo verde.

```bash
# o desde la terminal
mvn spring-boot:run
curl http://localhost:8080/api/productos
```

**Requisitos:** JDK 17+ (configurado para 21), PostgreSQL 12+, IntelliJ IDEA (Community basta).

---

## Arquitectura en capas

```
src/main/java/com/curso/tienda/
├── TiendaApplication.java     <-- clase con el main
├── model/                     <-- ENTIDADES JPA
│   └── dto/                   <-- objetos de transporte
├── repository/                <-- interfaces JpaRepository
├── service/                   <-- reglas de negocio y transacciones
└── controller/                <-- endpoints REST
```

| Capa | Responsabilidad | Lo que **no** debe hacer |
|---|---|---|
| `model` | Mapeo a tablas y lógica que depende solo de una fila | No conoce repositorios, servicios ni HTTP |
| `model/dto` | Objetos planos que viajan hacia dentro y fuera de la API | Sin anotaciones JPA ni lógica de negocio |
| `repository` | Solo consultas | No abre transacciones ni aplica reglas |
| `service` | Reglas, coordinación, transacciones y conversión a DTO | No sabe de HTTP ni de códigos de estado |
| `controller` | Recibir, validar formato, llamar al servicio, responder | No accede a repositorios ni devuelve entidades |

**El punto que más importa:** la conversión a DTO ocurre **dentro** de la transacción, en el servicio. Si el servicio devolviera entidades y el controlador las convirtiera, la transacción ya estaría cerrada y cualquier relación `LAZY` lanzaría `LazyInitializationException`.

---

## La base de datos

Nueve tablas que modelan una tienda, con casos borde **a propósito**: un ejercicio de `LEFT JOIN` sobre datos perfectos no enseña nada.

| Tabla | Filas | Particularidad |
|---|---|---|
| `categorias` | 8 | Una inactiva |
| `proveedores` | 7 | Uno inactivo, uno sin email |
| `productos` | 33 | Dos descontinuados, tres sin proveedor |
| `usuarios` | 17 | **Dos nunca han hecho un pedido** |
| `pedidos` | 79 | Cinco estados, guardados como texto |
| `detalle_pedido` | 199 | **Precio histórico**, distinto del actual |
| `resenas` | 39 | La entidad la escribes tú (Módulo 2) |
| `etiquetas` + `producto_etiqueta` | 5 + 31 | Relación N:N real |

---

## Tres cosas que este curso enseña ejecutando, no describiendo

**El problema N+1 se mide.** El Módulo 2 activa las estadísticas de Hibernate y cuenta las consultas: listar 31 productos con su categoría cuesta **32 consultas** sin `JOIN FETCH` y **1** con él.

**`COUNT` sin `DISTINCT` infla los informes.** Sobre los datos del curso, contar pedidos uniendo con sus líneas da **199** en vez de **79**. Es un informe que parece razonable y está al triple.

**La transacción se demuestra rompiéndola.** El ejercicio 4.2 pide crear un pedido de tres líneas donde la tercera no tenga stock, y comprobar con una consulta que el stock de las dos primeras **no** se descontó.

---

## Qué está verificado y qué no

Sé preciso con esto porque importa.

**Verificado contra herramientas reales:**
- Los 27 archivos `.java` **compilan** (`javac`, Java 21).
- Los dos scripts SQL se ejecutan **sin errores en PostgreSQL 16**, y los conteos de este README salen de esa base de datos.
- **Simulé la validación de esquema** que hace Hibernate con `ddl-auto=validate`: para cada entidad deduje el nombre físico de cada columna persistente —explícito con `@Column`, o implícito aplicando la misma conversión camelCase → snake_case que usa Spring Boot— y comprobé que exista en la tabla real. Las 7 entidades validan.
- La lógica de cada consulta JPQL se comprobó ejecutando su equivalente SQL contra los datos cargados.

**NO verificado:** la aplicación **no se ha arrancado**. El entorno donde se generó el curso tiene bloqueado Maven Central y no puede descargar Spring Boot, así que compilé contra stubs de la API de JPA y Spring. Eso valida sintaxis, tipos, genéricos y firmas, pero **no** el comportamiento en tiempo de ejecución.

**Cuatro defectos que ese método ocultó, encontrados en una auditoría posterior y ya corregidos:**

| Defecto | Consecuencia | Corrección |
|---|---|---|
| `Proveedor` no era `@Entity`, pero `Producto` tiene un `@ManyToOne` hacia ella | La aplicación **no arrancaba**: `AnnotationException` | `Proveedor` es entidad; el ejercicio pasó a ser el mapeo explícito y la relación inversa |
| Su campo `List<Producto>` sin anotar | `Could not determine recommended JdbcType` al arrancar | Marcado `@Transient` con el TODO de sustituirlo por `@OneToMany` |
| `@ControllerAdvice` en vez de `@RestControllerAdvice` | Los errores devolvían 500 en lugar de 404/409: Spring buscaba una vista llamada `ErrorRespuesta` | Cambiado a `@RestControllerAdvice` |
| `HttpStatus.ordinal()` en vez de `.value()` | El JSON de error traía códigos absurdos (3, 5) en lugar de 404 y 409 | Cambiado a `.value()` |

Ninguno de los cuatro produce error de compilación: todos fallan al arrancar o en ejecución. Es exactamente la clase de fallo que un compilador no puede ver.

**Riesgo residual que no puedo descartar sin ejecutar:** el patrón `(:estado IS NULL OR p.estado = :estado)` con un parámetro `enum` en `PedidoRepository.buscar` es un punto de fricción conocido entre Hibernate y el driver de PostgreSQL. Si al filtrar pedidos ves `could not determine data type of parameter`, la solución es pasar el enum como `String` y comparar con `CAST`, o partir el método en dos consultas.

## Método de trabajo

1. Lee la sección del módulo en su documento Word.
2. Abre los archivos del ejemplo y léelos **enteros**: los comentarios del código son la explicación real, el documento es el mapa.
3. Arranca la aplicación y prueba los endpoints.
4. **Mira la consola.** Con `show-sql` activado verás el SQL que Hibernate genera para cada petición. Es la mitad del aprendizaje.
5. **Rompe el ejemplo a propósito**: cambia un `LAZY` por `EAGER`, quita un `DISTINCT`, borra un `@Transactional`. Observa y deshaz.
6. Resuelve los ejercicios donde dice `TODO`, arrancando con frecuencia.

Los archivos de ejercicio **compilan y arrancan tal como están**: los métodos por completar devuelven valores neutros, y las entidades sin mapear (`Proveedor`, `Resena`) son clases Java normales que Hibernate ignora. Puedes completarlos de uno en uno sin que el proyecto se rompa.

---

## Documentos (carpeta `docs/`)

| Documento | Contenido |
|---|---|
| `00_Guia_General_del_Curso.docx` | Instalación de PostgreSQL, cómo abrir en IntelliJ, arquitectura en capas, flujo de una petición, 10 errores frecuentes, rúbrica |
| `Modulo01_JPA_Hibernate_y_primera_entidad.docx` | Teoría, conceptos, explicación del ejemplo y los 2 enunciados |
| `Modulo02_Relaciones_entre_entidades.docx` | ídem |
| `Modulo03_Repositorios_Spring_Data_JPA.docx` | ídem |
| `Modulo04_Consultas_Query_y_JPQL.docx` | ídem |
| `Modulo05_Paginacion.docx` | ídem |
| `Modulo06_Checklist_y_proyecto_integrador.docx` | ídem |

Cada archivo `.java` indica en su cabecera **a qué módulo pertenece**, **qué documento** contiene su enunciado y **qué archivo** consultar como referencia.
