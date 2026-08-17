package com.curso.tienda.repository;

import com.curso.tienda.model.Usuario;
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
 * MODULO 3 y 4 — EJEMPLO.
 * Documento: docs/Modulo03_Repositorios_Spring_Data_JPA.docx
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByCiudadIgnoreCase(String ciudad);

    Page<Usuario> findByActivoTrue(Pageable pageable);

    List<Usuario> findByFechaRegistroAfter(LocalDate fecha);

    /**
     * Usuarios que NUNCA han hecho un pedido.
     *
     * NOT EXISTS y no NOT IN: si la subconsulta pudiera devolver un NULL,
     * NOT IN no devolveria ninguna fila. NOT EXISTS es inmune a eso.
     */
    @Query("""
           SELECT u FROM Usuario u
           WHERE NOT EXISTS (SELECT 1 FROM Pedido p WHERE p.usuario = u)
           ORDER BY u.fechaRegistro DESC
           """)
    List<Usuario> findSinPedidos();

    /**
     * Trae el usuario CON sus pedidos en una sola consulta.
     * Sin el JOIN FETCH, acceder a usuario.getPedidos() fuera de la
     * transaccion lanza LazyInitializationException.
     */
    @Query("""
           SELECT DISTINCT u FROM Usuario u
           LEFT JOIN FETCH u.pedidos
           WHERE u.id = :id
           """)
    Optional<Usuario> findConPedidos(@Param("id") Long id);
    //  DISTINCT es necesario: el JOIN multiplica la fila del usuario por
    //  cada pedido, y sin el recibirias el mismo usuario repetido N veces.

    /** Los clientes que mas han gastado. Devuelve tuplas, no entidades. */
    @Query("""
           SELECT u.nombre, u.apellido, COUNT(DISTINCT p.id), SUM(d.cantidad * d.precioUnitario)
           FROM Usuario u
           JOIN Pedido p ON p.usuario = u
           JOIN DetallePedido d ON d.pedido = p
           WHERE p.estado <> com.curso.tienda.model.EstadoPedido.CANCELADO
           GROUP BY u.id, u.nombre, u.apellido
           ORDER BY SUM(d.cantidad * d.precioUnitario) DESC
           """)
    List<Object[]> findRankingDeGasto(Pageable pageable);
    //  COUNT(DISTINCT p.id) y no COUNT(p): al unir con DetallePedido, un
    //  pedido de cuatro lineas aparece cuatro veces. Sin DISTINCT
    //  contarias lineas, no pedidos.

    //Q4
    @Query(value = """
           SELECT u FROM Usuario u
           JOIN u.pedidos p
           LEFT JOIN p.detalles d
           LEFT JOIN d.producto pr
           WHERE (SELECT COUNT(u) WHERE pr.categoria = )
                """)
    List<Usuario> findMasTresCategoriasDistintas();
}
