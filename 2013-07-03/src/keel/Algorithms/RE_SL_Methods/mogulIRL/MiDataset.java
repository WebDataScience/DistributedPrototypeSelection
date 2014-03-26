package keel.Algorithms.RE_SL_Methods.mogulIRL;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.io.*;
import org.core.*;
import keel.Dataset.*;
import java.util.*;

public class MiDataset{

        public TTABLA [] datos;
    public int n_variables, n_var_estado, n_var_control, long_tabla;
        public int no_cubiertos;
        public TipoIntervalo [] extremos;
        public String fichero;
    public InstanceSet IS;
        public boolean noOutputs;
        public boolean salir;


        /** Stores in memory the contents of the data file "f" */
        public MiDataset(String f, boolean train) {
                fichero = f;
                IS = new InstanceSet();

                try {
                  processModelDataset(f, train);
                }
                catch (Exception e) {
          System.out.println("DBG: Exception in readSet");
          e.printStackTrace();
                }
        }


    public void processModelDataset(String nfejemplos, boolean train) throws IOException {
          int i, j, k;

          try {

        // Load in memory a dataset that contains a regression problem
        IS.readSet(nfejemplos,train);

                // We read the number of instances and variables
                long_tabla = IS.getNumInstances();
        n_var_estado = Attributes.getInputNumAttributes();
        n_var_control = Attributes.getOutputNumAttributes();
                no_cubiertos = long_tabla;


        // Check that there is only one output variable and
        // it is nominal

        if (n_var_control>1) {
          System.out.println("This algorithm can not process MIMO datasets");
          System.out.println("All outputs but the first one will be removed");
        }

        boolean noOutputs=false;
        if (n_var_control<1) {
          System.out.println("This algorithm can not process datasets without outputs");
          System.out.println("Zero-valued output generated");
          noOutputs=true;
        }

        n_var_control = 1;
        n_variables = n_var_estado + n_var_control;

        // Initialice and fill our own tables
                datos = new TTABLA[long_tabla];

        // Maximum and minimum of inputs/output data
                extremos = new TipoIntervalo[n_variables];
                for (i=0; i<n_variables; i++)  extremos[i] = new TipoIntervalo();

                /*check if there aren't continous attributes*/
                if(Attributes.hasNominalAttributes()) {
                        System.err.println("Mam-IRLSC can only handle real attributes." );
                        salir = true;
        }
                else {
          salir = false;
        // All values are casted into double/integer
        for (i=0, k=0; i<long_tabla; i++) {
          Instance inst = IS.getInstance(i);

                  if (inst.existsAnyMissingValue()==true) {
                          System.out.println ("This algorithm can not process missing values");
                          System.out.println ("This algorithm don't use the instance " + (i+1) + ". You have to apply before a preprocess method");
                  }
                  else {
                        datos[k] = new TTABLA(n_variables);
                        for (j=0; j<n_var_estado; j++)  {
                                datos[k].ejemplo[j] = IS.getInputNumericValue(i,j);
                                if (datos[k].ejemplo[j]>extremos[j].max || k==0) extremos[j].max = datos[k].ejemplo[j];
                                if (datos[k].ejemplo[j]<extremos[j].min || k==0) extremos[j].min = datos[k].ejemplo[j];
                        }

                        if (noOutputs)  datos[k].ejemplo[j] = 0;
                        else  datos[k].ejemplo[j] = IS.getOutputNumericValue(i,0);
                        if (datos[k].ejemplo[j] > extremos[j].max || k==0) extremos[j].max = datos[k].ejemplo[j];
                        if (datos[k].ejemplo[j] < extremos[j].min || k==0) extremos[j].min = datos[k].ejemplo[j];
                        k++;
                  }
        }

                long_tabla = k;
       }
      } catch (Exception e) {
        System.out.println("DBG: Exception in readSet");
        e.printStackTrace();
      }
        }

    /** It returns the header */
        public String getCabecera () {
                return (IS.getHeader());
        }

        public void nuevaTabla(){
                // Initialice and fill our own tables
                datos = new TTABLA[long_tabla];

        // All values are casted into double/integer
        for (int i=0, k=0, j = 0; i<long_tabla; i++) {
          Instance inst = IS.getInstance(i);
                        datos[k] = new TTABLA(n_variables);
                        for (j=0; j<n_var_estado; j++)  {
                                datos[k].ejemplo[j] = IS.getInputNumericValue(i,j);
                                if (datos[k].ejemplo[j]>extremos[j].max || k==0) extremos[j].max = datos[k].ejemplo[j];
                                if (datos[k].ejemplo[j]<extremos[j].min || k==0) extremos[j].min = datos[k].ejemplo[j];
                        }

                        if (noOutputs)  datos[k].ejemplo[j] = 0;
                        else  datos[k].ejemplo[j] = IS.getOutputNumericValue(i,0);
                        if (datos[k].ejemplo[j]>extremos[j].max) extremos[j].max = datos[k].ejemplo[j];
                        if (datos[k].ejemplo[j]<extremos[j].min) extremos[j].min = datos[k].ejemplo[j];
                        k++;
                  }
    }

}
