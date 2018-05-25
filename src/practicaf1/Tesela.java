package practicaf1;

/**
 *
 * @author Ivan Toro and David Muntal
 */
public class Tesela {
    
    /// ATRIBUTOS
    
    private int id_tesela_base;
    private int x_base;
    private int y_base;
    private int x_destino;
    private int y_destino;
    private double valor_comp;

    /**
     * Constructor 
     * 
     * @param id
     * @param xbase
     * @param ybase
     * @param xdestino
     * @param ydestino
     * @param valor 
     */
    Tesela(int id, int xbase, int ybase, int xdestino, int ydestino, double valor){
        this.id_tesela_base = id;
        this.x_base = xbase;
        this.y_base = ybase;
        this.x_destino = xdestino;
        this.y_destino = ydestino;
        this.valor_comp = valor;
    }

    /// GETTERS Y SETTERS
    
    public int getId_tesela_base() {
        return id_tesela_base;
    }

    public void setId_tesela_base(int id_tesela_base) {
        this.id_tesela_base = id_tesela_base;
    }

    public int getX_base() {
        return x_base;
    }

    public void setX_base(int x_base) {
        this.x_base = x_base;
    }

    public int getY_base() {
        return y_base;
    }

    public void setY_base(int y_base) {
        this.y_base = y_base;
    }

    public int getX_destino() {
        return x_destino;
    }

    public void setX_destino(int x_destino) {
        this.x_destino = x_destino;
    }

    public int getY_destino() {
        return y_destino;
    }

    public void setY_destino(int y_destino) {
        this.y_destino = y_destino;
    }

    public double getValor_comp() {
        return valor_comp;
    }

    public void setValor_comp(double valor_comp) {
        this.valor_comp = valor_comp;
    }
    
    /**
     * Devuelve en formato toString los datos de la tesela
     * 
     * @return 
     */
    @Override
    public String toString(){
        return this.id_tesela_base + "," + this.x_base + "," + this.y_base + "," + this.x_destino + "," + this.y_destino + "," + this.valor_comp;
    }
}
