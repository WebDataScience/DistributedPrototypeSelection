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

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;


/** 
* <p> 
* It is the abstract class for the remaining basic classes related with Fuzzy Regression defined in keel.Algorithms.Symbolic_Regression. 
* </p> 
*  
* <p> 
* @author Written by Luciano S�nchez (University of Oviedo) 20/01/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/
public abstract class FuzzyRegressor {
    //the type for fuzzy regressors based on crisp sets (singleton fuzzy sets) 
	public final static int Crisp=0;
	//the type for fuzzy regressors based on interval sets (interval fuzzy sets)
    public final static int Interval=1;
    //the type for fuzzy regressors based on fuzzy sets (triangular fuzzy sets)
    public final static int Fuzzy=2;
    //the type of constants (Crisp, Interval and Fuzzy) to manage in derived classes. 
    protected static int constType; 
    /** 
     * <p> 
     * Creates and returns a fuzzy alpha-cut with result of the run.
     * 
     * </p>
     * @return a fuzzy alpha-cut with result of the run. 
     */
    public abstract FuzzyAlphaCut output(FuzzyAlphaCut[] x);
    /** 
     * <p> 
     * Creates and returns a copy of this object.
     * 
     * </p>
     * @return a clone of this instance. 
     */
    public abstract FuzzyRegressor clone();
    /**
     * Get current debugging message setting.
     */
    public abstract void debug();
}

