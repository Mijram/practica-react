package com.curso.tienda.model.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * MODULO 5 — EJEMPLO: envoltorio de paginacion para la API.
 * Documento: docs/Modulo05_Paginacion.docx
 *
 * Page<T> de Spring trae mucha mas informacion de la que un cliente
 * necesita, y su formato JSON cambio entre versiones de Spring Boot.
 * Devolver un DTO propio te desacopla de eso y deja la respuesta limpia:
 *
 *   {
 *     "contenido":       [ ... ],
 *     "paginaActual":    0,
 *     "tamanoPagina":    10,
 *     "totalElementos":  33,
 *     "totalPaginas":    4,
 *     "esPrimera":       true,
 *     "esUltima":        false
 *   }
 *
 * Fijate en que paginaActual empieza en 0, no en 1. Es la convencion de
 * Spring Data y la fuente de mas de un error de un elemento al construir
 * el paginador en el frontend.
 */
public record PaginaDTO<T>(
        List<T> contenido,
        int paginaActual,
        int tamanoPagina,
        long totalElementos,
        int totalPaginas,
        boolean esPrimera,
        boolean esUltima
) {

    public static <T> PaginaDTO<T> desde(Page<T> pagina) {
        return new PaginaDTO<>(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast()
        );
    }
}
