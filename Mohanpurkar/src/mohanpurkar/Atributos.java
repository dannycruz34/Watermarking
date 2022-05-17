/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mohanpurkar;

/**
 *
 * @author DanniC
 */
public class Atributos {
    int attr=0;
    double media=0.0;
    double desviacion=0.0;
    double umbral=0.0;
    public Atributos(){
        attr=0;
        media=0;
        desviacion=0;
        umbral=0;

    }
    public Atributos(int attr, double media, double desviacion, double umbral) {
        this.attr = attr;
        this.media = media;
        this.desviacion = desviacion;
        this.umbral = umbral;
    }
    
    
    public int getAttr() {
        return attr;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    public double getDesviacion() {
        return desviacion;
    }

    public void setDesviacion(double desviacion) {
        this.desviacion = desviacion;
    }

    public double getUmbral() {
        return umbral;
    }

    public void setUmbral(double umbral) {
        this.umbral = umbral;
    }
    
}
