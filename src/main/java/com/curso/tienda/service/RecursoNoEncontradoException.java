package com.curso.tienda.service;

/**
 * MODULO 3 — EJEMPLO: excepcion de negocio.
 * Documento: docs/Modulo03_Repositorios_Spring_Data_JPA.docx
 *
 * Extiende RuntimeException y no Exception a proposito.
 *
 * Las excepciones comprobadas (checked) obligarian a poner throws en toda
 * la cadena de llamadas, y ademas Spring solo hace ROLLBACK automatico
 * ante RuntimeException. Con una checked, la transaccion se confirmaria
 * aunque el metodo fallara, salvo que lo declares con
 * @Transactional(rollbackFor = ...).
 *
 * REGLA: en la capa de servicio, excepciones NO comprobadas.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontradoException(String tipo, Object id) {
        super(tipo + " con id " + id + " no existe");
    }
}
