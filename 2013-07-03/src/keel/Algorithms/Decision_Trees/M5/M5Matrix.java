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
 * Class for handling a matrix
 */

public final class M5Matrix {


    double[][] elements;

    /**
     * Constructs a matrix
     * @param nr the number of the rows
     * @param nc the number of the columns
     */
    public M5Matrix(int nr, int nc) {
        elements = new double[nr][nc];
    }

    /**
     * Converts a matrix to a string
     * @param nrl the smallest index of the rows
     * @param nrh the largest index of the rows
     * @param ncl the smallest index of the column
     * @param nch the largest index of the column
     * @return the converted string
     */
    public final String toString(int nrl, int nrh, int ncl, int nch) {

        int i, j;
        StringBuffer text = new StringBuffer();

        text.append("Printing matrix[" + nrl + ":" + nrh + "][" + ncl + ":" +
                    nch + "]:\n");
        for (i = nrl; i <= nrh; i++) {
            for (j = ncl; j <= nch; j++) {
                text.append("\t" + M5.doubleToStringG(elements[i][j], 5, 3));
            }
            text.append("\n");
        }

        return text.toString();
    }

    /**
     * Returns the transpose of a matrix [0:n-1][0:m-1]
     * @param n the number of rows
     * @param m the number of columns
     * @return the transposed matrix
     */
    public final M5Matrix transpose(int n, int m) {
        int i, j;
        M5Matrix b;

        b = new M5Matrix(m, n);
        for (i = 0; i <= m - 1; i++) {
            for (j = 0; j <= n - 1; j++) {
                b.elements[i][j] = elements[j][i];
            }
        }
        return b;
    }

    /**
     * Reurns the multiplication of two matrices
     * @param b the multiplication matrix
     * @param l the number of the rows of the instance matrix
     * @param m the number of the columns of the instance matrix, and the number of the rows of matrix b
     * @param n the number of the columns of matrix b
     * @return the product matrix
     */
    public final M5Matrix multiply(M5Matrix b, int l, int m, int n) {
        int i, j, k;
        M5Matrix c;

        c = new M5Matrix(l, n);
        for (i = 0; i <= l - 1; i++) {
            for (j = 0; j <= n - 1; j++) {
                for (k = 0; k <= m - 1; k++) {
                    c.elements[i][j] += elements[i][k] * b.elements[k][j];
                }
            }
        }

        return c;
    }

    /**
     * Linear regression
     * @param y the dependent variable vector
     * @param n the number of the observations
     * @param m the number of the coefficients
     * @return the coefficients
     */
    public final double[] regression(M5Matrix y, int n, int m) { // x[0:n-1][0:m-1], y[0:n-1][0:0] b[0:m-1]
        int i, indx[];
        double b[];
        M5Matrix ss, xt, d, bb;

        xt = this.transpose(n, m);
        ss = xt.multiply(this, m, n, m);
        bb = xt.multiply(y, m, n, 1);
        b = new double[m];
        for (i = 0; i <= m - 1; i++) {
            b[i] = bb.elements[i][0];
        }

        indx = new int[m];
        ss.ludcmp(m, indx);
        ss.lubksb(m, indx, b);

        return b;
    }

    /**
     * LU backward substitution
     * @param n the number of the coefficients
     * @param indx the index
     * @param b the double vector, storing constant terms in the equation sets; it later stores the computed coefficients' values
     */
    public final void lubksb(int n, int[] indx, double b[]) {

        int i, ii = -1, ip, j;
        double sum;

        for (i = 0; i <= n - 1; i++) {
            ip = indx[i];
            sum = b[ip];
            b[ip] = b[i];
            if (ii != -1) {
                for (j = ii; j <= i - 1; j++) {
                    sum -= elements[i][j] * b[j];
                }
            } else if (sum != 0.0) {
                ii = i;
            }
            b[i] = sum;
        }
        for (i = n - 1; i >= 0; i--) {
            sum = b[i];
            for (j = i + 1; j <= n - 1; j++) {
                sum -= elements[i][j] * b[j];
            }
            b[i] = sum / elements[i][i];
        }
    }

    /**
     * LU decomposition
     * @param n the number of coefficients
     * @param indx the index
     * @return the integer vector of the attributes's singularities
     */
    public final int[] ludcmp(int n, int[] indx) {

        int i, imax = -1, j, k, singulars[];
        double big, dum, sum, temp;
        double vv[];
        double TINY = 1.e-20;

        singulars = new int[n];
        for (i = 0; i <= n - 1; i++) {
            singulars[i] = 0;
        }
        vv = new double[n];
        for (i = 0; i <= n - 1; i++) {
            big = 0.0;
            for (j = 0; j <= n - 1; j++) {
                if ((temp = Math.abs(elements[i][j])) > big) {
                    big = temp;
                }
            }
            if (big < 0.000000001) {
                elements[i][i] = 1.0;
                big = 1.0;
                singulars[i] = 1;
            }
            /* m5error("Singular matrix in routine ludcmp");*/
            vv[i] = 1.0 / big;
        }
        for (j = 0; j <= n - 1; j++) {
            for (i = 0; i < j; i++) {
                sum = elements[i][j];
                for (k = 0; k < i; k++) {
                    sum -= elements[i][k] * elements[k][j];
                }
                elements[i][j] = sum;
            }
            big = 0.0;
            for (i = j; i <= n - 1; i++) {
                sum = elements[i][j];
                for (k = 0; k < j; k++) {
                    sum -= elements[i][k] * elements[k][j];
                }
                elements[i][j] = sum;
                if ((dum = vv[i] * Math.abs(sum)) >= big) {
                    big = dum;
                    imax = i;
                }
            }
            if (j != imax) {
                for (k = 0; k <= n - 1; k++) {
                    dum = elements[imax][k];
                    elements[imax][k] = elements[j][k];
                    elements[j][k] = dum;
                }
                vv[imax] = vv[j];
            }
            indx[j] = imax;
            if (elements[j][j] == 0.0) {
                elements[j][j] = TINY;
            }
            if (j != n - 1) {
                dum = 1.0 / (elements[j][j]);
                for (i = j + 1; i <= n - 1; i++) {
                    elements[i][j] *= dum;
                }
            }
        }
        return singulars;
    }


}

