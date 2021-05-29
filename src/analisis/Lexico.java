package analisis;

import static interfaces.VtnCompilador.editorPaneSalida;
import java.awt.Color;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTextPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import recursos.TextPaneTest;

public class Lexico {

    //Inicio de declaracion de atributos
    private String textoCodigo;
    private String separadores;
    private String pathTablaExcel;
    private int[][] matrizTransaccionGeneral;
    private char[] vecAlfabeto;
    private String[][] conjuntoTokensFijos;
    private String[][] conjuntoTokensAutomatas;
    private String[][] conjuntoErrores;
    private ArrayList<Lexema> listaLexico;
    private int[] numRenglon;
    //Fin de declaración de atributos

    public Lexico() {
//        System.out.println("");
    }

    /**
     * Constructor de clase
     *
     * @param textoCodigo - String: Texto a analizar
     * @param separadores - String: Separadores naturales
     * @param pathTablaExcel - String: Direccion en donde se obtiene la tabla,
     * fila 1- primera linea Alfabeto, columna 1- Estados del 0 al ...
     */
    public Lexico(String textoCodigo, String separadores, String pathTablaExcel) {
        this.textoCodigo = textoCodigo;
        this.separadores = separadores;
        this.pathTablaExcel = pathTablaExcel;
        extraerTabla();
        this.listaLexico = new ArrayList<>();
    }

    /**
     * Constructor de clase
     *
     * @param textoCodigo - String: Texto a analizar
     * @param separadores - String: Separadores
     * @param matrizTransaccionGeneral - int[][] Matriz de transaccion general
     * @param vecAlfabeto -char [] Alfabeto
     */
    public Lexico(String textoCodigo, String separadores, int[][] matrizTransaccionGeneral, char[] vecAlfabeto) {
        this.textoCodigo = textoCodigo;
        this.separadores = separadores;
        this.matrizTransaccionGeneral = matrizTransaccionGeneral;
        this.vecAlfabeto = vecAlfabeto;
        this.listaLexico = new ArrayList<>();
    }

    /**
     * Get the value of pathTablaExcel
     *
     * @return the value of pathTablaExcel
     */
    public String getPathTablaExcel() {
        return pathTablaExcel;
    }

    /**
     * Set the value of pathTablaExcel
     *
     * @param pathTablaExcel new value of pathTablaExcel
     */
    public void setPathTablaExcel(String pathTablaExcel) {
        this.pathTablaExcel = pathTablaExcel;
    }

    /**
     * Get the value of conjuntoErrores
     *
     * @return the value of conjuntoErrores
     */
    public String[][] getConjuntoErrores() {
        String listaErrores[][] = {
            {"Caracter desconocido", "104", "E1"},
            {"Nombre de clase no valido", "104", "E4"},
            {"Nombre de metodo no valido", "105", "E5"},
            {"Nombre de variable no valida", "100", "E6"},
            {"Numero entero incorrecto", "101", "E9"},
            {"Numero de flotante incorrectos", "102", "E10"},
            {"Cadena no valida", "103", "E11"},
            {"Comentario de linea no valida", "150", "E4"},
            {"Comentario de bloque no valida", "150", "E15"},};

        this.conjuntoErrores = listaErrores;
        return conjuntoErrores;
    }

    /**
     * Set the value of conjuntoErrores
     *
     * @param conjuntoErrores new value of conjuntoErrores
     */
    public void setConjuntoErrores(String[][] conjuntoErrores) {
        this.conjuntoErrores = conjuntoErrores;
    }

    /**
     * Get the value of conjuntoTokensAutomatas
     *
     * @return the value of conjuntoTokensAutomatas
     */
    public String[][] getConjuntoTokensAutomatas() {
        String[][] listaTokensAutomatas = {
            {"Nombre de clase", "54"},
            {"Nombre de metodo", "55"},
            {"Nombre de variable", "50"},
            {"Numero entero", "51"},
            {"Numero flotante", "52"},
            {"Cadena", "53"},
            {"Comentario de linea", "59"},
            {"Comentario de bloque", "60"}
        };
        this.conjuntoTokensAutomatas = listaTokensAutomatas;
        return listaTokensAutomatas;
    }

    /**
     * Set the value of conjuntoTokensAutomatas
     *
     * @param conjuntoTokensAutomatas new value of conjuntoTokensAutomatas
     */
    public void setConjuntoTokensAutomatas(String[][] conjuntoTokensAutomatas) {
        this.conjuntoTokensAutomatas = conjuntoTokensAutomatas;
    }

    /**
     * Get the value of conjuntoTokensFijos
     *
     * @return the value of conjuntoTokensFijos
     */
    public String[][] getConjuntoTokensFijos() {
        String[][] listaTokensFijos = {
            {"class", "Palabra reservada: clase", "16", "class"},
            {"return", "Palabra reservada: retorno", "19", "return"},
            {"void", "Palabra reservada: funcion", "21", "void"},
            {"Inicio", "Palabra reservada: principal", "16", "Inicio"},
            {"scanf", "Palabra reservada: leer", "13", "scanf"},
            {"printf", "Palabra reservada: imprimir", "14", "printf"},
            {"if", "Estructura de seleccion: si", "6", "if"},
            {"else", "Estructura de seleccion: sino", "11", "else"},
            {"while", "Cliclo: mientras", "12", "while"},
            {"true", "Palabra reservada: verdadero", "22", "true"},
            {"false", "Palabra reservada: falso", "23", "false"},
            {"int", "Tipo de dato: entero", "1", "int"},
            {"float", "Tipo de dato: flotante", "1", "float"},
            {"String", "Tipo de dato: cadena", "1", "String"},
            {"boolean", "Tipo de dato: booleano", "1", "boolean"},
            {"+", "Operador aritmetico suma", "4", "+"},
            {"-", "Operador aritmetico resta", "4", "-"},
            {"*", "Operador aritmetico producto", "4", "*"},
            {"/", "Operador artimetico cociente", "4", "/"},
            {"%", "Operador artimetico residuo de cociente", "4", "%"},
            {"^", "Operador aritmetico potencia", "4", "^"},
            {"<", "Opererador relacional: menor que", "5", "<"},
            {">", "Operador relacional: mayor que", "5", ">"},
            {">=", "Operador relacional: mayor igual que", "5", ">="},
            {"<=", "Operador relacional: menor igual que", "5", "<="},
            {"==", "Operador relacional: comparacion igual", "5", "=="},
            {"!=", "Operador relaciona: diferente que", "5", "!="},
            {"=", "Asignacion: Igual", "53", "="},
            {"&&", "Operador lógico: Y ", "18", "&&"},
            {"||", "Operador lógico: O", "18", "||"},
            {"!", "Operador lógico: No", "18", "!"},
            {",", "Caracter especial: Coma", "15", ","},
            {";", "Caracter especial: Punto y coma", "2", ";"},
            {".", "Caracter especial: Punto", "3", "."},
            {":", "Caracter especial: Dos puntos", "3", ":"},
            {"{", "Caracter especial: Llave que abre", "9", "{"},
            {"}", "Caracter especial: Llave que cierra", "10", "}"},
            {"[", "Caracter especial: Corchete que abre", "30", "["},
            {"]", "Caracter especial: Corchete que cierra", "31", "]"},
            {"(", "Caracter especial: Parentesis que abre", "7", "("},
            {")", "Caracter especial: Parenctesis que cierra", "8", ")"}};
        this.conjuntoTokensFijos = listaTokensFijos;
        return listaTokensFijos;
    }

    /**
     * Set the value of conjuntoTokensFijos
     *
     * @param conjuntoTokensFijos new value of conjuntoTokensFijos
     */
    public void setConjuntoTokensFijos(String[][] conjuntoTokensFijos) {
        this.conjuntoTokensFijos = conjuntoTokensFijos;
    }

    /**
     * Get the value of listaLexico
     *
     * @return the value of listaLexico
     */
    public ArrayList<Lexema> getListaLexico() {
        return listaLexico;
    }

    /**
     * Set the value of listaLexico
     *
     * @param listaLexico new value of listaLexico
     */
    public void setListaLexico(ArrayList<Lexema> listaLexico) {
        this.listaLexico = listaLexico;
    }

    /**
     * Get the value of vecAlfabeto
     *
     * @return the value of vecAlfabeto
     */
    public char[] getVecAlfabeto() {
        return vecAlfabeto;
    }

    /**
     * Set the value of vecAlfabeto
     *
     * @param vecAlfabeto new value of vecAlfabeto
     */
    public void setVecAlfabeto(char[] vecAlfabeto) {
        this.vecAlfabeto = vecAlfabeto;
    }

    /**
     * Get the value of separadores
     *
     * @return the value of separadores
     */
    public String getSeparadores() {
        return separadores;
    }

    /**
     * Set the value of separadores
     *
     * @param separadores new value of separadores
     */
    public void setSeparadores(String separadores) {
        this.separadores = separadores;
    }

    /**
     * Get the value of matrizTransaccionGeneral
     *
     * @return the value of matrizTransaccionGeneral
     */
    public int[][] getMatrizTransaccionGeneral() {
        return matrizTransaccionGeneral;
    }

    /**
     * Set the value of matrizTransaccionGeneral
     *
     * @param matrizTransaccionGeneral new value of matrizTransaccionGeneral
     */
    public void setMatrizTransaccionGeneral(int[][] matrizTransaccionGeneral) {
        this.matrizTransaccionGeneral = matrizTransaccionGeneral;
    }

    /**
     * Get the value of textoCodigo
     *
     * @return the value of textoCodigo
     */
    public String getTextoCodigo() {
        return textoCodigo;
    }

    /**
     * Set the value of textoCodigo
     *
     * @param textoCodigo new value of textoCodigo
     */
    public void setTextoCodigo(String textoCodigo) {
        this.textoCodigo = textoCodigo;
    }

    /**
     * Método que realiza el analisis léxico del código Proceso: Toma el texto
     * completo del código, lo envia a otro método en donde lo separa, se crea
     * un vector en donde se conservan los numeros del renglon, se busca en la
     * tabla de tokens fijos, si la encuentra, la etiqueta, sino, se envia a la
     * busqueda de los tokens de los automatas, con la referencia de la tabla de
     * transacción del automata general, si el automata es correcto entonces lo
     * etiqueta con el nombre del automata sino se le coloca el error, se toman
     * como referencia la tabla de errores despues de que se han recorrido todos
     * los lexemas se recorren una vez más para colocar el numero del renglon.
     *
     * @return una lista de Lexemas, como analisis lexico contienen toda la
     * información para saber si es lexicamente correcto o no
     */
//    public ArrayList<Lexema> analisisLexico() {
//
//        ArrayList<String> lexemas = separaCodigo();
//        buscarTokensFijos(lexemas);
//        for (int i = 0; i < getListaLexico().size(); i++) {
//            getListaLexico().get(i).setRenglon(numRenglon[i]);
//        }
//        //Corrgige el punto decimal si es un numero
//        for (int i = 0; i < getListaLexico().size(); i++) {
//            if (getListaLexico().get(i).getNumToken() == 51) {
//                if (i + 1 < getListaLexico().size()) {
//                    if ((getListaLexico().get(i + 1).getNumToken() == 3) && (getListaLexico().get(i).getRenglon() == getListaLexico().get(i + 1).getRenglon())) {
//                        if (i + 2 < getListaLexico().size()) {
//                            if ((getListaLexico().get(i + 2).getNumToken() == 52)
//                                    && (getListaLexico().get(i).getRenglon() == getListaLexico().get(i + 2).getRenglon())) {
//                                Lexema num2 = getListaLexico().remove(i + 2);
//                                Lexema pun = getListaLexico().remove(i + 1);
//                                String s = getListaLexico().get(i).getLexema() + pun.getLexema() + num2.getLexema();
//                                getListaLexico().get(i).setLexema(s);
//                                getListaLexico().get(i).setNumToken(52);
//                                getListaLexico().get(i).setNombreToken("Numero flotante");
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        for (int i = 0; i < getListaLexico().size(); i++) {
//            if (getListaLexico().get(i).getNumToken() == 51 || getListaLexico().get(i).getNumToken() == 52) {
//                if (getListaLexico().get(i).getLexema().startsWith("-")) {
//                    if ((i - 1) > 0 && (i - 1) < getListaLexico().size()) {
//                        if (getListaLexico().get(i - 1).getNumToken() == 51 || getListaLexico().get(i - 1).getNumToken() == 50 || getListaLexico().get(i - 1).getNumToken() == 52|| getListaLexico().get(i - 1).getNumToken() == 8) {
//                            String s = getListaLexico().get(i).getLexema().replaceFirst("-", "");
//                            Lexema lxMenos = new Lexema();
//                            lxMenos.setLexema("-");
//                            lxMenos.setNombreToken("Operador aritmetico menos");
//                            lxMenos.setNumToken(4);
//                            lxMenos.setRenglon(getListaLexico().get(i).getRenglon());
//
//                            Lexema lxNumero = new Lexema();
//                            lxNumero.setLexema(s);
//                            lxNumero.setRenglon(getListaLexico().get(i).getRenglon());
//                            lxNumero.setNombreToken("Numero entero");
//
//                            String s2 = Semantico.tipoDato(s);
//                            if (s2 == "INTEGER") {
//                                lxNumero.setNumToken(51);
//                            } else {
//                                lxNumero.setNumToken(52);
//                            }
//                            getListaLexico().remove(i);
//                            getListaLexico().add(i, lxMenos);
//                            getListaLexico().add(i+1, lxNumero);
//                            
//                        }
//                    }
//                }
//
//            }
//        }
//        //Quita los comentarios para el sintactico y agrega los identificadores a al tabla de tokens
//        return getListaLexico();
//    }
    public ArrayList<Lexema> analisisLexico() {

        ArrayList<String> lexemas = separaCodigo();
        buscarTokensFijos(lexemas);
        for (int i = 0; i < getListaLexico().size(); i++) {
            getListaLexico().get(i).setRenglon(numRenglon[i]);
        }
//Corrgige el punto decimal si es un numero
        for (int i = 0; i < getListaLexico().size(); i++) {
            if (getListaLexico().get(i).getNumToken() == 51) {
                if (i + 1 < getListaLexico().size()) {
                    if ((getListaLexico().get(i + 1).getNumToken() == 3) && (getListaLexico().get(i).getRenglon() == getListaLexico().get(i + 1).getRenglon())) {
                        if (i + 2 < getListaLexico().size()) {
                            if ((getListaLexico().get(i + 2).getNumToken() == 52)
                                    && (getListaLexico().get(i).getRenglon() == getListaLexico().get(i + 2).getRenglon())) {
                                Lexema num2 = getListaLexico().remove(i + 2);
                                Lexema pun = getListaLexico().remove(i + 1);
                                String s = getListaLexico().get(i).getLexema() + pun.getLexema() + num2.getLexema();
                                getListaLexico().get(i).setLexema(s);
                                getListaLexico().get(i).setNumToken(52);
                                getListaLexico().get(i).setNombreToken("Numero flotante");
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < getListaLexico().size(); i++) {
            if (getListaLexico().get(i).getNumToken() == 51 || getListaLexico().get(i).getNumToken() == 52) {
                if (getListaLexico().get(i).getLexema().startsWith("-")) {
                    if ((i - 1) > 0 && (i - 1) < getListaLexico().size()) {
                        if (getListaLexico().get(i - 1).getNumToken() == 51 || getListaLexico().get(i - 1).getNumToken() == 50 || getListaLexico().get(i - 1).getNumToken() == 52 || getListaLexico().get(i - 1).getNumToken() == 8) {
                            String s = getListaLexico().get(i).getLexema().replaceFirst("-", "");
                            Lexema lxMenos = new Lexema();
                            lxMenos.setLexema("-");
                            lxMenos.setNombreToken("Operador aritmetico menos");
                            lxMenos.setNumToken(4);
                            lxMenos.setRenglon(getListaLexico().get(i).getRenglon());

                            Lexema lxNumero = new Lexema();
                            lxNumero.setLexema(s);
                            lxNumero.setRenglon(getListaLexico().get(i).getRenglon());
                            lxNumero.setNombreToken("Numero entero");

                            String s2 = Semantico.tipoDato(s);
                            if ("INTEGER".equals(s2)) {
                                lxNumero.setNumToken(51);
                            } else {
                                lxNumero.setNumToken(52);
                            }
                            getListaLexico().remove(i);
                            getListaLexico().add(i, lxMenos);
                            getListaLexico().add(i + 1, lxNumero);

                        }
                    }
                }
            }
        }

        for (int i = 0; i < getListaLexico().size(); i++) {

            if (getListaLexico().get(i).getLexema().equals("-")) {

                if (getListaLexico().get(i - 1).getNumToken() == 50 || getListaLexico().get(i - 1).getNumToken() == 51 || getListaLexico().get(i - 1).getNumToken() == 52 || getListaLexico().get(i - 1).getNumToken() == 53) {
// Var - 3
// 4 - 3
// "
                } else {
                    if (getListaLexico().get(i - 1).getLexema().equals("=") || getListaLexico().get(i - 1).getLexema().equals("(") || getListaLexico().get(i - 1).getNumToken() == 4) {
// = -3
// ( -3
                        getListaLexico().remove(i);
                        Lexema lxNumero = new Lexema();
                        lxNumero.setLexema("-".concat(getListaLexico().get(i).getLexema()));
                        lxNumero.setRenglon(getListaLexico().get(i).getRenglon());
                        if (lxNumero.getLexema().contains(".")) {
                            lxNumero.setNumToken(52);
                        } else {
                            lxNumero.setNumToken(51);
                        }
                        getListaLexico().set(i, lxNumero);
                    }
                }

            }
        }
//Corrgige el punto decimal si es un numero
        for (int i = 0; i < getListaLexico().size(); i++) {
            if (getListaLexico().get(i).getNumToken() == 51) {
                if (i + 1 < getListaLexico().size()) {
                    if ((getListaLexico().get(i + 1).getNumToken() == 3)) {
                        if (i + 2 < getListaLexico().size()) {
                            if ((getListaLexico().get(i + 2).getNumToken() == 51)) {
                                Lexema num2 = getListaLexico().remove(i + 2);
                                Lexema pun = getListaLexico().remove(i + 1);
                                String s = getListaLexico().get(i).getLexema() + pun.getLexema() + num2.getLexema();
                                getListaLexico().get(i).setLexema(s);
                                getListaLexico().get(i).setNumToken(52);
                                getListaLexico().get(i).setNombreToken("Numero flotante");
                            }
                        }
                    }
                }
            } else if (getListaLexico().get(i).getNumToken() == 52) {
                if (i + 1 < getListaLexico().size()) {
                    if ((getListaLexico().get(i + 1).getNumToken() == 3)) {
                        if (i + 2 < getListaLexico().size()) {
                            if ((getListaLexico().get(i + 2).getNumToken() == 51)) {
                                Lexema num2 = getListaLexico().remove(i + 2);
                                Lexema pun = getListaLexico().remove(i + 1);
                                String s = getListaLexico().get(i).getLexema() + pun.getLexema() + num2.getLexema();
                                getListaLexico().get(i).setLexema(s);
                                getListaLexico().get(i).setNumToken(52);
                                getListaLexico().get(i).setNombreToken("Numero flotante");
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < getListaLexico().size(); i++) {
            if (getListaLexico().get(i).getLexema().startsWith("-")) {
                if (getListaLexico().get(i).getLexema().length() == 1) {

                } else {
                    String flotante = getListaLexico().get(i).getLexema().substring(1, getListaLexico().get(i).getLexema().length());
              //      System.out.println(flotante);
                    if (flotante.startsWith("-")) {
                        Lexema m1 = new Lexema();
                        m1.setLexema("-");
                        m1.setNumToken(4);
                        Lexema m2 = new Lexema();
                        m2.setLexema(getListaLexico().get(i).getLexema().substring(1, getListaLexico().get(i).getLexema().length()));
                        m2.setNumToken(4);
                        getListaLexico().set(i, m1);
                        getListaLexico().add(i + 1, m2);
                        i = 0;
                    } else {

                    }

                }

            }
        }
        for (int i = 0; i < getListaLexico().size(); i++) {

            if (getListaLexico().get(i).getLexema().equals("-")) {

                if (getListaLexico().get(i - 1).getNumToken() == 50 || getListaLexico().get(i - 1).getNumToken() == 51 || getListaLexico().get(i - 1).getNumToken() == 52 || getListaLexico().get(i - 1).getNumToken() == 53) {
// Var - 3
// 4 - 3
// "
                } else {
                    if (getListaLexico().get(i - 1).getLexema().equals("=") || getListaLexico().get(i - 1).getLexema().equals("(") || getListaLexico().get(i - 1).getNumToken() == 4) {
// = -3
// ( -3
                        getListaLexico().remove(i);
                        Lexema lxNumero = new Lexema();
                        lxNumero.setLexema("-".concat(getListaLexico().get(i).getLexema()));
                        lxNumero.setRenglon(getListaLexico().get(i).getRenglon());
                        if (lxNumero.getLexema().contains(".")) {
                            lxNumero.setNumToken(52);
                        } else {
                            lxNumero.setNumToken(51);
                        }
                        getListaLexico().set(i, lxNumero);
                    }
                }

            }
        }

        //Quita los comentarios para el sintactico y agrega los identificadores a al tabla de tokens
        return getListaLexico();
    }

    /**
     * Método para separar el código
     *
     * @return
     */
    private ArrayList<String> separaCodigo() {

        ArrayList<String> palabras = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(getTextoCodigo(), "+ =*&| {}().[]-^/%;:,<>\n\t\r!\" ", true);
        String cadena;
        int contRenglon = 1; //incrementa las lineas de codigo
        int contRenglon2 = 1; //incrementa las lineas de codigo
        boolean aux1;
        boolean auxRe;

        while (st.hasMoreElements()) {
            String aux = "";
            aux1 = false;
            auxRe = false;
            cadena = st.nextToken();
            if (cadena.equals(" ") || cadena.equals("\n") || cadena.equals("\t") || cadena.equals("\r")) {

                if (cadena.equals("\n")) {
                    contRenglon++;
                }
            } else {

                switch (cadena) {
                    case "+":
                        if (st.hasMoreElements()) {
                            aux = st.nextToken();
                            switch (aux) {
                                case "+":
                                    cadena += aux;
                                    break;
                                case "=":
                                    cadena += aux;
                                    break;
                                case " ":
                                    break;
                                default:
                                    aux1 = true;
                                    break;
                            }
                        }
                        break;
                    case "-":
                        if (st.hasMoreElements()) {
                            aux = st.nextToken();
                            boolean bh = false;
                            try {

                                int aux32 = Integer.parseInt(aux);
                                bh = true;
                            } catch (NumberFormatException e) {

                            }
                            if (aux.equals("=")) {
                                cadena += aux;
                            } else if (aux.equals("-")) {
                                cadena += aux;
                            } else if (bh) {
                                cadena += aux;
                            } else if (aux.equals(" ")) {

                            } else {
                                aux1 = true;
                            }
                        }
                        break;
                    case "*":
                        if (st.hasMoreElements()) {
                            aux = st.nextToken();
                            switch (aux) {
                                case "=":
                                    cadena += aux;
                                    break;
                                case "/":
                                    cadena += aux;
                                    break;
                                case " ":
                                    break;
                                default:
                                    aux1 = true;
                                    break;
                            }
                        }
                        break;
                    case "=":
                        if (st.hasMoreElements()) {
                            aux = st.nextToken();
                            switch (aux) {
                                case "=":
                                    cadena += aux;
                                    break;
                                case " ":
                                    break;
                                default:
                                    aux1 = true;
                                    break;
                            }
                        }
                        break;
                    case "&":
                        if (st.hasMoreElements()) {
                            aux = st.nextToken();
                            switch (aux) {
                                case "&":
                                    cadena += aux;
                                    break;
                                case " ":
                                    break;
                                default:
                                    cadena += aux;
                                    break;
                            }

                        }
                        break;
                    case "|":
                        if (st.hasMoreElements()) {
                            aux = st.nextToken();
                            switch (aux) {
                                case "|":
                                    cadena += aux;
                                    break;
                                case " ":
                                    break;
                                default:
                                    aux1 = true;
                                    break;
                            }
                        }
                        break;
                    case "!":
                        if (st.hasMoreElements()) {
                            aux = st.nextToken();
                            switch (aux) {
                                case "=":
                                    cadena += aux;
                                    break;
                                case " ":
                                    break;
                                default:
                                    aux1 = true;
                                    break;
                            }
                        }
                        break;
                    case "/":
                        if (st.hasMoreElements()) {
                            auxRe = true;
                            contRenglon2 = contRenglon;
                            aux = st.nextToken();

                            switch (aux) {
                                case "*":
                                    cadena += aux;
                                    aux = "";
                                    while (st.hasMoreElements()) {
                                        aux = st.nextToken();
                                        if (aux.equals("*")) {

                                            cadena += aux;
                                            aux = "";
                                            if (st.hasMoreElements()) {
                                                aux = st.nextToken();
                                                if (aux.equals("/")) {
                                                    cadena += aux;
                                                    aux = "";
                                                    break;
                                                } else {
                                                    if (aux.equals("\n")) {
                                                        contRenglon++;
                                                    }
                                                }

                                            } else {
                                                break;
                                            }

                                        } else if (aux.equals("\n")) {
                                            contRenglon++;
                                            cadena += aux;
                                        }
                                    }
                                    break;
                                case "/":
                                    cadena += aux;
                                    aux = "";
                                    while (st.hasMoreElements()) {
                                        aux = st.nextToken();
                                        if (aux.equals("\n")) {
                                            contRenglon++;
                                            break;
                                        } else {
                                            cadena += aux;
                                        }
                                    }
                                    break;
                                case "=":
                                    auxRe = false;
                                    cadena += aux;
                                    break;
                                case " ":
                                    auxRe = false;
                                    break;
                                case "\n":
                                    contRenglon++;
                                    break;
                                default:
                                    aux1 = true;
                                    break;
                            }

                        }
                        break;
                    case "<":
                        if (st.hasMoreElements()) {
                            aux = st.nextToken();
                            switch (aux) {
                                case "=":
                                    cadena += aux;
                                    break;
                                case ">":
                                    cadena += aux;
                                    break;
                                case " ":
                                    break;
                                default:
                                    aux1 = true;
                                    break;
                            }
                        }
                        break;
                    case ">":
                        if (st.hasMoreElements()) {
                            aux = st.nextToken();
                            switch (aux) {
                                case "=":
                                    cadena += aux;
                                    break;
                                case " ":
                                    break;
                                default:
                                    aux1 = true;
                                    break;
                            }

                        }
                        break;
                    case "\"":
                        auxRe = true;
                        contRenglon2 = contRenglon;
                        while (st.hasMoreElements()) {
                            aux = st.nextToken();
                            if (aux.contains("\"")) {
                                cadena += aux;
                                break;
                            } else if (aux.equals("\n")) {
                                cadena += "\n";
                                contRenglon++;
                            } else if (aux.equals(" ")) {
                                cadena += " ";
                            } else {
                                cadena += aux;
                            }
                        }
                        break;
                    default:
                        break;
                }
                palabras.add(cadena);

                if (auxRe) {
                    if (aux1) {
                        palabras.add(aux);
                        agregaRenglon(contRenglon2);
                    }
                    agregaRenglon(contRenglon2);

                } else {
                    if (aux1) {
                        palabras.add(aux);
                        agregaRenglon(contRenglon);
                    }
                    agregaRenglon(contRenglon);
                }

            }
        }

        return palabras;
    }

    /**
     * Agregar valores al vector de renglón
     *
     * @param valor es el contador /n
     */
    private void agregaRenglon(int valor) {
        if (numRenglon == null) {
            numRenglon = new int[1];
            numRenglon[0] = valor;
        } else {
            int nvoRenglon[] = new int[numRenglon.length + 1];

            System.arraycopy(numRenglon, 0, nvoRenglon, 0, numRenglon.length);
            nvoRenglon[numRenglon.length] = valor;
            numRenglon = null;
            numRenglon = nvoRenglon;
        }

    }

    /**
     * Busca en tokens fijos la palabra y la prepara la busqueda para los
     * automatas
     *
     * @param lstPalabras
     */
    private void buscarTokensFijos(ArrayList<String> lstPalabras) {

        for (int i = 0; i < lstPalabras.size(); i++) {

            Lexema cadenaAnalizada = new Lexema();

            int token = 0;
            boolean automata = true;
            while (token < getConjuntoTokensFijos().length) {

                if (lstPalabras.get(i).equals(getConjuntoTokensFijos()[token][0]) || lstPalabras.get(i).equals(getConjuntoTokensFijos()[token][3])) {

                    cadenaAnalizada.setLexema(lstPalabras.get(i));
                    cadenaAnalizada.setNombreToken(getConjuntoTokensFijos()[token][1]);
                    cadenaAnalizada.setNumToken(Integer.parseInt(getConjuntoTokensFijos()[token][2]));
                    automata = false;
                    break;

                }
                token++;
            }
            if (automata) {

                char[] automataInverion = lstPalabras.get(i).toCharArray();
                String palabraConvertida = "";
                for (int j = 0; j < automataInverion.length; j++) {

                    if (Character.isLetterOrDigit(automataInverion[j])) {
                        if (Character.isLetter(automataInverion[j])) {
                            if (Character.isUpperCase(automataInverion[j])) {

                                palabraConvertida += "A";
                            }
                            if (Character.isLowerCase(automataInverion[j])) {
                                palabraConvertida += "L";
                            }
                        } else {
                            if (Character.isDigit(automataInverion[j])) {
                                palabraConvertida += "D";
                            }
                        }
                    } else if (automataInverion[j] == '"') {
                        palabraConvertida += "\"";
                    } else if (automataInverion[j] == '_') {
                        palabraConvertida += "_";
                    } else if (automataInverion[j] == '/') {
                        palabraConvertida += "/";
                    } else if (automataInverion[j] == '-') {
                        palabraConvertida += "-";
                    } else if (automataInverion[j] == '#') {
                        palabraConvertida += "#";
                    } else if (automataInverion[j] == '\'') {
                        palabraConvertida += "'";
                    } else if (automataInverion[j] == '*') {
                        palabraConvertida += "*";
                    } else if (automataInverion[j] == '.') {
                        palabraConvertida += ".";
                    } else {
                        palabraConvertida += "C";
                    }

                }

                cadenaAnalizada = buscaAutomata(palabraConvertida, lstPalabras.get(i));
            }

            getListaLexico().add(cadenaAnalizada);
        }
    }

    private Lexema buscaAutomata(String palabra, String lexema) {

        Lexema cadenaAnalizada = new Lexema();
        cadenaAnalizada.setLexema(lexema);

        Automata a = new Automata();
        a.setMatrizTransicion(getMatrizTransaccionGeneral());
        a.setVectorAlfabeto(getVecAlfabeto());
        a.setCadena(palabra);
        a.analisisAutomata();

        if (a.getEstado() == -4) {
            cadenaAnalizada.setNumToken(104);
            cadenaAnalizada.setNombreToken("Caracter desconocido");
        } else {
            switch (a.getTransaccionFinal()) {
                case 1:
                    if (a.getResAutomata().equals("Cadena valida")) {
                        cadenaAnalizada.setNumToken(50);
                        cadenaAnalizada.setNombreToken("Nombre de variable");
                    } else {
                        cadenaAnalizada.setNumToken(100);
                        cadenaAnalizada.setNombreToken("Nombre de variable no valida");
                    }
                    break;
                case 2:
                case 3:
                    if (a.getResAutomata().equals("Cadena valida")) {
                        cadenaAnalizada.setNumToken(55);
                        cadenaAnalizada.setNombreToken("Nombre de clase");
                    } else {
                        cadenaAnalizada.setNumToken(104);
                        cadenaAnalizada.setNombreToken("Nombre de clase no valido");
                    }
                    break;
                case 4:
                case 5:
                    if (a.getResAutomata().equals("Cadena valida")) {
                        cadenaAnalizada.setNumToken(56);
                        cadenaAnalizada.setNombreToken("Nombre de metodo");
                    } else {
                        cadenaAnalizada.setNumToken(105);
                        cadenaAnalizada.setNombreToken("Nombre de metodo no valido");
                    }
                    break;
                case 6:
                    if (a.getResAutomata().equals("Cadena valida")) {
                        cadenaAnalizada.setNumToken(51);
                        cadenaAnalizada.setNombreToken("Numero entero");
                    } else {
                        cadenaAnalizada.setNumToken(101);
                        cadenaAnalizada.setNombreToken("Numero entero incorrectos");
                    }
                    break;
                case 7:
                case 8:
                case 9:
                    if (a.getResAutomata().equals("Cadena valida")) {
                        cadenaAnalizada.setNumToken(52);
                        cadenaAnalizada.setNombreToken("Numero flotante");
                    } else {
                        cadenaAnalizada.setNumToken(102);
                        cadenaAnalizada.setNombreToken("Numero de flotante incorrectos");
                    }
                    break;
                case 10:
                case 11:
                case 12:
                    if (a.getResAutomata().equals("Cadena valida")) {
                        cadenaAnalizada.setNumToken(53);
                        cadenaAnalizada.setNombreToken("Cadena");
                    } else {
                        cadenaAnalizada.setNumToken(103);
                        cadenaAnalizada.setNombreToken("Cadena no valida");
                    }
                    break;
                case 13:
                case 14:
                    if (a.getResAutomata().equals("Cadena valida")) {
                        cadenaAnalizada.setNumToken(59);
                        cadenaAnalizada.setNombreToken("Comentario de linea");
                    } else {
                        cadenaAnalizada.setNumToken(96);
                        cadenaAnalizada.setNombreToken("Comentario de linea no valido");
                    }
                    break;
                case 15:
                case 16:
                case 17:
                    if (a.getResAutomata().equals("Cadena valida")) {
                        cadenaAnalizada.setNumToken(60);
                        cadenaAnalizada.setNombreToken("Comentario de bloque");
                    } else {
                        cadenaAnalizada.setNumToken(97);
                        cadenaAnalizada.setNombreToken("Comentario de bloque no valido");
                    }
                    break;

                default:
                    cadenaAnalizada.setNumToken(104);
                    cadenaAnalizada.setNombreToken("Caracter desconocido");
                    break;
            }

        }

        return cadenaAnalizada;
    }

    /**
     * Método que extrae la tabla de Excel en donde se encuentra la tabla de
     * transaccion y la convierte en la matriz de transaccion y obtiene el
     * vector del alfabeto
     */
    private void extraerTabla() {

        int tablaTransiccion[][];
        char vectorAlfabeto[];
        ArrayList<Integer> datos = new ArrayList<>();  //Matriz de transaccion
        ArrayList<Character> alfabeto = new ArrayList<>(); //Vector alfabeto
        int numF, numC = 0, conArray = 0;  //Matriz de transaccion
        int conVector = 0;             //Array del Vector alfabeto

        try {
            FileInputStream file = new FileInputStream(new File(getPathTablaExcel()));
            XSSFWorkbook book = new XSSFWorkbook(file);
            XSSFSheet sheet = book.getSheetAt(0);

            int numFilas = sheet.getLastRowNum();
            numF = numFilas;
            //Fila 1 Alfabeto
            for (int i = 0; i < numFilas + 1; i++) {
                Row fial = sheet.getRow(i);
                int numCOLS = fial.getLastCellNum();
                numC = numCOLS - 1;
                for (int j = 1; j < numCOLS; j++) {
                    Cell celda = fial.getCell(j);
                    if (i == 0) {
                        if (celda.getCellType().toString().equals("STRING")) {

                            char valorCelda = celda.getStringCellValue().charAt(0);
                            alfabeto.add(valorCelda);
                        }
                    } else {
                        if (celda.getCellType().toString().equals("NUMERIC")) {
                            datos.add((int) (celda.getNumericCellValue()));
                        } else {
                            datos.add(-1);
                        }
                    }
                }
            }
            vectorAlfabeto = new char[numC];
            for (int i = 0; i < numC; i++) {
                vectorAlfabeto[i] = alfabeto.get(conVector);
                conVector++;
            }

            tablaTransiccion = new int[numF][numC];
            for (int i = 0; i < numF; i++) {

                for (int j = 0; j < numC; j++) {
                    tablaTransiccion[i][j] = datos.get(conArray);
                    conArray++;
                }
            }
            setVecAlfabeto(vectorAlfabeto);
            setMatrizTransaccionGeneral(tablaTransiccion);
        } catch (IOException e) {
          //  System.out.println("" + e);
            TextPaneTest.appendToPane((JTextPane) editorPaneSalida, "Ups! Ha ocurrido un error "+ e.toString(), Color.red);
        }

    }

}
