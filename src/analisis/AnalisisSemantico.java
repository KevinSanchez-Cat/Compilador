package analisis;

import interfaces.PnlAnalisis;
import java.util.ArrayList;

/**
 * @author Kevin_Sanchez
 */
public class AnalisisSemantico {

    private static final int VARIABLE = 50;
    private static final int TIPO_DATO = 1;
    private static final int PUNTO_Y_COMA = 2;
    private static final int IGUAL = 3;
    private static final int OPERADOR_ARITMETICO = 4;
    private static final int OPERADOR_RELACIONAL = 5;
    private static final int LLAVE_C = 10;
    private static final int COMA = 15;
    private static final int OPERADOR_LOGICO = 18;
    private static final int TRUE = 20;
    private static final int FALSE = 21;
    private static final int ENTERO = 51;
    private static final int FLOTANTE = 52;
    private static final int CADENA = 53;

    public static ArrayList<Variable> tablaSimbolos = new ArrayList<>();
    public static String mensajesError = "";

    public static String analizar(ArrayList<TablaSimbolos> lexema) {
//        System.out.println("");
        tablaSimbolos = new ArrayList<>();
        Boolean repite;
        String tipo;
        for (TablaSimbolos tokenActual : lexema) {
            switch (tokenActual.getNumToken()) {
                case VARIABLE:
                    String variable = tokenActual.getLexema();
                    TablaSimbolos tokenAnterior = lastToken(lexema, tokenActual);
                    TablaSimbolos tokenSiguiente;
                    switch (tokenAnterior.getNumToken()) {
                        case TIPO_DATO:
                            if (!isDuplicated(tablaSimbolos, tokenActual.getLexema())) {
                                tokenSiguiente = nextToken(lexema, tokenActual);
                                tipo = tokenAnterior.getLexema();
                                switch (tokenSiguiente.getNumToken()) {
                                    case PUNTO_Y_COMA:
                                        if (!isDuplicated(tablaSimbolos, tokenActual.getLexema())) {
                                            agregarVarTabSimbolos(tablaSimbolos, tokenActual.getLexema(), tokenAnterior.getLexema(), true, false, 1, null);
                                        } else {
                                            errorVariableDefinida(tokenActual);
                                            cambiarEstadoUnicaVar(tablaSimbolos, tokenActual.getLexema(), false);
                                        }
                                        break;
                                    case IGUAL:
                                        ArrayList<Lexema> arrInt = new ArrayList<>();
                                        Lexema a = new Lexema();
                                        a.setLexema(tokenActual.lexema);
                                        arrInt.add(a);
                                        Lexema a2 = new Lexema();
                                        a2.setLexema(tokenSiguiente.lexema);
                                        arrInt.add(a2);

                                        tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                        ArrayList<String> arr = new ArrayList<>();

                                        boolean agrega = true,
                                         hacer = true;
                                        if (tokenSiguiente.getNumToken() == PUNTO_Y_COMA) {

                                            errorCaracterInserperado(tokenSiguiente);
                                            break;
                                        } else if (tokenSiguiente.getNumToken() == COMA) {

                                            errorCaracterInserperado(tokenSiguiente);
                                            break;
                                        }
                                        TablaSimbolos t = null;
                                        while (agrega) {
                                            if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                // Validar si esta declarada
                                                if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                    errorNoDeclarada(tokenSiguiente);
                                                    hacer = false;
                                                } else {
                                                    if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                        errorNoInicializada(tokenSiguiente);
                                                        hacer = false;
                                                    } else {
                                                        Lexema a3 = new Lexema();
                                                        a3.setLexema(tokenSiguiente.lexema);
                                                        a3.setTipoToken(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                        arrInt.add(a3);
                                                        arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                    }
                                                }
                                            } else {
                                                Lexema a3 = new Lexema();
                                                a3.setLexema(tokenSiguiente.lexema);
                                                arrInt.add(a3);
                                                arr.add(tokenSiguiente.lexema);
                                            }
                                            t = tokenSiguiente;
                                            tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                            if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                agrega = false;
                                                if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                    errorCaracterInserperado(t);
                                                    hacer = false;
                                                }
                                            }

                                        }
                                        if (hacer) {
                                            String tipoDatoID = Semantico.tipoDatoID(tipo);
                                            if (tipoDatoID.equals("?")) {
                                                // System.out.println("2- ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                            } else {
                                                String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                    agregarVarTabSimbolos(tablaSimbolos, tokenActual.getLexema(), tokenAnterior.getLexema(), true, true, 1, valor);
                                                    Intermedio.recibeTokens(arrInt, null);
                                                    Intermedio.operaciones += "*************************   RESULTADO   *************************\n";
                                                    Intermedio.operaciones += variable + " = " + valor + "\n\n\n";
                                                    PnlAnalisis.txtAreaIntermedio.setText(Intermedio.operaciones);
                                                    Main.ResCodigoIntermedio = Intermedio.operaciones;
                                                } else {
                                                    switch (tipoDatoValor) {
                                                        case "STRING":
                                                            tipoDatoValor = "String";
                                                            break;
                                                        case "INTEGER":
                                                            tipoDatoValor = "int";
                                                            break;
                                                        case "FLOAT":
                                                            tipoDatoValor = "float";
                                                            break;
                                                        case "BOOLEAN":
                                                            tipoDatoValor = "boolean";
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                    errorTipos(t, tipoDatoValor, tipo);
                                                }

                                            }
                                        }
                                        tokenActual = nextToken(lexema, t);
                                        switch (tokenActual.getNumToken()) {
                                            case COMA:
                                                repite = true;
                                                while (repite) {
                                                    tokenActual = nextToken(lexema, tokenActual);
                                                    switch (tokenActual.getNumToken()) {
                                                        case VARIABLE:
                                                            if (!isDuplicated(tablaSimbolos, tokenActual.getLexema())) {
                                                                tokenSiguiente = nextToken(lexema, tokenActual);
                                                                switch (tokenSiguiente.getNumToken()) {
                                                                    case IGUAL:
                                                                        tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                                        arr = new ArrayList<>();
                                                                        agrega = true;
                                                                        hacer = true;
                                                                        if (tokenSiguiente.getNumToken() == PUNTO_Y_COMA) {

                                                                            errorCaracterInserperado(tokenSiguiente);
                                                                            break;
                                                                        } else if (tokenSiguiente.getNumToken() == COMA) {

                                                                            errorCaracterInserperado(tokenSiguiente);
                                                                            break;
                                                                        }
                                                                        t = null;
                                                                        while (agrega) {
                                                                            if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                                                // Validar si esta declarada
                                                                                if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                                                    errorNoDeclarada(tokenSiguiente);
                                                                                    hacer = false;
                                                                                } else {
                                                                                    if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                                        errorNoInicializada(tokenSiguiente);
                                                                                        hacer = false;
                                                                                    } else {
                                                                                        Lexema a3 = new Lexema();
                                                                                        a3.setLexema(tokenSiguiente.lexema);
                                                                                        a3.setTipoToken(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                                                        arrInt.add(a3);

                                                                                        arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                Lexema a3 = new Lexema();
                                                                                a3.setLexema(tokenSiguiente.lexema);
                                                                                arrInt.add(a3);

                                                                                arr.add(tokenSiguiente.lexema);
                                                                            }
                                                                            t = tokenSiguiente;
                                                                            tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                                            if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                                                agrega = false;
                                                                                if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                                                    errorCaracterInserperado(t);
                                                                                    hacer = false;
                                                                                }
                                                                            }

                                                                        }
                                                                        if (hacer) {
                                                                            String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                                            if (tipoDatoID.equals("?")) {
                                                                                //   System.out.println("3 - ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                                            } else {
                                                                                String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                                                String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                                                if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                                                    agregarVarTabSimbolos(tablaSimbolos, tokenActual.getLexema(), tokenAnterior.getLexema(), true, true, 1, valor);
                                                                                    Intermedio.recibeTokens(arrInt, null);
                                                                                    Intermedio.operaciones += "*************************   RESULTADO   *************************\n";
                                                                                    Intermedio.operaciones += variable + " = " + valor + "\n\n\n";
                                                                                    PnlAnalisis.txtAreaIntermedio.setText(Intermedio.operaciones);
                                                                                    Main.ResCodigoIntermedio = Intermedio.operaciones;
                                                                                } else {
                                                                                    switch (tipoDatoValor) {
                                                                                        case "STRING":
                                                                                            tipoDatoValor = "String";
                                                                                            break;
                                                                                        case "INTEGER":
                                                                                            tipoDatoValor = "int";
                                                                                            break;
                                                                                        case "FLOAT":
                                                                                            tipoDatoValor = "float";
                                                                                            break;
                                                                                        case "BOOLEAN":
                                                                                            tipoDatoValor = "boolean";
                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }
                                                                                    errorTipos(t, tipoDatoValor, tipo);
                                                                                }
                                                                            }

                                                                        }
                                                                        break;
                                                                    default:
                                                                        agregarVarTabSimbolos(tablaSimbolos, tokenActual.getLexema(), tipo, true, false, 1, null);
                                                                        break;
                                                                }
                                                            } else {
                                                                errorVariableDefinida(tokenActual);
                                                                cambiarEstadoUnicaVar(tablaSimbolos, tokenActual.getLexema(), false);
                                                            }
                                                            break;
                                                        case PUNTO_Y_COMA:
                                                            repite = false;
                                                            break;
                                                    }
                                                }
                                                break;
                                            case PUNTO_Y_COMA:
                                                if (!isDuplicated(tablaSimbolos, tokenActual.getLexema())) {
                                                    //                                            agregarVarTabSimbolos(tablaSimbolos, tokenActual.getLexema(), tokenAnterior.getLexema(), true, false, 1,null);
                                                } else {
                                                    errorVariableDefinida(tokenActual);
                                                    cambiarEstadoUnicaVar(tablaSimbolos, tokenActual.getLexema(), false);
                                                }
                                                break;
                                            default:
                                                errorCaracterInserperado(tokenActual);
                                                break;
                                        }
                                        break;
                                    case COMA:
                                        if (!isDuplicated(tablaSimbolos, tokenActual.getLexema())) {
                                            agregarVarTabSimbolos(tablaSimbolos, tokenActual.getLexema(), tokenAnterior.getLexema(), true, false, 1, null);
                                        } else {
                                            errorVariableDefinida(tokenActual);
                                            cambiarEstadoUnicaVar(tablaSimbolos, tokenActual.getLexema(), false);
                                        }
                                        repite = true;
                                        while (repite) {
                                            tokenActual = nextToken(lexema, tokenActual);
                                            switch (tokenActual.getNumToken()) {
                                                case VARIABLE:
                                                    if (!isDuplicated(tablaSimbolos, tokenActual.getLexema())) {
                                                        tokenSiguiente = nextToken(lexema, tokenActual);
                                                        switch (tokenSiguiente.getNumToken()) {
                                                            case IGUAL:
                                                                arrInt = new ArrayList<>();
                                                                a = new Lexema();
                                                                a.setLexema(tokenActual.lexema);
                                                                arrInt.add(a);
                                                                a2 = new Lexema();
                                                                a2.setLexema(tokenSiguiente.lexema);
                                                                arrInt.add(a2);
                                                                tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                                arr = new ArrayList<>();
                                                                agrega = true;
                                                                hacer = true;
                                                                if (tokenSiguiente.getNumToken() == PUNTO_Y_COMA) {

                                                                    errorCaracterInserperado(tokenSiguiente);
                                                                    break;
                                                                } else if (tokenSiguiente.getNumToken() == COMA) {

                                                                    errorCaracterInserperado(tokenSiguiente);
                                                                    break;
                                                                }
                                                                t = null;
                                                                while (agrega) {
                                                                    if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                                        // Validar si esta declarada
                                                                        if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                                            errorNoDeclarada(tokenSiguiente);
                                                                            hacer = false;
                                                                        } else {
                                                                            if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                                errorNoInicializada(tokenSiguiente);
                                                                                hacer = false;
                                                                            } else {
                                                                                Lexema a3 = new Lexema();
                                                                                a3.setLexema(tokenSiguiente.lexema);
                                                                                a3.setTipoToken(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                                                arrInt.add(a3);
                                                                                arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                                            }
                                                                        }
                                                                    } else {
                                                                        Lexema a3 = new Lexema();
                                                                        a3.setLexema(tokenSiguiente.lexema);
                                                                        arrInt.add(a3);
                                                                        arr.add(tokenSiguiente.lexema);
                                                                    }
                                                                    t = tokenSiguiente;
                                                                    tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                                    if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                                        agrega = false;
                                                                        if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                                            errorCaracterInserperado(t);
                                                                            hacer = false;
                                                                        }
                                                                    }

                                                                }
                                                                if (hacer) {
                                                                    String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                                    if (tipoDatoID.equals("?")) {
                                                                        //    System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                                    } else {
                                                                        String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                                        String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                                        if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                                            agregarVarTabSimbolos(tablaSimbolos, tokenActual.getLexema(), tokenAnterior.getLexema(), true, true, 1, valor);
                                                                            Intermedio.recibeTokens(arrInt, null);
                                                                            Intermedio.operaciones += "*************************   RESULTADO   *************************\n";
                                                                            Intermedio.operaciones += variable + " = " + valor + "\n\n\n";
                                                                            PnlAnalisis.txtAreaIntermedio.setText(Intermedio.operaciones);
                                                                            Main.ResCodigoIntermedio = Intermedio.operaciones;
                                                                        } else {
                                                                            switch (tipoDatoValor) {
                                                                                case "STRING":
                                                                                    tipoDatoValor = "String";
                                                                                    break;
                                                                                case "INTEGER":
                                                                                    tipoDatoValor = "int";
                                                                                    break;
                                                                                case "FLOAT":
                                                                                    tipoDatoValor = "float";
                                                                                    break;
                                                                                case "BOOLEAN":
                                                                                    tipoDatoValor = "boolean";
                                                                                    break;
                                                                                default:
                                                                                    break;
                                                                            }
                                                                            errorTipos(t, tipoDatoValor, tipo);
                                                                        }
                                                                    }
                                                                }
                                                                break;
                                                            default:
                                                                agregarVarTabSimbolos(tablaSimbolos, tokenActual.getLexema(), tipo, true, false, 1, null);
                                                                break;
                                                        }
                                                    } else {
                                                        errorVariableDefinida(tokenActual);
                                                        cambiarEstadoUnicaVar(tablaSimbolos, tokenActual.getLexema(), false);
                                                    }
                                                    break;
                                                case PUNTO_Y_COMA:
                                                    repite = false;
                                                    break;
                                            }
                                        }
                                        break;
                                }
                            } else {
                                errorVariableDefinida(tokenActual);
                            }
                            break;
                        case PUNTO_Y_COMA: // Asignaciones
                            tipo = tipo(tablaSimbolos, tokenActual.lexema);
                            String var = tokenActual.lexema;
                            //    System.out.println(var + "ASIG");

                            ArrayList<Lexema> arrInt = new ArrayList<>();
                            Lexema a = new Lexema();
                            a.setLexema(tokenActual.lexema);
                            arrInt.add(a);

                            boolean revisa = true,
                             hacer = true;
                            if (!isDuplicated(tablaSimbolos, var)) {
                                errorNoDeclarada(tokenActual);
                                break;
                            } else { // Actualizar valor
                                tokenActual = nextToken(lexema, tokenActual);
                                //  System.out.println(tokenActual.lexema + "+");
                                switch (tokenActual.getNumToken()) {
                                    case IGUAL:

                                        Lexema a2 = new Lexema();
                                        a2.setLexema(tokenActual.lexema);
                                        arrInt.add(a2);

                                        tokenActual = nextToken(lexema, tokenActual);
                                     //  System.out.println(tokenActual.lexema);
                                        ArrayList<String> arr = new ArrayList<>();
                                        TablaSimbolos t = null;
                                        boolean agrega = true;
                                        switch (tokenActual.getNumToken()) {
                                            case VARIABLE:
                                                if (!isDuplicated(tablaSimbolos, tokenActual.getLexema())) {
                                                    errorNoDeclarada(tokenActual);

                                                } else { // Actualizar valor
                                                    if (valor(tablaSimbolos, tokenActual.lexema) == null) {
                                                        errorNoInicializada(tokenActual);

                                                    } else {
                                                        Lexema a5 = new Lexema();
                                                        a5.setLexema(tokenActual.lexema);
                                                        arrInt.add(a5);
                                                        arr.add(valor(tablaSimbolos, tokenActual.lexema));
                                                        //  System.out.println("VALOR ---- " + valor(tablaSimbolos, tokenActual.lexema));
                                                        tokenSiguiente = nextToken(lexema, tokenActual);
                                                        //   System.out.println(tokenSiguiente.lexema);
                                                        if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                            agrega = false;
                                                            t = tokenActual;
                                                        }
                                                        while (agrega) {
                                                            if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                                // Validar si esta declarada
                                                                if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                                    errorNoDeclarada(tokenSiguiente);

                                                                    hacer = false;
                                                                } else {
                                                                    if (valor(tablaSimbolos, tokenActual.lexema) == null) {
                                                                        errorNoInicializada(tokenSiguiente);

                                                                        hacer = false;
                                                                    } else {
                                                                        Lexema a3 = new Lexema();
                                                                        a3.setLexema(tokenSiguiente.lexema);
                                                                        a3.setTipoToken(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                                        arrInt.add(a3);
                                                                        //   System.out.println("VALOR ---- " + valor(tablaSimbolos, tokenSiguiente.lexema));
                                                                        arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                                    }
                                                                }
                                                            } else {
                                                                Lexema a3 = new Lexema();
                                                                a3.setLexema(tokenSiguiente.lexema);
                                                                arrInt.add(a3);

                                                                arr.add(tokenSiguiente.lexema);
                                                            }
                                                            t = tokenSiguiente;
                                                            tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                            if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                                agrega = false;
                                                                if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                                    errorCaracterInserperado(t);
                                                                    hacer = false;
                                                                }
                                                            }
                                                        }
                                                        if (hacer) {
                                                            String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                            if (tipoDatoID.equals("?")) {
                                                                // System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                            } else {
                                                                String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                                String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                                if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                                    cambiarValor(tablaSimbolos, var, valor);
                                                                    cambiarInicializada(tablaSimbolos, var, true);
                                                                    Intermedio.recibeTokens(arrInt, null);
                                                                    Intermedio.operaciones += "*************************   RESULTADO   *************************\n";
                                                                    Intermedio.operaciones += variable + " = " + valor + "\n\n\n";
                                                                    PnlAnalisis.txtAreaIntermedio.setText(Intermedio.operaciones);
                                                                    Main.ResCodigoIntermedio = Intermedio.operaciones;
                                                                } else {
                                                                    switch (tipoDatoValor) {
                                                                        case "STRING":
                                                                            tipoDatoValor = "String";
                                                                            break;
                                                                        case "INTEGER":
                                                                            tipoDatoValor = "int";
                                                                            break;
                                                                        case "FLOAT":
                                                                            tipoDatoValor = "float";
                                                                            break;
                                                                        case "BOOLEAN":
                                                                            tipoDatoValor = "boolean";
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                                    errorTipos(t, tipoDatoValor, tipo);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            case ENTERO:
                                                Lexema a5 = new Lexema();
                                                a5.setLexema(tokenActual.lexema);
                                                arrInt.add(a5);
                                                arr.add(tokenActual.lexema);
                                                tokenSiguiente = nextToken(lexema, tokenActual);
                                                // System.out.println(tokenSiguiente.lexema);
                                                if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                    agrega = false;
                                                    t = tokenActual;
                                                }
                                                while (agrega) {
                                                    if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                        // Validar si esta declarada
                                                        if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                            errorNoDeclarada(tokenSiguiente);

                                                            hacer = false;
                                                        } else {
                                                            //    System.out.println(tokenSiguiente.lexema + " # ");
                                                            if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                errorNoInicializada(tokenSiguiente);

                                                                hacer = false;
                                                            } else {

                                                                Lexema a3 = new Lexema();
                                                                a3.setLexema(tokenSiguiente.lexema);
                                                                a3.setTipoToken(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                                arrInt.add(a3);

                                                                arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                            }
                                                        }
                                                    } else {
                                                        Lexema a3 = new Lexema();
                                                        a3.setLexema(tokenSiguiente.lexema);
                                                        arrInt.add(a3);

                                                        arr.add(tokenSiguiente.lexema);
                                                    }
                                                    t = tokenSiguiente;
                                                    tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                    if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                        agrega = false;
                                                        if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                            errorCaracterInserperado(t);
                                                            hacer = false;
                                                        }
                                                    }
                                                }
                                                if (hacer) {
                                                    String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                    if (tipoDatoID.equals("?")) {
                                                        // System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                    } else {
                                                        String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                        String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                        if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                            cambiarValor(tablaSimbolos, var, valor);
                                                            cambiarInicializada(tablaSimbolos, var, true);
                                                            Intermedio.recibeTokens(arrInt, null);
                                                            Intermedio.operaciones += "*************************   RESULTADO   *************************\n";
                                                            Intermedio.operaciones += variable + " = " + valor + "\n\n\n";
                                                            PnlAnalisis.txtAreaIntermedio.setText(Intermedio.operaciones);
                                                            Main.ResCodigoIntermedio = Intermedio.operaciones;
                                                        } else {
                                                            switch (tipoDatoValor) {
                                                                case "STRING":
                                                                    tipoDatoValor = "String";
                                                                    break;
                                                                case "INTEGER":
                                                                    tipoDatoValor = "int";
                                                                    break;
                                                                case "FLOAT":
                                                                    tipoDatoValor = "float";
                                                                    break;
                                                                case "BOOLEAN":
                                                                    tipoDatoValor = "boolean";
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            errorTipos(t, tipoDatoValor, tipo);
                                                        }
                                                    }
                                                }
                                                break;
                                            case FLOTANTE:
                                                Lexema a6 = new Lexema();
                                                a6.setLexema(tokenActual.lexema);
                                                arrInt.add(a6);

                                                arr.add(tokenActual.lexema);
                                                tokenSiguiente = nextToken(lexema, tokenActual);
                                                //  System.out.println(tokenSiguiente.lexema);
                                                if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                    agrega = false;
                                                    t = tokenActual;
                                                }
                                                while (agrega) {
                                                    if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                        // Validar si esta declarada
                                                        if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                            errorNoDeclarada(tokenSiguiente);

                                                            hacer = false;
                                                        } else {
                                                            //   System.out.println(tokenSiguiente.lexema + " # ");
                                                            if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                errorNoInicializada(tokenSiguiente);

                                                                hacer = false;
                                                            } else {
                                                                Lexema a3 = new Lexema();
                                                                a3.setLexema(tokenSiguiente.lexema);
                                                                a3.setTipoToken(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                                arrInt.add(a3);

                                                                arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                            }
                                                        }
                                                    } else {
                                                        Lexema a3 = new Lexema();
                                                        a3.setLexema(tokenSiguiente.lexema);
                                                        arrInt.add(a3);
                                                        arr.add(tokenSiguiente.lexema);
                                                    }
                                                    t = tokenSiguiente;
                                                    tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                    if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                        agrega = false;
                                                        if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                            errorCaracterInserperado(t);
                                                            hacer = false;
                                                        }
                                                    }
                                                }
                                                if (hacer) {
                                                    String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                    if (tipoDatoID.equals("?")) {
                                                        //  System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                    } else {
                                                        String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                        String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                        if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                            cambiarValor(tablaSimbolos, var, valor);
                                                            cambiarInicializada(tablaSimbolos, var, true);
                                                            Intermedio.recibeTokens(arrInt, null);
                                                            Intermedio.operaciones += "*************************   RESULTADO   *************************\n";
                                                            Intermedio.operaciones += variable + " = " + valor + "\n\n\n";
                                                            PnlAnalisis.txtAreaIntermedio.setText(Intermedio.operaciones);
                                                            Main.ResCodigoIntermedio = Intermedio.operaciones;
                                                        } else {
                                                            switch (tipoDatoValor) {
                                                                case "STRING":
                                                                    tipoDatoValor = "String";
                                                                    break;
                                                                case "INTEGER":
                                                                    tipoDatoValor = "int";
                                                                    break;
                                                                case "FLOAT":
                                                                    tipoDatoValor = "float";
                                                                    break;
                                                                case "BOOLEAN":
                                                                    tipoDatoValor = "boolean";
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            errorTipos(t, tipoDatoValor, tipo);
                                                        }
                                                    }
                                                }
                                                break;
                                            case FALSE:
                                                arr.add(tokenActual.lexema);
                                                tokenSiguiente = nextToken(lexema, tokenActual);
                                                //  System.out.println(tokenSiguiente.lexema);
                                                if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                    agrega = false;
                                                    t = tokenActual;
                                                }
                                                while (agrega) {
                                                    if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                        // Validar si esta declarada
                                                        if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                            errorNoDeclarada(tokenSiguiente);

                                                            hacer = false;
                                                        } else {
                                                            //        System.out.println(tokenSiguiente.lexema + " # ");
                                                            if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                errorNoInicializada(tokenSiguiente);

                                                                hacer = false;
                                                            } else {
                                                                arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                            }
                                                        }
                                                    } else {
                                                        arr.add(tokenSiguiente.lexema);
                                                    }
                                                    t = tokenSiguiente;
                                                    tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                    if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                        agrega = false;
                                                        if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                            errorCaracterInserperado(t);
                                                            hacer = false;
                                                        }
                                                    }
                                                }
                                                if (hacer) {
                                                    String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                    if (tipoDatoID.equals("?")) {
                                                        //    System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                    } else {
                                                        String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                        String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                        if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                            cambiarValor(tablaSimbolos, var, valor);
                                                            cambiarInicializada(tablaSimbolos, var, true);
                                                        } else {
                                                            switch (tipoDatoValor) {
                                                                case "STRING":
                                                                    tipoDatoValor = "String";
                                                                    break;
                                                                case "INTEGER":
                                                                    tipoDatoValor = "int";
                                                                    break;
                                                                case "FLOAT":
                                                                    tipoDatoValor = "float";
                                                                    break;
                                                                case "BOOLEAN":
                                                                    tipoDatoValor = "boolean";
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            errorTipos(t, tipoDatoValor, tipo);
                                                        }
                                                    }
                                                }
                                                break;
                                            case TRUE:
                                                arr.add(tokenActual.lexema);
                                                tokenSiguiente = nextToken(lexema, tokenActual);
                                                // System.out.println(tokenSiguiente.lexema);
                                                if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                    agrega = false;
                                                    t = tokenActual;
                                                }
                                                while (agrega) {
                                                    if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                        // Validar si esta declarada
                                                        if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                            errorNoDeclarada(tokenSiguiente);

                                                            hacer = false;
                                                        } else {
                                                            //         System.out.println(tokenSiguiente.lexema + " # ");
                                                            if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                errorNoInicializada(tokenSiguiente);

                                                                hacer = false;
                                                            } else {
                                                                arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                            }
                                                        }
                                                    } else {
                                                        arr.add(tokenSiguiente.lexema);
                                                    }
                                                    t = tokenSiguiente;
                                                    tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                    if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                        agrega = false;
                                                        if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                            errorCaracterInserperado(t);
                                                            hacer = false;
                                                        }
                                                    }
                                                }
                                                if (hacer) {
                                                    String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                    if (tipoDatoID.equals("?")) {
                                                        //      System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                    } else {
                                                        String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                        String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                        if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                            cambiarValor(tablaSimbolos, var, valor);
                                                            cambiarInicializada(tablaSimbolos, var, true);
                                                        } else {
                                                            switch (tipoDatoValor) {
                                                                case "STRING":
                                                                    tipoDatoValor = "String";
                                                                    break;
                                                                case "INTEGER":
                                                                    tipoDatoValor = "int";
                                                                    break;
                                                                case "FLOAT":
                                                                    tipoDatoValor = "float";
                                                                    break;
                                                                case "BOOLEAN":
                                                                    tipoDatoValor = "boolean";
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            errorTipos(t, tipoDatoValor, tipo);
                                                        }
                                                    }
                                                }
                                                break;
                                            case CADENA:
                                                arr.add(tokenActual.lexema);
                                                tokenSiguiente = nextToken(lexema, tokenActual);
                                                //      System.out.println(tokenSiguiente.lexema);
                                                if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                    agrega = false;
                                                    t = tokenActual;
                                                }
                                                while (agrega) {
                                                    if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                        // Validar si esta declarada
                                                        if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                            errorNoDeclarada(tokenSiguiente);

                                                            hacer = false;
                                                        } else {
                                                            //        System.out.println(tokenSiguiente.lexema + " # ");
                                                            if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                errorNoInicializada(tokenSiguiente);

                                                                hacer = false;
                                                            } else {
                                                                arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                            }
                                                        }
                                                    } else {
                                                        arr.add(tokenSiguiente.lexema);
                                                    }
                                                    t = tokenSiguiente;
                                                    tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                    if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                        agrega = false;
                                                        if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                            errorCaracterInserperado(t);
                                                            hacer = false;
                                                        }
                                                    }
                                                }
                                                if (hacer) {
                                                    String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                    if (tipoDatoID.equals("?")) {
                                                        //    System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                    } else {
                                                        String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                        String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                        if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                            cambiarValor(tablaSimbolos, var, valor);
                                                            cambiarInicializada(tablaSimbolos, var, true);
                                                        } else {
                                                            switch (tipoDatoValor) {
                                                                case "STRING":
                                                                    tipoDatoValor = "String";
                                                                    break;
                                                                case "INTEGER":
                                                                    tipoDatoValor = "int";
                                                                    break;
                                                                case "FLOAT":
                                                                    tipoDatoValor = "float";
                                                                    break;
                                                                case "BOOLEAN":
                                                                    tipoDatoValor = "boolean";
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            errorTipos(t, tipoDatoValor, tipo);
                                                        }
                                                    }
                                                }
                                                break;
                                        }
                                        break;
                                    default:
                                        errorCaracterInserperado(tokenActual);
                                        break;
                                }

                            }
                            break;
                        case LLAVE_C: // Asignaciones
                            tipo = tipo(tablaSimbolos, tokenActual.lexema);
                            var = tokenActual.lexema;
                            //   System.out.println(var + "ASIG");
                            revisa = true;
                            hacer = true;
                            if (!isDuplicated(tablaSimbolos, var)) {
                                errorNoDeclarada(tokenActual);
                                break;
                            } else { // Actualizar valor
                                tokenActual = nextToken(lexema, tokenActual);
                                //    System.out.println(tokenActual.lexema + "+");
                                switch (tokenActual.getNumToken()) {
                                    case IGUAL:
                                        tokenActual = nextToken(lexema, tokenActual);
                                        //      System.out.println(tokenActual.lexema);
                                        ArrayList<String> arr = new ArrayList<>();
                                        TablaSimbolos t = null;
                                        boolean agrega = true;
                                        switch (tokenActual.getNumToken()) {
                                            case VARIABLE:
                                                if (!isDuplicated(tablaSimbolos, tokenActual.getLexema())) {
                                                    errorNoDeclarada(tokenActual);

                                                } else { // Actualizar valor
                                                    if (valor(tablaSimbolos, tokenActual.lexema) == null) {
                                                        errorNoInicializada(tokenActual);

                                                    } else {
                                                        arr.add(valor(tablaSimbolos, tokenActual.lexema));
                                                        tokenSiguiente = nextToken(lexema, tokenActual);
                                                        //        System.out.println(tokenSiguiente.lexema);
                                                        if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                            agrega = false;
                                                            t = tokenActual;
                                                        }
                                                        while (agrega) {
                                                            if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                                // Validar si esta declarada
                                                                if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                                    errorNoDeclarada(tokenSiguiente);

                                                                    hacer = false;
                                                                } else {
                                                                    if (valor(tablaSimbolos, tokenActual.lexema) == null) {
                                                                        errorNoInicializada(tokenSiguiente);

                                                                        hacer = false;
                                                                    } else {
                                                                        arr.add(valor(tablaSimbolos, tokenActual.lexema));
                                                                    }
                                                                }
                                                            } else {
                                                                arr.add(tokenSiguiente.lexema);
                                                            }
                                                            t = tokenSiguiente;
                                                            tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                            if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                                agrega = false;
                                                                if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                                    errorCaracterInserperado(t);
                                                                    hacer = false;
                                                                }
                                                            }
                                                        }
                                                        if (hacer) {
                                                            String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                            if (tipoDatoID.equals("?")) {
                                                                //               System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                            } else {
                                                                String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                                String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                                if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                                    cambiarValor(tablaSimbolos, var, valor);
                                                                    cambiarInicializada(tablaSimbolos, var, true);
                                                                } else {
                                                                    switch (tipoDatoValor) {
                                                                        case "STRING":
                                                                            tipoDatoValor = "String";
                                                                            break;
                                                                        case "INTEGER":
                                                                            tipoDatoValor = "int";
                                                                            break;
                                                                        case "FLOAT":
                                                                            tipoDatoValor = "float";
                                                                            break;
                                                                        case "BOOLEAN":
                                                                            tipoDatoValor = "boolean";
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                                    errorTipos(t, tipoDatoValor, tipo);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            case ENTERO:
                                                arr.add(tokenActual.lexema);
                                                tokenSiguiente = nextToken(lexema, tokenActual);
                                                //    System.out.println(tokenSiguiente.lexema);
                                                if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                    agrega = false;
                                                    t = tokenActual;
                                                }
                                                while (agrega) {
                                                    if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                        // Validar si esta declarada
                                                        if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                            errorNoDeclarada(tokenSiguiente);

                                                            hacer = false;
                                                        } else {
                                                            //       System.out.println(tokenSiguiente.lexema + " # ");
                                                            if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                errorNoInicializada(tokenSiguiente);

                                                                hacer = false;
                                                            } else {
                                                                arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                            }
                                                        }
                                                    } else {
                                                        arr.add(tokenSiguiente.lexema);
                                                    }
                                                    t = tokenSiguiente;
                                                    tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                    if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                        agrega = false;
                                                        if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                            errorCaracterInserperado(t);
                                                            hacer = false;
                                                        }
                                                    }
                                                }
                                                if (hacer) {
                                                    String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                    if (tipoDatoID.equals("?")) {
                                                        //    System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                    } else {
                                                        String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                        String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                        if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                            cambiarValor(tablaSimbolos, var, valor);
                                                            cambiarInicializada(tablaSimbolos, var, true);
                                                        } else {
                                                            switch (tipoDatoValor) {
                                                                case "STRING":
                                                                    tipoDatoValor = "String";
                                                                    break;
                                                                case "INTEGER":
                                                                    tipoDatoValor = "int";
                                                                    break;
                                                                case "FLOAT":
                                                                    tipoDatoValor = "float";
                                                                    break;
                                                                case "BOOLEAN":
                                                                    tipoDatoValor = "boolean";
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            errorTipos(t, tipoDatoValor, tipo);
                                                        }
                                                    }
                                                }
                                                break;
                                            case FLOTANTE:
                                                arr.add(tokenActual.lexema);
                                                tokenSiguiente = nextToken(lexema, tokenActual);
                                                //   System.out.println(tokenSiguiente.lexema);
                                                if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                    agrega = false;
                                                    t = tokenActual;
                                                }
                                                while (agrega) {
                                                    if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                        // Validar si esta declarada
                                                        if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                            errorNoDeclarada(tokenSiguiente);

                                                            hacer = false;
                                                        } else {
                                                            //      System.out.println(tokenSiguiente.lexema + " # ");
                                                            if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                errorNoInicializada(tokenSiguiente);

                                                                hacer = false;
                                                            } else {
                                                                arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                            }
                                                        }
                                                    } else {
                                                        arr.add(tokenSiguiente.lexema);
                                                    }
                                                    t = tokenSiguiente;
                                                    tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                    if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                        agrega = false;
                                                        if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                            errorCaracterInserperado(t);
                                                            hacer = false;
                                                        }
                                                    }
                                                }
                                                if (hacer) {
                                                    String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                    if (tipoDatoID.equals("?")) {
                                                        //   System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                    } else {
                                                        String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                        String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                        if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                            cambiarValor(tablaSimbolos, var, valor);
                                                            cambiarInicializada(tablaSimbolos, var, true);
                                                        } else {
                                                            switch (tipoDatoValor) {
                                                                case "STRING":
                                                                    tipoDatoValor = "String";
                                                                    break;
                                                                case "INTEGER":
                                                                    tipoDatoValor = "int";
                                                                    break;
                                                                case "FLOAT":
                                                                    tipoDatoValor = "float";
                                                                    break;
                                                                case "BOOLEAN":
                                                                    tipoDatoValor = "boolean";
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            errorTipos(t, tipoDatoValor, tipo);
                                                        }
                                                    }
                                                }
                                                break;
                                            case CADENA:
                                                arr.add(tokenActual.lexema);
                                                tokenSiguiente = nextToken(lexema, tokenActual);
                                                //  System.out.println(tokenSiguiente.lexema);
                                                if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                    agrega = false;
                                                    t = tokenActual;
                                                }
                                                while (agrega) {
                                                    if (tokenSiguiente.getNumToken() == VARIABLE) {
                                                        // Validar si esta declarada
                                                        if (!isDuplicated(tablaSimbolos, tokenSiguiente.getLexema())) {
                                                            errorNoDeclarada(tokenSiguiente);
                                                            hacer = false;
                                                        } else {
                                                            //     System.out.println(tokenSiguiente.lexema + " # ");
                                                            if (valor(tablaSimbolos, tokenSiguiente.lexema) == null) {
                                                                errorNoInicializada(tokenSiguiente);
                                                                hacer = false;
                                                            } else {
                                                                arr.add(valor(tablaSimbolos, tokenSiguiente.lexema));
                                                            }
                                                        }
                                                    } else {
                                                        arr.add(tokenSiguiente.lexema);
                                                    }
                                                    t = tokenSiguiente;
                                                    tokenSiguiente = nextToken(lexema, tokenSiguiente);
                                                    if (tokenSiguiente.numToken == COMA || tokenSiguiente.numToken == PUNTO_Y_COMA) {
                                                        agrega = false;
                                                        if (t.getNumToken() == OPERADOR_ARITMETICO || t.getNumToken() == OPERADOR_LOGICO || t.getNumToken() == OPERADOR_RELACIONAL) {
                                                            errorCaracterInserperado(t);
                                                            hacer = false;
                                                        }
                                                    }
                                                }
                                                if (hacer) {
                                                    String tipoDatoID = Semantico.tipoDatoID(tipo);
                                                    if (tipoDatoID.equals("?")) {
                                                        //    System.out.println("4 -ERROR, ACTUALIZAR TABLA Y DECIR QUE NO ESTA DECLARADA");
                                                    } else {
                                                        String valor = Semantico.conversionArrayCola(arr); //RETORNA EL VALOR DE LA OPERACION
                                                        String tipoDatoValor = Semantico.tipoDato(valor); //RETORNA EL TIPO DE DATO DEL VALOR
                                                        if (Semantico.asignacion(tipoDatoID, tipoDatoValor)) { //VALIDA EL TIPO DE DATO DEL IDENTIFICADOR CON EL DEL VALOR
                                                            cambiarValor(tablaSimbolos, var, valor);
                                                            cambiarInicializada(tablaSimbolos, var, true);
                                                        } else {
                                                            switch (tipoDatoValor) {
                                                                case "STRING":
                                                                    tipoDatoValor = "String";
                                                                    break;
                                                                case "INTEGER":
                                                                    tipoDatoValor = "int";
                                                                    break;
                                                                case "FLOAT":
                                                                    tipoDatoValor = "float";
                                                                    break;
                                                                case "BOOLEAN":
                                                                    tipoDatoValor = "boolean";
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            errorTipos(t, tipoDatoValor, tipo);
                                                        }
                                                    }
                                                }
                                                break;
                                        }
                                        break;
                                    default:
                                        errorCaracterInserperado(tokenActual);
                                        break;
                                }

                            }
                            break;
                    }
                    break;
            }
        }
        return mensajesError;
    }

    private static void errorVariableDefinida(TablaSimbolos tokenActual) {
        mensajesError += "Error en la línea " + tokenActual.getNumLinea() + ": La variable <" + tokenActual.getLexema() + "> ya se encuentra definida\n";
    }

    private static void errorCaracterInserperado(TablaSimbolos tokenSiguiente) {
        mensajesError += "Error en la línea " + tokenSiguiente.getNumLinea() + ": No se esperaba <" + tokenSiguiente.getLexema() + ">\n";
    }

    private static void errorNoDeclarada(TablaSimbolos tokenSiguiente) {
        mensajesError += "Error en la línea " + tokenSiguiente.getNumLinea() + ": La variable <" + tokenSiguiente.getLexema() + "> no se encuentra declarada\n";
    }

    private static void errorNoInicializada(TablaSimbolos tokenSiguiente) {
        mensajesError += "Error en la línea " + tokenSiguiente.getNumLinea() + ": La variable <" + tokenSiguiente.getLexema() + "> no se encuentra inicializada\n";
    }

    private static void errorTipos(TablaSimbolos t, String tipoDatoValor, String tipo1) {
        mensajesError += "Error en la línea " + t.getNumLinea() + ": <" + tipoDatoValor + "> no compatible con <" + tipo1 + ">\n";
    }

    private static void cambiarEstadoUnicaVar(ArrayList<Variable> tablaSimbolos, String nombreVariable, boolean isUnica) {
        Variable variable = new Variable();

        for (Variable temp : tablaSimbolos) {
            if (temp.getVariable().equals(nombreVariable)) {
                variable = temp;
                break;
            }
        }

        variable.setUnica(isUnica);
    }

    private static void cambiarValor(ArrayList<Variable> tablaSimbolos, String nombreVariable, String valor) {
        Variable variable = new Variable();
        for (Variable temp : tablaSimbolos) {
            if (temp.getVariable().equals(nombreVariable)) {
                variable = temp;
                break;
            }
        }

        variable.setValor(valor);
    }

    private static void cambiarInicializada(ArrayList<Variable> tablaSimbolos, String nombreVariable, boolean valor) {
        Variable variable = new Variable();
        for (Variable temp : tablaSimbolos) {
            if (temp.getVariable().equals(nombreVariable)) {
                variable = temp;
                break;
            }
        }

        variable.setInicializada(valor);
    }

    private static TablaSimbolos lastToken(ArrayList<TablaSimbolos> tokens, TablaSimbolos token) {
        return tokens.get(tokens.indexOf(token) - 1);
    }

    private static boolean isDuplicated(ArrayList<Variable> tablaSimbolos, String nombreVariable) {
        return tablaSimbolos.stream().anyMatch((variable) -> (variable.getVariable().equals(nombreVariable)));
    }

    private static String valor(ArrayList<Variable> tablaSimbolos, String nombreVariable) {
        String valor = null;
        for (Variable variable : tablaSimbolos) {
            if (variable.getVariable().equals(nombreVariable)) {
                valor = variable.getValor();
            }
        }

        return valor;
    }

    private static String tipo(ArrayList<Variable> tablaSimbolos, String nombreVariable) {
        String valor = null;
        for (Variable variable : tablaSimbolos) {
            if (variable.getVariable().equals(nombreVariable)) {
                valor = variable.getTipo();
            }
        }

        return valor;
    }

    private static void agregarVarTabSimbolos(ArrayList<Variable> tablaSimbolos, String nombreVariable, String tipo, boolean unica,
            boolean inicializada, int idAlcance, String valor) {
        tablaSimbolos.add(new Variable(nombreVariable, tipo, unica, inicializada, idAlcance, valor));
    }

    private static TablaSimbolos nextToken(ArrayList<TablaSimbolos> tokens, TablaSimbolos token) {
        return tokens.get(tokens.indexOf(token) + 1);
    }

}
