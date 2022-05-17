/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheme_mod6_fh_rp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import static scheme_mod6_fh_rp.DeteccionFinger.db;

/**
 *
 * @author DanniC
 */
public class Scheme_Mod6_FH_RP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException {
        // TODO code application logic here
        Conexion db = new Conexion();
            db.MySQLConnect();  
        
            
        //Esquema
        //Insercion --------> 1
        //Deteccion --------> 2       
        int seleccion = 2;
        
        //INSERCION
        if( seleccion == 1){
            Fingerprint finger = new Fingerprint(db);
            
            ///SELECCION DE LA HUELLA A INSERTAR
            String huella;
            int user = 1;
            huella = codigoTardos(user);
            
            //Generar copia de la tabla
            String nuevaCopia = "HUE1_581012_F70_NB3_prueba";
            generarCopiaDB(nuevaCopia ,"f581012");
            
            //CALCULAR MEDIA Y DESV DE CADA ATRIBUTO
            estadisticas2( nuevaCopia, 10);
        
            //Declaracion parametros
            Parametros p = new Parametros();
            p.setHuella( huella );
            p.setKey_secret("INAOES");
            p.setNombreTabla( nuevaCopia );
            p.setNumAttr("10");  //Numero de atributos
            p.setFtupla("70"); //Fraccion de tupla
            p.setFh("3"); //Numero de bits de huella
            // TODO add your handling code here:
            
            //MEDIR TIEMPO DE EJECUCION DE LA INSERCION
            long TInicio, TFin, tiempo;           //Para determinar el tiempo
            TInicio = System.currentTimeMillis(); //de ejecución
            
            finger.insercionHuella(p);
            
            /*//INSERCION MULTIPLES HUELLAS
            for (int i = 1; i < 6; i++) {
                huella = codigoTardos(i);
                p.setHuella(huella);
                finger.insercionHuella(p);
            }*/
            
            TFin = System.currentTimeMillis();
            tiempo = TFin - TInicio;
            System.out.println("Tiempo de ejecución en milisegundos (Inserción): " + tiempo);
        }
    /*-------------------------------------------------------------------------------------------------*/
        //DETECCION
        else if(seleccion == 2){
            
            DeteccionFinger deteccionHuella = new DeteccionFinger(db);
            //Generar copia de la tabla
            //String nombreTabla = "HUE1_581012_F20_NB2_eliminar9";
            TardosCode codigo = new TardosCode();
            //Recuperar codigos de Tardos
            String url = "/Users/DanniC/Documents/INAOE_2Cuatri/Tesis/Experiementos/codigos.txt";
            codigo = codigoTardos(url);
            
            
            //Declaracion parametros
            ParametrosDeteccion p = new ParametrosDeteccion();
            p.setCodigo(codigo);
            p.setLongitud("1842");
            //p.setNombreTabla( nombreTabla );
            p.setKey_secret("INAOE");
            p.setNumAttr("10");  //Numero de atributos
            p.setFtupla("30"); //Fraccion de tupla
            p.setFh("1"); //Numero de bits de huella
            // TODO add your handling code here:
            
            //MEDIR TIEMPO DE EJECUCION DE DETECCION
            long TInicio, TFin, tiempo;           //Para determinar el tiempo
            TInicio = System.currentTimeMillis(); //de ejecución
            
            //Todos
            for (int i =1; i < 10; i++) {
                //String nombreTabla = "HUE1_581012_F1_NB1";
                 String nombreTabla = "HUE1_581012_F30_NB1_actualizar" + i;
                 p.setNombreTabla(nombreTabla);
                 deteccionHuella.deteccion(p);
                 System.out.println("-----------------");

            }
           
            //deteccionHuella.deteccion(p);
            
            TFin = System.currentTimeMillis();
            tiempo = TFin - TInicio;
            //System.out.println("Tiempo de ejecución en milisegundos (Inserción): " + tiempo);
        }
        
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
    
    public static TardosCode codigoTardos(String url) throws IOException{
        TardosCode code = new TardosCode();
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList codigos = new ArrayList();
        
        try {
            archivo = new File (url);
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);
           
            // Lectura del fichero
            int length = Integer.parseInt( br.readLine() );
            double[] p = new double[length];
            double k = 0;
            
            String linea;
            int i=0,j=0;
            while((linea=br.readLine())!=null){
                if(i > length){
                    codigos.add(linea);
                    System.out.println(linea);
                }else if(i == length){
                    k = Double.parseDouble(linea);
                }else{
                    p[j] = Double.parseDouble(linea);
                    j++;
                }
                i++;
            }
            
            //Vaciar ArrayList a arreglo boolean
            boolean[][] huellas = new boolean[codigos.size()][length];
            for (int l = 0; l < codigos.size(); l++) {
                String aux = (String) codigos.get(l);
                for (int m = 0; m < length; m++) {
                    String x = aux.substring(m, m+1);
                    if(x.equals("0"))
                        huellas[l][m] = false;
                    else
                        huellas[l][m] = true;
                }
            }
    
            code.setK(k);
            code.setP(p);
            code.setHuella(huellas);
            
            
        } catch (FileNotFoundException ex) {
        }
       
         return code;
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
            db.conexion.close();
        } catch (SQLException ex) {
        }
    } 
    
    public static double[][] estadisticas2(String nombreTabla,int numAttr) throws SQLException{
        Conexion db = new Conexion();
        db.MySQLConnect();    
        String Query = "SELECT * FROM " + nombreTabla;
        String count = "SELECT COUNT(*) FROM " + nombreTabla;
        db.comando = db.conexion.createStatement();
        db.registro = db.comando.executeQuery(count);
        db.registro.next();
        int tuplas = db.registro.getInt(1);
        db.registro.close();
        db.comando.close();
        
        double sumas[] = new double[numAttr];
        double media1[] = new double[numAttr];
        double val=0.0;
        double estadisticaMD[][] = new double[numAttr][3];
        
        
        db.comando = db.conexion.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
              java.sql.ResultSet.CONCUR_READ_ONLY);
        db.comando.setFetchSize(Integer.MIN_VALUE);
        db.registro = db.comando.executeQuery(Query);
            
            while (db.registro.next()) {
                
                for (int i = 0; i < numAttr; i++) {
                    media1[i] = media1[i] + Integer.parseInt(db.registro.getString(i+2));
                }
                
                                
            }
            
                       
            for(int i=0; i<numAttr; i++){
                estadisticaMD[i][2] = media1[i];
                media1[i] = media1[i]/tuplas;                           
                estadisticaMD[i][0] = media1[i];                 
            }
            //db.registro.beforeFirst();
            //db.comando = db.conexion.createStatement();
            db.registro.close();
            db.registro = db.comando.executeQuery(Query);

            while (db.registro.next()) {
                
                for (int i = 0; i < numAttr; i++) {
                    val = Integer.parseInt(db.registro.getString(i+2));
                    sumas[i] = sumas[i] + Math.pow(val - media1[i],2);
                
                }
                
                                
            }
            
            for(int i=0; i<numAttr; i++){
                sumas[i] = sumas[i]/tuplas;                           
                estadisticaMD[i][1] = Math.sqrt(sumas[i]);                
 
            }
            
            System.out.println("FUNCION ESTADISTICAS 2");
            for (int i = 0; i < numAttr; i++) {
                System.out.println("AT: " + i + "   SUM:\t" + estadisticaMD[i][2] + "\tMedia:\t" + estadisticaMD[i][0] + "\t\tDesv:\t" + estadisticaMD[i][1]);
            }
            
            db.registro.close();
            db.comando.close();
            db.conexion.close();
            return estadisticaMD;
   }

    
}
