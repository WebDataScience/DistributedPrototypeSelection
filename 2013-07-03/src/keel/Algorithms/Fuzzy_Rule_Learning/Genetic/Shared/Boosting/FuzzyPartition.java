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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting;

public class FuzzyPartition {
   public double vertices[];
   public FuzzyPartition(double []v) {  
      vertices=new double[v.length];
      for (int i=0;i<v.length;i++) vertices[i]=v[i];
   }
   public double []pertenencia(double x) {
     // All terms ownership vector
     double mu[]=new double[vertices.length];
     if (x<=vertices[0]) mu[0]=1;
     for (int i=0;i<vertices.length-1;i++) {
        if (x>vertices[i] && x<=vertices[i+1]) {
           mu[i+1]=(x-vertices[i])/(vertices[i+1]-vertices[i]);
           mu[i]=1-mu[i+1];
        }
     }
     if (x>=vertices[vertices.length-1]) mu[vertices.length-1]=1;
     return mu;
   }
}


