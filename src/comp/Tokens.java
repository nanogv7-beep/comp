/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package comp;

/**
 *
 * @author gdlup
 */
public enum Tokens {
    CADENA, 
    NOTE,
    EOF,
    Identificador,
    Numero,
    NumeroDecimal,
    Igual,
    Coma,
    PuntoComa,
    LlaveAper,
    LlaveCierre,
    ParenAper,
    ParenCierre,
    CorcheteAper,    // [
    CorcheteCierre,  // ]
    
    Suma,
    Resta,
    Multiplicacion,
    Division,
    Modulo,
    
    /* Operadores lógicos */
    opAnd,     // yy
    opOr,      // ||
    opNot,     // !
    
    opRel_ig,
    opRel_dif,
    opRel_menIgual,
    opRel_mayIgual,
    opRel_men,
    opRel_may,
    
    DosPuntos,
    
    ERROR,
    KW_NOTA,
    KW_ACORDE,
    KW_MELODIA,
    KW_TIEMPO,
    KW_OCTAVA,
    KW_TOCAR,
    KW_SILENCIO,
    KW_COMPAS,
    KW_TEMPO,
    KW_TONO,
    KW_ESCALA,
    KW_LISTA,
    KW_SECUENCIA,
    KW_DEFINIR,
    KW_SI,
    KW_SINO,
    KW_SEGUN,
    KW_CASO,
    KW_DEFECTO,
    KW_REPETIR,
    KW_MIENTRAS,
    KW_ROMPER,
    KW_CONTINUAR,
    KW_SECCION,
    KW_FIN,
    KW_MOSTRAR,
    KW_LEER,
    KW_DO,
    KW_RE,
    KW_MI,
    KW_FA,
    KW_SOL,
    KW_LA,
    KW_SI_NOTE,
    KW_VECES,    
    KW_MAYOR,    
    KW_MENOR,
    KW_TRUE,      // true
    KW_FALSE,     // false
    KW_ADAGIO,
    KW_ANDANTE,
    KW_ALLEGRO
}