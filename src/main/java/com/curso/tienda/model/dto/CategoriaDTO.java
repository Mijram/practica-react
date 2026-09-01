package com.curso.tienda.model.dto;

import com.curso.tienda.model.Categoria;

public record CategoriaDTO(
        Long id,
        String nombre,
        String descripcion,
        boolean activo

) {

    public static CategoriaDTO desde(Categoria c){
        return new CategoriaDTO(
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.isActiva()
        );
    }
}
