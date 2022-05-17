/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheme_mod6_fh_rp;

/**
 *
 * @author DanniC
 */
public class ParametrosDeteccion {
    String key_secret;
    String nombreTabla;
    String ftupla;
    String numAttr;
    String fh;
    String longitud;
    TardosCode codigo;

    public ParametrosDeteccion(){
        this.key_secret = key_secret;
        this.nombreTabla = nombreTabla;
        this.ftupla = ftupla;
        this.numAttr = numAttr;
        this.fh = fh;
        this.longitud = longitud;
        this.codigo = codigo;
    }
    
    public ParametrosDeteccion(String key_secret, String nombreTabla, String ftupla, String numAttr, String lsb, String fh, String longitud, TardosCode codigo) {
        this.key_secret = key_secret;
        this.nombreTabla = nombreTabla;
        this.ftupla = ftupla;
        this.numAttr = numAttr;
        this.fh = fh;
        this.longitud = longitud;
        this.codigo = codigo;
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


    public String getFh() {
        return fh;
    }

    public void setFh(String fh) {
        this.fh = fh;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public TardosCode getCodigo() {
        return codigo;
    }

    public void setCodigo(TardosCode codigo) {
        this.codigo = codigo;
    }
    
    
    
    
}
