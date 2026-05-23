package comp;

public class Cuarteto {
    public String operador;
    public String arg1;
    public String arg2;
    public String resultado;

    public Cuarteto(String op, String arg1, String arg2, String res) {
        this.operador = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.resultado = res;
    }

    @Override
    public String toString() {
        // Formato tabular para que se vea bonito en consola
        return String.format("%-10s | %-10s | %-10s | %-10s", operador, arg1, arg2, resultado);
    }
}