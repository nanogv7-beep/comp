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
                // Si el resultado es una variable o temporal, la guardamos
                if (esVariable(c.resultado)) variables.add(c.resultado);
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
            writer.println("; COMPILADOR PARA 8086 (PROTEUS)");
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

            // 4. SEGMENTO DE CÓDIGO
            writer.println("\n.CODE");
            writer.println("INICIO:");
            writer.println("    MOV AX, @DATA");
            writer.println("    MOV DS, AX\n");

            msgContador = 1;

            // 5. TRADUCCIÓN DE CUÁDRUPLOS
            for (Cuarteto c : cuadruplos) {
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

                    case "MOSTRAR":
                        if (esCadena(c.arg1)) {
                            writer.println("    LEA DX, msg" + msgContador);
                            writer.println("    MOV AH, 09h");
                            writer.println("    INT 21h");
                            msgContador++;
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
                }
                writer.println(""); // Espacio para que el ASM sea legible
            }

            // 6. FIN DEL PROGRAMA
            writer.println("    ; SALIR A DOS");
            writer.println("    MOV AX, 4C00h");
            writer.println("    INT 21h\n");

            // 7. RUTINAS DE HARDWARE (PUERTO 01H PARA LA BOCINA)
            writer.println("; ==========================================");
            writer.println("; SUBRUTINAS DE HARDWARE Y SONIDO");
            writer.println("; ==========================================");
            
            writer.println("RUTINA_SONIDO PROC");
            writer.println("    PUSH AX");
            writer.println("    PUSH CX");
            writer.println("BUCLE_NOTA:");
            writer.println("    MOV AL, 1");
            writer.println("    OUT 01h, AL  ; Sube voltaje de la bocina");
            writer.println("    CALL RETARDO");
            writer.println("    MOV AL, 0");
            writer.println("    OUT 01h, AL  ; Baja voltaje de la bocina");
            writer.println("    CALL RETARDO");
            writer.println("    LOOP BUCLE_NOTA ; Repite según la nota en CX");
            writer.println("    POP CX");
            writer.println("    POP AX");
            writer.println("    RET");
            writer.println("RUTINA_SONIDO ENDP\n");

            writer.println("RUTINA_SILENCIO PROC");
            writer.println("    PUSH CX");
            writer.println("    MOV CX, 0FFFFh ; Tiempo largo en silencio");
            writer.println("BUCLE_S:");
            writer.println("    NOP");
            writer.println("    LOOP BUCLE_S");
            writer.println("    POP CX");
            writer.println("    RET");
            writer.println("RUTINA_SILENCIO ENDP\n");
            
            writer.println("RETARDO PROC");
            writer.println("    PUSH CX");
            writer.println("    MOV CX, 0FFFh ; Ajustar este valor en Proteus");
            writer.println("DELAY_LOOP:");
            writer.println("    NOP");
            writer.println("    LOOP DELAY_LOOP");
            writer.println("    POP CX");
            writer.println("    RET");
            writer.println("RETARDO ENDP\n");

            writer.println("END INICIO");

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo ASM: " + e.getMessage());
        }
    }

    // --- MÉTODOS AUXILIARES ---
    private boolean esVariable(String str) {
        return str != null && !str.isEmpty() && !esNumero(str) && !esCadena(str) && !str.equals("OR") && !str.equals("AND") && !str.equals("NOT") && !str.equals("==") && !str.equals("!=") && !str.equals(">") && !str.equals("<") && !str.equals(">=") && !str.equals("<=");
    }

    private boolean esNumero(String str) {
        try { Double.parseDouble(str); return true; } catch (Exception e) { return false; }
    }
    
    private boolean esCadena(String str) {
        return str != null && str.startsWith("\"") && str.endsWith("\"");
    }
    
    private String formatear(String arg) {
        if (esNumero(arg)) {
            // Convierte decimales (ej. 50.0) a enteros (50) para que el ensamblador no tire error
            try {
                double val = Double.parseDouble(arg);
                return String.valueOf((int)val);
            } catch(Exception e) { return arg; }
        }
        return arg;
    }
}