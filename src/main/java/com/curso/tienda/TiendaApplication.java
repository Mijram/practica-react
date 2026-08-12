package com.curso.tienda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CURSO JPA / HIBERNATE — Clase principal
 * Documento: docs/00_Guia_General_del_Curso.docx
 *
 * Punto de entrada de la aplicacion. Ejecuta esta clase (triangulo verde
 * junto al main en IntelliJ) para levantar el servidor en el puerto 8080.
 *
 * @SpringBootApplication equivale a tres anotaciones juntas:
 *   @Configuration        esta clase puede definir beans
 *   @EnableAutoConfiguration  Spring configura solo lo que encuentra en el
 *                         classpath (ve PostgreSQL -> configura el DataSource;
 *                         ve Spring Data JPA -> configura Hibernate)
 *   @ComponentScan        busca @Service, @Repository y @RestController
 *                         en ESTE paquete y en todos los que cuelgan de el
 *
 * Esa ultima parte explica por que TiendaApplication vive en
 * com.curso.tienda y no dentro de una subcarpeta: si estuviera en
 * com.curso.tienda.config, Spring no encontraria los controladores.
 */
@SpringBootApplication
public class TiendaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiendaApplication.class, args);
    }
}
