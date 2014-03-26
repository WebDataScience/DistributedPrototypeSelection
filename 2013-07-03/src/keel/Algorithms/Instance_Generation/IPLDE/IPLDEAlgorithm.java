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
	IPLDE.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  23-7-2009
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.
**/

package keel.Algorithms.Instance_Generation.IPLDE;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * IPLDE algorithm calling.
 * @author Isaac Triguero
 */
public class IPLDEAlgorithm extends PrototypeGenerationAlgorithm<IPLDEGenerator>
{
    /**
     * Builds a new IPADE algorithm
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected IPLDEGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new IPLDEGenerator(train, params);    
    }
    
     /**
     * Main method. Executes IPLDE algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        IPLDEAlgorithm isaak = new IPLDEAlgorithm();
        isaak.execute(args);
    }
}
