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
 * @author Written by Albert Orriols (La Salle University Ram�n Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Sol� (La Salle University Ram�n Lull, Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.util.*;
import java.lang.*;
import java.io.*;


public class RFreeMutation implements RealMutation {
/**
 * <p>
 * This class implements the free mutation. So, a gene can be changed for
 * any valid value in the representation.
 * </p>
 */
	
  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Mutates the lower real value.
 * </p>
 * <p>
 * 
 * @param lowerValue is the current lower value of this classifier position
 * </p>
 * <p>
 * @param upperValue is the current upper value of this classifier position.
 * </p>
 * <p>
 * @param currentState is the current State of the environment for this
 * allele of the classifier.
 * </p>
 * <p>
 * @return a double with the mutated value
 * </p>
 */
    public double mutateLower(double lowerValue, double upperValue, double currentState) {        
        double des = 0.0;
        if (Config.rand() < Config.pM){
        	des = (Config.rand() - 0.5)*2 * Config.m_0;  //We obtain a number between -1 and 1.
        	lowerValue += des;
        }
    	return lowerValue;  
    } // end mutateLower        

/**
 * <p>
 * Mutates the upper real value.
 * </p>
 * <p>
 * 
 * </p>
 * <p>
 * 
 * @return a double with the mutated value.
 * </p>
 */
    public double mutateUpper(double lowerValue, double upperValue, double currentState) {        
        double des = 0.0;
        if (Config.rand() < Config.pM){
        	des = (Config.rand() - 0.5)*2 * Config.m_0;  //We obtain a number between -1 and 1.
        	upperValue += des;
        }
    	return upperValue;  
    } // end mutateUpper        

} // end RFreeMutation




