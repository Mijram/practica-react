package com.curso.tienda.model.dto;

import com.curso.tienda.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MODULO 4 — EJEMPLO: DTO de salida para pedidos.
 * Documento: docs/Modulo04_Consultas_Query_y_JPQL.docx
 */
public record PedidoResumenDTO(
        Long id,
        LocalDate fecha,
        String estado,
        String cliente,
        String email,
        int articulos,
        BigDecimal total
) {

    /** Debe llamarse dentro de una transaccion: recorre detalles (LAZY). */
    public static PedidoResumenDTO desde(Pedido p) {
        return new PedidoResumenDTO(
                p.getId(),
                p.getFecha(),
                p.getEstado().name(),
                p.getUsuario().getNombreCompleto(),
                p.getUsuario().getEmail(),
                p.cantidadTotalArticulos(),
                p.calcularTotal()
        );
    }
}
