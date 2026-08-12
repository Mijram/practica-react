package com.curso.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * MODULO 1 — EJEMPLO: entidad con tipos reales y una clave foranea.
 * MODULO 2 — EJEMPLO: aqui viven los dos lados "muchos" de las relaciones.
 * Documento: docs/Modulo01_JPA_Hibernate_y_primera_entidad.docx
 * Tabla:     productos
 *
 * Esta clase reune las decisiones de mapeo que mas se equivocan en
 * proyectos reales. Leela entera antes de escribir tu primera entidad.
 */
@Entity
@Table(name = "productos", indexes = {
        @Index(name = "idx_productos_categoria", columnList = "categoria_id"),
        @Index(name = "idx_productos_activo_cat", columnList = "activo, categoria_id")
})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(name = "nombre", nullable = false, length = 120)
    @Size(max = 120)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    @Size(max = 500)
    private String descripcion;

    /**
     * DINERO: BigDecimal, NUNCA double ni float.
     *
     * Los flotantes son binarios y no pueden representar 0.1 exactamente.
     * Con double, sumar cien veces 0.1 no da 10. En un carrito de compra
     * eso son centavos que desaparecen, y en un informe contable es un
     * descuadre que nadie sabe explicar.
     *
     * precision = 12, scale = 2 se traduce en NUMERIC(12,2): hasta doce
     * digitos en total, dos de ellos decimales.
     */
    @NotNull
    @Positive(message = "El precio debe ser mayor que cero")
    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    /**
     * FECHAS: usa siempre java.time (LocalDate, LocalDateTime).
     * Las clases antiguas java.util.Date y java.sql.Date son mutables y
     * arrastran problemas de zona horaria. Hibernate 6 mapea LocalDate a
     * DATE y LocalDateTime a TIMESTAMP sin necesidad de @Temporal.
     */
    @NotNull
    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    /**
     * @ManyToOne — muchos productos pertenecen a UNA categoria.
     * La clave foranea vive en ESTA tabla (productos.categoria_id), que es
     * siempre el lado @ManyToOne. Ese es el lado "propietario" de la
     * relacion: el que de verdad escribe la columna.
     *
     * fetch = LAZY es OBLIGATORIO en la practica. El valor por defecto de
     * @ManyToOne es EAGER, y eso significa que cada vez que traigas un
     * producto Hibernate traera tambien su categoria, aunque no la mires.
     * Multiplicado por cien productos son cien consultas de mas.
     *
     * optional = false le dice a Hibernate que la FK es NOT NULL, lo que
     * le permite generar un INNER JOIN en lugar de un LEFT JOIN.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /**
     * Un producto puede no tener proveedor asignado, asi que la FK admite
     * null y aqui NO ponemos optional = false.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    /**
     * MODULO 2 — @ManyToMany real.
     *
     * Un producto tiene varias etiquetas y una etiqueta esta en varios
     * productos, y la relacion NO tiene atributos propios: solo une los
     * dos ids. Ese es el unico caso en que @ManyToMany esta justificado.
     *
     * @JoinTable describe la tabla intermedia:
     *   name                 -> nombre de la tabla puente
     *   joinColumns          -> la FK que apunta a ESTA entidad
     *   inverseJoinColumns   -> la FK que apunta a la OTRA
     *
     * Se usa Set y no List a proposito: con List, Hibernate borra e
     * inserta TODAS las filas de la tabla puente cada vez que cambias un
     * elemento. Con Set solo toca la fila que cambio.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "producto_etiqueta",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    private Set<Etiqueta> etiquetas = new HashSet<>();

    // ---------------------------------------------------------------
    //  Constructores
    // ---------------------------------------------------------------

    public Producto() {
    }

    public Producto(String nombre, BigDecimal precio, int stock, Categoria categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.activo = true;
        this.fechaAlta = LocalDate.now();
    }

    /**
     * @PrePersist se ejecuta justo ANTES del INSERT.
     * Sirve para rellenar valores por defecto sin depender de que quien
     * construya el objeto se acuerde. Su pareja es @PreUpdate, que corre
     * antes de cada UPDATE.
     */
    @PrePersist
    private void alGuardar() {
        if (fechaAlta == null) {
            fechaAlta = LocalDate.now();
        }
    }

    // ---------------------------------------------------------------
    //  Metodos de negocio
    // ---------------------------------------------------------------
    //  Una entidad no tiene por que ser una bolsa de getters y setters.
    //  La logica que depende SOLO de los datos de esta fila vive aqui.

    /** Indica si se puede vender la cantidad pedida. */
    public boolean hayStock(int cantidad) {
        return activo && stock >= cantidad;
    }

    /** Descuenta unidades del stock validando que alcancen. */
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        if (stock < cantidad) {
            throw new IllegalStateException(
                    "Stock insuficiente para '" + nombre + "': hay " + stock + ", se piden " + cantidad);
        }
        this.stock -= cantidad;
    }

    /** Devuelve el valor total del inventario de este producto. */
    public BigDecimal valorInventario() {
        return precio.multiply(BigDecimal.valueOf(stock));
    }

    // ---------------------------------------------------------------
    //  Getters y setters
    // ---------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Set<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    /**
     * Metodo auxiliar para mantener los DOS lados de la relacion N:N
     * sincronizados en memoria. Si solo hicieras
     * producto.getEtiquetas().add(e), el objeto etiqueta que ya tuvieras
     * cargado no se enteraria.
     */
    public void agregarEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
        etiqueta.getProductos().add(this);
    }

    public void quitarEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.remove(etiqueta);
        etiqueta.getProductos().remove(this);
    }

    // ---------------------------------------------------------------
    //  equals y hashCode
    // ---------------------------------------------------------------

    /**
     * REGLA IMPORTANTE Y POCO CONOCIDA:
     * en una entidad JPA, hashCode debe devolver un valor CONSTANTE.
     *
     * Motivo: cuando metes una entidad recien creada en un HashSet, su id
     * todavia es null. Al guardarla, la base de datos le asigna un id, y
     * si hashCode dependiera del id, el objeto cambiaria de posicion
     * dentro del Set y dejaria de encontrarse: set.contains(p) devolveria
     * false para un objeto que esta ahi dentro.
     *
     * Por eso: hashCode fijo, y equals comparando el id solo cuando existe.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto otro)) {
            return false;
        }
        return id != null && Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Producto{id=" + id + ", nombre='" + nombre + "', precio=" + precio + ", stock=" + stock + "}";
    }
}
