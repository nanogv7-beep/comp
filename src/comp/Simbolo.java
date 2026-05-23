/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comp;

/**
 *
 * @author nanog
 */
public class Simbolo {
    String nombre;
    String tipoDato; // "entero", "cadena", "nota", "booleano"
    Object valor;    // Opcional por ahora
    int linea;
    int columna;

    public Simbolo(String nombre, String tipoDato, int linea, int columna) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.linea = linea;
        this.columna = columna;
    }
}