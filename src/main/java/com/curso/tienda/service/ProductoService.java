package com.curso.tienda.service;

import com.curso.tienda.model.Categoria;
import com.curso.tienda.model.Producto;
import com.curso.tienda.model.Proveedor;
import com.curso.tienda.model.dto.CrearProductoDTO;
import com.curso.tienda.model.dto.ProductoDTO;
import com.curso.tienda.repository.CategoriaRepository;
import com.curso.tienda.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * MODULO 3, 4 y 5 — EJEMPLO: la capa de servicio.
 * Documento: docs/Modulo03_Repositorios_Spring_Data_JPA.docx
 *
 * QUE HACE ESTA CAPA Y POR QUE EXISTE
 * -----------------------------------
 * El repositorio sabe LEER Y ESCRIBIR. El controlador sabe de HTTP.
 * El servicio es donde vive todo lo demas: las reglas de negocio, la
 * coordinacion entre varios repositorios, las transacciones y la
 * conversion entidad -> DTO.
 *
 * Si metes las reglas en el controlador, no las puedes reutilizar desde
 * una tarea programada ni testear sin levantar el servidor web. Si las
 * metes en el repositorio, las mezclas con el acceso a datos. Por eso la
 * capa intermedia.
 *
 * @Transactional(readOnly = true) A NIVEL DE CLASE
 * ------------------------------------------------
 * Marca por defecto todos los metodos como solo lectura, que es lo que
 * son la mayoria. Eso permite a Hibernate saltarse el "dirty checking"
 * (comparar cada objeto cargado con su estado original al cerrar la
 * transaccion) y le dice al driver que puede usar una replica de lectura
 * si la hay. Los metodos que escriben se anotan de nuevo, sin readOnly,
 * y esa anotacion mas cercana gana.
 *
 * LA REGLA: la transaccion se abre en el SERVICE, nunca en el controller
 * ni en el repository.
 */
@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * INYECCION POR CONSTRUCTOR, no con @Autowired sobre el campo.
     *
     * Tres motivos:
     *   - los campos pueden ser final, asi que nadie los reasigna
     *   - para construir el objeto hay que darle sus dependencias, con lo
     *     que es imposible tener un servicio a medio montar
     *   - en un test creas el servicio con  new ProductoService(mock1, mock2)
     *     sin necesidad de Spring
     *
     * Desde Spring 4.3, si la clase tiene UN solo constructor, no hace
     * falta @Autowired: Spring lo usa igualmente.
     */
    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // =================================================================
    //  LECTURA
    // =================================================================

    /**
     * Devuelve DTO, no entidades.
     *
     * La conversion ocurre AQUI DENTRO, con la transaccion todavia
     * abierta, porque ProductoDTO.desde() toca producto.getCategoria(),
     * que es LAZY. Si devolvieras la lista de entidades y convirtieras en
     * el controlador, la transaccion ya estaria cerrada y saltaria
     * LazyInitializationException.
     *
     * Usa findActivosConCategoria(), que trae la categoria con JOIN FETCH
     * en la misma consulta. Con findByActivoTrue() esto funcionaria
     * igual, pero lanzaria una consulta extra por cada producto: el
     * problema N+1.
     */
    public List<ProductoDTO> listarActivos() {
        return productoRepository.findActivosConCategoria()
                .stream()
                .map(ProductoDTO::desde)
                .toList();
    }

    /** MODULO 5: version paginada de lo anterior. */
    public Page<ProductoDTO> listarPaginado(Pageable pageable) {
        return productoRepository.findByActivoTrue(pageable)
                .map(ProductoDTO::desde);
        //  Page.map convierte el contenido y CONSERVA los metadatos de
        //  paginacion (total, numero de pagina). No hagas
        //  page.getContent().stream()... porque perderias esa informacion.
    }

    /** MODULO 5: buscador con filtros opcionales y paginacion. */
    public Page<ProductoDTO> buscar(String texto, Long categoriaId, Pageable pageable) {
        String limpio = (texto == null || texto.isBlank()) ? null : texto.trim();
        return productoRepository.buscar(limpio, categoriaId, pageable)
                .map(ProductoDTO::desde);
    }

    public ProductoDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        return ProductoDTO.desde(producto);
    }

    public List<ProductoDTO> buscarPorNombre(String texto) {
        return productoRepository.findByNombreContainingIgnoreCase(texto)
                .stream()
                .map(ProductoDTO::desde)
                .toList();
    }

    public List<ProductoDTO> listarBajoStock(int limite) {
        return productoRepository.findByStockLessThan(limite)
                .stream()
                .map(ProductoDTO::desde)
                .toList();
    }

    // =================================================================
    //  ESCRITURA
    // =================================================================

    /**
     * @Transactional sin readOnly: sobrescribe el de la clase.
     *
     * Todo lo que pasa aqui dentro es atomico. Si la validacion de la
     * categoria falla despues de haber guardado algo, se deshace entero.
     */
    @Transactional
    public ProductoDTO crear(CrearProductoDTO datos) {

        //  Regla de negocio: no dos productos con el mismo nombre.
        //  existsBy es mas barato que findBy: no trae la fila, solo
        //  ejecuta un COUNT.
        if (productoRepository.existsByNombre(datos.nombre())) {
            throw new ReglaDeNegocioException(
                    "Ya existe un producto llamado '" + datos.nombre() + "'");
        }

        Categoria categoria = categoriaRepository.findById(datos.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", datos.categoriaId()));

        if (!categoria.isActiva()) {
            throw new ReglaDeNegocioException(
                    "No se pueden dar de alta productos en la categoria inactiva '"
                    + categoria.getNombre() + "'");
        }

        Producto producto = new Producto(datos.nombre(), datos.precio(), datos.stock(), categoria);
        producto.setDescripcion(datos.descripcion());

        //  save() devuelve la instancia GESTIONADA, que ya tiene el id
        //  asignado por la base de datos. Usa siempre el valor devuelto,
        //  no el objeto que le pasaste.
        Producto guardado = productoRepository.save(producto);
        return ProductoDTO.desde(guardado);
    }

    /**
     * ACTUALIZAR SIN LLAMAR A save().
     *
     * Dentro de una transaccion, las entidades que vienen del repositorio
     * estan GESTIONADAS: Hibernate las vigila. Al cerrarse la
     * transaccion compara su estado con el original y lanza el UPDATE de
     * lo que cambio. A eso se le llama dirty checking.
     *
     * Por eso aqui no hay ningun save() y el cambio se guarda igual.
     * Llamarlo no seria un error, pero es innecesario y hace pensar que
     * sin el no se guardaria.
     *
     * Ojo: esto solo funciona si el metodo es @Transactional. Fuera de
     * una transaccion, la entidad esta "detached" y los cambios se pierden
     * en silencio.
     */
    @Transactional
    public ProductoDTO actualizarPrecio(Long id, BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio.signum() <= 0) {
            throw new ReglaDeNegocioException("El precio debe ser mayor que cero");
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        producto.setPrecio(nuevoPrecio);   // <- sin save(): dirty checking
        return ProductoDTO.desde(producto);
    }

    @Transactional
    public ProductoDTO reponerStock(Long id, int unidades) {
        if (unidades <= 0) {
            throw new ReglaDeNegocioException("Las unidades a reponer deben ser positivas");
        }
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        producto.setStock(producto.getStock() + unidades);
        if (!producto.isActivo() && producto.getStock() > 0) {
            producto.setActivo(true);
        }
        return ProductoDTO.desde(producto);
    }

    /**
     * BORRADO LOGICO en lugar de fisico.
     *
     * Un producto que ya aparece en pedidos historicos NO se puede borrar:
     * la clave foranea de detalle_pedido lo impide, y aunque no lo
     * impidiera, borrarlo dejaria facturas antiguas apuntando al vacio.
     *
     * La solucion habitual es marcarlo como inactivo. La fila sigue ahi
     * para el historico, pero deja de aparecer en el catalogo.
     */
    @Transactional
    public void desactivar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        producto.setActivo(false);
    }

    /**
     * Operacion masiva con @Modifying: un solo UPDATE para todas las
     * filas, en vez de cargarlas y modificarlas una a una.
     * Devuelve cuantas filas cambio.
     */
    @Transactional
    public int desactivarSinStock() {
        return productoRepository.desactivarSinStock();
    }

    // =================================================================
    //  MODULO 3 — EJERCICIO 1: completa estos metodos
    // =================================================================
    //  Documento: Modulo03_Repositorios_Spring_Data_JPA.docx, seccion 4.1

    /**
     * TODO E1: devuelve los productos de una categoria dada, buscando por
     * el NOMBRE de la categoria y no por su id. Si la categoria no existe,
     * lanza RecursoNoEncontradoException.
     * Devuelve lista vacia mientras no lo implementes.
     */
    public List<ProductoDTO> listarPorNombreDeCategoria(String nombreCategoria) {
        return List.of();
    }

    /**
     * TODO E2: aplica un descuento porcentual a todos los productos
     * activos de una categoria. El porcentaje llega como entero (15
     * significa 15%). Valida que este entre 1 y 50; si no, lanza
     * ReglaDeNegocioException. Devuelve cuantos productos modifico.
     *
     * PISTA: recorre los productos dentro de un metodo @Transactional y
     * cambia el precio con setPrecio(). No necesitas llamar a save().
     * PISTA: para el calculo, precio.multiply(BigDecimal) y recuerda
     * usar setScale(2, RoundingMode.HALF_UP) al final.
     */
    public int aplicarDescuentoACategoria(Long categoriaId, int porcentaje) {
        return 0;
    }

    /**
     * TODO E3: devuelve el valor total del inventario activo, es decir,
     * la suma de precio * stock de todos los productos activos.
     * PISTA: Producto ya tiene el metodo valorInventario().
     * PISTA: reduce(BigDecimal.ZERO, BigDecimal::add) sobre el stream.
     */
    public BigDecimal calcularValorInventario() {
        return BigDecimal.ZERO;
    }

    /**
     * TODO E4: reasigna todos los productos de un proveedor a otro.
     * Si el proveedor destino no existe o esta inactivo, lanza
     * ReglaDeNegocioException. Devuelve cuantos productos movio.
     *
     * PISTA: necesitaras crear ProveedorRepository (no existe todavia).
     * Es parte del ejercicio: creala siguiendo el patron de
     * CategoriaRepository, e inyectala en el constructor de esta clase.
     */
    public int reasignarProveedor(Long proveedorOrigenId, Long proveedorDestinoId) {
        return 0;
    }
}
