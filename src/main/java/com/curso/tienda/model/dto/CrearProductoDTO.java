package com.curso.tienda.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * MODULO 3 — EJEMPLO: DTO de ENTRADA.
 * Documento: docs/Modulo03_Repositorios_Spring_Data_JPA.docx
 *
 * Los DTO de entrada y de salida son distintos a proposito.
 * Al CREAR un producto, el cliente no puede enviar el id (lo asigna la
 * base de datos) ni decidir si esta activo. Solo manda lo que le
 * corresponde. Un DTO de entrada bien acotado es la primera linea de
 * defensa contra el "mass assignment": que alguien mande un campo que no
 * deberia poder tocar y acabe modificandolo.
 *
 * Las anotaciones de validacion se activan cuando el controlador marca el
 * parametro con @Valid. Si algo falla, Spring devuelve 400 sin llegar al
 * service.
 */
public record CrearProductoDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede pasar de 120 caracteres")
        String nombre,

        @Size(max = 500)
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor que cero")
        BigDecimal precio,

        @PositiveOrZero(message = "El stock no puede ser negativo")
        int stock,

        @NotNull(message = "Debes indicar la categoria")
        Long categoriaId,

        Long proveedorId
) {
}
