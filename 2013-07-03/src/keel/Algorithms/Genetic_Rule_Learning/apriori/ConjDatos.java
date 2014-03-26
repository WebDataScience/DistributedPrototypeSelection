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

package keel.Algorithms.Genetic_Rule_Learning.apriori;

import java.util.*;

/**
 * <p>T�tulo: Conjunto de Datos</p>
 * <p>Descripci�n: Maneja los conjuntos de datos</p>
 * <p>Creado: 02-agosto 2004</p>
 * @author Jos� Ram�n Cano de Amo
 * @version 1.0
*/
public class ConjDatos {

  /**
   *  Esta clase almacena conjuntos de dato de la forma.. atr atr atr.. clas
   */

  private LinkedList datos;
//	private int nAtrib;

  /**
   *Constructor. Inicializa las variables contenedoras
   */
  public ConjDatos() { //int nAtributos) {
    super();
    datos = new LinkedList();
//		nAtrib=nAtributos;
  }

  /**
   * Borra un dato
   * @param i Posicion a borrar
   */
  public void deleteDato(int i) {
    datos.remove(i);
  }

  /**
   * A�ade un dato
   * @param m Ejemplo
   */
  public void addDato(Muestra m) {
    Muestra mim = m.copiaMuestra();
    datos.add(mim);
  }

//	public int getNatributos(){
//		return nAtrib;
//	}

  /**
   * Devuelve un ejemplo
   * @param i Posicion del ejemplo
   * @return El ejemplo o muestra en la posicion i-esima
   */
  public Muestra getDato(int i) {
    Muestra m = (Muestra) datos.get(i);
    return (Muestra) datos.get(i);
  }

  /**
   * Asigna un dato. Modifica el que hubiese de antemano
   * @param i Posicion a insertar
   * @param m Ejemplo
   */
  public void setDato(int i, Muestra m) {
    datos.set(i, m);
  }

  /**
   * Devuelve el n�mero de ejemplos de nuestro conjunto de datos
   * @return El tama�o
   */
  public int size() {
    return (datos.size());
  }

  /**
   * Muestra por pantalla los ejemplos
   */
  public void print() {
    int i;
    for (i = 0; i < datos.size(); i++) {
      Muestra m = (Muestra) datos.get(i);
      m.print();
    }

  }

  /**
   * Copia el conjunto de datos en otro nuevo
   * @return Un nuevo conjunto de datos copia del actual
   */
  public ConjDatos copiaConjDatos() {
    int i;
    ConjDatos c = new ConjDatos();

    for (i = 0; i < datos.size(); i++) {
      Muestra aux;
      Muestra m = (Muestra) datos.get(i);
      aux = m.copiaMuestra();
      c.addDato(aux);
    }

    return c;
  }

  /**
   * Hace que los atributos de todos los ejemplos est�n en el intervalo [0,1]
   * @param datos Conjunto de datos
   */
  public void hazUniforme(Dataset datos){
    datos.normaliza();
  }

}

