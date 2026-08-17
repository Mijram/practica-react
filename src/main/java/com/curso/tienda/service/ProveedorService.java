package com.curso.tienda.service;

import com.curso.tienda.model.Producto;
import com.curso.tienda.model.Proveedor;
import com.curso.tienda.model.dto.CrearProveedorDTO;
import com.curso.tienda.model.dto.ProductoDTO;
import com.curso.tienda.model.dto.ProveedorDTO;
import com.curso.tienda.repository.ProductoRepository;
import com.curso.tienda.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class ProveedorService {

    private final ProductoRepository productoRepository; //
    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProductoRepository productoRepo, ProveedorRepository proveedorRepo) {
        this.productoRepository = productoRepo;
        this.proveedorRepository = proveedorRepo;
    }

    //metodos relacionados a GET

    public List<ProveedorDTO> listarActivos() {
        return proveedorRepository.findProveedorConProductos()
                .stream()
                .map(ProveedorDTO::desde)
                .toList();
    }

    public List<ProveedorDTO> listarPorPais(String pais){
        return proveedorRepository.findByPaisIgnoreCase(pais)
                .stream()
                .map(ProveedorDTO::desde)
                .toList();
    }

    public List<ProveedorDTO> listarPorOrdenDeEntrega(){
        return proveedorRepository.findByActivoTrueOrderByDiasEntregaDesc()
                .stream()
                .map(ProveedorDTO::desde)
                .toList();
    }

    public int numeroProveedorPorPais(String pais){
        return proveedorRepository.countByPais(pais);
    }

    public List<ProveedorDTO> proveedoresSinProductos(){
        return proveedorRepository.findProveedorSinProductos().stream()
                .map(ProveedorDTO::desde)
                .toList();
    }

    //metodo POST

    @Transactional
    public ProveedorDTO crearProveedor(CrearProveedorDTO datos){
        if(proveedorRepository.existsByNombre(datos.nombre())){
            throw new ReglaDeNegocioException("Ya hay un proveedor con el nombre "+ datos.nombre() + "...");
        }

        Proveedor proveedor = new Proveedor(datos.nombre(), datos.pais(), datos.email(), datos.diasEntrega());

        Proveedor guardado = proveedorRepository.save(proveedor);

        return ProveedorDTO.desde(guardado);
    }

    //metodos relacionados a PUT

    @Transactional
    public ProveedorDTO actualizarNombre(Long id, String nuevoNombre){
        if(nuevoNombre.isBlank()){
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proveedor no existe"));

        proveedor.setNombre(nuevoNombre);

        return ProveedorDTO.desde(proveedor);
    }

    //Metodos relacionados a DELETE

    @Transactional
    public void desactivar(Long id){
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado"));
        proveedor.setActivo(false);
    }

}
