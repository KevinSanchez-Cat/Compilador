/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisis;

import interfaces.PnlAnalisis;
import interfaces.VtnCompilador;
import static interfaces.VtnCompilador.editorPaneSalida;
import interfaces.VtnResultadoCmp;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;
import jsyntaxpane.DefaultSyntaxKit;
import recursos.CmpntTabPane;
import recursos.HiloTemporizador;
import recursos.ManipulaArchivos;
import recursos.TextPaneTest;

/**
 *
 * @author Kevin_Sanchez
 */
public class Main {

    /**
     * Creates new form Main
     */
    public static String textoFuente = "";
    public static String cadena;
    public static File archivo;
    public static FileInputStream input;
    public static FileOutputStream output;
    public static Boolean error;
    public static ArrayList<TablaSimbolos> lexema = new ArrayList<TablaSimbolos>();
    public static ArrayList<Variable> tablaSimbolos = new ArrayList<>();
    public static ArrayList<Archivo> archivos = new ArrayList<>();
    public static boolean l = true;
    public static HiloTemporizador h1;
    public static ArrayList<analisis.Lexema> lexemas;
    public static VtnResultadoCmp vtnResultado;
    public static DefaultTableModel modelo = null;
    public static String ResCodigoOptimizado = null;
    public static String ResCodigoFuente = null;
    public static String ResCodigoIntermedio = null;
    public static String ResAnalisiSemantico = null;
    public static String ResAnalisiSintactico = null;
    public static String ResAnalisiLexico = null;
    public static String ResOptimizacion = null;
    public static String ResFuente = null;
    public static Color lexicoColor = null;
    public static Color sintacticoColor = null;
    public static Color semanticoColor = null;
    public static Color intermedioColor = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        archivo = new File("Codigo.txt");
        vtnResultado = new VtnResultadoCmp();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            TextPaneTest.appendToPane((JTextPane) editorPaneSalida, "Ups! Ha ocurrido un error " + ex.toString(), Color.red);
        }

        new VtnCompilador().setVisible(true);

    }

    public static void btnGuardar() {
        int index = VtnCompilador.tabEdicion.getSelectedIndex();
        String s = VtnCompilador.tabEdicion.getTitleAt(index);
        for (int i = 0; i < archivos.size(); i++) {
            if (archivos.get(i).getNombre().equals(s)) {
                javax.swing.JScrollPane scroll = (javax.swing.JScrollPane) VtnCompilador.tabEdicion.getComponentAt(index);
                JViewport view = scroll.getViewport();
                javax.swing.JEditorPane edit = (javax.swing.JEditorPane) view.getView();
                String texto = edit.getText();
                ManipulaArchivos.guardar(texto, archivos.get(i).getUrl());
            }
        }
    }

    public static void btnLimpiar() {
        int index = VtnCompilador.tabEdicion.getSelectedIndex();
        javax.swing.JScrollPane scroll = (javax.swing.JScrollPane) VtnCompilador.tabEdicion.getComponentAt(index);
        JViewport view = scroll.getViewport();
        javax.swing.JEditorPane edit = (javax.swing.JEditorPane) view.getView();
        edit.setText("");

        PnlAnalisis.txtAreaLexico.setText(null);
        Main.ResAnalisiLexico = "";
        PnlAnalisis.txtAreaSintactico.setText(null);
        Main.ResAnalisiSintactico = "";
        PnlAnalisis.txtAreaSemantico.setText(null);
        Main.ResAnalisiSemantico = "";
        PnlAnalisis.txtAreaIntermedio.setText(null);
        Main.ResCodigoIntermedio = "";
        Main.ResFuente = (null);
        Main.ResOptimizacion = (null);
        Main.ResCodigoOptimizado = (null);
        Main.ResCodigoFuente = (null);
    }

    public static void btnCompilar() {
        // TODO add your handling code here:
        h1 = new HiloTemporizador(0, 0, 0, 0);
        h1.start();
        lexico(Main.textoFuente);
        Intermedio.operaciones = "";
        sintactico(Main.textoFuente);
        semantico();
        Intermedio.temp = 0;
        Main.ResCodigoFuente = (Main.textoFuente);
        h1.stop();
        String tiempo = h1.getMin() + ":" + h1.getSeg() + ":" + h1.getMilisegundos();

        String lex = Main.ResAnalisiLexico, sint = Main.ResAnalisiSintactico, sem = Main.ResAnalisiSemantico, inter = Main.ResCodigoIntermedio;

        h1 = new HiloTemporizador(0, 0, 0, 0);
        h1.start();
        optimizacion();
        lexico(Main.ResCodigoOptimizado);
        Intermedio.operaciones = "";
        sintactico(Main.ResCodigoOptimizado);
        semantico();
        h1.stop();
        Main.ResAnalisiLexico = lex;
        Main.ResAnalisiSemantico = sem;
        Main.ResCodigoIntermedio = inter;
        Main.ResAnalisiSintactico = sint;
        PnlAnalisis.txtAreaLexico.setText(lex);
        PnlAnalisis.txtAreaSintactico.setText(sint);
        PnlAnalisis.txtAreaSemantico.setText(sem);
        PnlAnalisis.txtAreaIntermedio.setText(inter);

        String tiempo2 = h1.getMin() + ":" + h1.getSeg() + ":" + h1.getMilisegundos();
        String nombre = "";
        if (VtnCompilador.tabEdicion.getSelectedIndex() != -1) {
            nombre = VtnCompilador.tabEdicion.getTitleAt(VtnCompilador.tabEdicion.getSelectedIndex());
        } else if (VtnCompilador.tabEdicion.getTabCount() == 1) {
            nombre = VtnCompilador.tabEdicion.getTitleAt(0);
        }
        String url = "";
        for (int i = 0; i < Main.archivos.size(); i++) {
            if (nombre.equals(Main.archivos.get(i).getNombre())) {
                url = Main.archivos.get(i).getUrl();
            }
        }
        String rutaFinal = url.replace(".txt", " - ");
        String URLfuente = rutaFinal + "Fuente.txt";
        String URLoptimizado = rutaFinal + "Optimizado.txt";
        File fuenteTxt = new File(URLfuente);
        File optimizadoTxt = new File(URLoptimizado);
        try {
            output = new FileOutputStream(fuenteTxt);
            byte[] txt = Main.textoFuente.getBytes();
            output.write(txt);
            output = new FileOutputStream(optimizadoTxt);
            txt = Main.ResCodigoOptimizado.getBytes();
            output.write(txt);
            Main.ResFuente = ("Tiempo de ejecución " + tiempo + "\nTamaño de archivo " + fuenteTxt.length() + " bytes");
            Main.ResOptimizacion = ("Tiempo de ejecución " + tiempo2 + "\nTamaño de archivo " + optimizadoTxt.length() + " bytes");
            JOptionPane.showMessageDialog(null, "Se ha guardado el código fuente y optimizado", "Escritura exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            TextPaneTest.appendToPane(editorPaneSalida, "Ups! Ha ocurrido un error " + ex.toString(), Color.red);
        } catch (IOException ex) {
            TextPaneTest.appendToPane((JTextPane) editorPaneSalida, "Ups! Ha ocurrido un error " + ex.toString(), Color.red);
        }
        TextPaneTest.appendToPane((JTextPane) editorPaneSalida, "COMPILACION TERMINADA", Color.green);

    }

    public static void btnLexico() {
        try {
            lexico(Main.textoFuente);
        } catch (Exception ex) {

        }
    }

    public static void btnSintactico() {
        sintactico(Main.textoFuente);
    }

    public static void btnIntermedio() {
        PnlAnalisis.txtAreaIntermedio.setText("");
        Main.ResCodigoIntermedio = "";
//        Intermedio.intermedio(lexema);
        Main.ResCodigoIntermedio = Intermedio.operaciones;
        PnlAnalisis.txtAreaIntermedio.setText(Intermedio.operaciones);
    }

    public static void btnOptimizacion() {
        h1 = new HiloTemporizador(0, 0, 0, 0);
        h1.start();
        lexico(Main.textoFuente);
        PnlAnalisis.txtAreaLexico.setText("");
        Main.ResAnalisiLexico = "";
        optimizacion();
        h1.stop();
        Main.ResCodigoFuente = (Main.textoFuente);
        String tiempo2 = h1.getMin() + ":" + h1.getSeg() + ":" + h1.getMilisegundos();
        File fuenteTxt = new File("Fuente.txt");
        File optimizadoTxt = new File("Optimizado.txt");
        try {
            output = new FileOutputStream(fuenteTxt);
            byte[] txt = Main.textoFuente.getBytes();
            output.write(txt);
            output = new FileOutputStream(optimizadoTxt);
            txt = Main.ResCodigoOptimizado.getBytes();
            output.write(txt);
            Main.ResFuente = ("Tiempo de ejecución " + tiempo2 + "\nTamaño de archivo " + fuenteTxt.length() + " bytes");
            Main.ResOptimizacion = ("Tiempo de ejecución " + tiempo2 + "\nTamaño de archivo " + optimizadoTxt.length() + " bytes");
            JOptionPane.showMessageDialog(null, "Se ha guardado el código fuente y optimizado", "Escritura exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            TextPaneTest.appendToPane((JTextPane) editorPaneSalida, "Ups! Ha ocurrido un error " + ex.toString(), Color.red);
        } catch (IOException ex) {
            TextPaneTest.appendToPane((JTextPane) editorPaneSalida, "Ups! Ha ocurrido un error " + ex.toString(), Color.red);
        }
    }

    public static void btnSematico() {
        // TODO add your handling code here:
        sintactico(Main.textoFuente);
        semantico();
    }

    public static void notificar_er(String cad) {
        PnlAnalisis.txtAreaSintactico.append(cad + "\n\n");
        Main.ResAnalisiSintactico += cad + "\n\n";
    }

    private static void lexico(String fuente) {
        l = true;
        String expresion = fuente + " ";
        /*  if (expresion.trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "No se ha escrito el codigo fuente", "Lectura exitosa", JOptionPane.INFORMATION_MESSAGE);
            lexico.setText("");
        } else {
            cadena = "";
            if (AnalisisLexico.separa("", expresion)) {
                System.out.println("si");
                lexico.setText(cadena);
                lexico.setForeground(new Color(25, 111, 61));
            }else{
                System.out.println("no");
                lexico.setText(cadena);
                lexico.setForeground(new Color(25, 111, 61));
            }
        }*/
        analisis.Lexico analisisLexico = new analisis.Lexico(expresion, "+-=*&| {}()[]!?^/%;:,<>\n\t\r\b\f", "Tabla del automata general.xlsx");

        lexemas = analisisLexico.analisisLexico();
        String cad = "Lexema\tToken\tNombre\tRenglon\n";
        for (int p = 0; p < lexemas.size(); p++) {
            //textoMostrar += " " +  + "\t" +  + "\t" + lexemas.get(i).getNumToken() + "\t" + lexemas.get(i).getRenglon() + "\n";
            cad += ""
                    + lexemas.get(p).getLexema() + "\t"
                    + "t" + String.valueOf(lexemas.get(p).getNumToken()) + "   ➡ \t"
                    + lexemas.get(p).getNombreToken() + "    \t"
                    + String.valueOf(lexemas.get(p).getRenglon()) + "\n";

        }
        Main.ResAnalisiLexico = cad;
        PnlAnalisis.txtAreaLexico.setText(cad);
        PnlAnalisis.txtAreaLexico.setForeground(new Color(25, 111, 61));
        Main.lexicoColor = new Color(25, 111, 61);
    }

    private static void sintactico(String fuente) {
        String expresion = fuente + " ";
        PnlAnalisis.txtAreaSintactico.setText("");
        Main.ResAnalisiSintactico = "";
        lexema.clear();
        if (expresion.trim().length() == 0) {
            PnlAnalisis.txtAreaLexico.setText("");
        } else {

            String ST = fuente;
            Sintax s = new Sintax(new LexerCup(new StringReader(ST)));
            try {
                s.parse();
                if (Main.ResAnalisiSintactico.equals("")) {
                    PnlAnalisis.txtAreaSintactico.append("Análisis realizado correctamente");
                    PnlAnalisis.txtAreaSintactico.setForeground(new Color(25, 111, 61));
                    Main.ResAnalisiSintactico += "Análisis realizado correctamente";
                    Main.sintacticoColor = new Color(25, 111, 61);
                } else {
                    PnlAnalisis.txtAreaSintactico.append("Análisis realizado correctamente");
                    PnlAnalisis.txtAreaSintactico.setForeground(new Color(25, 111, 61));
                    Main.sintacticoColor = new Color(25, 111, 61);
                    Main.ResAnalisiSintactico += "Análisis realizado correctamente";
                }
            } catch (Exception ex) {
//                Symbol sym = s.getS();
//                sintactico.setText("Error de sintaxis, en la línea: " + (sym.right + 1)+  "\n\nPalabra no esperada: \"" + sym.value + "\"");
//                sintactico.setForeground(Color.red);
                TextPaneTest.appendToPane((JTextPane) editorPaneSalida, "Ups! Ha ocurrido un error " + ex.toString(), Color.red);
            }
        }
    }

    private static void semantico() {
        PnlAnalisis.txtAreaSemantico.setText("");
        Main.ResAnalisiSemantico = "";
        String expresion = Main.textoFuente + " ";
        if (expresion.trim().length() == 0) {
            PnlAnalisis.txtAreaSemantico.setText("");
            Main.ResAnalisiSemantico = "";
        } else {
            AnalisisSemantico.mensajesError = "";
//            for (int i = 0; i <lexema.size(); i++) {
//                System.out.println(lexema.get(i).getLexema()+" "+lexema.get(i).getNumToken());
//                if(lexemas.get(i).getLexema().equals("-")){
//                    //Numero - Nmumero
//                    // = - Numero
//                }
//            }
//            
            AnalisisSemantico.analizar(lexema);
            if (AnalisisSemantico.mensajesError.equals("")) {
                Main.ResAnalisiSemantico += "Análisis realizado correctamente";
                PnlAnalisis.txtAreaSemantico.append("Análisis realizado correctamente");
                PnlAnalisis.txtAreaSemantico.setForeground(new Color(25, 111, 61));
                Main.semanticoColor = new Color(25, 111, 61);
            } else {
                PnlAnalisis.txtAreaSemantico.setForeground(Color.RED);
                Main.semanticoColor = Color.RED;
                PnlAnalisis.txtAreaSemantico.setText(AnalisisSemantico.mensajesError);
                Main.ResAnalisiSemantico += AnalisisSemantico.mensajesError;
            }
            modelo = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Columnas de la tabla
            modelo.addColumn("Tipo");
            modelo.addColumn("Variable");
            modelo.addColumn("Valor");
            modelo.addColumn("Unica");
            modelo.addColumn("Inicializada");
            String unica, inicializada;
            for (int i = 0; i < AnalisisSemantico.tablaSimbolos.size(); i++) {
                Object fila[] = new Object[5];
                fila[0] = AnalisisSemantico.tablaSimbolos.get(i).getTipo();
                fila[1] = AnalisisSemantico.tablaSimbolos.get(i).getVariable();
                fila[2] = AnalisisSemantico.tablaSimbolos.get(i).getValor();
                if (AnalisisSemantico.tablaSimbolos.get(i).isUnica()) {
                    unica = "Si";
                } else {
                    unica = "No";
                }
                fila[3] = unica;
                if (AnalisisSemantico.tablaSimbolos.get(i).isInicializada()) {
                    inicializada = "Si";
                } else {
                    inicializada = "No";
                }
                fila[4] = inicializada;
                modelo.addRow(fila);
            }
        }
    }

    public static void optimizacion() {

        ArrayList<analisis.Lexema> lista2 = new ArrayList<>();
        //Tecnica 1 quitar comentarios
        lista2 = optimizaComentarios(lista2);
        //Tecnica 2 Estructura
        lista2 = optimizarEstructura(lista2);
        //Tecnica 3 Reduccion Operaciones
        lista2 = optimizarVariablesOp(lista2);
        //Tecnica 4 Variables no usadas
        lista2 = optimizaVariablesSinUso(lista2);
        //Tecnica 5 Reduccion Operaciones Revision 2
        lista2 = optimizarVariablesOp(lista2);
        //Tecnica 6 Variables no usadas Revision 2
        lista2 = optimizaVariablesSinUso(lista2);
        //Tecnica 7 Asignaciones por declaraciones, si no hubieron cambios entre una asignacion y otra o no la variable no se ocupo entre una asignacion y otra
        lista2 = optimizaAsignacionesInnecesarias(lista2);
        //Tecnica 8 Sustitucion de varibles
        // lista2 = optimizaOperaciones(lista2);
        //Tecnica 8 Quitar espacios y tabuladores
        imprimeOptimizacion(lista2);
    }

    private static ArrayList<Lexema> optimizaAsignacionesInnecesarias(ArrayList<Lexema> lista2) {
// si no hubieron cambios entre una asignacion y otra o no la variable no se ocupo entre una asignacion y otra
        ArrayList<Lexema> arrLex = new ArrayList<>();
//Agrega variables
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 1) {
                Lexema lx = new Lexema();
                lx.setNombreToken(lista2.get(i).getLexema());
                lx.setLexema(lista2.get(i + 1).getLexema());
                if (lista2.get(i + 2).getLexema().equals("=")) {
                    lx.setRenglon(1);
                } else {
                    lx.setRenglon(0);
                }
                arrLex.add(lx);
            }
        }
//En las variables que se inicializan despues de ser declaradas, poner en su asignacion la declaracion.
//El tipo antes y eliminar su declaracion;

        for (int i = 0; i < arrLex.size(); i++) {
            for (int j = 0; j < lista2.size(); j++) {
                if (arrLex.get(i).getLexema().equals(lista2.get(j).getLexema())) {
                    if (arrLex.get(i).getRenglon() == 0) {
                        Lexema aux = new Lexema();
                        aux.setLexema(arrLex.get(i).getLexema());
                        aux.setNombreToken(arrLex.get(i).getNombreToken());
                        aux.setNumToken(arrLex.get(i).getNumToken());
                        aux.setRenglon(1);
                        if (lista2.get(j - 1).getLexema().equals("int") || lista2.get(j - 1).getLexema().equals("float") || lista2.get(j - 1).getLexema().equals("boolean") || lista2.get(j - 1).getLexema().equals("String")) {
//Eliminar
                            lista2.remove(j - 1);//tipo
                            lista2.remove(j - 1);//lexema
                            lista2.remove(j - 1);//;
                        }

                    }
                }
            }
        }

        for (int i = 0; i < arrLex.size(); i++) {
            for (int j = 0; j < lista2.size(); j++) {
                if (arrLex.get(i).getLexema().equals(lista2.get(j).getLexema())) {
                    if (arrLex.get(i).getRenglon() == 0) {
                        Lexema aux = new Lexema();
                        aux.setLexema(arrLex.get(i).getNombreToken());
                        aux.setNombreToken("Tipo de dato");
                        aux.setNumToken(1);
                        lista2.add(j, aux);
                        arrLex.get(i).setRenglon(1);
                        break;
                    }
                }
            }
        }
// //buscar a partir del este punto su primera asignacion para declarar
// for (int k = j; k < lista2.size(); k++) {
// if (arrLex.get(i).getLexema().equals(lista2.get(k).getLexema())) {
// Lexema tipo=new Lexema();
// tipo.setLexema(aux.getNombreToken());
// lista2.add(k-1,tipo);
// break;
// }
// }

        return lista2;
    }

    public static ArrayList<analisis.Lexema> optimizaComentarios(ArrayList<analisis.Lexema> lista2) {
        for (int j = 0; j < lexemas.size(); j++) {
            if ((lexemas.get(j).getNumToken() == 59) || (lexemas.get(j).getNumToken() == 60)) {

            } else {
                analisis.Lexema lx = new analisis.Lexema();
                lx.setLexema(lexemas.get(j).getLexema());
                lx.setNombreToken(lexemas.get(j).getNombreToken());
                lx.setNumToken(lexemas.get(j).getNumToken());
                lx.setRenglon(lexemas.get(j).getRenglon());
                lista2.add(lx);
            }
        }
        return lista2;
    }

    public static void imprimeOptimizacion(ArrayList<analisis.Lexema> lista2) {
        String optimizado = "";
        for (int i = 0; i < lista2.size(); i++) {
            optimizado += lista2.get(i).getLexema();
            if (lista2.get(i).getNumToken() == 9 || lista2.get(i).getNumToken() == 10 || lista2.get(i).getNumToken() == 2) {
                optimizado += "\n";
            } else {
                optimizado += " ";
            }
        }
        Main.ResCodigoOptimizado = (optimizado);
    }

    private static ArrayList<Lexema> optimizaOperaciones(ArrayList<Lexema> lista2) {
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 53) {
                i++;
                int renglon = 0;
                ArrayList<String> arrInt = new ArrayList<>();
                boolean bandera = true;
                while (bandera) {
                    if (lista2.get(i).getNumToken() == 2 || lista2.get(i).getNumToken() == 15) {
                        bandera = false;
                    } else {

                        if (lista2.get(i).getNumToken() == 50) {
                            for (int j = 0; j < tablaSimbolos.size(); j++) {
                                if (lista2.get(i).getLexema().equals(tablaSimbolos.get(j).getVariable())) {
                                    arrInt.add(tablaSimbolos.get(j).getValor());
                                    break;
                                }
                            }

                        } else {
                            arrInt.add(lista2.get(i).getLexema());
                        }
                        renglon = lista2.get(i).getRenglon();
                        lista2.remove(i);
                    }

                }
                String valor = Semantico.conversionArrayCola(arrInt); //RETORNA EL VALOR DE LA OPERACION
                //  String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                Lexema ls = new Lexema();
                ls.setLexema(valor);
                ls.setNombreToken("Operacion");
                ls.setRenglon(renglon);
                ls.setNumToken(200);
                lista2.add(i, ls);
            }

        }

        return lista2;
    }

    public static ArrayList<Lexema> optimizarVariablesOp(ArrayList<Lexema> lista2) {
        //Repaso 1: Solo operables
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 53) {
                int index = i + 1;
                ArrayList<Integer> indexs = new ArrayList<>();
                boolean n = true;
                ArrayList<String> str = new ArrayList<>();
                int k = i + 1;
                while (lista2.get(k).getNumToken() != 2 && n) {
                    if (lista2.get(k).getNumToken() == 50) {
                        n = false;
                        break;
                    } else {
                        str.add(lista2.get(k).getLexema());
                        indexs.add(k);
                    }
                    k++;
                }
                if (n == true) {
                    String res = Semantico.conversionArrayCola(str);
                    String tipo = Semantico.tipoDato(res);
                    for (int j = 0; j < indexs.size() - 1; j++) {
                        lista2.remove(index);
                    }
                    Lexema ls = new Lexema();
                    ls.setLexema(res);
                    switch (tipo) {
                        case "INTEGER":
                            ls.setNumToken(51);
                            break;
                        case "FLOAT":
                            ls.setNumToken(52);
                            break;
                        case "BOOLEAN":
                            if (res.equals("true")) {
                                ls.setNumToken(22);
                            } else {
                                ls.setNumToken(23);
                            }
                            break;
                        default:
                            ls.setNumToken(53);
                            break;
                    }
                    lista2.set(index, ls);
                } else {
                    indexs.clear();
                }
            }
        }

        //Repaso 2: Sustituir sumas
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getLexema().equals("+") || lista2.get(i).getLexema().equals("-")) {
                int index2 = i - 1;
                //Hacia la izquierda
                if (lista2.get(i - 1).getNumToken() == 50) {

                } else {
                    if (lista2.get(i - 2).getNumToken() == 53 || lista2.get(i - 2).getNumToken() == 7 || lista2.get(i - 2).getLexema().equals("+") || lista2.get(i - 2).getLexema().equals("-")) {
                        //Hacia la derecha
                        if (lista2.get(i + 1).getNumToken() == 50) {

                        } else {
                            if (lista2.get(i + 2).getNumToken() == 15 || lista2.get(i + 2).getNumToken() == 8 || lista2.get(i + 2).getNumToken() == 2 || lista2.get(i + 2).getLexema().equals("+") || lista2.get(i + 2).getLexema().equals("-")) {
                                //Si
                                int op1 = Integer.parseInt(lista2.get(i - 1).getLexema());
                                int op2 = Integer.parseInt(lista2.get(i + 1).getLexema());
                                int result;
                                if (lista2.get(i).getLexema().equals("+")) {

                                    result = op1 + op2;
                                } else {

                                    result = op1 - op2;
                                }
                                Lexema ls2 = new Lexema();
                                ls2.setLexema(String.valueOf(result));
                                ls2.setNumToken(51);
                                lista2.set(index2, ls2);
                                lista2.remove(index2 + 1);
                                lista2.remove(index2 + 1);
                                i = 0;
                            } else {

                            }

                        }

                    } else {

                    }
                }
            }

        }

        //Repaso 3: Sustituir Multiplicaciones
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getLexema().equals("*") || lista2.get(i).getLexema().equals("/")) {
                int index2 = i - 1;
                //Hacia la izquierda
                if (lista2.get(i - 1).getNumToken() == 50) {

                } else {
                    if (lista2.get(i - 2).getNumToken() == 53 || lista2.get(i - 2).getNumToken() == 7 || lista2.get(i - 2).getLexema().equals("+") || lista2.get(i - 2).getLexema().equals("-") || lista2.get(i - 2).getLexema().equals("/") || lista2.get(i - 2).getLexema().equals("*")) {
                        //Hacia la derecha
                        if (lista2.get(i + 1).getNumToken() == 50) {

                        } else {
                            if (lista2.get(i + 2).getNumToken() == 15 || lista2.get(i + 2).getNumToken() == 8 || lista2.get(i + 2).getNumToken() == 2 || lista2.get(i + 2).getLexema().equals("+") || lista2.get(i + 2).getLexema().equals("-") || lista2.get(i + 2).getLexema().equals("/") || lista2.get(i + 2).getLexema().equals("*")) {
                                //Si
                                int op1 = Integer.parseInt(lista2.get(i - 1).getLexema());
                                int op2 = Integer.parseInt(lista2.get(i + 1).getLexema());
                                int result;
                                if (lista2.get(i).getLexema().equals("*")) {

                                    result = op1 * op2;
                                } else {

                                    result = op1 / op2;
                                }
                                Lexema ls2 = new Lexema();
                                ls2.setLexema(String.valueOf(result));
                                ls2.setNumToken(51);
                                lista2.set(index2, ls2);
                                lista2.remove(index2 + 1);
                                lista2.remove(index2 + 1);
                                i = 0;
                            } else {

                            }

                        }

                    } else {

                    }
                }
            }

        }
        //Resapo 4 : sumas 
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getLexema().equals("+") || lista2.get(i).getLexema().equals("-")) {
                int index2 = i - 1;
                //Hacia la izquierda
                if (lista2.get(i - 1).getNumToken() == 50) {

                } else {
                    if (lista2.get(i - 2).getNumToken() == 53 || lista2.get(i - 2).getNumToken() == 7 || lista2.get(i - 2).getLexema().equals("+") || lista2.get(i - 2).getLexema().equals("-")) {
                        //Hacia la derecha
                        if (lista2.get(i + 1).getNumToken() == 50) {

                        } else {
                            if (lista2.get(i + 2).getNumToken() == 15 || lista2.get(i + 2).getNumToken() == 8 || lista2.get(i + 2).getNumToken() == 2 || lista2.get(i + 2).getLexema().equals("+") || lista2.get(i + 2).getLexema().equals("-")) {
                                //Si
                                int op1 = Integer.parseInt(lista2.get(i - 1).getLexema());
                                int op2 = Integer.parseInt(lista2.get(i + 1).getLexema());
                                int result;
                                if (lista2.get(i).getLexema().equals("+")) {

                                    result = op1 + op2;
                                } else {

                                    result = op1 - op2;
                                }
                                Lexema ls2 = new Lexema();
                                ls2.setLexema(String.valueOf(result));
                                ls2.setNumToken(51);
                                lista2.set(index2, ls2);
                                lista2.remove(index2 + 1);
                                lista2.remove(index2 + 1);
                                i = 0;
                            } else {

                            }

                        }

                    } else {

                    }
                }
            }

        }

        return lista2;
    }

    public static ArrayList<Lexema> optimizarEstructura(ArrayList<Lexema> lista2) {
        //Variables globales
        int renglonClase = 0;
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 55) {
                if (i + 1 < lista2.size()) {
                    if (lista2.get(i + 1).getNumToken() == 9) {
                        renglonClase = i + 2;
                    }
                }

            }
        }
        ArrayList<Lexema> lxV = new ArrayList<>();
        ArrayList<Lexema> lxM = new ArrayList<>();
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 21) {
                //Metodos
                int llaveAbre = 0;
                boolean llaveCierra = false;
                while (true) {
                    if (lista2.get(i).getNumToken() == 9) {
                        if (!llaveCierra) {
                            llaveCierra = true;
                        }
                        llaveAbre++;
                    } else if (lista2.get(i).getNumToken() == 10) {
                        llaveAbre--;
                    }
                    Lexema lx = new Lexema();
                    lx.setLexema(lista2.get(i).getLexema());
                    lx.setNumToken(lista2.get(i).getNumToken());
                    lx.setNombreToken(lista2.get(i).getNombreToken());
                    lxM.add(lx);
                    lista2.remove(i);

                    if (llaveAbre == 0 && llaveCierra) {
                        break;
                    }
                }

            }

        }
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 1 || lista2.get(i).getNumToken() == 50) {
                while (true) {

                    if (lista2.get(i).getNumToken() == 2) {
                        Lexema lx = new Lexema();
                        lx.setLexema(lista2.get(i).getLexema());
                        lx.setNumToken(lista2.get(i).getNumToken());
                        lx.setNombreToken(lista2.get(i).getNombreToken());
                        lxV.add(lx);
                        lista2.remove(i);
                        i--;
                        break;
                    } else {
                        Lexema lx = new Lexema();
                        lx.setLexema(lista2.get(i).getLexema());
                        lx.setNumToken(lista2.get(i).getNumToken());
                        lx.setNombreToken(lista2.get(i).getNombreToken());
                        lxV.add(lx);
                        lista2.remove(i);
                    }
                }
            }
        }
        for (int i = 0; i < lxV.size(); i++) {
            lista2.add(renglonClase, lxV.get(i));
            renglonClase++;
        }
        for (int i = 0; i < lxM.size(); i++) {
            lista2.add(renglonClase, lxM.get(i));
            renglonClase++;
        }

        return lista2;
    }

    private static ArrayList<Lexema> optimizaVariablesSinUso(ArrayList<Lexema> lista2) {
        ArrayList<Variable> arrV = new ArrayList<>();

        for (int i = 0; i < lista2.size(); i++) {

            if (lista2.get(i).getNumToken() == 1) {
                i++;
                if (lista2.get(i).getNumToken() == 50) {
                    Variable v = new Variable(lista2.get(i).getLexema(), null, false, false, 0, null);
                    i++;
                    if (lista2.get(i).getNumToken() == 53) {
                        v.setInicializada(true);
                    }
                    arrV.add(v);
                }
            }
        }
        //Apilicar propiedades aritmeticas
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 50) {
                if (lista2.get(i - 1).getLexema().equals("*")) {
                    if (lista2.get(i - 2).getLexema().equals("1")) {
                        lista2.remove(i - 2);
                        lista2.remove(i - 2);
                    } else if (lista2.get(i - 2).getLexema().equals("0")) {
                        lista2.remove(i - 1);
                        lista2.remove(i);
                    }
                } else if (lista2.get(i + 1).getLexema().equals("*") || lista2.get(i + 1).getLexema().equals("/")) {
                    if (lista2.get(i + 2).getLexema().equals("1")) {
                        lista2.remove(i + 1);
                        lista2.remove(i + 1);
                    } else if (lista2.get(i + 1).getLexema().equals("*") && lista2.get(i + 2).getLexema().equals("0")) {
                        lista2.remove(i + 1);
                        lista2.remove(i);
                    }
                } else if (lista2.get(i - 1).getLexema().equals("+") || lista2.get(i - 1).getLexema().equals("-")) {
                    if (lista2.get(i - 2).getLexema().equals("0")) {
                        lista2.remove(i - 2);
                        lista2.remove(i - 2);
                    }
                } else if (lista2.get(i + 1).getLexema().equals("+") || lista2.get(i + 1).getLexema().equals("-")) {
                    if (lista2.get(i + 2).getLexema().equals("0")) {
                        lista2.remove(i + 1);
                        lista2.remove(i + 1);
                    }
                }

            }
        }

        //Si x=x; eliminar instruccion
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 50 && lista2.get(i + 1).getLexema().equals("=") && lista2.get(i + 2).getLexema().equals(lista2.get(i).getLexema()) && lista2.get(i + 3).getLexema().equals(";")) {
                if (lista2.get(i).getNumToken() == 1) {
                    lista2.remove(i - 1);
                    lista2.remove(i - 1);
                    lista2.remove(i - 1);
                    lista2.remove(i - 1);
                    lista2.remove(i - 1);
                } else {
                    lista2.remove(i);
                    lista2.remove(i);
                    lista2.remove(i);
                    lista2.remove(i);
                }
            }
        }

        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getLexema().equals("=")) {
                i++;
                for (int j = 0; j < arrV.size(); j++) {

                    if (lista2.get(i - 2).getLexema().equals(arrV.get(j).getVariable())) {
                        arrV.get(j).setInicializada(true);
                    }
                }
                while (!lista2.get(i).getLexema().equals(";")) {
                    if (lista2.get(i).getNumToken() == 50) {
                        for (int j = 0; j < arrV.size(); j++) {
                            if (lista2.get(i).getLexema().equals(arrV.get(j).getVariable())) {
                                arrV.get(j).setUnica(true);
                            }
                        }

                    }
                    i++;
                }
            }
        }
        //Sin inicializar
        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 50) {
                for (int j = 0; j < arrV.size(); j++) {
                    if (lista2.get(i).getLexema().equals(arrV.get(j).getVariable()) && (arrV.get(j).isInicializada() == false)) {
                        if (lista2.get(i - 1).getNumToken() == 1) {
                            if (lista2.get(i + 1).getNumToken() == 2) {
                                //Se elimina el tipo, lexema, punto y coma
                                lista2.remove(i - 1);
                                lista2.remove(i - 1);
                                lista2.remove(i - 1);
                            } else if (lista2.get(i + 1).getNumToken() == 15) {//coma
                                //Se elimina el lexema, coma
                                lista2.remove(i + 1);
                                lista2.remove(i + 1);
                            }

                        } else if (lista2.get(i - 1).getNumToken() == 15) {
                            if (lista2.get(i + 1).getNumToken() == 2) {
                                //Se elimina la coma , lexema
                                lista2.remove(i - 1);
                                lista2.remove(i - 1);
                            } else if (lista2.get(i + 1).getNumToken() == 15) {//coma
                                //Se elimina el coma, lexema

                                lista2.remove(i - 1);
                                lista2.remove(i - 1);
                            }
                        }
                        i = 0;
                    } else {

                    }
                }
            }
        }
        //Variables que tienen valor pero nunca son usadas
        for (int i = 0; i < arrV.size(); i++) {
            boolean valor = false;
            for (int j = 0; j < lista2.size(); j++) {
                if (lista2.get(j).getNumToken() == 50) {
                    if (arrV.get(i).getVariable().equals(lista2.get(j).getLexema())) {
                        if (lista2.get(j + 1).getLexema().equals("=")) {
                            j++;
                            while (!lista2.get(j).getLexema().equals(";")) {
                                if (lista2.get(j).getNumToken() == 50) {
                                    valor = true;
                                    break;
                                } else {
                                    j++;
                                }
                            }
                        }
                    }
                }
            }

            if (valor == false && arrV.get(i).isUnica() == false) {
                for (int j = 0; j < lista2.size(); j++) {

                    if (lista2.get(j).getNumToken() == 50) {
                        if (arrV.get(i).getVariable().equals(lista2.get(j).getLexema())) {

                            if (lista2.get(j - 1).getNumToken() == 1) {
                                if (lista2.get(j + 1).getNumToken() == 2) {
                                    //Se elimina el tipo, lexema, punto y coma
                                    lista2.remove(j - 1);
                                    lista2.remove(j - 1);
                                    lista2.remove(j - 1);
                                } else if (lista2.get(j + 1).getNumToken() == 15) {//coma
                                    //Se elimina el lexema, coma
                                    lista2.remove(j + 1);
                                    lista2.remove(j + 1);
                                } else if (lista2.get(j + 1).getLexema().equals("=")) {
                                    //eliminar
                                    j++;
                                    while (true) {
                                        if (lista2.get(j).getLexema().equals(";") || lista2.get(j).getLexema().equals(",")) {
                                            break;
                                        } else {
                                            lista2.remove(j);
                                        }
                                    }
                                    //Eliminar 

                                }

                            } else if (lista2.get(j - 1).getNumToken() == 15) {
                                if (lista2.get(j + 1).getNumToken() == 2) {
                                    //Se elimina la coma , lexema
                                    lista2.remove(j - 1);
                                    lista2.remove(j - 1);
                                } else if (lista2.get(j + 1).getNumToken() == 15) {//coma
                                    //Se elimina el coma, lexema

                                    lista2.remove(j - 1);
                                    lista2.remove(j - 1);
                                } else if (lista2.get(j + 1).getLexema().equals("=")) {
                                    //eliminar
                                    j++;
                                    while (true) {
                                        if (lista2.get(j).getLexema().equals(";") || lista2.get(j).getLexema().equals(",")) {
                                            break;
                                        } else {
                                            lista2.remove(j);
                                        }
                                    }
                                    //Eliminar 

                                    if (lista2.get(j).getNumToken() == 2) {
                                        //Se elimina el tipo, lexema, punto y coma
                                        lista2.remove(j - 1);
                                        lista2.remove(j - 1);
                                        lista2.remove(j - 1);
                                    } else if (lista2.get(j + 1).getNumToken() == 15) {//coma
                                        //Se elimina el lexema, coma
                                        lista2.remove(j + 1);
                                        lista2.remove(j + 1);
                                    }

                                }
                            }

                        }
                    }

                }
            }
        }
        //Quitar parentesis inecesarios

        for (int i = 0; i < lista2.size(); i++) {
            if (lista2.get(i).getNumToken() == 9) {
                if (lista2.get(i + 2).getNumToken() == 10) {
                    lista2.remove(i);
                    lista2.remove(i + 1);
                }
            }

        }

        return lista2;
    }

    public static void abrirArchivo(javax.swing.JTabbedPane tabs, String nombre, String texto, String url) {
        if (tabs.getComponentCount() < 0) {
            DefaultSyntaxKit.initKit();
            javax.swing.JScrollPane scrollPag = new javax.swing.JScrollPane();
            JEditorPane pag = new JEditorPane();
            scrollPag.setViewportView(pag);
            pag.setContentType("text/java");
            tabs.addTab(nombre, scrollPag);
            CmpntTabPane pnlTab = new CmpntTabPane(tabs, 0, VtnCompilador.imageLenguaje1);
            tabs.setTabComponentAt(tabs.getTabCount() - 1, pnlTab);
            pag.setText(texto);
            Archivo f = new Archivo();
            f.setIndex(tabs.getTabCount());
            f.setNombre(nombre);
            f.setUrl(url);
            archivos.add(f);
        } else {
            boolean edicion = false;
            int index1 = 0;
            for (int i = 0; i < tabs.getComponentCount() - 1; i++) {
                if (tabs.getTitleAt(i).equals("Edición")) {
                    edicion = true;
                    index1 = i;
                }
            }
            if (edicion) {
                javax.swing.JScrollPane scroll = (javax.swing.JScrollPane) VtnCompilador.tabEdicion.getComponentAt(index1);
                JViewport view = scroll.getViewport();
                javax.swing.JEditorPane edit = (javax.swing.JEditorPane) view.getView();
                VtnCompilador.tabEdicion.setTitleAt(index1, nombre);
                edit.setText(texto);

                Archivo f = new Archivo();
                f.setIndex(index1);
                f.setNombre(nombre);
                f.setUrl(url);
                archivos.add(f);
            } else {
                DefaultSyntaxKit.initKit();
                javax.swing.JScrollPane scrollPag = new javax.swing.JScrollPane();
                JEditorPane pag = new JEditorPane();
                scrollPag.setViewportView(pag);
                pag.setContentType("text/java");
                tabs.addTab(nombre, scrollPag);
                CmpntTabPane pnlTab = new CmpntTabPane(tabs, 0, VtnCompilador.imageLenguaje1);
                tabs.setTabComponentAt(tabs.getTabCount() - 1, pnlTab);
                pag.setText(texto);
                Archivo f = new Archivo();
                f.setIndex(tabs.getTabCount());
                f.setNombre(nombre);
                f.setUrl(url);
                archivos.add(f);
            }
        }

    }

    public static void nuevoArchivo(javax.swing.JTabbedPane tabs, String nombre, String url) {
        if (tabs.getComponentCount() == 0) {
            DefaultSyntaxKit.initKit();
            javax.swing.JScrollPane scrollPag = new javax.swing.JScrollPane();
            JEditorPane pag = new JEditorPane();
            scrollPag.setViewportView(pag);
            pag.setContentType("text/java");
            tabs.addTab(nombre, scrollPag);
            CmpntTabPane pnlTab = new CmpntTabPane(tabs, 0, VtnCompilador.imageLenguaje1);

            tabs.setTabComponentAt(tabs.getTabCount() - 1, pnlTab);
            Archivo f = new Archivo();
            f.setIndex(tabs.getTabCount());
            f.setNombre(nombre);
            f.setUrl(url);
            archivos.add(f);
        } else {
            boolean edicion = false;
            int index1 = 0;
            for (int i = 0; i < tabs.getComponentCount() - 1; i++) {
                if (tabs.getTitleAt(i).equals("Edición")) {
                    edicion = true;
                    index1 = i;
                }
            }
            if (edicion) {
                javax.swing.JScrollPane scroll = (javax.swing.JScrollPane) VtnCompilador.tabEdicion.getComponentAt(index1);
                JViewport view = scroll.getViewport();
                javax.swing.JEditorPane edit = (javax.swing.JEditorPane) view.getView();
                VtnCompilador.tabEdicion.setTitleAt(index1, nombre);

                Archivo f = new Archivo();
                f.setIndex(index1);
                f.setNombre(nombre);
                f.setUrl(url);
                archivos.add(f);
            } else {
                DefaultSyntaxKit.initKit();
                javax.swing.JScrollPane scrollPag = new javax.swing.JScrollPane();
                JEditorPane pag = new JEditorPane();
                scrollPag.setViewportView(pag);
                pag.setContentType("text/java");
                tabs.addTab(nombre, scrollPag);
                CmpntTabPane pnlTab = new CmpntTabPane(tabs, 0, VtnCompilador.imageLenguaje1);

                tabs.setTabComponentAt(tabs.getTabCount() - 1, pnlTab);
                Archivo f = new Archivo();
                f.setIndex(tabs.getTabCount());
                f.setNombre(nombre);
                f.setUrl(url);
                archivos.add(f);
            }
        }

    }

}
