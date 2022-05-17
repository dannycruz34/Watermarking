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
public class Particion {
    String id_key;
    int particion;
    
    public Particion(){}
    public Particion(String id_key, int particion) {
        this.id_key = id_key;
        this.particion = particion;
    }
       
    public String getId_key() {
        return id_key;
    }

    public void setId_key(String id_key) {
        this.id_key = id_key;
    }

    public int getParticion() {
        return particion;
    }

    public void setParticion(int particion) {
        this.particion = particion;
    }
    
}
