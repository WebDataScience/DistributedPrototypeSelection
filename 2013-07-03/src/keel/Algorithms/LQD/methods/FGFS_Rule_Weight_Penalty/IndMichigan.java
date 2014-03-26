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

package keel.Algorithms.LQD.methods.FGFS_Rule_Weight_Penalty;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


/**
 *
 * File: fuzzy.java
 *
 * Properties and functions of individual of the population
 *
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
 * @version 1.0
 */


public class IndMichigan {

	  fuzzy[][] X;
	  Vector<Vector<Float>> Y;  
	  rule individuo;
	  Interval fitness;
	 
	  
	  IndMichigan(fuzzy[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int classes, int COST, int alfa,
			  Vector<Float> values_classes,Vector<Vector<Float>> costs,int instance, int asign_weight_rule) throws IOException 
	  {
		  individuo= new rule(pentradas,classes,asign_weight_rule);
		  if(instance<x.length && instance<10)
			  individuo.obtain_rule(x,y,pentradas,classes,COST, alfa,values_classes,costs,instance);
		  else
			  individuo.obtain_rule_random(x,y,pentradas,classes,COST, alfa,values_classes,costs);
		  
		  X=x;
		  Y=y;
	  }
    
	
	  IndMichigan(Vector<Integer> ant,fuzzy[][] x,Vector<Vector<Float>> y,Vector<partition> pentradas,int classes, int COST, int alfa,
			  Vector<Float> values_classes,Vector<Vector<Float>> costs,int asign_weight_rule) throws IOException 
	  {
//		 

		  individuo= new rule(pentradas,classes, asign_weight_rule);
		  Integer[]a= new Integer[ant.size()];
		  for(int i=0;i<ant.size();i++)
		  {
			  a[i]= ant.get(i);
		  }
		  individuo.setAntecedente(a);
		  
		  individuo.calcularconsequent(x, y, pentradas, classes,COST,alfa,values_classes,costs);

		  X=x;
		  Y=y;  
	  }
       
	
	  public rule getregla(){return individuo;}
	  public int size() { return individuo.size(); }
	  public Interval getfitness()
	  {
		  return fitness;
	  }
	  public fuzzy[][] getX()
	  {
		  return X;
	  }
	  public Vector<Vector<Float>> getY()
	  {
		  return Y;
	  }
	  public void asigninstances(fuzzy[][] x,Vector<Vector<Float>> y) 
	  {
		  X=x;
		  Y=y;
	  }

}

