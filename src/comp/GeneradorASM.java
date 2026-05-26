package comp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GeneradorASM {

    public void generarArchivo(ArrayList<Cuarteto> cuadruplos, String rutaSalida) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaSalida))) {
            
            // 1. RECOLECCIÓN DINÁMICA DE VARIABLES (.DATA)
            Set<String> variables = new HashSet<>();
            ArrayList<String> mensajes = new ArrayList<>();
            
            for (Cuarteto c : cuadruplos) {
                // Los resultados de LABEL/GOTO/IF_FALSE/IF_TRUE son etiquetas de código,
                // no variables de datos. Incluirlos en .DATA causaría "duplicate declaration"
                // en emu8086 porque la misma cadena (ej. "L1") aparecería como DW y como label.
                boolean esCtrlFlujo = c.operador.equals("LABEL")
                        || c.operador.equals("GOTO")
                        || c.operador.equals("IF_FALSE")
                        || c.operador.equals("IF_TRUE");

                if (!esCtrlFlujo && esVariable(c.resultado)) variables.add(c.resultado);
                if (esVariable(c.arg1)) variables.add(c.arg1);
                if (esVariable(c.arg2)) variables.add(c.arg2);

                // Si es un mensaje de texto para la instrucción MOSTRAR
                if (c.operador.equals("MOSTRAR") && esCadena(c.arg1)) {
                    mensajes.add(c.arg1);
                }
            }

            // 2. ENCABEZADO DEL ARCHIVO ENSAMBLADOR
            writer.println("; ==========================================");
            writer.println("; CODIGO OBJETO GENERADO POR NOTASCRIPT");
            writer.println("; COMPILADOR PARA 8086 (DOSBOX / PC SPEAKER)");
            writer.println("; ==========================================\n");
            
            writer.println(".MODEL SMALL");
            writer.println(".STACK 100h");
            
            // 3. SEGMENTO DE DATOS
            writer.println(".DATA");
            for (String v : variables) {
                writer.println("    " + v + " DW 0  ; Variable o temporal");
            }
            
            int msgContador = 1;
            for (String msg : mensajes) {
                // Quitamos las comillas y le agregamos el terminador de DOS '$'
                String textoLimpio = msg.replace("\"", "");
                writer.println("    msg" + msgContador + " DB \"" + textoLimpio + "\", '$'");
                msgContador++;
            }

            // Buffer para LEER (INT 21h, AH=0Ah)
            writer.println("    inBufLen DB 31");
            writer.println("    inBufCnt DB 0");
            writer.println("    inBufData DB 31 DUP(0)");

            // 4. SEGMENTO DE CÓDIGO
            writer.println("\n.CODE");
            writer.println("INICIO:");
            writer.println("    MOV AX, @DATA");
            writer.println("    MOV DS, AX\n");

            msgContador = 1;
            int labelCounter = 0;

            // 5. TRADUCCIÓN DE CUÁDRUPLOS
            for (Cuarteto c : cuadruplos) {
                labelCounter++;
                String suf = "_" + labelCounter;
                writer.println("    ; Instrucción: " + c.operador + " " + c.arg1 + " " + c.arg2 + " " + c.resultado);
                
                switch (c.operador) {
                    case "=":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;
                        
                    case "+":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    ADD AX, " + formatear(c.arg2));
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "-":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    SUB AX, " + formatear(c.arg2));
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;
                        
                    case "*":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    MOV BX, " + formatear(c.arg2));
                        writer.println("    MUL BX");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;
                        
                    case "/":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    MOV DX, 0");
                        writer.println("    MOV BX, " + formatear(c.arg2));
                        writer.println("    DIV BX");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "%":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    MOV DX, 0");
                        writer.println("    MOV BX, " + formatear(c.arg2));
                        writer.println("    DIV BX");
                        writer.println("    MOV " + c.resultado + ", DX");
                        break;

                    case "AND":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, 0");
                        writer.println("    JNE AND_A_TRUE" + suf);
                        writer.println("    MOV AX, 0");
                        writer.println("    JMP AND_A_DONE" + suf);
                        writer.println("AND_A_TRUE" + suf + ":");
                        writer.println("    MOV AX, 1");
                        writer.println("AND_A_DONE" + suf + ":");
                        writer.println("    MOV BX, " + formatear(c.arg2));
                        writer.println("    CMP BX, 0");
                        writer.println("    JNE AND_B_TRUE" + suf);
                        writer.println("    MOV BX, 0");
                        writer.println("    JMP AND_B_DONE" + suf);
                        writer.println("AND_B_TRUE" + suf + ":");
                        writer.println("    MOV BX, 1");
                        writer.println("AND_B_DONE" + suf + ":");
                        writer.println("    AND AX, BX");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "OR":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, 0");
                        writer.println("    JNE OR_A_TRUE" + suf);
                        writer.println("    MOV AX, 0");
                        writer.println("    JMP OR_A_DONE" + suf);
                        writer.println("OR_A_TRUE" + suf + ":");
                        writer.println("    MOV AX, 1");
                        writer.println("OR_A_DONE" + suf + ":");
                        writer.println("    MOV BX, " + formatear(c.arg2));
                        writer.println("    CMP BX, 0");
                        writer.println("    JNE OR_B_TRUE" + suf);
                        writer.println("    MOV BX, 0");
                        writer.println("    JMP OR_B_DONE" + suf);
                        writer.println("OR_B_TRUE" + suf + ":");
                        writer.println("    MOV BX, 1");
                        writer.println("OR_B_DONE" + suf + ":");
                        writer.println("    OR AX, BX");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "NOT":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, 0");
                        writer.println("    JE NOT_IS_ZERO" + suf);
                        writer.println("    MOV AX, 0");
                        writer.println("    JMP NOT_DONE" + suf);
                        writer.println("NOT_IS_ZERO" + suf + ":");
                        writer.println("    MOV AX, 1");
                        writer.println("NOT_DONE" + suf + ":");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "==":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, " + formatear(c.arg2));
                        writer.println("    MOV AX, 0");
                        writer.println("    JNE CMP_DONE" + suf);
                        writer.println("    MOV AX, 1");
                        writer.println("CMP_DONE" + suf + ":");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "!=":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, " + formatear(c.arg2));
                        writer.println("    MOV AX, 0");
                        writer.println("    JE CMP_DONE" + suf);
                        writer.println("    MOV AX, 1");
                        writer.println("CMP_DONE" + suf + ":");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "<":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, " + formatear(c.arg2));
                        writer.println("    MOV AX, 0");
                        writer.println("    JGE CMP_DONE" + suf);
                        writer.println("    MOV AX, 1");
                        writer.println("CMP_DONE" + suf + ":");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case ">":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, " + formatear(c.arg2));
                        writer.println("    MOV AX, 0");
                        writer.println("    JLE CMP_DONE" + suf);
                        writer.println("    MOV AX, 1");
                        writer.println("CMP_DONE" + suf + ":");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "<=":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, " + formatear(c.arg2));
                        writer.println("    MOV AX, 0");
                        writer.println("    JG CMP_DONE" + suf);
                        writer.println("    MOV AX, 1");
                        writer.println("CMP_DONE" + suf + ":");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case ">=":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, " + formatear(c.arg2));
                        writer.println("    MOV AX, 0");
                        writer.println("    JL CMP_DONE" + suf);
                        writer.println("    MOV AX, 1");
                        writer.println("CMP_DONE" + suf + ":");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "LEER":
                        writer.println("    LEA DX, inBufLen");
                        writer.println("    MOV AH, 0Ah");
                        writer.println("    INT 21h");
                        writer.println("    LEA SI, inBufData");
                        writer.println("    MOV CL, inBufCnt");
                        writer.println("    XOR CH, CH");
                        writer.println("    XOR AX, AX");
                        writer.println("LEER_LOOP" + suf + ":");
                        writer.println("    CMP CX, 0");
                        writer.println("    JE LEER_DONE" + suf);
                        writer.println("    MOV BX, AX");
                        writer.println("    SHL AX, 1");
                        writer.println("    SHL BX, 1");
                        writer.println("    SHL BX, 1");
                        writer.println("    SHL BX, 1");
                        writer.println("    ADD AX, BX");
                        writer.println("    MOV BL, [SI]");
                        writer.println("    SUB BL, '0'");
                        writer.println("    XOR BH, BH");
                        writer.println("    ADD AX, BX");
                        writer.println("    INC SI");
                        writer.println("    DEC CX");
                        writer.println("    JMP LEER_LOOP" + suf);
                        writer.println("LEER_DONE" + suf + ":");
                        writer.println("    MOV " + c.resultado + ", AX");
                        break;

                    case "MOSTRAR":
                        if (esCadena(c.arg1)) {
                            writer.println("    LEA DX, msg" + msgContador);
                            writer.println("    MOV AH, 09h");
                            writer.println("    INT 21h");
                            msgContador++;
                        } else if (c.arg1 != null && !c.arg1.isEmpty()) {
                            // Imprimir variable o literal numérico
                            writer.println("    MOV AX, " + formatear(c.arg1));
                            writer.println("    CALL IMPRIMIR_NUM");
                        }
                        break;

                    case "TOCAR":
                        // Pasamos el argumento a CX para que la subrutina sepa qué nota tocar
                        writer.println("    MOV CX, " + formatear(c.arg1) + " ; Frecuencia de la nota");
                        writer.println("    CALL RUTINA_SONIDO");
                        break;

                    case "SILENCIO":
                        writer.println("    CALL RUTINA_SILENCIO");
                        break;

                    // Soporte para futuros cuádruplos de control de flujo
                    case "LABEL":
                        writer.println(c.resultado + ":");
                        break;

                    case "GOTO":
                        writer.println("    JMP " + c.resultado);
                        break;

                    case "IF_FALSE":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, 0");
                        writer.println("    JE " + c.resultado);
                        break;

                    case "IF_TRUE":
                        writer.println("    MOV AX, " + formatear(c.arg1));
                        writer.println("    CMP AX, 0");
                        writer.println("    JNE " + c.resultado);
                        break;

                    case "ROMPER":
                    case "CONTINUAR":
                        // El parser ya traduce estos a GOTO; este caso es fallback defensivo
                        break;
                }
                writer.println(""); // Espacio para que el ASM sea legible
            }

            // 6. FIN DEL PROGRAMA
            writer.println("    ; SALIR A DOS");
            writer.println("    MOV AX, 4C00h");
            writer.println("    INT 21h\n");

            // 7. RUTINAS DE SONIDO PARA PC SPEAKER (DOSBox)
            // El PIT 8253/8254 canal 2 controla la bocina del PC.
            // Divisor = 1193180 / frecuencia_Hz
            // Puerto 43h: byte de control del PIT
            // Puerto 42h: divisor (LSB luego MSB)
            // Puerto 61h: bit0=gate del canal 2, bit1=habilitar bocina
            writer.println("; ==========================================");
            writer.println("; SUBRUTINAS DE SONIDO PARA PC SPEAKER (DOSBOX)");
            writer.println("; ==========================================");

            // --- RUTINA_SONIDO ---
            writer.println("RUTINA_SONIDO PROC");
            writer.println("    PUSH AX");
            writer.println("    PUSH BX");
            writer.println("    PUSH CX");
            writer.println("    PUSH DX");
            writer.println("    ; Verificar frecuencia minima (>= 40 Hz evita overflow en DIV)");
            writer.println("    CMP CX, 28h");
            writer.println("    JB FIN_SONIDO");
            writer.println("    ; Calcular divisor del PIT: 1193180 / CX");
            writer.println("    ; 1193180 = 0x001234DC -> DX:AX");
            writer.println("    MOV BX, CX");
            writer.println("    MOV DX, 0012h");
            writer.println("    MOV AX, 34DCh");
            writer.println("    DIV BX              ; AX = divisor");
            writer.println("    ; Programar PIT canal 2: modo 3 (onda cuadrada), LSB+MSB");
            writer.println("    MOV BX, AX");
            writer.println("    MOV AL, 0B6h");
            writer.println("    OUT 43h, AL");
            writer.println("    MOV AL, BL");
            writer.println("    OUT 42h, AL         ; Divisor: parte baja");
            writer.println("    MOV AL, BH");
            writer.println("    OUT 42h, AL         ; Divisor: parte alta");
            writer.println("    ; Activar la bocina (puerto 61h bits 0 y 1)");
            writer.println("    IN AL, 61h");
            writer.println("    OR AL, 03h");
            writer.println("    OUT 61h, AL");
            writer.println("    ; Esperar duracion de la nota");
            writer.println("    CALL RETARDO");
            writer.println("    ; Desactivar la bocina");
            writer.println("    IN AL, 61h");
            writer.println("    AND AL, 0FCh");
            writer.println("    OUT 61h, AL");
            writer.println("FIN_SONIDO:");
            writer.println("    POP DX");
            writer.println("    POP CX");
            writer.println("    POP BX");
            writer.println("    POP AX");
            writer.println("    RET");
            writer.println("RUTINA_SONIDO ENDP\n");

            // --- RUTINA_SILENCIO ---
            writer.println("RUTINA_SILENCIO PROC");
            writer.println("    PUSH AX");
            writer.println("    ; Asegurarse de que la bocina este apagada");
            writer.println("    IN AL, 61h");
            writer.println("    AND AL, 0FCh");
            writer.println("    OUT 61h, AL");
            writer.println("    CALL RETARDO");
            writer.println("    POP AX");
            writer.println("    RET");
            writer.println("RUTINA_SILENCIO ENDP\n");

            // --- RETARDO (~250 ms via BIOS INT 15h AH=86h) ---
            // CX:DX = microsegundos; 250000 us = 0x0003D090h
            writer.println("RETARDO PROC");
            writer.println("    PUSH AX");
            writer.println("    PUSH CX");
            writer.println("    PUSH DX");
            writer.println("    MOV AH, 86h         ; BIOS Wait");
            writer.println("    MOV CX, 0003h       ; ~250 ms = 250000 us = 0x0003D090h");
            writer.println("    MOV DX, 0D090h");
            writer.println("    INT 15h");
            writer.println("    POP DX");
            writer.println("    POP CX");
            writer.println("    POP AX");
            writer.println("    RET");
            writer.println("RETARDO ENDP\n");

            // --- IMPRIMIR_NUM: imprime AX como decimal con salto de línea ---
            writer.println("IMPRIMIR_NUM PROC");
            writer.println("    PUSH AX");
            writer.println("    PUSH BX");
            writer.println("    PUSH CX");
            writer.println("    PUSH DX");
            writer.println("    MOV BX, 10");
            writer.println("    MOV CX, 0");
            writer.println("    TEST AX, AX");
            writer.println("    JNS IMPNUM_POS");
            writer.println("    PUSH AX");
            writer.println("    MOV DL, '-'");
            writer.println("    MOV AH, 02h");
            writer.println("    INT 21h");
            writer.println("    POP AX");
            writer.println("    NEG AX");
            writer.println("IMPNUM_POS:");
            writer.println("    CMP AX, 0");
            writer.println("    JNZ IMPNUM_EXTRAE");
            writer.println("    PUSH AX");
            writer.println("    INC CX");
            writer.println("    JMP IMPNUM_IMPRIME");
            writer.println("IMPNUM_EXTRAE:");
            writer.println("    CMP AX, 0");
            writer.println("    JZ IMPNUM_IMPRIME");
            writer.println("    XOR DX, DX");
            writer.println("    DIV BX");
            writer.println("    PUSH DX");
            writer.println("    INC CX");
            writer.println("    JMP IMPNUM_EXTRAE");
            writer.println("IMPNUM_IMPRIME:");
            writer.println("    CMP CX, 0");
            writer.println("    JZ IMPNUM_FIN");
            writer.println("    POP DX");
            writer.println("    ADD DL, '0'");
            writer.println("    MOV AH, 02h");
            writer.println("    INT 21h");
            writer.println("    DEC CX");
            writer.println("    JMP IMPNUM_IMPRIME");
            writer.println("IMPNUM_FIN:");
            writer.println("    MOV DL, 13");
            writer.println("    MOV AH, 02h");
            writer.println("    INT 21h");
            writer.println("    MOV DL, 10");
            writer.println("    MOV AH, 02h");
            writer.println("    INT 21h");
            writer.println("    POP DX");
            writer.println("    POP CX");
            writer.println("    POP BX");
            writer.println("    POP AX");
            writer.println("    RET");
            writer.println("IMPRIMIR_NUM ENDP\n");

            writer.println("END INICIO");

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo ASM: " + e.getMessage());
        }
    }

    // --- MÉTODOS AUXILIARES ---
    private boolean esVariable(String str) {
        return str != null
                && !str.isEmpty()
                && !esNumero(str)
                && !esCadena(str)
                && !esBooleanoLiteral(str)
                && !esNotaLiteral(str)
                && !str.equals("OR")
                && !str.equals("AND")
                && !str.equals("NOT")
                && !str.equals("==")
                && !str.equals("!=")
                && !str.equals(">")
                && !str.equals("<")
                && !str.equals(">=")
                && !str.equals("<=");
    }

    private boolean esNumero(String str) {
        try { Double.parseDouble(str); return true; } catch (Exception e) { return false; }
    }
    
    private boolean esCadena(String str) {
        return str != null && str.startsWith("\"") && str.endsWith("\"");
    }
    
    private String formatear(String arg) {
        if (arg == null) return "0";

        if (esBooleanoLiteral(arg)) {
            return arg.equalsIgnoreCase("true") ? "1" : "0";
        }

        if (esNotaLiteral(arg)) {
            return String.valueOf(notaLiteralAFrecuencia(arg));
        }

        if (esNumero(arg)) {
            // Convierte decimales (ej. 50.0) a enteros (50) para que el ensamblador no tire error
            try {
                double val = Double.parseDouble(arg);
                return String.valueOf((int)val);
            } catch(Exception e) { return arg; }
        }
        return arg;
    }

    private boolean esBooleanoLiteral(String str) {
        return "true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str);
    }

    private boolean esNotaLiteral(String str) {
        return str != null && str.matches("(?i)^(do|re|mi|fa|sol|la|si)([#b]?[1-8]?)$");
    }

    private int notaLiteralAFrecuencia(String nota) {
        String n = nota.toLowerCase();
        int octava = 4;

        char last = n.charAt(n.length() - 1);
        if (Character.isDigit(last)) {
            octava = Character.getNumericValue(last);
            n = n.substring(0, n.length() - 1);
        }

        int semitono;
        String resto;
        if (n.startsWith("sol")) {
            semitono = 7;
            resto = n.substring(3);
            if (resto.contains("#")) semitono = 8;
            else if (resto.contains("b")) semitono = 6;
        } else if (n.startsWith("si")) {
            semitono = 11;
            resto = n.substring(2);
            if (resto.contains("#")) { semitono = 0; octava++; }
            else if (resto.contains("b")) semitono = 10;
        } else if (n.startsWith("do")) {
            semitono = 0;
            resto = n.substring(2);
            if (resto.contains("#")) semitono = 1;
            else if (resto.contains("b")) { semitono = 11; octava--; }
        } else if (n.startsWith("re")) {
            semitono = 2;
            resto = n.substring(2);
            if (resto.contains("#")) semitono = 3;
            else if (resto.contains("b")) semitono = 1;
        } else if (n.startsWith("mi")) {
            semitono = 4;
            resto = n.substring(2);
            if (resto.contains("#")) semitono = 5;
            else if (resto.contains("b")) semitono = 3;
        } else if (n.startsWith("fa")) {
            semitono = 5;
            resto = n.substring(2);
            if (resto.contains("#")) semitono = 6;
            else if (resto.contains("b")) semitono = 4;
        } else if (n.startsWith("la")) {
            semitono = 9;
            resto = n.substring(2);
            if (resto.contains("#")) semitono = 10;
            else if (resto.contains("b")) semitono = 8;
        } else {
            semitono = 0;
        }

        int midiNote = 12 * (octava + 1) + semitono;
        double freq = 440.0 * Math.pow(2.0, (midiNote - 69.0) / 12.0);
        int freqInt = (int) Math.round(freq);
        return Math.max(40, Math.min(freqInt, 32000));
    }
}