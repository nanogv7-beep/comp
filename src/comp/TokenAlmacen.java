package comp;


/*Creación de la clase tokenAlmacen, que nos ayuda a almacenar el tipo y el valor del lexema*/
   

public class TokenAlmacen {
    

    private final String value;
    private final Tokens type;
    private final int line, column;
    
    public TokenAlmacen(Tokens type, String value,  int line, int column){
        this.value = value;
        this.type = type;
        this.column = column;
        this.line = line;
    }

    public Tokens getType(){
        return type;
    }
    
    public String getValue(){
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString(){
        return "Token("+value+", "+type+", "+line+", "+column+")";
    }
}
