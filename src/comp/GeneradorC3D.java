package comp;

import java.util.ArrayList;

public class GeneradorC3D {
    public ArrayList<Cuarteto> cuadruplos = new ArrayList<>();
    public ArrayList<Cuarteto> cuadruplosOptimizados = new ArrayList<>(); // Nueva lista para el código limpio
    
    private int contadorTemp = 1;
    private int contadorLabel = 1;

    public String nuevoTemporal() { return "t" + (contadorTemp++); }
    public String nuevoLabel()    { return "L" + (contadorLabel++); }

    // Pila general para labels de control de flujo (if/while/repetir)
    private java.util.Deque<String> pilaLabels = new java.util.ArrayDeque<>();
    public void   pushLabel(String l) { pilaLabels.push(l); }
    public String popLabel()          { return pilaLabels.isEmpty() ? "L_ERR" : pilaLabels.pop(); }
    public String peekLabel()         { return pilaLabels.isEmpty() ? "L_ERR" : pilaLabels.peek(); }

    // Pilas para ROMPER (break) y CONTINUAR (continue)
    private java.util.Deque<String> pilaRomper    = new java.util.ArrayDeque<>();
    private java.util.Deque<String> pilaContinuar = new java.util.ArrayDeque<>();
    public void   pushBreakLabel(String l)    { pilaRomper.push(l); }
    public void   popBreakLabel()             { if (!pilaRomper.isEmpty())    pilaRomper.pop(); }
    public String getBreakLabel()             { return pilaRomper.isEmpty()    ? null : pilaRomper.peek(); }
    public void   pushContinueLabel(String l) { pilaContinuar.push(l); }
    public void   popContinueLabel()          { if (!pilaContinuar.isEmpty()) pilaContinuar.pop(); }
    public String getContinueLabel()          { return pilaContinuar.isEmpty() ? null : pilaContinuar.peek(); }

    // Pilas para el contexto anidado de 'segun' (switch)
    private java.util.Deque<String> pilaSegunVar = new java.util.ArrayDeque<>();
    private java.util.Deque<String> pilaSegunFin = new java.util.ArrayDeque<>();
    public void   iniciarSegun(String var, String fin) { pilaSegunVar.push(var); pilaSegunFin.push(fin); }
    public String getSegunVar() { return pilaSegunVar.isEmpty() ? null : pilaSegunVar.peek(); }
    public String getSegunFin() { return pilaSegunFin.isEmpty() ? null : pilaSegunFin.peek(); }
    public void   finalizarSegun() {
        if (!pilaSegunVar.isEmpty()) pilaSegunVar.pop();
        if (!pilaSegunFin.isEmpty()) pilaSegunFin.pop();
    }

    public void agregar(String op, String arg1, String arg2, String res) {
        cuadruplos.add(new Cuarteto(op, arg1, arg2, res));
    }

    // --- EL CEREBRO DE LA OPTIMIZACIÓN ---
    public void optimizar() {
        cuadruplosOptimizados.clear();

        // ================================================================
        // PASO 0: Mapa de propagación de constantes
        // Variables de usuario asignadas exactamente UNA vez con un valor
        // constante (número, true, false).  Esas variables serán sustituidas
        // por su valor en los cuádruplos que las usen como argumento.
        // ================================================================
        java.util.Map<String, String> constMap = new java.util.HashMap<>();
        java.util.Set<String> noConst = new java.util.HashSet<>();
        for (Cuarteto c : cuadruplos) {
            String res = c.resultado;
            if (res == null || res.isEmpty()) continue;
            // Solo asignación directa: = CONST "" VAR  (no temporales de usuario)
            if (c.operador.equals("=") && esConstante(c.arg1)
                    && (c.arg2 == null || c.arg2.isEmpty())
                    && !esTemporal(res)) {
                if (!noConst.contains(res)) {
                    if (constMap.containsKey(res)) {
                        noConst.add(res); constMap.remove(res); // segunda asignación → no constante
                    } else {
                        constMap.put(res, normalizarValor(c.arg1));
                    }
                }
            } else if (!res.isEmpty()) {
                noConst.add(res); constMap.remove(res); // cualquier otra asignación → no constante
            }
        }

        // ================================================================
        // PASO 1: Propagación + Plegamiento de constantes + Simplificación
        // ================================================================
        for (Cuarteto c : cuadruplos) {
            String op   = c.operador;
            String arg1 = c.arg1;
            String arg2 = c.arg2 == null ? "" : c.arg2;
            String res  = c.resultado;

            // Sustituir variables con su valor constante conocido
            String pa1 = constMap.getOrDefault(arg1, arg1);
            String pa2 = constMap.getOrDefault(arg2, arg2);
            boolean propagado = !pa1.equals(arg1) || !pa2.equals(arg2);

            // Marcar cuádruplos que son fuente de propagación (= CONST "" VAR)
            if (op.equals("=") && !propagado && arg2.isEmpty()
                    && esConstante(arg1) && constMap.containsKey(res) && !esTemporal(res)) {
                Cuarteto q = new Cuarteto(op, arg1, arg2, res);
                q.nota = "Fuente de propagación";
                cuadruplosOptimizados.add(q);
                continue;
            }

            // ---- Plegamiento de constantes (ambos args son números) ----
            if (esNumero(pa1) && esNumero(pa2) && !pa2.isEmpty()) {
                double v1 = Double.parseDouble(pa1), v2 = Double.parseDouble(pa2);
                Double fr = null;
                switch (op) {
                    case "+":  fr = v1 + v2; break;
                    case "-":  fr = v1 - v2; break;
                    case "*":  fr = v1 * v2; break;
                    case "/":  if (v2 != 0) fr = Math.floor(v1 / v2); break;
                    case "%":  if (v2 != 0) fr = v1 % v2;             break;
                    case ">":  fr = (v1 >  v2) ? 1.0 : 0.0; break;
                    case "<":  fr = (v1 <  v2) ? 1.0 : 0.0; break;
                    case ">=": fr = (v1 >= v2) ? 1.0 : 0.0; break;
                    case "<=": fr = (v1 <= v2) ? 1.0 : 0.0; break;
                    case "==": fr = (v1 == v2) ? 1.0 : 0.0; break;
                    case "!=": fr = (v1 != v2) ? 1.0 : 0.0; break;
                    case "AND": fr = (v1 != 0 && v2 != 0) ? 1.0 : 0.0; break;
                    case "OR":  fr = (v1 != 0 || v2 != 0) ? 1.0 : 0.0; break;
                }
                if (fr != null) {
                    String nv = (fr == Math.floor(fr)) ? String.valueOf(fr.longValue()) : String.valueOf(fr);
                    Cuarteto q = new Cuarteto("=", nv, "", res);
                    q.nota = propagado ? "Propagación + Plegamiento" : "Plegamiento de constantes";
                    cuadruplosOptimizados.add(q);
                    continue;
                }
            }

            // ---- NOT con constante ----
            if (op.equals("NOT") && esNumero(pa1)) {
                double v1 = Double.parseDouble(pa1);
                Cuarteto q = new Cuarteto("=", v1 != 0 ? "0" : "1", "", res);
                q.nota = propagado ? "Propagación + Plegamiento" : "Plegamiento de constantes";
                cuadruplosOptimizados.add(q);
                continue;
            }

            // ---- Simplificación algebraica ----
            Cuarteto simplif = null; String notaSimplif = "";
            if (op.equals("+")) {
                if (pa1.equals("0"))  { simplif = new Cuarteto("=", pa2, "", res); notaSimplif = "Simplif.: 0 + X = X"; }
                else if (pa2.equals("0")) { simplif = new Cuarteto("=", pa1, "", res); notaSimplif = "Simplif.: X + 0 = X"; }
            } else if (op.equals("*")) {
                if (pa1.equals("0") || pa2.equals("0")) { simplif = new Cuarteto("=", "0", "", res); notaSimplif = "Simplif.: X × 0 = 0"; }
                else if (pa1.equals("1")) { simplif = new Cuarteto("=", pa2, "", res); notaSimplif = "Simplif.: 1 × X = X"; }
                else if (pa2.equals("1")) { simplif = new Cuarteto("=", pa1, "", res); notaSimplif = "Simplif.: X × 1 = X"; }
            } else if (op.equals("-") && pa2.equals("0")) {
                simplif = new Cuarteto("=", pa1, "", res); notaSimplif = "Simplif.: X - 0 = X";
            }
            if (simplif != null) {
                simplif.nota = notaSimplif;
                cuadruplosOptimizados.add(simplif);
                continue;
            }

            // ---- Propagación sin plegamiento (args sustituidos pero no reducibles) ----
            if (propagado) {
                Cuarteto q = new Cuarteto(op, pa1, pa2, res);
                q.nota = "Propagación de constantes";
                cuadruplosOptimizados.add(q);
            } else {
                cuadruplosOptimizados.add(new Cuarteto(op, arg1, arg2, res));
            }
        }

        // ================================================================
        // PASO 2: Eliminación de temporales de un solo uso (peephole)
        // Patrón: (op a b tN) + (= tN "" X)  →  (op a b X)
        // Condición: tN definido y usado exactamente una vez en la lista.
        // ================================================================
        java.util.Map<String, Integer> defs = new java.util.HashMap<>();
        java.util.Map<String, Integer> usos = new java.util.HashMap<>();
        for (Cuarteto c : cuadruplosOptimizados) {
            if (esTemporal(c.resultado)) defs.merge(c.resultado, 1, Integer::sum);
            if (esTemporal(c.arg1))      usos.merge(c.arg1,      1, Integer::sum);
            if (esTemporal(c.arg2))      usos.merge(c.arg2,      1, Integer::sum);
        }
        ArrayList<Cuarteto> lista2 = new ArrayList<>();
        int idx = 0;
        while (idx < cuadruplosOptimizados.size()) {
            Cuarteto c = cuadruplosOptimizados.get(idx);
            boolean plegado = false;
            if (idx + 1 < cuadruplosOptimizados.size()) {
                Cuarteto sig = cuadruplosOptimizados.get(idx + 1);
                boolean sigEsAsig = sig.operador.equals("=")
                        && sig.arg1.equals(c.resultado)
                        && (sig.arg2 == null || sig.arg2.isEmpty());
                boolean tempUnico = defs.getOrDefault(c.resultado, 0) == 1
                        && usos.getOrDefault(c.resultado, 0) == 1;
                if (esTemporal(c.resultado) && sigEsAsig && tempUnico) {
                    String base = (c.nota == null || c.nota.isEmpty()) ? "" : c.nota + " + ";
                    Cuarteto q = new Cuarteto(c.operador, c.arg1, c.arg2, sig.resultado);
                    q.nota = base + "Elim. temporal (" + c.resultado + ")";
                    lista2.add(q);
                    idx += 2;
                    plegado = true;
                }
            }
            if (!plegado) { lista2.add(c); idx++; }
        }
        cuadruplosOptimizados = lista2;
    }

    private boolean esTemporal(String s) {
        return s != null && s.matches("t\\d+");
    }

    private boolean esConstante(String s) {
        return s != null && !s.isEmpty()
                && (esNumero(s) || "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s));
    }

    private String normalizarValor(String s) {
        if ("true".equalsIgnoreCase(s))  return "1";
        if ("false".equalsIgnoreCase(s)) return "0";
        return s;
    }

    private boolean esNumero(String str) {
        if (str == null || str.isEmpty()) return false;
        try { Double.parseDouble(str); return true; } catch (NumberFormatException e) { return false; }
    }
}