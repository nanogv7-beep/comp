package comp;
import static comp.Tokens.*;
import java.util.HashMap;
import java.util.Map;
import comp.Tokens;
%%

%class Lexer
%unicode
//%cup añadir cuando se use CUP
%function nextToken
%type TokenAlmacen
%line
%column

%{
    private Map<String, Tokens> keywords;

    public void initKeywords() {
        keywords = new HashMap<>();
        // Palabras reservadas (agrega/quita según necesites; clave en minúsculas)
        keywords.put("nota", KW_NOTA);
        keywords.put("acorde", KW_ACORDE);
        keywords.put("melodia", KW_MELODIA);
        keywords.put("tiempo", KW_TIEMPO);
        keywords.put("octava", KW_OCTAVA);
        keywords.put("tocar", KW_TOCAR);
        keywords.put("silencio", KW_SILENCIO);
        keywords.put("compas", KW_COMPAS);
        keywords.put("tempo", KW_TEMPO);
        keywords.put("tono", KW_TONO);
        keywords.put("escala", KW_ESCALA);

        keywords.put("lista", KW_LISTA);
        keywords.put("secuencia", KW_SECUENCIA);
        keywords.put("definir", KW_DEFINIR);

        // Estructuras de control
        keywords.put("si_", KW_SI);        // Estructura condicional
        keywords.put("sino", KW_SINO);
        keywords.put("segun", KW_SEGUN);
        keywords.put("caso", KW_CASO);
        keywords.put("defecto", KW_DEFECTO);
        keywords.put("repetir", KW_REPETIR);
        keywords.put("veces", KW_VECES);    // NUEVO: para "repetir X veces"
        keywords.put("mientras", KW_MIENTRAS);
        keywords.put("romper", KW_ROMPER);
        keywords.put("continuar", KW_CONTINUAR);

        keywords.put("seccion", KW_SECCION);
        keywords.put("fin", KW_FIN);
        keywords.put("mostrar", KW_MOSTRAR);
        keywords.put("leer", KW_LEER);

        // Valores para "tono" - NUEVOS
        keywords.put("mayor", KW_MAYOR);
        keywords.put("menor", KW_MENOR);

        //valores constantes
        keywords.put("adagio", KW_ADAGIO);   // Nuevo
        keywords.put("andante", KW_ANDANTE); // Nuevo
        keywords.put("allegro", KW_ALLEGRO); // Nuevo

        // Valores booleanos - NUEVOS
        keywords.put("true", KW_TRUE);     // ← AÑADIDO
        keywords.put("false", KW_FALSE);   // ← AÑADIDO

        // Notas musicales - "si" no se incluye aquí para evitar conflictos
        keywords.put("do", KW_DO);
        keywords.put("re", KW_RE);
        keywords.put("mi", KW_MI);
        keywords.put("fa", KW_FA);
        keywords.put("sol", KW_SOL);
        keywords.put("la", KW_LA);
        // "si" se maneja exclusivamente en el patrón NOTE
    }

    public String lexeme;
    public Tokens type;
    public int line;
    public int column;
    public TokenAlmacen token;

    // Helper para mapear identificadores a keywords
    private Tokens lookupKeyword(String text) {
        if (keywords == null) initKeywords();
        Tokens t = keywords.get(text.toLowerCase());
        return t;
    }
    
    // Método para verificar si una nota está delimitada correctamente
    private boolean esNotaDelimitada() {
        // Verificar caracter anterior
        int start = zzStartRead;
        if (start > 0) {
            char anterior = zzBuffer[start - 1];
            if (Character.isLetterOrDigit(anterior) || anterior == '_') {
                return false;
            }
        }
        
        // Verificar caracter posterior
        int end = start + yylength();
        if (end < zzEndRead) {
            char siguiente = zzBuffer[end];
            if (Character.isLetterOrDigit(siguiente) || siguiente == '_') {
                return false;
            }
        }
        
        return true;
    }
%}

/* ------ reglas léxicas (expresiones) ------ */
LETTER = [A-Za-zÁÉÍÓÚáéíóúÑñÜü_]
DIGIT  = [0-9]
ID     = {LETTER}({LETTER}|{DIGIT})*
WS     = [ \t\f]+
EOL    = [\r\n]+

/* Comentarios CORREGIDOS - ahora permiten cualquier carácter */
COMMENT_LINE = "//" [^\r\n]*
COMMENT_BLOCK = "/*" [^*] ~"*/" | "/*" "*"+ "/"
COMMENT_AMP = "&" [^&] ~"&" | "&" "&"

/* Caracteres no válidos (sin & porque ahora es para comentarios) */
INVALID_CHAR = [@$~¿?¡]

/* Notas: DO, RE, MI, FA, SOL, LA, SI con opcional # o b y octava 1-8 (según reporte) */
NOTE_LETTER = ([Dd][Oo]|[Rr][Ee]|[Mm][Ii]|[Ff][Aa]|[Ss][Oo][Ll]|[Ll][Aa]|[Ss][Ii])

/* Notas inválidas - CORREGIDO: ahora captura alteraciones múltiples correctamente */
INVALID_NOTE = {NOTE_LETTER}("##"|"bb"|"#b"|"b#")[1-8]? | {NOTE_LETTER}(\#|b)?[09]

/* Notas válidas - solo una alteración opcional y octava válida */
NOTE = ({NOTE_LETTER})(\#|b)?[1-8]?

%%

/* ------ SECCIÓN DE REGLAS ------ */

/* Ignorar espacios y comentarios */
{WS}               { /* ignore whitespace */ }
{EOL}              { /* increment line is automatic with %line; ignore */ }

/* Comentarios CORREGIDOS - deben ir antes de los errores */
{COMMENT_LINE}     { /* ignore line comments */ }
{COMMENT_BLOCK}    { /* ignore block comments */ }
{COMMENT_AMP}      { /* ignore comentarios entre & */ }

/* Errores de comentarios sin cerrar */
"/*" [^*]* {
    return new TokenAlmacen(ERROR, "Comentario de bloque sin cerrar: " + yytext(), yyline + 1, yycolumn + 1);
}

/* Errores de comentarios con & sin cerrar - NUEVO */
"&" [^&\r\n]* {
    return new TokenAlmacen(ERROR, "Comentario con & sin cerrar: " + yytext(), yyline + 1, yycolumn + 1);
}

/* Errores de cadenas sin cerrar */
\"([^\"\\\n]|\\.)*\n {
    return new TokenAlmacen(ERROR, "Cadena sin cerrar: " + yytext(), yyline + 1, yycolumn + 1);
}

\"([^\"\\\n]|\\.)* {
    return new TokenAlmacen(ERROR, "Cadena sin cerrar al final del archivo: " + yytext(), yyline + 1, yycolumn + 1);
}

/* Cadenas válidas */
\"([^\"\\\n]|\\.)*\" {
    lexeme = yytext();
    type = CADENA;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

/* Notas inválidas - DEBE IR ANTES de las notas válidas (CORREGIDO) */
{INVALID_NOTE} {
    String text = yytext();
    // Verificar que sea realmente una nota delimitada (no parte de una palabra)
    if (esNotaDelimitada()) {
        if (text.matches("(?i).*(##|bb|#b|b#).*")) {
            return new TokenAlmacen(ERROR, "Nota con alteraciones múltiples inválidas: " + text, yyline + 1, yycolumn + 1);
        } else {
            return new TokenAlmacen(ERROR, "Nota con octava inválida (debe ser 1-8): " + text, yyline + 1, yycolumn + 1);
        }
    }
    // Si no es una nota delimitada, continuar sin hacer nada
}

/* Notas musicales válidas - SOLO cuando están delimitadas (CORREGIDO) */
{NOTE} {
    String text = yytext();
    // Verificar que la nota esté delimitada correctamente
    if (esNotaDelimitada()) {
        // Verificar que tenga formato válido de nota
        if (text.matches("(?i)^(do|re|mi|fa|sol|la|si)([#b]?[1-8]?)$")) {
            return new TokenAlmacen(Tokens.NOTE, text, yyline + 1, yycolumn + 1);
        }
    }
    // Si no es una nota válida, no hacer nada (dejar que continúe)
}

/* Caracteres no válidos */
{INVALID_CHAR} {
    return new TokenAlmacen(ERROR, "Carácter no reconocido: '" + yytext() + "'", yyline + 1, yycolumn + 1);
}

/* ------ MODIFICADO: Errores de números mal formados ahora como "Cadena inválida" ------ */

/* Cadenas inválidas (antes eran errores de números) */
{DIGIT}+("."{DIGIT}*)+"."{DIGIT}* {
    return new TokenAlmacen(ERROR, "Cadena inválida: '" + yytext() + "' (múltiples puntos decimales)", yyline + 1, yycolumn + 1);
}

{DIGIT}+"." {
    return new TokenAlmacen(ERROR, "Cadena inválida: '" + yytext() + "' (termina con punto decimal)", yyline + 1, yycolumn + 1);
}

\.{DIGIT}* {
    return new TokenAlmacen(ERROR, "Cadena inválida: '" + yytext() + "' (sin parte entera)", yyline + 1, yycolumn + 1);
}

{DIGIT}+{LETTER}+ {
    return new TokenAlmacen(ERROR, "Cadena inválida: '" + yytext() + "' (mezcla números con letras)", yyline + 1, yycolumn + 1);
}

/* Errores de identificadores mal formados */
{DIGIT}+{ID} {
    return new TokenAlmacen(ERROR, "Cadena inválida: '" + yytext() + "' (identificador que empieza con número)", yyline + 1, yycolumn + 1);
}

/* Identificadores con caracteres inválidos - MÁS ESPECÍFICO */
{ID}[-@$#%&*]+{ID}? {
    return new TokenAlmacen(ERROR, "Cadena inválida: '" + yytext() + "' (caracteres inválidos en identificador)", yyline + 1, yycolumn + 1);
}

/* Números decimales válidos */
{DIGIT}+"."{DIGIT}+ {
    lexeme = yytext();
    type = NumeroDecimal;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

/* Números enteros válidos */
{DIGIT}+ {
    lexeme = yytext();
    type = Numero;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

/* Operadores lógicos */
[Yy][Yy] { lexeme = yytext(); type = opAnd; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
"||" { lexeme = yytext(); type = opOr; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
"!"  { lexeme = yytext(); type = opNot; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }

/* Identificadores y palabras reservadas - INCLUYENDO true y false */
{ID} {
    lexeme = yytext();
    Tokens k = lookupKeyword(lexeme);
    if (k != null) {
        type = k;
    } else {
        type = Identificador; /* token por defecto para identificadores */
    }
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

/* Símbolos y operadores */
"=" {
    lexeme = yytext();
    type = Igual;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

"," {
    lexeme = yytext();
    type = Coma;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

";" {
    lexeme = yytext();
    type = PuntoComa;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

":" {
    lexeme = yytext();
    type = DosPuntos;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

"{" {
    lexeme = yytext();
    type = LlaveAper;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

"}" {
    lexeme = yytext();
    type = LlaveCierre;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

"(" {
    lexeme = yytext();
    type = ParenAper;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

")" {
    lexeme = yytext();
    type = ParenCierre;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

"[" {
    lexeme = yytext();
    type = CorcheteAper;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

"]" {
    lexeme = yytext();
    type = CorcheteCierre;
    line = yyline + 1;
    column = yycolumn + 1;
    token = new TokenAlmacen(type,lexeme,line,column);
    return token;
}

/* Operadores aritméticos */
"+" { lexeme = yytext(); type = Suma; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
"-" { lexeme = yytext(); type = Resta; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
"*" { lexeme = yytext(); type = Multiplicacion; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
"/" { lexeme = yytext(); type = Division; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
"%" { lexeme = yytext(); type = Modulo; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }

/* Operadores relacionales */
"==" { lexeme = yytext(); type = opRel_ig; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
"!=" { lexeme = yytext(); type = opRel_dif; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
"<=" { lexeme = yytext(); type = opRel_menIgual; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
">=" { lexeme = yytext(); type = opRel_mayIgual; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
"<"  { lexeme = yytext(); type = opRel_men; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }
">"  { lexeme = yytext(); type = opRel_may; line = yyline + 1; column = yycolumn + 1; token = new TokenAlmacen(type,lexeme,line,column); return token; }

/* fin del archivo o EOF */
<<EOF>>   { return new TokenAlmacen(Tokens.EOF, "EOF", yyline + 1, yycolumn + 1); }

/* Carácter no reconocido (catch-all) */
. { 
    return new TokenAlmacen(ERROR, "Cadena inválida: '" + yytext() + "'", yyline + 1, yycolumn + 1); 
}