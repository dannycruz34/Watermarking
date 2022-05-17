/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
ESQUEMA DE HUELLA DIGITAL
ARTI MOHANPURKAR 2015
A FINGERPRINT TECHNIQUE FOR NUMERIC RELATIONAL DATABASES WITH DISTORTION MINIMIZATION
 */
package mohanpurkar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import static mohanpurkar.Stages.db;

/**
 *
 * @author DanniC
 */
public class Mohanpurkar {

    /**
     * @param args the command line arguments
     */
    public static Conexion db;

    public static void main(String[] args) throws SQLException, IOException {
        // TODO code application logic here
        db = new Conexion();
        db.MySQLConnect();
        Stages s = new Stages(db);
        
        //Esquema
        //Insercion --------> 1
        //Deteccion --------> 2       
        int seleccion = 1;
        
        //INSERCION
        if( seleccion == 1){
            
            String huella;
            String id_comprador = "1";
            ///SELECCION DE LA HUELLA A INSERTAR       
            huella = codigoTardos(Integer.parseInt(id_comprador));

            //Generar copia de la tabla
            String nuevaCopia = "CAP_MO_100000_4";
            generarCopiaDB(nuevaCopia ,"f100000");

            ArrayList<Particion> a,b;
            ArrayList delta;
            String secret_key = "INAOE";
            int numAttr = 10;
            System.out.println("Huella tam: " + huella.length());
            int numParticiones = huella.length();
            //Factor de confianza para calcular el umbral
            double f = 1;
            //Parametro de alteracion en la insercion
            int c = 1;

            //MEDIR TIEMPO DE EJECUCION DE LA INSERCION
            long TInicio, TFin, tiempo;           //Para determinar el tiempo
            TInicio = System.currentTimeMillis(); //de ejecución

            //Particionamiento de la DB y Seleccion
            a = s.getPatitions(secret_key,nuevaCopia,numParticiones,f);
            System.out.println("PRE SELECCION");
            System.out.println("TAMAÑO PRESELECCION:\t" + a.size());
           /* for (int i = 0; i < a.size(); i++) {
                System.out.println("ID: " + a.get(i).getId_key() + " P:" + a.get(i).getParticion() );
            }*/
            
            //Seleccion final
            b = s.finalSelection(a, secret_key);
            System.out.println("SELECCION FINAL TAM:\t" + b.size());
            //exportarRegistrosFinales(b);
           // guardarRegistrosFinales(b);

           /* for (int i = 0; i < b.size(); i++) {
                System.out.println("ID: " + b.get(i).getId_key() + " P:" + b.get(i).getParticion() );
            }*/

            /*
            //Fingerprint
            int usuarios[] = {2,4,5};
            for (int i = 0; i < usuarios.length; i++) {
                String huella;
                String id_comprador = Integer.toString(usuarios[i]);
                //Generar copia de la tabla
                String nuevaCopia = "PRUEBA21";
                generarCopiaDB(nuevaCopia ,"f581012");
                ///SELECCION DE LA HUELLA A INSERTAR       
                huella = codigoTardos(Integer.parseInt(id_comprador));
                delta = s.fingerprint(b, secret_key, huella, nuevaCopia,numAttr,id_comprador,c);
                //GUARDAR DELTA EN ARCHIVO DE TEXTO
                guardarDeltas(id_comprador,delta);
            }*/
            
            //Fingerprint
            delta = s.fingerprint(b, secret_key, huella, nuevaCopia,numAttr,id_comprador,c);
            //GUARDAR DELTA EN ARCHIVO DE TEXTO
            guardarDeltas(id_comprador,delta);
            
            
            //Fin del tiempo
            TFin = System.currentTimeMillis();
            tiempo = TFin - TInicio;
            System.out.println("TIEMPO TOTAL DE INSERCION (DB): " + tiempo);
            
            
           
        }
        // DETECCION DE LAS HUELLAS
        else if( seleccion == 2){

            //Generar copia de la tabla
            String copiaTabla = "PRUEBA21";

            ArrayList<Particion> a,b;
            ArrayList delta;
            String secret_key = "INAOE";
            int numAttr = 10;
            int numParticiones = 1842;
            //Factor de confianza para calcular el umbral
            double f = 2;
            //Parametro de alteracion en la insercion
            int c = 1;
            
            //MEDIR TIEMPO DE EJECUCION DE LA INSERCION
            long TInicio, TFin, tiempo;           //Para determinar el tiempo
            TInicio = System.currentTimeMillis(); //de ejecución

            //Particionamiento de la DB y Seleccion
            a = s.getPatitions(secret_key,copiaTabla,numParticiones,f);
            System.out.println("PRE SELECCION");
            System.out.println("TAMAÑO PRESELECCION: " + a.size());
           /* for (int i = 0; i < a.size(); i++) {
                System.out.println("ID: " + a.get(i).getId_key() + " P:" + a.get(i).getParticion() );
            }*/
            //Seleccion final
            b = s.finalSelection(a, secret_key);
            System.out.println("SELECCION FINAL TAM: " + b.size());
           /* for (int i = 0; i < b.size(); i++) {
                System.out.println("ID: " + b.get(i).getId_key() + " P:" + b.get(i).getParticion() );
            }*/
           
            //EXTRAER DELTAS ALMACENADAS EN ARCHIVO
            ArrayList<Deltas> deltas = extraerDeltas();
           
            //RECUPERACION DE HUELLAS CON DELTAS Fingerprint
            System.out.println("Deltas tam: " + deltas.size());
            String[] huellasRecuperadas = new String[deltas.size()];
            for (int i = 0; i < deltas.size(); i++) {
                ArrayList deltaR = deltas.get(i).getDelta();
                String idComprador = deltas.get(i).getId_usuario();
                System.out.println("RECUPERACION ID: " + idComprador);
                huellasRecuperadas[i] = s.deteccion(b, secret_key, deltaR, copiaTabla, idComprador, c);
                System.out.println("Huella Recuperada con ID( " + idComprador + " ): " + huellasRecuperadas[i]);
            }
            
            
            //TRAITOR TRACING
            String huella;
            int numUsers = 10;
            for (int i = 0; i < numUsers; i++) {
                huella = codigoTardos(i);
                for (int j = 0; j < huellasRecuperadas.length; j++) {
                    if (huella.equals(huellasRecuperadas[j])) {
                        System.out.println("USUARIO CULPABLE ID : " + i);
                    }
                }

            }

            
            //Fin del tiempo
            TFin = System.currentTimeMillis();
            tiempo = TFin - TInicio;
            System.out.println("TIEMPO TOTA DE INSERCION (DB): " + tiempo);
            
        
        }

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
                   // System.out.println(linea);
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

    
    public static void guardarDeltas(String id,ArrayList delta) throws IOException{
        FileWriter fichero = null;
        File file = new File("/Users/DanniC/Documents/INAOE_2Cuatri/Tesis/Experiementos/deltas.txt");
        // Si el archivo no existe, se crea!
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            fichero = new FileWriter(file.getAbsoluteFile(),true);
            fichero.write( id + "\n");
            int p = delta.size();
            for (int i = 0; i < p; i++) {
                fichero.write(delta.get(i) + ":");
            }           
              
            fichero.write("\n");
            
            fichero.close();
        } catch (IOException ex) {
        }
    }
    
    public static ArrayList<Deltas> extraerDeltas() throws IOException{
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<Deltas> deltas = new ArrayList<Deltas>();
        try {
            archivo = new File ("/Users/DanniC/Documents/INAOE_2Cuatri/Tesis/Experiementos/deltas.txt");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);
           
            // Lectura del fichero
            Deltas delta;
            String linea;
            int aux = 1;
            while((linea = br.readLine())!=null){
                
                delta = new Deltas();
                int id_user = Integer.parseInt( linea );
                //System.out.println("id: " + id_user); 
                delta.setId_usuario(Integer.toString(id_user));
                
                linea = br.readLine();
                String s[] = linea.split(":");
                for (int j = 0; j < s.length; j++) {
                  //  System.out.println("s[" + j + "]: "  + s[j]);
                    delta.delta.add(s[j]);
                }
                deltas.add(delta);
                              

            }
            
        } catch (FileNotFoundException ex) {
        }
       return deltas;
    }
    
    public static void exportarRegistrosFinales(ArrayList<Particion> reducidaFinal) throws SQLException{
        Conexion db = new Conexion();
        Conexion db2 = new Conexion();
        db.MySQLConnect();   
        db2.MySQLConnect();    
        String nombreCopia = "mohanpurkar_5000_4";
        String nombreTabla = "f5000";
        String query = "CREATE TABLE "+nombreCopia+" LIKE " + nombreTabla;
        db.comando = db.conexion.createStatement();
        db.comando.execute(query);
        db.comando.close();
        
        
        //Consulta de la tabla para la insercion
        String Query = "SELECT * FROM " + nombreTabla;
        db.comando = db.conexion.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
              java.sql.ResultSet.CONCUR_READ_ONLY);
        db.comando.setFetchSize(Integer.MIN_VALUE);                
        db.registro = db.comando.executeQuery(Query);        
        
        
        boolean aux;
        int indiceP = 0;
        while (db.registro.next()) {
            /*System.out.println("key: " + db.registro.getString(1) + 
                            "\ta1: " + db.registro.getString(2) +
                            "\ta2: " + db.registro.getString(3) +
                            "\ta3: " + db.registro.getString(4) +
                            "\ta4: " + db.registro.getString(5) +
                            "\ta5: " + db.registro.getString(6) +
                            "\ta6: " + db.registro.getString(7) +
                            "\ta7: " + db.registro.getString(8) +
                            "\ta8: " + db.registro.getString(9) +
                            "\ta9: " + db.registro.getString(10)+
                            "\ta10: " + db.registro.getString(11));*/
            
            
           //aux = reducidaFinal.indexOf(db.registro.getString(1));
           if(indiceP < reducidaFinal.size()){
                aux = db.registro.getString(1).equals(reducidaFinal.get(indiceP).getId_key());
           }else{ 
               aux = false;
           }
           if( aux == true ){
               String insert = "INSERT INTO " + nombreCopia + "(id_key,a1,a2,a3,a4,a5,a6,a7,a8,a9,a10) VALUES(" + db.registro.getString(1) + "," + 
                                                                           db.registro.getString(2) + "," + 
                                                                           db.registro.getString(3) + "," +
                                                                           db.registro.getString(4) + "," +
                                                                           db.registro.getString(5) + "," +
                                                                           db.registro.getString(6) + "," +
                                                                           db.registro.getString(7) + "," +
                                                                           db.registro.getString(8) + "," +
                                                                           db.registro.getString(9) + "," +
                                                                           db.registro.getString(10) + "," +
                                                                           db.registro.getString(11) + ")";
               db2.comando = db2.conexion.createStatement();
               db2.comando.execute(insert);
               indiceP++;
           }
           
        }
        db.registro.close();
        
    }
    
    public static void guardarRegistrosFinales(ArrayList<Particion> reducidaFinal) throws IOException{
        FileWriter fichero = null;
        File file = new File("/Users/DanniC/Documents/INAOE_2Cuatri/Tesis/Experiementos/reducidaMohanpurkar.txt");
        // Si el archivo no existe, se crea!
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            fichero = new FileWriter(file.getAbsoluteFile(),true);
            int p = reducidaFinal.size();
            for (int i = 0; i < p; i++) {
                fichero.write(reducidaFinal.get(i).getId_key() + "\n");
            }           
              
            
            fichero.close();
        } catch (IOException ex) {
        }
    }
}
