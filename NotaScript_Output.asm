; ==========================================
; CODIGO OBJETO GENERADO POR NOTASCRIPT
; COMPILADOR PARA 8086 (DOSBOX / PC SPEAKER)
; ==========================================

.MODEL SMALL
.STACK 100h
.DATA
    n_do6 DW 0  ; Variable o temporal
    t5 DW 0  ; Variable o temporal
    t6 DW 0  ; Variable o temporal
    t7 DW 0  ; Variable o temporal
    t8 DW 0  ; Variable o temporal
    n_sol DW 0  ; Variable o temporal
    t9 DW 0  ; Variable o temporal
    n_do DW 0  ; Variable o temporal
    t10 DW 0  ; Variable o temporal
    t12 DW 0  ; Variable o temporal
    t11 DW 0  ; Variable o temporal
    t14 DW 0  ; Variable o temporal
    t13 DW 0  ; Variable o temporal
    t16 DW 0  ; Variable o temporal
    t15 DW 0  ; Variable o temporal
    t17 DW 0  ; Variable o temporal
    t19 DW 0  ; Variable o temporal
    ctr DW 0  ; Variable o temporal
    a DW 0  ; Variable o temporal
    b DW 0  ; Variable o temporal
    resultado DW 0  ; Variable o temporal
    i DW 0  ; Variable o temporal
    t21 DW 0  ; Variable o temporal
    t23 DW 0  ; Variable o temporal
    t22 DW 0  ; Variable o temporal
    t25 DW 0  ; Variable o temporal
    t24 DW 0  ; Variable o temporal
    nivel DW 0  ; Variable o temporal
    n_mi DW 0  ; Variable o temporal
    activo DW 0  ; Variable o temporal
    msg1 DB "=== NOTASCRIPT: TEST COMPLETO ===", '$'
    msg2 DB "a + b = ", '$'
    msg3 DB "a - b = ", '$'
    msg4 DB "a * b = ", '$'
    msg5 DB "a mod b = ", '$'
    msg6 DB "--- TEST si_/sino ---", '$'
    msg7 DB "a es mayor que b", '$'
    msg8 DB "a no es mayor que b", '$'
    msg9 DB "a vale exactamente 10", '$'
    msg10 DB "--- TEST logica ---", '$'
    msg11 DB "AND: ambas condiciones ciertas", '$'
    msg12 DB "OR: al menos una condicion cierta", '$'
    msg13 DB "activo es falso", '$'
    msg14 DB "NOT correcto: activo es verdadero", '$'
    msg15 DB "--- TEST notas ---", '$'
    msg16 DB "--- TEST repetir (3 veces) ---", '$'
    msg17 DB "--- TEST mientras con continuar y romper ---", '$'
    msg18 DB "ctr al salir del while:", '$'
    msg19 DB "--- TEST segun ---", '$'
    msg20 DB "Nivel 1 - DO", '$'
    msg21 DB "Nivel 2 - MI", '$'
    msg22 DB "Nivel 3 - SOL", '$'
    msg23 DB "Nivel desconocido", '$'
    msg24 DB "--- MELODIA FINAL (2 repeticiones) ---", '$'
    msg25 DB "=== FIN DEL TEST ===", '$'
    inBufLen DB 31
    inBufCnt DB 0
    inBufData DB 31 DUP(0)

.CODE
INICIO:
    MOV AX, @DATA
    MOV DS, AX

    ; Instrucción: MOSTRAR "=== NOTASCRIPT: TEST COMPLETO ==="  
    LEA DX, msg1
    MOV AH, 09h
    INT 21h

    ; Instrucción: = 10  a
    MOV AX, 10
    MOV a, AX

    ; Instrucción: = 3  b
    MOV AX, 3
    MOV b, AX

    ; Instrucción: = 13  resultado
    MOV AX, 13
    MOV resultado, AX

    ; Instrucción: MOSTRAR "a + b = "  
    LEA DX, msg2
    MOV AH, 09h
    INT 21h

    ; Instrucción: MOSTRAR resultado  
    MOV AX, resultado
    CALL IMPRIMIR_NUM

    ; Instrucción: = 7  resultado
    MOV AX, 7
    MOV resultado, AX

    ; Instrucción: MOSTRAR "a - b = "  
    LEA DX, msg3
    MOV AH, 09h
    INT 21h

    ; Instrucción: MOSTRAR resultado  
    MOV AX, resultado
    CALL IMPRIMIR_NUM

    ; Instrucción: = 30  resultado
    MOV AX, 30
    MOV resultado, AX

    ; Instrucción: MOSTRAR "a * b = "  
    LEA DX, msg4
    MOV AH, 09h
    INT 21h

    ; Instrucción: MOSTRAR resultado  
    MOV AX, resultado
    CALL IMPRIMIR_NUM

    ; Instrucción: = 1  resultado
    MOV AX, 1
    MOV resultado, AX

    ; Instrucción: MOSTRAR "a mod b = "  
    LEA DX, msg5
    MOV AH, 09h
    INT 21h

    ; Instrucción: MOSTRAR resultado  
    MOV AX, resultado
    CALL IMPRIMIR_NUM

    ; Instrucción: MOSTRAR "--- TEST si_/sino ---"  
    LEA DX, msg6
    MOV AH, 09h
    INT 21h

    ; Instrucción: = 1  t5
    MOV AX, 1
    MOV t5, AX

    ; Instrucción: IF_FALSE t5  L1
    MOV AX, t5
    CMP AX, 0
    JE L1

    ; Instrucción: MOSTRAR "a es mayor que b"  
    LEA DX, msg7
    MOV AH, 09h
    INT 21h

    ; Instrucción: GOTO   L2
    JMP L2

    ; Instrucción: LABEL   L1
L1:

    ; Instrucción: MOSTRAR "a no es mayor que b"  
    LEA DX, msg8
    MOV AH, 09h
    INT 21h

    ; Instrucción: LABEL   L2
L2:

    ; Instrucción: = 1  t6
    MOV AX, 1
    MOV t6, AX

    ; Instrucción: IF_FALSE t6  L3
    MOV AX, t6
    CMP AX, 0
    JE L3

    ; Instrucción: MOSTRAR "a vale exactamente 10"  
    LEA DX, msg9
    MOV AH, 09h
    INT 21h

    ; Instrucción: GOTO   L4
    JMP L4

    ; Instrucción: LABEL   L3
L3:

    ; Instrucción: LABEL   L4
L4:

    ; Instrucción: MOSTRAR "--- TEST logica ---"  
    LEA DX, msg10
    MOV AH, 09h
    INT 21h

    ; Instrucción: = 1  t7
    MOV AX, 1
    MOV t7, AX

    ; Instrucción: = 1  t8
    MOV AX, 1
    MOV t8, AX

    ; Instrucción: AND t7 t8 t9
    MOV AX, t7
    CMP AX, 0
    JNE AND_A_TRUE_33
    MOV AX, 0
    JMP AND_A_DONE_33
AND_A_TRUE_33:
    MOV AX, 1
AND_A_DONE_33:
    MOV BX, t8
    CMP BX, 0
    JNE AND_B_TRUE_33
    MOV BX, 0
    JMP AND_B_DONE_33
AND_B_TRUE_33:
    MOV BX, 1
AND_B_DONE_33:
    AND AX, BX
    MOV t9, AX

    ; Instrucción: IF_FALSE t9  L5
    MOV AX, t9
    CMP AX, 0
    JE L5

    ; Instrucción: MOSTRAR "AND: ambas condiciones ciertas"  
    LEA DX, msg11
    MOV AH, 09h
    INT 21h

    ; Instrucción: GOTO   L6
    JMP L6

    ; Instrucción: LABEL   L5
L5:

    ; Instrucción: LABEL   L6
L6:

    ; Instrucción: = 0  t10
    MOV AX, 0
    MOV t10, AX

    ; Instrucción: = 1  t11
    MOV AX, 1
    MOV t11, AX

    ; Instrucción: OR t10 t11 t12
    MOV AX, t10
    CMP AX, 0
    JNE OR_A_TRUE_41
    MOV AX, 0
    JMP OR_A_DONE_41
OR_A_TRUE_41:
    MOV AX, 1
OR_A_DONE_41:
    MOV BX, t11
    CMP BX, 0
    JNE OR_B_TRUE_41
    MOV BX, 0
    JMP OR_B_DONE_41
OR_B_TRUE_41:
    MOV BX, 1
OR_B_DONE_41:
    OR AX, BX
    MOV t12, AX

    ; Instrucción: IF_FALSE t12  L7
    MOV AX, t12
    CMP AX, 0
    JE L7

    ; Instrucción: MOSTRAR "OR: al menos una condicion cierta"  
    LEA DX, msg12
    MOV AH, 09h
    INT 21h

    ; Instrucción: GOTO   L8
    JMP L8

    ; Instrucción: LABEL   L7
L7:

    ; Instrucción: LABEL   L8
L8:

    ; Instrucción: = true  activo
    MOV AX, 1
    MOV activo, AX

    ; Instrucción: = 0  t13
    MOV AX, 0
    MOV t13, AX

    ; Instrucción: IF_FALSE t13  L9
    MOV AX, t13
    CMP AX, 0
    JE L9

    ; Instrucción: MOSTRAR "activo es falso"  
    LEA DX, msg13
    MOV AH, 09h
    INT 21h

    ; Instrucción: GOTO   L10
    JMP L10

    ; Instrucción: LABEL   L9
L9:

    ; Instrucción: MOSTRAR "NOT correcto: activo es verdadero"  
    LEA DX, msg14
    MOV AH, 09h
    INT 21h

    ; Instrucción: LABEL   L10
L10:

    ; Instrucción: MOSTRAR "--- TEST notas ---"  
    LEA DX, msg15
    MOV AH, 09h
    INT 21h

    ; Instrucción: = 523  n_do
    MOV AX, 523
    MOV n_do, AX

    ; Instrucción: = 659  n_mi
    MOV AX, 659
    MOV n_mi, AX

    ; Instrucción: = 784  n_sol
    MOV AX, 784
    MOV n_sol, AX

    ; Instrucción: = 1047  n_do6
    MOV AX, 1047
    MOV n_do6, AX

    ; Instrucción: TOCAR 523  
    MOV CX, 523 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR 659  
    MOV CX, 659 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR 784  
    MOV CX, 784 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: MOSTRAR "--- TEST repetir (3 veces) ---"  
    LEA DX, msg16
    MOV AH, 09h
    INT 21h

    ; Instrucción: = 3  t14
    MOV AX, 3
    MOV t14, AX

    ; Instrucción: LABEL   L11
L11:

    ; Instrucción: > t14 0 t15
    MOV AX, t14
    CMP AX, 0
    MOV AX, 0
    JLE CMP_DONE_67
    MOV AX, 1
CMP_DONE_67:
    MOV t15, AX

    ; Instrucción: IF_FALSE t15  L12
    MOV AX, t15
    CMP AX, 0
    JE L12

    ; Instrucción: TOCAR 523  
    MOV CX, 523 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: - t14 1 t14
    MOV AX, t14
    SUB AX, 1
    MOV t14, AX

    ; Instrucción: GOTO   L11
    JMP L11

    ; Instrucción: LABEL   L12
L12:

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: MOSTRAR "--- TEST mientras con continuar y romper ---"  
    LEA DX, msg17
    MOV AH, 09h
    INT 21h

    ; Instrucción: = 1  ctr
    MOV AX, 1
    MOV ctr, AX

    ; Instrucción: LABEL   L13
L13:

    ; Instrucción: <= ctr 6 t16
    MOV AX, ctr
    CMP AX, 6
    MOV AX, 0
    JG CMP_DONE_77
    MOV AX, 1
CMP_DONE_77:
    MOV t16, AX

    ; Instrucción: IF_FALSE t16  L14
    MOV AX, t16
    CMP AX, 0
    JE L14

    ; Instrucción: == ctr 3 t17
    MOV AX, ctr
    CMP AX, 3
    MOV AX, 0
    JNE CMP_DONE_79
    MOV AX, 1
CMP_DONE_79:
    MOV t17, AX

    ; Instrucción: IF_FALSE t17  L15
    MOV AX, t17
    CMP AX, 0
    JE L15

    ; Instrucción: + ctr 1 ctr
    MOV AX, ctr
    ADD AX, 1
    MOV ctr, AX

    ; Instrucción: GOTO   L13
    JMP L13

    ; Instrucción: GOTO   L16
    JMP L16

    ; Instrucción: LABEL   L15
L15:

    ; Instrucción: LABEL   L16
L16:

    ; Instrucción: == ctr 5 t19
    MOV AX, ctr
    CMP AX, 5
    MOV AX, 0
    JNE CMP_DONE_86
    MOV AX, 1
CMP_DONE_86:
    MOV t19, AX

    ; Instrucción: IF_FALSE t19  L17
    MOV AX, t19
    CMP AX, 0
    JE L17

    ; Instrucción: GOTO   L14
    JMP L14

    ; Instrucción: GOTO   L18
    JMP L18

    ; Instrucción: LABEL   L17
L17:

    ; Instrucción: LABEL   L18
L18:

    ; Instrucción: TOCAR 659  
    MOV CX, 659 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: + ctr 1 ctr
    MOV AX, ctr
    ADD AX, 1
    MOV ctr, AX

    ; Instrucción: GOTO   L13
    JMP L13

    ; Instrucción: LABEL   L14
L14:

    ; Instrucción: MOSTRAR "ctr al salir del while:"  
    LEA DX, msg18
    MOV AH, 09h
    INT 21h

    ; Instrucción: MOSTRAR ctr  
    MOV AX, ctr
    CALL IMPRIMIR_NUM

    ; Instrucción: MOSTRAR "--- TEST segun ---"  
    LEA DX, msg19
    MOV AH, 09h
    INT 21h

    ; Instrucción: = 2  nivel
    MOV AX, 2
    MOV nivel, AX

    ; Instrucción: = 2  t21
    MOV AX, 2
    MOV t21, AX

    ; Instrucción: == t21 1 t22
    MOV AX, t21
    CMP AX, 1
    MOV AX, 0
    JNE CMP_DONE_101
    MOV AX, 1
CMP_DONE_101:
    MOV t22, AX

    ; Instrucción: IF_FALSE t22  L20
    MOV AX, t22
    CMP AX, 0
    JE L20

    ; Instrucción: MOSTRAR "Nivel 1 - DO"  
    LEA DX, msg20
    MOV AH, 09h
    INT 21h

    ; Instrucción: TOCAR 523  
    MOV CX, 523 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: GOTO   L19
    JMP L19

    ; Instrucción: LABEL   L20
L20:

    ; Instrucción: == t21 2 t23
    MOV AX, t21
    CMP AX, 2
    MOV AX, 0
    JNE CMP_DONE_107
    MOV AX, 1
CMP_DONE_107:
    MOV t23, AX

    ; Instrucción: IF_FALSE t23  L21
    MOV AX, t23
    CMP AX, 0
    JE L21

    ; Instrucción: MOSTRAR "Nivel 2 - MI"  
    LEA DX, msg21
    MOV AH, 09h
    INT 21h

    ; Instrucción: TOCAR 659  
    MOV CX, 659 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: GOTO   L19
    JMP L19

    ; Instrucción: LABEL   L21
L21:

    ; Instrucción: == t21 3 t24
    MOV AX, t21
    CMP AX, 3
    MOV AX, 0
    JNE CMP_DONE_113
    MOV AX, 1
CMP_DONE_113:
    MOV t24, AX

    ; Instrucción: IF_FALSE t24  L22
    MOV AX, t24
    CMP AX, 0
    JE L22

    ; Instrucción: MOSTRAR "Nivel 3 - SOL"  
    LEA DX, msg22
    MOV AH, 09h
    INT 21h

    ; Instrucción: TOCAR 784  
    MOV CX, 784 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: GOTO   L19
    JMP L19

    ; Instrucción: LABEL   L22
L22:

    ; Instrucción: MOSTRAR "Nivel desconocido"  
    LEA DX, msg23
    MOV AH, 09h
    INT 21h

    ; Instrucción: TOCAR 1047  
    MOV CX, 1047 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: GOTO   L19
    JMP L19

    ; Instrucción: LABEL   L19
L19:

    ; Instrucción: MOSTRAR "--- MELODIA FINAL (2 repeticiones) ---"  
    LEA DX, msg24
    MOV AH, 09h
    INT 21h

    ; Instrucción: = 0  i
    MOV AX, 0
    MOV i, AX

    ; Instrucción: LABEL   L23
L23:

    ; Instrucción: < i 2 t25
    MOV AX, i
    CMP AX, 2
    MOV AX, 0
    JGE CMP_DONE_126
    MOV AX, 1
CMP_DONE_126:
    MOV t25, AX

    ; Instrucción: IF_FALSE t25  L24
    MOV AX, t25
    CMP AX, 0
    JE L24

    ; Instrucción: TOCAR 523  
    MOV CX, 523 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR 659  
    MOV CX, 659 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR 784  
    MOV CX, 784 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: TOCAR 1047  
    MOV CX, 1047 ; Frecuencia de la nota
    CALL RUTINA_SONIDO

    ; Instrucción: + i 1 i
    MOV AX, i
    ADD AX, 1
    MOV i, AX

    ; Instrucción: GOTO   L23
    JMP L23

    ; Instrucción: LABEL   L24
L24:

    ; Instrucción: SILENCIO   
    CALL RUTINA_SILENCIO

    ; Instrucción: MOSTRAR "=== FIN DEL TEST ==="  
    LEA DX, msg25
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

IMPRIMIR_NUM PROC
    PUSH AX
    PUSH BX
    PUSH CX
    PUSH DX
    MOV BX, 10
    MOV CX, 0
    TEST AX, AX
    JNS IMPNUM_POS
    PUSH AX
    MOV DL, '-'
    MOV AH, 02h
    INT 21h
    POP AX
    NEG AX
IMPNUM_POS:
    CMP AX, 0
    JNZ IMPNUM_EXTRAE
    PUSH AX
    INC CX
    JMP IMPNUM_IMPRIME
IMPNUM_EXTRAE:
    CMP AX, 0
    JZ IMPNUM_IMPRIME
    XOR DX, DX
    DIV BX
    PUSH DX
    INC CX
    JMP IMPNUM_EXTRAE
IMPNUM_IMPRIME:
    CMP CX, 0
    JZ IMPNUM_FIN
    POP DX
    ADD DL, '0'
    MOV AH, 02h
    INT 21h
    DEC CX
    JMP IMPNUM_IMPRIME
IMPNUM_FIN:
    MOV DL, 13
    MOV AH, 02h
    INT 21h
    MOV DL, 10
    MOV AH, 02h
    INT 21h
    POP DX
    POP CX
    POP BX
    POP AX
    RET
IMPRIMIR_NUM ENDP

END INICIO
