package map_reduce_dps;

import java.io.IOException;

import keel.Dataset.DatasetException;
import keel.Dataset.HeaderFormatException;

public class run {

	/**
	 * @param args
	 * @throws HeaderFormatException 
	 * @throws DatasetException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * args[0] = config on regular file system, args[1] = input file in hdfs, args[2] = outputfile in hdfs
	 */
	public static void main(String[] args) throws DatasetException, HeaderFormatException, ClassNotFoundException, IOException, InterruptedException   {
		// TODO Auto-generated method stub

	//	use_medeto dps = new use_medeto("/Users/naren/Documents/workspace/2013-07-03/config0_ENN.txt");
	//	use_medeto dps = new use_medeto("/Users/naren/Documents/workspace/2013-07-03/src/dps/adult_ps_hadoop_config.txt","adult_for_ps/adult_trim_train.dat","adult_for_ps/results");	
		


		use_medeto dps = new use_medeto(args[0],args[1],args[2]);
	
		 //ps_CPruner dps = new ps_CPruner(args[0],args[1],args[2]);

		dps.service();
		
	}

}

