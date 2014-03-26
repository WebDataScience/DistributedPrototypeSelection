/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Genetic_Rule_Learning.apriori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.core.Fichero;

/**
 * <p>T�tulo: Clase principal del algoritmo</p>
 * <p>Descripci�n: Contiene los metodos esenciales del algoritmo APRIORI</p>
 * @author Alberto Fern�ndez Hilario 31-01-2006.
 * @version 1.0
 */
public class apriori {
    public apriori() {
    }

    private int nClases; // Numero m�ximo de clases
    private int datos; //n�mero total de datos (transacciones)
    private int entradas; //n�mero total de entradas (variables / columnas)

    private double Smin;
    private double Cmin;

    private int[] maximos;

    private Dataset train, eval, test;
    private ConjDatos datosTrain, datosTest, datosEval;

    private int[][] X; //conjunto de datos de entrada (transacciones);
    private int[] C; //conjunto de clases (transacciones junto con los datos de entrada)

    private ConjReglas reglas;
    private ConjReglas rFinal;

    private String ficheroSalida;
    private String ficheroSalidaTr;
    private String ficheroSalidaTst;
    private String miSalida;

    private int N;
    private int postpoda;

    private EvaluaCalidadReglas evReg; // Para evaluar la calidad de las reglas

    private long tiempo;
    private String[] nombreAtributos;
    private String[] nombreClases;

    private int muestPorClaseEval[];
    private int muestPorClaseTest[];
    private boolean hayContinuos = false;

    public boolean todoBien(){
        return (!hayContinuos);
    }
    
    /**
     * Constructor de la clase pruebas</br>
     * Simplemente nos ocupamos de hacer una copia local a la clase de los nombres
     * de los ficheros para su posterior uso;<br/>
     * Despues obtenemos todos los datos del fichero y los guardamos en un formato
     * reconocible por el programa.</br>
     * Por �ltimo crea todos los posibles selectores que se puedan dar para el conjunto
     * concreto de datos y los almacena.
     * @param ftrain Nombre del fichero donde reside el conjunto de entrenamiento
     * @param feval Nombre del fichero donde reside el conjunto de validaci�n
     * @param ftest Nombre del fichero donde reside el conjunto de test
     * @param fSalidaTr Nombre del fichero de salida donde guardaremos el resultado de entrenamiento
     * @param fSalidaTst Nombre del fichero de salida donde guardaremos el resultado de test
     * @param fsal Nombre del fichero donde guardaremos los datos generales de salida (reglas, tiempo...)
     * @param _Smin Minimo Support
     * @param _Cmin Minimo Confidence
     * @param _N N�mero m�ximo de reglas a generar
     * @param _postpoda Se refiere al tipo de postpoda
     */
    public apriori(String ftrain, String feval, String ftest, String fSalidaTr,
                   String fSalidaTst, String fsal, double _Smin, double _Cmin,
                   int _N, int _postpoda) {
        int i;

        //System.out.println("Ejecutandose ejemplo prueba");
        tiempo = System.currentTimeMillis(); // medimos tiempo

        ficheroSalida = fsal;
        ficheroSalidaTr = fSalidaTr;
        ficheroSalidaTst = fSalidaTst;

        N = _N;
        postpoda = _postpoda;

        train = new Dataset();
        eval = new Dataset();
        test = new Dataset();

        try {
            //System.out.println("\nLeyendo train: " + ftrain);
            train.leeConjunto(ftrain, true);
            if (train.hayAtributosContinuos()){
                System.err.println("Apriori may not handle continuous attributes.\nPlease discretize the data base");
                //System.exit(-1);
                hayContinuos = true;
            }
            //System.out.println("\nLeyendo eval: " + feval);
            eval.leeConjunto(feval, false);
            if (eval.hayAtributosContinuos()){
                System.err.println("Apriori may not handle continuous attributes.\nPlease discretize the data base");
                //System.exit(-1);
                hayContinuos = true;
            }
            //System.out.println("\nLeyendo test: " + ftest);
            test.leeConjunto(ftest, false);
        } catch (IOException e) {
            System.err.println("There was a problem while trying to read the dataset files:");
            System.err.println("-> " + e);
            System.exit(0);
        }

        X = train.getX();
        C = train.getC();

        datos = train.getndatos();
        entradas = train.getnentradas();
        nClases = train.getnclases();

        datosEval = new ConjDatos();
        datosEval = creaConjunto(eval);

        maximos = train.ordenLexicografico();
        //maximos = test.ordenLexicografico();

        datosTrain = new ConjDatos();
        datosTest = new ConjDatos();

        datosTrain = creaConjunto(train); //Leemos los datos de entrenamiento (todos seguidos como un String)
        datosTest = creaConjunto(test); //Idem TEST

        int[] auxiliar = eval.copiaC();
        int [] clasesEval = eval.copiaC();
        Arrays.sort(auxiliar);
        int [] valorClases = new int[nClases];
        valorClases[0] = auxiliar[0];
        int valor = 0;
        for (i = 1; i < nClases; i++) {
            int j;
            for (j = valor;
                     (j < auxiliar.length) && (auxiliar[j] == valorClases[i - 1]);
                     j++) {
                ;
            }
            if (j < auxiliar.length) {
                valorClases[i] = auxiliar[j];
                valor = j;
            }
        }

        muestPorClaseEval = new int[nClases];
        for (int j = 0; j < nClases; j++) {
            muestPorClaseEval[j] = 0;
            for (i = 0; i < datosEval.size(); i++) {
                if (valorClases[j] == clasesEval[i]) {
                    muestPorClaseEval[j]++;
                }
            }
        }

        int [] clasesTest = test.getC();

        auxiliar = test.getC();
        Arrays.sort(auxiliar);
        valorClases = new int[nClases];
        valorClases[0] = auxiliar[0];
        valor = 0;
        for (i = 1; i < nClases; i++) {
            int j;
            for (j = valor;
                     (j < auxiliar.length) && (auxiliar[j] == valorClases[i - 1]);
                     j++) {
                ;
            }
            if (j < auxiliar.length) {
                valorClases[i] = auxiliar[j];
                valor = j;
            }
        }

        muestPorClaseTest = new int[nClases];
        for (int j = 0; j < nClases; j++) {
            muestPorClaseTest[j] = 0;
            for (i = 0; i < datosTest.size(); i++) {
                if (valorClases[j] == clasesTest[i]) {
                    muestPorClaseTest[j]++;
                }
            }
        }

        Cmin = _Cmin;
        Smin = _Smin * (datos - 0.5);

        reglas = new ConjReglas();
        rFinal = new ConjReglas();

        miSalida = new String("");
        miSalida = test.copiaCabeceraTest();

        nombreAtributos = train.dameNombres();
        nombreClases = train.dameClases();
        if (nombreClases == null){
            nombreClases = new String[nClases];
            for (i = 0; i < nClases; i++){
                nombreClases[i] = ""+valorClases[i];
            }
        }

    }

    /**
     * Crea un conjunto de datos (atributos/clase) segun los obtenidos de un fichero de datos
     * @param mis_datos Debe ser un conjunto de datos leido del fichero (mirar doc Dataset.java)
     * @return El conjunto de datos ya creado, es decir, una lista enlazada de muestras (consultar ConjDatos.java y Muestra.java)
     */
    private ConjDatos creaConjunto(Dataset mis_datos) {
        ConjDatos datos = new ConjDatos(); //Creo un nuevo conjunto de datos
        int tam = mis_datos.getnentradas(); //Pillo el n�mero de atributos de entrada (suponemos una sola salida [clase])
        double[] vars = new double[tam]; //Creamos el vector que guardar� los valores de los atributos (aun siendo enteros o enum)
        int[][] X;
        int[] C;
        int clase = 0; //Variable que contendr� el valor para la clase
        boolean salir = false;
        X = mis_datos.getX();
        C = mis_datos.getC();
        for (int i = 0; i < mis_datos.getndatos(); i++) {
            salir = false;
            for (int j = 0; (j < tam) && !salir; j++) {
                if (mis_datos.isMissing(i, j)) {
                    vars[j] = Double.NaN;
                } else {
                    vars[j] = X[i][j]; //Double.parseDouble(mis_datos.getDatosIndex(i, j)); //pillo el valor del atributo j para el ejemplo i
                }
            }
            if (!salir) {
                clase = C[i]; //Integer.parseInt(mis_datos.getDatosIndex(i, tam));
                Muestra m = new Muestra(vars, clase, tam); //Creo un nuevo dato del conjunto con sus variables, su clase y el num variables
                m.setPosFile(i);
                datos.addDato(m);
            }
        }
        return datos;
    }


    /**
     * Busca y crea los 1-items, es decir, conjuntos de un elemento con un support >= Smin
     * @param L ArrayList Lista donde guardar� los 1-items
     */
    private void uno_items(ArrayList L) {
        int[] auxi = new int[datos];
        int S;
        int itemAct = 0;

        for (int i = 0; i < entradas; i++) { //para cada columna (atributos)
            for (int j = 0; j < datos; j++) {
                auxi[j] = X[j][i];
            }
            Arrays.sort(auxi); //Ordeno de menor a mayor la columna
            int valor = itemAct;
            int j;
            for (j = 0; auxi[j] == -1; j++) {
                ;
            }
            valor = auxi[j];
            S = 0;
            for (; valor != maximos[i]; j++) { //A�ado items por columnas en funcion de su support
                if (valor == auxi[j]) { //cuento uno mas porque se repite
                    S++;
                } else { //A�ado el item si tiene suficiente Support
                    if (S >= Smin) { //Tiene mayor support
                        Item item = new Item(valor, i, S);
                        L.add(item); //a�ado el item
                    }
                    S = 1; //reinicio el contador de support
                    valor = auxi[j];
                }
            }
            S = datos - j + 1; //para el �ltimo (n� datos - los que ya he contado)
            if (S >= Smin) {
                Item item = new Item(maximos[i], i, S);
                L.add(item);
            }
            itemAct = maximos[i] + 1;
        }

        //Ahora contamos las clases (deber�an ser 1-items!!)
        for (int j = 0; j < datos; j++) {
            auxi[j] = C[j];
        }
        Arrays.sort(auxi);

        //Sigo el mismo esquema que con los otros 1-items
        int valor = itemAct;
        S = 1;
        int j;
        for (j = 1; valor != maximos[entradas]; j++) { //A�ado items por columnas en funcion de su support
            if (valor == auxi[j]) {
                S++;
            } else {
                if (S >= Smin) { //Tiene mayor support
                    Item item = new Item(valor, entradas, S);
                    L.add(item);
                }
                S = 1;
                valor = auxi[j];
            }
        }
        S = datos - j + 1;
        if (S >= Smin) {
            Item item = new Item(maximos[entradas], entradas, S);
            L.add(item);
        }
        //YA TENGO LOS 1-ITEMS!!
        //Collections.sort(L);
        /*
                 System.out.println("1-ITEMS:");
                 for (int i = 0; i < L.size(); i++){
            Item it = (Item)L.get(i);
            System.out.println(it.getItem()[0]+" ("+it.getSupport()+")");
                 }
                 System.exit(-1);
         */
    }

    /**
     * Creacion de candidatos k-items de L(k-1)
     * @param L ArrayList Lista de k-1 items
     * @param Cand ArrayList Lista que contendr� los k-items Candidatos
     */
    private void creaCandidatos(ArrayList L, ArrayList Cand) {
        for (int i = 0; i < L.size() - 1; i++) {
            Item aux = (Item) L.get(i); //tomamos el item para hacer el subconjunto
            for (int j = i + 1; j < L.size(); j++) { //para todos los demas conjuntos
                Item aux2 = (Item) L.get(j); //tomamos el item para hacer el subconjunto
                Item it = new Item(); //conjunto que voy a crear
                boolean seguir = it.creaItem(aux, aux2); //intento hacer uno nuevo con esos 2
                if (seguir) {
                    Cand.add(it); //A�ado si lo he creado correctamente (todos los subconjuntos estan en L)
                }
            }
        }
    }

    /**
     * Paso que elimina aquellos k-items no v�lidos (alguno de sus subconjuntos no pertenece a L)
     * @param L ArrayList Lista de k-1 Items
     * @param Cand ArrayList Lista de k-items candidatos
     * @param k int Valor de k con el que estamos trabajando actualmente
     */
    private void pruneStep(ArrayList L, ArrayList Cand, int k) {
        for (int i = 0; i < Cand.size(); i++) { //Para todos los candidatos
            //Hacemos los 3 subconjuntos:
            Item item = (Item) Cand.get(i);
            int[] it = item.getItem();
            int[] subconjunto = new int[k - 1];
            for (int j = 0; j < k - 1; j++) {
                subconjunto[j] = it[j]; //primer subconjunto
            }
            //compruebo:
            boolean parar = false, seguir = true;
            for (int j = 0; (j < L.size()) && !parar; j++) { //Busco entre todos los elementos de L hasta que encuentre 1 =
                Item aux = (Item) L.get(j);
                int[] aux2 = aux.getItem();
                seguir = true;
                for (int l = 0; (l < k - 1) && seguir; l++) { //seguir = true si es igual todo el rato
                    seguir = (aux2[l] == subconjunto[l]);
                }
                parar = seguir; //parar = true si el item 'j' y el subconjunto son iguales
            }
            if (!parar) { //Este subconjunto no estaba en L
                Cand.remove(i);
                i--; //porque kito uno
            } else {
                subconjunto[0] = it[0];
                for (int j = 2; j < k; j++) {
                    subconjunto[j - 1] = it[j]; //segundo subconjunto
                }
                //compruebo:
                parar = false;
                for (int j = 0; (j < L.size()) && !parar; j++) {
                    Item aux = (Item) L.get(j);
                    int[] aux2 = aux.getItem(); //tomamos el item para ver si est�
                    seguir = true;
                    for (int l = 0; (l < k - 1) && seguir; l++) {
                        seguir = (aux2[l] == subconjunto[l]);
                    }
                    parar = seguir;
                }
                if (!parar) { //Este subconjunto no estaba en L
                    Cand.remove(i);
                    i--; //porque kito uno
                } else {
                    for (int j = 1; j < k; j++) {
                        subconjunto[j - 1] = it[j]; //tercer subconjunto
                    }
                    //compruebo
                    parar = false;
                    for (int j = 0; (j < L.size()) && !parar; j++) {
                        seguir = true;
                        Item aux = (Item) L.get(j);
                        int[] aux2 = aux.getItem(); //tomamos el item para ver si est�
                        for (int l = 0; (l < k - 1) && seguir; l++) {
                            seguir = (aux2[l] == subconjunto[l]);
                        }
                        parar = seguir;
                    }
                    if (!parar) { //Este subconjunto no estaba en L
                        Cand.remove(i);
                        i--; //porque kito uno
                    }
                }
            }
        }

    }

    /**
     * Cuenta las ocurrencias de cada k-item en Cand que aparezca en el conjunto de entrenamiento (transacciones).
     * @param Cand ArrayList Lista de k-items Candidatos
     * @param k int Valor de k actual
     */
    private void contar(ArrayList Cand, int k) {
        for (int i = 0; i < Cand.size(); i++) { //Contar los candidatos
            Item item = (Item) Cand.get(i);
            int[] aux = item.getItem();
            int[] columnas = item.getColumnas();
            if (aux[1] <= maximos[columnas[0]]) { //Estan en la misma columna
                Cand.remove(i); //Ni me molesto en contarlo
                i--; //porque he eliminado uno.
            } else {
                boolean parar = false;
                for (int h = 1; (h < k) && !parar; h++) { //Para todos los elementos del k-item
                    if (aux[h] <= maximos[columnas[h - 1]]) { //Estan en la misma columna
                        Cand.remove(i); //Ni me molesto en contarlo
                        i--; //porque he eliminado uno.
                        parar = true; //dejo de fijarme en lo de las columnas
                    }
                }
                if (!parar) { //No he parado, luego estan todos en una columna distinta
                    //Aqui ya los cuento
                    int contador = 0;
                    if (columnas[k - 1] < entradas) { //Ambas son entradas
                        for (int l = 0; l < datos; l++) {
                            boolean seguir = true;
                            for (int j = 0; (j < k) && seguir; j++) {
                                seguir = (X[l][columnas[j]] == aux[j]);
                            }
                            if (seguir) {
                                contador++; //Son todos iguales
                            }
                        }

                    } else { //El ultimo es una clase
                        for (int l = 0; l < datos; l++) {
                            boolean seguir = true;
                            for (int j = 0; (j < k - 1) && seguir; j++) {
                                seguir = (X[l][columnas[j]] == aux[j]);
                            }
                            seguir = seguir && (C[l] == aux[k - 1]);
                            if (seguir) {
                                contador++; //Son todos iguales
                            }
                        }
                    }
                    if (contador < Smin) {
                        Cand.remove(i);
                        i--; //porque he eliminado uno.
                    } else {
                        item.setSupport(contador);
                        //item.print();
                        /*if (contador > 25){
                            item.print();
                                                 }
                         */
                    }
                }
            }
        }
    }

    /**
     * Para cada ITEM en el conjunto L comprueba si el ultimo valor corresponde a una clase
     * y crea una regla si supera el Cmin
     * @param L ArrayList Conjunto de Items
     */
    private void ponReglas(ArrayList L) {
        for (int i = 0; i < L.size(); i++) {
            Item it = (Item) L.get(i);
            int[] aux = it.getItem();
            int[] columnas = it.getColumnas();
            int clase = (int) aux[aux.length - 1];
            if (clase > maximos[entradas - 1]) { //El ultimo valor corresponde a una clase
                int contador = 0;
                for (int l = 0; l < datos; l++) {
                    boolean seguir = true;
                    for (int j = 0; (j < aux.length - 1) && seguir; j++) {
                        seguir = (X[l][columnas[j]] == aux[j]);
                    }
                    if (seguir) {
                        contador++; //Son todos iguales
                    }
                }
                double conf = (double) it.getSupport() / contador;
                if (conf > Cmin) { //s(l) / s(a) > minconf
                    Regla r = new Regla(it);
                    r.setConf(conf);
                    reglas.addRegla(r);
                    L.remove(i); //Elimino el item utilizado para crear la regla
                    i--;
                    //System.out.println("Conf-> " + conf + " item: " +r.printString() + " C:" + r.getClase());
                }
            }
        }
    }

    /**
     * Elimina los ejemplos cubiertos por la regla r
     * @param r Regla Regla a comprobar
     * @param datos ConjDatos Es la lista de ejemplos a comprobar
     */
    private void eliminaCubiertos(Regla r, ConjDatos datos) {
        for (int i = 0; i < datos.size(); i++) {
            if (r.cubre(datos.getDato(i))) {
                datos.deleteDato(i);
                i--;
            }
        }
    }

    /**
     * Recalcula el support para cada regla segun los ejemplos que quedan
     * @param datos ConjDatos Conjunto de ejemplos restantes
     * @return boolean True si no se pueden cubrir mas ejemplos, False en caso contrario (alguna regla tiene S > 0)
     */
    private boolean recalculaSupport(ConjDatos datos) {
        boolean ret = true;
        for (int i = 0; i < reglas.size(); i++) {
            int support = 0;
            int contador = 0;
            Regla r = reglas.getRegla(i);
            for (int j = 0; j < datos.size(); j++) {
                if (r.cubre(datos.getDato(j))) {
                    contador++;
                    if (r.getClase() == datos.getDato(j).getClase()) {
                        support++;
                        ret = false;
                    }
                }
            }
            if (support == 0) {
                reglas.deleteRegla(i);
                i--;
            } else {
                double conf = (double) support / contador;
                r.setSupport(support);
                r.setConf(conf);
            }
        }
        return ret;
    }

    /**
     * Escribe los ficheros de salida KEEL basicos (train y test) y un informe de las reglas (valores y support)
     * @param reglas ConjReglas El conjunto de reglas a analiar
     */
    private void generaSalida(ConjReglas reglas) {
        reglas.ajusta(train.getCambio());
        reglas.adjuntaNombreClases(nombreClases);
        reglas.adjuntaNombreClase(nombreAtributos[entradas]);

        evReg = new EvaluaCalidadReglas(reglas, nClases,nombreClases);
        evReg.calculaSignificance(datosEval,muestPorClaseEval,0);
        evReg.calculaSignificance(datosEval,muestPorClaseTest,1);
        Fichero f = new Fichero();
        String cad = "";

        f.escribeFichero(ficheroSalidaTr,
                         miSalida + evReg.salida(datosEval, 0));
        f.escribeFichero(ficheroSalidaTst, miSalida + evReg.salida(datosTest, 1));

        cad = reglas.printString();
        cad += "\n\n" + evReg.printString() + "\nTIME (sec): " +
                (tiempo / 1000);
        f.escribeFichero(ficheroSalida, cad);

        System.out.print(cad);
    }

    public void ejecutar() {
        ArrayList L = new ArrayList();
        uno_items(L); //En L se guardaran los 1-items
        System.out.println("1-ITEMS COMPUTED!! Total: " + L.size());
        //Algoritmo principal
        ArrayList Cand = new ArrayList();
        for (int k = 2; L.size() > 0; k++) {
            Cand.clear();
            creaCandidatos(L, Cand); //En Cand se guardan los k-items candidatos
            System.out.println(k + "-ITEMS Candidates created!! Total: " +
                               Cand.size());
            //-----------------------
            if (k > 3) {
                pruneStep(L, Cand, k); //En Cand solo quedan los 'v�lidos' (aunque habr� algunos que no se podr�n contar)
                //es decir, aquellos que tengan 2 items en la misma columna!
                System.out.println(
                        "Candidates elimination step. Remaining: " +
                        Cand.size());
            }
            //----------------
            //System.out.println("");
            contar(Cand, k); //count step
            L.clear();
            L.addAll(Cand); //Meto los candidatos en L
            ponReglas(L); //introduzco nuevas reglas en el conjunto si procede
            System.out.println(k + "-ITEMS COMPUTED!! Total: " + L.size());
            /*
                         for (int i = 0; i < L.size(); i++) {
                Item it = (Item) L.get(i);
                for (int j = 0; j < it.getItem().length; j++){
                    System.out.print(it.getItem()[j] + " ");
                }
                System.out.println(" (" + it.getSupport() + ")");
                         }
             */
        }
        //reglas.print(); //a ver qu� sale...

        //Ahora realizo el post-procesamiento: Selecci�n de un subconjunto de reglas

        boolean parar = false;
        ConjDatos auxiliar = datosTrain.copiaConjDatos();
        Collections.sort(reglas.getConjReglas());
        if (postpoda == 0) {
            // a) Usar las N mejores reglas (falla si hay reglas que no deducen una de las clases: NO se agotan ej's):
            for (int i = 0;
                         (i < N) && (auxiliar.size() > 0) &&
                         (reglas.size() > 0) && (!parar);
                         i++) { //Hasta que me quede con N reglas o no haya ejemplos o no haya reglas
                Collections.sort(reglas.getConjReglas()); //Ordeno las reglas en funcion de su support y tama�o
                eliminaCubiertos(reglas.getUltimaRegla(), auxiliar); //Ahora elimino los ejemplos cubiertos por la mejor regla
                Regla r = reglas.getUltimaRegla().copiaRegla();
                r.adjuntaNombreAtributos(nombreAtributos);
                rFinal.addRegla(r);
                reglas.deleteRegla(reglas.size() - 1); //Elimino la regla que acabo de usar
                parar = recalculaSupport(auxiliar); //Ahora recalculo el support en funcion de los ejemplos (transacciones) que me quedan
            }
        } else {
            // b) Usar las N mejores reglas para cada clase ---
            for (int j = 0; j < nClases; j++) { //para cada clase
                parar = false;
                for (int i = 0;
                             (i < N) && (auxiliar.size() > 0) &&
                             (reglas.size() > 0) && (!parar);
                             i++) { //Hasta que me quede con N reglas o no haya ejemplos o no haya reglas (para la clase)
                    Collections.sort(reglas.getConjReglas()); //Ordeno las reglas en funcion de su support y tama�o
                    //reglas.print();
                    int l;
                    boolean seguir = true;
                    Regla r = new Regla();
                    for (l = reglas.size() - 1; (l > 0) && seguir; l--) {
                        r = reglas.getRegla(l);
                        seguir = !(r.getClase() ==
                                   (maximos[entradas - 1] + 1 + j)); //Es la clase para la que busco reglas
                    }
                    parar = seguir; //si sigo, no paro xD
                    if (!parar) {
                        eliminaCubiertos(r, auxiliar); //Ahora elimino los ejemplos cubiertos por la mejor regla
                        r.adjuntaNombreAtributos(nombreAtributos);
                        rFinal.addRegla(r.copiaRegla());
                        reglas.deleteRegla(l + 1); //Elimino la regla que acabo de usar
                        parar = recalculaSupport(auxiliar); //Ahora recalculo el support en funcion de los ejemplos (transacciones) que me quedan
                    }
                }
            }
        }
        System.out.print("Init. time: " + tiempo + "  Final: " +
                         System.currentTimeMillis());
        System.out.print("  Diff: " + (System.currentTimeMillis() - tiempo));
        tiempo = System.currentTimeMillis() - tiempo;

        //rFinal.print();
        generaSalida(rFinal); //Creo el fichero de salida KEEL y el de las reglas
    }
}

