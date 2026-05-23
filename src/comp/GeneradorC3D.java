package comp;

import java.util.ArrayList;

public class GeneradorC3D {
    public ArrayList<Cuarteto> cuadruplos = new ArrayList<>();
    private int contadorTemp = 1;

    // Genera nombres como t1, t2, t3...
    public String nuevoTemporal() {
        return "t" + (contadorTemp++);
    }

    // Agrega una fila a nuestra tabla de cuádruplos
    public void agregar(String op, String arg1, String arg2, String res) {
        cuadruplos.add(new Cuarteto(op, arg1, arg2, res));
    }

    // Imprime la tabla final
    public void imprimirCodigoIntermedio() {
        System.out.println("\n=== CÓDIGO INTERMEDIO (CUÁDRUPLOS) ===");
        System.out.println(String.format("%-10s | %-10s | %-10s | %-10s", "OPERADOR", "ARG 1", "ARG 2", "RESULTADO"));
        System.out.println("-----------------------------------------------------");
        for (Cuarteto c : cuadruplos) {
            System.out.println(c.toString());
        }
        System.out.println("=====================================================\n");
    }
}