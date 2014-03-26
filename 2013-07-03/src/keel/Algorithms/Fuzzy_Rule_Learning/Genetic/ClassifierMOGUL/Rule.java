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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco Jos� Berlanga (University of Ja�n) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
class Rule {
/**
 * <p>
 * Each rule of the population has this form
 * </p>
 */
  
        public FuzzySet [] Ant;
        public T_Consequent [] Cons;

	/**
	 * <p>
	 * Constructor
	 * </p>
	 * @param n_ant int Number of input variables
	 * @param n_clases int Number of classes for the problem
	 */
        public Rule(int n_ant, int n_clases) {
                int i;

                Ant = new FuzzySet[n_ant];
                Cons = new T_Consequent[n_clases];

                for (i=0; i<n_ant; i++)  Ant[i] = new FuzzySet();
                for (i=0; i<n_clases; i++)  Cons[i] = new T_Consequent();
        }

}

