package com.curso.tienda.repository;

import com.curso.tienda.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
class ProductoRepositoryTest {

    @Autowired
    ProductoRepository repo;

    @Test
    void e1_inactivos_con_stock() {
        System.out.println("\n=== E1: findByActivoFalseAndStockGreaterThan ===");
        List<Producto> resultado = repo.findByActivoFalseAndStockGreaterThan(0);
        System.out.println("Resultados: " + resultado.size());
    }

    @Test
    void e2_por_nombre_proveedor() {
        System.out.println("\n=== E2: findByProveedorNombre ===");
        List<Producto> resultado = repo.findByProveedorNombre("TechSupplies");
        System.out.println("Resultados: " + resultado.size());
    }

    @Test
    void e3_contar_activos_por_categoria() {
        System.out.println("\n=== E3: countByCategoriaIdAndActivoTrue ===");
        long cantidad = repo.countByCategoriaIdAndActivoTrue(1L);
        System.out.println("Cantidad: " + cantidad);
    }

    @Test
    void e4_top3_mas_baratos() {
        System.out.println("\n=== E4: findTop3ByCategoriaIdOrderByPrecioAsc ===");
        List<Producto> resultado = repo.findTop3ByCategoriaIdOrderByPrecioAsc(1L);
        System.out.println("Resultados: " + resultado.size());
        resultado.forEach(p -> System.out.println("  " + p));
    }

    @Test
    void e5_por_fecha_alta() {
        System.out.println("\n=== E5: findByFechaAltaAfterOrderByFechaAltaDesc ===");
        List<Producto> resultado = repo.findByFechaAltaAfterOrderByFechaAltaDesc(LocalDate.of(2023, 1, 1));
        System.out.println("Resultados: " + resultado.size());
    }

    @Test
    void e6_nombre_empieza_por_y_activo() {
        System.out.println("\n=== E6: findByNombreStartingWithAndActivoTrue ===");
        List<Producto> resultado = repo.findByNombreStartingWithAndActivoTrue("A");
        System.out.println("Resultados: " + resultado.size());
        resultado.forEach(p -> System.out.println("  " + p));
    }
}
