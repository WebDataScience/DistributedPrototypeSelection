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
* @author Written by Cristobal Romero (Universidad de C�rdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

/**
 * Class to store information about an option. <p>
 *
 * Typical usage: <p>
 *
 * <code>Option myOption = new Option("Uses extended mode.", "E", 0, "-E")); </code><p>
 */
public class Information {

    /** What does this option do? */
    private String m_Description;

    /** The synopsis. */
    private String m_Synopsis;

    /** What's the option's name? */
    private String m_Name;

    /** How many arguments does it take? */
    private int m_NumArguments;

    /**
     * Creates new option with the given parameters.
     *
     * @param description the option's description
     * @param name the option's name
     * @param numArguments the number of arguments
     */
    public Information(String description, String name,
                       int numArguments, String synopsis) {

        m_Description = description;
        m_Name = name;
        m_NumArguments = numArguments;
        m_Synopsis = synopsis;
    }

    /**
     * Returns the option's description.
     *
     * @return the option's description
     */
    public String description() {

        return m_Description;
    }

    /**
     * Returns the option's name.
     *
     * @return the option's name
     */
    public String name() {

        return m_Name;
    }

    /**
     * Returns the option's number of arguments.
     *
     * @return the option's number of arguments
     */
    public int numArguments() {

        return m_NumArguments;
    }

    /**
     * Returns the option's synopsis.
     *
     * @return the option's synopsis
     */
    public String synopsis() {

        return m_Synopsis;
    }
}

