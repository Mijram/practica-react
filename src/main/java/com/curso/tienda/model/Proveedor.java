package com.curso.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * MODULO 1 — EJERCICIO 1: completa esta entidad.
 * Documento: docs/Modulo01_JPA_Hibernate_y_primera_entidad.docx, seccion 4.1
 * Referencia: model/Categoria.java y model/Producto.java
 * Tabla destino: proveedores  (ya existe, ver sql/01_crear_base_datos.sql)
 *
 * LA CLASE YA TRAE LO MINIMO PARA QUE LA APLICACION ARRANQUE:
 * @Entity, @Table, @Id y @GeneratedValue. No son un regalo: sin ellas
 * Hibernate abortaria el arranque, porque Producto tiene una relacion
 * @ManyToOne hacia Proveedor y una relacion no puede apuntar a una clase
 * que no es una entidad. El error seria:
 *
 *     AnnotationException: Association 'Producto.proveedor' targets the
 *     type 'Proveedor' which is not an '@Entity' type
 *
 * Lo que falta es el mapeo explicito y la relacion inversa. Sigue los
 * TODO en orden. Cuando termines, arranca la aplicacion: si el mapeo no
 * coincide con la tabla, Hibernate lo dira al arrancar (porque ddl-auto
 * esta en validate) y el mensaje nombrara la columna exacta.
 *
 * TODO 1: observa que SIN @Column, Hibernate deduce el nombre de la
 *         columna a partir del atributo, convirtiendo camelCase a
 *         snake_case (diasEntrega -> dias_entrega). Arranca la aplicacion
 *         y comprueba que valida. Despues escribe los @Column explicitos:
 *         documentan el contrato y no dependen de una convencion.
 * TODO 2: provoca un fallo a proposito. Pon @Column(name = "dias") sobre
 *         diasEntrega, arranca, y anota el mensaje de error completo.
 *         Despues corrigelo.
 * TODO 3: anota cada atributo con su @Column, respetando nombre,
 *         longitud y nullable segun la tabla:
 *              nombre        VARCHAR(80)  NOT NULL  UNIQUE
 *              pais          VARCHAR(40)  NOT NULL
 *              email         VARCHAR(120) NULL
 *              dias_entrega  INTEGER      NOT NULL   <- ojo al nombre
 *              activo        BOOLEAN      NOT NULL
 * TODO 4: el atributo diasEntrega se llama distinto que la columna
 *         dias_entrega. Resuelvelo con @Column(name = "...").
 * TODO 5: añade la relacion inversa con Producto:
 *         un proveedor surte muchos productos.
 *         Usa @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY).
 *         Fijate en Producto: alli el atributo se llama "proveedor",
 *         y ese es exactamente el valor que va en mappedBy.
 * TODO 6: implementa el metodo esConfiable() segun su javadoc.
 * TODO 7: escribe equals y hashCode siguiendo el patron de Producto
 *         (hashCode constante, equals por id cuando no es null).
 */
@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nombre", nullable = false, length = 80, unique = true)
    private String nombre;

    @NotBlank
    @Column(name = "pais", length = 40, nullable = false)
    private String pais;

    @Email(message = "El email del proveedor no tiene formato valido")
    @Column(name = "email", length = 120)
    private String email;

    @Positive
    @Column(name = "dias_entrega", nullable = false)
    private int diasEntrega;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    /**
     * TODO 5: sustituye @Transient por la relacion inversa.
     *
     *     @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
     *
     * @Transient esta aqui solo para que la aplicacion arranque mientras
     * no hagas el ejercicio: le dice a Hibernate que ignore este campo.
     * Sin @Transient ni @OneToMany, JPA intentaria guardar una List como
     * si fuera una columna normal y el arranque fallaria con:
     *
     *     Could not determine recommended JdbcType for Java type
     *     'java.util.List<com.curso.tienda.model.Producto>'
     *
     * Regla general: dentro de una entidad, TODO campo cuyo tipo sea otra
     * entidad o una coleccion de entidades necesita una anotacion de
     * relacion, o @Transient si no debe persistirse.
     *
     * El valor de mappedBy es el nombre del ATRIBUTO en Producto que
     * apunta de vuelta hacia aqui. Abre Producto.java y compruebalo.
     */
    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();

    public Proveedor() {
    }

    public Proveedor(String nombre, String pais, String email, int diasEntrega) {
        this.nombre = nombre;
        this.pais = pais;
        this.email = email;
        this.diasEntrega = diasEntrega;
        this.activo = true;
    }

    /**
     * TODO 6: un proveedor es confiable si esta activo Y entrega en 15 dias
     * o menos. Devuelve false mientras no lo implementes.
     */
    public boolean esConfiable() {
        return this.isActivo() && this.getDiasEntrega() < 15;
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

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDiasEntrega() {
        return diasEntrega;
    }

    public void setDiasEntrega(int diasEntrega) {
        this.diasEntrega = diasEntrega;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Proveedor otro)) {
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
        return "Proveedor{id=" + id + ", nombre='" + nombre + "', pais='" + pais + "'}";
    }
}
