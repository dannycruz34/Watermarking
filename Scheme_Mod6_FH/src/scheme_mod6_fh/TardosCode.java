package scheme_mod6_fh;

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
    
    public TardosCode(){
    }
    
    public TardosCode(double[] p, double k, boolean[][] huella) {
        this.p = p;
        this.k = k;
        this.huella = huella;
    }

    public double[] getP() {
        return p;
    }

    public void setP(double[] p) {
        this.p = p;
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
