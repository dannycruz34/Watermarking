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
public class Grupos {
    
    int particion;
    Atributos[] atributos= new Atributos[10];
    public Grupos(){
         for (int i = 0; i < 10; i++) {
            atributos[i] = new Atributos();
        }
    }
    public Grupos(int particion) {
        this.particion = particion;
    }

    public int getParticion() {
        return particion;
    }

    public void setParticion(int particion) {
        this.particion = particion;
    }

    public Atributos[] getAtributos() {
        return atributos;
    }

    public void setAtributos(Atributos[] atributos) {
        this.atributos = atributos;
    }

    
}
