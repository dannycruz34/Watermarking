/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author DanniC
 */
public class Parametros {

    public Parametros(){
        huella = this.huella;
        key_secret = this.key_secret;
        nombreTabla = this.nombreTabla;
        ftupla = this.ftupla;
        numAttr = this.numAttr;
        lsb = this.lsb;
        nA = this.nA;
    }
    
    public String getHuella() {
        return huella;
    }

    public void setHuella(String huella) {
        this.huella = huella;
    }

    public String getKey_secret() {
        return key_secret;
    }

    public void setKey_secret(String key_secret) {
        this.key_secret = key_secret;
    }

    public String getNombreTabla() {
        return nombreTabla;
    }

    public void setNombreTabla(String nombreTabla) {
        this.nombreTabla = nombreTabla;
    }

    public String getFtupla() {
        return ftupla;
    }

    public void setFtupla(String ftupla) {
        this.ftupla = ftupla;
    }

    public String getNumAttr() {
        return numAttr;
    }

    public void setNumAttr(String numAttr) {
        this.numAttr = numAttr;
    }

    public String getLsb() {
        return lsb;
    }

    public void setLsb(String lsb) {
        this.lsb = lsb;
    }
    
   String huella;
   String key_secret;
   String nombreTabla;
   String ftupla;
   String numAttr;
   String lsb;
   String nA;

    public String getnA() {
        return nA;
    }

    public void setnA(String nA) {
        this.nA = nA;
    }
   
}
