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