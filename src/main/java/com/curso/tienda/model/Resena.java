package com.curso.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * MODULO 2 — EJERCICIO 1: escribe las relaciones de esta entidad.
 * Documento: docs/Modulo02_Relaciones_entre_entidades.docx, seccion 4.1
 * Referencia: model/DetallePedido.java (tiene el mismo patron de dos FK)
 * Tabla destino: resenas
 *
 * Una resena la escribe UN usuario sobre UN producto. Es decir, tiene dos
 * claves foraneas, igual que DetallePedido.
 *
 * La clase ya compila. Sigue los TODO:
 *
 * TODO 1: anota la clase con @Entity y @Table(name = "resenas").
 * TODO 2: marca el id con @Id y @GeneratedValue(strategy = IDENTITY).
 * TODO 3: mapea el atributo producto con @ManyToOne + @JoinColumn.
 *         La columna se llama producto_id y es NOT NULL.
 *         Recuerda poner fetch = FetchType.LAZY y optional = false.
 * TODO 4: haz lo mismo con usuario (columna usuario_id).
 * TODO 5: anota calificacion, comentario y fecha con su @Column.
 *              calificacion  INTEGER      NOT NULL   (CHECK entre 1 y 5)
 *              comentario    VARCHAR(500) NULL
 *              fecha         DATE         NOT NULL
 * TODO 6: añade un @PrePersist que ponga la fecha de hoy si viene null.
 * TODO 7: implementa esPositiva() y esCritica() segun su javadoc.
 * TODO 8: añade equals y hashCode siguiendo el patron de las demas
 *         entidades (hashCode constante).
 *
 * TODO 9 (EN Producto.java): añade el lado inverso de la relacion.
 *         Un producto tiene muchas resenas:
 *              @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
 *              private List<Resena> resenas = new ArrayList<>();
 *         Piensa antes de escribirlo: ¿deberia llevar cascade = ALL?
 *         Justifica tu respuesta en un comentario.
 */

@Entity
@Table(name="resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Min(1)
    @Max(5)
    @Column(name = "calificacion", nullable = false)
    private int calificacion;

    @Column(name = "comentario", length = 500)
    @Size(max = 500)
    private String comentario;

    @Column(name = "fecha", nullable = false)
    @NotNull
    private LocalDate fecha;

    public Resena() {
    }

    public Resena(Producto producto, Usuario usuario, int calificacion, String comentario) {
        this.producto = producto;
        this.usuario = usuario;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = LocalDate.now();
    }

    //fecha como .now() si vienen null
    @PrePersist
    private void alGuardar(){
        if(fecha == null){
            fecha = LocalDate.now();
        }
    }

    /**
     * TODO 7: una resena es positiva si la calificacion es 4 o 5.
     * Devuelve false mientras no lo implementes.
     */
    public boolean esPositiva() {
        return calificacion >= 4 && calificacion <= 5;
    }

    /**
     * TODO 7: una resena es critica si la calificacion es 1 o 2.
     * Devuelve false mientras no lo implementes.
     */
    public boolean esCritica() {
        return calificacion <=2;
    }

    public Long getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof  Resena otro)){
            return false;
        }
        return id != null && Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode(){
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Resena{id=" + id + ", calificacion=" + calificacion + "}";
    }
}
