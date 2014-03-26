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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Alcalaetal;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.1
 * @since JDK1.6
 * </p>
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.core.Randomize;
import keel.Dataset.Attributes;

public class Alcalaetal {
	/**
	 * <p>
	 * It gathers all the parameters, launches the algorithm, and prints out the results
	 * </p>
	 */

    private myDataset trans;
    
    private String rulesFilename;
    private String valuesFilename;
    private String uniformFuzzyAttributesFilename;
    private String adjustedFuzzyAttributesFilename;
    private String geneticLearningLogFilename;
    private AlcalaetalProcess proc;
    private ArrayList<AssociationRule> associationRulesSet;
	

    private int nEvaluations;
    private int popSize;
    private int nBitsGene;
    private double phi;
    private double d;
    private int nFuzzyRegionsForNumericAttributes;
    private boolean useMaxForOneFrequentItemsets;
    private double minSupport;
    private double minConfidence;
    
    
    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public Alcalaetal() {
    }

    /**
     * It reads the data from the input files and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public Alcalaetal(parseParameters parameters) {
    	
        this.rulesFilename = parameters.getAssociationRulesFile();
        this.adjustedFuzzyAttributesFilename = parameters.getOutputFile(0);
        this.valuesFilename = parameters.getOutputFile(1);
        this.uniformFuzzyAttributesFilename = parameters.getOutputFile(2);
        this.geneticLearningLogFilename = parameters.getOutputFile(3);
        
        try {
            System.out.println("\nReading the transaction set: " + parameters.getTransactionsInputFile());
            
            this.trans = new myDataset();
            this.trans.readDataSet(parameters.getTransactionsInputFile());
        }
        catch (IOException e) {
            System.err.println("There was a problem while reading the input transaction set: " + e);
            somethingWrong = true;
        }
        
        long seed = Long.parseLong(parameters.getParameter(0));
        
        this.nEvaluations = Integer.parseInt(parameters.getParameter(1));
        this.popSize = Integer.parseInt(parameters.getParameter(2));
        this.nBitsGene = Integer.parseInt(parameters.getParameter(3));
        this.phi = Double.parseDouble(parameters.getParameter(4));
        this.d = Double.parseDouble(parameters.getParameter(5));
        this.nFuzzyRegionsForNumericAttributes = Integer.parseInt(parameters.getParameter(6));
        this.useMaxForOneFrequentItemsets = Boolean.parseBoolean(parameters.getParameter(7));
        this.minSupport = Double.parseDouble(parameters.getParameter(8));
        this.minConfidence = Double.parseDouble(parameters.getParameter(9));
        
        Randomize.setSeed(seed);
    }

    /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
        	this.proc = new AlcalaetalProcess(this.trans, this.nEvaluations, this.popSize, this.nBitsGene, this.phi, this.d, this.nFuzzyRegionsForNumericAttributes, this.useMaxForOneFrequentItemsets, this.minSupport, this.minConfidence);
        	this.proc.run();
        	this.associationRulesSet = this.proc.getRulesSet();
        	this.proc.printReport(this.associationRulesSet);
        	
        	/*for (int i=0; i < this.associationRulesSet.size(); i++) {
        		System.out.println(this.associationRulesSet.get(i));
        	}*/
        	
			try {
				int r, i;
				AssociationRule ar;
				Itemset itemset;
				
				this.saveFuzzyAttributes(this.uniformFuzzyAttributesFilename, this.proc.getUniformFuzzyAttributes());
				this.saveFuzzyAttributes(this.adjustedFuzzyAttributesFilename, this.proc.getAdjustedFuzzyAttributes());
				this.saveGeneticLearningLog(this.geneticLearningLogFilename, this.proc.getGeneticLearningLog());
				
				PrintWriter rules_writer = new PrintWriter(this.rulesFilename);
				PrintWriter values_writer = new PrintWriter(this.valuesFilename);
				
				rules_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				rules_writer.println("<rules>");
				
				values_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				values_writer.print("<values ");
				values_writer.println("n_one_frequent_itemsets=\"" + this.proc.getNumberOfOneFrequentItemsets() + "\" n_rules=\"" + this.associationRulesSet.size() + "\">");
				
				for (r=0; r < this.associationRulesSet.size(); r++) {
					ar = this.associationRulesSet.get(r);
					
					rules_writer.println("<rule id = \"" + r + "\" />");
					values_writer.println("<rule id=\"" + r + "\" rule_support=\"" + ar.getRuleSupport() + "\" antecedent_support=\"" + ar.getAntecedentSupport() + "\" confidence=\"" + ar.getConfidence() + "\"/>");					
					rules_writer.println("<antecedents>");			
					itemset = ar.getAntecedent();
					
					for (i=0; i < itemset.size(); i++)
						this.createRule(itemset.get(i), this.proc.getAdjustedFuzzyAttributes(), rules_writer);
						
					rules_writer.println("</antecedents>");
					
					rules_writer.println("<consequents>");			
					itemset = ar.getConsequent();
					
					for (i=0; i < itemset.size(); i++)
						this.createRule(itemset.get(i), this.proc.getAdjustedFuzzyAttributes(), rules_writer);
					
					rules_writer.println("</consequents>");
					
					rules_writer.println("</rule>");
				}
				
				rules_writer.println("</rules>");
				values_writer.println("</values>");
				
				rules_writer.close();
				values_writer.close();
				
				System.out.println("\nAlgorithm Finished");
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
    }
    
    private void createRule(Item item, ArrayList<FuzzyAttribute> fuzzy_attributes, PrintWriter w) {
		int attr;
		boolean stop;
    	FuzzyAttribute fuzzy_attr;
    	FuzzyRegion[] fuzzy_regions;

		attr = item.getIDAttribute();

		if (this.trans.isNominal(attr)) {
			w.print("<attribute name = \"" + this.trans.getAttributeName(attr) + "\" value=\"");
			w.print( ""+ this.trans.getNominalValue(attr, item.getIDLabel()));
		}
		else {
			stop = false;
			fuzzy_attr = fuzzy_attributes.get(0);
			for (int i=0; i < fuzzy_attributes.size() && !stop; i++) {
				fuzzy_attr = fuzzy_attributes.get(i);
			    if (fuzzy_attr.getIdAttr() == item.getIDAttribute())  stop = true;
			}
			fuzzy_regions = fuzzy_attr.getFuzzyRegions();
			
			w.print("<attribute name = \"" + trans.getAttributeName( fuzzy_attr.getIdAttr() ) + "\" value = \"");
			w.print( fuzzy_regions[ item.getIDLabel() ].getLabel() );
		}
		w.println("\" />");
    }
    
    private void saveFuzzyAttributes(String fuzzy_attrs_fname, ArrayList<FuzzyAttribute> fuzzy_attributes) throws FileNotFoundException {
		int attr, region, id_attr;
		boolean stop;
    	FuzzyRegion[] fuzzy_regions;
    	FuzzyAttribute fuzzy_attr;
		PrintWriter fuzzy_attrs_writer = new PrintWriter(fuzzy_attrs_fname);
		
		fuzzy_attrs_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		fuzzy_attrs_writer.println("<data_base>");

		for (attr=0; attr < this.trans.getnVars(); attr++) {
			if (this.trans.isNominal(attr)) {
				fuzzy_attrs_writer.println("<attribute name = \"" + this.trans.getAttributeName(attr) + "\" nValues = \"" + this.trans.nValueNominal(attr) + "\" Type = \"" + this.trans.getAttributeTypeString(attr) + "\" >");
				
				for (region=0; region < this.trans.nValueNominal(attr); region++) {
					fuzzy_attrs_writer.println("<value \"" + this.trans.getNominalValue(attr, region) + "\" />");
				}
			}
			else {
				fuzzy_attrs_writer.println("<attribute name = \"" + this.trans.getAttributeName(attr) + "\" nValues = \"" + this.nFuzzyRegionsForNumericAttributes + "\" Type = \"" + this.trans.getAttributeTypeString(attr) + "\" >");
				stop = false;
				fuzzy_attr = fuzzy_attributes.get(0);
				for (int i=0; i < fuzzy_attributes.size() && !stop; i++) {
					fuzzy_attr = fuzzy_attributes.get(i);
					if (fuzzy_attr.getIdAttr() == attr)  stop = true;
				}		
				fuzzy_regions = fuzzy_attr.getFuzzyRegions();
				for (region=0; region < fuzzy_regions.length; region++) {
					fuzzy_attrs_writer.print("<value \"" + fuzzy_regions[region].getLabel() + "\" ");
					fuzzy_attrs_writer.print("\"" + fuzzy_regions[region].getX0() + "\" ");
					fuzzy_attrs_writer.print("\"" + fuzzy_regions[region].getX1() + "\" ");
					fuzzy_attrs_writer.println("\"" + fuzzy_regions[region].getX3() + "\" />");
				}
			}
			
			fuzzy_attrs_writer.println("</attribute>");
		}
		
		fuzzy_attrs_writer.println("</data_base>");
		fuzzy_attrs_writer.close();
    }
    
    private void saveGeneticLearningLog(String genetic_learning_log_fname, String xml_str) throws FileNotFoundException {
    	PrintWriter genetic_learning_log_writer = new PrintWriter(genetic_learning_log_fname);
    	
    	genetic_learning_log_writer.println(xml_str);
    	genetic_learning_log_writer.close();
    }
    
}