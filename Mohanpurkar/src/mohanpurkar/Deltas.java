/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mohanpurkar;

import java.util.ArrayList;

/**
 *
 * @author DanniC
 */
public class Deltas {
    
    String id_usuario;
    ArrayList delta = new ArrayList();

    public Deltas(){
    
    }
    
    public Deltas(String id_usuario, ArrayList delta) {
        this.id_usuario = id_usuario;
        this.delta = delta;
    }
    

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public ArrayList getDelta() {
        return delta;
    }

    public void setDelta(ArrayList delta) {
        this.delta = delta;
    }
    
    
    
}
