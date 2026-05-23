/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import jflex.exceptions.SilentExit;

/**
 *
 * @author gdlup
 */
public class RutaLexer {

    public static void main(String[] args) throws Exception {
        String ruta = new File("src/comp/Lexer.flex").getPath();
        String ruta2 = new File("src/comp/LexerCUP.flex").getPath();
        String ruta3 = new File("src/comp/sintax.cup").getPath();
        String[] rutaS ={"-parser", "sintax", ruta3};

        parserLexer(ruta, ruta2, rutaS);
    }

    public static void parserLexer(String ruta, String ruta2, String[] rutaS) throws IOException, Exception {
        String[] opcion1 = {ruta};
        String[] opcion2 = {ruta2};
        
        try {
            jflex.Main.generate(opcion1);
            jflex.Main.generate(opcion2);
            java_cup.Main.main(rutaS);
        } catch (SilentExit ex) {

        }
        //"C:\Users\nanog\OneDrive\Desktop\comp\sym.java"
        //"C:\Users\nanog\OneDrive\Desktop\comp\src\comp\sym.java"
        
        Path rutaSym = Paths.get("C:\\Users\\omaro\\OneDrive\\Desktop\\comp\\src\\comp\\sym.java");
        if (Files.exists(rutaSym)) {
            Files.delete(rutaSym);
        }
        
        Files.move(
                Paths.get("C:\\Users\\omaro\\OneDrive\\Desktop\\comp\\sym.java"), 
                Paths.get("C:\\Users\\omaro\\OneDrive\\Desktop\\comp\\src\\comp\\sym.java")
        );
        
        Path rutaSin = Paths.get("C:\\Users\\omaro\\OneDrive\\Desktop\\comp\\src\\comp\\sintax.java");
        if (Files.exists(rutaSin)) {
            Files.delete(rutaSin);
        }
        
        Files.move(
                Paths.get("C:\\Users\\omaro\\OneDrive\\Desktop\\comp\\sintax.java"), 
                Paths.get("C:\\Users\\omaro\\OneDrive\\Desktop\\comp\\src\\comp\\sintax.java")
        );
        }
}
/* {
        File archivo = new File(ruta);
        jflex.Main.generate(archivo);
    }*/
