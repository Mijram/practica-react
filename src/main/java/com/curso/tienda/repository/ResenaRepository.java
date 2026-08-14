package com.curso.tienda.repository;

import com.curso.tienda.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    public List<Resena> findByProductoId(Long id);

    public Long countByProductoId(Long id);

    public List<Resena> findByCalificacionGreaterThanEqual(int calificacion);

}
