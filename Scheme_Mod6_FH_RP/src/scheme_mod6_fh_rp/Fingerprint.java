package scheme_mod6_fh_rp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

MODIFICACION CON FRACCION DE HUELLA Y ORDENAMIENTO POR # DE BITS

 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
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
    
    public void insercionHuella(Parametros p) throws SQLException, IOException{
        
        String id_key;
        String geneS;
        BigInteger fTupla,lsb,l,fh;
        int numAttr;
        int nBits;
        int indiceAttr;
        int longitud = p.getHuella().length();
        int fingerprint_bit;
        int valor;
        int valorO;
        String valorBinary;
        int index=0;
        int fingerprint_index;
        int payload = 0;
        int valorActual=0;
        int[][] fingerprint = new int[longitud][2];
        CriptographicFunctions funciones =  new CriptographicFunctions();
        ArrayList atributos = new ArrayList();
        ArrayList indices = new ArrayList();      
        ArrayList aleatorios = new ArrayList();
        int numRegistros=0;
        
        //PARAMETROS PRINCIPALES
        fTupla = new BigInteger(p.getFtupla());
        numAttr = Integer.parseInt(p.getNumAttr());
        l = new BigInteger("" + p.getHuella().length());
        fh = new BigInteger(p.getFh());
        
        //Aleatoridad en atributos
        int[] atributosHASH = new int[numAttr];

        //Parametros de HASH
        BigInteger n;
        int indice;

                 
        
        //ORDENAR ATRIBUTOS Y ASIGNAR #BITS /////////////////////////////
        //Numero de registros de la tabla
        String Query = "SELECT COUNT(*) FROM " + p.getNombreTabla();
        db.comando = db.conexion.createStatement();
        db.registro = db.comando.executeQuery(Query);
        db.registro.next();
        int tuplas = db.registro.getInt(1);
        System.out.println("Numero de registros: " + tuplas);
        db.registro.close();
        db.comando.close();
        
        Quicksort sorter = new Quicksort();
        
        
        //DISTORSION GLOBAL POR ATRIBUTO
        int[] distorsionUnits = new int[numAttr];                
        
        //CALCULAR MSE
        int[] mse = new int[numAttr];
        int[] valOriginal = new int[numAttr];
        int[] valMod = new int[numAttr];
         
        
        //INCORPORAR CORRIDA PARA REGISTROS POR MOHANPURKAR
       // ArrayList mohanpurkar = recuperarMohanpurkar();
        int indiceP=0;
        boolean aux3;
        //int numBits = mohanpurkar.size();
        int numRows = 0;
        ///////////////////////////////////////////////////
        
        //MEDIR TIEMPO DE EJECUCION DE LA INSERCION
        long TInicio, TFin, tiempo;           //Para determinar el tiempo
        TInicio = System.currentTimeMillis(); //de ejecución
            
        //CONSULTA DE TODOS LOS REGISTROS
        Query = "SELECT * FROM " + p.getNombreTabla();
        db.comando = db.conexion.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
              java.sql.ResultSet.CONCUR_READ_ONLY);
        db.comando.setFetchSize(Integer.MIN_VALUE);
        db.registro = db.comando.executeQuery(Query);
       
        
        //RECORRIDO DE TODOS LOS REGISTROS
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
            //System.out.println("LLave + id : " + geneS);
            String resumen = funciones.getSha256( geneS );
            n = new BigInteger(resumen,16);
            indice = (n.mod(fTupla)).intValue();
            
            //LLENAR VECTORES PARA MSE
            for (int i = 0; i < numAttr; i++) {
                valOriginal[i] = Integer.parseInt(db.registro.getString(i+2));
                valMod[i] = Integer.parseInt(db.registro.getString(i+2));
            }
            ////////////////////////////////////////////////////////////////////////////////////////////
            /*//INCORPORACION DE MOHANPURKAR PARA COMPARAR DISTORSION
            if(indiceP < mohanpurkar.size()){
                aux3 = db.registro.getString(1).equals(mohanpurkar.get(indiceP));
            }else{ 
               aux3 = false;
            }   
            
            if( aux3 == true){
                indiceP++;
                numRows++;
            ////////////////////////////////////////////////////////////////////////////////////////////
            //CONTINUA METODO PROPUESTO
            */
            //SELECCIONA TUPLA PARA MARCAR SI INDICE==0
            if( indice == 0 ){
                //CONTABILIZAR NUMERO DE REGISTROS OCUPADOS
                numRows++;
                
                //MODIFICACION POR NUMERO DE BITS A INSERTAR( FRACCION DE HUELLA)
                resumen = funciones.getSha256( geneS + resumen);
                n = new BigInteger(resumen,16);
                nBits = (n.mod(fh)).intValue() + 1; 
                //System.out.println("NUM BITS A MARCAR: " + nBits);
                
                //OBTENER LOS NBITS DE LA HUELLA A INSERTAR
                int[] bitsHuella = new int[nBits];
                for (int i = 0; i < nBits; i++) {
                    resumen = funciones.getSha256( geneS + Integer.toString(i) );
                    n = new BigInteger(resumen,16);
                    fingerprint_index = (n.mod(l)).intValue();;
                    bitsHuella[i] = obtenerBitH(fingerprint_index,p.getHuella());
                    //System.out.println("BIT H [" + i + "]: " + bitsHuella[i]);
                    
                    //Comprobacion de huella digital
                    fingerprint[fingerprint_index][0] = bitsHuella[i];
                    fingerprint[fingerprint_index][1] = fingerprint[fingerprint_index][1] + 1;
                    
                }
                
                
                
                //OBTENER LONGITUD DE BITS DE VALORES DE C/ATRIBUTO
                int[][] binaryAttr = new int[2][numAttr];
                for (int i = 0; i < numAttr; i++) {
                    int valorAux = Integer.parseInt(db.registro.getString(i+2));
                    String binario = Integer.toBinaryString(Math.abs(valorAux));                    
                    binaryAttr[0][i] = binario.length();
                    binaryAttr[1][i] = i;
                }
                sorter.sort(binaryAttr);
                //System.out.println("ATRIBUTOS ORDENADOS POR # BITS");
               /* for (int i = 0; i < numAttr; i++) {
                    System.out.println("A[ " + binaryAttr[1][i] + "] VAL: " + binaryAttr[0][i]);
                }*/
                                
                //CONTADOR DE BITSHUELLA
                int k=0;
                
                //RECORRER ATRIBUTOS POR ORDEN PARA INSERTAR BITS DE HUELLA
                for (int j = numAttr-1; j >= 0; j--) {
                            
                        //Indice de atributo a marcar( MAYOR NUM DE BITS)
                        indiceAttr = binaryAttr[1][j] + 1;

                        //OBTENER VALOR DEL ATRIBUTO SELECCIONADO
                        valor = Integer.parseInt(db.registro.getString(indiceAttr+1));
                        valorO = valor;                        
                        valorBinary = Integer.toBinaryString(valor);
                        
                        //CONDICION CUANDO # BITS ES MENOR O IGUAL A 6
                        if( valorBinary.length() <= 6 ){
                            //INSERTAR EN 1 LSB
                            valorActual = (int) cambiarBit(valor, bitsHuella[k] , 1);   
                            nBits = nBits - 1; 
                            payload += 1;   
                            k++;
                        }else{
                            //CALCULAR NUMERO DE BITS A INSERTAR EN EL ATRIBUTO
                            int bMark1 = valorBinary.length() - 5;
                            int nMark = 0;
                            //COMPROBAR SI EXISTE UN ATRIBUTO SIGUIENTE
                            //System.out.println("J > 0:" + j);
                            if( j > 0){
                                //CALULAR NUMERO DE BITS LIBRES EN SIGUIENTE ATRIBUTO
                                int valorSiguiente = Integer.parseInt(db.registro.getString(binaryAttr[1][j-1] + 2));
                                int bMark2 = Integer.toBinaryString(valorSiguiente).length();

                                //CONDICION SI EL SIGUIENTE ATRIBUTO
                                if( bMark2 > 6 ){
                                    bMark2 = bMark2 - 5;
                                }else{
                                    bMark2 = 1;
                                }

                                //FUNCION DE REPARTO PROPORCIONAL
                                //System.out.println("bMark1: " + bMark1 + "  bMark2: " + bMark2 + "  nBits:" + nBits);
                                nMark = repartoProporcional(bMark1,bMark2,nBits);
                            }else{
                                nMark = bMark1;
                            } 
                            //System.out.println("NBITS PARA EL ATRIBUTO: " + nMark);
                            //INSERCION DE BITS DE HUELLA                            
                            for (int i = 1; i <= nMark; i++) {
                                //CAMBIAR los BITS DEL VALOR
                                valorActual = (int) cambiarBit(valor, bitsHuella[k] , i);
                                valor = valorActual;
                                nBits = nBits - 1;
                                k++;

                                
                                //SUMAR PAYLOAD EN 1
                                payload += 1;   
                                /*//////////////////////POR MOHANPURKAR///////////////////////////
                                if( payload == numBits){
                                    indiceP = mohanpurkar.size();
                                    System.out.println("PAYLOAD =" + payload);
                                    break;
                                }
                                ////////////////////////////////////////////////////////////////*/
                                if( nBits == 0)
                                    break;                                
                            }                                                     

                        }                                                
                                                    
                        //DISTORSION GLOBAL POR ATRIBUTOS
                       // System.out.println("FINALES--VALOR: " + valorO + "  VALOR ACTUAL: "  + valorActual);
                        if (valorO != valorActual) {
                            distorsionUnits[indiceAttr-1] += Math.abs(valorActual - valorO);
                        }

                        //VALORES EN VALMOD PARA MSE
                        valMod[indiceAttr-1] = valorActual;

                        //Actualizar valor
                        actualizarValor(valorActual,Integer.parseInt(id_key),indiceAttr,p.getNombreTabla());
                        
                        
                       /* //////////////////////POR MOHANPURKAR///////////////////////////
                        if( payload == numBits){
                            indiceP = mohanpurkar.size();
                            System.out.println("PAYLOAD =" + payload);
                            break;
                        }
                        ////////////////////////////////////////////////////////////////*/
                        //BREAK PARA SALIR DEL FOR
                        if( nBits == 0)
                            break;
                }
            }                                                                        
            
            
            //CALCULAR MSE
            for (int i = 0; i < numAttr; i++) {
                mse[i] += Math.pow(valOriginal[i] - valMod[i], 2);
            }

           // System.out.println("------------------------------------------");           
            
        }
        //Cerrar ResultSet
        db.registro.close();
        
        //TIEMPO DE INSERCION
        TFin = System.currentTimeMillis();
        tiempo = TFin - TInicio;
            
        System.out.println("Huella insertada: ");
        int aux = 0,aux2 = 0;
        for(int i=0; i< longitud;i++){
            System.out.println(i+1 + "\t" + fingerprint[i][0] + "\t" + fingerprint[i][1]);
            aux += fingerprint[i][1];
            if( fingerprint[i][1] == 0){
                aux2++;
            }
        }
        
        System.out.println("TIEMPO DE INSERCION (DB):\t" + tiempo);
        System.out.println("PROMEDIO DE INSERCION DE BITS DE HUELLA:\t" + aux/fingerprint.length);
        System.out.println("BITS INSERTADOS (PAYLOAD) :\t" + payload);
        System.out.println("CANTIDAD DE BITS NO INSERTADOS:\t" + aux2);
        System.out.println("NUMERO DE REGISTROS OCUPADOS:\t" + numRows);
        
        System.out.println("DISTORSION POR ATRIBUTOS EN UNIDADES");
        for (int i = 0; i < numAttr; i++) {
            System.out.println("ATTR " + i + ":\t" + distorsionUnits[i]);
        }
        
        System.out.println("MSE");
        for (int i = 0; i < numAttr; i++) {
            System.out.println("ATTR " + i + ":\t" + (mse[i]/(double)tuplas));
        }
        
        System.out.println("ESTADISTICAS DB MARCADA");
        estadisticas2(p.getNombreTabla(),numAttr);
        
      /*  System.out.println("ALEATORIDAD EN NUM ATRIBUTOS");
        for (int i = 0; i < numAttr; i++) {
            System.out.println("ATTR " + i + ": " + atributosHASH[i]);
        }
    */      
    }    


    public long cambiarBit(int valor, int bitHuella, int lsb){
        //Convertir a binario        
        String binario = Integer.toBinaryString(valor);
        //System.out.println("Valor: " + valor);
        //System.out.println("BitHuella: " + bitHuella);
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
                //System.out.println("Nuevo Valor: " + nuevoVal);
                //System.out.println("Nuevo valor decimal: " + num);
        
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
        //System.out.println("Indice: " + indice + "  Bit huella funcion: " + valor);
        return valor;
    }
    
    
    
    public int repartoProporcional(int bMark1, int bMark2, int nBits){
        int nMark = (int) Math.ceil((bMark1 * nBits) / (double)(bMark1 + bMark2));
        return nMark;
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
                varianzas[i] = media2[i] / (tuplas - 1) - (sumas[i] * sumas[i]) / (tuplas * (tuplas - 1));
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
