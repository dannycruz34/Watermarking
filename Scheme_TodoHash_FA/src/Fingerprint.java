/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

MODIFICACION CON NUMEROS ALEATORIOS PARA LOS INDICES DE LA HUELLA

 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author DanniC
 */
public class Fingerprint {
    
    public static Conexion db;
     public static Conexion db2;

    
    public Fingerprint(Conexion database){
        db = database;
            db2 = new Conexion();
            db2.MySQLConnect();
    }
    
    public int insercionHuella(Parametros p) throws SQLException, IOException{
        
        String id_key;
        String geneS;
        BigInteger fTupla,lsb,attr,l;
        int numAttr;
        int attribute_num;
        int indiceAttr;
        int bit_index;
        int longitud = p.getHuella().length();
        int fingerprint_bit;
        int mark_bit;
        int valor;
        int index=0;
        int fingerprint_index;
        int payload = 0;
        int valorActual;
        int[][] fingerprint = new int[longitud][2];
        Random number;       
        CriptographicFunctions funciones =  new CriptographicFunctions();
        ArrayList atributos = new ArrayList();


        
        //PARAMETROS PRINCIPALES
        fTupla = new BigInteger(p.getFtupla());
        numAttr = Integer.parseInt(p.getNumAttr());
        attr = new BigInteger(p.getnA());
        lsb = new BigInteger(p.getLsb());
        l = new BigInteger("" + p.getHuella().length());
        
        //Parametros de HASH
        BigInteger n;
        int indice;
        int numRepetidos=0;
        int numMarcas = 0;
        
        //NUM REGISTROS DE LA DB
        String count = "SELECT COUNT(*) FROM " + p.getNombreTabla();
        db.comando = db.conexion.createStatement();
        db.registro = db.comando.executeQuery(count);
        db.registro.next();
        int tuplas = db.registro.getInt(1);
        db.registro.close();
        db.comando.close();
         
        //CALCULAR MEDIA Y DESV DE CADA ATRIBUTO
        double[][] estadistica = estadisticas2(p.getNombreTabla(),numAttr);
        //estadisticas2(p.getNombreTabla(),numAttr);               
        
        //DISTORSION GLOBAL POR ATRIBUTO
        int[] distorsionUnits = new int[numAttr];
        
        
        //CALCULAR MSE
        int[] mse = new int[numAttr];
        int[] valOriginal = new int[numAttr];
        int[] valMod = new int[numAttr];
        int[] seleccionAttr = new int[numAttr];
        
        //INCORPORAR CORRIDA PARA REGISTROS POR MOHANPURKAR
        ArrayList mohanpurkar = recuperarMohanpurkar();
        int indiceP=0;
        boolean aux3;
        int numBits = mohanpurkar.size();
        ///////////////////////////////////////////////////

        //MEDIR TIEMPO DE EJECUCION DE LA INSERCION
        long TInicio, TFin, tiempo;           //Para determinar el tiempo
        TInicio = System.currentTimeMillis(); //de ejecución
                
        //CONSULTA DE TODOS LOS REGISTROS
        String Query = "SELECT * FROM " + p.getNombreTabla();
        db.comando = db.conexion.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
              java.sql.ResultSet.CONCUR_READ_ONLY);
        db.comando.setFetchSize(Integer.MIN_VALUE);
        db.registro = db.comando.executeQuery(Query);
       
        
        // Insercion en cada registro
        while (db.registro.next()) {
                        
          /*  System.out.println("key: " + db.registro.getString(1) + 
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
            
            //Generar pseudoaleatorio
            id_key = db.registro.getString(1);
            geneS = p.getKey_secret() + id_key;
           //******** System.out.println("LLave + id : " + geneS);
            String resumen = funciones.getSha256( geneS );
            n = new BigInteger(resumen,16);
           // System.out.println("N: " + n);
            indice = (n.mod(fTupla)).intValue();
            
            //LLENAR VECTORES PARA MSE
            for (int i = 0; i < numAttr; i++) {
                valOriginal[i] = Integer.parseInt(db.registro.getString(i+2));
                valMod[i] = Integer.parseInt(db.registro.getString(i+2));
            }
                        
           /////////////////////////////////////////////////////////////////////////////////////////////
            /*//INCORPORACION DE MOHANPURKAR PARA COMPARAR DISTORSION
            if(indiceP < mohanpurkar.size()){
                aux3 = db.registro.getString(1).equals(mohanpurkar.get(indiceP));
            }else{ 
               aux3 = false;
            }   
            
            if( aux3 == true){
                indiceP++;
            ////////////////////////////////////////////////////////////////////////////////////////////
            */            
            //SELECCION DE LA TUPLA A MARCAR
            if( indice == 0 ){
                //
                //Modificacion por numero de atributos a modificar
                resumen = funciones.getSha256( geneS + resumen);
                n = new BigInteger(resumen,16);
                attribute_num = (n.mod(attr)).intValue() + 1;
                //System.out.println("NumAttr MARCAR: " + attribute_num );
                seleccionAttr[attribute_num-1]++;
                
                //Extraccion del digito que funcionara como indice
                //Obtener posicion bit LSB
                bit_index = (n.mod(lsb)).intValue() + 1;
                //*****System.out.println("LSB: "  + bit_index);
                                
                //Payload
                payload = payload + attribute_num;
               /* ////////////////////POR MOHANPURKAR/////////////////////////////
                if( payload == numBits){
                    indiceP = mohanpurkar.size();
                    System.out.println("PAYLOAD: " + payload);
                }
                ////////////////////////////////////////////////////////////////*/

                //Aleatorios para atributos
                atributos = funciones.aleatoriosAtributos(Integer.parseInt(id_key), numAttr, attribute_num);

 
                
                //INSERCION EN LOS ATRIBUTOS SELECCIONADOS DE LA TUPLA
                for(int i=0;i < attribute_num ; i++){
                    
                    //Seleccion numero de atributo a seleccionar
                    indiceAttr = (int) atributos.get(i);                   
                    
                    //MODIFICACION DE LA HUELLA**********************************************

                    //Obtener bits de huella a insertar en los atributos seleccionados
                    //HASH
                    resumen = funciones.getSha256( geneS + Integer.toString(i) );
                    n = new BigInteger(resumen,16);
                    indice = (n.mod(l)).intValue();
                    fingerprint_index = indice;
                    fingerprint_bit = obtenerBitH(fingerprint_index,p.getHuella());                   
                    

                    //Comprobacion de huella digital
                    fingerprint[fingerprint_index][0] = fingerprint_bit;
                    fingerprint[fingerprint_index][1] = fingerprint[fingerprint_index][1] + 1;
         
                    
                    //Obtener marca bit
                    mark_bit = fingerprint_bit;
                    
                    //Cambiar bit del valor
                    valor = Integer.parseInt(db.registro.getString(indiceAttr+1));
                    //******System.out.println("Attr: " + (indiceAttr) + " Valor: " + valor);              
                    valorActual = (int) cambiarBit(valor,mark_bit, bit_index);
                    
                    //DISTORSION GLOBAL POR ATRIBUTOS
                    if (valor != valorActual) {
                        distorsionUnits[indiceAttr-1] += Math.abs(valorActual - valor);
                    }
                    
                    //VALORES EN VALMOD PARA MSE
                    valMod[indiceAttr-1] = valorActual;
                
                    //Actualizar valor
                    actualizarValor(valorActual,Integer.parseInt(id_key),indiceAttr,p.getNombreTabla());
                }
                                            
                
            }
            
            //CALCULAR MSE
            for (int i = 0; i < numAttr; i++) {
                mse[i] += Math.pow(valOriginal[i] - valMod[i], 2);
            }
            //*****System.out.println("------------------------------------------");           
            
        }
        
        db.registro.close();

        TFin = System.currentTimeMillis();
        tiempo = TFin - TInicio;
        
        System.out.println("Bits insertados : " + payload);
        System.out.println("Huella insertada: ");
        int aux = 0,aux2 = 0;
        for(int i=0; i< longitud;i++){
            System.out.println(i+1 + "\t" + fingerprint[i][0] + "\t" + fingerprint[i][1]);
            aux += fingerprint[i][1];
            if( fingerprint[i][1] == 0){
                aux2++;
            }
        }
        System.out.println("PROMEDIO DE INSERCION DE BITS DE HUELLA: " + aux/fingerprint.length);
        System.out.println("CANTIDAD DE BITS NO INSERTADOS: " + aux2);
        System.out.println("CANTIDAD DE BITS INSERTADOS: " + payload);
        System.out.println("TIEMPO INSERCION (DB): " + tiempo);
        
        System.out.println("Distorsion por atributo en unidades");
        for (int i = 0; i < numAttr; i++) {
            System.out.println("ATTR " + i + ":\t" + distorsionUnits[i]);
        }
        
        System.out.println("MSE");
        for (int i = 0; i < numAttr; i++) {
            System.out.println("ATTR " + i + ":\t" + (mse[i]/(double)tuplas));
        }
        
        System.out.println("Distribucion de seleccion de num de atributos");
        for (int i = 0; i < numAttr; i++) {
            System.out.println("seleccionAttr[" + i +"]: " + seleccionAttr[i] );
        }
        
        estadisticas2(p.getNombreTabla(),numAttr);
        return payload;
    }    


    public long cambiarBit(int valor, int bitHuella, int lsb){
        //Convertir a binario        
        String binario = Integer.toBinaryString(valor);
       // System.out.println("Valor: " + valor);
       // System.out.println("BitHuella: " + bitHuella);
        //System.out.println("LSB: " + lsb);
        //System.out.println("Binario de Valor: " + binario);
        String nuevoVal="";
        if(binario.length() <= lsb){
            binario = "00000" + binario;
        }
                for(int i=0; i< binario.length(); i++){
                    if(i == (binario.length() - lsb)){
                        nuevoVal = nuevoVal + bitHuella;
                    }else{
                        nuevoVal = nuevoVal + binario.substring(i, i+1);
                    }
                }
                long num = Long.parseLong(nuevoVal,2);               
          //      System.out.println("Nuevo Valor: " + nuevoVal);
            //    System.out.println("Nuevo valor decimal: " + num);
        
        return num;
    }

    public void actualizarValor(int valor,int pK,int numAttr,String nombreTabla){
        String queryUpdate = "UPDATE "+nombreTabla+" SET a" + numAttr + "=" + valor +" WHERE id_key="+ pK ;

        try {            
            
            db2.comando = db2.conexion.createStatement();
            db2.comando.executeUpdate(queryUpdate);

        } catch (SQLException ex) {
        }
        
    }

    public int obtenerBitH(int indice, String huella){
        int valor = Integer.parseInt(huella.substring(indice, indice+1));
        //***System.out.println("Indice: " + indice + "  Bit huella funcion: " + valor);
        return valor;
    }
    
        
    public double[][] estadisticas(String nombreTabla,int numAttr) throws SQLException{
        
        String Query = "SELECT * FROM " + nombreTabla;
        String count = "SELECT COUNT(*) FROM " + nombreTabla;
        db.comando = db.conexion.createStatement();
        db.registro = db.comando.executeQuery(count);
        db.registro.next();
        int tuplas = db.registro.getInt(1);
       
        double sumas[] = new double[10];
        double media1[] = new double[10];
        double media2[] = new double[10];
        double varianzas[] = new double[10];
        double estadisticaMD[][] = new double[10][2];
        
        for(int i=0;i<10;i++){
            sumas[i] = 0.0;
            media1[i] = 0.0;
            media2[i] = 0.0;
            varianzas[i] = 0.0;
        }
        
            db.comando = db.conexion.createStatement();
            db.registro = db.comando.executeQuery(Query);

            while (db.registro.next()) {
                
                for (int i = 0; i < numAttr; i++) {
                    media1[i] = media1[i] + Integer.parseInt(db.registro.getString(i+2));
                    media2[i] = media2[i] + (Integer.parseInt(db.registro.getString(i+2)) * Integer.parseInt(db.registro.getString(i+2)));    
                }                
            }
            
                        
            for(int i=0; i < numAttr; i++){
                sumas[i] = media1[i];
                varianzas[i] = media2[i] / (tuplas - 1) - (sumas[i] * sumas[i]) / (double)(tuplas * (tuplas - 1));
                media1[i] = media1[i]/tuplas;                            
                estadisticaMD[i][0] = media1[i];
                
                double desviacion = Math.sqrt(varianzas[i]);
                estadisticaMD[i][1] = desviacion;

            }
           System.out.println("FUNCION ESTADISTICAS");
            for (int i = 0; i < 10; i++) {
                System.out.println("Media " + i + ": " + estadisticaMD[i][0] + "   Desv: " + estadisticaMD[i][1]);
            }
            
            return estadisticaMD;

    }    
    
    public double[][] estadisticas2(String nombreTabla,int numAttr) throws SQLException{
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
            return estadisticaMD;
   }
    
    public ArrayList recuperarMohanpurkar() throws IOException{
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList reducida = new ArrayList();
        try {
            archivo = new File ("/Users/DanniC/Documents/INAOE_2Cuatri/Tesis/Experiementos/reducidaMohanpurkar.txt");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);
           
            
            String linea;
            while((linea = br.readLine())!=null){
                   reducida.add(linea);

            }
            
        } catch (FileNotFoundException ex) {
        }
       
         return reducida;
    }


}
