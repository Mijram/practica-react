package com.curso.tienda.controller;

import com.curso.tienda.model.Categoria;
import com.curso.tienda.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * MODULO 3 — EJEMPLO: un controlador minimo, y una advertencia.
 * Documento: docs/Modulo03_Repositorios_Spring_Data_JPA.docx
 *
 * ESTE CONTROLADOR ROMPE DOS REGLAS A PROPOSITO, para que veas el efecto:
 *
 *   1. Llama al repositorio DIRECTAMENTE, saltandose el servicio.
 *   2. Devuelve la ENTIDAD en lugar de un DTO.
 *
 * Funciona para categorias porque la lista de productos es LAZY y
 * open-in-view esta desactivado, asi que Jackson no la serializa... pero
 * es fragil: el dia que alguien ponga EAGER o active open-in-view,
 * empezara a devolver todos los productos de cada categoria, o entrara en
 * recursion infinita (Categoria -> productos -> categoria -> ...).
 *
 * EJERCICIO DEL MODULO 6: reescribe esta clase como debe ser. Crea
 * CategoriaService y CategoriaDTO y haz que el controlador solo hable con
 * el servicio. Compara el JSON antes y despues.
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepository.findByActivaTrueOrderByNombreAsc();
    }

    @GetMapping("/sin-productos")
    public List<Categoria> sinProductos() {
        return categoriaRepository.findSinProductos();
    }
}
