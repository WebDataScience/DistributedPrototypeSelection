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

package keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification;


/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Modified by Aaron Ruiz Mora (University of Cordoba)16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IClassifier {
	
	/**
	 * <p>
	 * Generic classifier.
	 * </p>
	 */
	
	/**
	 * <p>
	 * Obtain the associated class of one observation
	 * 
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return byte [] Array indicating the class of the observation
	 *                 For example, {0,0,1} indicates that observation
	 *                 is of the third class, {0,1,0} indicates that 
	 *                 observation is of the second class
	 * </p>                
	 */
    
    public byte[] classify(double []inputs);
    
	/**
	 * <p>
	 * Obtain the associated class of a set of observations, through
	 * their inputs values
	 * 
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return byte[] Matrix indicating the class of the observation
	 *                For example, {0,0,1} indicates that observation
	 *                is of the third class, {0,1,0} indicates that 
	 *                observation is of the second class
	 * </p>               
	 */
    
    public byte[][] classify(double [][]inputs);
    
}

