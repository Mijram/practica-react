package com.curso.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * MODULO 2 — EJEMPLO: por que una tabla intermedia NO siempre es @ManyToMany.
 * Documento: docs/Modulo02_Relaciones_entre_entidades.docx
 * Tabla:     detalle_pedido
 *
 * Un pedido tiene muchos productos y un producto esta en muchos pedidos.
 * Suena a @ManyToMany, y es la trampa mas comun del modelado con JPA.
 *
 * NO lo es, porque la relacion TIENE ATRIBUTOS PROPIOS: cantidad y
 * precio_unitario. Un @ManyToMany solo puede guardar los dos ids en la
 * tabla puente, sin espacio para nada mas.
 *
 * La regla: si la union entre A y B necesita guardar algo, deja de ser
 * una relacion y pasa a ser una ENTIDAD con dos @ManyToOne.
 *
 * Por que guardamos precio_unitario si el producto ya tiene precio:
 * porque el precio del catalogo cambia. Si manana sube, todas las
 * facturas del ano pasado cambiarian de importe. El precio historico se
 * congela en el momento de la compra. Este detalle no es de JPA, es de
 * modelado, y separa un modelo correcto de uno que parece correcto.
 */
@Entity
@Table(name = "detalle_pedido",
       uniqueConstraints = @UniqueConstraint(
               name = "uq_detalle", columnNames = {"pedido_id", "producto_id"}))
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Positive
    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @NotNull
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    public DetallePedido() {
    }

    /**
     * Al crear la linea se copia el precio ACTUAL del producto.
     * A partir de ese momento la linea es independiente del catalogo.
     */
    public DetallePedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
    }

    /** Subtotal de la linea: cantidad por precio congelado. */
    public BigDecimal calcularSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetallePedido otro)) {
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
        return "DetallePedido{id=" + id + ", cantidad=" + cantidad
                + ", precioUnitario=" + precioUnitario + "}";
    }
}
