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

//
//  Trio.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 19-8-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CPruner;

public class Trio implements Comparable {
  public int id;
  public double distancia;
  public int nVec;

  public Trio () {}
  public Trio (int a, int c, double b) {
	  
    id = a;
    distancia = b;
    nVec = c;
  }
  public int compareTo (Object o1) {
    if (this.nVec > ((Trio)o1).nVec)
      return -1;
    else if (this.nVec < ((Trio)o1).nVec)
      return 1;
    else {
      if (this.distancia > ((Trio)o1).distancia)
        return -1;
      else if (this.distancia < ((Trio)o1).distancia)
        return 1;
      else return 0;
    }
  }
  public String toString () {
    return new String ("{"+id+", "+nVec+", "+distancia+"}");
  }
}
