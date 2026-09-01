package com.curso.tienda.model.dto;

import com.curso.tienda.model.Producto;

import java.math.BigDecimal;

/**
 * MODULO 3 — EJEMPLO: por que los controladores NO devuelven entidades.
 * Documento: docs/Modulo03_Repositorios_Spring_Data_JPA.docx
 *
 * Un DTO (Data Transfer Object) es un objeto plano que representa lo que
 * la API expone hacia fuera. Aqui usamos un record, que en Java es
 * inmutable y no necesita getters, constructor ni equals escritos a mano.
 *
 * TRES RAZONES PARA NO DEVOLVER LA ENTIDAD DIRECTAMENTE:
 *
 * 1. LazyInitializationException. Producto tiene relaciones LAZY. Cuando
 *    Jackson intenta convertir el objeto a JSON, la transaccion ya se
 *    cerro, y al tocar producto.getCategoria() salta la excepcion. Es el
 *    error numero uno de quien empieza con Spring Data.
 *
 * 2. Recursion infinita. Producto apunta a Categoria y Categoria tiene
 *    una lista de Producto. Jackson entra en bucle hasta desbordar la
 *    pila. Se puede parchear con @JsonIgnore, pero eso es ensuciar el
 *    modelo para resolver un problema de la capa web.
 *
 * 3. Acoplamiento. Si expones la entidad, cualquier cambio en la tabla
 *    cambia tu API publica. Con un DTO decides que sale y que no: aqui,
 *    por ejemplo, no exponemos el proveedor ni el stock exacto.
 *
 * REGLA: la entidad vive entre repository y service. Del service hacia
 * el controller viajan DTO.
 */
public record ProductoDTO(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        int stock,
        boolean activo,
        String categoria,
        String proveedor,
        String imagen
) {

    /**
     * Fabrica que convierte una entidad en su DTO.
     *
     * IMPORTANTE: este metodo toca producto.getCategoria(), que es LAZY.
     * Por eso solo se puede llamar DENTRO de una transaccion abierta, es
     * decir, desde un metodo @Transactional del service. Si lo llamas
     * desde el controller, ya es tarde.
     */
    public static ProductoDTO desde(Producto p) {
        return new ProductoDTO(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getStock(),
                p.isActivo(),
                p.getCategoria() != null ? p.getCategoria().getNombre() : null,
                p.getProveedor() != null ? p.getProveedor().getNombre() : "Sin proveedor",
                p.getImagen()
        );
    }
}
