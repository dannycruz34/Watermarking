/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tardos;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author DanniC
 */
public class CriptographicFunctions {
    
    public CriptographicFunctions(){
    }
    
    public int generadorPseudo(Random number){
        //for (int i = 0; i < 20; i++) {
          // Generate another random integer in the range [0, 20]
        int n = number.nextInt(64);
        System.out.println("Numero generado: " + n);
        return n;
        
    }

    public ArrayList lfsr(int numero,int periodo){
        String binario =  Integer.toBinaryString(numero) + "00000000000000000000";
        int y[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,1}; // 6,5	
        
        System.out.println("LFSR: " + binario);
        
	ArrayList aleatorios = new ArrayList();
        y[0] = Integer.parseInt(binario.substring(0,1));
        y[1] = Integer.parseInt(binario.substring(1,2));
        y[2] = Integer.parseInt(binario.substring(2,3));
        y[3] = Integer.parseInt(binario.substring(3,4));
        y[4] = Integer.parseInt(binario.substring(4,5));
        y[5] = Integer.parseInt(binario.substring(5,6));
        y[6] = Integer.parseInt(binario.substring(6,7));
        y[7] = Integer.parseInt(binario.substring(7,8));
        y[8] = Integer.parseInt(binario.substring(8,9));
        y[9] = Integer.parseInt(binario.substring(9,10));
        y[10] = Integer.parseInt(binario.substring(10,11));
        y[11] = Integer.parseInt(binario.substring(11,12));
        y[12] = Integer.parseInt(binario.substring(12,13));
        y[13] = Integer.parseInt(binario.substring(13,14));
        y[14] = Integer.parseInt(binario.substring(14,15));
        y[15] = Integer.parseInt(binario.substring(15,16));
        y[16] = Integer.parseInt(binario.substring(16,17));
        y[17] = Integer.parseInt(binario.substring(17,18));
        y[18] = Integer.parseInt(binario.substring(18,19));

		int num;
		String valor="";
		int sy;
		int l=0;
		for(int j=0;j< periodo;j++){
				
				sy = y[18];
				y[18] = y[17];
				y[17] = y[16];
				y[16] = y[15];
				y[15] = y[14];
				y[14] = y[13];
				y[13] = y[12];
				y[12] = y[11];
				y[11] = y[10];
				y[10] = y[9];
				y[9] = y[8];
				y[8] = y[7];
				y[7] = y[6];
				y[6] = y[5];
				y[5] = y[4];
				y[4] = y[3];
				y[3] = y[2];
				y[2] = y[1];
				y[1] = y[0];
				y[0] = y[18]^sy^y[17]^y[15];
				
				for(int k=0;k < 19 ; k++){
					valor = valor + y[k];
					System.out.print(y[k]);
				}
	            num =Integer.parseInt(valor,2);
	            aleatorios.add(num) ;
	            l++;
                    
                    valor="";
                    System.out.println(" " + num);
			
		}
		return aleatorios;
	}

    public String cryptMD5(String textoPlano)
    {
        try
        {
           char[] HEXADECIMALES = { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };

           MessageDigest msgdgt = MessageDigest.getInstance("MD5");
           byte[] bytes = msgdgt.digest(textoPlano.getBytes());
           StringBuilder strCryptMD5 = new StringBuilder(2 * bytes.length);
           for (int i = 0; i < bytes.length; i++)
           {
               int low = (int)(bytes[i] & 0x0f);
               int high = (int)((bytes[i] & 0xf0) >> 4);
               strCryptMD5.append(HEXADECIMALES[high]);
               strCryptMD5.append(HEXADECIMALES[low]);
           }
           return strCryptMD5.toString();
        } catch (NoSuchAlgorithmException e) {
           return null;
        }
    }
    
    public ArrayList aleatoriosAtributos(int geneS,int numAttr,int attribute_num){
        int indiceAttr;
        //Inicializar Random para la seleccion de atributos
        Random sr = new Random(geneS);
        //Inicializar vector aleatorio para seleccion de atributos
        ArrayList aleatoriosAtributos = new ArrayList();
        ArrayList aleatoriosAtributosF =  new ArrayList();
        for( int i=1; i <= numAttr;i++){
            aleatoriosAtributos.add(i);
        }
        
        while(aleatoriosAtributosF.size()  < attribute_num){
            indiceAttr=sr.nextInt(numAttr);
            if(!aleatoriosAtributosF.contains(aleatoriosAtributos.get(indiceAttr))){
                aleatoriosAtributosF.add(aleatoriosAtributos.get(indiceAttr));
            }
        }
        
        return aleatoriosAtributosF;
    }
    
    public ArrayList lfsrPrueba(int numero,int periodo){
        String binario =  Integer.toBinaryString(numero) + "00000000000000000";
        int y[] = {1,0,1,0,0,0,0,0,0,0,0}; // 11,9,0	
        
        System.out.println("LFSR: " + binario);
        
	ArrayList aleatorios = new ArrayList();
        y[0] = Integer.parseInt(binario.substring(0,1));
        y[1] = Integer.parseInt(binario.substring(1,2));
        y[2] = Integer.parseInt(binario.substring(2,3));
        y[3] = Integer.parseInt(binario.substring(3,4));
        y[4] = Integer.parseInt(binario.substring(4,5));
        y[5] = Integer.parseInt(binario.substring(5,6));        
        y[6] = Integer.parseInt(binario.substring(6,7));
        y[7] = Integer.parseInt(binario.substring(7,8));
        y[8] = Integer.parseInt(binario.substring(8,9));
        y[9] = Integer.parseInt(binario.substring(9,10));
        y[10] = Integer.parseInt(binario.substring(10,11));        


		int num;
		String valor="";
		int sy;
		int l=0;
		for(int j=0;j< periodo;j++){
				
                                
				sy = y[10];
                                y[10] = y[9];
				y[9] = y[8];
				y[8] = y[7];
				y[7] = y[6];
				y[6] = y[5];
				y[5] = y[4];
				y[4] = y[3];
				y[3] = y[2];
				y[2] = y[1];
				y[1] = y[0];
				y[0] = sy^y[9];
				
				for(int k=0;k < 11 ; k++){
					valor = valor + y[k];
					System.out.print(y[k]);
				}
	            num =Integer.parseInt(valor,2);
	            aleatorios.add(num) ;
	            l++;
                    
                    valor="";
                    System.out.println(" " + num);
			
		}
		return aleatorios;
	}
}
