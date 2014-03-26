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
 * @author Writed by Pedro Gonz�lez (University of Jaen) 22/08/2004
 * @author Modified by Pedro Gonz�lez (University of Jaen) 4/08/2007
 * @author Modified by Crist�bal J. Carmona (University of Jaen) 30/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.MESDIF;

import org.core.*;

public class Genetic {
    /**
     * <p>
     * Methods to define the genetic algorithm and to apply operators and reproduction schema
     * </p>
     */

    private Population poblac;          // Main Population
    private int long_poblacion;         // Number of individuals of the population

    private Population elite;           /* Elite Set */
    private int long_elite;             // Number of individuals of the elite population
    private int used_elite;             /* Number of different individuals in the final elite set */

    private Population temporal;        /* Temporal population (max elite plus poblac) to compute fitness */
    private int used_temp;              /* Number of individuals in the temporal population */

    private Population Inter;           // Local Inter population generated by applying genetic ops
    private Population Des;             // Local Population generated by cross and reproduction ops
    private int descendientes;          // Number of descendants generated in the iteration of the GA

    private float cross_prob;           // Cross probability
    private float mut_prob;             // Mutation probability
    private int Mu_next;                // Next gen to mutate
    private float min_conf;             // Minimun value for the confidence

    private int Gen;                    // Number of generations performed by the GA
    private int n_eval;                 // Maximun number of evaluations per ejecution
    private int Trials;                 // Number of chromosomes evaluated so far

    private String RulesRep = "CAN";    // Rules representation

    private String Obj[];               // Names of the quality measures used as objectives


    
    /**
     * <p>
     * Creates a new instance of Genetic
     * </p>
     */
    public Genetic() {
    }
    
    
    /**
     * <p>
     * JoinTemp
     * <p>
     * Joins Elite population with Population into temporal population
     * Previous contents are lost
     */
    public void JoinTemp() {

	/* First we copy elite population individuals in temporal population */
	for (int i=0;i<long_elite;i++) {
            temporal.copyIndiv(i, elite.getIndiv(i));
	}

	/* Set the number of individuals in temporal */
	used_temp = used_elite;

	/* Now we add "poblacion" population individuals to "temporal" population*/
	for (int i=0;i<long_poblacion;i++) {
            temporal.copyIndiv(used_temp+i, poblac.getIndiv(i));
	}
    // Update number of individuals in temporal
	used_temp += long_poblacion;

    }


    /**
     * <p>
     * CompletaElite
     * <p>
     * Stores in elite the better individuals of temporal
     *    It is suposed here there that are at last the number of individulas that fit on elite
     */
    public void CompletaElite () {
        int i,j, best;
        int posicion[]= new int[used_temp];
        int pos[]= new int[used_temp];
        float perf;

        // First sort the individuals of temporal by their fitness
        /* "Posicion" used to store the ordered position of the individual in the population */
        for (i=0;i<used_temp;i++) {
            posicion[i] = -1; /* Not really necesary */
        }
        /* Compute the ranking of each individual of the population: elemento 0 is the better */
        for (i=0;i<used_temp;i++) {
            /* Find better individual of the rest */
            best=-1;
            perf=0;
            for (j=0;j<used_temp;j++) {
                if (posicion[j]==-1 && (best==-1 || temporal.getIndivFitness(j)< perf)) {
                    perf = temporal.getIndivFitness(j);
                    best = j;
                }
            }
            /* Mark the individual with the ranking */
            posicion[best] = i;
        }

        // Next invert to obtain the selection order of the individuals
        for (int f=0; f<pos.length; f++)
            pos[posicion[f]]=f;

        // Now they are copied in fitness order until the number of elements of elite
        for (i=0;i<long_elite;i++) {
            elite.copyIndiv(i, temporal.getIndiv(pos[i]));
        }

        // Finally, the number of elements in size is "long_elite" again
        used_elite = long_elite;

    }


    /**
     * <p>
     * TruncaElite
     * <p>
     * Stores in elite part of the non-dominated individuals of temporal
     *    This is because temporal contains more non-dominated that fits in elite
     */
    public void TruncaElite () {
    	int numInter=0;
    	double MAXDOUBLE = 99999999;  //A large number used for sorting

        // Copy non-dominated individuals of temporal in Inter
        for (int x=0;x<used_temp;x++) {
            if (!temporal.getIndivDom(x)) {
                Inter.copyIndiv(numInter, temporal.getIndiv(x));
                numInter++;
            }
        }

        // Create arrays
        double[][] distances = new double[numInter][numInter];  // Array for the distances
        int[][] sortedIndex = new int[numInter][numInter];      // Array of indexes to individuals sorted by distances
        boolean[] mantener = new boolean[numInter];             // Array for the individuals to keep in elite
        for (int x=0;x<numInter;x++)
            mantener[x] = true;

        // Set distances between elites
        for ( int y=0; y<numInter; y++ ) {
            for(int z=y+1;z<numInter;z++) {
                distances[y][z] = Inter.getIndiv(y).calcDist(Inter.getIndiv(z));
                distances[z][y] = distances[y][z];
            }
            distances[y][y] = -1;
        }

        // Create sorted index lists
        for (int i=0; i<numInter; i++) {
            sortedIndex[i][0] = 0;
            for (int j=1; j<numInter; j++) { // for all columns
                int k = j;  // insertion position
                while (k>0 && distances[i][j] < distances[i][sortedIndex[i][k-1]]) {
                    sortedIndex[i][k] = sortedIndex[i][k-1];
                    k--;
                }
                sortedIndex[i][k] = j;
            }
        }

        // Now we compute into "mantener" the "long_elite" individuals of Inter to keep in elite
        int mf = numInter;
        while (mf> long_elite) {
            // search for minimal distances
            int minpos = 0;
            for (int i=1; i<numInter; i++) {
                for (int j=1; j<mf; j++) {
                    if (distances[i][sortedIndex[i][j]] < distances[minpos][sortedIndex[minpos][j]]) {
                        minpos = i;
                        break;
                    }
                    else if (distances[i][sortedIndex[i][j]] > distances[minpos][sortedIndex[minpos][j]])
                        break;
                }
            }
            // kill entries of pos (which is now minpos) from lists
            for (int i=0; i<numInter; i++) {
                // Don't choose these positions again
                distances[i][minpos] = MAXDOUBLE;
                distances[minpos][i] = MAXDOUBLE;
                for (int j=1; j<mf-1; j++) {
                    if (sortedIndex[i][j]==minpos) {
                        sortedIndex[i][j] = sortedIndex[i][j+1];
                        sortedIndex[i][j+1] = minpos;
                    }
                }
            }
            mantener[minpos] = false;
            mf--;
        }

        // Finally, copy the rest of individuals of Inter in elite
        int pos=0;
        for (int i=0; i<numInter;i++) {
            if (mantener[i]) {
                elite.copyIndiv(pos, Inter.getIndiv(i));
                pos++;
            }
        }

        // Finally, the number of elements in size is "long_elite" again
        used_elite = long_elite;

    }


    /**
     * <p>
     * Methods to get the lenght of the population
     * </p>
     * @return              Length of the population
     */
    public int getLenghtPop () {
        return long_poblacion;
    }

    /**
     * <p>
     * Methods to set the lenght of the population
     * </p>
     * @param value              Length of the population
     */
    public void setLenghtPop (int value) {
        long_poblacion = value;    }



    /**
     * <p>
     * Methods to get the lenght of the elite population
     * </p>
     * @return              Length of the elite population
     */
    public int getLenghtElite () {
        return long_elite;
    }

    /**
     * <p>
     * Methods to set the lenght of the elite population
     * </p>
     * @param value              Length of the elite population
     */
    public void setLenghtElite (int value) {
        long_elite = value;    }



    /**
     * <p>
     * Method to get the number of different individuals in the final elite population
     * </p>
     * @return              Size of the final elite population
     */
    public int getUsedElite () {
        return used_elite;
    }

    /**
     * <p>
     * Method to set the number of indivisuals in the final elite population
     * </p>
     */
    //////////////// no utilizar fuera de genetico
    private void setUsedElite (int value) {
        used_elite = value;    }



    /**
     * <p>
     * Sets the value for indicating the position of the next mutation
     * </p>
     * @param value             Value of the variable Mu_next
     */
    public void setMuNext (int value) {
        Mu_next = value;
    }
        
    
    /**
     * <p>
     * Methods to get the number of evaluation to perform in an iteration of the GA
     * </p>
     * @return                  Number of evaluations
     */
    public int getNEval () {
        return n_eval;    }


    /**
     * <p>
     * Methods to set the number of evaluation to perform in an iteration of the GA
     * </p>
     * @param value             Number of evaluations
     */
    public void setNEval (int value) {
        n_eval = value;    }

    
    /**
     * <p>
     * Methods to get the value for the minimum confidence of the rules to be generated
     * </p>
     * @return                  Minimum confidence
     */
    public float getMinConf () {
        return min_conf;    }


    /**
     * <p>
     * Methods to set the value for the minimum confidence of the rules to be generated
     * </p>
     * @param value             Minimum confidence
     */
    public void setMinConf (float value) {
        min_conf = value;    }

    /**
     * <p>
     * Gets the representation of the rules
     * </p>
     * @return              Representation of the rules
     */
    public String getRulesRep(){
        return RulesRep;
    }

    /**
     * <p>
     * Sets the representation of the rules
     * </p>
     * @param value              Representation of the rules
     */
    public void setRulesRep(String value){
        RulesRep = value;
    }

    /**
     * <p>
     * Methods to get the value for the crossover probability
     * </p>
     * @return                  Crossover probability
     */
    public float getProbCross () {
        return cross_prob;
    }

    /**
     * <p>
     * Methods to set the value for the crossover probability
     * </p>
     * @param value             Crossover probability
     */
    public void setProbCross (float value) {
        cross_prob = value;
    }

    /**
     * <p>
     * Methods to get the value for the mutation probability
     * </p>
     * @return                  Mutation probability
     */
    public float getProbMut () {
        return mut_prob;
    }

    /**
     * <p>
     * Methods to set the value for the mutation probability
     * </p>
     * @param value             Mutation probability
     */
    public void setProbMut (float value) {
        mut_prob= value;
    }


    /**
     * <p>
     * Method to create the Obj array with num (+1) elements
     * </p>
     * @param num           Num of elementos
     */
    public void  createObj (int num) {
        Obj = new String[num+1];
    }


    /**
     * <p>
     * Method to get the name of the quality measure in position pos
     * </p>
     * @param pos           Position of the quality measure
     * @return              Nama of the quality measure in position pos
     */
    public String getObj (int pos) {
        return Obj[pos];
    }


    /**
     * <p>
     * Method to set the name of the quality measure in position pos
     * </p>
     * @param pos               Postion of the quality measure
     * @param value             Name of the quality measure in position pos
     */
    public void setObj (int pos, String value) {
        Obj[pos] = value;
    }


    /**
     * <p>
     * Get the measures of a single rule of the main population
     * </p>
     * @param pos           Position of the individual
     * @param nFile         Name of the file to write the values
     */
    public QualityMeasures getQualityMeasures (int pos, String nFile) {
        return poblac.getMedidas(this, pos, nFile);
    }

    /**
     * <p>
     * Get the measures of a single rule of the elite population
     * </p>
     * @param pos           Position of the individual
     * @param nFile         Name of the file to write the values
     */
    public QualityMeasures getQualityMeasuresElite (int pos, String nFile) {
        return elite.getMedidas(this, pos, nFile);
    }


    /**
     * <p>
     * Evaluates an individual of the main population
     * </p>
     * @param pos           Position of the invidivual
     * @param AG            Genetic algorithm object
     * @param Variables     Structure of the Variables
     * @param Examples      Structure of the Examples
     */
    public void evalIndiv (int pos, Genetic AG, TableVar Variables, TableDat Examples) {
        poblac.evalIndiv(pos, AG, Variables, Examples);
    }


    /**
     * <p>
     * Evaluates an individual of the elite population
     * </p>
     * @param pos           Position of the invidivual
     * @param AG            Genetic algorithm object
     * @param Variables     Structure of the Variables
     * @param Examples      Structure of the Examples
     */
    public void evalEliteIndiv (int pos, Genetic AG, TableVar Variables, TableDat Examples) {
        elite.evalIndiv(pos, AG, Variables, Examples);
    }



    /**
     * <p>
     * Returns de hole chromosome of the selected individual of the main population
     * </p>
     * @param pos           Position of the individual
     * @return              Canonical chromosome of the individual
     */
    public CromCAN getIndivCromCAN (int pos) {
        return poblac.getIndivCromCAN(pos);
    }

    /**
     * <p>
     * Returns de hole chromosome of the selected individual of the main population
     * </p>
     * @param pos           Position of the individual
     * @return              DNF chromosome of the individual
     */
    public CromDNF getIndivCromDNF (int pos) {
        return poblac.getIndivCromDNF(pos);
    }


    /**
     * <p>
     * Returns de hole chromosome of the selected individual of the elite population
     * </p>
     * @param pos           Position of the individual
     * @return              Canonical chromosome of the individual
     */
    public CromCAN getEliteIndivCromCAN (int pos) {
        return elite.getIndivCromCAN(pos);
    }

    /**
     * <p>
     * Returns de hole chromosome of the selected individual iof the elite pupulation
     * </p>
     * @param pos           Position of the individual
     * @return              DNF chromosome of the individual
     */
    public CromDNF getEliteIndivCromDNF (int pos) {
        return elite.getIndivCromDNF(pos);
    }



    /**
     * <p>
     * BinTournSelect
     * Applies the selection schema of the genetic algorithm
     * </p>
     * Binary tournament selection from elite to inter
     */
    public void BinTournSelect() {
        int winner;

        /* Uses a binary tournament selection to select Globals.Param.long_poblacion individuals and stores on population inter */
        for (int i=0;i<long_poblacion;i++) {

            /* Ramdon selection of the individuals */
            int opponent1 = Randomize.Randint (0,long_elite-1);
            int opponent2 = Randomize.Randint (0,long_elite-1);

            winner = opponent1;  // Initially, the first is the winner
            /* If the second individual is better than the first, it is selected as winner */
            if ( elite.getIndivFitness(opponent2) < elite.getIndivFitness(opponent1) )
                winner = opponent2;
            else if (elite.getIndivFitness(opponent2) == elite.getIndivFitness(opponent1) ) {
                /* Will have to check the distances. By now, select the first */
                winner = opponent1;
            }

            /* Copies the winner in the correspondant position of Inter population */
            Inter.copyIndiv( i, elite.getIndiv(winner));
        }


    }


    /**
     * <p>
     * Multipoint cross operator for the genetic algorithm
     * </p>
     * Better chromosomes of Inter are at the first positions
     *    as the cross individual, stored in Des
     * 
     * @param Variables         Structure of Variables
     * @return              Number of crossovers performed
     */
    
    public int MultipointCrossover (TableVar Variables) {
        int mom, dad, xpoint1, xpoint2;
        int numcruces = (int) Math.round(cross_prob*long_poblacion/2);

        for (int x=0; x<numcruces; x++) {
            mom = Randomize.Randint (0,long_poblacion-1);
            dad = Randomize.Randint (0,long_poblacion-1);

            /* Copy the individuals to cross */
            if (RulesRep.equalsIgnoreCase("CAN"))   // Canonical representation of the rules
                for (int i=0; i<Variables.getNVars(); i++)
                {
                    Des.setCromElem (2*x,   i, Inter.getCromElem(mom,i));
                    Des.setCromElem (2*x+1, i, Inter.getCromElem(dad,i));
                }
            else   // DNF representation of the rules
                for (int i=0; i<Variables.getNVars(); i++)
                    for (int j=0; j<=Variables.getNLabelVar(i);j++)
                    {
                        Des.setCromElemGene (2*x,   i,j, Inter.getCromElemGene(mom,i,j));
                        Des.setCromElemGene (2*x+1, i,j, Inter.getCromElemGene(dad,i,j));
                    }
            
            /* Generation of the two point of cross */
            xpoint1 = Randomize.Randint (0,(Variables.getNVars()-1));
            if (xpoint1!=Variables.getNVars()-1)
                xpoint2 = Randomize.Randint ((xpoint1+1),(Variables.getNVars()-1));
            else
                xpoint2 = Variables.getNVars()-1;

            /* Cross the parts between both points  */
            if (RulesRep.equalsIgnoreCase("CAN"))   // Canonical representation of the rules
                for (int i=xpoint1;i<=xpoint2;i++) 
                {
                    Des.setCromElem(2*x,   i, Inter.getCromElem(dad,i));
                    Des.setCromElem(2*x+1, i, Inter.getCromElem(mom,i));
                }
            else   // DNF representation of the rules
                for (int i=xpoint1;i<=xpoint2;i++)
                    for (int j=0; j<=Variables.getNLabelVar(i);j++)
                    {
                        Des.setCromElemGene (2*x,   i,j, Inter.getCromElemGene(dad,i,j));
                        Des.setCromElemGene (2*x+1, i,j, Inter.getCromElemGene(mom,i,j));
                    }

            /* New individuals are not evaluated */
            Des.setIndivEvaluated (2*x, false);
            Des.setIndivEvaluated (2*x+1, false);

        }
      return (numcruces);   /* Number of crossovers */

    }
        

     /**
     * <p>
     * Applies the mutation operator. Uniform biased operator
     * </p>
     * @param Variables             Structure of Variables
     */
    public void UniformBiasedMutation (TableVar Variables) {
        int i, j;
        int posiciones = Variables.getNVars()*long_poblacion;
        
        if (mut_prob > 0)
            while (Mu_next < posiciones) {
                /* Determine the chromosome and gene to be muted */
                i = Mu_next/Variables.getNVars();
                j = Mu_next%Variables.getNVars();

                /* Copy the chromosome */
                if (RulesRep.equalsIgnoreCase("CAN"))   // Canonical representation of the rules
                    for (int h=0; h<Variables.getNVars(); h++)
                        Des.setCromElem(descendientes, h, Inter.getCromElem(i,h));
                else   // DNF representation of the rules
                    for (int h=0; h<Variables.getNVars(); h++)
                        for (int l=0; l<=Variables.getNLabelVar(h);l++)
                            Des.setCromElemGene (descendientes, h, l, Inter.getCromElemGene(i,h,l));
        
                /* Make the mutation */
                /* Half the mutations eliminates the variable from the rule */
                /* The others sets a random value (including the elimination) */
                if (Randomize.Randint (0,10) <=5) {  // Eliminate
                    if (RulesRep.equalsIgnoreCase("CAN"))   // Canonical representation of the rules
                    {
                        if (!Variables.getContinuous(j))
                            Des.setCromElem(descendientes, j, (int)Variables.getMax(j)+1);
                        else
                            Des.setCromElem(descendientes, j, Variables.getNLabelVar(j));
                    }
                    else   // DNF Representation
                    {
                        for (int l=0; l<=Variables.getNLabelVar(j); l++)
                            Des.setCromElemGene(descendientes, j, l, 0);
                    }
                    
                }
                else {   // Mutate
                    if (RulesRep.equalsIgnoreCase("CAN"))   // Canonical representation of the rules
                    {
                    if (!Variables.getContinuous(j))
                        Des.setCromElem(descendientes, j, Randomize.Randint((int)Variables.getMin(j), (int)Variables.getMax(j)));
                    else
                        Des.setCromElem(descendientes, j, Randomize.Randint(0,(Variables.getNLabelVar(j)-1)));
                    }
                    else   // DNF representation
                    {
                        int interv=0;
                        for (int l=0; l<Variables.getNLabelVar(j); l++) {
                            // pone a todos valores aleatorios, y calcula si interviene
                            if (Randomize.Rand()<0.5)
                                Des.setCromElemGene (descendientes, j, l, 1);
                            else
                                Des.setCromElemGene (descendientes, j, l, 0);
                            // cambiar despu�s de las comprobaciones por:
                            // Des.setCromElemGene(descendientes, j, l, Randomize.Randint(0,1));
                            if (Des.getCromElemGene(descendientes, j, l)==1)
                                interv ++;
                        }
                        // si no interviene ning�n valor o intervienen todos, la variable no interviene
                        if (interv==0 || interv==Variables.getNLabelVar(j))
                            Des.setCromElemGene(descendientes, j, Variables.getNLabelVar(j), 0);
                        else
                            Des.setCromElemGene(descendientes, j, Variables.getNLabelVar(j), 1);
                    }
                        
                }
                descendientes++;

                /* Marks the chromosome as not evaluated */
                Des.setIndivEvaluated(i,false);

                /* Compute next position to be muted */
                if (mut_prob<1) {
                    float  m = (float) Randomize.Rand();
                    Mu_next += Math.ceil (Math.log(m) / Math.log(1.0 - mut_prob));
                }   
                else {
                    Mu_next += 1;
                }

            }

        Mu_next -= posiciones;
            
    }

    
    /**
     * <p>
     * Reproductdion schema
     * </p>
     * Replaces the worst individuals with the generated by cross and mutation
     *
     * @param Variables             Structure of Variables
     */
    public void Reproduccion (TableVar Variables) {
        int pos;
        int[] indices = new int[long_poblacion];
        float[] fit = new float[long_poblacion];

        // Fist, copy individuals of intar in poblac
        for (int i=0;i<long_poblacion;i++)
            poblac.copyIndiv(i, Inter.getIndiv(i));

        // Save the fitness of the elements of inter
        for (int i=0; i<long_poblacion; i++) {
            fit[i]= poblac.getIndivFitness(i);
            indices[i] = i;
        }

        /* Sort the fitness array of actual population and store the values at "indices" */
        Utils.OrCrecIndex (fit,0,long_poblacion-1,indices);  /* To minimice */

        for (int i=0; i<descendientes; i++) {	/* For each descendant */
            pos = indices[long_poblacion-1-i];
            for (int j=0; j<Variables.getNVars(); j++) {
                if (RulesRep.equalsIgnoreCase("CAN"))   // Canonical representation of the rules
                    poblac.setCromElem(pos, j, Des.getCromElem(i,j));
                else     // DNF representation
                    for (int l=0; l<=Variables.getNLabelVar(j);l++)
                        poblac.setCromElemGene(pos, j, l, Des.getCromElemGene(i,j,l));
            }
            poblac.setIndivEvaluated(pos,false);

        }
        /* Remark: Always makes the substitution, without see if the new individual is better */

    }


    /**
     * <p>
     * Reproductdion schema - Modified steady step
     * </p>
     * Not used
     *
     * @param Variables             Structure of Variables
     */

    public void SteadyStepReproduction (TableVar Variables) {
        int pos;
        int[] indices = new int[long_poblacion];
        float[] fit = new float[long_poblacion];

        for (int i=0; i<long_poblacion; i++) {
            fit[i]= poblac.getIndivFitness(i);
            indices[i] = i;
        }

        // Sort the fitness array of actual population and store the values at "indices"
        Utils.OrDecIndex (fit,0,long_poblacion-1,indices);  // To maximise

        for (int i=0; i<descendientes; i++) {	// For each descendant
            pos = indices[long_poblacion-1-i];
            for (int j=0; j<Variables.getNVars(); j++) {
                if (RulesRep.equalsIgnoreCase("CAN"))   // Canonical representation of the rules
                    poblac.setCromElem(pos, j, Des.getCromElem(i,j));
                else {  // DNF representation
                    for (int k=0; k<=Variables.getNLabelVar(j); k++)
                        poblac.setCromElemGene(pos, j, k, Des.getCromElemGene(i,j,k));
                }

            }
            poblac.setIndivEvaluated(pos,false);
        }

    }

   

    /**
     * <p>
     * Composes the genetic algorithm applying the operators
     * </p>
     * @param Variables             Structure of Variables
     * @param Examples              Structure of Examples
     * @param NumObjectives         Number of objectives used
     * @param nFile                 File to write the process of the genetic algorithm
     */
    public void GeneticAlgorithm (TableVar Variables, TableDat Examples, int NumObjectives, String nFile) {
        int cruces;
        String contents;
        float porcVar = (float) 0.25;   // Percentaje of variables
        float porcPob = (float) 0.75;   // Percentaje of individuals


        /* Creates and initialices the population  */
        poblac = new Population (this, long_poblacion, Variables.getNVars(), RulesRep, Variables, Examples.getNEx(),NumObjectives);
        poblac.BsdInitPop (Variables, porcVar,porcPob);

        // Creates the elite population
        elite = new Population(this, long_elite, Variables.getNVars(), RulesRep, Variables, Examples.getNEx(),NumObjectives);

        // Echo of the lengths
        System.out.println ("Population length:" + long_poblacion);
        System.out.println ("Elite set length:" + long_elite);

        /* Creates the Temporal set (elite plus poblacion to compute fitness) */
        temporal = new Population (this, long_elite+long_poblacion, Variables.getNVars(), RulesRep, Variables, Examples.getNEx(),NumObjectives);
        used_temp  = 0;

        /* Creates the populations "Inter" and "Des" to aply the genetic operations */
        Inter = new Population(this, long_poblacion, Variables.getNVars(), RulesRep, Variables, Examples.getNEx(),NumObjectives);
        Des   = new Population(this, 2*long_poblacion, Variables.getNVars(), RulesRep, Variables, Examples.getNEx(),NumObjectives);

        /* Inicialization of variables */
        Trials = 0;
        Gen = 1;
        used_elite = 0;   // At the beginning, elite has 0 individuals


        do { /* GA General cycle */

            // Population will be evaluated once joined population and elite in temporal, and repetitions are eliminated

            // Joins population plus elite population into temporal population
            JoinTemp ();

            // Eliminate duplicates in temporal
            used_temp = temporal.delDup(used_temp);

            // Evaluate non evaluated individuals in poblac (without file writing)
            Trials += temporal.evalPop (this, used_temp, Variables, Examples);

            // Computes and stores the fitness of temporal individuals
            temporal.CalcFitness (used_temp, NumObjectives);

            // Compute the number of non dominated individuals
            int noDom=0;
            for (int x=0; x<used_temp; x++) {
                // If the individual x is not dominated, increase the number of non dominated
                if (!temporal.getIndivDom(x))
                    noDom++;
            }

            // Create the new elite population (with exactly "long_elite" individuals
            if (noDom<=long_elite)
                CompletaElite();
            else
                TruncaElite();

            noDom=0;
            for (int x=0; x<used_elite; x++) {
                // If the individual x is not dominated, increase the number of non dominated
                if (!elite.getIndivDom(x))
                    noDom++;
            }

            /* Binary Tournament Selection (from elite to Inter)  */
            BinTournSelect ();

            /* Crossover: Generated individuals are stored in Des */
            descendientes= 0;
            cruces = MultipointCrossover(Variables);
            descendientes = cruces*2;

            /* Mutation: new individuals stored in des */
            UniformBiasedMutation (Variables);

            /* Reproduction model: replaces worst individulas with the generated by cross and mutation */
            Reproduccion(Variables);

            /* Actual population evaluation (only evaluates new individuals not yet evaluated) */
            Trials+=poblac.evalPop (this, long_poblacion, Variables, Examples);

            /* Next generation  */
            Gen++;

        } while (Trials <= n_eval);   // GA finishes when the number of evaluations is reached


        /* SEG_File. GA concludes when the number of evaluations is reached */
        contents = "\nGenetic Algorithm execution finished\n";
        contents+= "\tNumber of Generations = " + (Gen-1) + "\n";
        contents+= "\tNumber of Evaluations = " + Trials + "\n";
        Files.addToFile(nFile, contents);
        


        /* Eliminate duplicates in elite population obtaining the remaining number of individuals */
        setUsedElite (elite.delDup(long_elite));

        /* Evaluation of individuals in elite (only evaluates new individuals not yet evaluated) */
        Trials+=elite.evalPop (this, used_elite, Variables, Examples);

    }


    
}
