package com.curso.tienda.service;

/**
 * MODULO 3 — EJEMPLO: excepcion para reglas de negocio incumplidas.
 * Documento: docs/Modulo03_Repositorios_Spring_Data_JPA.docx
 *
 * Se distingue de RecursoNoEncontradoException porque el controlador las
 * traduce a codigos HTTP distintos:
 *     RecursoNoEncontradoException -> 404 Not Found
 *     ReglaDeNegocioException      -> 409 Conflict
 * Ver ManejadorDeErrores en el paquete controller.
 */
public class ReglaDeNegocioException extends RuntimeException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
