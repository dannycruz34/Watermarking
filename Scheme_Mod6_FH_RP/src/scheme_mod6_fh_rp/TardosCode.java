package scheme_mod6_fh_rp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author DanniC
 */
public class TardosCode {
    
    double[] p;
    double k;
    boolean[][] huella;
    int tamColusion;
    
    public TardosCode(){
    }
    
    public TardosCode(double[] p, double k, boolean[][] huella,int tamColusion) {
        this.p = p;
        this.k = k;
        this.huella = huella;
        this.tamColusion = tamColusion;
    }

    public double[] getP() {
        return p;
    }

    public void setP(double[] p) {
        this.p = p;
    }

    public int getTamColusion() {
        return tamColusion;
    }

    public void setTamColusion(int tamColusion) {
        this.tamColusion = tamColusion;
    }
    
    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public boolean[][] getHuella() {
        return huella;
    }

    public void setHuella(boolean[][] huella) {
        this.huella = huella;
    }
    
    
    
}
