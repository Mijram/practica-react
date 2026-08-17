package com.curso.tienda.repository;

import com.curso.tienda.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * MODULO 3 — EJEMPLO: query methods.
 * MODULO 4 — EJEMPLO: @Query con JPQL, JOIN FETCH y proyecciones.
 * MODULO 5 — EJEMPLO: paginacion con Pageable.
 * Documentos: Modulo03_Repositorios_Spring_Data_JPA.docx
 *             Modulo04_Consultas_Query_y_JPQL.docx
 *             Modulo05_Paginacion.docx
 *
 * Esto es una INTERFAZ. No hay ninguna clase que la implemente, y no
 * tienes que escribirla: Spring Data genera la implementacion en tiempo
 * de arranque, mirando el nombre de cada metodo.
 *
 * @Repository es opcional aqui (Spring detecta las interfaces que
 * extienden JpaRepository de todos modos), pero se pone por claridad y
 * porque activa la traduccion de excepciones de JDBC a excepciones de
 * Spring.
 *
 * Al extender JpaRepository<Producto, Long> ya heredas gratis:
 *     findAll()            findAll(Pageable)     findById(Long)
 *     save(Producto)       saveAll(...)          deleteById(Long)
 *     existsById(Long)     count()               flush()
 * Todo eso sin escribir una linea.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // =================================================================
    //  MODULO 3 — QUERY METHODS
    // =================================================================
    //  Spring lee el NOMBRE del metodo y genera el SQL. La gramatica es:
    //
    //     find / read / get / count / exists / delete
    //       + By
    //       + Propiedad
    //       + Operador  (Is, Not, Like, Containing, Between, GreaterThan,
    //                    LessThan, After, Before, True, False, In, IsNull...)
    //       + And / Or + otra propiedad
    //       + OrderBy + Propiedad + Asc/Desc
    //
    //  Las propiedades son ATRIBUTOS DE LA ENTIDAD, no columnas de la
    //  tabla. findByFechaAlta funciona; findByFecha_alta no.
    //
    //  Si escribes mal un nombre, la aplicacion NO ARRANCA y el error
    //  dice exactamente que propiedad no encontro. Es un fallo temprano
    //  y explicito, que es justo lo que quieres.

    /** WHERE activo = ? */
    List<Producto> findByActivoTrue();

    /** WHERE nombre = ? — devuelve Optional porque puede no existir. */
    Optional<Producto> findByNombre(String nombre);

    /**
     * WHERE LOWER(nombre) LIKE LOWER('%?%')
     * Containing envuelve el valor en comodines; IgnoreCase aplica LOWER
     * a los dos lados. Es la busqueda tipica de un buscador.
     */
    List<Producto> findByNombreContainingIgnoreCase(String texto);

    /** WHERE precio BETWEEN ? AND ? — incluye ambos extremos. */
    List<Producto> findByPrecioBetween(BigDecimal minimo, BigDecimal maximo);

    /** WHERE categoria_id = ? AND activo = true ORDER BY precio ASC */
    List<Producto> findByCategoriaIdAndActivoTrueOrderByPrecioAsc(Long categoriaId);

    /**
     * Navegar por una relacion se hace con el nombre del atributo:
     * "Categoria" + "Nombre" recorre producto.categoria.nombre y genera
     * el JOIN solo.
     */
    List<Producto> findByCategoriaNombre(String nombreCategoria);

    /** WHERE stock < ? — para el reporte de reposicion. */
    List<Producto> findByStockLessThan(int limite);

    /** COUNT(*) WHERE categoria_id = ? */
    long countByCategoriaId(Long categoriaId);

    /** EXISTS — mas barato que traer la fila solo para ver si esta. */
    boolean existsByNombre(String nombre);

    /** Los N mas caros. Top/First limitan el resultado sin Pageable. */
    List<Producto> findTop5ByActivoTrueOrderByPrecioDesc();


    // =================================================================
    //  MODULO 4 — @Query CON JPQL
    // =================================================================
    //  Cuando el nombre del metodo se vuelve ilegible
    //  (findByActivoTrueAndCategoriaIdAndPrecioBetweenOrderByNombreAsc),
    //  o cuando necesitas algo que la gramatica no cubre, se usa @Query.
    //
    //  JPQL NO ES SQL. Opera sobre ENTIDADES y sus ATRIBUTOS:
    //      SQL :  SELECT * FROM productos p WHERE p.categoria_id = 3
    //      JPQL:  SELECT p FROM Producto p WHERE p.categoria.id = 3
    //  "Producto" es la clase, no la tabla. Distingue mayusculas.

    /**
     * Parametros CON NOMBRE, enlazados con @Param.
     *
     * NUNCA construyas una consulta concatenando strings. JPQL con
     * parametros es inmune a la inyeccion; la concatenacion no.
     */
    @Query("""
           SELECT p FROM Producto p
           WHERE p.activo = true
             AND p.categoria.nombre = :categoria
             AND p.precio <= :precioMaximo
           ORDER BY p.precio ASC
           """)
    List<Producto> buscarPorCategoriaYPrecio(@Param("categoria") String categoria,
                                             @Param("precioMaximo") BigDecimal precioMaximo);

    /**
     * JOIN FETCH — LA SOLUCION AL PROBLEMA N+1.
     *
     * findAll() trae los productos y, si luego recorres la lista pidiendo
     * p.getCategoria().getNombre(), Hibernate lanza UNA consulta extra por
     * cada producto: 1 + N consultas. Con 33 productos son 34 viajes a la
     * base de datos para algo que cabe en uno.
     *
     * JOIN FETCH le dice a Hibernate que traiga la relacion EN LA MISMA
     * consulta. El resultado es identico; el numero de consultas, no.
     *
     * Activa spring.jpa.properties.hibernate.generate_statistics=true en
     * application.properties y compara el conteo con y sin este metodo.
     */
    @Query("""
           SELECT p FROM Producto p
           JOIN FETCH p.categoria
           LEFT JOIN FETCH p.proveedor
           WHERE p.activo = true
           """)
    List<Producto> findActivosConCategoria();
    //  Fijate en el LEFT del segundo: proveedor puede ser null, y con un
    //  JOIN FETCH normal desaparecerian los productos sin proveedor.

    /** Subconsulta: productos por encima del precio medio de su categoria. */
    @Query("""
           SELECT p FROM Producto p
           WHERE p.precio > (SELECT AVG(p2.precio) FROM Producto p2
                             WHERE p2.categoria = p.categoria)
           ORDER BY p.categoria.nombre, p.precio DESC
           """)
    List<Producto> findPorEncimaDelPromedioDeSuCategoria();

    /**
     * PROYECCION: la consulta no devuelve entidades, sino tuplas.
     * Object[] es el tipo mas simple, aunque poco elegante: hay que
     * castear por posicion. La alternativa buena es una proyeccion por
     * constructor (ver findResumenPorCategoria mas abajo).
     */
    @Query("""
           SELECT p.categoria.nombre, COUNT(p), AVG(p.precio)
           FROM Producto p
           WHERE p.activo = true
           GROUP BY p.categoria.nombre
           ORDER BY COUNT(p) DESC
           """)
    List<Object[]> contarPorCategoria();

    /**
     * SQL NATIVO: nativeQuery = true.
     *
     * Aqui SI escribes nombres de TABLA y de COLUMNA reales, y puedes usar
     * cualquier cosa propia de PostgreSQL. El precio es la portabilidad:
     * esta consulta no funcionaria en MySQL sin retocarla.
     *
     * Usa nativo solo cuando JPQL no llegue. Casi nunca hace falta.
     */
    @Query(value = """
           SELECT p.* FROM productos p
           WHERE p.activo = true
             AND p.stock > 0
             AND p.precio < (SELECT AVG(precio) * 0.8 FROM productos WHERE activo = true)
           ORDER BY p.precio ASC
           """, nativeQuery = true)
    List<Producto> findOfertasNativo();

    /**
     * @Modifying es OBLIGATORIO en las consultas que escriben.
     * Sin ella, Hibernate intenta ejecutar el UPDATE como si fuera un
     * SELECT y lanza una excepcion.
     *
     * clearAutomatically = true limpia el contexto de persistencia
     * despues, para que los objetos que ya tenias en memoria no queden
     * con el valor viejo.
     *
     * ADVERTENCIA: un UPDATE asi va DIRECTO a la base de datos, saltandose
     * el ciclo de vida de las entidades. No dispara @PreUpdate ni las
     * validaciones. Es rapido, y por eso mismo hay que usarlo con cabeza.
     * El metodo que lo llame debe estar anotado con @Transactional.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Producto p SET p.activo = false WHERE p.stock = 0 AND p.activo = true")
    int desactivarSinStock();


    // =================================================================
    //  MODULO 5 — PAGINACION
    // =================================================================
    //  Basta con añadir un parametro Pageable al final y devolver Page<T>.
    //  Spring genera el LIMIT / OFFSET y, ademas, una segunda consulta
    //  COUNT(*) para saber cuantos hay en total.
    //
    //  Page<T>  -> trae los datos Y el total (2 consultas)
    //  Slice<T> -> trae los datos y solo sabe si hay siguiente (1 consulta)
    //  List<T>  -> trae la pagina sin ninguna informacion de total
    //
    //  Si tu interfaz no necesita mostrar "pagina 3 de 47", usa Slice:
    //  te ahorras el COUNT, que en tablas grandes es caro.

    /** Paginado sobre un query method normal. */
    Page<Producto> findByActivoTrue(Pageable pageable);

    /** Paginado sobre un query method con filtro. */
    Page<Producto> findByCategoriaId(Long categoriaId, Pageable pageable);

    /**
     * Paginado con @Query. Cuando la consulta lleva JOIN, conviene dar el
     * countQuery a mano: el que Spring deduce solo puede ser mucho mas
     * lento de lo necesario porque arrastra los JOIN sin usarlos.
     */
    @Query(value = """
           SELECT p FROM Producto p
           WHERE p.activo = true
             AND (:texto IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))
             AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
           """,
           countQuery = """
           SELECT COUNT(p) FROM Producto p
           WHERE p.activo = true
             AND (:texto IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))
             AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
           """)
    Page<Producto> buscar(@Param("texto") String texto,
                          @Param("categoriaId") Long categoriaId,
                          Pageable pageable);
    //  El patron  (:param IS NULL OR condicion)  permite que un mismo
    //  metodo sirva para filtro presente y filtro ausente, sin construir
    //  la consulta a mano.


    // =================================================================
    //  MODULO 3 — EJERCICIO 2:  escribe tu los metodos de abajo
    // =================================================================
    //  Documento: Modulo03_Repositorios_Spring_Data_JPA.docx, seccion 4.2
    //


    //  Estan comentados a proposito: un query method con un nombre
    //  invalido impide que la aplicacion arranque. Descomenta cada uno
    //  segun lo vayas resolviendo, y arranca para comprobarlo.
    //
    //  E1. Productos inactivos que todavia tienen stock.
    List<Producto> findByActivoFalseAndStockGreaterThan(int stock);
    //SQLHibernate:
    //    select
    //        p1_0.id,
    //        p1_0.activo,
    //        p1_0.categoria_id,
    //        p1_0.descripcion,
    //        p1_0.fecha_alta,
    //        p1_0.nombre,
    //        p1_0.precio,
    //        p1_0.proveedor_id,
    //        p1_0.stock
    //    from
    //        productos p1_0
    //    where
    //        not(p1_0.activo)
    //        and p1_0.stock>?

    List<Producto> findByProveedorNombre(String nombreProveedor);
    // SQL: Hibernate:
    //    select
    //        p1_0.id,
    //        p1_0.activo,
    //        p1_0.categoria_id,
    //        p1_0.descripcion,
    //        p1_0.fecha_alta,
    //        p1_0.nombre,
    //        p1_0.precio,
    //        p1_0.proveedor_id,
    //        p1_0.stock
    //    from
    //        productos p1_0
    //    left join
    //        proveedores p2_0
    //            on p2_0.id=p1_0.proveedor_id
    //    where
    //        p2_0.nombre=?
    //
    //  E3. Cuantos productos activos hay en una categoria.
    long countByCategoriaIdAndActivoTrue(Long categoriaId);
    //SQL:
    //=== E3: countByCategoriaIdAndActivoTrue ===
    //Hibernate:
    //    select
    //        count(p1_0.id)
    //    from
    //        productos p1_0
    //    where
    //        p1_0.categoria_id=?
    //        and p1_0.activo
    //  E4. Los tres productos mas baratos de una categoria.
    List<Producto> findTop3ByCategoriaIdOrderByPrecioAsc(Long categoriaId);
    //
    // SQL: Hibernate:
    //    select
    //        p1_0.id,
    //        p1_0.activo,
    //        p1_0.categoria_id,
    //        p1_0.descripcion,
    //        p1_0.fecha_alta,
    //        p1_0.nombre,
    //        p1_0.precio,
    //        p1_0.proveedor_id,
    //        p1_0.stock
    //    from
    //        productos p1_0
    //    where
    //        p1_0.categoria_id=?
    //    order by
    //        p1_0.precio
    //    fetch
    //        first ? rows only
    //  E5. Productos dados de alta despues de una fecha, ordenados por
    //      fecha descendente.
    List<Producto> findByFechaAltaAfterOrderByFechaAltaDesc(LocalDate fecha);
    //SQL: Hibernate:
    //    select
    //        p1_0.id,
    //        p1_0.activo,
    //        p1_0.categoria_id,
    //        p1_0.descripcion,
    //        p1_0.fecha_alta,
    //        p1_0.nombre,
    //        p1_0.precio,
    //        p1_0.proveedor_id,
    //        p1_0.stock
    //    from
    //        productos p1_0
    //    where
    //        p1_0.fecha_alta>?
    //    order by
    //        p1_0.fecha_alta desc
    //  E6. Productos cuyo nombre empieza por un texto Y estan activos.
    List<Producto> findByNombreStartingWithAndActivoTrue(String prefijo);

    //SQL: Hibernate:
    //    select
    //        p1_0.id,
    //        p1_0.activo,
    //        p1_0.categoria_id,
    //        p1_0.descripcion,
    //        p1_0.fecha_alta,
    //        p1_0.nombre,
    //        p1_0.precio,
    //        p1_0.proveedor_id,
    //        p1_0.stock
    //    from
    //        productos p1_0
    //    where
    //        p1_0.nombre like ? escape '\'
    //        and p1_0.activo

    //====metodo con error====

    //public Producto findByPrecioMaximo();
    //org.springframework.beans.factory.UnsatisfiedDependencyException:
    // Error creating bean with name 'pedidoController' defined in file
    // [/Users/migueljorami/Documents/bootcamp/CursoJPA/target/classes/com/curso/
    // tienda/controller/PedidoController.class]: Unsatisfied dependency expressed
    // through constructor parameter 0: Error creating bean with name 'pedidoService'
    // defined in file [/Users/migueljorami/Documents/bootcamp/CursoJPA/target/classes
    // /com/curso/tienda/service/PedidoService.class]: Unsatisfied dependency expressed
    // through constructor parameter 2: Error creating bean with name 'productoRepository'
    // defined in com.curso.tienda.repository.ProductoRepository defined in
    // @EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration:
    // Could not create query for public abstract com.curso.tienda.model.Producto
    // com.curso.tienda.repository.ProductoRepository.findByPrecioMaximo(); Reason:
    // Failed to create query for method public abstract com.curso.tienda.model.Producto
    // com.curso.tienda.repository.ProductoRepository.findByPrecioMaximo(); No property
    // 'maximo' found for type 'BigDecimal'; Traversed path: Producto.precio

    // Ejercicios modulo 4
    //Q1
    @Query(value = """
        SELECT p FROM Producto p
        WHERE p.precio > (SELECT AVG(p2.precio) FROM Producto p2)
                """)
    List<Producto> findPorEncimaDelPrecioDelCatalogo();

    //Q2
    @Query(value = """
            SELECT p FROM Producto p WHERE NOT EXISTS
            (SELECT 1 FROM DetallePedido d WHERE d.producto = p)
                """)
    List<Producto> findPorNuncaVendido();
}
