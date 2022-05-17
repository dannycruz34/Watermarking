/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mohanpurkar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DanniC
 */
public class Tardos {
    
    public Tardos(){}
    
    public static String codigoTardos(int user) throws IOException{
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList codigos = new ArrayList();
        try {
            archivo = new File ("/Users/DanniC/Documents/INAOE_2Cuatri/Tesis/Experiementos/codigos.txt");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);
           
            // Lectura del fichero
            int length = Integer.parseInt( br.readLine() );
            
            String linea;
            int i=0;
            while((linea = br.readLine())!=null){
                if(i > length){
                   codigos.add(linea);
                    System.out.println(linea);
                }
                i++;
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
       
         return (String)codigos.get(user);
    }
}
