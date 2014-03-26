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

import java.io.*;
import java.util.*;

/**
 * Class for handling the impurity values when spliting the instances
 */
public final class Impurity {
    double n; // number of total instances
    int attr; // splitting attribute
    double nl; // number of instances in the left group
    double nr; // number of instances in the right group
    double sl; // sum of the left group
    double sr; // sum of the right group
    double s2l; // squared sum of the left group
    double s2r; // squared sum of the right group
    double sdl; // standard deviation of the left group
    double sdr; // standard deviation of the right group
    double vl; // variance of the left group
    double vr; // variance of the right group
    double sd; // overall standard deviation
    double va; // overall variance

    double impurity; // impurity value;
    int order; // order = 1, variance; order = 2, standard deviation; order = 3, the cubic root of the variance;
    // order = k, the k-th order root of the variance

    /**
     * Constructs an Impurity object containing the impurity values of partitioning the instances using an attribute
     * @param partition the index of the last instance in the left subset
     * @param attribute the attribute used in partitioning
     * @param inst instances
     * @param k the order of the impurity; =1, the variance; =2, the stardard deviation; =k, the k-th order root of the variance
     */
    public Impurity(int partition, int attribute, M5Instances inst, int k) {

        StatisticsStore values = new StatisticsStore(0, inst.numInstances() - 1,
                inst.classIndex(), inst);
        attr = attribute;
        n = inst.numInstances();
        sd = values.sd;
        va = values.va;

        values = new StatisticsStore(0, partition, inst.classIndex(), inst);
        nl = partition + 1;
        sl = values.sum;
        s2l = values.sqrSum;

        values = new StatisticsStore(partition + 1, inst.numInstances() - 1,
                                     inst.classIndex(), inst);
        nr = inst.numInstances() - partition - 1;
        sr = values.sum;
        s2r = values.sqrSum;

        order = k;
        this.incremental(0, 0);
    }

    /**
     * Converts an Impurity object to a string
     * @return the converted string
     */
    public final String toString() {

        StringBuffer text = new StringBuffer();

        text.append("Print impurity values:\n");
        text.append("    Number of total instances:\t" + n + "\n");
        text.append("    Splitting attribute:\t\t" + attr + "\n");
        text.append("    Number of the instances in the left:\t" + nl + "\n");
        text.append("    Number of the instances in the right:\t" + nr + "\n");
        text.append("    Sum of the left:\t\t\t" + sl + "\n");
        text.append("    Sum of the right:\t\t\t" + sr + "\n");
        text.append("    Squared sum of the left:\t\t" + s2l + "\n");
        text.append("    Squared sum of the right:\t\t" + s2r + "\n");
        text.append("    Standard deviation of the left:\t" + sdl + "\n");
        text.append("    Standard deviation of the right:\t" + sdr + "\n");
        text.append("    Variance of the left:\t\t" + vr + "\n");
        text.append("    Variance of the right:\t\t" + vr + "\n");
        text.append("    Overall standard deviation:\t\t" + sd + "\n");
        text.append("    Overall variance:\t\t\t" + va + "\n");
        text.append("    Impurity (order " + order + "):\t\t" + impurity + "\n");

        return text.toString();
    }

    /**
     * Incrementally computes the impurirty values
     * @param value the incremental value
     * @param type if type=1, value will be added to the left subset; type=-1, to the right subset; type=0, initializes
     */
    public final void incremental(double value, int type) {
        double y = 0., yl = 0., yr = 0.;

        switch (type) {
        case 1:
            nl += 1;
            nr -= 1;
            sl += value;
            sr -= value;
            s2l += value * value;
            s2r -= value * value;
            break;
        case -1:
            nl -= 1;
            nr += 1;
            sl -= value;
            sr += value;
            s2l -= value * value;
            s2r += value * value;
            break;
        case 0:
            break;
        default:
            M5.errorMsg("wrong type in Impurity.incremental().");
        }

        if (nl <= 0.0) {
            vl = 0.0;
            sdl = 0.0;
        } else {
            vl = (nl * s2l - sl * sl) / ((double) nl * ((double) nl));
            vl = Math.abs(vl);
            sdl = Math.sqrt(vl);
        }
        if (nr <= 0.0) {
            vr = 0.0;
            sdr = 0.0;
        } else {
            vr = (nr * s2r - sr * sr) / ((double) nr * ((double) nr));
            vr = Math.abs(vr);
            sdr = Math.sqrt(vr);
        }

        if (order <= 0) {
            M5.errorMsg(
                    "Impurity order less than zero in Impurity.incremental()");
        } else if (order == 1) {
            y = va;
            yl = vl;
            yr = vr;
        } else {
            y = Math.pow(va, 1. / order);
            yl = Math.pow(vl, 1. / order);
            yr = Math.pow(vr, 1. / order);
        }

        if (nl <= 0.0 || nr <= 0.0) {
            impurity = 0.0;
        } else {
            impurity = y - ((double) nl / (double) n) * yl -
                       ((double) nr / (double) n) * yr;
        }
    }

}


