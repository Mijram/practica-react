package com.curso.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * MODULO 2 — EJEMPLO: la entidad que concentra tres relaciones.
 * Documento: docs/Modulo02_Relaciones_entre_entidades.docx
 * Tabla:     pedidos
 *
 * Pedido es a la vez:
 *   - el lado MUCHOS respecto de Usuario  (@ManyToOne, tiene la FK)
 *   - el lado UNO respecto de DetallePedido (@OneToMany)
 */
@Entity
@Table(name = "pedidos", indexes = {
        @Index(name = "idx_pedidos_usuario", columnList = "usuario_id")
})
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    /**
     * @Enumerated(EnumType.STRING) guarda el TEXTO del enum.
     * Sin esta anotacion, JPA usa ORDINAL por defecto y guarda la posicion
     * numerica, que se corrompe en cuanto alguien reordena el enum.
     * Lee el comentario largo en EstadoPedido.java.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @NotBlank
    @Column(name = "direccion", nullable = false, length = 200)
    private String direccion;

    @Column(name = "costo_envio", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    /** Lado propietario: la columna usuario_id vive en esta tabla. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Las lineas del pedido. Aqui CascadeType.ALL y orphanRemoval SI son
     * correctos: una linea de detalle no existe fuera de su pedido.
     */
    @OneToMany(mappedBy = "pedido",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Usuario usuario, String direccion) {
        this.usuario = usuario;
        this.direccion = direccion;
        this.fecha = LocalDate.now();
        this.estado = EstadoPedido.PENDIENTE;
        this.costoEnvio = BigDecimal.ZERO;
    }

    @PrePersist
    private void alGuardar() {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        if (estado == null) {
            estado = EstadoPedido.PENDIENTE;
        }
    }

    // ---------------------------------------------------------------
    //  Metodos de negocio
    // ---------------------------------------------------------------

    /** Sincroniza los dos lados de la relacion con DetallePedido. */
    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }

    public void quitarDetalle(DetallePedido detalle) {
        detalles.remove(detalle);
        detalle.setPedido(null);
    }

    /**
     * Suma de todas las lineas mas el envio.
     *
     * OJO: este metodo recorre la lista detalles, que es LAZY. Si llamas a
     * calcularTotal() fuera de una transaccion, sobre un pedido que ya
     * salio del contexto de persistencia, obtendras
     * LazyInitializationException. Por eso PedidoService lo llama dentro
     * de metodos anotados con @Transactional.
     */
    public BigDecimal calcularTotal() {
        BigDecimal subtotal = detalles.stream()
                .map(DetallePedido::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return subtotal.add(costoEnvio == null ? BigDecimal.ZERO : costoEnvio);
    }

    public int cantidadTotalArticulos() {
        return detalles.stream().mapToInt(DetallePedido::getCantidad).sum();
    }

    /** Aplica la transicion de estado si las reglas del enum lo permiten. */
    public void cambiarEstado(EstadoPedido nuevo) {
        if (!estado.puedePasarA(nuevo)) {
            throw new IllegalStateException(
                    "No se puede pasar de " + estado + " a " + nuevo);
        }
        this.estado = nuevo;
    }

    // ---------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pedido otro)) {
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
        return "Pedido{id=" + id + ", fecha=" + fecha + ", estado=" + estado + "}";
    }
}
