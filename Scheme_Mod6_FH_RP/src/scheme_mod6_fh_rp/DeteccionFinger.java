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
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author DanniC
 */
public class DeteccionFinger {
    
    public static Conexion db;
    
    public DeteccionFinger(Conexion dbParametro) throws SQLException {
        db = dbParametro;
                
    }

    
    public void deteccion(ParametrosDeteccion p) throws SQLException{
        String id_key;
        String geneS;
        BigInteger fTupla,l,fh;
        int numAttr;
        int longitud;
        int nBits;
        int indiceAttr;
        int fingerprint_bit;
        int valor;
        String valorBinary;
        int fingerprint_index;
        CriptographicFunctions funciones =  new CriptographicFunctions();

        
        //PARAMETROS PRINCIPALES
        fTupla = new BigInteger(p.getFtupla());
        numAttr = Integer.parseInt(p.getNumAttr());
        l = new BigInteger("" + p.getLongitud());
        fh = new BigInteger(p.getFh());
        longitud = Integer.parseInt(p.getLongitud());


        //Parametros de HASH
        BigInteger n;
        int indice;
        int numRepetidos=0;
        int numMarcas = 0;
         
        //CALCULAR MEDIA Y DESV DE CADA ATRIBUTO
        //estadisticas2(p.getNombreTabla(),numAttr);
        
        
        //ORDENAR ATRIBUTOS Y ASIGNAR #BITS /////////////////////////////
        //Numero de registros de la tabla
        String Query = "SELECT COUNT(*) FROM " + p.getNombreTabla();
        db.comando = db.conexion.createStatement();
        db.registro = db.comando.executeQuery(Query);
        db.registro.next();
        int tuplas = db.registro.getInt(1);
        System.out.println("Numero de registros:\t" + tuplas);
        numMarcas = (tuplas/fTupla.intValue()) * numAttr;
        db.registro.close();
        db.comando.close();
        
        //Algoritmo de ordenamiento
        Quicksort sorter = new Quicksort();
        
        //Arreglo para recuperacion de la huella
        //Inicializar huella y contadores
        int[][] count = new int[longitud][2];
        
        for(int i=0; i< longitud; i++){
            count[i][0] = 0;
            count[i][1] = 0;
        }
       
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
            
         
            //SELECCIONA TUPLA PARA MARCAR SI INDICE==0
            if( indice == 0 ){
                //
                //MODIFICACION POR NUMERO DE BITS A DETECTAR( FRACCION DE HUELLA)
                resumen = funciones.getSha256( geneS + resumen);
                n = new BigInteger(resumen,16);
                nBits = (n.mod(fh)).intValue() + 1; 
                //System.out.println("NUM BITS A MARCAR: " + nBits);
                
                //OBTENER LOS NBITS DE LA HUELLA A DETECTAR
                int[] bitsHuella = new int[nBits];
                for (int i = 0; i < nBits; i++) {
                    resumen = funciones.getSha256( geneS + Integer.toString(i) );
                    n = new BigInteger(resumen,16);
                    fingerprint_index = (n.mod(l)).intValue();;
                    bitsHuella[i] = fingerprint_index;
                    
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
                
                //RECORRER ATRIBUTOS POR ORDEN PARA LA DETECCION BITS DE HUELLA
                for (int j = numAttr-1; j >= 0; j--) {
                            
                        //Indice de atributo a marcar( MAYOR NUM DE BITS)
                        indiceAttr = binaryAttr[1][j] + 1;

                        //OBTENER VALOR DEL ATRIBUTO SELECCIONADO
                        valor = Integer.parseInt(db.registro.getString(indiceAttr+1));
                        valorBinary = Integer.toBinaryString(valor);
                        
                        //CONDICION CUANDO # BITS ES MENOR O IGUAL A 6
                        if( valorBinary.length() <= 6 ){
                            //Obtener bit marcado
                            int lsbM = valorBinary.length() - 1;
                            fingerprint_bit = Integer.parseInt(valorBinary.substring(lsbM,lsbM+1));
                            count[bitsHuella[k]][fingerprint_bit]++;  
                            nBits = nBits - 1;     
                            k++;
                        }else{
                            //CALCULAR NUMERO DE BITS A OBTENER EN EL ATRIBUTO
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
                            //DETECCION DE NMARK BITS DE HUELLA                            
                            for (int i = 1; i <= nMark; i++) {

                                //Obtener bit marcado
                                int lsbM = valorBinary.length() - i;
                                fingerprint_bit = Integer.parseInt(valorBinary.substring(lsbM,lsbM+1));
                                count[bitsHuella[k]][fingerprint_bit]++;  
                                nBits = nBits - 1;
                                k++;
                                if( nBits == 0)
                                    break;
                                                                
                            }                                                     
                        
                        }                                                

                        //BREAK PARA SALIR DEL FOR
                        if( nBits == 0)
                            break;
                }
            }                                                                        

           // System.out.println("------------------------------------------");           
            
        }
        //Cerrar ResultSet
        db.registro.close();
        
        //TIEMPO DE DETECCION
        TFin = System.currentTimeMillis();
        tiempo = TFin - TInicio;
        
        
       /* System.out.println("Fingerprint recuperado con contadores de 0 y 1");
        for(int i=0;i<longitud;i++){
            System.out.println(i + "\t0: " + (count[i][0] + "\t1: " + count[i][1]));
        }*/
        
        //Funcion de recuperacion y identificacion del Traidor
        recuperarHuella(count,p.getCodigo(),longitud);
        
        System.out.println("TIEMPO DE DETECCION (DB):\t" + tiempo);

       
    }
    
    
    
    public int repartoProporcional(int bMark1, int bMark2, int nBits){
        int nMark = (int) Math.ceil((bMark1 * nBits) / (double)(bMark1 + bMark2));
        return nMark;
    }    
    
    public String obtenerBitsHuella(String huella){
        String text = huella;
        System.out.println("Texto: "+ text);

        String binary = new BigInteger(text.getBytes()).toString(2);
        System.out.println("Binario: " + binary);

        String text2 = new String(new BigInteger(binary, 2).toByteArray());
        System.out.println("Texto: "+text2);
       
        String huellaCorta = binary.substring(0,64);
        System.out.println(huellaCorta.length());
        
        return huellaCorta;
    }
    
    
    public String verificaBit(int bit_index, String valor){
        int tam=0;
        String acompletar="";
        if(valor.length() < bit_index){
            tam = bit_index - valor.length();
            for(int i=0;i < tam; i++){
                acompletar = acompletar + "0";
            }
            valor = acompletar + valor;            
        }
        return valor;
    }
    
    public void recuperarHuella(int count[][],TardosCode codigoTardos,int longitud){
        int L=longitud;
        boolean[] fingerprint = new boolean[L];
        float T=0.5f;
        int x=0;
        for(int i=0; i< L; i++){
            if(count[i][0] + count[i][1] == 0){
                x++;
               // System.out.println("None");
            }else{
                if(count[i][0]/(float)(count[i][0] + count[i][1]) > T){
                    fingerprint[i] = false;
                }
                if(count[i][1]/(float)(count[i][0] + count[i][1]) > T){
                    fingerprint[i] = true;
                }
            }
        }
        System.out.println("Bits faltantes:\t" + x);
        /*System.out.println("Huella final recuperada");
        for(int i=0; i< L; i++){
            if(fingerprint[i] == true)
                System.out.println(i+1 + "\t" + 1);
            else
                System.out.println(i+1 + "\t" + 0);
        }*/
        
        ///LLAMAR FUNCION PARA DETERMINAR AL TRAIDOR CON TARDOS
        //Arreglo para insertar culpables
        ArrayList culpables = new ArrayList();
        //LLamado a la funcion traidorTardos
        culpables = traidorTardos(fingerprint, codigoTardos);
        if( culpables.size() > 0){
            for (int i = 0; i < culpables.size(); i++) {
                //System.out.println("User : " + culpables.get(i));

            }
        }else{
            System.out.println("No se detecto ningun usuario");
        }            
        
        ///////////////////////////////////////////////////////
        
    }
    
    public ArrayList traidorTardos(boolean[] fingerprint, TardosCode codigo){
        ArrayList culpables = new ArrayList();
        Tardos codeTools = new Tardos();
        int tamColusion= 2;
        culpables = codeTools.accusation(codigo.getHuella(), fingerprint, codigo.getP(), tamColusion , codigo.getK());
        return culpables;
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

    
}
