package com.curso.tienda.model;

/**
 * MODULO 2 — EJEMPLO: enum mapeado a una columna.
 * Documento: docs/Modulo02_Relaciones_entre_entidades.docx
 *
 * En la entidad Pedido este enum se anota con:
 *     @Enumerated(EnumType.STRING)
 *
 * POR QUE STRING Y NO ORDINAL (que es el valor por defecto):
 * con ORDINAL, Hibernate guarda la POSICION del valor en el enum: 0 para
 * PENDIENTE, 1 para PAGADO, etc. El dia que alguien inserte un estado
 * nuevo en medio de la lista, o reordene las constantes, todas las filas
 * ya guardadas pasan a significar otra cosa. Sin error, sin aviso.
 *
 * Con STRING se guarda el texto 'PENDIENTE'. Ocupa mas y es inmune a los
 * cambios de orden. En la tabla pedidos hay ademas un CHECK que solo
 * admite estos cinco valores, asi que la base de datos protege el rango
 * aunque alguien inserte por fuera de la aplicacion.
 *
 * USA SIEMPRE EnumType.STRING.
 */
public enum EstadoPedido {

    PENDIENTE("Pendiente de pago"),
    PAGADO("Pagado, en preparacion"),
    ENVIADO("En camino"),
    ENTREGADO("Entregado al cliente"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Un pedido cancelado o entregado ya no admite cambios. */
    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }

    /** Reglas de transicion entre estados, usadas por PedidoService. */
    public boolean puedePasarA(EstadoPedido nuevo) {
        if (this.esFinal()) {
            return false;
        }
        return switch (this) {
            case PENDIENTE -> nuevo == PAGADO || nuevo == CANCELADO;
            case PAGADO    -> nuevo == ENVIADO || nuevo == CANCELADO;
            case ENVIADO   -> nuevo == ENTREGADO;
            default        -> false;
        };
    }
}
