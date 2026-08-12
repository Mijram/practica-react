package com.curso.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "almacenes")
public class Almacen {

    @Id
    @NotBlank(message = "El código identificador no puede estar vacío")
    @Size(min = 6, max = 6, message = "El código debe ser de exactamente 6 caracteres")
    @Column(name = "id", length = 6, nullable = false)
    private String id;

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Column(name = "nombre", nullable = false, length = 120)
    @Size(max = 120)
    private String nombre;

    @Size(max = 120)
    @NotBlank(message = "El almacen debe tener una ciudad de origen")
    @Column(name = "ciudad", length = 120, nullable = false)
    private String ciudad;

    @Size(max = 120)
    @NotBlank(message = "El almacen debe tener una dirección fisica")
    @Column(name = "direccion", length = 120, nullable = false)
    private String direccion;

    //el BigDecimal es mas exacto y no genera redondeos con numeros grandes,
    // lo cual es clave en este parametro que debe ser exacto
    @NotNull(message = "El almacen no puede tener capacidad nula")
    @Positive(message = "El almacen no puede tener capacidad negativa")
    @Column(name = "capacidad", nullable = false)
    private BigDecimal capacidad;

    @NotNull
    @Column(name = "fecha_apertura")
    private LocalDate fechaApertura;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    /* el usuario no puede ser nulo, ya que un almacen siempre debe ser
        manejado por un usuario
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Almacen(){}


    public Almacen(String id, String nombre, String ciudad, String direccion, BigDecimal capacidad, LocalDate fechaApertura, boolean activo, Usuario usuario) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.capacidad = capacidad;
        this.fechaApertura = fechaApertura;
        this.activo = activo;
        this.usuario = usuario;
    }

    @PrePersist
    private void alGuardar() {
        if (fechaApertura == null) {
            fechaApertura = LocalDate.now();
        }
    }

    public BigDecimal capacidadDisponible(BigDecimal ocupado){
        if(ocupado.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("el valor ingresado es negativo");
        }
        if(ocupado.compareTo(capacidad) > 0){
            throw new IllegalArgumentException("el valor ingresado es mayor a la capacidad del almacen");
        }
        return capacidad.subtract(ocupado);
    }

    //getters


    public String getNombre() {
        return nombre;
    }

    public BigDecimal getCapacidad() {
        return capacidad;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    //setters


    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setCapacidad(BigDecimal capacidad) {
        this.capacidad = capacidad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setFechaApertura(LocalDate fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Almacen otro)) {
            return false;
        }
        return id != null && Objects.equals(id, otro.id);
    }

    @Override
    public String toString() {
        return "Almacen{id='" + id + "', nombre='" + nombre + "', ciudad='" + ciudad + "', direccion'" + direccion + "', capacidad:'" + capacidad + "', fecha_apertura:'" + fechaApertura + "', activo:'" + activo + "'}";
    }
}
