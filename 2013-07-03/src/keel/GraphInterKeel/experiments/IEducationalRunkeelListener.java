/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
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

package keel.GraphInterKeel.experiments;

import keel.RunKeelTxtDocente.EducationalRunKeelTxt;

/**
 * <p>
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez and(University of Córdoba) 07/07/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

public interface IEducationalRunkeelListener<A extends EducationalRunKeelTxt> {

    /**
     * <p>
     *  Is invoqued when all partitions have finished
     * </p>
     * @param event Event
     */
    public void runKeelFinished(EducationalRunkeelEvent<A> event);


    /**
     * <p>
     * Is invoqued when a partition has finished
     * </p>
     * @param event Event
     */
    public void runKeelIterationCompleted(EducationalRunkeelEvent<A> event);
}
