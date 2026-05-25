; ==========================================
; CODIGO OBJETO GENERADO POR NOTASCRIPT
; COMPILADOR PARA 8086 (DOSBOX / PC SPEAKER)
; ==========================================

.MODEL SMALL
.STACK 100h
.DATA
    nLa4 DW 0  ; Variable o temporal
    nSi4 DW 0  ; Variable o temporal
    nSol4 DW 0  ; Variable o temporal
    nRe5 DW 0  ; Variable o temporal
    nMi4 DW 0  ; Variable o temporal
    nDo5 DW 0  ; Variable o temporal
    msg1 DB "=== Noche de Paz - Silent Night ===", '$'
    msg2 DB "Feliz Navidad!", '$'

.CODE
INICIO:
    MOV AX, @DATA
    MOV DS, AX

    ; Instrucción: = 392  nSol4
    MOV AX, 392
    MOV nSol4, AX

    ; Instrucción: = 440  nLa4
    MOV AX, 440
    MOV nLa4, AX

    ; Instrucción: = 330  nMi4
    MOV AX, 330
    MOV nMi4, AX

    ; Instrucción: = 494  nSi4
    MOV AX, 494
    MOV nSi4, AX

    ; Instrucción: = 523  nDo5
    MOV AX, 523
    MOV nDo5, AX

    ; Instrucción: = 587  nRe5
    MOV AX, 587
    MOV nRe5, AX

    ; Instrucción: MOSTRAR "=== Noche de Paz - Silent Night ==="  
    LEA DX, msg1
    MOV AH, 09h
    INT 21h

    ; Instrucción: TOCAR nSol4  
    MOV CX, nSol4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nLa4  
    MOV CX, nLa4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nSol4  
    MOV CX, nSol4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nMi4  
    MOV CX, nMi4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nMi4  
    MOV CX, nMi4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nSol4  
    MOV CX, nSol4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nLa4  
    MOV CX, nLa4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nSol4  
    MOV CX, nSol4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nMi4  
    MOV CX, nMi4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nMi4  
    MOV CX, nMi4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nRe5  
    MOV CX, nRe5 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nRe5  
    MOV CX, nRe5 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nSi4  
    MOV CX, nSi4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nDo5  
    MOV CX, nDo5 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nDo5  
    MOV CX, nDo5 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nSol4  
    MOV CX, nSol4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nSi4  
    MOV CX, nSi4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nDo5  
    MOV CX, nDo5 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nSi4  
    MOV CX, nSi4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nSol4  
    MOV CX, nSol4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nRe5  
    MOV CX, nRe5 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nRe5  
    MOV CX, nRe5 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nRe5  
    MOV CX, nRe5 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nSi4  
    MOV CX, nSi4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR nDo5  
    MOV CX, nDo5 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: TOCAR nSol4  
    MOV CX, nSol4 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: MOSTRAR "Feliz Navidad!"  
    LEA DX, msg2
    MOV AH, 09h
    INT 21h

    ; SALIR A DOS
    MOV AX, 4C00h
    INT 21h

; ==========================================
; SUBRUTINAS DE SONIDO PARA PC SPEAKER (DOSBOX)
; ==========================================
RUTINA_SONIDO PROC
    PUSH AX
    PUSH BX
    PUSH CX
    PUSH DX
    ; Verificar frecuencia minima (>= 40 Hz evita overflow en DIV)
    CMP CX, 28h
    JB FIN_SONIDO
    ; Calcular divisor del PIT: 1193180 / CX
    ; 1193180 = 0x001234DC -> DX:AX
    MOV BX, CX
    MOV DX, 0012h
    MOV AX, 34DCh
    DIV BX              ; AX = divisor
    ; Programar PIT canal 2: modo 3 (onda cuadrada), LSB+MSB
    MOV BX, AX
    MOV AL, 0B6h
    OUT 43h, AL
    MOV AL, BL
    OUT 42h, AL         ; Divisor: parte baja
    MOV AL, BH
    OUT 42h, AL         ; Divisor: parte alta
    ; Activar la bocina (puerto 61h bits 0 y 1)
    IN AL, 61h
    OR AL, 03h
    OUT 61h, AL
    ; Esperar duracion de la nota
    CALL RETARDO
    ; Desactivar la bocina
    IN AL, 61h
    AND AL, 0FCh
    OUT 61h, AL
FIN_SONIDO:
    POP DX
    POP CX
    POP BX
    POP AX
    RET
RUTINA_SONIDO ENDP

RUTINA_SILENCIO PROC
    PUSH AX
    ; Asegurarse de que la bocina este apagada
    IN AL, 61h
    AND AL, 0FCh
    OUT 61h, AL
    CALL RETARDO
    POP AX
    RET
RUTINA_SILENCIO ENDP

RETARDO PROC
    PUSH AX
    PUSH CX
    PUSH DX
    MOV AH, 86h         ; BIOS Wait
    MOV CX, 0003h       ; ~250 ms = 250000 us = 0x0003D090h
    MOV DX, 0D090h
    INT 15h
    POP DX
    POP CX
    POP AX
    RET
RETARDO ENDP

END INICIO
