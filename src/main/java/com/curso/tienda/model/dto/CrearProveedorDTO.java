package com.curso.tienda.model.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record CrearProveedorDTO (
        @NotBlank(message ="Debe haber un nombre")
        @Size(max = 80)
        String nombre,
        @Size(max = 40)
        String pais,
        @NotBlank(message = "Debe tener un email")
        @Email(message = "Debe ser un email valido")
        String email,
        @Positive(message = "Los días deben ser positivos")
        int diasEntrega
    ) {
}
