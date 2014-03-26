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

package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import org.core.Randomize;

public class Sampling {
	
	int maxSize;
	int num;
	int[] sample;

	void initSampling() {
		int i;
		for(i=0;i<maxSize;i++) sample[i]=i;
		num=maxSize;
	}


	Sampling(int pMaxSize) {
		maxSize=pMaxSize;
		sample = new int[maxSize];
		initSampling();
	}



	int getSample() {
		int pos=Randomize.Randint(0,num);
		int value=sample[pos];
		sample[pos]=sample[num-1];
		num--;

		if(num==0) initSampling();

		return value;
	}

	int numSamplesLeft() {return num;}
}


