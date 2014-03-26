package map_reduce_dps; 

import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

import keel.Algorithms.Preprocess.Basic.CheckException;
import keel.Algorithms.Preprocess.Basic.KNN;
import keel.Algorithms.Preprocess.Basic.Metodo;
import keel.Algorithms.Preprocess.Basic.OutputIS;
import keel.Algorithms.Preprocess.Basic.Referencia;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.DatasetException;
import keel.Dataset.FormatErrorKeeper;
import keel.Dataset.HeaderFormatException;
import keel.Dataset.Instance;
import keel.Dataset.InstanceParser;
import keel.Dataset.InstanceSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.core.Fichero;



public class use_medeto {
		
		


public	static String config_file="";
public	static String Input_file="";
public	static String Output_file="";
		
			
		
	  public static int num_class =3 ;
	 
	 //regular code for PS
	
	public static class Map extends
			Mapper<LongWritable, Text, IntWritable,Text > {
		private static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			if(!line.matches("(.*)@(.*)")) {
				one = new IntWritable((int)(Math.random()*num_class));
			//StringTokenizer tokenizer = new StringTokenizer(line);
			word.set(line);
			context.write(one,word);		
			}
		}
	}
	
	

public static class Reduce extends Reducer<IntWritable,Text, IntWritable, DoubleWritable> {
	
	  //add these two
	  static boolean isTrain = true;
	  static FormatErrorKeeper errorLogger = new FormatErrorKeeper();
	  Metodo schema;
  public void reduce(IntWritable key, Iterable<Text> values, Context context) 
    throws IOException, InterruptedException {
  	

	  System.out.println("Inside Reducer key : " +key);
	  
	  System.out.println(config_file);
			schema = new Metodo("/home/hadoop/2013-07-03/src/map_reduce_dps/dps.config",0);
	
	//schema = new Metodo(config_file);
	  
	  schema.training = new InstanceSet();
	  
	// if (key.get() == 0) {
	  
		 System.out.println("Working if statement");
	  
	  Attributes att = new Attributes();
	  
	  att.clearAll();
	     
	     System.out.println ("Opening the file: "+schema.ficheroTraining+".");
	     //Parsing the header of the DB.
	     errorLogger = new FormatErrorKeeper();
	     
	     //Declaring an instance parser
	     InstanceParser parser = new InstanceParser( schema.ficheroTraining, true );
	     
	     // Reading information in the header, i.e., @relation, @attribute, @inputs and @outputs
	     schema.training.parseHeader ( parser, isTrain );
	     
	     System.out.println ( " The number of output attributes is: " + att.getOutputNumAttributes() );
	     
	     
	     //The attributes statistics are init if we are in train mode.
	     if (isTrain && att.getOutputNumAttributes() == 1){
	         att.initStatistics();
	    }	
	     
	     
	     
	 //    System.out.println("Number of attributes : " + att.getNumAttributes() + "	Number of input attributes : " + att.getInputNumAttributes());
	     
	     
	// }
	     
	     
	     
	     
	     // Now add the data for each key
	     System.out.println ( "\n\n  > Reading the data from key: " + key);
	       Vector tempSet=new Vector(10000,100000);
	       
	       
	     String line;


	   	
	       int sum = 0;
	       for (Text val : values) {
	       	line = val.toString();
	       	
	       	
	       	//System.out.println(tempSet.size());
	       	
	        tempSet.addElement( new Instance( line, isTrain, tempSet.size()) );
	           sum ++;
	       }
	       
	       //The vector of instances is converted to an array of instances.
	       int sizeInstance=tempSet.size();
	       System.out.println ("    > Number of instances read: "+tempSet.size());
	       
	       
	       schema.training.instanceSet=new Instance[sizeInstance];
	       for ( int i=0; i<sizeInstance; i++) {
	       
	    	   schema.training.instanceSet[i]=(Instance)tempSet.elementAt(i);
	       }
	       
	       
	       //System.out.println("After converting all instances");
	       
	      	//System.out.println("The error logger has any error: "+errorLogger.getNumErrors()); 
	          if (errorLogger.getNumErrors() > 0){
	              
	      		System.out.println ("There has been "+errorLogger.getAllErrors().size()+
	                                          " errors in the Dataset format.");
	      		for (int k=0;k<errorLogger.getNumErrors();k++){
	      			errorLogger.getError(k).print();
	      		}
	      		try {
	   				throw new DatasetException("There has been "+errorLogger.getAllErrors().size()+
	   				                            " errors in the Dataset format", errorLogger.getAllErrors());
	   			} catch (DatasetException e) {
	   				// TODO Auto-generated catch block
	   				e.printStackTrace();
	   			}
	          }
	          
	          
	          
	         	System.out.println ("\n  > Finishing the statistics: (isTrain)"+isTrain+", (# out attributes)"+Attributes.getOutputNumAttributes());
	            //If being on a train dataset, the statistics are finished
	            if (isTrain && Attributes.getOutputNumAttributes() == 1){ 
	                Attributes.finishStatistics();
	            }
	            
	           
	            System.out.println ("  >> File LOADED CORRECTLY!!");
	            
		  
	            
	            // Intialize normalizer
	            
	            try {
	    			schema.normalizar();
	    		} catch (CheckException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	            
	            
	            // required variables from Metodo class
	            
	    		int nClases, i, j, l, m, n;
	    		double VDM;
	    		int Naxc, Nax, Nayc, Nay;
	    		double media, SD;	
	         	schema.distanceEu = false; 	 

	            /*Previous computation for HVDM distance*/
	            if (schema.distanceEu == false) {    	
	            	schema.stdDev = new double[Attributes.getInputNumAttributes()];
	            	schema.nominalDistance = new double[Attributes.getInputNumAttributes()][][];
	            	nClases = Attributes.getOutputAttribute(0).getNumNominalValues();
	                     
	                        
	                for (i=0; i<schema.nominalDistance.length; i++) {
	                        if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
	                        	schema.nominalDistance[i] = new double[Attributes.getInputAttribute(i).getNumNominalValues()][Attributes.getInputAttribute(i).getNumNominalValues()];
	                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) { 
	                                	schema.nominalDistance[i][j][j] = 0.0;

	                                }
	                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) {
	                                        for (l=j+1; l<Attributes.getInputAttribute(i).getNumNominalValues(); l++) {
	                                        	VDM = 0.0;
	                                        	Nax = Nay = 0;
	                                                for (m=0; m<schema.training.getNumInstances(); m++) {
	                                                        if (schema.nominalTrain[m][i] == j) {
	                                                                Nax++;

	                                                        }
	                                                        if (schema.nominalTrain[m][i] == l) {
	                                                                Nay++;


	                                                        }
	                                                }
	                                                for ( m=0; m<nClases; m++) {
	                                                        Naxc = Nayc = 0;
	                                                        for (n=0; n<schema.training.getNumInstances(); n++) {
	                                                                if (schema.nominalTrain[n][i] == j && schema.clasesTrain[n] == m) {
	                                                                        Naxc++;

	                                                                }
	                                                                if (schema.nominalTrain[n][i] == l && schema.clasesTrain[n] == m) {
	                                                                        Nayc++;


	                                                                }
	                                                        }
	                                                        VDM += (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay)) * (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay));

	                                                }
	                                                schema.nominalDistance[i][j][l] = Math.sqrt(VDM);
	                                                schema.nominalDistance[i][l][j] = Math.sqrt(VDM);



	                                        }
	                                }
	                        } else {
	                        	media = 0;
	                                SD = 0;
	                                for (j=0; j<schema.training.getNumInstances(); j++) {
	                                        media += schema.realTrain[j][i];
	                                        SD += schema.realTrain[j][i]*schema.realTrain[j][i];

	                                }
	                                media /= (double)schema.realTrain.length;
	                                schema.stdDev[i] = Math.sqrt((SD/((double)schema.realTrain.length)) - (media*media));



	                        }
	                }
	            } 
	            
	            ejecutar(key.toString());
      
      
  }
	
  
  private int k;
  
  
  public void ejecutar (String key) {

	    int i, j, l, m, n, o;
	    int nClases;
	    int claseObt;
	    boolean marcas[];
	    int nSel;
	    double conjS[][];
	    double conjR[][];
	    int conjN[][];
	    boolean conjM[][];
	    int clasesS[];
	    int vecinos[][];
	    Vector asociados[];
	    int aciertosSin;
	    int vecinosTemp[];
	    double distTemp[];
	    double dist, bestD;
	    boolean parar;
	    Referencia orden[];
	    int mayoria;

	    long tiempo = System.currentTimeMillis();

	    /*Getting the number of different classes*/
	    nClases = 0;
	    for (i=0; i<schema.clasesTrain.length; i++)
	      if (schema.clasesTrain[i] > nClases)
	        nClases = schema.clasesTrain[i];
	    nClases++;

	    /*Inicialization of the instance flagged vector of the S set*/
	    marcas = new boolean[schema.datosTrain.length];
	    for (i=0; i<schema.datosTrain.length; i++) {
	      marcas[i] = true;
	    }
	    nSel = schema.datosTrain.length;
		k = schema.k;
	    /*Do ENN before sorting*/
	    for (i=0; i<schema.datosTrain.length; i++) {
	      claseObt = KNN.evaluacionKNN2 (k, schema.datosTrain, schema.realTrain, schema.nominalTrain, schema.nulosTrain, schema.clasesTrain, schema.datosTrain[i], schema.realTrain[i], schema.nominalTrain[i], schema.nulosTrain[i], nClases, schema.distanceEu);
	      if (claseObt != schema.clasesTrain[i]) { //is included in the solution set if it is agree with your mayority
	        marcas[i] = false;
	        nSel--;
	      }
	    }

	    /*Construction of an instance vector with distances to the nearest enemy*/
	    orden = new Referencia[schema.datosTrain.length];
	    for (i=0; i<schema.datosTrain.length; i++) {
	      bestD = Double.POSITIVE_INFINITY;
	      for (j=0; j<schema.datosTrain.length; j++) {
	        if (schema.clasesTrain[i] != schema.clasesTrain[j]) {
	          dist = KNN.distancia (schema.datosTrain[i], schema.realTrain[i], schema.nominalTrain[i], schema.nulosTrain[i], schema.datosTrain[j], schema.realTrain[j], schema.nominalTrain[j], schema.nulosTrain[j], schema.distanceEu);
	          if (dist < bestD)
	            bestD = dist;
	        }
	      }
	      orden[i] = new Referencia (i, bestD);
	    }

	    /*Sorting the previous vector*/
	    Arrays.sort(orden);

	    /*Inicialization of the data structures of neighbors and associates*/
	    distTemp = new double[k+1];
	    vecinosTemp = new int[k+1];
	    vecinos = new int[schema.datosTrain.length][k+1];
	    asociados = new Vector[schema.datosTrain.length];
	    for (i=0; i<schema.datosTrain.length; i++)
	      asociados[i] = new Vector ();

	    /*Body of the algorithm DROP3 (same as DROP2).*/
	    for (i=0; i<schema.datosTrain.length; i++) {
	      /*Get the k+1 nearest neighbors of each instance*/
	      if (marcas[i]) {
	        KNN.evaluacionKNN2 (k+1, schema.datosTrain, schema.realTrain, schema.nominalTrain, schema.nulosTrain, schema.clasesTrain, schema.datosTrain[i], schema.realTrain[i], schema.nominalTrain[i], schema.nulosTrain[i], nClases, schema.distanceEu, vecinos[i]);
	        for (j=0; j<vecinos[i].length; j++) {
	          if (vecinos[i][j] >= 0)
	        	  asociados[vecinos[i][j]].addElement (new Referencia (i,0));
	        }
	      }
	    }

	    /*Check if delete or not the instances considering the WITH and WITHOUT sets*/
	    for (o=0; o<schema.datosTrain.length; o++){
	      i = orden[o].entero;
	      if (marcas[i]) { //only for instances havent noise filtered
	        aciertosSin = 0;

	        marcas[i] = false;
	        nSel--;
	        /*Construction of S set from the temporaly flags*/
	        conjS = new double[nSel][schema.datosTrain[0].length];
	        conjR = new double[nSel][schema.datosTrain[0].length];
	        conjN = new int[nSel][schema.datosTrain[0].length];
	        conjM = new boolean[nSel][schema.datosTrain[0].length];
	        clasesS = new int[nSel];
	        for (m=0, l=0; m<schema.datosTrain.length; m++) {
	          if (marcas[m]) { //the instance will evaluate
	            for (j=0; j<schema.datosTrain[0].length; j++) {
	              conjS[l][j] = schema.datosTrain[m][j];
	              conjR[l][j] = schema.realTrain[m][j];
	              conjN[l][j] = schema.nominalTrain[m][j];
	              conjM[l][j] = schema.nulosTrain[m][j];
	            }
	            clasesS[l] = schema.clasesTrain[m];
	            l++;
	          }
	        }

	        marcas[i] = true;
	        nSel++;

	        /*Evaluation of associates without the instance in T*/
	        for (j=0; j<k+1; j++) {
	          if (vecinos[i][j] >= 0) {
	        	  claseObt = KNN.evaluacionKNN2 (k, conjS, conjR, conjN, conjM, clasesS, schema.datosTrain[vecinos[i][j]], schema.realTrain[vecinos[i][j]], schema.nominalTrain[vecinos[i][j]], schema.nulosTrain[vecinos[i][j]], nClases, schema.distanceEu);
	        	  if (claseObt == schema.clasesTrain[vecinos[i][j]])  //classify it correctly
	        		  aciertosSin++;
	          }
	        }

	        mayoria = (k+1) / 2;
	        if (aciertosSin > mayoria) {
	          /*Delete P from S*/
	          marcas[i] = false;
	          nSel--;

	          /*For each associate of P, search a new nearest neighbor*/
	          for (j=0; j<asociados[i].size(); j++) {
	            for (l=0; l<k+1; l++) {
	              vecinosTemp[l] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l];
	              vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l] = -1;
	              distTemp[l] = Double.POSITIVE_INFINITY;
	            }
	            for (l=0; l<schema.datosTrain.length; l++) {
	              if (marcas[l]) { //it is in S
	                dist = KNN.distancia (schema.datosTrain[((Referencia)(asociados[i].elementAt(j))).entero], schema.realTrain[((Referencia)(asociados[i].elementAt(j))).entero], schema.nominalTrain[((Referencia)(asociados[i].elementAt(j))).entero], schema.nulosTrain[((Referencia)(asociados[i].elementAt(j))).entero], schema.datosTrain[l], schema.realTrain[l], schema.nominalTrain[l], schema.nulosTrain[l], schema.distanceEu);
	                parar = false;

	                /*Calculate the nearest neighbors in this situation again*/
	                for (m=0; m<(k+1) && !parar; m++) {
	                  if (dist < distTemp[m]) {
	                    parar = true;
	                    for (n=m+1; n<k+1; n++) {
	                      distTemp[n] = distTemp[n-1];
	                      vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n-1];
	                    }
	                    distTemp[m] = dist;
	                    vecinos[((Referencia)(asociados[i].elementAt(j))).entero][m] = l;
	                  }
	                }
	              }
	            }

	            /*Add to the list of associates of the new neighbor this instance*/
	            for (l=0; l<k+1; l++) {
	              parar = false;
	              for (m=0; m<asociados[vecinosTemp[l]].size() && !parar; m++) {
	                if (((Referencia)(asociados[vecinosTemp[l]].elementAt(m))).entero == ((Referencia)(asociados[i].elementAt(j))).entero
	                    && vecinosTemp[l] != i) {
	                  asociados[vecinosTemp[l]].removeElementAt(m);
	                  parar = true;
	                }
	              }
	            }
	            for (l=0; l<k+1; l++) {
	              asociados[vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l]].addElement(new Referencia (((Referencia)(asociados[i].elementAt(j))).entero,0));
	            }
	          }
	        }
	      }
	    }

	    /*Construction of the S set from the flags*/
	    System.out.println(nSel);
	    System.out.println(schema.datosTrain[1].length);
	    conjS = new double[nSel][schema.datosTrain[0].length];
	    conjR = new double[nSel][schema.datosTrain[0].length];
	    conjN = new int[nSel][schema.datosTrain[0].length];
	    conjM = new boolean[nSel][schema.datosTrain[0].length];
	    clasesS = new int[nSel];
	    for (m=0, l=0; m<schema.datosTrain.length; m++) {
	      if (marcas[m]) { //the instance will evaluate
	        for (j=0; j<schema.datosTrain[0].length; j++) {
	          conjS[l][j] = schema.datosTrain[m][j];
	          conjR[l][j] = schema.realTrain[m][j];
	          conjN[l][j] = schema.nominalTrain[m][j];
	          conjM[l][j] = schema.nulosTrain[m][j];
	        }
	        clasesS[l] = schema.clasesTrain[m];
	        l++;
	      }
	    }

	    System.out.println("DROP3 "+ schema.relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");
	    
	    System.out.println(conjR+" "+ conjN + " "+ conjM + " "+ clasesS + " " + schema.entradas + " "+schema.salida + " "+ schema.nEntradas);

	    try {
			OutputIS.escribeSalida(schema.ficheroSalida[0]+"_"+key, conjR, conjN, conjM, clasesS, schema.entradas, schema.salida, schema.nEntradas, schema.relation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     // OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
	  }

  
  
  public void leerConfiguracion (String ficheroScript) {

	    String fichero, linea, token;
	    StringTokenizer lineasFichero, tokens;
	    byte line[];
	    int i, j;

	    schema.ficheroSalida = new String[2];

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
	    schema.ficheroTraining = new String (line,i,j-i);
	    for (i=j+1; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    schema.ficheroTest = new String (line,i,j-i);

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
	    schema.ficheroSalida[0] = new String (line,i,j-i);
	    for (i=j+1; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    schema.ficheroSalida[1] = new String (line,i,j-i);

	    /*Getting the number of neighbors*/
	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    Integer.parseInt(tokens.nextToken().substring(1));
	    
	    /*Getting the type of distance function*/
	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    schema.distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
	}


	
	
}


public use_medeto (String config,String Input,String Output) {
	

	
	config_file = config	;
	Input_file = Input;
	Output_file = Output;
	
	
}


	public static void service () throws IOException, ClassNotFoundException, InterruptedException {
		
		
		 
		 
		 System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				  "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		 
	  Configuration hadoop_conf = new Configuration();
	  
	  
	       Job job = new Job(hadoop_conf, "urloutstats1");
	       
	      
	   job.setOutputKeyClass(IntWritable.class);
	   job.setOutputValueClass(Text.class);
	       
	   job.setMapperClass(Map.class);
	   job.setReducerClass(Reduce.class);
	       
	   job.setInputFormatClass(TextInputFormat.class);
	   job.setOutputFormatClass(TextOutputFormat.class);
	       
	  // FileInputFormat.addInputPath(job, new Path("/Users/naren/testdata/adult_data/adult_train.dat"));
	  // FileOutputFormat.setOutputPath(job, new Path("/Users/naren/testdata/map_output/result" + Math.random()*10000));
	  

	//for connect4 
	 //FileInputFormat.addInputPath(job, new Path("ucc_data/connect-4_train.dat"));
	  // FileOutputFormat.setOutputPath(job, new Path("ucc_data/results" + Math.random()*10000));
	            
	// for adult
	  // FileInputFormat.addInputPath(job, new Path("adult_for_ps/adult_trim_train.dat"));
          // FileOutputFormat.setOutputPath(job, new Path("adult_for_ps/results" +Math.random()*10000));

           FileInputFormat.addInputPath(job, new Path(Input_file));
           FileOutputFormat.setOutputPath(job, new Path(Output_file+Math.random()*10000));


	      job.setJarByClass(use_medeto.class);
	      
	      System.out.println(job.getJar());
	   
	       
	   job.waitForCompletion(true);
	   
	 }

}

