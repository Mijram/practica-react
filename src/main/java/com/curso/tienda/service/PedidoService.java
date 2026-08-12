package com.curso.tienda.service;

import com.curso.tienda.model.DetallePedido;
import com.curso.tienda.model.EstadoPedido;
import com.curso.tienda.model.Pedido;
import com.curso.tienda.model.Producto;
import com.curso.tienda.model.Usuario;
import com.curso.tienda.model.dto.PedidoResumenDTO;
import com.curso.tienda.repository.PedidoRepository;
import com.curso.tienda.repository.ProductoRepository;
import com.curso.tienda.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * MODULO 4 y 6 — EJEMPLO: el servicio donde la transaccion importa de verdad.
 * Documentos: Modulo04_Consultas_Query_y_JPQL.docx
 *             Modulo06_Checklist_y_proyecto_integrador.docx
 *
 * crearPedido() es el metodo mas instructivo del curso: toca tres
 * entidades, descuenta stock y crea lineas de detalle. Si cualquiera de
 * esos pasos falla, TODO debe deshacerse. Sin @Transactional, un fallo a
 * mitad dejaria el stock descontado y el pedido sin guardar.
 */
@Service
@Transactional(readOnly = true)
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    // =================================================================
    //  LECTURA
    // =================================================================

    /** MODULO 5: listado paginado con filtros opcionales. */
    public Page<PedidoResumenDTO> buscar(EstadoPedido estado,
                                         LocalDate desde,
                                         LocalDate hasta,
                                         Pageable pageable) {
        return pedidoRepository.buscar(estado, desde, hasta, pageable)
                .map(PedidoResumenDTO::desde);
    }

    /**
     * Usa findCompletoById, que trae usuario, detalles y productos en UNA
     * consulta. Con findById normal harian falta ocho.
     */
    public PedidoResumenDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findCompletoById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", id));
        return PedidoResumenDTO.desde(pedido);
    }

    public List<PedidoResumenDTO> listarDeUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException("Usuario", usuarioId);
        }
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(PedidoResumenDTO::desde)
                .toList();
    }

    // =================================================================
    //  ESCRITURA — el caso que justifica las transacciones
    // =================================================================

    /**
     * Crea un pedido completo a partir de un mapa productoId -> cantidad.
     *
     * SECUENCIA:
     *   1. validar que el usuario existe y esta activo
     *   2. crear el pedido en memoria
     *   3. por cada linea: buscar el producto, comprobar stock,
     *      descontarlo y añadir el detalle
     *   4. guardar
     *
     * TODO ESTO ES UNA SOLA TRANSACCION. Si el tercer producto no tiene
     * stock, descontarStock lanza IllegalStateException, Spring hace
     * rollback, y el stock de los dos primeros vuelve a su valor original.
     * Sin @Transactional habrias vendido stock que no existe.
     *
     * Fijate tambien en que solo se llama a save() UNA vez, sobre el
     * pedido. Los detalles se guardan solos gracias a
     * cascade = CascadeType.ALL en la relacion.
     */
    @Transactional
    public PedidoResumenDTO crearPedido(Long usuarioId, String direccion, Map<Long, Integer> lineas) {

        if (lineas == null || lineas.isEmpty()) {
            throw new ReglaDeNegocioException("El pedido debe tener al menos una linea");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));

        if (!usuario.isActivo()) {
            throw new ReglaDeNegocioException("El usuario " + usuario.getEmail() + " esta inactivo");
        }

        Pedido pedido = new Pedido(usuario, direccion);

        for (Map.Entry<Long, Integer> linea : lineas.entrySet()) {
            Long productoId = linea.getKey();
            int cantidad = linea.getValue();

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", productoId));

            if (!producto.isActivo()) {
                throw new ReglaDeNegocioException(
                        "El producto '" + producto.getNombre() + "' no esta disponible");
            }
            if (!producto.hayStock(cantidad)) {
                throw new ReglaDeNegocioException(
                        "Stock insuficiente de '" + producto.getNombre()
                        + "': hay " + producto.getStock() + ", se piden " + cantidad);
            }

            producto.descontarStock(cantidad);            // dirty checking
            pedido.agregarDetalle(new DetallePedido(producto, cantidad));
        }

        Pedido guardado = pedidoRepository.save(pedido);  // cascada a los detalles
        return PedidoResumenDTO.desde(guardado);
    }

    /**
     * Cambia el estado respetando las transiciones validas definidas en el
     * enum. Al cancelar, devuelve el stock.
     */
    @Transactional
    public PedidoResumenDTO cambiarEstado(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findCompletoById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));

        if (!pedido.getEstado().puedePasarA(nuevoEstado)) {
            throw new ReglaDeNegocioException(
                    "Un pedido en estado " + pedido.getEstado()
                    + " no puede pasar a " + nuevoEstado);
        }

        if (nuevoEstado == EstadoPedido.CANCELADO) {
            for (DetallePedido d : pedido.getDetalles()) {
                Producto p = d.getProducto();
                p.setStock(p.getStock() + d.getCantidad());
            }
        }

        pedido.setEstado(nuevoEstado);
        return PedidoResumenDTO.desde(pedido);
    }

    // =================================================================
    //  MODULO 4 — EJERCICIO 2: completa estos metodos
    // =================================================================
    //  Documento: Modulo04_Consultas_Query_y_JPQL.docx, seccion 4.2

    /**
     * TODO E1: devuelve el importe total facturado entre dos fechas,
     * excluyendo los pedidos cancelados.
     * PISTA: escribe una @Query en PedidoRepository que devuelva
     * directamente el SUM, en vez de traer los pedidos y sumarlos en Java.
     * Traer mil pedidos para sumar un numero es justo lo que no hay que
     * hacer.
     */
    public java.math.BigDecimal facturacionEntre(LocalDate desde, LocalDate hasta) {
        return java.math.BigDecimal.ZERO;
    }

    /**
     * TODO E2: devuelve los pedidos que llevan mas de N dias en estado
     * PENDIENTE. Añade el query method o la @Query que necesites.
     */
    public List<PedidoResumenDTO> pendientesAntiguos(int dias) {
        return List.of();
    }

    /**
     * TODO E3: añade una linea a un pedido que todavia este PENDIENTE.
     * Debe validar stock, descontarlo y rechazar el cambio si el pedido ya
     * salio de PENDIENTE. Si el producto ya esta en el pedido, suma la
     * cantidad a la linea existente en lugar de crear otra
     * (recuerda la restriccion UNIQUE (pedido_id, producto_id)).
     */
    public PedidoResumenDTO agregarLinea(Long pedidoId, Long productoId, int cantidad) {
        return null;
    }
}
