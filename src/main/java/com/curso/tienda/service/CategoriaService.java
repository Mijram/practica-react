package com.curso.tienda.service;

import com.curso.tienda.model.Categoria;
import com.curso.tienda.model.dto.CategoriaDTO;
import com.curso.tienda.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepo;

    public CategoriaService(CategoriaRepository categoriaRepo){
        this.categoriaRepo = categoriaRepo;
    }

    public List<CategoriaDTO> categorias(){
        List<Categoria> categorias = categoriaRepo.findByActivaTrueOrderByNombreAsc();

        if(categorias.isEmpty()){
            throw new RecursoNoEncontradoException("No se encontraron categorias");
        }
        return categorias.stream()
                .map(CategoriaDTO::desde)
                .toList();
    }
}
