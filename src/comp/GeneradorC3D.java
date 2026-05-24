package comp;

import java.util.ArrayList;

public class GeneradorC3D {
    public ArrayList<Cuarteto> cuadruplos = new ArrayList<>();
    public ArrayList<Cuarteto> cuadruplosOptimizados = new ArrayList<>(); // Nueva lista para el código limpio
    
    private int contadorTemp = 1;

    public String nuevoTemporal() {
        return "t" + (contadorTemp++);
    }

    public void agregar(String op, String arg1, String arg2, String res) {
        cuadruplos.add(new Cuarteto(op, arg1, arg2, res));
    }

    // --- EL CEREBRO DE LA OPTIMIZACIÓN ---
    public void optimizar() {
        cuadruplosOptimizados.clear();
        
        for (Cuarteto c : cuadruplos) {
            String op = c.operador;
            String arg1 = c.arg1;
            String arg2 = c.arg2;
            String res = c.resultado;

            // TÉCNICA 1: Plegamiento de Constantes (Pre-calcular matemáticas)
            if (esNumero(arg1) && esNumero(arg2)) {
                double val1 = Double.parseDouble(arg1);
                double val2 = Double.parseDouble(arg2);
                double resultadoOp = 0;
                boolean optimizado = false;

                switch (op) {
                    case "+": resultadoOp = val1 + val2; optimizado = true; break;
                    case "-": resultadoOp = val1 - val2; optimizado = true; break;
                    case "*": resultadoOp = val1 * val2; optimizado = true; break;
                    case "/": if (val2 != 0) { resultadoOp = val1 / val2; optimizado = true; } break;
                }

                if (optimizado) {
                    String nuevoValor = (resultadoOp == Math.floor(resultadoOp)) ? 
                                        String.valueOf((long)resultadoOp) : String.valueOf(resultadoOp);
                    cuadruplosOptimizados.add(new Cuarteto("=", nuevoValor, "", res));
                    continue; 
                }
            }

            // TÉCNICA 2: Simplificación Algebraica (Reglas de Ceros y Unos)
            if (op.equals("+")) {
                if (arg1.equals("0")) { cuadruplosOptimizados.add(new Cuarteto("=", arg2, "", res)); continue; }
                if (arg2.equals("0")) { cuadruplosOptimizados.add(new Cuarteto("=", arg1, "", res)); continue; }
            } else if (op.equals("*")) {
                if (arg1.equals("1")) { cuadruplosOptimizados.add(new Cuarteto("=", arg2, "", res)); continue; }
                if (arg2.equals("1")) { cuadruplosOptimizados.add(new Cuarteto("=", arg1, "", res)); continue; }
                if (arg1.equals("0") || arg2.equals("0")) { cuadruplosOptimizados.add(new Cuarteto("=", "0", "", res)); continue; }
            }

            // Si no se pudo optimizar, se pasa igual
            cuadruplosOptimizados.add(new Cuarteto(op, arg1, arg2, res));
        }
    }

    private boolean esNumero(String str) {
        if (str == null || str.isEmpty()) return false;
        try { Double.parseDouble(str); return true; } catch (NumberFormatException e) { return false; }
    }
}