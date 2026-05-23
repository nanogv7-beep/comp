package comp;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {
    // Usamos un Mapa para buscar rápidamente las variables por su nombre
    private Map<String, Simbolo> tabla = new HashMap<>();

    // Intentar agregar una variable
    public boolean insertar(String nombre, String tipo, int linea, int columna) {
        if (tabla.containsKey(nombre)) {
            return false; // Error: Ya existe
        }
        tabla.put(nombre, new Simbolo(nombre, tipo, linea, columna));
        return true;     // Éxito
    }

    // Buscar una variable
    public Simbolo buscar(String nombre) {
        return tabla.get(nombre);
    }
    
    // Verificar si existe
    public boolean existe(String nombre) {
        return tabla.containsKey(nombre);
    }
}