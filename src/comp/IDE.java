package comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.logging.*;
import java_cup.runtime.Symbol;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author gdlup
 */
public class IDE extends javax.swing.JFrame {

    //variable global para acceder al numero de linea
    NumeroLinea numeroLinea;

    //tabla
    DefaultTableModel modelo;

    //variable global de tipo directorio, para acceder a sus métodos
    Directorio dir;

    /**
     * Creates new form IDE
     */
    public IDE() { //inicializador
        initComponents();
        inicializar();
        jtaConsola.setEditable(false);

        //ligando los métodos a los menu
        MenuReservadas.addActionListener(e -> llenarTablaReservadas());
        MenuTokens.addActionListener(e -> analisisLexico());
        MenuGuardar.addActionListener(e -> dir.Guardar(this));
        MenuAbrir.addActionListener(e -> dir.Abrir(this));
        menuID.addActionListener(e -> generarTablaIdentificadoresCompleta());
        MenuNuevo.addActionListener(e -> {
            jtaConsola.setText("");
            dir.Nuevo(this);
        });

        //código para el tamaño de las letras
        jtpCodigo.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                Font font = jtpCodigo.getFont();
                int size = font.getSize();

                if (e.getWheelRotation() < 0) { // rueda hacia arriba → zoom in
                    size++;
                } else {                        // rueda hacia abajo → zoom out
                    size = Math.max(8, size - 1);
                }

                jtpCodigo.setFont(font.deriveFont((float) size));
            }
        });

        jtaConsola.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                Font font = jtaConsola.getFont();
                int size = font.getSize();

                if (e.getWheelRotation() < 0) { // rueda hacia arriba → zoom in
                    size++;
                } else {                        // rueda hacia abajo → zoom out
                    size = Math.max(8, size - 1);
                }

                jtaConsola.setFont(font.deriveFont((float) size));
            }
        });

    }

    //método para encontrar las últimas cadenas
    private int findLastNonWordChar(String texto, int index) {
        while (--index >= 0) {
            // \\W es una expresión regular equivalente a [A-Za-Z0-9], contiene lo mismo 
            if (String.valueOf(texto.charAt(index)).matches("\\W")) {
                break; //romperá/saldrá porque ya encontramos la última palabra
            }
        }
        return index; //si es positivo, retornar el índice para saber dónde estamos
    }

    //método para encontrar las primeras cadenas
    private int findFirstNonWordChar(String texto, int index) {
        while (index < texto.length()) {
            if (String.valueOf(texto.charAt(index)).matches("\\W")) {
                break; //en caso positivo
            }
            index++;
        }
        return index;
    }

    //método para pintar las palabras reservadas
    private void colorear() {
        final StyleContext cont = StyleContext.getDefaultStyleContext();

        // Definición de colores para palabras reservadas
        final AttributeSet attBlue = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 0, 255));
        final AttributeSet attDarkRed = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(139, 0, 0));
        final AttributeSet attPurple = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(128, 0, 128));
        final AttributeSet attGreen = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 128, 0));
        final AttributeSet attBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 0, 0));
        final AttributeSet attNote = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(204, 0, 0));
        final AttributeSet attOrange = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(255, 140, 0)); // para valores de tono
        final AttributeSet attGray = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(128, 128, 128)); // para comentarios
        // NUEVO: Color para valores booleanos (magenta)
        final AttributeSet attMagenta = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(148, 0, 211));
        // NUEVO: Color para números
        final AttributeSet attBrown = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(139, 69, 19));
        // NUEVO: Color para cadenas (verde oscuro)
        final AttributeSet attDarkGreen = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 100, 0));

        // Documento con estilos personalizados
        DefaultStyledDocument doc = new DefaultStyledDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);
                colorearTexto();
            }

            @Override
            public void remove(int offs, int len) throws BadLocationException {
                super.remove(offs, len);
                colorearTexto();
            }

            private void colorearTexto() throws BadLocationException {
                String texto = getText(0, getLength());
                // Primero poner todo en negro (estilo por defecto)
                setCharacterAttributes(0, texto.length(), attBlack, true);

                // Palabras reservadas de NotaScript agrupadas por categorías
                // Tipos y estructuras musicales (Azul)
                String[] palabrasMusica = {"nota", "acorde", "melodia", "tiempo", "octava", "tocar",
                    "silencio", "compas", "tempo", "tono", "escala", "adagio", "andante", "allegro"};

                // Estructuras de datos (Rojo oscuro)
                String[] palabrasEstructuras = {"lista", "secuencia", "definir"};

                // Estructuras de control (Púrpura) - INCLUYE "veces"
                String[] palabrasControl = {"si_", "sino", "segun", "caso", "defecto", "repetir", "veces",
                    "mientras", "romper", "continuar"};

                // Funciones y secciones (Verde)
                String[] palabrasFunciones = {"seccion", "fin", "mostrar", "leer"};

                // Valores de tono (Naranja)
                String[] palabrasTono = {"mayor", "menor"};

                // NUEVO: Valores booleanos (Magenta)
                String[] palabrasBooleanas = {"true", "false"};

                // Colorear palabras musicales (Azul)
                for (String palabra : palabrasMusica) {
                    colorearPalabra(texto, palabra, attBlue);
                }

                // Colorear estructuras de datos (Rojo oscuro)
                for (String palabra : palabrasEstructuras) {
                    colorearPalabra(texto, palabra, attDarkRed);
                }

                // Colorear estructuras de control (Púrpura)
                for (String palabra : palabrasControl) {
                    colorearPalabra(texto, palabra, attPurple);
                }

                // Colorear funciones y secciones (Verde)
                for (String palabra : palabrasFunciones) {
                    colorearPalabra(texto, palabra, attGreen);
                }

                // Colorear valores de tono (Naranja)
                for (String palabra : palabrasTono) {
                    colorearPalabra(texto, palabra, attOrange);
                }

                // NUEVO: Colorear valores booleanos (Magenta)
                for (String palabra : palabrasBooleanas) {
                    colorearPalabra(texto, palabra, attMagenta);
                }

                // Colorear notas musicales (Rojo)
                colorearNotasMusicales(texto);

                // Colorear números (Marrón) - NUEVO
                colorearNumeros(texto, attBrown);

                // Colorear cadenas (Verde oscuro) - NUEVO
                colorearCadenas(texto, attDarkGreen);

                // Colorear comentarios (Gris)
                colorearComentarios(texto, attGray);
            }

            // NUEVO: Método para colorear números
            private void colorearNumeros(String texto, AttributeSet color) {
                // Patrón para números enteros y decimales
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b\\d+(\\.\\d+)?\\b");
                java.util.regex.Matcher matcher = pattern.matcher(texto);

                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    // Verificar que no es parte de una palabra
                    if (esPalabraCompleta(texto, start, end - start)) {
                        setCharacterAttributes(start, end - start, color, false);
                    }
                }
            }

            // NUEVO: Método para colorear cadenas
            private void colorearCadenas(String texto, AttributeSet color) {
                // Patrón para cadenas entre comillas dobles
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
                java.util.regex.Matcher matcher = pattern.matcher(texto);

                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    setCharacterAttributes(start, end - start, color, false);
                }
            }

            private void colorearComentarios(String texto, AttributeSet color) {
                // Comentarios de línea: // ...
                colorearComentariosLinea(texto, color);
                // Comentarios de bloque: /* ... */
                colorearComentariosBloque(texto, color);
                // Comentarios con &: & ... &
                colorearComentariosAmp(texto, color);
            }

            private void colorearComentariosLinea(String texto, AttributeSet color) {
                // Patrón para comentarios de línea: // ... hasta el final de la línea
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("//[^\\n]*");
                java.util.regex.Matcher matcher = pattern.matcher(texto);

                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    setCharacterAttributes(start, end - start, color, false);
                }
            }

            private void colorearComentariosBloque(String texto, AttributeSet color) {
                // Patrón para comentarios de bloque: /* ... */
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("/\\*.*?\\*/", java.util.regex.Pattern.DOTALL);
                java.util.regex.Matcher matcher = pattern.matcher(texto);

                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    setCharacterAttributes(start, end - start, color, false);
                }
            }

            private void colorearComentariosAmp(String texto, AttributeSet color) {
                // Patrón para comentarios con &: & ... &
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("&[^&]*&");
                java.util.regex.Matcher matcher = pattern.matcher(texto);

                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    setCharacterAttributes(start, end - start, color, false);
                }
            }

            private void colorearNotasMusicales(String texto) {
                // Lista de notas musicales básicas
                String[] notas = {"do", "re", "mi", "fa", "sol", "la", "si"};

                for (String nota : notas) {
                    colorearNotaConOctava(texto, nota, attNote);
                }
            }

            private void colorearNotaConOctava(String texto, String nota, AttributeSet color) {
                String textoLower = texto.toLowerCase();
                String notaLower = nota.toLowerCase();
                int index = 0;

                while ((index = textoLower.indexOf(notaLower, index)) != -1) {
                    // Verificar que es una nota completa (no parte de otra palabra)
                    if (esNotaCompleta(texto, index, nota.length())) {
                        // Calcular la longitud total (nota + posible alteración + posible octava)
                        int longitudTotal = calcularLongitudNota(texto, index, nota.length());
                        setCharacterAttributes(index, longitudTotal, color, false);
                    }
                    index += nota.length();
                }
            }

            private int calcularLongitudNota(String texto, int inicio, int longitudNota) {
                int pos = inicio + longitudNota;
                int longitudTotal = longitudNota;

                // Verificar si hay alteración (# o b)
                if (pos < texto.length()) {
                    char c = texto.charAt(pos);
                    if (c == '#' || c == 'b') {
                        longitudTotal++;
                        pos++;
                    }
                }

                // Verificar si hay octava (1-8)
                if (pos < texto.length()) {
                    char c = texto.charAt(pos);
                    if (c >= '1' && c <= '8') {
                        longitudTotal++;
                    }
                }

                return longitudTotal;
            }

            private boolean esNotaCompleta(String texto, int inicio, int longitud) {
                // Verificar caracter anterior
                if (inicio > 0) {
                    char anterior = texto.charAt(inicio - 1);
                    if (Character.isLetterOrDigit(anterior) || anterior == '_' || anterior == '@') {
                        return false;
                    }
                }

                // Verificar caracter posterior
                int fin = inicio + longitud;
                if (fin < texto.length()) {
                    char siguiente = texto.charAt(fin);
                    // Para notas, permitimos que después venga #, b, o dígito (1-8)
                    if (!(siguiente == '#' || siguiente == 'b' || (siguiente >= '1' && siguiente <= '8'))) {
                        if (Character.isLetterOrDigit(siguiente) || siguiente == '_' || siguiente == '@') {
                            return false;
                        }
                    }
                }

                return true;
            }

            private void colorearPalabra(String texto, String palabra, AttributeSet color) {
                String textoLower = texto.toLowerCase();
                String palabraLower = palabra.toLowerCase();
                int index = 0;

                while ((index = textoLower.indexOf(palabraLower, index)) != -1) {
                    // Verificar que es una palabra completa (no parte de otra palabra)
                    if (esPalabraCompleta(texto, index, palabra.length())) {
                        setCharacterAttributes(index, palabra.length(), color, false);
                    }
                    index += palabra.length();
                }
            }

            private boolean esPalabraCompleta(String texto, int inicio, int longitud) {
                // Verificar caracter anterior
                if (inicio > 0) {
                    char anterior = texto.charAt(inicio - 1);
                    if (Character.isLetterOrDigit(anterior) || anterior == '_') {
                        return false;
                    }
                }

                // Verificar caracter posterior
                int fin = inicio + longitud;
                if (fin < texto.length()) {
                    char siguiente = texto.charAt(fin);
                    if (Character.isLetterOrDigit(siguiente) || siguiente == '_') {
                        return false;
                    }
                }

                return true;
            }
        };

        // Configuración del JTextPane
        String temp = jtpCodigo.getText();
        jtpCodigo.setDocument(doc);
        jtpCodigo.setText(temp);
    }//Fin del metodo colorear

    //método para inicializar componentes y variables
    private void inicializar() {
        setTitle("NotaScript");
        String[] opciones = new String[]{"Guardar y continuar", "Descargar"};

        numeroLinea = new NumeroLinea(jtpCodigo);

        //asignacion al jScrollPane el numero de linea
        jScrollPane1.setRowHeaderView(numeroLinea);

        dir = new Directorio();

        colorear();

    }//fin inicializar

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        btnCompilar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpCodigo = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaConsola = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        Archivo = new javax.swing.JMenu();
        MenuAbrir = new javax.swing.JMenuItem();
        MenuGuardar = new javax.swing.JMenuItem();
        MenuNuevo = new javax.swing.JMenuItem();
        Tablas = new javax.swing.JMenu();
        MenuReservadas = new javax.swing.JMenuItem();
        menuID = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        MenuTokens = new javax.swing.JMenuItem();
        btnSintactico = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        jMenu5.setText("File");
        jMenuBar3.add(jMenu5);

        jMenu6.setText("Edit");
        jMenuBar3.add(jMenu6);

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 204, 102));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCompilar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/On Layer/play.png"))); // NOI18N
        btnCompilar.setText("Compilar");
        btnCompilar.setToolTipText("Compilar código");
        btnCompilar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCompilar.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/pressed/music (1).png"))); // NOI18N
        btnCompilar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/On Layer/icons8_code_48px_on.png"))); // NOI18N
        btnCompilar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCompilar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompilarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCompilar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 100, 90));

        jtpCodigo.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jtpCodigoMouseWheelMoved(evt);
            }
        });
        jtpCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtpCodigoKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jtpCodigo);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 28, 670, 400));

        jtaConsola.setColumns(20);
        jtaConsola.setRows(5);
        jScrollPane2.setViewportView(jtaConsola);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 440, 770, 140));

        jMenuBar1.setBackground(new java.awt.Color(255, 204, 0));

        Archivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/Icon/music.png"))); // NOI18N
        Archivo.setText("Archivo");
        Archivo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ArchivoMouseClicked(evt);
            }
        });

        MenuAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/Icon/next.png"))); // NOI18N
        MenuAbrir.setText("Abrir");
        Archivo.add(MenuAbrir);

        MenuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/Icon/pause.png"))); // NOI18N
        MenuGuardar.setText("Guardar");
        Archivo.add(MenuGuardar);

        MenuNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/Icon/play-button.png"))); // NOI18N
        MenuNuevo.setText("Nuevo");
        Archivo.add(MenuNuevo);

        jMenuBar1.add(Archivo);

        Tablas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/Icon/music-notes.png"))); // NOI18N
        Tablas.setText("Tablas Fijas");

        MenuReservadas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/Icon/tone.png"))); // NOI18N
        MenuReservadas.setText("Palabras Reservadas");
        MenuReservadas.setName("MenuReservadas"); // NOI18N
        MenuReservadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuReservadasActionPerformed(evt);
            }
        });
        Tablas.add(MenuReservadas);

        menuID.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/Icon/music.png"))); // NOI18N
        menuID.setText("Tabla de Simbolos");
        menuID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuIDActionPerformed(evt);
            }
        });
        Tablas.add(menuID);

        jMenuBar1.add(Tablas);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/Icon/pause.png"))); // NOI18N
        jMenu2.setText("Compilar");

        MenuTokens.setIcon(new javax.swing.ImageIcon(getClass().getResource("/comp/iconos/Icon/sound.png"))); // NOI18N
        MenuTokens.setText("Analisis Lexico");
        MenuTokens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuTokensActionPerformed(evt);
            }
        });
        jMenu2.add(MenuTokens);

        btnSintactico.setText("Analisis Sintactico");
        btnSintactico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSintacticoActionPerformed(evt);
            }
        });
        jMenu2.add(btnSintactico);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtpCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtpCodigoKeyReleased
        int keyCode = evt.getKeyCode();
        if ((keyCode >= 65 && keyCode <= 90) || (keyCode >= 48 && keyCode <= 57)
                || (keyCode >= 97 && keyCode <= 122) || (keyCode != 27 && !(keyCode >= 37 && keyCode <= 40) && !(keyCode >= 16
                && keyCode <= 18) && keyCode != 524 && keyCode != 20)) {
            if (!getTitle().contains("*")) {
                setTitle(getTitle() + "*");
            }
        }
    }//GEN-LAST:event_jtpCodigoKeyReleased

    private void btnCompilarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompilarActionPerformed
        jtaConsola.setText(""); // Limpiar consola al inicio

        /* try {
            // 1. Ejecutar Léxico y obtener resultados
            List erroresLexicos = probarLex();

            // 2. Ejecutar Sintáctico y obtener resultados
            // (Ahora se ejecuta SIEMPRE, haya errores léxicos o no)
            String erroresSintacticos = probarSintactico();

            // 3. Evaluar resultados combinados
            boolean hayErroresLex = !erroresLexicos.isEmpty();
            boolean hayErroresSin = !erroresSintacticos.isEmpty();

            if (!hayErroresLex && !hayErroresSin) {
                // --- CASO DE ÉXITO ---
                jtaConsola.setText("RESULTADO DEL ANÁLISIS:\n"
                        + "-----------------------\n"
                        + "Análisis Léxico:  Correcto\n"
                        + "Análisis Sintáctico: Correcto\n\n"
                        + "¡Compilación Exitosa! El código es válido.");
                jtaConsola.setForeground(new Color(0, 100, 0)); // Verde Oscuro

            } else {
                // --- CASO DE ERROR (Mostramos todo junto) ---
                StringBuilder reporteFinal = new StringBuilder();
                reporteFinal.append("RESULTADO DE LA COMPILACIÓN:\n");
                reporteFinal.append("============================\n\n");

                if (hayErroresLex) {
                    reporteFinal.append(">>> ERRORES LÉXICOS ENCONTRADOS:\n");
                    reporteFinal.append(erroresLexicos);
                    reporteFinal.append("\n");
                } else {
                    reporteFinal.append(">>> Análisis Léxico: Sin errores.\n\n");
                }

                if (hayErroresSin) {
                    reporteFinal.append(">>> ERRORES SINTÁCTICOS ENCONTRADOS:\n");
                    reporteFinal.append(erroresSintacticos);
                } else {
                    reporteFinal.append(">>> Análisis Sintáctico: Sin errores.\n");
                }

                jtaConsola.setText(reporteFinal.toString());
                jtaConsola.setForeground(Color.RED); // Rojo
            }

        } catch (IOException ex) {
            jtaConsola.setText("Error crítico de archivo: " + ex.getMessage());
            jtaConsola.setForeground(Color.RED);
        }

        // Actualizar tablas visuales
        // llenarTablaSimbolos();
         */
        jtaConsola.setText(""); // Limpiar consola

        try {
            // 1. Obtener las listas (Ahora ambos métodos devuelven List<ErrorAlmacen>)
            List<ErrorAlmacen> erroresLex = probarLex();
            List<ErrorAlmacen> erroresSin = probarSintactico();

            // 2. Unir todo en una sola lista
            List<ErrorAlmacen> todosLosErrores = new ArrayList<>();
            todosLosErrores.addAll(erroresLex);
            todosLosErrores.addAll(erroresSin);

            // 3. Verificar resultados
            if (todosLosErrores.isEmpty()) {
                jtaConsola.setText("RESULTADO DEL ANÁLISIS:\n"
                        + "-----------------------\n"
                        + "¡Compilación Exitosa! El código es válido.");
                jtaConsola.setForeground(new Color(0, 100, 0)); // Verde
            } else {
                // 4. ORDENAR y MOSTRAR

                // Ordenar por número de línea
                todosLosErrores.sort(Comparator.comparingInt(ErrorAlmacen::getLine));

                StringBuilder reporte = new StringBuilder();
                reporte.append("ERRORES ENCONTRADOS (" + todosLosErrores.size() + "):\n");
                reporte.append("====================================\n");

                for (ErrorAlmacen error : todosLosErrores) {
                    // Si la línea es 0 (error sintáctico genérico), no la mostramos para que se vea mejor
                    String etiquetaLinea = (error.getLine() > 0) ? String.valueOf(error.getLine()) : "?";

                    reporte.append(" > [Línea ").append(etiquetaLinea).append("]: ")
                            .append(error.getValue())
                            .append("\n");
                }

                jtaConsola.setText(reporte.toString());
                jtaConsola.setForeground(Color.RED);
            }

        } catch (Exception ex) {
            jtaConsola.setText("Error inesperado: " + ex.getMessage());
        }


    }//GEN-LAST:event_btnCompilarActionPerformed

    private void jtpCodigoMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jtpCodigoMouseWheelMoved
        // TODO add your handling code here:

    }//GEN-LAST:event_jtpCodigoMouseWheelMoved

    private void MenuTokensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuTokensActionPerformed
   // TODO add your handling code here:
    }//GEN-LAST:event_MenuTokensActionPerformed

    private void ArchivoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ArchivoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_ArchivoMouseClicked

    private void MenuReservadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuReservadasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MenuReservadasActionPerformed

    private void menuIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menuIDActionPerformed

    private void btnSintacticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSintacticoActionPerformed
    analisisSintactico();        // TODO add your handling code here:
    }//GEN-LAST:event_btnSintacticoActionPerformed

    //lista para la tabla de tokens
    public static List<TokenAlmacen> listaTokens = new ArrayList<>();
    
    // --- MÉTODO PARA MOSTRAR VENTANAS FLOTANTES (NO BLOQUEANTES) ---
    private void mostrarReporteNoModal(String titulo, String mensaje, int tipoMensaje) {
        // 1. Crear un área de texto para el mensaje (mejor que un label simple)
        javax.swing.JTextArea areaTexto = new javax.swing.JTextArea(mensaje);
        areaTexto.setEditable(false);
        areaTexto.setLineWrap(false); // No cortar líneas automáticamente si son errores de código
        areaTexto.setWrapStyleWord(false);
        areaTexto.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        areaTexto.setBackground(new java.awt.Color(240, 240, 240)); // Color grisáceo estándar
        areaTexto.setMargin(new java.awt.Insets(10, 10, 10, 10));

        // 2. Meter el área en un Scroll (por si son muchos errores)
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(areaTexto);
        scroll.setPreferredSize(new java.awt.Dimension(600, 350)); // Tamaño inicial

        // 3. Crear el JOptionPane (solo como contenedor del contenido)
        javax.swing.JOptionPane optionPane = new javax.swing.JOptionPane(
                scroll, 
                tipoMensaje, 
                javax.swing.JOptionPane.DEFAULT_OPTION
        );

        // 4. Crear el JDialog manualmente a partir del JOptionPane
        javax.swing.JDialog dialog = optionPane.createDialog(this, titulo);
        
        // 5. >>> LA CLAVE: HACERLO NO MODAL <<<
        dialog.setModal(false); 
        
        // Opcional: Que se quede siempre encima para no perderla de vista mientras corriges
        dialog.setAlwaysOnTop(true); 
        
        dialog.setVisible(true);
    }
    
public void analisisLexico() {
        List<ErrorAlmacen> errores = probarLex();
        llenarTablaTokens();

        if (errores.isEmpty()) {
            mostrarReporteNoModal(
                "Resultado Léxico",
                "¡Análisis Léxico Correcto!\nNo se encontraron tokens inválidos.",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("Se encontraron ").append(errores.size()).append(" errores léxicos:\n\n");

            for (ErrorAlmacen error : errores) {
                mensaje.append(" > [Línea ").append(error.getLine()).append("]: ")
                        .append(error.getValue())
                        .append("\n");
            }
            
            // Usamos el método no modal
            mostrarReporteNoModal(
                "Errores Léxicos",
                mensaje.toString(),
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

public void analisisSintactico() {
        List<ErrorAlmacen> errores = probarSintactico();

        if (errores.isEmpty()) {
            mostrarReporteNoModal(
                "Resultado Sintáctico",
                "¡Análisis Sintáctico Correcto!\nLa estructura del código está bien definida.",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("Se encontraron errores de sintaxis:\n\n");

            for (ErrorAlmacen error : errores) {
                String etiquetaLinea = (error.getLine() > 0) ? String.valueOf(error.getLine()) : "?";
                mensaje.append(" > [Línea ").append(etiquetaLinea).append("]: ")
                        .append(error.getValue())
                        .append("\n");
            }

            // Usamos el método no modal
            mostrarReporteNoModal(
                "Errores Sintácticos",
                mensaje.toString(),
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }


    //agregado para errores
    public List<ErrorAlmacen> probarLex() {
        listaTokens.clear();
        List<ErrorAlmacen> lista = new ArrayList<>();

        // 1. Escritura del archivo
        File archivo = new File("archivo.txt");
        PrintWriter writer;
        try {
            writer = new PrintWriter(archivo);
            writer.print(jtpCodigo.getText());
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IDE.class.getName()).log(Level.SEVERE, null, ex);
            // CORRECCIÓN 1: No retornamos String, agregamos un objeto de error a la lista
            lista.add(new ErrorAlmacen("Error crítico: No se pudo escribir el archivo", Tokens.ERROR, 0, 0));
            return lista;
        }

        // 2. Lectura y Análisis
        try {
            Reader reader = new BufferedReader(new FileReader("archivo.txt"));
            Lexer lexer = new Lexer(reader);

            while (true) {
                TokenAlmacen token = lexer.nextToken();
                listaTokens.add(token);

                if (token.getType() == Tokens.EOF) {
                    break;
                }

                if (token.getType() == Tokens.ERROR) {
                    // CORRECCIÓN 2: Agregamos el error a la lista
                    // Nota: Usamos token.getValue() para saber QUÉ caracter falló
                    lista.add(new ErrorAlmacen(
                            "Error Léxico en Línea " + token.getLine() + ", Columna " + token.getColumn() + ". " + token.getValue(),
                            Tokens.ERROR,
                            token.getLine(),
                            token.getColumn()
                    ));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(IDE.class.getName()).log(Level.SEVERE, null, ex);
            // CORRECCIÓN 3: Manejo de excepción como objeto ErrorAlmacen
            lista.add(new ErrorAlmacen("Error excepción IO: " + ex.getMessage(), Tokens.ERROR, 0, 0));
        }

        //el return final va fuera de todos los try/catch y while
        return lista;
    }

    //método para la tabla de reservadas
    public void llenarTablaReservadas() {
        // Frame emergente
        JFrame frame = new JFrame("Palabras Reservadas");
        frame.setSize(350, 300);
        frame.setLocationRelativeTo(null);

        //creación de tabla
        String[] columnas = {"Palabra Reservada"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        //crear una lista temporal
        List<String> listaPalabras = new ArrayList<>();

        //llenar la lista con los tokens
        for (Tokens t : Tokens.values()) {
            if (t.name().startsWith("KW_")) {
                listaPalabras.add(t.name().substring(3).toLowerCase());
            }
        }

        //usar sort para ordenar la lista
        Collections.sort(listaPalabras);

        //pasar la lista ordenada a la tabla
        for (String palabra : listaPalabras) {
            modelo.addRow(new Object[]{palabra});
        }

        /* //obtener los valores de la clase token
        for (Tokens t : Tokens.values()) {
            if (t.name().startsWith("KW_")) {
                // quitar kw_ de la palabra
                modelo.addRow(new Object[]{t.name().substring(3).toLowerCase()});
            }
        }
         */
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        frame.add(scroll);
        frame.setVisible(true);
    }

    //para llenar la tabla de tokens
    private void llenarTablaTokens() {
        JFrame frame = new JFrame("Tokens recopilados:");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        String[] columnas = {"Lexema", "Token", "Línea", "Columna"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (TokenAlmacen t : listaTokens) {
            // Solo procesar tokens que no sean ERROR ni EOF
            if (t.getType() == Tokens.ERROR || t.getType() == Tokens.EOF) {
                continue;
            }

            String categoria = obtenerComponente(t);
            modelo.addRow(new Object[]{
                t.getValue(),
                categoria,
                t.getLine(),
                t.getColumn()
            });
        }

        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        frame.add(scroll);
        frame.setVisible(true);
    }

    private String obtenerCategoriaGeneral(TokenAlmacen t) {
        Tokens tipo = t.getType();
        String lexema = t.getValue();

        // 1. PALABRAS RESERVADAS (todas las que comienzan con KW_)
        if (tipo.name().startsWith("KW_")) {
            return "Palabra reservada";
        }

        // 2. IDENTIFICADORES
        if (tipo == Tokens.Identificador) {
            return "Identificador";
        }

        // 3. OPERADORES
        // Operadores aritméticos
        if (tipo == Tokens.Suma || tipo == Tokens.Resta
                || tipo == Tokens.Multiplicacion || tipo == Tokens.Division
                || tipo == Tokens.Modulo) {
            return "Operador Aritmetico";
        }

        // Operadores relacionales
        if (tipo == Tokens.opRel_ig || tipo == Tokens.opRel_dif
                || tipo == Tokens.opRel_menIgual || tipo == Tokens.opRel_mayIgual
                || tipo == Tokens.opRel_men || tipo == Tokens.opRel_may) {
            return "Operador Relacional";
        }

        // Operadores lógicos
        if (tipo == Tokens.opAnd || tipo == Tokens.opOr || tipo == Tokens.opNot) {
            return "Operador logico";
        }

        // Operador de asignación
        if (tipo == Tokens.Igual) {
            return "Asignacion";
        }

        // 4. NÚMEROS (constantes numéricas)
        if (tipo == Tokens.Numero || tipo == Tokens.NumeroDecimal) {
            return "Número";
        }

        // 5. CADENAS (constantes de caracteres)
        if (tipo == Tokens.CADENA) {
            return "Cadena";
        }

        // Notas musicales (también son constantes de caracteres)
        if (tipo == Tokens.NOTE) {
            return "Nota";
        }

        // 6. SÍMBOLOS ESPECIALES (delimitadores y separadores)
        // Delimitadores
        if (tipo == Tokens.PuntoComa || tipo == Tokens.Coma
                || tipo == Tokens.DosPuntos) {
            return "Símbolo especial";
        }

        // Paréntesis
        if (tipo == Tokens.ParenAper || tipo == Tokens.ParenCierre) {
            return "Símbolo especial";
        }

        // Llaves
        if (tipo == Tokens.LlaveAper || tipo == Tokens.LlaveCierre) {
            return "Símbolo especial";
        }

        // Corchetes
        if (tipo == Tokens.CorcheteAper || tipo == Tokens.CorcheteCierre) {
            return "Símbolo especial";
        }

        // Para cualquier token no categorizado, mostrar su nombre
        return tipo.name();
    }

    //se modificó el anterior método, ahora no retorna string si no una lista, se añadió una ER para obtner el número de línea
    // Método modificado para capturar errores Sintácticos y Semánticos
    public List<ErrorAlmacen> probarSintactico() {
        List<ErrorAlmacen> lista = new ArrayList<>();
        String input = jtpCodigo.getText();

        // Asumo que LexerCup es tu lexer para CUP
        sintax s = new sintax(new LexerCup(new StringReader(input)));

        try {
            s.parse();

            // --- 1. RECOLECTAR ERRORES SINTÁCTICOS ---
            ArrayList<String> erroresParser = s.getErrores();
            Pattern p = Pattern.compile("(?i)(?:l.nea|line)\\s*(\\d+)"); // ER para buscar el número de línea

            if (erroresParser != null && !erroresParser.isEmpty()) {
                for (String err : erroresParser) {
                    int lineaDetectada = 0; 
                    Matcher m = p.matcher(err);
                    if (m.find()) {
                        lineaDetectada = Integer.parseInt(m.group(1));
                    }
                    lista.add(new ErrorAlmacen(err, Tokens.ERROR, lineaDetectada, 0));
                }
            }

            // --- 2. RECOLECTAR ERRORES SEMÁNTICOS ---
            ArrayList<String> erroresSemanticos = s.getErroresSemanticos();
            
            if (erroresSemanticos != null && !erroresSemanticos.isEmpty()) {
                for (String errSem : erroresSemanticos) {
                    int lineaDetectada = 0; 
                    Matcher m = p.matcher(errSem);
                    if (m.find()) {
                        lineaDetectada = Integer.parseInt(m.group(1));
                    }
                    // Agregamos a la misma lista para mostrarlos todos juntos
                    lista.add(new ErrorAlmacen(errSem, Tokens.ERROR, lineaDetectada, 0));
                }
            }

            // >>> NUEVO: MOSTRAR C3D SI LA COMPILACIÓN FUE EXITOSA <<<
            // Si la lista de errores sigue vacía, significa que el código es perfecto.
            // Entonces mandamos llamar a la nueva ventana pasándole el parser 's'
            if (lista.isEmpty()) {
                mostrarCodigoIntermedio(s);
            }

        } catch (Exception ex) {
            // En caso de error fatal del parser
            lista.add(new ErrorAlmacen("Error fatal: " + ex.getMessage(), Tokens.ERROR, 0, 0));
        }

        return lista;
    }
    

    // Método auxiliar para inferir el tipo de dato según el valor
    private String inferirTipoDato(String valor, Map<String, Object[]> infoIdentificadores) {
        if (valor == null || valor.equals("null")) return "Indefinido";
        valor = valor.trim();

        // 1. Funciones de entrada (leer siempre devuelve texto)
        if (valor.startsWith("leer") || valor.contains("leer(")) return "cadena";

        // 2. Booleanos
        if (valor.contains("true") || valor.contains("false")) return "booleano";

        // 3. Cadenas explícitas
        if (valor.contains("\"")) return "cadena";

        // 4. Notas musicales
        if (valor.toLowerCase().contains("nota ")) return "nota";

        // 5. Listas / Arreglos
        if (valor.startsWith("[")) return "lista";

        // 6. Operaciones matemáticas (Si detecta +, -, *, /, asume número)
        if (valor.matches(".*[\\+\\-\\*\\/].*")) {
            // Si la operación involucra puntos decimales, es flotante
            if (valor.contains(".")) return "decimal";
            return "entero"; 
        }

        // 7. Números directos (Enteros o Decimales)
        if (valor.matches("-?\\d+(\\.\\d+)?")) {
            if (valor.contains(".")) return "decimal";
            return "entero";
        }

        // 8. Búsqueda en variables ya registradas (Asignación de variable a variable)
        // Limpiamos los espacios para comparar
        String valorLimpio = valor.replaceAll("\\s+", "");
        for (Map.Entry<String, Object[]> entry : infoIdentificadores.entrySet()) {
            if (valorLimpio.equals(entry.getKey())) {
                return (String) entry.getValue()[2]; // Retorna el tipo de la variable previa
            }
        }

        return "Desconocido";
    }

    private void generarTablaIdentificadoresCompleta() {
        JFrame frame = new JFrame("Tabla de Símbolos (Tipos Dinámicos)");
        frame.setSize(950, 500);
        frame.setLocationRelativeTo(null);

        String[] columnas = {"Nombre (ID)", "Tipo de Dato", "Valor / Contenido", "Línea", "Columna"};
        
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Map<String, Object[]> infoIdentificadores = new LinkedHashMap<>();

        for (int i = 0; i < listaTokens.size(); i++) {
            TokenAlmacen t = listaTokens.get(i);

            if (t.getType() == Tokens.Identificador) {
                String lexema = t.getValue();
                
                // --- 1. DETECCIÓN DE CONTEXTO (Palabra anterior) ---
                String contexto = "Indefinido";
                if (i > 0) {
                    TokenAlmacen anterior = listaTokens.get(i - 1);
                    
                    // Manejo de secciones (saltando los dos puntos)
                    if (anterior.getType() == Tokens.DosPuntos && i > 1) {
                        if (listaTokens.get(i - 2).getValue().equalsIgnoreCase("seccion")) {
                            contexto = "seccion";
                        }
                    } else {
                        contexto = anterior.getValue();
                    }
                }

                // --- 2. DETECCIÓN DE VALOR (Mirando adelante) ---
                String valorDetectado = "null";
                if (i + 1 < listaTokens.size()) {
                    TokenAlmacen siguiente = listaTokens.get(i + 1);

                    // A) Asignación simple (=) -> Captura hasta ';'
                    if (siguiente.getType() == Tokens.Igual) {
                        StringBuilder sb = new StringBuilder();
                        int k = i + 2; 
                        while (k < listaTokens.size()) {
                            TokenAlmacen tokenActual = listaTokens.get(k);
                            if (tokenActual.getType() == Tokens.PuntoComa) break; 
                            sb.append(tokenActual.getValue()).append(" ");
                            k++;
                        }
                        valorDetectado = sb.toString().trim();
                    } 
                    
                    // B) Definición de parámetros (...) -> Captura hasta ')'
                    else if (siguiente.getType() == Tokens.ParenAper) {
                         StringBuilder sb = new StringBuilder();
                         sb.append("( "); // Agregamos el paréntesis inicial visualmente
                         int k = i + 2; // Empezamos después del '('
                         while (k < listaTokens.size()) {
                            TokenAlmacen tokenActual = listaTokens.get(k);
                            sb.append(tokenActual.getValue()).append(" ");
                            if (tokenActual.getType() == Tokens.ParenCierre) break; // Paramos al cerrar
                            k++;
                         }
                         valorDetectado = sb.toString().trim();
                    }
                    
                    // C) Definición de bloques {...}
                    else if (siguiente.getType() == Tokens.LlaveAper) {
                         // EXCEPCIÓN: Si es una SECCIÓN, usamos el texto corto
                         if (contexto.equals("seccion")) {
                             valorDetectado = "Bloque de código {...}";
                         } 
                         // Si es melodía, secuencia, intro, etc., capturamos todo el contenido
                         else {
                             StringBuilder sb = new StringBuilder();
                             sb.append("{ "); 
                             int k = i + 2; 
                             while (k < listaTokens.size()) {
                                TokenAlmacen tokenActual = listaTokens.get(k);
                                sb.append(tokenActual.getValue()).append(" ");
                                if (tokenActual.getType() == Tokens.LlaveCierre) break; 
                                k++;
                             }
                             valorDetectado = sb.toString().trim();
                         }
                    }
                } // Fin de detección de valor

                // >>> NUEVO: PASO 2.5. EVALUAR LA OPERACIÓN MATEMÁTICA <<<
                // Si el valor no es nulo, lo pasamos por nuestro súper evaluador
                if (!valorDetectado.equals("null")) {
                    valorDetectado = resolverValor(valorDetectado, infoIdentificadores);
                }

                // --- 3. INFERENCIA DEL TIPO FINAL ---
                String tipoFinal = contexto; 

                // CASO A: Variables definidas con 'definir' -> Inferimos por su valor
                if (contexto.equals("definir")) {
                    // PASAMOS EL MAPA 'infoIdentificadores' AL NUEVO MÉTODO
                    tipoFinal = inferirTipoDato(valorDetectado, infoIdentificadores);
                }
                
                // CASO B: Estructuras del Lenguaje (Clases de NotaScript)
                switch (contexto.toLowerCase()) {
                    case "melodia":  tipoFinal = "Estructura (Melodía)"; break;
                    case "acorde":   tipoFinal = "Estructura (Acorde)"; break;
                    case "secuencia": tipoFinal = "Estructura (Secuencia)"; break;
                    case "seccion":  tipoFinal = "Sección de Código"; break;
                }

                // --- ALMACENAMIENTO ---
                if (!infoIdentificadores.containsKey(lexema)) {
                    infoIdentificadores.put(lexema, new Object[]{
                        t.getLine(), t.getColumn(), tipoFinal, valorDetectado
                    });
                } else {
                    Object[] datosExistentes = infoIdentificadores.get(lexema);
                    // Actualizamos si tenemos información más precisa
                    if ((datosExistentes[2].equals("Indefinido") || datosExistentes[2].equals("definir")) 
                            && !tipoFinal.equals("Indefinido") && !tipoFinal.equals("definir")) {
                         infoIdentificadores.put(lexema, new Object[]{
                            t.getLine(), t.getColumn(), tipoFinal, valorDetectado
                        });
                    }
                }
            }
        }

        // Llenar tabla visual
        for (Map.Entry<String, Object[]> entry : infoIdentificadores.entrySet()) {
            String lexema = entry.getKey();
            Object[] info = entry.getValue();
            modelo.addRow(new Object[]{ lexema, info[2], info[3], info[0], info[1] });
        }

        JTable tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(tabla);
        
        tabla.getColumnModel().getColumn(0).setPreferredWidth(150); // Nombre
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150); // Tipo
        tabla.getColumnModel().getColumn(2).setPreferredWidth(250); // Valor

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Símbolos encontrados: " + infoIdentificadores.size()));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);

        frame.add(panelPrincipal);
        frame.setVisible(true);
    }

    //método para la tabla de símbolos
    public void llenarTablaSimbolos() {

        String[] columnas = {"Lexema", "Componente Léxico"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        // usando la tabla con los tokens ya almacenados
        for (TokenAlmacen t : listaTokens) {

            // para ignorar el EOF
            if (t.getType() == Tokens.EOF) {
                continue;
            }

            if (t.getType() == Tokens.ERROR) {
                continue;
            }

            String lexema = t.getValue();
            String componente = obtenerComponente(t);
            //validacion para no agregar por si ya existe
            boolean existe = false;

            for (int i = 0; i < modelo.getRowCount(); i++) {
                if (modelo.getValueAt(i, 0).equals(lexema)) {
                    existe = true;
                    break;
                }
            }

            // si no existe, lo agrega
            if (!existe) {
                modelo.addRow(new Object[]{lexema, componente});
            }

        }

        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        JFrame vent = new JFrame("Tabla de símbolos");
        vent.add(scroll);
        vent.setSize(400, 400);
        vent.setLocationRelativeTo(null);
        vent.setVisible(true);
    }

//método para definir el componente léxico o grupo de cada lexema
    // Método CORREGIDO: Palabras reservadas generales, Símbolos específicos
    // Método DEFINITIVO: Switch explícito para CADA tipo de token
    public String obtenerComponente(TokenAlmacen t) {
        Tokens tipo = t.getType();

        switch (tipo) {
            case ERROR: 
                return "Error Léxico";

            // --- 1. VALORES Y LITERALES ---
            case Identificador: 
                return "Identificador";
            case Numero: 
                return "Número Entero";
            case NumeroDecimal: 
                return "Número Decimal";
            case CADENA: 
                return "Cadena";
            
            // --- 2. NOTAS MUSICALES 
            case NOTE:          // Formato regex (Do#5)
            case KW_DO:         // Palabra do
            case KW_RE:         // Palabra re
            case KW_MI:         // Palabra mi
            case KW_FA:         // Palabra fa
            case KW_SOL:        // Palabra sol
            case KW_LA:         // Palabra la
            case KW_SI_NOTE:    // Palabra si (nota) - Distinta al condicional
                return "Nota Musical";

            // --- 3. SÍMBOLOS DE AGRUPACIÓN ---
            case LlaveAper: 
                return "Llave de Apertura";
            case LlaveCierre: 
                return "Llave de Cierre";
            case ParenAper: 
                return "Paréntesis de Apertura";
            case ParenCierre: 
                return "Paréntesis de Cierre";
            case CorcheteAper: 
                return "Corchete de Apertura";
            case CorcheteCierre: 
                return "Corchete de Cierre";

            // --- 4. SIGNOS DE PUNTUACIÓN ---
            case PuntoComa: 
                return "Punto y Coma";
            case Coma: 
                return "Coma";
            case DosPuntos: 
                return "Dos Puntos";
            case Igual: 
                return "Asignación";

            // --- 5. OPERADORES ARITMÉTICOS ---
            case Suma: 
                return "Suma";
            case Resta: 
                return "Resta";
            case Multiplicacion: 
                return "Multiplicación";
            case Division: 
                return "División";

            // --- 6. OPERADORES LÓGICOS Y RELACIONALES ---
            case opRel_ig: 
                return "Igualdad";
            case opRel_dif: 
                return "Diferente";
            case opRel_men: 
                return "Menor que";
            case opRel_may: 
                return "Mayor que";
            case opRel_menIgual: 
                return "Menor o igual";
            case opRel_mayIgual: 
                return "Mayor o igual";
            case opAnd: 
                return "AND";
            case opOr: 
                return "OR";
            case opNot: 
                return "NOT";

            // --- 7. PALABRAS RESERVADAS ---
            case KW_NOTA:       return "Palabra Reservada"; 
            case KW_SI:         return "Palabra Reservada"; // El condicional 'si_'
            
            case KW_SECCION:    return "Palabra Reservada";
            case KW_FIN:        return "Palabra Reservada";
            case KW_MOSTRAR:    return "Palabra Reservada";
            case KW_LEER:       return "Palabra Reservada";
            case KW_DEFINIR:    return "Palabra Reservada";
            case KW_SINO:       return "Palabra Reservada";
            case KW_SEGUN:      return "Palabra Reservada";
            case KW_CASO:       return "Palabra Reservada";
            case KW_DEFECTO:    return "Palabra Reservada";
            case KW_REPETIR:    return "Palabra Reservada";
            case KW_VECES:      return "Palabra Reservada";
            case KW_MIENTRAS:   return "Palabra Reservada";
            case KW_ROMPER:     return "Palabra Reservada";
            case KW_CONTINUAR:  return "Palabra Reservada";
            case KW_TOCAR:      return "Palabra Reservada";
            case KW_SILENCIO:   return "Palabra Reservada";
            case KW_ACORDE:     return "Palabra Reservada";
            case KW_MELODIA:    return "Palabra Reservada";
            case KW_SECUENCIA:  return "Palabra Reservada";
            case KW_LISTA:      return "Palabra Reservada";
            case KW_TIEMPO:     return "Palabra Reservada";
            case KW_OCTAVA:     return "Palabra Reservada";
            case KW_COMPAS:     return "Palabra Reservada";
            case KW_TEMPO:      return "Palabra Reservada";
            case KW_TONO:       return "Palabra Reservada";
            case KW_ESCALA:     return "Palabra Reservada";
            case KW_MAYOR:      return "Palabra Reservada";
            case KW_MENOR:      return "Palabra Reservada";
            case KW_ADAGIO:     return "Palabra Reservada";
            case KW_ANDANTE:    return "Palabra Reservada";
            case KW_ALLEGRO:    return "Palabra Reservada";
            case KW_TRUE:       return "Palabra Reservada";
            case KW_FALSE:      return "Palabra Reservada";
            case EOF:           return "Fin de Archivo";

            default: 

                return "Token Desconocido: " + tipo.name();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IDE().setVisible(true);
            }
        });
    }

    //método para limpiar los componentes, en este caso el componente para escribir el código
    public void clearAllComp() {
        jtpCodigo.setText("");

    }
    
    // --- 1. MÉTODO PARA SUSTITUIR Y RESOLVER ---
    private String resolverValor(String valorCrudo, Map<String, Object[]> infoIdentificadores) {
        if (valorCrudo == null || valorCrudo.isEmpty()) return "";
        
        // Si es un texto puro, una entrada (leer) o una estructura musical, no tocamos nada
        if (valorCrudo.contains("\"") || valorCrudo.startsWith("leer") || valorCrudo.startsWith("{") || valorCrudo.startsWith("(")) {
            return valorCrudo;
        }

        // 1. Reemplazar nombres de variables por sus valores ya guardados
        String expresion = valorCrudo;
        for (Map.Entry<String, Object[]> entry : infoIdentificadores.entrySet()) {
            String varName = entry.getKey();
            String varValue = String.valueOf(entry.getValue()[3]); // El valor guardado
            
            // Reemplaza solo la palabra exacta para no arruinar otras cosas
            expresion = expresion.replaceAll("\\b" + varName + "\\b", varValue);
        }

        // 2. Intentar evaluar matemáticamente
        try {
            double resultado = evaluarMatematica(expresion);
            // Si el resultado es exacto (ej: 2.0), lo mostramos como entero (2)
            if (resultado == Math.floor(resultado)) {
                return String.valueOf((long) resultado);
            }
            return String.valueOf(resultado); // Si tiene decimales, lo dejamos
        } catch (Exception e) {
            // Si falla la matemática (porque no era una operación), devolvemos el texto reemplazado
            return expresion;
        }
    }

    // --- 2. MINI-INTÉRPRETE MATEMÁTICO (Descenso Recursivo) ---
    private double evaluarMatematica(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Inesperado: " + (char)ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // suma
                    else if (eat('-')) x -= parseTerm(); // resta
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplicación
                    else if (eat('/')) x /= parseFactor(); // división
                    else if (eat('%')) x %= parseFactor(); // residuo
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor(); 
                if (eat('-')) return -parseFactor(); 
                double x;
                int startPos = this.pos;
                if (eat('(')) { // resolver paréntesis
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // leer números
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Inesperado: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }
    
    // --- NUEVO MÉTODO PARA MOSTRAR EL CÓDIGO INTERMEDIO (C3D) ---
    private void mostrarCodigoIntermedio(comp.sintax parser) {
        // Verificamos que existan cuádruplos generados
        if (parser.genC3D == null || parser.genC3D.cuadruplos.isEmpty()) {
            return; // Si no hay nada, no mostramos la ventana
        }

        JFrame frameC3D = new JFrame("Código Intermedio (Cuádruplos)");
        frameC3D.setSize(600, 400);
        frameC3D.setLocationRelativeTo(null); // Centrar en pantalla

        // Nombres de las columnas del Cuádruplo
        String[] columnas = {"Operador", "Argumento 1", "Argumento 2", "Resultado (Temporal)"};
        
        javax.swing.table.DefaultTableModel modeloC3D = new javax.swing.table.DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };

        // Extraemos la lista de cuádruplos de tu clase GeneradorC3D
        for (comp.Cuarteto c : parser.genC3D.cuadruplos) {
            modeloC3D.addRow(new Object[]{
                c.operador, 
                c.arg1, 
                c.arg2, 
                c.resultado
            });
        }

        javax.swing.JTable tablaC3D = new javax.swing.JTable(modeloC3D);
        tablaC3D.setAutoCreateRowSorter(true);
        
        // Ajustar anchos visuales
        tablaC3D.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaC3D.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaC3D.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaC3D.getColumnModel().getColumn(3).setPreferredWidth(150);

        javax.swing.JScrollPane scrollC3D = new javax.swing.JScrollPane(tablaC3D);
        frameC3D.add(scrollC3D);
        frameC3D.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu Archivo;
    private javax.swing.JMenuItem MenuAbrir;
    private javax.swing.JMenuItem MenuGuardar;
    private javax.swing.JMenuItem MenuNuevo;
    private javax.swing.JMenuItem MenuReservadas;
    private javax.swing.JMenuItem MenuTokens;
    private javax.swing.JMenu Tablas;
    private javax.swing.JButton btnCompilar;
    private javax.swing.JMenuItem btnSintactico;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jtaConsola;
    public javax.swing.JTextPane jtpCodigo;
    private javax.swing.JMenuItem menuID;
    // End of variables declaration//GEN-END:variables
}
