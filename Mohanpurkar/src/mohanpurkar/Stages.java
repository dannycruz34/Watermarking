/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mohanpurkar;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static mohanpurkar.Mohanpurkar.db;

/**
 *
 * @author DanniC
 */
public class Stages {
    
    public static Conexion db;
     public static Conexion db2;
     
    public Stages(Conexion database){
            db = database;
            db2 = new Conexion();
            db2.MySQLConnect();
    } 
    public ArrayList getPatitions(String secret_key,String tabla,int numParticiones,double f) throws SQLException{
        
        int indice;
        ArrayList<Particion> particiones = new ArrayList<Particion>();
        int[][] indicesHASH = new int[numParticiones][1];
        BigInteger d = new BigInteger("" + numParticiones);
        BigInteger n;
        double media,desviacion;
        Grupos[] grupos = new Grupos[numParticiones];
        for (int i = 0; i < numParticiones; i++) {
            grupos[i] = new Grupos();
        }
        //////////////////////////////////////////
        String Query = "SELECT * FROM " + tabla;
        db.comando = db.conexion.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
              java.sql.ResultSet.CONCUR_READ_ONLY);
        db.comando.setFetchSize(Integer.MIN_VALUE);        
        db.registro = db.comando.executeQuery(Query);
        
        CryptographicFunctions hash = new CryptographicFunctions();
                 
        // Etiquetado de particiones a cada registro
        while (db.registro.next()) {
           /* System.out.println("key: " + db.registro.getString(1) + 
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
            
            String id_key = db.registro.getString(1);
            String resumen = hash.getSha256(secret_key + id_key);
            resumen = hash.getSha256(secret_key + resumen);
            n = new BigInteger(resumen,16);
            indice = (n.mod(d)).intValue();
            Particion p = new Particion(id_key,indice);
            particiones.add(p);
            indicesHASH[indice][0]++;
            
            //////////////MEDIA,DESVIACION y UMBRAL ///////////////////////
            for (int i = 2,j=0; i < 12; i++,j++) {
                double val = Double.parseDouble(db.registro.getString(i));
                media = grupos[indice].atributos[j].getMedia();
                desviacion = grupos[indice].atributos[j].getDesviacion();
                grupos[indice].atributos[j].setMedia(media + val); 
                grupos[indice].atributos[j].setDesviacion(desviacion + (val*val));
            }
            
        }
        
        //////////MEDIA;DESVIACION;UMBRAL/////////////
        for (int i = 0; i < numParticiones; i++) {
            for (int j = 0; j < 10; j++) {
                double suma1 = grupos[i].atributos[j].getMedia();
                double suma2 = grupos[i].atributos[j].getDesviacion();
                media = grupos[i].atributos[j].getMedia() / indicesHASH[i][0];
                if(indicesHASH[i][0] == 1){
                    desviacion = 0;
                }else{
                    desviacion = suma2 / (indicesHASH[i][0] - 1) - (suma1 * suma1) / (indicesHASH[i][0] * (indicesHASH[i][0] - 1));
                    desviacion = Math.sqrt(desviacion);
                }
                //System.out.println("SumaMedia: " + suma1 + "    SumaDesv: " + suma2 + " #IndiceHash[i][0]:" + indicesHASH[i][0]);
                //System.out.println("G: " + i + " A: " + j + " MED: " + media + " DES: " + desviacion + " UM: " + (f*media+desviacion));
                grupos[i].atributos[j].setMedia(media);
                grupos[i].atributos[j].setDesviacion(desviacion);
                grupos[i].atributos[j].setUmbral(f * media + desviacion);
            }
            
        }
         /////////////ETIQUETADO DE PARTICIONES
       /* for (int i = 0; i < numParticiones; i++) {
            System.out.println("ID: " + i + "\t" + indicesHASH[i][0]);
        }*/
     
        
        db.registro.close();

        return selection(particiones,grupos,tabla);
        
    }
    
    
    public ArrayList selection(ArrayList<Particion> particiones,Grupos[] grupos,String tabla) throws SQLException{
        String Query = "SELECT * FROM " + tabla;
        db.comando = db.conexion.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
              java.sql.ResultSet.CONCUR_READ_ONLY);
        db.comando.setFetchSize(Integer.MIN_VALUE);        
        db.registro = db.comando.executeQuery(Query);
        
        ArrayList<Particion> particionesReducida = new ArrayList<Particion>();
        ArrayList reducida = new ArrayList();
        ///////////////////////                        
        // Etiquetado de particiones a cada registro
        while (db.registro.next()) {                                    
            String id_key = db.registro.getString(1);
            int particion = buscarElemento(particiones,id_key);
          //  System.out.println("ID: " + id_key + " Part: " + particion + " Lon." + particiones.size());
           
            for (int i = 2; i < 12; i++) {
                //System.out.println("VAL: " + db.registro.getString(i) + " UM: " + grupos[particion].atributos[i-2].getUmbral());
                if (Double.parseDouble(db.registro.getString(i)) > grupos[particion].atributos[i-2].getUmbral()) {
                    reducida.add(id_key);
                    Particion p = new Particion(id_key,particion);
                    particionesReducida.add(p);
                    break;
                }
            }
                
        }
        db.registro.close();
        //Antes se retornaba
        //return reducida;
        return particionesReducida;
    }
    
    
    public ArrayList finalSelection(ArrayList<Particion> reducida, String secret_key) throws SQLException{
        
        ArrayList reducidaFinal = new ArrayList();
        ArrayList<Particion> particionesFinal = new ArrayList<Particion>();

        CryptographicFunctions hash = new CryptographicFunctions();
        BigInteger d = new BigInteger("2");
        BigInteger n;
        int indice;
        int cont=0;
        
        for (int i = 0; i < reducida.size(); i++) {
            String resumen = hash.getSha256(secret_key + reducida.get(i).getId_key() );
            n = new BigInteger(resumen,16);
            indice = (n.mod(d)).intValue();
            
            if(indice == 0){
                //reducidaFinal.add(reducida.get(i));
                particionesFinal.add(reducida.get(i));
                cont++;
            }
        }
        
        System.out.println("REGISTROS FINALES PARA FINGERPRINT: " + cont);
        
        return particionesFinal;
    }
    
    
    
    public ArrayList fingerprint(ArrayList<Particion> reducidaFinal,String secret_key,String huella,String tabla,int numAttr,String id_comprador,int c) throws SQLException{

        
        CryptographicFunctions hash = new CryptographicFunctions();
        BigInteger d = new BigInteger("10");
        BigInteger n;
        int indiceAttr;
        boolean aux;
        int cont=0;
        int alteracion=0;
        String bit="";
        ArrayList delta = new ArrayList();
        int longitud = 1842;
        
        //NUM REGISTROS DE LA DB
        String count = "SELECT COUNT(*) FROM " + tabla;
        db.comando = db.conexion.createStatement();
        db.registro = db.comando.executeQuery(count);
        db.registro.next();
        int tuplas = db.registro.getInt(1);
        db.registro.close();
        db.comando.close();
        
        //Estadisticas Antes del fingerprint
        estadisticas2(tabla,numAttr);

        //COmprobacion de fingerprint
        int fingerprint_index;
        int fingerprint_bit;
        int payload = 0;
        int[][] fingerprint = new int[longitud][2];
        
        //DISTORSION GLOBAL POR ATRIBUTO
        int[] distorsionUnits = new int[numAttr];
        
        
        //CALCULAR MSE
        int[] mse = new int[numAttr];
        int[] valOriginal = new int[numAttr];
        int[] valMod = new int[numAttr];
        int[] seleccionAttr = new int[numAttr];
        
        //Consulta de la tabla para la insercion
        String Query = "SELECT * FROM " + tabla;
        db.comando = db.conexion.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
              java.sql.ResultSet.CONCUR_READ_ONLY);
        db.comando.setFetchSize(Integer.MIN_VALUE);                
        db.registro = db.comando.executeQuery(Query);        
        
        //TIEMPO DE INSERCION CON ARREGLO DE REGISTROS REDUCIDA
        long TInicio, TFin, tiempo;           //Para determinar el tiempo
        TInicio = System.currentTimeMillis(); //de ejecución
        
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
            
            //LLENAR VECTORES PARA MSE
            for (int i = 0; i < numAttr; i++) {
                valOriginal[i] = Integer.parseInt(db.registro.getString(i+2));
                valMod[i] = Integer.parseInt(db.registro.getString(i+2));
            }
            
           //aux = reducidaFinal.indexOf(db.registro.getString(1));
           if(indiceP < reducidaFinal.size()){
                aux = db.registro.getString(1).equals(reducidaFinal.get(indiceP).getId_key());
           }else{ 
               aux = false;
           }
           
           if( aux == true ){
               
               //System.out.println("ID: " + reducidaFinal.get(indiceP).getId_key() + " Particion:" + reducidaFinal.get(indiceP).getParticion());
               //Usando llave secreta + llave primaria + id comprador
                String resumen = hash.getSha256(secret_key + reducidaFinal.get(indiceP).getId_key() + id_comprador);
                n = new BigInteger(resumen,16);                
                indiceAttr = (n.mod(d)).intValue();
                
                //System.out.println("Indice Attr: " + indiceAttr);
                seleccionAttr[indiceAttr]++;
                
                ///////////MARCAR////////////////////
                int valor = Integer.parseInt( db.registro.getString(indiceAttr + 2) );
                int indiceH = reducidaFinal.get(indiceP).getParticion();
                bit = huella.substring(indiceH, indiceH+1);
                   
                fingerprint_index = indiceH;
                fingerprint_bit = Integer.parseInt(bit);                   
                    
                //Comprobacion de huella digital
                fingerprint[fingerprint_index][0] = fingerprint_bit;
                fingerprint[fingerprint_index][1] = fingerprint[fingerprint_index][1] + 1;
                
                //Payload
                payload = payload + 1;
                
      
                //Si BIT = 1
               // System.out.println("BIT: " + bit);
                if( bit.equals("1" )){
                    if(valor != 0 )
                        alteracion = c % valor;
                    else
                        alteracion = c;
                }else{
                    if(valor != 0){
                        //System.out.println("-1*c" + -1*c + " Valor:" + valor);
                        alteracion = (-1 * c) % valor;
                    }else{
                        alteracion = -1 * c;
                    }
                }
                
                //DISTORSION GLOBAL POR ATRIBUTOS
                if (valor != (alteracion + valor)) {
                    distorsionUnits[indiceAttr] += Math.abs(alteracion);
                }
                    
                //VALORES EN VALMOD PARA MSE
                valMod[indiceAttr] = (alteracion+valor);                

                //Actualizar registro
               // System.out.println("Valor : " + valor + " Valor+Alter:" + (alteracion + valor));
                valor = alteracion + valor;
                actualizarValor(valor,db.registro.getString(1),indiceAttr+2,tabla);
                ///
                
                ///GUARDAR LA ALTERACION
                delta.add(alteracion); 
                
                //Aumentar indiceP
               indiceP++;
           }
           
           //CALCULAR MSE
            for (int i = 0; i < numAttr; i++) {
                mse[i] += Math.pow(valOriginal[i] - valMod[i], 2);
            }
           
        }
        
        db.registro.close();
        
        //TIEMPO FINAL DE LA INSERCION CON ARREGLO REDUCIDO
        TFin = System.currentTimeMillis();
        tiempo = TFin - TInicio;
        
        System.out.println("Huella insertada: ");
        int aux1 = 0,aux2 = 0;
        for(int i=0; i< longitud;i++){
            System.out.println(i+1 + "\t" + fingerprint[i][0] + "\t" + fingerprint[i][1]);
            aux1 += fingerprint[i][1];
            if( fingerprint[i][1] == 0){ 
                aux2++;
            }
        }
        System.out.println("PROMEDIO DE INSERCION DE BITS DE HUELLA:\t" + aux1/fingerprint.length);
        System.out.println("CANTIDAD DE BITS NO INSERTADOS:\t" + aux2);
        System.out.println("BITS INSERTADOS:\t" + payload);
        System.out.println("TIEMPO INSERCION (DB):\t" + tiempo);
        
        System.out.println("Distorsion por atributo en unidades");
        for (int i = 0; i < numAttr; i++) {
            System.out.println("ATTR " + i + ":\t" + distorsionUnits[i]);
        }
        
        System.out.println("MSE");
        for (int i = 0; i < numAttr; i++) {
            System.out.println("ATTR " + i + ":\t" + (mse[i]/(double)tuplas));
        }
        
        System.out.println("Distribucion de seleccion de atributo");
        for (int i = 0; i < numAttr; i++) {
            System.out.println("seleccionAttr[" + i +"]: " + seleccionAttr[i] );
        }
        
        //Estadisticas pos-fingerprint
        estadisticas2(tabla,numAttr);
        
        //Retornar tabla de alteraciones (Delta)
        return delta;
    }
    
    
    public String deteccion(ArrayList<Particion> reducidaFinal,String secret_key,ArrayList delta,String tabla,String id_comprador,int c) throws SQLException{
        String huella="";
        int longitud = 1842;
         
        CryptographicFunctions hash = new CryptographicFunctions();
        BigInteger d = new BigInteger("10");
        BigInteger n;
        int indiceAttr;
        boolean aux;
        int cont=0,iH=0;
        int alteracion=0;
        
        //Consulta a la DB
        String Query = "SELECT * FROM " + tabla;
        int[][] fingerprint = new int[longitud][2];
        db.comando = db.conexion.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
              java.sql.ResultSet.CONCUR_READ_ONLY);
        db.comando.setFetchSize(Integer.MIN_VALUE);             
        db.registro = db.comando.executeQuery(Query);       
        
        int indiceP = 0;
        while (db.registro.next()) {
           /* System.out.println("key: " + db.registro.getString(1) + 
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
            
            if( aux == true){
                String resumen = hash.getSha256(secret_key + reducidaFinal.get(indiceP).getId_key() + id_comprador);
                n = new BigInteger(resumen,16);
                indiceAttr = (n.mod(d)).intValue();
                
                ///////////MARCAR////////////////////
                int valor = Integer.parseInt( db.registro.getString(indiceAttr + 2) );
                if( valor != 0 ){
                    alteracion = c % valor;
                }else{
                    alteracion = c;
                }
                
                int indiceH = reducidaFinal.get(indiceP).getParticion();
                int valorDelta = Integer.valueOf((String)delta.get(cont));
               // System.out.println("ValorDelta: " + valorDelta + " vs alteracion: " + alteracion);
                if( valorDelta >= alteracion){
                    fingerprint[indiceH][1]++;
                }else{
                    fingerprint[indiceH][0]++;
                }
                cont++;
                indiceP++;
            }   
           
        }
        
        //Mayoria de votos; Recuperar huella
        for (int i = 0; i < longitud; i++) {
            if(fingerprint[i][1] >= fingerprint[i][0]){
                huella = huella + "1";
            }else{
                huella = huella + "0";
            }
        }
        return huella;
    }
    
    
    
    
    public int buscarElemento(ArrayList<Particion> particiones,String id){
        for (int i = 0; i < particiones.size(); i++) {
            if(particiones.get(i).getId_key().equals(id)){
                return particiones.get(i).getParticion();
            }
        }
        return -1;
    }
    
    public void actualizarValor(int valor,String pK,int numAttr,String nombreTabla){
        String queryUpdate = "UPDATE "+nombreTabla+" SET a" + numAttr + "=" + valor +" WHERE id_key="+ pK ;

        try {            
            
            db2.comando = db2.conexion.createStatement();
            db2.comando.executeUpdate(queryUpdate);

        } catch (SQLException ex) {
            System.err.println(ex);
        }
       
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

    
}
