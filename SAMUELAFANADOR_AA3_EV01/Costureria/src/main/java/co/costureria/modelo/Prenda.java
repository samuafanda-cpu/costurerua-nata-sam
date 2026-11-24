package co.costureria.modelo;
import java.time.LocalDate;
/** Datos de una prenda y su orden de recepción. */
public record Prenda(int id, int cliente, String tipo, String arreglo, String estado, LocalDate estimada) {}
