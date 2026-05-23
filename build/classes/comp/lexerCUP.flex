package comp;
import java_cup.runtime.Symbol;
%%

%class LexerCup
%type java_cup.runtime.Symbol
%cup
%full
%line
%column
%char
%ignorecase

%{
    /* Método para tokens SIN valor (ej: if, while, +) */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }
    
    /* Método para tokens CON valor (ej: identificadores, números) */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

/* ------ Definiciones léxicas ------ */
LETTER = [A-Za-zÁÉÍÓÚáéíóúÑñÜü_]
DIGIT  = [0-9]
ID     = {LETTER}({LETTER}|{DIGIT})*
WS     = [ \t\f\r\n]+

/* Comentarios */
COMMENT_LINE = "//" [^\r\n]*
COMMENT_BLOCK = "/*" [^*] ~"*/" | "/*" "*"+ "/"

/* Notas musicales */
NOTE_REGEX = (DO|RE|MI|FA|SOL|LA|SI)(\#|b)?[1-8]?

%%

/* ------ SECCIÓN DE REGLAS ------ */

/* Ignorar espacios y comentarios */
{WS}            { /* ignore */ }
{COMMENT_LINE}  { /* ignore */ }
{COMMENT_BLOCK} { /* ignore */ }

/* Cadenas válidas */
\"([^\"\\\n]|\\.)*\" { return symbol(sym.CADENA, yytext()); }

/* Notas musicales válidas */
{NOTE_REGEX} { return symbol(sym.NOTE, yytext()); }

/* Números */
{DIGIT}+"."{DIGIT}+ { return symbol(sym.NumeroDecimal, yytext()); }
{DIGIT}+            { return symbol(sym.Numero, yytext()); }

/* Operadores lógicos */
[Yy][Yy] { return symbol(sym.opAnd, yytext()); }
"&&"     { return symbol(sym.opAnd, yytext()); }
"||"     { return symbol(sym.opOr, yytext()); }
"!"      { return symbol(sym.opNot, yytext()); }

/* Palabras reservadas */

/* CORREGIDO: Se respeta "si_" para diferenciar de la nota SI */
"si_"        { return symbol(sym.KW_SI, yytext()); } 
"sino"       { return symbol(sym.KW_SINO, yytext()); }

"nota"       { return symbol(sym.KW_NOTA, yytext()); }
"acorde"     { return symbol(sym.KW_ACORDE, yytext()); }
"melodia"    { return symbol(sym.KW_MELODIA, yytext()); }
"secuencia"  { return symbol(sym.KW_SECUENCIA, yytext()); }
"lista"      { return symbol(sym.KW_LISTA, yytext()); }
"silencio"   { return symbol(sym.KW_SILENCIO, yytext()); }

"tiempo"     { return symbol(sym.KW_TIEMPO, yytext()); }
"octava"     { return symbol(sym.KW_OCTAVA, yytext()); }
"compas"     { return symbol(sym.KW_COMPAS, yytext()); }
"tempo"      { return symbol(sym.KW_TEMPO, yytext()); }
"tono"       { return symbol(sym.KW_TONO, yytext()); }
"escala"     { return symbol(sym.KW_ESCALA, yytext()); }

"mayor"      { return symbol(sym.KW_MAYOR, yytext()); }
"menor"      { return symbol(sym.KW_MENOR, yytext()); }
"adagio"     { return symbol(sym.KW_ADAGIO, yytext()); }
"andante"    { return symbol(sym.KW_ANDANTE, yytext()); }
"allegro"    { return symbol(sym.KW_ALLEGRO, yytext()); }

"tocar"      { return symbol(sym.KW_TOCAR, yytext()); }
"definir"    { return symbol(sym.KW_DEFINIR, yytext()); }
"mostrar"    { return symbol(sym.KW_MOSTRAR, yytext()); }
"leer"       { return symbol(sym.KW_LEER, yytext()); }

"seccion"    { return symbol(sym.KW_SECCION, yytext()); }
"fin"        { return symbol(sym.KW_FIN, yytext()); }

/* Estructuras de control */
"segun"      { return symbol(sym.KW_SEGUN, yytext()); }
"caso"       { return symbol(sym.KW_CASO, yytext()); }
"defecto"    { return symbol(sym.KW_DEFECTO, yytext()); }
"repetir"    { return symbol(sym.KW_REPETIR, yytext()); }
"veces"      { return symbol(sym.KW_VECES, yytext()); }
"mientras"   { return symbol(sym.KW_MIENTRAS, yytext()); }
"romper"     { return symbol(sym.KW_ROMPER, yytext()); }
"continuar"  { return symbol(sym.KW_CONTINUAR, yytext()); }

/* Booleanos */
"true"       { return symbol(sym.KW_TRUE, yytext()); }
"false"      { return symbol(sym.KW_FALSE, yytext()); }

/* Identificadores */
{ID} { return symbol(sym.Identificador, yytext()); }

/* Símbolos y operadores */
"="  { return symbol(sym.Igual, yytext()); }
","  { return symbol(sym.Coma, yytext()); }
";"  { return symbol(sym.PuntoComa, yytext()); }
":"  { return symbol(sym.DosPuntos, yytext()); }
"{"  { return symbol(sym.LlaveAper, yytext()); }
"}"  { return symbol(sym.LlaveCierre, yytext()); }
"("  { return symbol(sym.ParenAper, yytext()); }
")"  { return symbol(sym.ParenCierre, yytext()); }
"["  { return symbol(sym.CorcheteAper, yytext()); }
"]"  { return symbol(sym.CorcheteCierre, yytext()); }

"+"  { return symbol(sym.Suma, yytext()); }
"-"  { return symbol(sym.Resta, yytext()); }
"*"  { return symbol(sym.Multiplicacion, yytext()); }
"/"  { return symbol(sym.Division, yytext()); }
"%"  { return symbol(sym.Modulo, yytext()); }

"==" { return symbol(sym.opRel_ig, yytext()); }
"!=" { return symbol(sym.opRel_dif, yytext()); }
"<=" { return symbol(sym.opRel_menIgual, yytext()); }
">=" { return symbol(sym.opRel_mayIgual, yytext()); }
"<"  { return symbol(sym.opRel_men, yytext()); }
">"  { return symbol(sym.opRel_may, yytext()); }

/* Carácter no reconocido */
. { return symbol(sym.error, yytext()); }