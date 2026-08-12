package com.curso.tienda.repository;

import com.curso.tienda.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MODULO 3 — EJEMPLO: el repositorio mas pequeno posible.
 * Documento: docs/Modulo03_Repositorios_Spring_Data_JPA.docx
 *
 * Con solo extender JpaRepository ya tienes el CRUD completo.
 * Todo lo demas es opcional.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNombre(String nombre);

    List<Categoria> findByActivaTrueOrderByNombreAsc();

    boolean existsByNombre(String nombre);

    /**
     * Categorias que no tienen ni un solo producto.
     * Con LEFT JOIN + IS NULL, igual que en SQL: las categorias sin
     * pareja quedan con la parte derecha en null.
     */
    @Query("""
           SELECT c FROM Categoria c
           LEFT JOIN c.productos p
           WHERE p.id IS NULL
           """)
    List<Categoria> findSinProductos();

    /**
     * Categorias con al menos N productos activos.
     * HAVING filtra grupos, igual que en SQL.
     */
    @Query("""
           SELECT c FROM Categoria c
           JOIN c.productos p
           WHERE p.activo = true
           GROUP BY c
           HAVING COUNT(p) >= :minimo
           ORDER BY COUNT(p) DESC
           """)
    List<Categoria> findConAlMenos(@Param("minimo") long minimo);
}
