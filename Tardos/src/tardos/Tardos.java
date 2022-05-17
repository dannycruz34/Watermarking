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
public class Tardos {

    /**
     * @param args the command line arguments
     */
    //epsilon 0 < ep < 1
    double epsilon;
    //Tamaño de la collusion
    int collusionSize;
    //Numero de usuarios
    int numberOfUsers;
    //Vector de probabilidades
    double[] p;
    double k;

    public double[] getP() {
        return p;
    }

    public double getK(){
        return k;
    }
        
    public Tardos(int numberOfUsers, int collusionSize, double epsilon){
        this.numberOfUsers = numberOfUsers;
        this.collusionSize = collusionSize;
        this.epsilon = epsilon;
        
    }
    
    
    public boolean[][] generateMatrix(int numberOfUsers, int collusionSize, double epsilon) {
        // TODO code application logic here
        //Longitud del codigo
        int lengthOfCodes;
        //Matriz de codigo
        boolean[][] codeMatrix;
        
        System.out.println("NumberOfUsers: " + numberOfUsers);
        System.out.println("CollusionSize: " + collusionSize);
        System.out.println("Epsilon: " + epsilon);
        
        //Logaritmo natural
        k = Math.log(1.0/epsilon);
        System.out.println("Valor k: " + k);
        lengthOfCodes = (int)(100.0 * Math.pow(collusionSize, 2.0) * k);
        codeMatrix = new boolean[numberOfUsers][lengthOfCodes];
        
        double t = 1/ (300 * collusionSize);
        double tp = Math.asin(Math.sqrt(t));
        double rmin = tp;
        double rmax = (Math.PI / 2.0 - tp);
        p = new double[lengthOfCodes];
        
        double r = 0.0;
        for (int i = 0; i < lengthOfCodes; i++) {
            r = rmin + Math.random() * (rmax - rmin);
            p[i] = Math.pow(Math.sin(r), 2.0);            
        }
        
        
        System.out.println("MATRIZ DE CÓDIGO TARDOS");  
                
        for (int i = 0; i < numberOfUsers; i++) {
            codeMatrix[i] = getFingerprintCodes(p);
            for (int j = 0; j < lengthOfCodes; j++) {
                System.out.print( codeMatrix[i][j] + "\t");               
            }
            System.out.println();
        }
        
        System.out.println("Longitud de código: " + lengthOfCodes);
        System.out.println("Número de usuarios: " + numberOfUsers);
        System.out.println("Tamaño de colusión: " + collusionSize);
        
        return codeMatrix;
    }
    
    
    public static boolean[] getFingerprintCodes(double[] p){
        boolean[] fp = new boolean[p.length];
        for (int i = 0; i < p.length; i++) {
            fp[i] = Math.random() < p[i];
        }
        return fp;
    }
    
    
    public static boolean[][] getFingerprintMatrix(int numberOfUsers,int lengthOfCodes,boolean[][] codeMatrix,double[] p){
        boolean[][] tempCodeMatrix = codeMatrix;
        //Numero extra de usuarios para agregar al esquema
        int numberOfExtraUsers=4;
        codeMatrix = new boolean[numberOfUsers + numberOfExtraUsers][lengthOfCodes];
        for (int i = 0; i < numberOfUsers; i++) {
            codeMatrix[i] = tempCodeMatrix[i];
        }
        
        for (int i = numberOfUsers; i < numberOfExtraUsers + numberOfUsers; i++) {
                codeMatrix[i] = getFingerprintCodes(p);
        }
        
        numberOfUsers += numberOfExtraUsers;
        return codeMatrix;
    }
    
   
    public ArrayList accusation(int numberOfUsers,boolean[][] codeMatrix,
                                boolean[] piratedCopy,double[] p,int collusionSize,double k){
        double sum = 0.0;
        int cntr = 0;
        double Z = 20 * collusionSize * k;
        System.out.println("Valor Z : " + Z);
        int lengthOfCodes = codeMatrix[0].length;        
        ArrayList id = new ArrayList();
        
        for (int i = 0; i < numberOfUsers; i++) {
            for (int j = 0; j < lengthOfCodes; j++) {
                if(codeMatrix[i][j] && piratedCopy[j]){
                    sum += Math.sqrt((1 - p[j])/p[j]);                    
                }else if(!codeMatrix[i][j] && piratedCopy[j]){
                    sum += - Math.sqrt(p[j] / (1-p[j]));
                }
            }
            
        if(sum > Z){
            id.add(i);
            System.out.println("User " + i + "\t" + "Suma = " + sum);            
        }
        
        sum = 0.0;
        }   
        return id;
    }
    
    
    
    public boolean[] ataques( boolean[][] codeMatrix){
        boolean codesAgree;
        boolean[] pirateCopy =  new boolean[codeMatrix[0].length];
        int lengthOfCodes = codeMatrix[0].length;
        int[] pirateId = { 5 , 6};
        int collusionSize = pirateId.length;
        //Estrategia a seguir
        int strategy =3;
        //Random
        Random rand = new Random();
        
        for (int i = 0; i < lengthOfCodes ; i++) {
            codesAgree = true;
            for (int j = 0; j < collusionSize-1; j++) {
                if (codeMatrix[pirateId[j]][i] != codeMatrix[pirateId[j+1]][i]) {
                    codesAgree = false;
                    break;
                }
            }

            if(codesAgree){
                pirateCopy[i] = codeMatrix[pirateId[0]][i];
            }else{
                switch(strategy){
                    case 0: 
                            pirateCopy[i] = rand.nextBoolean();
                            break;
                    case 1:
                            double mk = 1.0 / collusionSize;
                            int zone = (int) (rand.nextDouble() / mk);
                            pirateCopy[i] = codeMatrix[pirateId[zone]][i];
                            break;
                    case 2:
                        //Minoria
                            boolean xor = codeMatrix[pirateId[0]][i];
                            for (int j = 1; j < collusionSize; j++) {
                                xor = xor ^ codeMatrix[pirateId[j]][i];                            
                            }
                            pirateCopy[i] = xor;
                            break;
                    case 3:
                            //Mayoria
                            xor = codeMatrix[pirateId[0]][i];
                            for (int j = 1; j < collusionSize; j++) {
                                xor = xor ^ codeMatrix[pirateId[j]][i];                            
                            }
                            pirateCopy[i] = !xor;
                            break;
                }
            }
        }
        return pirateCopy;
    }
    
    

    
}
