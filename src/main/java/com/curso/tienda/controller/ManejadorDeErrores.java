package com.curso.tienda.controller;

import com.curso.tienda.service.RecursoNoEncontradoException;
import com.curso.tienda.service.ReglaDeNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

/**
 * MODULO 3 — EJEMPLO: manejo centralizado de errores.
 * Documento: docs/Modulo03_Repositorios_Spring_Data_JPA.docx
 *
 * @RestControllerAdvice intercepta las excepciones que salen de CUALQUIER
 * controlador y las traduce a una respuesta HTTP coherente.
 *
 * OJO A LA ANOTACION: tiene que ser @RestControllerAdvice y no
 * @ControllerAdvice. La segunda NO incluye @ResponseBody, asi que Spring
 * interpretaria el objeto devuelto como el NOMBRE DE UNA VISTA e
 * intentaria resolver una plantilla llamada "ErrorRespuesta". El
 * resultado es un 500 en lugar del 404 que esperabas. Compila igual: es
 * un fallo que solo aparece al ejecutar.
 *
 * Sin esta clase, cada metodo de cada controlador tendria que envolver la
 * llamada al servicio en un try/catch para decidir el codigo de estado.
 * Con ella, los controladores quedan limpios y el formato del error es el
 * mismo en toda la API.
 */
@RestControllerAdvice
public class ManejadorDeErrores {

    /** Formato uniforme de error para toda la API. */
    public record ErrorRespuesta(int codigo,
                                 String error,
                                 String mensaje,
                                 LocalDateTime momento) {

        static ErrorRespuesta de(HttpStatus estado, String mensaje) {
            //  value() devuelve el codigo HTTP (404, 409...).
            //  NO uses ordinal(), que devuelve la posicion del valor dentro
            //  del enum: compila igual y produce codigos absurdos como 3 o 5.
            return new ErrorRespuesta(estado.value(), estado.name(), mensaje, LocalDateTime.now());
        }
    }

    /** 404: se pidio algo que no existe. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorRespuesta noEncontrado(RecursoNoEncontradoException ex) {
        return ErrorRespuesta.de(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** 409: la peticion es valida pero choca con una regla de negocio. */
    @ExceptionHandler(ReglaDeNegocioException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorRespuesta reglaIncumplida(ReglaDeNegocioException ex) {
        return ErrorRespuesta.de(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** 400: argumentos invalidos detectados por la logica de dominio. */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta peticionInvalida(RuntimeException ex) {
        return ErrorRespuesta.de(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
