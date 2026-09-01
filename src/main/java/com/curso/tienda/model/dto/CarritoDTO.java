package com.curso.tienda.model.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record CarritoDTO(
        Long    usuarioId,
        String usuarioNombre,
        String usuarioApellido,
        String  productoNombre,
        int productoStock,
        String proveedorNombre,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal costoEnvio,
        BigDecimal precioFinal
) {
}
