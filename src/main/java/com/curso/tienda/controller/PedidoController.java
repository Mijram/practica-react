package com.curso.tienda.controller;

import com.curso.tienda.model.EstadoPedido;
import com.curso.tienda.model.dto.PaginaDTO;
import com.curso.tienda.model.dto.PedidoResumenDTO;
import com.curso.tienda.service.PedidoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * MODULO 4 y 5 — EJEMPLO.
 * Documento: docs/Modulo04_Consultas_Query_y_JPQL.docx
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * GET /api/pedidos?estado=ENTREGADO&desde=2025-01-01&page=0&size=10
     *
     * Spring convierte solo el texto "ENTREGADO" al enum EstadoPedido y
     * "2025-01-01" a LocalDate. Si el valor no encaja, devuelve 400 sin
     * llegar al servicio.
     */
    @GetMapping
    public PaginaDTO<PedidoResumenDTO> buscar(
            @RequestParam(required = false) EstadoPedido estado,
            //  @DateTimeFormat es necesario para que Spring convierta el
            //  texto "2025-01-01" de la query string a LocalDate. Sin ella,
            //  la peticion falla con 400 y un mensaje poco claro.
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by("fecha").descending());

        return PaginaDTO.desde(pedidoService.buscar(estado, desde, hasta, pageable));
    }

    /** GET /api/pedidos/12 */
    @GetMapping("/{id}")
    public PedidoResumenDTO buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    /** GET /api/pedidos/usuario/3 */
    @GetMapping("/usuario/{usuarioId}")
    public List<PedidoResumenDTO> deUsuario(@PathVariable Long usuarioId) {
        return pedidoService.listarDeUsuario(usuarioId);
    }

    /**
     * POST /api/pedidos
     * Cuerpo esperado:
     * {
     *   "usuarioId": 3,
     *   "direccion": "Calle 45 # 12-30, Bogota",
     *   "lineas": { "1": 2, "7": 1 }
     * }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResumenDTO crear(@RequestBody CrearPedidoRequest peticion) {
        return pedidoService.crearPedido(
                peticion.usuarioId(),
                peticion.direccion(),
                peticion.lineas());
    }

    /** PUT /api/pedidos/12/estado?nuevo=ENVIADO */
    @PutMapping("/{id}/estado")
    public PedidoResumenDTO cambiarEstado(@PathVariable Long id,
                                          @RequestParam("nuevo") EstadoPedido nuevo) {
        return pedidoService.cambiarEstado(id, nuevo);
    }

    /**
     * Record anidado para el cuerpo del POST.
     * Se declara aqui porque solo lo usa este controlador; si lo
     * necesitaras en mas sitios, iria en model/dto.
     */
    public record CrearPedidoRequest(Long usuarioId,
                                     String direccion,
                                     Map<Long, Integer> lineas) {
    }
}
