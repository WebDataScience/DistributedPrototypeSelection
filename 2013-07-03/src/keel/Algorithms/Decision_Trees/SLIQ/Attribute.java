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

package keel.Algorithms.Decision_Trees.SLIQ;
import java.util.*;

/** 
 * Clase que representa un atributo
 */
public class Attribute {
	/** Atributo continuo (real o entero). */
	public final static int CONTINUOUS = 0;
	
	/** Atributo discreto (conjunto de elementos). */
	public final static int DISCRET = 1;	
	
	/** Nombre del atributo.*/
	private String name;					
	
	/** Tipo del atributo. */
	private int type;			
	
	/** Valores para atributos que son una lista. */
	private Vector values;					
	
	/** Menor valor para un atributo num�rico. */
	private float bottom;					
	
	/** Mayor valor para un atributo num�rico. */
	private float top;
	
	/** �ndice asociado al atributo. */
	private int index;
	
	/** �Est� incluido en las entradas o salidas?. */
	private boolean used;					

  	/** Constructor para atributos continuos.
  	 * 
  	 * @param attributeName		Nombre del atributo.
  	 * @param attributeIndex	�ndice del atributo.
  	 */
  	public Attribute(String attributeName, int attributeIndex) {
  		name = attributeName;
  		index = attributeIndex;
  		values = null;
  		type = CONTINUOUS;
  		used = false;
	}

  	/** Constructor para atributos discretos.
  	 * 
  	 * @param attributeName		Nombre del atributo.
  	 * @param attributeValues	Valores del atributo.
  	 * @param attributeIndex	�ndice del atributo.
  	 */
  	public Attribute(String attributeName, Vector attributeValues, int attributeIndex ) {
  		name = attributeName;
  		index = attributeIndex;
		type = DISCRET;
		values = new Vector(attributeValues.size());
  		used = false;
  			
		for(int i=0; i<attributeValues.size(); i++) {
			Object store = attributeValues.elementAt(i);
			values.addElement(store);
		}
	}

  	/** M�todo para obtener el �ndice de un valor en una lista de valores.
  	 * 
  	 * @param value 	El valor.
  	 * 
  	 * @return			�ndice que corresponde al valor.
  	 */
  	public final int valueIndex(String value)  {
  		int i = 0;
  		if (!isDiscret())
  			return -1;
    
  		Enumeration enum2 = values.elements();
  		
  		while (enum2.hasMoreElements()) {
  			String element = (String)enum2.nextElement();
  			
  			if(element.equalsIgnoreCase(value))
	  			return i;
  			 
  			i++;
  		}
  		
  		return -1;
	}

  	/** Devuelve si el atributo es discreto o no.
  	 * 
  	 */
  	public final boolean isDiscret() {
  		return (type == DISCRET);
	}

  	/** Devuelve si el atributo es continuo o no.
  	 * 
  	 */
  	public final boolean isContinuous() {
  		return (type == CONTINUOUS);
	}

  	/** Devuelve el nombre del atributo.
  	 * 
  	 */
  	public final String name() {
  		return name;
	}
  
   	/** M�todo para obtener el n�mero de valores de un atributo discreto.
   	 * 
   	 * @return	El n�mero de valores del atributo.
   	 */
  	public final int numValues() {
  		if (!isDiscret())
  			return 0;
  		else
  			return values.size();
	}

  	/** Devuelve el valor correspondiente a un �ndice.
  	 * 
  	 * @param valIndex	El �ndice asociado al valor.
  	 */
  	public final String value(int valIndex)  {
  		if (!isDiscret())
  			return "";
  		else {
  			Object val = values.elementAt(valIndex);
      
  			return (String) val;
  		}
	}

  	/** Establece el rango de un atributo continuo.
  	 * 
  	 * @param minRange	M�nimo valor del rango.
  	 * @param maxRange	M�ximo valor del rango.
  	 */ 
    final void setRange(float minRange, float maxRange)  {
    	if(isDiscret()) 
    		throw new IllegalArgumentException("Solamente puede aplicarse a atributos num�ricos");
    	else {
    		bottom = minRange;
    		top = maxRange;
    	}
  	}

  	/** Establece el rango de un atributo continuo.
  	 * 
  	 * @param minRange	M�nimo valor del rango.
  	 * @param maxRange	M�ximo valor del rango.
  	 */
    final void setRange(int minRange, int maxRange) {
    	if(isDiscret()) 
    		throw new IllegalArgumentException("Solamente puede aplicarse a atributos num�ricos");
    	else {
    		bottom = minRange;
    		top = maxRange;
    	}
  	}

  	/** Devuelve el menor valor de un atributo num�rico.
  	 * 
  	 */
    public final float getMinRange() {
    	if(isDiscret()) 
    		throw new IllegalArgumentException("Solamente puede aplicarse a atributos num�ricos");
    	else
    		return bottom;
  	}

  	/** Devuelve el mayor valor de un atributo num�rico.
  	 * 
  	 */
    public final float getMaxRange() {
    	if(isDiscret()) 
    		throw new IllegalArgumentException("Solamente puede aplicarse a atributos num�ricos");
    	else
    		return top;
  	}
    
    /** Establece el atributo como utilizado.
     * 
     */
    public void activate() {
    	used = true;
    }
    
    /** Devuelve true si el atributo es utilizado como entrada o salida.
     * 
     */
    public boolean isActive() {
    	return used;
    }

    /** Devuelve el �ndice del atributo.
     * 
     */
    public int getIndex() {
    	return index;
    }
}

