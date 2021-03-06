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

package keel.Algorithms.RE_SL_Postprocess.MamWTuning;
import java.io.*; 
import org.core.*;
import java.util.*;
import java.lang.Math;

class P {
	public double semilla;
	public long cont_soluciones;
	public long Gen, n_genes, n_reglas, n_generaciones;
	public int n_soluciones, tipoc;

	public String fich_datos_chequeo, fich_datos_tst;
	public String fichero_conf, fichero_datos, ruta_salida;
	public String fichero_br, fichero_reglas, fich_tra_obli, fich_tst_obli;
	public String cadenaReglas = "";

	public MiDataset tabla, tabla_tst;
    public BaseR base_reglas;
    public Adap fun_adap;
    public AG alg_gen;


	public P (String f_e) {
		fichero_conf = f_e;
	}

	private String Quita_blancos(String cadena) {
		StringTokenizer sT = new StringTokenizer(cadena, "\t ", false);
		return (sT.nextToken());
	}


	/** Reads the data of the configuration file */
	public void leer_conf (){
		int i, j;
		String cadenaEntrada, valor;
		double cruce, mutacion, a, b, tau;
		int long_poblacion, tipo_fitness;


		// we read the file in a String
		cadenaEntrada = Fichero.leeFichero(fichero_conf);
		StringTokenizer sT = new StringTokenizer(cadenaEntrada, "\n\r=", false);

		// we read the algorithm's name
		sT.nextToken();
		sT.nextToken();

		// we read the name of the training and test files
		sT.nextToken();
		valor = sT.nextToken();

		StringTokenizer ficheros = new StringTokenizer(valor, "\t ", false);
		ficheros.nextToken();
		fich_datos_chequeo = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fich_datos_tst = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fichero_br = ((ficheros.nextToken()).replace('\"',' ')).trim();


		// we read the name of the output files
		sT.nextToken();
		valor = sT.nextToken();

		ficheros = new StringTokenizer(valor, "\t ", false);
		fich_tra_obli = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fich_tst_obli = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fichero_reglas = ((ficheros.nextToken()).replace('\"',' ')).trim();
		ruta_salida = fich_tst_obli.substring(0, fich_tst_obli.lastIndexOf('/') + 1);

		// we read the seed of the random generator
		sT.nextToken();
		valor = sT.nextToken();
		semilla = Double.parseDouble(Quita_blancos(valor));
		Randomize.setSeed((long) semilla);;

		// we read the Number of Iterations
		sT.nextToken();
		valor = sT.nextToken();
		n_generaciones = Long.parseLong(Quita_blancos(valor));

		// we read the Population Size
		sT.nextToken();
		valor = sT.nextToken();
		long_poblacion = Integer.parseInt(Quita_blancos(valor));

		// we read the Tau parameter for the minimun maching degree required to the KB
		sT.nextToken();
		valor = sT.nextToken();
		tau = Double.parseDouble(Quita_blancos(valor));

		// we read the Parameter a
		sT.nextToken();
		valor = sT.nextToken();
		a = Double.parseDouble(Quita_blancos(valor));

		// we read the Parameter b
		sT.nextToken();
		valor = sT.nextToken();
		b = Double.parseDouble(Quita_blancos(valor));

		// we read the Type of Fitness Function
		sT.nextToken();
		valor = sT.nextToken();
		tipo_fitness = Integer.parseInt(Quita_blancos(valor));

		// we read the Cross Probability
		sT.nextToken();
		valor = sT.nextToken();
		cruce = Double.parseDouble(Quita_blancos(valor));

		// we read the Mutation Probability
		sT.nextToken();
		valor = sT.nextToken();
		mutacion = Double.parseDouble(Quita_blancos(valor));

		// we create all the objects
		tipoc = 1;
		tabla = new MiDataset(fich_datos_chequeo, true);
		if (tabla.salir==false) {
		base_reglas = new BaseR(fichero_br, tabla);
		fun_adap = new Adap(tabla, base_reglas, tau, tipo_fitness);
		alg_gen = new AG(long_poblacion, base_reglas.n_reglas, cruce, mutacion, a, b, fun_adap);
	    }
	}

	public void run () {
		int i, j;
		double ec, el, ec_tst, el_tst;


		/* We read the configutate file and we initialize the structures and variables */
		leer_conf();
		if (tabla.salir==false) {
		Gen = 0;

		/* Generation of the initial population */
		alg_gen.Initialize();

		/* Evaluation of the initial population */
		alg_gen.Evaluate ();

		Gen++;

		/* Main of the genetic algorithm */
		do {
			/* Interchange of the new and old population */
			alg_gen.Intercambio();

			/* Selection by means of Baker */
			alg_gen.Select ();

			/* Crossover */
			if (tipoc>0)  alg_gen.Max_Min_Crossover();
			else  alg_gen.Cruce_Multipunto();

			/* Mutation */
			alg_gen.Mutacion ();

			/* Elitist selection */
			alg_gen.Elitist ();

			/* Evaluation of the current population */
			alg_gen.Evaluate ();

			/* we increment the counter */
			Gen++;
		} while (Gen <= n_generaciones);


		/* we calcule the MSEs */
		fun_adap.Decodifica (alg_gen.solucion());
		fun_adap.Error_tra ();
		ec = fun_adap.EC;
		el = fun_adap.EL;

		tabla_tst = new MiDataset(fich_datos_tst, false);
		fun_adap.Error_tst (tabla_tst);
		ec_tst  = fun_adap.EC;
		el_tst = fun_adap.EL;

		fun_adap.Cubrimientos_Base ();

		/* we write the RB */
		cadenaReglas = base_reglas.BRtoString();
		cadenaReglas += "\nMSEtra: " + ec + "  MSEtst: " + ec_tst + "\nAverage covering degree: " + fun_adap.medcb + " Minimum covering degree: " + fun_adap.mincb ;

		Fichero.escribeFichero(fichero_reglas, cadenaReglas);

		/* we write the obligatory output files*/
		String salida_tra = tabla.getCabecera();
		salida_tra += fun_adap.getSalidaObli(tabla);
		Fichero.escribeFichero(fich_tra_obli, salida_tra);

		String salida_tst = tabla_tst.getCabecera();
		salida_tst += fun_adap.getSalidaObli(tabla_tst);
		Fichero.escribeFichero(fich_tst_obli, salida_tst);


		/* we write the MSEs in specific files */
		Fichero.AnadirtoFichero(ruta_salida + "PcomunR.txt", "" + base_reglas.n_reglas + "\n");
		Fichero.AnadirtoFichero(ruta_salida + "PcomunTRA.txt", "" + ec + "\n");
		Fichero.AnadirtoFichero(ruta_salida + "PcomunTST.txt", "" + ec_tst + "\n");
	}
       }
}

