package com.curso.tienda.controller;

import com.curso.tienda.model.dto.CrearProductoDTO;
import com.curso.tienda.model.dto.PaginaDTO;
import com.curso.tienda.model.dto.ProductoDTO;
import com.curso.tienda.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * MODULO 3 y 5 — EJEMPLO: la capa web.
 * Documentos: Modulo03_Repositorios_Spring_Data_JPA.docx
 *             Modulo05_Paginacion.docx
 *
 * QUE HACE UN CONTROLADOR Y QUE NO
 * --------------------------------
 * SI hace:  recibir la peticion HTTP, validar el formato de entrada,
 *           llamar a UN metodo del servicio y traducir el resultado a una
 *           respuesta HTTP con su codigo de estado.
 *
 * NO hace:  reglas de negocio, acceso a repositorios, transacciones ni
 *           conversion entidad -> DTO.
 *
 * Si un metodo de controlador tiene mas de cinco lineas, casi siempre
 * significa que hay logica que deberia estar en el servicio.
 *
 * @RestController = @Controller + @ResponseBody: todo lo que devuelvan
 * los metodos se convierte a JSON automaticamente.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * GET /api/productos
     * Listado completo de productos activos.
     */
    @GetMapping
    public List<ProductoDTO> listar() {
        return productoService.listarActivos();
    }

    /**
     * MODULO 5 — GET /api/productos/pagina?page=0&size=10&sort=precio&dir=desc
     *
     * Construimos el Pageable a mano para que se vea de que esta hecho.
     * En un proyecto real basta con poner Pageable como parametro del
     * metodo y Spring lo arma solo leyendo page, size y sort de la query
     * string; incluso puedes fijar valores por defecto con
     * @PageableDefault(size = 20, sort = "nombre").
     *
     * OJO: la primera pagina es la 0, no la 1.
     */
    @GetMapping("/pagina")
    public PaginaDTO<ProductoDTO> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(defaultValue = "asc") String dir) {

        //  Limitar el tamano de pagina no es opcional: sin este tope,
        //  cualquiera puede pedir ?size=1000000 y tumbar el servidor.
        int tamano = Math.min(Math.max(size, 1), 100);

        Sort orden = dir.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(Math.max(page, 0), tamano, orden);
        Page<ProductoDTO> pagina = productoService.listarPaginado(pageable);

        return PaginaDTO.desde(pagina);
    }

    /**
     * MODULO 5 — GET /api/productos/buscar?texto=monitor&categoriaId=3&page=0
     * Buscador con filtros opcionales.
     */
    @GetMapping("/buscar")
    public PaginaDTO<ProductoDTO> buscar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        return PaginaDTO.desde(productoService.buscar(texto, categoriaId, pageable));
    }

    /** GET /api/productos/5 */
    @GetMapping("/{id}")
    public ProductoDTO buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id);
        //  Si no existe, el servicio lanza RecursoNoEncontradoException y
        //  ManejadorDeErrores la convierte en un 404. El controlador no
        //  necesita ningun if.
    }

    /** GET /api/productos/bajo-stock?limite=10 */
    @GetMapping("/bajo-stock")
    public List<ProductoDTO> bajoStock(@RequestParam(defaultValue = "10") int limite) {
        return productoService.listarBajoStock(limite);
    }

    /**
     * POST /api/productos
     *
     * @Valid dispara las validaciones declaradas en CrearProductoDTO.
     * Si alguna falla, Spring devuelve 400 y este metodo ni se ejecuta.
     * SIN @Valid las anotaciones se ignoran por completo: es el olvido
     * mas frecuente al montar una API.
     *
     * @ResponseStatus(CREATED) devuelve 201 en lugar de 200, que es lo
     * correcto al crear un recurso.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO crear(@Valid @RequestBody CrearProductoDTO datos) {
        return productoService.crear(datos);
    }

    /** PUT /api/productos/5/precio */
    @PutMapping("/{id}/precio")
    public ProductoDTO actualizarPrecio(@PathVariable Long id,
                                        @RequestParam BigDecimal precio) {
        return productoService.actualizarPrecio(id, precio);
    }

    /** PUT /api/productos/5/stock?unidades=20 */
    @PutMapping("/{id}/stock")
    public ProductoDTO reponerStock(@PathVariable Long id,
                                    @RequestParam int unidades) {
        return productoService.reponerStock(id, unidades);
    }

    /**
     * DELETE /api/productos/5
     * Borrado logico: marca el producto como inactivo.
     * 204 No Content es el codigo correcto cuando no se devuelve cuerpo.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
