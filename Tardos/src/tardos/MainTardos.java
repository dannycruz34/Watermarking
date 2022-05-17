/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tardos;

import java.util.ArrayList;

/**
 *
 * @author DanniC
 */
public class MainTardos {
    public static void main(String[] args) {
        int numberOfUsers = 10;
        int collusionSize = 2;
        double epsilon = 0.9;
        boolean[][] huellas;
        ArrayList culpables;

                
        Tardos codigo = new Tardos(numberOfUsers,collusionSize,epsilon);
        huellas = codigo.generateMatrix(numberOfUsers,collusionSize,epsilon);
        
        /*
        //Recuperar fingerprint
        String[] fingerprint = new String[numberOfUsers];
        String auxiliar="";
        
        for (int i = 0; i < numberOfUsers; i++) {
            for (int j = 0; j < huellas[0].length; j++) {
                 if(huellas[i][j])               
                    auxiliar += 1;
                else
                    auxiliar += 0;
            }    
            fingerprint[i] = auxiliar;
            System.out.println("User " + i + ":" + fingerprint[i]);
            auxiliar = "";
        }
        
        */
        
        //Pruebas
        //Usuario 0
        System.out.println("-------------------------------------------------");
        System.out.println("Prueba de k : " + codigo.getK());
        culpables = codigo.accusation(numberOfUsers, huellas, huellas[4], codigo.getP(), collusionSize, codigo.getK());
        
        System.out.println("Usuarios culpables");
        for (int i = 0; i < culpables.size(); i++) {
            System.out.println("User " + culpables.get(i) + "");
        }
        culpables.clear();
        
        
        //---------------------------------------------------------------------
        //Ataque de collusion
        boolean[] pirateCopy;
        pirateCopy = codigo.ataques(huellas);
        System.out.println("-------------------------------------------------");
        System.out.println("Huella pirata por ataque de collusion");
        for (int i = 0; i < pirateCopy.length; i++) {
            System.out.print( pirateCopy[i] + "\t");
        }
        System.out.println();
        
        culpables = codigo.accusation(numberOfUsers, huellas, pirateCopy, codigo.getP(), collusionSize, codigo.getK());
        
        System.out.println("Usuarios culpables");
        for (int i = 0; i < culpables.size(); i++) {
            System.out.println("User " + culpables.get(i) + "");
        }
        
        
        
        //////////////////////////////////////
        
        CriptographicFunctions funciones = new CriptographicFunctions();
        ArrayList aleatorios = new ArrayList();
        aleatorios = funciones.lfsrPrueba(54,2050);
        
        for (int i = 0; i < 2050; i++) {
            System.out.println(i+1 + ": " + aleatorios.get(i));
        }
        
    }
}
