package com.curso.tienda.repository;

import com.curso.tienda.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    //Encontrar proveedor por país, ignorando mayusculas
    List<Proveedor> findByPaisIgnoreCase(String pais);

    //listar proveedores activos en orden de días de entrega
    List<Proveedor> findByActivoTrueOrderByDiasEntregaDesc();

    //saber si existe un proveedor por el nombre
    boolean existsByNombre(String nombre);

    //numero de proveedores por país
    int countByPais(String pais);


    @Query("""
            SELECT p FROM Proveedor p
            LEFT JOIN p.productos producto
            WHERE producto.id IS NULL
                        """)
    List<Proveedor>  findProveedorSinProductos();

    @Query("""
            SELECT DISTINCT p FROM Proveedor p
            JOIN FETCH p.productos
            """)
    List<Proveedor> findProveedorConProductos();

}
