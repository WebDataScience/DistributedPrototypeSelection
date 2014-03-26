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
 * File: Main.java
 *
 * A Main class to process the parameters of the method and launch the algorithm
 * 
 * @author Written by Julian Luengo Martin 01/01/2006
 * @author Modified by Victoria Lopez Morales 01/05/2010
 * @author Modified by Victoria Lopez Morales 05/10/2010
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.ImbalancedClassification.CSMethods.C_SVMCost;


public class Main {
    
    /** 
     * Creates a new instance of Main 
     */
    public Main() {
    }
    
    /**
     * Main method
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
    	svmClassifierCost M;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        M = new svmClassifierCost (args[0]);
        M.process();
    }
    
}
