package com.curso.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * MODULO 2 — EJEMPLO: lado inverso de un @ManyToMany.
 * Documento: docs/Modulo02_Relaciones_entre_entidades.docx
 * Tabla:     etiquetas  (+ la tabla puente producto_etiqueta)
 *
 * Fijate en la diferencia con Producto:
 *   - Producto declara @JoinTable  -> es el lado PROPIETARIO,
 *     el que decide como se escribe la tabla puente.
 *   - Etiqueta declara mappedBy    -> es el lado INVERSO,
 *     que solo lee. Si añades un producto SOLO desde aqui, no se guarda.
 *
 * En toda relacion bidireccional hay exactamente un propietario. El
 * propietario es siempre el lado que NO tiene mappedBy.
 */
@Entity
@Table(name = "etiquetas")
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nombre", nullable = false, length = 40, unique = true)
    private String nombre;

    @Column(name = "color", nullable = false, length = 7)
    private String color = "#CCCCCC";

    @ManyToMany(mappedBy = "etiquetas", fetch = FetchType.LAZY)
    private Set<Producto> productos = new HashSet<>();

    public Etiqueta() {
    }

    public Etiqueta(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<Producto> getProductos() {
        return productos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Etiqueta otra)) {
            return false;
        }
        return id != null && Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Etiqueta{id=" + id + ", nombre='" + nombre + "'}";
    }
}
