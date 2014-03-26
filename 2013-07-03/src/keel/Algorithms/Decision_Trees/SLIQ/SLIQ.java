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

package keel.Algorithms.Decision_Trees.SLIQ;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
Implementaci�n en Java del algoritmo SLIQ
Basada parcialmente en el c�digo del algoritmo ID3 de Crist�bal Romero Morales (UCO)
@author Francisco Charte Ojeda (pr�ctica ICO de la UJA)
@version 1.0 (28/12/09 - 10/1/10)
 */
public class SLIQ extends Algorithm {

    /** Ra�z del �rbol */
    Node root;
    // Para el proceso de divisi�n
    Node subnodoIzquierdo, subnodoDerecho;
    /** N�mero de nodos existentes en el �rbol */
    int NumberOfNodes;
    /** N�mero de hojas en el �rbol */
    int NumberOfLeafs;
    /** Lista con las clases */
    ListaClases[] listaClases;
    /** Listas con los atributos ordenados */
    Vector<ListaAtributos>[] listas;
    /** Lista de nodos pendientes de procesar durante el crecimiento del �rbol */
    Queue<Node> listaNodos;

    /** Constructor.
     *
     * @param paramFile			El archivo de par�metros.
     *
     */
    public SLIQ(String paramFile) {
        try {
            // Inicia el temporizador
            startTime = System.currentTimeMillis();

            // Establecer las opciones de ejecuci�n del algoritmo
            StreamTokenizer tokenizer = new StreamTokenizer(new BufferedReader(new FileReader(paramFile)));
            initTokenizer(tokenizer);
            setOptions(tokenizer);

            // Inicializa el dataset a procesar
            modelDataset = new Dataset(modelFileName, true);
            // Obtener los conjuntos de datos de entrenamiento y prueba
            trainDataset = new Dataset(trainFileName, false);
            testDataset = new Dataset(testFileName, false);

            // Se generan la lista de clases y las listas de atributos, ya ordenadas
            generaListas();

            // El �rbol est� vac�o
            NumberOfNodes = 0;
            NumberOfLeafs = 0;

            // Genera el �rbol seg�n el algoritmo SLIQ.
            generateTree();

            // Imprimir los resultados generados
            printTrain();
            printTest();
            printResult();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    /** Construye las listas por atributo y lista de clases usadas en SLIQ
     *
     */
    protected void generaListas() {
        int n = 0; // Para contabilizar las clases

        // La lista de clases tendr� tantas entradas como muestras de datos en el dataset
        listaClases = new ListaClases[modelDataset.numItemsets()];

        // La lista de listas de atributos tendr� un vector por atributo
        listas = (Vector<ListaAtributos>[]) Array.newInstance(Vector.class, modelDataset.numAttributes());

        // y cada elemento de la lista ser� un vector que hay que crear
        for (int indice = 0; indice < modelDataset.numAttributes(); indice++) {
            listas[indice] = new Vector<ListaAtributos>();
        }

        // Se recorren todas las muestras de datos
        Enumeration datos = modelDataset.enumerateItemsets();
        Itemset dato;

        while (datos.hasMoreElements()) {
            // Agregar el valor de clase a la lista de clases
            dato = (Itemset) datos.nextElement();
            listaClases[n] = new ListaClases((int) dato.getClassValue(), root);

            // Agregar cada atributo a la lista correspondiente seg�n el atributo
            Enumeration atributos = modelDataset.enumerateAttributes();
            while (atributos.hasMoreElements()) {
                Attribute atributo = (Attribute) atributos.nextElement();
                // Se introduce el atributo con el �ndice que corresponde a la clase
                listas[atributo.getIndex()].add(new ListaAtributos(dato.getValue(atributo.getIndex()), n));
            }
            n++;
        }

        // Ahora hay que ordenar las listas de atributos
        for (int indice = 0; indice < modelDataset.numAttributes(); indice++) {
            // Los atributos discretos (categ�ricos) no hay que ordenarlos
            if (modelDataset.getAttribute(indice).isContinuous()) {
                Collections.sort(listas[indice], new ListaAtributos.Comparador());
            }
        }
    }

    /** M�todo que genera el �rbol seg�n el algoritmo SLIQ
     *
     */
    public void generateTree() {
        // Se crea el nodo ra�z para todas las clases
        root = new Node(listaClases.length);
        // Asoci�ndoles las listas de atributos con todos los valores
        root.setData(listas);

        // y se agregan todas las clases
        for (int indice = 0; indice < listaClases.length; indice++) {
            listaClases[indice].hoja = root;
            root.agregaElemento(listaClases[indice].clase);
        }

        // Se crea la cola de nodos para procesar el �rbol en anchura, no en profundidad
        listaNodos = new LinkedList<Node>();
        listaNodos.add(root); // y se agrega la ra�z

        // Mientras haya nodos a procesar en la cola
        while (!listaNodos.isEmpty()) {
            Node nodo = listaNodos.poll(); // Obtener el nodo a procesar

            // Si no es un nodo puro y a�n no ha sido dividido
            if (!nodo.esHoja() && nodo.numChildren() == 0) {
                // Recorrer todos los atributos
                for (int indice = 0; indice < nodo.getData().length; indice++) {
                    if (indice != modelDataset.getClassIndex()) {
                        // calculando el mejor corte posible
                        calculaMejorCorte(indice, nodo);
                    }
                }

                // Y a continuaci�n aplicar ese corte a los nodos a dividir
                for (int indice = 0; indice < listaClases.length; indice++) {
                    // que son aquellos no considerados hoja y a los que se ha agregado hijos
                    if (indice != modelDataset.getClassIndex() &&
                            !listaClases[indice].hoja.esHoja() &&
                            listaClases[indice].hoja.numChildren() != 0) {
                        aplicaMejorCorte(listaClases[indice].hoja);
                    }
                } // for
            } //else
        } // while

        // Realizar la poda
        podaArbol();

    } // generateTree

    /** M�todo que lleva a cabo la poda del �rbol tras la fase de crecimiento
     *
     */
    protected void podaArbol() {
        int Lt = 2;

        // Se inicia la primera fase de la poda (pasos 1 y 2)
        root.calculaCoste(1);

        // Partir desde los nodos hoja
        for (int indice = 0; indice < listaClases.length; indice++) {
            // Obtener la referencia al nodo padre
            Node padre = listaClases[indice].hoja.getParent();

            do {
                // y comprobar si el coste justifica la poda de ambos hijos
                if (padre.getCoste() < padre.getChildren(0).getCoste() + padre.getChildren(1).getCoste()) {
                    podaNodoCompleto(padre); // Aplicar la primera fase de poda

                    // Si se ha podado salir de este bucle y reiniciar el bucle exterior
                    indice = -1;
                    break;
                }
                padre = padre.getParent(); // Subir por el �rbol
            } while (padre != null); // Hasta alcanzar la ra�z
        }

        // Se inicia la segunda fase de la poda (pasos 2 a 4)

        Vector<Node> listaNodos = new Vector<Node>();
        for(int indice = 0; indice < listaClases.length; indice++)
            if(!listaNodos.contains(listaClases[indice].hoja.getParent()))
                listaNodos.add(listaClases[indice].hoja.getParent());

        // Recorrer los nodos hoja, en este caso no hay que subir por el �rbol
        //for (int indice = 0; indice < listaClases.length; indice++) {
        for(int indice = 0; indice < listaNodos.size(); indice++) {
            // Obtener la referencia al nodo padre, del que cuelga este nodo y otro
            Node padre = listaNodos.get(indice); // listaClases[indice].hoja.getParent();

            // Si ya se le ha podado una rama
         //   if ( padre == null ||
         //       padre.getChildren(0) == null || padre.getChildren(1) == null) {
         //       continue; // no procesarlo
         //   }

            padre.calculaCoste(2); // Calcular el coste adecuado para la segunda fase
            padre.getChildren(0).calculaCoste(2);
            padre.getChildren(1).calculaCoste(2);

            int costeAmbos = padre.getCoste(); // Coste de tener ambos hijos

            // Costes teniendo solamente uno, se resta el coste de tener un hijo
            // y se considera el error que se a�adir�a
            int costeIzq = costeAmbos - Lt + padre.costeError(padre.getChildren(0));
            int costeDch = costeAmbos - Lt + padre.costeError(padre.getChildren(1));

            if (costeIzq < costeAmbos) // Si el coste de tener solamente el izquierdo
            {
                podaNodoParcial(padre, 1); // es menor que ambos, se poda el derecho
            } else if (costeDch < costeAmbos) // y viceversa
            {
                podaNodoParcial(padre, 0);
            }
        }
    }

    /** M�todo encargado de podar el nodo que se recibe como par�metro, eliminando sus hijos
     *
     * @param padre Nodo del que se podar�n los dos hijos
     */
    protected void podaNodoCompleto(Node padre) {
        // Se podan los dos hijos
        podaNodoParcial(padre, 0);
        podaNodoParcial(padre, 1);

        // Marcar padre como nodo hoja, aunque tenga datos de m�s de una clase
        padre.setHoja(true);
    }

    /** M�todo encargado de podar un nodo hijo de un nodo padre
     *
     * @param padre     Nodo del que se va a podar
     * @param indHijo   �ndice del nodo hijo a podar: 0-izquierdo, 1-derecho
     */
    protected void podaNodoParcial(Node padre, int indHijo) {
        // Referencia al hijo que corresponda
        Node hijo = padre.getChildren(indHijo);

        // Hay que agregar al nodo padre los datos del hijo
        agregaDatos(padre, hijo);

        // Eliminar el nodo hijo
        padre.getChildren()[indHijo] = null;
    }

    /** M�todo que se encarga de agregar los datos de un hijo que va a
     *  podarse a la lista de datos de su padre
     *
     * @param padre Nodo al que se agregar�n los datos
     * @param hijo  Nodo del que se toman los datos
     */
    protected void agregaDatos(Node padre, Node hijo) {
        // Se recorre la lista de datos que contiene el hijo, agregando los
        // valores de sus atributos al padre
        for (int atributo = 0; atributo < hijo.getData().length; atributo++) {
            Vector<ListaAtributos> lista = hijo.getData()[atributo];
            for (int indice = 0; indice < lista.size(); indice++) {
                // Se contabiliza en el histograma
                padre.agregaElemento(listaClases[lista.get(indice).indice].clase);
                // y se agrega a la lista de atributos correspondiente
                padre.getData()[atributo].add(lista.get(indice));

                // Se actualiza la entrada en la lista de clases para que apunte al nodo padre
                listaClases[lista.get(indice).indice].hoja = padre;
            }
        }

        // Actualizar la clase principal en el padre, que puede ahora ser otra
        padre.actualizaClasePrincipal();
    }

    /** M�todo encargado de comprobar cu�l es el mejor corte para un cierto
     *  atributo en un nodo dado
     *
     * @param indAtributo   �ndice del atributo por el que se cortar�a
     * @param nodo          Nodo que se pretende dividir
     */
    protected void calculaMejorCorte(int indAtributo, Node nodo) {
        // Se recorre la lista ordenada de valores para el atributo �ndAtributo
        for (int indice = 0; indice < nodo.getData()[indAtributo].size() - 1; indice++) {
            // Se obtiene el nodo hoja que pertenece al valor examinado
            Node nodoHoja = listaClases[nodo.getData()[indAtributo].get(indice).indice].hoja;

            // Si es un nodo impuro que a�n no ha sido dividido
            if (!nodoHoja.esHoja()) {
                if (nodoHoja.numChildren() == 0) { // Si no tiene a�n hijos
                    nodoHoja.divide(); // dividirlo

                    // Y agregar a la lista de nodos pendientes de procesar
                    listaNodos.add(nodoHoja.getChildren(0));
                    listaNodos.add(nodoHoja.getChildren(1));
                }

                // Probar el corte por el atributo y valor indicados
                nodoHoja.pruebaCorte(indAtributo, listaClases,
                        nodo.getData()[indAtributo].get(indice).valor,
                        nodo.getData()[indAtributo].get(indice + 1).valor);
            }
        }
    }

    /** M�todo que aplica en un nodo el mejor corte obtenido previamente
     *
     * @param nodo  Nodo a dividir
     */
    protected void aplicaMejorCorte(Node nodo) {
        if (modelDataset.getAttribute(nodo.getDecompositionAttribute()).isDiscret()) 
            aplicaMejorCorteDiscreto(nodo);
        else                                                                                                                      aplicaMejorCorteContinuo(nodo);
    }

    /** M�todo que divide un nodo por un atributo discreto
     *
     * @param nodo  Nodo a dividir
     */
    protected void aplicaMejorCorteDiscreto(Node nodo) {
        // �ndice del atributo por el que se dividir�
        int indAtributo = nodo.getDecompositionAttribute();

        // Se toman las listas de atributos ordenadas del nodo a dividir
        Vector<ListaAtributos>[] listaI = nodo.getData().clone();
        Vector<ListaAtributos>[] listaD = nodo.getData().clone();

        // Y se generan nuevas listas inicialmente vac�as
        for (int indice = 0; indice < listaI.length; indice++) {
            listaI[indice] = new Vector<ListaAtributos>();
            listaD[indice] = new Vector<ListaAtributos>();
        }

        // Referencias a los nodos hijo entre los que se repartir�n los datos
        Node nodoI = nodo.getChildren(0), nodoD = nodo.getChildren(1);

        // Procesar la lista de valores correspondiente a cada atributo
        // y dividirla entre los dos nodos seg�n el criterio � subconjunto
        for (int atributo = 0; atributo < nodo.getData().length; atributo++) {
            // Lista de valores a dividir
            Vector<ListaAtributos> lista = nodo.getData()[atributo];
            for (int indice = 0; indice < lista.size(); indice++) {
                // �ndice del mejor subconjunto encontrado
                int indSubconjunto = (int) nodo.getDecompositionValue();

                // Obtener el subconjunto que corresponde a indSubconjunto
                while (indSubconjunto > 0) {
                    if (indSubconjunto % 2 != 0) {
                        // indSubconjunto es el �ndice del atributo que quedar�a a la izquierda
                        if (modelDataset.itemset(lista.get(indice).indice).getValue(indAtributo) == indSubconjunto) {
                            nodoI.agregaElemento(listaClases[lista.get(indice).indice].clase);
                            listaI[atributo].add(lista.get(indice));

                            // Se actualiza la entrada en la lista de clases para que apunte al nuevo nodo
                            listaClases[lista.get(indice).indice].hoja = nodoI;
                        }
                    } else {
                        if (modelDataset.itemset(lista.get(indice).indice).getValue(indAtributo) == indSubconjunto) {
                            // indSubconjunto es el �ndice del atributo que quedar�a a la derecha
                            nodoD.agregaElemento(listaClases[lista.get(indice).indice].clase);
                            listaD[atributo].add(lista.get(indice));
                            // Se actualiza la entrada en la lista de clases para que apunte al nuevo nodo
                            listaClases[lista.get(indice).indice].hoja = nodoD;
                        }
                    }
                    indSubconjunto /= 2; // Partir el conjunto en dos subconjuntos
                }
            }
        }

        // Facilitar a cada nodo su lista de atributos y valores
        nodoI.setData(listaI);
        nodoD.setData(listaD);
    }

    /** M�todo que divide un nodo por un atributo continuo
     *
     * @param nodo  Nodo a dividir
     */
    protected void aplicaMejorCorteContinuo(Node nodo) {

        // �ndice del atributo por el que se dividir�
        int indAtributo = nodo.getDecompositionAttribute();
        // Valor por el que se dividir�
        double valor = nodo.getDecompositionValue();

        // Se toman las listas de atributos ordenadas del nodo a dividir
        Vector<ListaAtributos>[] listaI = nodo.getData().clone();
        Vector<ListaAtributos>[] listaD = nodo.getData().clone();

        // Y se generan nuevas listas inicialmente vac�as
        for (int indice = 0; indice < listaI.length; indice++) {
            listaI[indice] = new Vector<ListaAtributos>();
            listaD[indice] = new Vector<ListaAtributos>();
        }

        // Referencias a los nodos hijo entre los que se repartir�n los datos
        Node nodoI = nodo.getChildren(0), nodoD = nodo.getChildren(1);

        // Procesar la lista de valores correspondiente a cada atributo
        // y dividirla entre los dos nodos seg�n el criterio <= valor
        for (int atributo = 0; atributo < nodo.getData().length; atributo++) {
            // Lista de valores a dividir
            Vector<ListaAtributos> lista = nodo.getData()[atributo];
            for (int indice = 0; indice < lista.size(); indice++) {
                if (modelDataset.itemset(lista.get(indice).indice).getValue(indAtributo) <= valor) {
                    nodoI.agregaElemento(listaClases[lista.get(indice).indice].clase);
                    listaI[atributo].add(lista.get(indice));

                    // Se actualiza la entrada en la lista de clases para que apunte al nuevo nodo
                    listaClases[lista.get(indice).indice].hoja = nodoI;
                } else {
                    nodoD.agregaElemento(listaClases[lista.get(indice).indice].clase);
                    listaD[atributo].add(lista.get(indice));

                    // Se actualiza la entrada en la lista de clases para que apunte al nuevo nodo
                    listaClases[lista.get(indice).indice].hoja = nodoD;
                }
            }
        }

        // Facilitar a cada nodo su lista de atributos y valores
        nodoI.setData(listaI);
        nodoD.setData(listaD);

    /**** Posiblemente nodo.data ya no sea necesario y pueda eliminarse,
     *  reduciendo la ocupaci�n en memoria
     */
    }

    /** M�todo que comprueba la clase a la que corresponder�a una muestra seg�n el �rbol generado
     *
     * @param itemset		La muestra a evaluar
     * @param node			El nodo que est� recorri�ndose.
     *
     * @return				El �ndice de la clase predicha.
     */
    public int evaluateItemset(Itemset itemset, Node node) {
        try {
            // Si el nodo es una hoja
            if (node.esHoja() ||
                    // o a pesar de no serlo tiene un solo hijo que corresponde a la condici�n a evaluar
                    itemset.getValue(node.getDecompositionAttribute()) <= node.getDecompositionValue() && node.getChildren(0) == null ||
                    itemset.getValue(node.getDecompositionAttribute()) > node.getDecompositionValue() && node.getChildren(1) == null) {
                return node.getClase(); // Se devuelve el �ndice de clase
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Evaluar los nodos hijo.
        if (itemset.getValue(node.getDecompositionAttribute()) <= node.getDecompositionValue()) {
            return (evaluateItemset(itemset, node.getChildren()[0]));
        } else {
            return (evaluateItemset(itemset, node.getChildren()[1]));
        }
    }

    /** Funci�n para contar el n�mero de nodos y de hojas en el �rbol
     *
     * @param node		El nodo actual.
     */
    public void cuentaNodosHojas(Node node) {

        NumberOfNodes++;    // Contabilizar el n�mero total de nodos

        // Descendiendo por el �rbol
        if (node.getChildren(0) != null) {
            cuentaNodosHojas(node.getChildren(0));
        }
        if (node.getChildren(1) != null) {
            cuentaNodosHojas(node.getChildren(1));
        }

        // Hasta alcanzar las hojas
        if (node.esHoja()) {
            NumberOfLeafs++;
        }
    }

    /** Escribe en el archivo los resultados de entrenamiento y pruebas.
     *
     * @exception 	Si no es posible escribir en el archivo.
     */
    public void printResult() throws IOException {
        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        long seconds = totalTime % 60;
        long minutes = ((totalTime - seconds) % 3600) / 60;
        String tree = "";
        PrintWriter resultPrint;

        cuentaNodosHojas(root);

        tree += "\n@TotalNumberOfNodes " + NumberOfNodes;
        tree += "\n@NumberOfLeafs " + NumberOfLeafs;

        tree += "\n\n@NumberOfItemsetsTraining " + trainDataset.numItemsets();
        tree += "\n@NumberOfCorrectlyClassifiedTraining " + correct;
        tree += "\n@PercentageOfCorrectlyClassifiedTraining " + (float) (correct * 100.0) / (float) trainDataset.numItemsets() + "%";
        tree += "\n@NumberOfInCorrectlyClassifiedTraining " + (trainDataset.numItemsets() - correct);
        tree += "\n@PercentageOfInCorrectlyClassifiedTraining " + (float) ((trainDataset.numItemsets() - correct) * 100.0) / (float) trainDataset.numItemsets() + "%";

        tree += "\n\n@NumberOfItemsetsTest " + testDataset.numItemsets();
        tree += "\n@NumberOfCorrectlyClassifiedTest " + testCorrect;
        tree += "\n@PercentageOfCorrectlyClassifiedTest " + (float) (testCorrect * 100.0) / (float) testDataset.numItemsets() + "%";
        tree += "\n@NumberOfInCorrectlyClassifiedTest " + (testDataset.numItemsets() - testCorrect);
        tree += "\n@PercentageOfInCorrectlyClassifiedTest " + (float) ((testDataset.numItemsets() - testCorrect) * 100.0) / (float) testDataset.numItemsets() + "%";

        tree += "\n\n@ElapsedTime " + (totalTime - minutes * 60 - seconds) / 3600 + ":" + minutes / 60 + ":" + seconds;

        resultPrint = new PrintWriter(new FileWriter(resultFileName));
        resultPrint.print(getHeader() + "\n@decisiontree\n\n" + tree);
        resultPrint.close();
    }

    /** Eval�a el dataset de entrenamiento y escribe el resultado en un archivo.
     *
     */
    public void printTrain() {
        String text = getHeader();
        for (int i = 0; i < trainDataset.numItemsets(); i++) {
            try {
                Itemset itemset = trainDataset.itemset(i);
                int cl = evaluateItemset(itemset, root);

                if (cl == (int) itemset.getValue(trainDataset.getClassIndex())) {
                    correct++;
                }

                text += trainDataset.getClassAttribute().value(cl) + " " +
                        trainDataset.getClassAttribute().value(((int) itemset.getClassValue())) + "\n";
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
        }

        try {
            PrintWriter print = new PrintWriter(new FileWriter(trainOutputFileName));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("No es posible abrir el archivo de salida de entrenamiento: " + e.getMessage());
        }
    }

    /** Eval�a el dataset de pruebas y escribe el resultado en un archivo
     *
     */
    public void printTest() {
        String text = getHeader();

        for (int i = 0; i < testDataset.numItemsets(); i++) {
            try {
                int cl = (int) evaluateItemset(testDataset.itemset(i), root);
                Itemset itemset = testDataset.itemset(i);

                if (cl == (int) itemset.getValue(testDataset.getClassIndex())) {
                    testCorrect++;
                }

                text += testDataset.getClassAttribute().value(((int) itemset.getClassValue())) + " " +
                        testDataset.getClassAttribute().value(cl) + "\n";
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            PrintWriter print = new PrintWriter(new FileWriter(testOutputFileName));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("No es posible abrir el archivo de salida de pruebas.");
        }
    }

    /** M�todo encargado de leer las opciones de ejecuci�n del archivo y establecer los par�metros adecuados.
     *
     * @param options 		El StreamTokenizer que lee del archivo de par�metros.
     *
     * @throws Exception	En caso de que el formato del archivo no sea el correcto.
     */
    protected void setOptions(StreamTokenizer options) throws Exception {

        options.nextToken();

        // Comprobar que el archivo comienza con el token 'algorithm'
        if (options.sval.equalsIgnoreCase("algorithm")) {
            options.nextToken(); // Saltar el s?mbolo =
            options.nextToken(); // y tomar el nombre del algoritmo

            // que debe ser 'SLIQ'
            if (!options.sval.equalsIgnoreCase("SLIQ")) {
                throw new Exception("El nombre del algoritmo no es correcto.");
            }

            options.nextToken();
            options.nextToken();

            // Recuperar los nombres de los archivos de entrada
            if (options.sval.equalsIgnoreCase("inputData")) {
                options.nextToken();
                options.nextToken();
                modelFileName = options.sval;

                if (options.nextToken() != StreamTokenizer.TT_EOL) {
                    trainFileName = options.sval;
                    options.nextToken();
                    testFileName = options.sval;
                    if (options.nextToken() != StreamTokenizer.TT_EOL) {
                        trainFileName = modelFileName;
                        options.nextToken();
                    }
                }
            } else {
                throw new Exception("El archivo debe comenzar con la palabra 'inputData'.");
            }

            // Avanzar en el archivo hasta la marca 'outputData'
            while (true) {
                if (options.nextToken() == StreamTokenizer.TT_EOF) {
                    throw new Exception("No se han indicado archivos de salida.");
                }

                if (options.sval == null) {
                    continue;
                } else if (options.sval.equalsIgnoreCase("outputData")) {
                    break;
                }
            }

            /* Recuperar los nombres de los archivos de salida */

            options.nextToken();
            options.nextToken();
            trainOutputFileName = options.sval;
            options.nextToken();

            testOutputFileName = options.sval;
            options.nextToken();

            resultFileName = options.sval;
        } else {
            throw new Exception("El archivo debe comenzar con la palabra 'algorithm' seguida del nombre del algoritmo.");
        }

    } // setOptions

    /** Funci�n main.
     *
     * @param args 			El archivo de par�metros.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("\nError: debe especificar el archivo de par�metros\n\tuso: java -jar SLIQ.jar archivoparametros.txt");
            System.exit(-1);
        } else {
            new SLIQ(args[0]);
        }
    }
} // sliq

