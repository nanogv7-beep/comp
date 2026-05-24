; ==========================================
; CODIGO OBJETO GENERADO POR NOTASCRIPT
; COMPILADOR PARA 8086 (PROTEUS)
; ==========================================

.MODEL SMALL
.STACK 100h
.DATA
    r DW 0  ; Variable o temporal
    JingleParte1 DW 0  ; Variable o temporal
    s DW 0  ; Variable o temporal
    solBajo DW 0  ; Variable o temporal
    JingleParte2 DW 0  ; Variable o temporal
    d DW 0  ; Variable o temporal
    LuzRoja DW 0  ; Variable o temporal
    m DW 0  ; Variable o temporal
    nota_def DW 0  ; Variable o temporal
    LuzVerde DW 0  ; Variable o temporal
    doBajo DW 0  ; Variable o temporal
    msg1 DB "--- INICIANDO SECUENCIA NAVIDEÑA ---", '$'
    msg2 DB "Encendiendo arbolito...", '$'
    msg3 DB " [*] LUZ ROJA ENCENDIDA [*] ", '$'
    msg4 DB " [*] LUZ VERDE ENCENDIDA [*] ", '$'
    msg5 DB " [*] LUZ DORADA DESTELLANDO [*] ", '$'
    msg6 DB "¡Feliz Navidad y Compilacion Exitosa!", '$'

.CODE
INICIO:
    MOV AX, @DATA
    MOV DS, AX

    ; Instrucción: MOSTRAR "--- INICIANDO SECUENCIA NAVIDEÑA ---"  
    LEA DX, msg1
    MOV AH, 09h
    INT 21h

    ; Instrucción: = nota_def  m
    MOV AX, nota_def
    MOV m, AX

    ; Instrucción: = nota_def  s
    MOV AX, nota_def
    MOV s, AX

    ; Instrucción: = nota_def  d
    MOV AX, nota_def
    MOV d, AX

    ; Instrucción: = nota_def  r
    MOV AX, nota_def
    MOV r, AX

    ; Instrucción: = nota_def  doBajo
    MOV AX, nota_def
    MOV doBajo, AX

    ; Instrucción: = nota_def  solBajo
    MOV AX, nota_def
    MOV solBajo, AX

    ; Instrucción: MOSTRAR "Encendiendo arbolito..."  
    LEA DX, msg2
    MOV AH, 09h
    INT 21h

    ; Instrucción: MOSTRAR " [*] LUZ ROJA ENCENDIDA [*] "  
    LEA DX, msg3
    MOV AH, 09h
    INT 21h

    ; Instrucción: TOCAR LuzRoja  
    MOV CX, LuzRoja ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR JingleParte1  
    MOV CX, JingleParte1 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: MOSTRAR " [*] LUZ VERDE ENCENDIDA [*] "  
    LEA DX, msg4
    MOV AH, 09h
    INT 21h

    ; Instrucción: TOCAR LuzVerde  
    MOV CX, LuzVerde ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR JingleParte1  
    MOV CX, JingleParte1 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: MOSTRAR " [*] LUZ DORADA DESTELLANDO [*] "  
    LEA DX, msg5
    MOV AH, 09h
    INT 21h

    ; Instrucción: TOCAR LuzRoja  
    MOV CX, LuzRoja ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR JingleParte2  
    MOV CX, JingleParte2 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: MOSTRAR "¡Feliz Navidad y Compilacion Exitosa!"  
    LEA DX, msg6
    MOV AH, 09h
    INT 21h

    ; SALIR A DOS
    MOV AX, 4C00h
    INT 21h

; ==========================================
; SUBRUTINAS DE HARDWARE Y SONIDO
; ==========================================
RUTINA_SONIDO PROC
    PUSH AX
    PUSH CX
BUCLE_NOTA:
    MOV AL, 1
    OUT 01h, AL  ; Sube voltaje de la bocina
    CALL RETARDO
    MOV AL, 0
    OUT 01h, AL  ; Baja voltaje de la bocina
    CALL RETARDO
    LOOP BUCLE_NOTA ; Repite según la nota en CX
    POP CX
    POP AX
    RET
RUTINA_SONIDO ENDP

RUTINA_SILENCIO PROC
    PUSH CX
    MOV CX, 0FFFFh ; Tiempo largo en silencio
BUCLE_S:
    NOP
    LOOP BUCLE_S
    POP CX
    RET
RUTINA_SILENCIO ENDP

RETARDO PROC
    PUSH CX
    MOV CX, 0FFFh ; Ajustar este valor en Proteus
DELAY_LOOP:
    NOP
    LOOP DELAY_LOOP
    POP CX
    RET
RETARDO ENDP

END INICIO
