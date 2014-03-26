
/**

 * 

 * File: Metodo.java

 * 

 * An auxiliary class to initialize Instance Selection algorithms

 * 

 * @author Written by Salvador Garc?a (University of Granada) 20/07/2004 

 * @author Modified by Isaac Triguero (University of Granada) 22/06/2010 

 * @author Modified by Victoria Lopez (University of Granada) 21/09/2010 

 * @version 0.1 

 * @since JDK1.5

 * 

 */

package keel.Algorithms.Preprocess.Basic;



import keel.Dataset.*;



import java.util.StringTokenizer;



import org.core.Fichero;



public class Metodo {



	  /*Path and names of I/O files*/

	 public static String ficheroTraining;

	  public String ficheroValidation;

	  public static String ficheroTest;

	  public static String ficheroSalida[];



	  /*Data Structures*/

	  public static InstanceSet training;

	  public static InstanceSet test;

	  public static Attribute entradas[];

	  public static Attribute salida;

	  public static int nEntradas;

	  public static String relation;



	  /*Data Matrix*/

	  public static double datosTrain[][];

	  public static int clasesTrain[];

          

          

          	  /*Data Matrix*/

	  public static double datosTest[][];

	  public static double realTest[][];

	  public static int clasesTest[];



	  /*Extra*/

	  public static boolean nulosTrain[][];

	  public static int nominalTrain[][];

	  public static double realTrain[][];

	  

	  public static boolean distanceEu;

	  

	  static public double nominalDistance[][][];

	  static public double stdDev[];

		static public int k=5;	  

  	/**

	 * Default builder

	 */

	public Metodo () {} //end-method

	

	

	

	public Metodo (String ficheroScript, int k) {

		

		int nClases, i, j, l, m, n;

		double VDM;

		int Naxc, Nax, Nayc, Nay;

		double media, SD;	

		

		distanceEu = false;

		  

		/*Read of the script file*/

		leerConfiguracion1 (ficheroScript);

		

	

		

		

	}



	/**

	 * Builder. Creates the basic structures of the algorithm

	 *

	 * @param ficheroScript Configuration script

	 */

	public Metodo (String ficheroScript) {



		int nClases, i, j, l, m, n;

		double VDM;

		int Naxc, Nax, Nayc, Nay;

		double media, SD;	

		

		distanceEu = false;

		  

		/*Read of the script file*/

		leerConfiguracion (ficheroScript);



		/*Read of data files*/

		try {

		  training = new InstanceSet();

		  training.readSet(ficheroTraining, true);







	

		} catch (Exception e) {

		  System.err.println(e);

		  System.exit(1);

		}



		try {

		  test = new InstanceSet();

		  test.readSet(ficheroTest, false);

                     

                  

        	} catch (Exception e) {

		  System.err.println(e);

		  System.exit(1);

		}



               

             

                try{

                    normalizar();

                }catch (Exception e) {

		  System.err.println(e);

		  System.exit(1);

		}

                

                /*Previous computation for HVDM distance*/

            if (distanceEu == false) {    	

                stdDev = new double[Attributes.getInputNumAttributes()];

                nominalDistance = new double[Attributes.getInputNumAttributes()][][];

                        nClases = Attributes.getOutputAttribute(0).getNumNominalValues();

                     

                        

                for (i=0; i<nominalDistance.length; i++) {

                        if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {

                                nominalDistance[i] = new double[Attributes.getInputAttribute(i).getNumNominalValues()][Attributes.getInputAttribute(i).getNumNominalValues()];

                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) { 

                                        nominalDistance[i][j][j] = 0.0;



                                }

                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) {

                                        for (l=j+1; l<Attributes.getInputAttribute(i).getNumNominalValues(); l++) {

                                                VDM = 0.0;

                                                Nax = Nay = 0;

                                                for (m=0; m<training.getNumInstances(); m++) {

                                                        if (nominalTrain[m][i] == j) {

                                                                Nax++;



                                                        }

                                                        if (nominalTrain[m][i] == l) {

                                                                Nay++;





                                                        }

                                                }

                                                for (m=0; m<nClases; m++) {

                                                        Naxc = Nayc = 0;

                                                        for (n=0; n<training.getNumInstances(); n++) {

                                                                if (nominalTrain[n][i] == j && clasesTrain[n] == m) {

                                                                        Naxc++;



                                                                }

                                                                if (nominalTrain[n][i] == l && clasesTrain[n] == m) {

                                                                        Nayc++;





                                                                }

                                                        }

                                                        VDM += (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay)) * (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay));



                                                }

                                                nominalDistance[i][j][l] = Math.sqrt(VDM);

                                                nominalDistance[i][l][j] = Math.sqrt(VDM);







                                        }

                                }

                        } else {

                                media = 0;

                                SD = 0;

                                for (j=0; j<training.getNumInstances(); j++) {

                                        media += realTrain[j][i];

                                        SD += realTrain[j][i]*realTrain[j][i];



                                }

                                media /= (double)realTrain.length;

                                stdDev[i] = Math.sqrt((SD/((double)realTrain.length)) - (media*media));







                        }

                }

            }    



  

	} //end-method

	

	

	public Metodo (String ficheroScript, InstanceSet train) {



		int nClases, i, j, l, m, n;

		double VDM;

		int Naxc, Nax, Nayc, Nay;

		double media, SD;	

		

		distanceEu = false;

		  

		/*Read of the script file*/

		leerConfiguracion (ficheroScript);



		/*Read of data files*/

		try {

		  training = new InstanceSet(train);

	

		} catch (Exception e) {

		  System.err.println(e);

		  System.exit(1);

		}



		try {

		  test = new InstanceSet(train);

         } catch (Exception e) {

		  System.err.println(e);

		  System.exit(1);

		}



          try{

              normalizar();

          }catch (Exception e) {

		  System.err.println(e);

		  System.exit(1);

		}



               

                /*Previous computation for HVDM distance*/

            if (distanceEu == false) {    	

                stdDev = new double[Attributes.getInputNumAttributes()];

                nominalDistance = new double[Attributes.getInputNumAttributes()][][];

                        nClases = Attributes.getOutputAttribute(0).getNumNominalValues();

                        

                for (i=0; i<nominalDistance.length; i++) {

                        if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {

                                nominalDistance[i] = new double[Attributes.getInputAttribute(i).getNumNominalValues()][Attributes.getInputAttribute(i).getNumNominalValues()];

                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) { 

                                        nominalDistance[i][j][j] = 0.0;



                                }

                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) {

                                        for (l=j+1; l<Attributes.getInputAttribute(i).getNumNominalValues(); l++) {

                                                VDM = 0.0;

                                                Nax = Nay = 0;

                                                for (m=0; m<training.getNumInstances(); m++) {

                                                        if (nominalTrain[m][i] == j) {

                                                                Nax++;



                                                        }

                                                        if (nominalTrain[m][i] == l) {

                                                                Nay++;





                                                        }

                                                }

                                                for (m=0; m<nClases; m++) {

                                                        Naxc = Nayc = 0;

                                                        for (n=0; n<training.getNumInstances(); n++) {

                                                                if (nominalTrain[n][i] == j && clasesTrain[n] == m) {

                                                                        Naxc++;



                                                                }

                                                                if (nominalTrain[n][i] == l && clasesTrain[n] == m) {

                                                                        Nayc++;





                                                                }

                                                        }

                                                        VDM += (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay)) * (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay));



                                                }

                                                nominalDistance[i][j][l] = Math.sqrt(VDM);

                                                nominalDistance[i][l][j] = Math.sqrt(VDM);







                                        }

                                }

                        } else {

                                media = 0;

                                SD = 0;

                                for (j=0; j<training.getNumInstances(); j++) {

                                        media += realTrain[j][i];

                                        SD += realTrain[j][i]*realTrain[j][i];



                                }

                                media /= (double)realTrain.length;

                                stdDev[i] = Math.sqrt((SD/((double)realTrain.length)) - (media*media));







                        }

                }

            }    



           

	} //end-method



	/** 

	 * This function builds the data matrix for reference data and normalizes inputs values

	 */	

	public void normalizar () throws CheckException {



		int i, j, k;

		Instance temp;

		double caja[];

		StringTokenizer tokens;

		boolean nulls[];



		/*Check if dataset corresponding with a classification problem*/



		if (Attributes.getOutputNumAttributes() < 1) {

		  throw new CheckException ("This dataset haven?t outputs, so it not corresponding to a classification problem.");

		} else if (Attributes.getOutputNumAttributes() > 1) {

		  throw new CheckException ("This dataset have more of one output.");

		}



		if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {

		  throw new CheckException ("This dataset have an input attribute with floating values, so it not corresponding to a classification problem.");

		}





      

        

		entradas = Attributes.getInputAttributes();

		salida = Attributes.getOutputAttribute(0);

		nEntradas = Attributes.getInputNumAttributes();

		tokens = new StringTokenizer (training.getHeader()," \n\r");

		tokens.nextToken();

		relation = tokens.nextToken();



		  

		

		datosTrain = new double[training.getNumInstances()][Attributes.getInputNumAttributes()];

		clasesTrain = new int[training.getNumInstances()];

		caja = new double[1];



		nulosTrain = new boolean[training.getNumInstances()][Attributes.getInputNumAttributes()];

		nominalTrain = new int[training.getNumInstances()][Attributes.getInputNumAttributes()];

		realTrain = new double[training.getNumInstances()][Attributes.getInputNumAttributes()];



	

		for (i=0; i<training.getNumInstances(); i++) {

			temp = training.getInstance(i);

			nulls = temp.getInputMissingValues();

			datosTrain[i] = training.getInstance(i).getAllInputValues();

			for (j=0; j<nulls.length; j++)

				if (nulls[j]) {

					datosTrain[i][j]=0.0;

					nulosTrain[i][j] = true;

				}

			//caja = training.getInstance(i).getOutputRealValues(0)  //.getAllOutputValues();

			caja[0]= training.getInstance(i).getOutputNominalValuesInt(0);

			clasesTrain[i] = (int) caja[0];

			for (k = 0; k < datosTrain[i].length; k++) {

				if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {

					nominalTrain[i][k] = (int)datosTrain[i][k]; 

					datosTrain[i][k] /= Attributes.getInputAttribute(k).

					getNominalValuesList().size() - 1;

				} else {

					realTrain[i][k] = datosTrain[i][k];

					datosTrain[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();

					datosTrain[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() -

					Attributes.getInputAttribute(k).getMinAttribute();

					if (Double.isNaN(datosTrain[i][k])){

						datosTrain[i][k] = realTrain[i][k];

			    }

				}

			}

		} 



		

		/*

        datosTest = new double[test.getNumInstances()][Attributes.getInputNumAttributes()];

        realTest = new double[test.getNumInstances()][Attributes.getInputNumAttributes()];

		clasesTest = new int[test.getNumInstances()];

        caja = new double[1];

    

        for (i=0; i<test.getNumInstances(); i++) {

			temp = test.getInstance(i);

			nulls = temp.getInputMissingValues();

			datosTest[i] = test.getInstance(i).getAllInputValues().clone();

			

			for (k = 0; k < datosTest[i].length; k++) {

				

				if (Attributes.getInputAttribute(k).getType() != Attribute.NOMINAL) {

					realTest[i][k] = datosTest[i][k];

					datosTest[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();

					datosTest[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() - Attributes.getInputAttribute(k).getMinAttribute();

					if (Double.isNaN(datosTest[i][k])){

						datosTest[i][k] = realTest[i][k];

					}

				}

		    }

			

			for (j=0; j<nulls.length; j++)

				if (nulls[j]) {

					datosTest[i][j]=0.0;

				}

			caja = test.getInstance(i).getAllOutputValues();

			clasesTest[i] = (int) caja[0];

		} 

		

		*/

                		

	} //end-method



	/** 

	 * Reads the parameters of the algorithm. 

	 * Must be implemented in the subclass.

	 * 

	 * @param ficheroScript Configuration script

	 * 

	 */

	public void leerConfiguracion (String ficheroScript) {}

	

	

	public void leerConfiguracion1 (String ficheroScript) {



	    String fichero, linea, token;

	    StringTokenizer lineasFichero, tokens;

	    byte line[];

	    int i, j;



	   ficheroSalida = new String[2];



	    fichero = Fichero.leeFichero (ficheroScript);

	    lineasFichero = new StringTokenizer (fichero,"\n\r");



	    lineasFichero.nextToken();

	    linea = lineasFichero.nextToken();



	    tokens = new StringTokenizer (linea, "=");

	    tokens.nextToken();

	    token = tokens.nextToken();



	    /*Getting the names of the training and test files*/

	    line = token.getBytes();

	    for (i=0; line[i]!='\"'; i++);

	    i++;

	    for (j=i; line[j]!='\"'; j++);

	   ficheroTraining = new String (line,i,j-i);

	    for (i=j+1; line[i]!='\"'; i++);

	    i++;

	    for (j=i; line[j]!='\"'; j++);

	   ficheroTest = new String (line,i,j-i);



	    /*Getting the path and base name of the results files*/

	    linea = lineasFichero.nextToken();

	    tokens = new StringTokenizer (linea, "=");

	    tokens.nextToken();

	    token = tokens.nextToken();



	    /*Getting the names of output files*/

	    line = token.getBytes();

	    for (i=0; line[i]!='\"'; i++);

	    i++;

	    for (j=i; line[j]!='\"'; j++);

	    ficheroSalida[0] = new String (line,i,j-i);

	    for (i=j+1; line[i]!='\"'; i++);

	    i++;

	    for (j=i; line[j]!='\"'; j++);

	   ficheroSalida[1] = new String (line,i,j-i);



	    /*Getting the number of neighbors*/

	    linea = lineasFichero.nextToken();

	    tokens = new StringTokenizer (linea, "=");

	    tokens.nextToken();

		k=Integer.parseInt(tokens.nextToken().substring(1));

	    

	    /*Getting the type of distance function*/

	    linea = lineasFichero.nextToken();

	    tokens = new StringTokenizer (linea, "=");

	    tokens.nextToken();

	    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    

	}

} //end-class


