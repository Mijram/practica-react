package com.curso.tienda.service;

import com.curso.tienda.repository.PedidoRepository;
import com.curso.tienda.repository.ProductoRepository;
import com.curso.tienda.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * MODULO 6 — PROYECTO INTEGRADOR: escribe este servicio completo.
 * Documento: docs/Modulo06_Checklist_y_proyecto_integrador.docx
 * Referencia: service/ProductoService.java y service/PedidoService.java
 *
 * Un panel de control para la gerencia. Cada metodo reune varias piezas
 * del curso: consultas con @Query, proyecciones, agregacion y paginacion.
 *
 * REGLA DEL EJERCICIO: ninguno de estos metodos puede traer entidades a
 * memoria para contarlas o sumarlas en Java. Todo lo que sea una cuenta
 * debe resolverse en la base de datos con una consulta. Si escribes
 * findAll().stream().filter(...).count(), el ejercicio esta mal aunque el
 * numero salga bien: con cien mil filas eso no funciona.
 *
 * Los metodos devuelven valores neutros para que el proyecto arranque
 * mientras los completas.
 */
@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteService(ProductoRepository productoRepository,
                          PedidoRepository pedidoRepository,
                          UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * TODO R1 — Resumen general del negocio.
     * Devuelve un mapa con: total de productos activos, total de usuarios
     * activos, total de pedidos, y facturacion acumulada sin cancelados.
     * PISTA: cuatro consultas de conteo, ninguna carga de entidades.
     */
    public Map<String, Object> resumenGeneral() {
        return Map.of();
    }

    /**
     * TODO R2 — Ventas por categoria.
     * Una fila por categoria con: nombre, unidades vendidas, importe
     * facturado y porcentaje sobre el total.
     * PISTA: parte de ProductoRepository.contarPorCategoria() y escribe
     * una @Query nueva que una con DetallePedido.
     */
    public List<Map<String, Object>> ventasPorCategoria() {
        return List.of();
    }

    /**
     * TODO R3 — Top N de clientes por gasto.
     * PISTA: UsuarioRepository.findRankingDeGasto(Pageable) ya existe.
     * Usalo con PageRequest.of(0, n) y convierte los Object[] a mapas.
     */
    public List<Map<String, Object>> topClientes(int n) {
        return List.of();
    }

    /**
     * TODO R4 — Productos que nunca se han vendido.
     * PISTA: NOT EXISTS contra DetallePedido, igual que
     * UsuarioRepository.findSinPedidos().
     */
    public List<String> productosNuncaVendidos() {
        return List.of();
    }

    /**
     * TODO R5 — Valor total del inventario activo.
     * PISTA: se puede hacer con una sola consulta:
     *     SELECT SUM(p.precio * p.stock) FROM Producto p WHERE p.activo = true
     * Compara el rendimiento con la version que carga todos los productos
     * y suma en Java, y anota la diferencia en un comentario.
     */
    public BigDecimal valorInventario() {
        return BigDecimal.ZERO;
    }

    /**
     * TODO R6 — Productos que hay que reponer.
     * Los que tienen stock por debajo de un umbral Y se han vendido al
     * menos una vez en los ultimos 90 dias. Ordenados por unidades
     * vendidas descendente.
     */
    public List<Map<String, Object>> alertaReposicion(int umbralStock) {
        return List.of();
    }
}
