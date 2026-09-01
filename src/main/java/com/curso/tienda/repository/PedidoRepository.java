package com.curso.tienda.repository;

import com.curso.tienda.model.EstadoPedido;
import com.curso.tienda.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * MODULO 4 y 5 — EJEMPLO: consultas complejas y paginacion.
 * Documentos: Modulo04_Consultas_Query_y_JPQL.docx
 *             Modulo05_Paginacion.docx
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioId(Long usuarioId);

    List<Pedido> findByEstado(EstadoPedido estado);

    Page<Pedido> findByEstadoOrderByFechaDesc(EstadoPedido estado, Pageable pageable);

    long countByEstado(EstadoPedido estado);

    List<Pedido> findByFechaBetween(LocalDate desde, LocalDate hasta);

    /**
     * Un pedido con TODO lo que necesita una factura, en una consulta.
     *
     * Sin este metodo, mostrar un pedido con cinco lineas costaria:
     *   1 consulta del pedido
     * + 1 del usuario
     * + 1 de la lista de detalles
     * + 5 de los productos de cada detalle
     * = 8 viajes. Con el JOIN FETCH encadenado, uno.
     */
    @Query("""
           SELECT DISTINCT p FROM Pedido p
           JOIN FETCH p.usuario
           LEFT JOIN FETCH p.detalles d
           LEFT JOIN FETCH d.producto
           WHERE p.id = :id
           """)
    Optional<Pedido> findCompletoById(@Param("id") Long id);

    /**
     * LIMITACION IMPORTANTE DE JPA:
     * no se puede usar JOIN FETCH sobre una coleccion Y paginar a la vez.
     * Si lo intentas, Hibernate avisa con
     *     "firstResult/maxResults specified with collection fetch;
     *      applying in memory"
     * y trae TODAS las filas para paginarlas en memoria. Es exactamente
     * lo que la paginacion pretendia evitar.
     *
     * Por eso este metodo solo hace FETCH del usuario, que es un
     * @ManyToOne (una sola fila), y no de los detalles.
     * Para el caso con coleccion, el patron correcto es en dos pasos:
     * primero paginar los ids, despues traer los datos de esos ids.
     */
    @Query(value = """
           SELECT p FROM Pedido p
           JOIN FETCH p.usuario
           WHERE (:estado IS NULL OR p.estado = :estado)
             AND (:desde  IS NULL OR p.fecha >= :desde)
             AND (:hasta  IS NULL OR p.fecha <= :hasta)
           """,
           countQuery = """
           SELECT COUNT(p) FROM Pedido p
           WHERE (:estado IS NULL OR p.estado = :estado)
             AND (:desde  IS NULL OR p.fecha >= :desde)
             AND (:hasta  IS NULL OR p.fecha <= :hasta)
           """)
    Page<Pedido> buscar(@Param("estado") EstadoPedido estado,
                        @Param("desde") LocalDate desde,
                        @Param("hasta") LocalDate hasta,
                        Pageable pageable);

    /** Ventas agrupadas por estado: tuplas (estado, cantidad, importe). */
    @Query("""
           SELECT p.estado, COUNT(DISTINCT p.id), SUM(d.cantidad * d.precioUnitario)
           FROM Pedido p
           JOIN p.detalles d
           GROUP BY p.estado
           ORDER BY p.estado
           """)
    List<Object[]> resumenPorEstado();

    //Q3

    @Query(value = """
           SELECT p.nombre, c.nombre, SUM(d.cantidad)
           FROM DetallePedido d
           JOIN d.producto p
           LEFT JOIN p.categoria c
           GROUP BY p.nombre, c.nombre
           ORDER BY SUM(d.cantidad) DESC
                """)
    List<Object[]> productosMasVendidos(Pageable pageable);

    //Q5
    @Query(value = """
        SELECT FUNCTION('date_trunc', 'month', p.fecha), SUM(d.cantidad * d.precioUnitario), SUM(p.costoEnvio)
        FROM Pedido p
        JOIN p.detalles d
        WHERE YEAR(p.fecha) = 2025
        AND p.estado <> com.curso.tienda.model.EstadoPedido.CANCELADO
        GROUP BY FUNCTION('date_trunc', 'month', p.fecha)
        ORDER BY FUNCTION('date_trunc', 'month', p.fecha) ASC
                """)
    List<Object[]> facturacionPorMes2025();

    //Q6
    //El uso del native query fue necesario, ya que su uso normal en JPQL
    //no existe una palabra clave llamada interval. Por lo que usar nativeQuery
    //que en este caso es psql
    @Query(value = """
        SELECT * FROM pedidos p
        WHERE p.estado = 'PENDIENTE'
        AND p.fecha < CURRENT_DATE - INTERVAL '30 days'
        """, nativeQuery = true)
    List<Pedido> pedidosMas30DiasPendientes();
}
