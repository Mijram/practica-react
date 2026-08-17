package com.curso.tienda.controller;

import com.curso.tienda.model.dto.CrearProveedorDTO;
import com.curso.tienda.model.dto.ProveedorDTO;
import com.curso.tienda.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    //metodos GET

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public List<ProveedorDTO> obtenerTodos(){
        return proveedorService.listarActivos();
    }

    @GetMapping("/obtener-pais")
    public List<ProveedorDTO> obtenerPorPais(@RequestParam String pais){
        return proveedorService.listarPorPais(pais);
    }

    @GetMapping("/orden-entrega")
    public List<ProveedorDTO> obtenerPorOrdenEntrega(){
        return proveedorService.listarPorOrdenDeEntrega();
    }

    @GetMapping("/numero-pais")
    public ResponseEntity<Integer> obtenerNumeroPorPais(@RequestParam String pais){
        return ResponseEntity.ok(proveedorService.numeroProveedorPorPais(pais));
    }

    @GetMapping("/sin-productos")
    public List<ProveedorDTO> obtenerSinProductos(){
        return proveedorService.proveedoresSinProductos();
    }

    //metodos POST

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProveedorDTO crearProveedor(@Valid @RequestBody CrearProveedorDTO datos){
        return proveedorService.crearProveedor(datos);
    }

    //metodos PUT

    @PutMapping("/{id}/nombre")
    public ProveedorDTO actualizarNombre(@PathVariable Long id, @RequestParam String nombreNuevo){
        return proveedorService.actualizarNombre(id, nombreNuevo);
    }

    //metodos DELETE

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id){
        proveedorService.desactivar(id);

        return ResponseEntity.noContent().build();
    }


}
