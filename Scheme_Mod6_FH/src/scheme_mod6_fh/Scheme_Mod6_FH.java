package scheme_mod6_fh;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author DanniC
 */
public class Scheme_Mod6_FH {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, IOException {
        // TODO code application logic here
            Conexion db = new Conexion();
            db.MySQLConnect();    
            Fingerprint finger = new Fingerprint(db);
            
            String huella;
            ///SELECCION DE LA HUELLA A INSERTAR
            huella = codigoTardos(3);
            
            //Generar copia de la tabla
            String nuevaCopia = "H_FH7_100";
            generarCopiaDB(nuevaCopia ,"f581012");
            
            //Declaracion parametros
            Parametros p = new Parametros();
            p.setHuella( huella );
            p.setKey_secret("INAOE");
            p.setNombreTabla( nuevaCopia );
            p.setFtupla("100"); //Fraccion de tupla
            p.setNumAttr("10");  //Numero de atributos
            p.setFh("7"); //Numero de bits de huella
            // TODO add your handling code here:
            
            //MEDIR TIEMPO DE EJECUCION DE LA INSERCION
            long TInicio, TFin, tiempo;           //Para determinar el tiempo
            TInicio = System.currentTimeMillis(); //de ejecución
            
            int payload = finger.insercionHuella(p);
            
            TFin = System.currentTimeMillis();
            tiempo = TFin - TInicio;
            System.out.println("Tiempo de ejecución en milisegundos (Inserción): " + tiempo);
            ////////////
    }
    
    
    
    
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
        }
       
         return (String)codigos.get(user);
    }
    
    
    public static void generarCopiaDB(String nombreCopia,String nombreTabla){
        Conexion db = new Conexion();
        db.MySQLConnect();    
        String query = "CREATE TABLE "+nombreCopia+" LIKE " + nombreTabla;
        String query2 = "INSERT INTO "+nombreCopia+" SELECT * FROM " + nombreTabla;
        try {
            db.comando = db.conexion.createStatement();
            db.comando.execute(query);
            db.comando.execute(query2);
            db.comando.close();
        } catch (SQLException ex) {
        }
    } 
    
    
    public static void MSE(String tablaOriginal, String tablaMod) throws SQLException{
        String Query1 = "SELECT * FROM " + tablaOriginal;
        String Query2 = "SELECT * FROM " + tablaMod;
        Conexion db = new Conexion();
        db.MySQLConnect();   
        int numAttr = 10;
        
        //Num registros
        String count = "SELECT COUNT(*) FROM " + tablaOriginal;
        db.comando = db.conexion.createStatement();
        db.registro = db.comando.executeQuery(count);
        db.registro.next();
        int tuplas = db.registro.getInt(1);
        
        double[][] tOriginal = new double[tuplas][numAttr];
        double[][] tMod = new double[tuplas][numAttr];
        
        db.comando = db.conexion.createStatement();
        db.registro = db.comando.executeQuery(Query1);
        
        int i=0;
        while (db.registro.next()) {            
            for (int j = 0; j < numAttr; j++) {
                tOriginal[i][j] = Integer.parseInt(db.registro.getString(j+2));
            }
            i++;
        }
        
        db.comando = db.conexion.createStatement();
        db.registro = db.comando.executeQuery(Query2);        
        i=0;
        while (db.registro.next()) {            
            for (int j = 0; j < numAttr; j++) {
                tMod[i][j] = Integer.parseInt(db.registro.getString(j+2));
            }
            i++;
        }
    } 

    
}
