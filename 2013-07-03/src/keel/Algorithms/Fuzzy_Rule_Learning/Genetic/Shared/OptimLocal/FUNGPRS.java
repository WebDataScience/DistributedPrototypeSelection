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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.OptimLocal;

import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model.*;
import keel.Algorithms.Shared.Exceptions.*;
    

/**
 * <p>
 * Class for evaluating the fitness of a rule set.
 * Need: The fuzzy system
 * </p>
 * 
 * <p>
 * @author Written by Luciano S�nchez (University of Oviedo) 03/03/2004
 * @author Modified by M.R. Su�rez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class FUNGPRS extends FUN {
    
    RegSymFuzzyGP f;
    
    /**
     * <p> 
     * Constructor
     * </p>
     * @param vf Fuzzy system
     * @param used The set od used rules
     * @param initial The set of initial rules
     */
    public FUNGPRS(RegSymFuzzyGP vf, boolean[] used, double[]initial) { 
        super(used,initial);
        f=vf; 
    }
    
    /**
     * <p>
     * The public method evaluate the fitness of the rule set
     * </p>
     * @param x The set of rules
     * @return The result of evaluate the fitness
     */
    public double evaluate(double[] x) { 
        f.setConsts(filter(x));
        
        try {
          double result=f.fitness(); 
          f.setConsts(initial);
          return result;
        } catch(invalidFitness e) {
            System.err.println(e);   
            return 0;
        }
    }
    
}

