import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.ModelSerializer;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.utils.vectors.io.SequenceFileVectorWriter;
import org.apache.mahout.utils.vectors.io.VectorWriter;
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder;
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;


public class Logistic_regression {


//variable

static List<String> keys = new ArrayList<String>();
static List<FeatureVectorEncoder> encoder = new ArrayList<FeatureVectorEncoder>();
static List<RandomAccessSparseVector> vec = new ArrayList<RandomAccessSparseVector>();
static List<Boolean> c_C= new ArrayList<Boolean>();
static List<String> estimate = new ArrayList<String>();
static List<String> estimate_test = new ArrayList<String>();
static List<NamedVector> list_vec = new ArrayList<NamedVector>();
static OnlineLogisticRegression learningAlgorithm; 


static Map<String,Integer> map_tar = new HashMap<String,Integer>();
static int num_tar=17;
static int save = 1;

static URI path1;
static URI path2;
static URI path3;
static URI path4;
static URI path43;

static Configuration hadoop_conf;
static FileSystem fs;
static String dirName = "test_tmp1";
static Path src;
static SequenceFile.Writer writer;
static SequenceFile.Writer writer_name;
static SequenceFile.Writer writer_test_name;


public static void make_vectors (String col_att, String train, String vec_out,String test, String target_variable) throws IOException {


BufferedReader reader_col = new BufferedReader(new FileReader(col_att));
BufferedReader reader_data = new BufferedReader(new FileReader(train));
BufferedWriter write_vec = new BufferedWriter (new FileWriter(vec_out));

Map<String, Set<Integer>> traceDictionary =
new TreeMap<String, Set<Integer>>();

path1 = URI.create("/tmp/connect_train");
path2 = URI.create("/tmp/connect_name_train");
path3 = URI.create("/tmp/connect_name_test");

path43 = URI.create("/tmp");

// String path = "/tmp/vec_seq";
//URI path = URI.create("file:/tmp/vec.seq");
 hadoop_conf = new Configuration() ;

 hadoop_conf.set("fs.default.name","hdfs://glisco.multicare.org/");

 hadoop_conf.set("mapred.job.tracker", "glisco.mutlicare.org");


 System.out.println(hadoop_conf);
  fs = FileSystem.get(hadoop_conf);



   src = new Path(fs.getWorkingDirectory()+"/"+dirName);

   fs.mkdirs(src);

   System.out.println(fs.getHomeDirectory());
   System.out.println(fs.getWorkingDirectory()) ;
   System.out.println(fs.getUri());

    writer = SequenceFile.createWriter(fs, hadoop_conf,new Path(path1), IntWritable.class,VectorWritable.class);
     
      writer_name = SequenceFile.createWriter(fs, hadoop_conf,new Path(path2), LongWritable.class, VectorWritable.class);


      VectorWritable vec_wri = new VectorWritable();



      VectorWriter vw = new SequenceFileVectorWriter(writer_name);


      String key= reader_col.readLine();

      NamedVector tmp_vec;

      int i=0;


      while(key!=null && key.length()>0){
      //Make an array of keys

      String[] temps = key.split("\\s+");





      if (!temps[1].equals(target_variable)) {

      keys.add(temps[1]);
      if (temps[2].matches("(.*)numeric(.*)")) {
      encoder.add(new ConstantValueEncoder(key));
      c_C.add(true);

      }
      else {
      encoder.add(new StaticWordValueEncoder(key));
      encoder.get(i).setProbes(2);
      c_C.add(false);
      }
      encoder.get(i).setTraceDictionary(traceDictionary);


      i++;

      }
      else 
      {

      //System.out.println(temps[1]);
      save =i;

      System.out.println("target: " + save);

      }
      key = reader_col.readLine();


      }
      //RandomAccessSparseVector vec = new RandomAccessSparseVector(keys.size());


      learningAlgorithm=
      new OnlineLogisticRegression(
      num_tar,keys.size() , new L1())
      .alpha(1).stepOffset(1000)
      .decayExponent(0.9)
      .lambda(3.0e-5)
      .learningRate(40);







      //AdaptiveLogisticRegression learningAlgorithm = new AdaptiveLogisticRegression(4,keys.size(), new L1());

      String line = reader_data.readLine();

      String actual=null;
      int limit=0;

      while(line!=null && line.length()>0 ) {
      limit++;
      String[] tmp_words = line.split(",");
      RandomAccessSparseVector vec_in = new RandomAccessSparseVector(keys.size());
      //actual = (int)(Math.random() * 2);


      int j=0;
      for(i =0 ;i<tmp_words.length;i++) {

      if(save!=i) {
      encoder.get(j).addToVector(tmp_words[i],vec_in ); j++;
      }
      else {
      estimate.add(tmp_words[i]);
      actual = tmp_words[i];


      if(!map_tar.isEmpty()) {

      if(!map_tar.containsKey(actual) ) {
      map_tar.put(actual, map_tar.size()+1);

      }
      }
      else {
      map_tar.put(actual, 1);
      }

      }

      }
      //list of all the vectors
      //vec.add(vec_in);




      vec_wri.set(vec_in);
      writer.append(new IntWritable(map_tar.get(actual)),vec_wri);




      tmp_vec = new NamedVector(vec_in,actual);
      //list of all the named vectors
      // list_vec.add(tmp_vec);

      vw.write(tmp_vec);

      // System.out.println(vec_in);
      // System.out.printf("%s\n", new SequentialAccessSparseVector(vec_in));
      line = reader_data.readLine();
      // System.out.println(actual + " mapped value" + map_tar.get(actual));
      learningAlgorithm.train(map_tar.get(actual),tmp_vec);
      // learningAlgorithm.train(99,vec_in);

      //write_vec.write(vec_in.toString()+"\n");

      } 
      System.out.println("Train instances " + limit);

      reader_col.close();
      reader_data.close();
      write_vec.close();





      //make test vectors

      BufferedReader reader_test_data = new BufferedReader(new FileReader(test));

      line = reader_test_data.readLine();

      writer_test_name = SequenceFile.createWriter(fs, hadoop_conf,new Path(path3), 
                   LongWritable.class,
		                VectorWritable.class);


				VectorWriter vw_test = new SequenceFileVectorWriter(writer_test_name);


				BufferedWriter write_test_vec = new BufferedWriter (new FileWriter(vec_out+"_test"));


				limit =0;
				while(line!=null && line.length()>0 ) {
				limit++;
				String[] tmp_words = line.split(",");
				RandomAccessSparseVector vec_in = new RandomAccessSparseVector(keys.size());
				//actual = (int)(Math.random() * 2);


				int j=0;
				for(i =0 ;i<tmp_words.length;i++) {

				if(save!=i) {
				//System.out.println("J: "+ j + " tmp_words[i] " + tmp_words[i]);
				encoder.get(j).addToVector(tmp_words[i],vec_in ); j++;
				}
				else {
				estimate_test.add(tmp_words[i]);
				actual = tmp_words[i];


				if(!map_tar.isEmpty()) {

				if(!map_tar.containsKey(actual) ) {
				map_tar.put(actual, map_tar.size()+1);

				}
				}
				else {
				map_tar.put(actual, 1);
				}

				}

				}
				//list of all the vectors
				//vec.add(vec_in);




				//vec_wri.set(vec_in);
				//writer_test_name.append(new IntWritable(map_tar.get(actual)),vec_wri);


				//System.out.println(actual + " mapped value" + map_tar.get(actual));

				tmp_vec = new NamedVector(vec_in,actual);
				//list of all the named vectors
				// list_vec.add(tmp_vec);

				vw_test.write(tmp_vec);

				// System.out.println(vec_in);
				// System.out.printf("%s\n", new SequentialAccessSparseVector(vec_in));
				line = reader_test_data.readLine();
				// System.out.println(actual);
				//learningAlgorithm.train(map_tar.get(actual),vec_in);
				// learningAlgorithm.train(99,vec_in);

				// write_test_vec.write(vec_in.toString()+"\n");

				} 



				String model_path = "";

				ModelSerializer.writeBinary("./model", learningAlgorithm);

				learningAlgorithm.close();

				}





				/**
				* @param args
				* arg0 = attribute file name
				* args1 = training data file
				* args2  = tmp vectors file
				* args3 = testing data file
				* arge4 = target field
				* @throws IOException 
				*/
				public static void main(String[] args) throws IOException {
				// TODO Auto-generated method stub



				//make_vectors("connect-4-att.txt","connect-4.txt","vec_connect-4.txt", "43");

				make_vectors(args[0],args[1],args[2],args[3],args[4]);

				writer.close();
				writer_name.close();
				writer_test_name.close();


				learningAlgorithm = ModelSerializer.readBinary(new
				FileInputStream("./model"), OnlineLogisticRegression.class);



				System.out.println("Cat " + learningAlgorithm.numCategories());

				SequenceFile.Reader reader = new SequenceFile.Reader(fs,new Path(path3),hadoop_conf);
				LongWritable kay = new LongWritable();
				VectorWritable value = new VectorWritable();

				int j=0;
				int k =0;
				long actual;

				int tp[],tn[],fp[],fn[];


				double averageCorrect=0,averageLL=0;
				while(reader.next(kay, value)){
				NamedVector vec_tmp = (NamedVector) value.get();
				//System.out.println(v.asFormatString());




				actual = map_tar.get(vec_tmp.getName());
				// System.out.println(vec_tmp.getName());
				double mu = Math.min(50, 100);
				double ll = learningAlgorithm.logLikelihood((int)actual, vec_tmp);
				averageLL = averageLL + (ll - averageLL) / mu;
				DenseVector p = new DenseVector(num_tar);
				learningAlgorithm.classifyFull(p, vec_tmp);
				int estimated = p.maxValueIndex();
				System.out.println("Actual " + actual + "   Estimate " + estimated);
				int correct = (estimated == actual? 1 : 0);
				averageCorrect = averageCorrect + (correct - averageCorrect) / mu;
				k=k+correct;
				j++;





				}

				System.out.println("Correctly predicted" +k);
				System.out.println("Total Instances" +j);

				learningAlgorithm.close();

				}

				}


