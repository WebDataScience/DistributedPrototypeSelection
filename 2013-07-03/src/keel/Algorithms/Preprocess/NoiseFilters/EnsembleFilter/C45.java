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

/**
 * <p>
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.EnsembleFilter;

import java.io.*;
import keel.Algorithms.Decision_Trees.C45.Algorithm;
import keel.Algorithms.Decision_Trees.C45.Dataset;
import keel.Algorithms.Decision_Trees.C45.Itemset;
import keel.Algorithms.Decision_Trees.C45.SelectCut;
import keel.Algorithms.Decision_Trees.C45.Tree;
import keel.Dataset.Attributes;


/**
 * Class to implement the C4.5 algorithm
 */
public class C45 extends Algorithm {
    /** Decision tree. */
    private Tree root;

    /** Is the tree pruned or not. */
    private boolean prune = true;

    /** Confidence level. */
    private float confidence = 0.25f;

    /** Minimum number of itemsets per leaf. */
    private int minItemsets = 2;

    /** The prior probabilities of the classes. */
    private double[] priorsProbabilities;

    /** Resolution of the margin histogram. */
    private static int marginResolution = 500;

    /** Cumulative margin classification. */
    private double marginCounts[];

    /** The sum of counts for priors. */
    private double classPriorsSum;

    /** Constructor.
     *
     * @throws Exception	If the algorithm cannot be executed.
     */
    public C45(String trainfn, String testfn) throws Exception {
        try {

            // starts the time
            long startTime = System.currentTimeMillis();

            /* Sets the options of the execution from text file*/
            setOptions(trainfn, testfn);
            
            /* Initializes the dataset. */
        	Attributes.clearAll();
            modelDataset = new Dataset(modelFileName, true);
            trainDataset = new Dataset(trainFileName, false);
            testDataset = new Dataset(testFileName, false);

            priorsProbabilities = new double[modelDataset.numClasses()];
            priorsProbabilities();
            marginCounts = new double[marginResolution + 1];

            // generate the tree
            generateTree(modelDataset);

            //printTrain();
            //printTest();
            //printResult();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit( -1);
        }

    }

    public int[] getPredictions(){
    	int[] classesp = new int[testDataset.numItemsets()];
    	 for(int i = 0 ; i < testDataset.numItemsets() ; ++i)
			try {
				classesp[i] = (int) evaluateItemset(testDataset.itemset(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		return classesp;
    }

    protected void setOptions(StreamTokenizer option) throws Exception {}


    /** Function to read the options from the execution file and assign the values to the parameters.
     *
     * @param options 		The StreamTokenizer that reads the parameters file.
     *
     * @throws Exception	If the format of the file is not correct.
     */
    protected void setOptions(String trainfn, String testfn) throws Exception {
    	
        modelFileName = trainfn;
        trainFileName = trainfn;
        testFileName = testfn;
        
        trainOutputFileName = "salidac45train.dat";
        testOutputFileName = "salidac45test.dat";
        resultFileName = "salidac45result.dat";

        prune = Parameters.prune;

        /* Checks that the confidence threshold is between 0 and 1. */
        confidence = (float)Parameters.confidence; // debe estar entre 0 y 1

        minItemsets = Parameters.itemsetsPerLeaf; // debe ser > 0
    }


    /** Generates the tree.
     *
     * @param itemsets		The dataset used to build the tree.
     *
     * @throws Exception	If the tree cannot be built.
     */
    public void generateTree(Dataset itemsets) throws Exception {
        SelectCut selectCut;

        selectCut = new SelectCut(minItemsets, itemsets);
        root = new Tree(selectCut, prune, confidence);
        root.buildTree(itemsets);
    }

    /** Function to evaluate the class which the itemset must have according to the classification of the tree.
     *
     * @param itemset		The itemset to evaluate.
     *
     * @return				The index of the class index predicted.
     */
    public double evaluateItemset(Itemset itemset) throws Exception {
        Itemset classMissing = (Itemset) itemset.copy();
        double prediction = 0;
        classMissing.setDataset(itemset.getDataset());
        classMissing.setClassMissing();

        double[] classification = classificationForItemset(classMissing);
        prediction = maxIndex(classification);
        updateStats(classification, itemset, itemset.numClasses());

        //itemset.setPredictedValue( prediction );

        return prediction;
    }

    /** Updates all the statistics for the current itemset.
     *
     * @param predictedClassification	Distribution of class values predicted for the itemset.
     * @param itemset					The itemset.
     * @param nClasses					The number of classes.
     *
     */
    private void updateStats(double[] predictedClassification, Itemset itemset,
                             int nClasses) {
        int actualClass = (int) itemset.getClassValue();

        if (!itemset.classIsMissing()) {
            updateMargins(predictedClassification, actualClass, nClasses);

            // Determine the predicted class (doesn't detect multiple classifications)
            int predictedClass = -1;
            double bestProb = 0.0;

            for (int i = 0; i < nClasses; i++) {
                if (predictedClassification[i] > bestProb) {
                    predictedClass = i;
                    bestProb = predictedClassification[i];
                }
            }

            // Update counts when no class was predicted
            if (predictedClass < 0) {
                return;
            }

            double predictedProb = Math.max(Double.MIN_VALUE,
                                            predictedClassification[actualClass]);
            double priorProb = Math.max(Double.MIN_VALUE,
                                        priorsProbabilities[actualClass] /
                                        classPriorsSum);
        }
    }

    /** Returns class probabilities for an itemset.
     *
     * @param itemset		The itemset.
     *
     * @throws Exception	If cannot compute the classification.
     */
    public final double[] classificationForItemset(Itemset itemset) throws
            Exception {
        return root.classificationForItemset(itemset);
    }

    /** Update the cumulative record of classification margins.
     *
     * @param predictedClassification	Distribution of class values predicted for the itemset.
     * @param actualClass				The class value.
     * @param nClasses					Number of classes.
     */
    private void updateMargins(double[] predictedClassification,
                               int actualClass, int nClasses) {
        double probActual = predictedClassification[actualClass];
        double probNext = 0;

        for (int i = 0; i < nClasses; i++) {
            if ((i != actualClass) && ( //Comparators.isGreater( predictedClassification[i], probNext ) ) )
                    predictedClassification[i] > probNext)) {
                probNext = predictedClassification[i];
            }
        }

        double margin = probActual - probNext;
        int bin = (int) ((margin + 1.0) / 2.0 * marginResolution);
        marginCounts[bin]++;
    }

    /** Evaluates if a string is a boolean value.
     *
     * @param value		The string to evaluate.
     *
     * @return			True if value is a boolean value. False otherwise.
     */
    private boolean isBoolean(String value) {
        if (value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("FALSE")) {
            return true;
        } else {
            return false;
        }
    }

    /** Returns index of maximum element in a given array of doubles. First maximum is returned.
     *
     * @param doubles		The array of elements.
     *
     */
    public static int maxIndex(double[] doubles) {
        double maximum = 0;
        int maxIndex = 0;

        for (int i = 0; i < doubles.length; i++) {
            if ((i == 0) || //
                doubles[i] > maximum) {
                maxIndex = i;
                maximum = doubles[i];
            }
        }

        return maxIndex;
    }

    /** Sets the class prior probabilities.
     *
     * @throws Exception	If cannot compute the probabilities.
     */
    public void priorsProbabilities() throws Exception {
        for (int i = 0; i < modelDataset.numClasses(); i++) {
            priorsProbabilities[i] = 1;
        }

        classPriorsSum = modelDataset.numClasses();

        for (int i = 0; i < modelDataset.numItemsets(); i++) {
            if (!modelDataset.itemset(i).classIsMissing()) {
                try {
                    priorsProbabilities[(int) modelDataset.itemset(i).
                            getClassValue()] += modelDataset.itemset(i).
                            getWeight();
                    classPriorsSum += modelDataset.itemset(i).getWeight();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /** Writes the tree and the results of the training and the test in the file.
     *
     * @exception 	If the file cannot be written.
     */
    public void printResult() throws IOException {
        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        long seconds = totalTime % 60;
        long minutes = ((totalTime - seconds) % 3600) / 60;
        String tree = "";
        PrintWriter resultPrint;

        tree += toString();
        tree += "\n@TotalNumberOfNodes " + root.NumberOfNodes;
        tree += "\n@NumberOfLeafs " + root.NumberOfLeafs;

        tree += "\n\n@NumberOfItemsetsTraining " + trainDataset.numItemsets();
        tree += "\n@NumberOfCorrectlyClassifiedTraining " + correct;
        tree += "\n@PercentageOfCorrectlyClassifiedTraining " +
                (float) (correct * 100.0) / (float) trainDataset.numItemsets() +
                "%";
        tree += "\n@NumberOfInCorrectlyClassifiedTraining " +
                (trainDataset.numItemsets() - correct);
        tree += "\n@PercentageOfInCorrectlyClassifiedTraining " +
                (float) ((trainDataset.numItemsets() - correct) * 100.0) /
                (float) trainDataset.numItemsets() + "%";

        tree += "\n\n@NumberOfItemsetsTest " + testDataset.numItemsets();
        tree += "\n@NumberOfCorrectlyClassifiedTest " + testCorrect;
        tree += "\n@PercentageOfCorrectlyClassifiedTest " +
                (float) (testCorrect * 100.0) / (float) testDataset.numItemsets() +
                "%";
        tree += "\n@NumberOfInCorrectlyClassifiedTest " +
                (testDataset.numItemsets() - testCorrect);
        tree += "\n@PercentageOfInCorrectlyClassifiedTest " +
                (float) ((testDataset.numItemsets() - testCorrect) * 100.0) /
                (float) testDataset.numItemsets() + "%";

        tree += "\n\n@ElapsedTime " +
                (totalTime - minutes * 60 - seconds) / 3600 + ":" +
                minutes / 60 + ":" + seconds;

        resultPrint = new PrintWriter(new FileWriter(resultFileName));
        resultPrint.print(getHeader() + "\n@decisiontree\n\n" + tree);
        resultPrint.close();
    }

    /** Evaluates the training dataset and writes the results in the file.
     *
     * @exception 	If the file cannot be written.
     */
    public void printTrain() {
        String text = getHeader();

        for (int i = 0; i < trainDataset.numItemsets(); i++) {
            try {
                Itemset itemset = trainDataset.itemset(i);
                int cl = (int) evaluateItemset(itemset);

                if (cl == (int) itemset.getValue(trainDataset.getClassIndex())) {
                    correct++;
                }

                text += trainDataset.getClassAttribute().value(((int) itemset.
                        getClassValue())) + " " + trainDataset.getClassAttribute().value(cl)
                         + "\n";
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            PrintWriter print = new PrintWriter(new FileWriter(
                    trainOutputFileName));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("Can not open the training output file: " +
                               e.getMessage());
        }
    }

    /** Evaluates the test dataset and writes the results in the file.
     *
     * @exception 	If the file cannot be written.
     */
    public void printTest() {
        String text = getHeader();

        for (int i = 0; i < testDataset.numItemsets(); i++) {
            try {
                int cl = (int) evaluateItemset(testDataset.itemset(i));
                Itemset itemset = testDataset.itemset(i);

                if (cl == (int) itemset.getValue(testDataset.getClassIndex())) {
                    testCorrect++;
                }

                text += testDataset.getClassAttribute().value(((int) itemset.
                        getClassValue())) + " " + testDataset.getClassAttribute().value(cl) + "\n";
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            PrintWriter print = new PrintWriter(new FileWriter(
                    testOutputFileName));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("Can not open the training output file.");
        }
    }


    /** Function to print the tree.
     *
     */
    public String toString() {
        return root.toString();
    }


}


