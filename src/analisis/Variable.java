package analisis;

/**
 * @author Los Bebelin
 */
public class Variable {

    private String variable;
    private String tipo;
    private boolean unica;
    private boolean inicializada;
    private int idAlcance;
    private String valor;

    public Variable() {
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Variable(String variable, String tipo, boolean unica, boolean inicializada, int idAlcance, String valor) {
        this.variable = variable;
        this.tipo = tipo;
        this.unica = unica;
        this.inicializada = inicializada;
        this.idAlcance = idAlcance;
        this.valor = valor;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isUnica() {
        return unica;
    }

    public void setUnica(boolean unica) {
        this.unica = unica;
    }

    public boolean isInicializada() {
        return inicializada;
    }

    public void setInicializada(boolean inicializada) {
        this.inicializada = inicializada;
    }

    public int getIdAlcance() {
        return idAlcance;
    }

    public void setIdAlcance(int idAlcance) {
        this.idAlcance = idAlcance;
    }
}
