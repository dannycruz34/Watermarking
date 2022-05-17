/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tardos;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author DanniC
 */
public class BoSh {
    public static void main(String[] args) {
     
        int duplication;
        int lengthOfCodes;
        boolean[][] codeMatrix;
        double epsilon = 0.01;
        int numberOfUsers = 10;
        int n = 10;
        String[] huella;
        
        duplication =(int)(2 * Math.pow(numberOfUsers, 2.0) * Math.log(2 * n / epsilon));
        lengthOfCodes = (numberOfUsers - 1) * duplication;
        codeMatrix = new boolean[numberOfUsers][lengthOfCodes];
        int startPnt = 0;
        
        
        
        for (int i = 0; i < numberOfUsers; i++) {
            for (int j = startPnt; j < lengthOfCodes; j++) {
                codeMatrix[i][j] = true;
            }
            startPnt += duplication;
        }
        
        int[] pidx;
        pidx = new int[lengthOfCodes];
        for (int i = 0; i < lengthOfCodes; i++) {
            pidx[i] = i;
        }
        
        permutar(pidx);
        
        boolean[] initCode = null;
        for (int i = 0; i < numberOfUsers; i++) {
            initCode = codeMatrix[i].clone();
            for (int j = 0; j < lengthOfCodes; j++) {
                codeMatrix[i][j] = initCode[pidx[j]];
            }
        }
        
        System.out.println("MATRIZ DE CODIGO BONEH Y SHAW");
        //Arreglo para guardas las huellas generadas
        huella = new String[numberOfUsers];
        String auxiliar="";
        for (int i = 0; i < numberOfUsers; i++) {
            for (int j = 0; j < lengthOfCodes; j++) {
                System.out.print( codeMatrix[i][j] + "\t");
                if(codeMatrix[i][j])               
                    auxiliar += 1;
                else
                    auxiliar += 0;
            }
            System.out.println();
            huella[i] = auxiliar;
            auxiliar="";
        }
        
        System.out.println("Prueba de huella 1,0");
        for (int i = 0; i < numberOfUsers; i++) {
            System.out.println(huella[i]);
        }
        
        System.out.println("Longitud del código: " + lengthOfCodes);
        System.out.println("Número de usuarios: " + numberOfUsers);
        
        
        
        //PRUEBA CON USUARIOS
        acusacion(duplication, lengthOfCodes, numberOfUsers, epsilon, codeMatrix[8]);
        
        
    }
    
    private static void permutar(int[] arr){
        int temp,randmax = Integer.MAX_VALUE;
        int arrlength = arr.length;
        Random r = new Random();
        for (int i = 0,j = 0; i < arrlength; i++) {
            j = r.nextInt(randmax) * arrlength / (randmax + 1);
            temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
    
    public static void acusacion(int duplication, int lengthOfCodes,int numberOfUsers,double epsilon, boolean[] copiaPirata){
        boolean[] b = new boolean[duplication];
        boolean[] ippCopy =  copiaPirata.clone();
        ArrayList id =  new ArrayList();
        
        //PRIMERA REGLA
        for(int i = 0; i < duplication; i++) {
            b[i] = ippCopy[i];
        }
        
        if(getWeight(b) > 0){
            id.add(0);
        }
        
        //SEGUNDA REGLA
        for (int i = lengthOfCodes-duplication, j = 0; i < lengthOfCodes; i++,j++) {
            b[j] = ippCopy[i];
        }
        
        if(getWeight(b) < duplication){
            id.add( numberOfUsers - 1);
        }
       
        //TERCERA REGLA
        boolean[] rs = new boolean[duplication * 2];
        for (int i = 1; i < numberOfUsers - 1; i++) {
            for (int j = 0; j < duplication; j++) {
                b[j] = rs[j] = ippCopy[(i-1) * duplication + j];
                rs[j + duplication] = ippCopy[i * duplication + j]; 
            }
            int k =getWeight(rs);
            
            if(getWeight(b) < (k / 2 - Math.sqrt(k / 2 * Math.log(2 * numberOfUsers / epsilon)))){
                id.add(i);
                break;
            }
        }
        
        //CULPABLES
        System.out.println("---- USUARIOS CULPABLES ----");
        for (int i = 0; i < id.size(); i++) {
            System.out.println("User " + id.get(i) + "---");
        }
    }
    
    
    public static int getWeight(boolean[] rs){
        int suma=0;
        for (int i = 0; i < rs.length; i++) {
            if( rs[i] == true)
                suma += 1;
        }
        return suma;
    }
   
}
