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

package keel.Algorithms.Neural_Networks.IRPropPlus_Regr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import keel.Algorithms.Neural_Networks.IRPropPlus_Clas.IOptimizableFunc;
import keel.Algorithms.Neural_Networks.IRPropPlus_Clas.IRPropPlus;
import keel.Algorithms.Neural_Networks.NNEP_Common.NeuralNetCreator;
import keel.Algorithms.Neural_Networks.NNEP_Common.NeuralNetIndividualSpecies;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.AttributeType;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.CategoricalAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DatasetException;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IMetadata;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IntegerNumericalAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.KeelDataSet;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.RealNumericalAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.INeuralNet;
import keel.Algorithms.Neural_Networks.NNEP_Common.problem.ProblemEvaluator;
import keel.Algorithms.Neural_Networks.NNEP_Common.util.random.RanNnepFactory;
import keel.Algorithms.Neural_Networks.NNEP_Regr.problem.regression.RegressionProblemEvaluator;
import net.sf.jclec.IConfigure;
import net.sf.jclec.IEvaluator;
import net.sf.jclec.IPopulation;
import net.sf.jclec.IProvider;
import net.sf.jclec.ISpecies;
import net.sf.jclec.base.AbstractIndividual;
import net.sf.jclec.util.intset.Interval;
import net.sf.jclec.util.random.IRandGen;
import net.sf.jclec.util.random.IRandGenFactory;

import org.apache.commons.configuration.XMLConfiguration;


/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 3/12/2007
 * @author Modified by Juan Carlos Fernandez Caballero (University of Cordoba) 3/12/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class KEELIRPropPlusWrapperRegr implements IPopulation<AbstractIndividual<INeuralNet>>
{
	
	/**
	 * <p>	 
	 * Wrapper of iRProp+ algorithm for KEEL
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Generated by Eclipse */	

	private static final long serialVersionUID = 3487472700821000039L;
	

	/////////////////////////////////////////////////////////////////
	// ------------------------------------- Configuration properties
	/////////////////////////////////////////////////////////////////
	
	/** Random generators factory */
	
	protected static IRandGenFactory randGenFactory = null;
	
	/** Individual species */
	
	protected static ISpecies<AbstractIndividual<INeuralNet>> species = null;
	
	/** Individuals evaluator */
	
	protected static IEvaluator<AbstractIndividual<INeuralNet>> evaluator = null;
	
	/** Individuals provider */
	
	protected static IProvider<AbstractIndividual<INeuralNet>> provider = null;

	/** Wrapped algorithm */
	protected static IRPropPlus algorithm = null;

	/** Console reporter */
	protected static IRPropPlusReporterRegr consoleReporter = new IRPropPlusReporterRegr();
	
	/////////////////////////////////////////////////////////////////
	// ------------------------- Implementing ISystem and IPopulation
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Factory method.
	 * 
	 * @return A new instance of a random generator
	 * </p>
	 */
	
	public IRandGen createRandGen() {
		return randGenFactory.createRandGen();
	}
	
	/**
	 * <p>
	 * Access to system evaluator.
	 * 
	 * @return System evaluator
	 * </p>
	 */
	
	public IEvaluator<AbstractIndividual<INeuralNet>> getEvaluator() {
		return evaluator;
	}
	
	/**
	 * <p>
	 * Access to current generation.
	 *  
	 * @return Current generation
	 * </p>
	 */
	
	public int getGeneration() {
		return 0;
	}
	
	/**
	 * <p>
	 * Access to population inhabitants.
	 * 
	 * @return Population inhabitants
	 * </p>
	 */
	
	public List<AbstractIndividual<INeuralNet>> getInhabitants() {
		return null;
	}
	
	/**
	 * <p>
	 * Access to system species.
	 * 
	 * @return System species
	 * </p>
	 */
	
	public ISpecies<AbstractIndividual<INeuralNet>> getSpecies() {
		return species;
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Main method
	 * </p>
	 */
	
	public static void main(String[] args) {
		configureJob(args[0]);
		executeJob();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Configure the execution of the algorithm.
	 * 
	 * @param jobFilename Name of the KEEL file with properties of the
	 *                    execution
	 * </p>                   
	 */

	@SuppressWarnings("unchecked")
	private static void configureJob(String jobFilename) {

		Properties props = new Properties();

		try {
			InputStream paramsFile = new FileInputStream(jobFilename);
			props.load(paramsFile);
			paramsFile.close();			
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
		
		// Files training and test
		String trainFile;
		String testFile;
		StringTokenizer tokenizer = new StringTokenizer(props.getProperty("inputData"));
		tokenizer.nextToken();
		trainFile = tokenizer.nextToken();
		trainFile = trainFile.substring(1, trainFile.length()-1);
		testFile = tokenizer.nextToken();
		testFile = testFile.substring(1, testFile.length()-1);
		
		// Configure schema
		byte[] schema = null;
		try {	
			schema = readSchema(trainFile);			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DatasetException e) {
			e.printStackTrace();
		}

		// Auxiliar configuration file
		XMLConfiguration conf = new XMLConfiguration();
		conf.setRootElementName("algorithm");
		
		// Configure randGenFactory
		randGenFactory = new RanNnepFactory();
		conf.addProperty("rand-gen-factory[@seed]", Integer.parseInt(props.getProperty("seed")));
		if(randGenFactory instanceof IConfigure)
			((IConfigure)randGenFactory).configure(conf.subset("rand-gen-factory"));

		// Configure species
		NeuralNetIndividualSpecies nnspecies = new NeuralNetIndividualSpecies();
		species = (ISpecies) nnspecies;
		if  (props.getProperty("Transfer").equals("Product_Unit")) {
			conf.addProperty("species.neural-net-type", "keel.Algorithms.Neural_Networks.IRPropPlus_Regr.MSEOptimizablePUNeuralNetRegressor");
			conf.addProperty("species.hidden-layer[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ExpLayer" );
			conf.addProperty("species.hidden-layer[@biased]", false );
		} else {
			conf.addProperty("species.neural-net-type", "keel.Algorithms.Neural_Networks.IRPropPlus_Regr.MSEOptimizableSigmNeuralNetRegressor");
			conf.addProperty("species.hidden-layer[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.SigmLayer" );
			conf.addProperty("species.hidden-layer[@biased]", true );
		}
		int neurons = Integer.parseInt(props.getProperty("Hidden_nodes"));
		conf.addProperty("species.hidden-layer.minimum-number-of-neurons", neurons);
		conf.addProperty("species.hidden-layer.initial-maximum-number-of-neurons", neurons);
		conf.addProperty("species.hidden-layer.maximum-number-of-neurons", neurons);
		conf.addProperty("species.hidden-layer.initiator-of-links", "keel.Algorithms.Neural_Networks.IRPropPlus_Clas.FullRandomInitiator");
		conf.addProperty("species.hidden-layer.weight-range[@type]", "net.sf.jclec.util.range.Interval" );
		conf.addProperty("species.hidden-layer.weight-range[@closure]", "closed-closed" );
		if  (props.getProperty("Transfer").equals("Product_Unit")){
			conf.addProperty("species.hidden-layer.weight-range[@left]", -0.1 );
			conf.addProperty("species.hidden-layer.weight-range[@right]", 0.1 );
		} else {
			conf.addProperty("species.hidden-layer.weight-range[@left]", -5.0 );
			conf.addProperty("species.hidden-layer.weight-range[@right]", 5.0 );			
		}
		conf.addProperty("species.output-layer[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinearLayer" );
		conf.addProperty("species.output-layer[@biased]", true );
		conf.addProperty("species.output-layer.initiator-of-links", "keel.Algorithms.Neural_Networks.IRPropPlus_Clas.FullRandomInitiator");
		conf.addProperty("species.output-layer.weight-range[@type]", "net.sf.jclec.util.range.Interval" );
		conf.addProperty("species.output-layer.weight-range[@closure]", "closed-closed" );
		conf.addProperty("species.output-layer.weight-range[@left]", -5.0 );
		conf.addProperty("species.output-layer.weight-range[@right]", 5.0 );	
		if(species instanceof IConfigure)
			((IConfigure)species).configure(conf.subset("species"));
		
		// Configure evaluator
		evaluator = (IEvaluator) new RegressionProblemEvaluator();
		if (props.getProperty("Transfer").equals("Product_Unit"))
			conf.addProperty("evaluator[@log-input-data]", true );	
		conf.addProperty("evaluator[@normalize-data]", true);
		conf.addProperty("evaluator.error-function", "keel.Algorithms.Neural_Networks.NNEP_Regr.problem.errorfunctions.MSEErrorFunction");
		conf.addProperty("evaluator.input-interval[@closure]", "closed-closed");
		conf.addProperty("evaluator.input-interval[@left]", 0.1);
		conf.addProperty("evaluator.input-interval[@right]", 0.9);
		conf.addProperty("evaluator.output-interval[@closure]", "closed-closed");
		conf.addProperty("evaluator.output-interval[@left]", 1.0);
		conf.addProperty("evaluator.output-interval[@right]", 2.0);
		if(evaluator instanceof IConfigure)
			((IConfigure)evaluator).configure(conf.subset("evaluator"));
		
		// Configure provider
		provider = new NeuralNetCreator();
		KEELIRPropPlusWrapperRegr system = new KEELIRPropPlusWrapperRegr();
		provider.contextualize(system);
		
		// Configure iRProp+ algorithm
		algorithm = new IRPropPlus();
		conf.addProperty("algorithm.initial-step-size[@value]", 0.0125);
		conf.addProperty("algorithm.minimum-delta[@value]", 0.0);		
		conf.addProperty("algorithm.maximum-delta[@value]", 50.0);		
		conf.addProperty("algorithm.positive-eta[@value]", 1.2);		
		conf.addProperty("algorithm.negative-eta[@value]", 0.2);		
		conf.addProperty("algorithm.cycles[@value]", Integer.parseInt(props.getProperty("Epochs")));
		if(algorithm instanceof IConfigure)
			((IConfigure)algorithm).configure(conf.subset("algorithm"));
	
		// Read data
		ProblemEvaluator<AbstractIndividual<INeuralNet>> evaluator2 = (ProblemEvaluator<AbstractIndividual<INeuralNet>>) evaluator;
		evaluator2.readData(schema, new KeelDataSet(trainFile), new KeelDataSet(testFile));
		nnspecies.setNOfInputs(evaluator2.getTrainData().getNofinputs());
		nnspecies.setNOfOutputs(evaluator2.getTrainData().getNofoutputs());
		algorithm.setTrainingData(evaluator2.getTrainData());
			
		// Read output files
		tokenizer = new StringTokenizer(props.getProperty("outputData"));
		String trainResultFile = tokenizer.nextToken();
		trainResultFile = trainResultFile.substring(1, trainResultFile.length()-1);
		consoleReporter.setTrainResultFile(trainResultFile);
		String testResultFile = tokenizer.nextToken();
		testResultFile = testResultFile.substring(1, testResultFile.length()-1);
		consoleReporter.setTestResultFile(testResultFile);
		String bestModelResultFile = tokenizer.nextToken();
		bestModelResultFile = bestModelResultFile.substring(1, bestModelResultFile.length()-1);
		consoleReporter.setBestModelResultFile(bestModelResultFile);
	}

	/**
	 * <p>
	 * Executes the algorithm
	 * </p>
	 */

	private static void executeJob() {
		// Provide a random individual
		List<AbstractIndividual<INeuralNet>> nnind = provider.provide(1);
		
		// Evaluate it
		System.out.println("\n\nGenerated Individual\n--------------------\n" + 
				consoleReporter.renderNeuralNetIndividual(nnind.get(0),evaluator));

		// Exponents weights have a reduced step size
		if(nnind.get(0).getGenotype() instanceof MSEOptimizablePUNeuralNetRegressor)
			algorithm.setReducedStepSize(obtainReducedStepSize((MSEOptimizablePUNeuralNetRegressor) nnind.get(0).getGenotype()));
		else
			algorithm.setReducedStepSize(null);
		
		// Apply iRPropAlgorithm
		nnind.get(0).setGenotype((INeuralNet) algorithm.optimize((IOptimizableFunc) nnind.get(0).getGenotype()));
		
		// Evaluate it
		System.out.println("Optimized Individual\n--------------------\n" + 
				consoleReporter.renderNeuralNetIndividual(nnind.get(0),evaluator));
		
		// Print Results
		consoleReporter.algorithmFinished(nnind.get(0), (ProblemEvaluator<AbstractIndividual<INeuralNet>>) evaluator);
	}

	/**
	 * <p>
	 * Reads schema from the KEEL file
	 * 
	 * @param jobFilename Name of the KEEL dataset file
	 * </p>
	 */
	
	private static byte[] readSchema(String fileName) throws IOException, DatasetException{

		KeelDataSet dataset = new KeelDataSet(fileName);
		dataset.open();		

		File file = new File(fileName);

		List<String> inputIds = new ArrayList<String>();
		List<String> outputIds = new ArrayList<String>();

		Reader reader = new BufferedReader(new FileReader(file));			
		String line = ((BufferedReader) reader).readLine();
		StringTokenizer elementLine = new StringTokenizer(line);
		String element = elementLine.nextToken();

		while (!element.equalsIgnoreCase("@data")){

			if(element.equalsIgnoreCase("@inputs")){
				while(elementLine.hasMoreTokens()){
					StringTokenizer commaTokenizer = new StringTokenizer(elementLine.nextToken(),",");
					while(commaTokenizer.hasMoreTokens())
						inputIds.add(commaTokenizer.nextToken());
				}
			}
			else if(element.equalsIgnoreCase("@outputs")){					
				while(elementLine.hasMoreTokens()){
					StringTokenizer commaTokenizer = new StringTokenizer(elementLine.nextToken(),",");
					while(commaTokenizer.hasMoreTokens())
						outputIds.add(commaTokenizer.nextToken());	
				}
			}

			// Next line of the file
			line = ((BufferedReader) reader).readLine();
			while(line.startsWith("%") || line.equalsIgnoreCase(""))
				line = ((BufferedReader) reader).readLine();

			elementLine = new StringTokenizer(line);
			element = elementLine.nextToken();
		}

		IMetadata metadata = dataset.getMetadata();
		byte[] schema = new byte[metadata.numberOfAttributes()];

		if(inputIds.isEmpty() || outputIds.isEmpty()){
			for(int i=0; i<schema.length; i++){
				if(i!=(schema.length-1))
					schema[i] = 1;
				else{
					IAttribute outputAttribute = metadata.getAttribute(i);
					schema[i] = 2;
					consoleReporter.setOutputAttribute(outputAttribute);
				}
			}
		}
		else{
			for(int i=0; i<schema.length; i++){
				if(inputIds.contains(metadata.getAttribute(i).getName()))
					schema[i] = 1;
				else if(outputIds.contains(metadata.getAttribute(i).getName())){
					IAttribute outputAttribute = metadata.getAttribute(i);
					schema[i] = 2;
					consoleReporter.setOutputAttribute(outputAttribute);
				}
				else
					schema[i] = -1;
			}
		}
		
		StringBuffer header = new StringBuffer();
		header.append("@relation " + dataset.getName() + "\n");
		for(int i=0; i<metadata.numberOfAttributes(); i++){
			IAttribute attribute = metadata.getAttribute(i);
			header.append("@attribute " + attribute.getName() +" ");
			if(attribute.getType() == AttributeType.Categorical ){
				CategoricalAttribute catAtt = (CategoricalAttribute) attribute;
				
				Interval interval = catAtt.intervalValues();
				
				header.append("{");
				for(int j=(int)interval.getLeft(); j<=interval.size()+1; j++){
					header.append( catAtt.show(j)+ (j!=interval.size()+1?",":"}\n"));
				}
			}
			else if(attribute.getType() == AttributeType.IntegerNumerical ){
				IntegerNumericalAttribute intAtt = (IntegerNumericalAttribute) attribute;
				header.append("integer[" + (int) intAtt.intervalValues().getLeft() + "," + (int) intAtt.intervalValues().getRight() +"]\n");
			}
			else if(attribute.getType() == AttributeType.DoubleNumerical ){
				RealNumericalAttribute doubleAtt = (RealNumericalAttribute) attribute;
				header.append("real[" + doubleAtt.intervalValues().getLeft() + "," + doubleAtt.intervalValues().getRight() +"]\n");
			}
		}
		header.append("@data\n");
		consoleReporter.setHeader(header.toString());		
		
		dataset.close();
		return schema;
	}
	
	/**
	 * <p>
	 * Mark exponents of a MSEOptimizablePUNeuralNetClassifier as reduced
	 * 
	 * @param neuralNet MSEOptimizablePUNeuralNetClassifier to analyze
	 * @return boolean[] Reduced Step Size Array
	 * </p>
	 */
	
	private static boolean[] obtainReducedStepSize(MSEOptimizablePUNeuralNetRegressor neuralNet){

		int inputs = neuralNet.getInputLayer().getMaxnofneurons();
		int outputs = neuralNet.getOutputLayer().getMaxnofneurons();
		int hiddenNeurons = neuralNet.getNofhneurons();
		boolean[] reduced = new boolean[inputs*hiddenNeurons + outputs*(hiddenNeurons+1)];
		
		// For each hidden neuron
		for(int i=0; i<hiddenNeurons; i++){
			// Base index
			int baseIndex = outputs+i*(inputs+outputs);
			
			for(int j=0; j<inputs; j++)
				reduced[baseIndex+j] = true;				
	
			// Base index
			baseIndex += inputs;
			
			for(int j=0; j<outputs; j++)
				reduced[baseIndex+j] = false;
		}
		for(int j=0; j<outputs; j++)
			reduced[j] = false;
		
		return reduced;
		
	}

}

