package com.curso.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * MODULO 1 — EJEMPLO: la entidad mas simple posible.
 * Documento: docs/Modulo01_JPA_Hibernate_y_primera_entidad.docx
 * Tabla:     categorias   (ver sql/01_crear_base_datos.sql)
 *
 * Una entidad es una clase Java que representa una TABLA.
 * Cada instancia de la clase es una FILA de esa tabla.
 * Cada atributo es una COLUMNA.
 *
 * REQUISITOS QUE TODA ENTIDAD JPA DEBE CUMPLIR:
 *   1. Llevar @Entity.
 *   2. Tener un atributo marcado con @Id.
 *   3. Tener un constructor sin argumentos (Hibernate lo necesita para
 *      crear el objeto antes de rellenarlo; puede ser protected).
 *   4. NO ser final, y sus atributos persistentes tampoco.
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    /**
     * @Id marca la clave primaria.
     *
     * @GeneratedValue(strategy = IDENTITY) delega la generacion del id a
     * la base de datos: en PostgreSQL, a la columna
     * BIGINT GENERATED ALWAYS AS IDENTITY.
     *
     * Por que Long y no long: el tipo primitivo no puede valer null, y una
     * entidad que todavia no se ha guardado NO tiene id. Con Long, un id
     * null significa "esta fila aun no existe en la base de datos", que es
     * informacion util. Con long valdria 0, que es indistinguible de un id
     * real. USA SIEMPRE EL TIPO ENVOLTORIO EN LOS ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column describe la columna. Si no la pones, Hibernate usa el nombre
     * del atributo y valores por defecto (nullable, longitud 255).
     * Ponerla explicita documenta el contrato con la base de datos.
     *
     * OJO: @Column(nullable = false) le dice a Hibernate como CREAR la
     * columna, pero no valida nada al guardar. Quien valida es
     * @NotBlank, de Bean Validation. Son dos cosas distintas y se usan
     * juntas: una define el esquema, la otra rechaza datos malos antes de
     * llegar a la base de datos.
     */
    @NotBlank(message = "El nombre de la categoria es obligatorio")
    @Size(max = 60)
    @Column(name = "nombre", nullable = false, length = 60, unique = true)
    private String nombre;

    @Size(max = 200)
    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    /**
     * Lado "uno" de la relacion 1:N con Producto. Se explica a fondo en el
     * Modulo 2; por ahora quedate con tres cosas:
     *   - mappedBy = "categoria" significa "la clave foranea NO esta en mi
     *     tabla, esta en la de Producto, en su atributo categoria".
     *   - fetch = LAZY: la lista NO se carga al traer la categoria, solo
     *     cuando alguien llama a getProductos().
     *   - se inicializa a lista vacia para que nunca sea null.
     */
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();

    // ---------------------------------------------------------------
    //  Constructores
    // ---------------------------------------------------------------

    /** Constructor vacio EXIGIDO por JPA. No lo borres. */
    public Categoria() {
    }

    /** Constructor de conveniencia para crear categorias en el codigo. */
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activa = true;
    }

    // ---------------------------------------------------------------
    //  Getters y setters
    // ---------------------------------------------------------------
    //  JPA puede acceder a los campos directamente (porque @Id esta sobre
    //  el atributo), pero el resto de la aplicacion usa estos metodos.
    //  Fijate en que NO hay setId(): el id lo asigna la base de datos y
    //  nadie mas deberia poder cambiarlo.

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

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    /**
     * toString NO debe incluir la lista de productos.
     * Si lo hiciera, imprimir una categoria dispararia la carga de todos
     * sus productos, y como Producto tiene una referencia de vuelta a
     * Categoria, entraria en un bucle infinito.
     * REGLA: en el toString de una entidad, nunca incluyas relaciones.
     */
    @Override
    public String toString() {
        return "Categoria{id=" + id + ", nombre='" + nombre + "', activa=" + activa + "}";
    }
}
