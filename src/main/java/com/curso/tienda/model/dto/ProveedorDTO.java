package com.curso.tienda.model.dto;

import com.curso.tienda.model.Producto;
import com.curso.tienda.model.Proveedor;

import java.util.List;

public record ProveedorDTO(
        Long id,
        String nombre,
        String pais,
        String email,
        int diasEntrega,
        boolean activo,
        List<String> productos
) {

    public static ProveedorDTO desde(Proveedor p){
        return new ProveedorDTO(
                p.getId(),
                p.getNombre(),
                p.getPais(),
                p.getEmail(),
                p.getDiasEntrega(),
                p.isActivo(),
                p.getProductos().stream().map(Producto::getNombre).toList()
        );
    }

}
