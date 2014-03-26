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

/*
 * This software is a cooperative product of The MathWorks and the National
 * Institute of Standards and Technology (NIST) which has been released to the
 * public domain. Neither The MathWorks nor NIST assumes any responsibility
 * whatsoever for its use by other parties, and makes no guarantees, expressed
 * or implied, about its quality, reliability, or any other characteristic.
 */

/*
 * CholeskyDecomposition.java
 * Copyright (C) 1999 The Mathworks and NIST
 *
 */

package keel.Algorithms.Statistical_Classifiers.Logistic.core.matrix;

import java.io.Serializable;

/** 
 * Cholesky Decomposition.
 * <P>
 * For a symmetric, positive definite matrix A, the Cholesky decomposition is
 * an lower triangular matrix L so that A = L*L'.
 * <P>
 * If the matrix is not symmetric or positive definite, the constructor
 * returns a partial decomposition and sets an internal flag that may
 * be queried by the isSPD() method.
 * <p/>
 * Adapted from the <a href="http://math.nist.gov/javanumerics/jama/" target="_blank">JAMA</a> package.
 *
 * @author The Mathworks and NIST 
 * @author Fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.1 $
 */
public class CholeskyDecomposition 
  implements Serializable {

  /** for serialization */
  private static final long serialVersionUID = -8739775942782694701L;

  /** 
   * Array for internal storage of decomposition.
   * @serial internal array storage.
   */
  private double[][] L;

  /** 
   * Row and column dimension (square matrix).
   * @serial matrix dimension.
   */
  private int n;

  /** 
   * Symmetric and positive definite flag.
   * @serial is symmetric and positive definite flag.
   */
  private boolean isspd;

  /** 
   * Cholesky algorithm for symmetric and positive definite matrix.
   *
   * @param  Arg   Square, symmetric matrix.
   */
  public CholeskyDecomposition(Matrix Arg) {
    // Initialize.
    double[][] A = Arg.getArray();
    n = Arg.getRowDimension();
    L = new double[n][n];
    isspd = (Arg.getColumnDimension() == n);
    // Main loop.
    for (int j = 0; j < n; j++) {
      double[] Lrowj = L[j];
      double d = 0.0;
      for (int k = 0; k < j; k++) {
        double[] Lrowk = L[k];
        double s = 0.0;
        for (int i = 0; i < k; i++) {
          s += Lrowk[i]*Lrowj[i];
        }
        Lrowj[k] = s = (A[j][k] - s)/L[k][k];
        d = d + s*s;
        isspd = isspd & (A[k][j] == A[j][k]); 
      }
      d = A[j][j] - d;
      isspd = isspd & (d > 0.0);
      L[j][j] = Math.sqrt(Math.max(d,0.0));
      for (int k = j+1; k < n; k++) {
        L[j][k] = 0.0;
      }
    }
  }

  /** 
   * Is the matrix symmetric and positive definite?
   * @return     true if A is symmetric and positive definite.
   */
  public boolean isSPD() {
    return isspd;
  }

  /** 
   * Return triangular factor.
   * @return     L
   */
  public Matrix getL() {
    return new Matrix(L,n,n);
  }

  /** 
   * Solve A*X = B
   * @param  B   A Matrix with as many rows as A and any number of columns.
   * @return     X so that L*L'*X = B
   * @exception  IllegalArgumentException  Matrix row dimensions must agree.
   * @exception  RuntimeException  Matrix is not symmetric positive definite.
   */
  public Matrix solve(Matrix B) {
    if (B.getRowDimension() != n) {
      throw new IllegalArgumentException("Matrix row dimensions must agree.");
    }
    if (!isspd) {
      throw new RuntimeException("Matrix is not symmetric positive definite.");
    }

    // Copy right hand side.
    double[][] X = B.getArrayCopy();
    int nx = B.getColumnDimension();

    // Solve L*Y = B;
    for (int k = 0; k < n; k++) {
      for (int i = k+1; i < n; i++) {
        for (int j = 0; j < nx; j++) {
          X[i][j] -= X[k][j]*L[i][k];
        }
      }
      for (int j = 0; j < nx; j++) {
        X[k][j] /= L[k][k];
      }
    }

    // Solve L'*X = Y;
    for (int k = n-1; k >= 0; k--) {
      for (int j = 0; j < nx; j++) {
        X[k][j] /= L[k][k];
      }
      for (int i = 0; i < k; i++) {
        for (int j = 0; j < nx; j++) {
          X[i][j] -= X[k][j]*L[k][i];
        }
      }
    }
    return new Matrix(X,n,nx);
  }
}

