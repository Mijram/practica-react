package com.curso.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * MODULO 2 — EJEMPLO: lado "uno" de una relacion 1:N bidireccional.
 * Documento: docs/Modulo02_Relaciones_entre_entidades.docx
 * Tabla:     usuarios
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @NotBlank
    @Column(name = "apellido", nullable = false, length = 60)
    private String apellido;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, length = 120, unique = true)
    private String email;

    @NotBlank
    @Column(name = "ciudad", nullable = false, length = 60)
    private String ciudad;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    /**
     * @OneToMany — un usuario tiene muchos pedidos.
     *
     * mappedBy = "usuario" apunta al ATRIBUTO de la clase Pedido que
     * contiene la referencia de vuelta. No es el nombre de la columna ni
     * el de la tabla: es el nombre del campo Java. Si lo escribes mal,
     * la aplicacion no arranca.
     *
     * cascade = ALL + orphanRemoval = true significan:
     *   - al guardar un usuario se guardan sus pedidos nuevos
     *   - al borrar un usuario se borran sus pedidos
     *   - si sacas un pedido de la lista, se borra de la base de datos
     *
     * CUIDADO: esta combinacion es correcta aqui porque un pedido no tiene
     * sentido sin su usuario. NO la pongas por costumbre. En una relacion
     * Producto-Categoria seria un desastre: borrar una categoria borraria
     * todos sus productos.
     *
     * Por que LAZY: sin ella, cada consulta de usuario traeria todos sus
     * pedidos. Con quinientos usuarios en pantalla eso son quinientas
     * consultas extra. Es el problema N+1, y se explica en el Modulo 2.
     */
    @OneToMany(mappedBy = "usuario",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String email, String ciudad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.ciudad = ciudad;
        this.fechaRegistro = LocalDate.now();
        this.activo = true;
    }

    @PrePersist
    private void alGuardar() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDate.now();
        }
    }

    // ---------------------------------------------------------------
    //  METODOS DE CONVENIENCIA: la pieza que casi todo el mundo olvida
    // ---------------------------------------------------------------

    /**
     * En una relacion bidireccional, Java NO sincroniza los dos lados solo.
     * Si escribieras
     *      usuario.getPedidos().add(pedido);
     * sin mas, el objeto pedido seguiria teniendo usuario == null, y como
     * el lado propietario es Pedido (es quien tiene la columna
     * usuario_id), Hibernate guardaria la FK en null.
     *
     * Por eso toda relacion bidireccional necesita un par de metodos como
     * estos, que tocan LOS DOS lados. Usalos siempre en vez de manipular
     * la lista directamente.
     */
    public void agregarPedido(Pedido pedido) {
        pedidos.add(pedido);
        pedido.setUsuario(this);
    }

    public void quitarPedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setUsuario(null);
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario otro)) {
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
        return "Usuario{id=" + id + ", email='" + email + "'}";
    }
}
