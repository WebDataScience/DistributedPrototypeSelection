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

package keel.Algorithms.Genetic_Rule_Learning.M5Rules;

/**
 * Class for handing a node in the tree or the subtree under this node
 */

public final class M5TreeNode {
    boolean type; // = true, NODE;  = false, LEAF
    int splitAttr; // splitting attribute
    double splitValue; // the value of the splitting attribute at the
    // splitting position
    Function unsmoothed; // unsmoothed function
    Function smoothed; // smoothed function
    boolean valueNode; // =true, if use the constant term as the
    // predicting function
    M5TreeNode upNode; // pointer of the up node
    M5TreeNode leftNode; // pointer of the left node
    M5TreeNode rightNode; // pointer of the right node
    Results errors; // evaluation errors of the model under this node
    int numParameters; // number of parameters of the chosen model for this
    // node, either the subtree model or the linear model
    SplitInfo sf; // Spliting infomation
    int lm; // linear model number at the leaf; for NODE, lm = 0;
    MyDataset itemsets; // itemsets reaching this node

    int model; // model type: LINEAR REGRESSION, REGRESSION_TREE,
    // MODEL_TYPE
    double pruningFactor; // to control the tree size
    double deviation; // deviation of the gobal class variable

    final static int LINEAR_REGRESSION = 1;
    final static int REGRESSION_TREE = 2;
    final static int MODEL_TREE = 3;
    final static double SPLIT_NUM = 3.5; // a node will not be further split
    // if it contains itemsets less than SPLIT_NUM

    /**
     * Constructs a new node
     * @param inst itemsets
     * @param up the parent node
     */
    public M5TreeNode(MyDataset inst, M5TreeNode up) {
        int i;

        type = true;
        unsmoothed = new Function();
        smoothed = new Function();
        valueNode = true;
        upNode = up;
        leftNode = null;
        rightNode = null;
        errors = null;
        numParameters = 0;
        itemsets = inst;
        lm = 0;
        if (up != null) {
            model = up.model;
            pruningFactor = up.pruningFactor;
            deviation = up.deviation;
        }
    }

    /**
     * Constructs the root of a tree
     * @param inst itemsets
     * @param up the parent node
     * @param optmodel the model of the tree:LINEAR_REGRESION,MODEL_TREE or REGRESSION_TREE
     * @param optpruningFactor the pruning factor
     * @param std standard deviation
     */
    public M5TreeNode(MyDataset inst, M5TreeNode up,int optmodel,double optpruningFactor,double std) {
        int i;

        type = true;
        unsmoothed = new Function();
        smoothed = new Function();
        valueNode = true;
        upNode = up;
        leftNode = null;
        rightNode = null;
        errors = null;
        numParameters = 0;
        itemsets = inst;
        lm = 0;

        model = optmodel;
        pruningFactor = optpruningFactor;
        deviation = std;
    }

    /**
     * Converts the information stored at this node to a string
     * @return the converted string
     * @exception Exception if something goes wrong
     */
    public final String singleNodeToString() throws Exception {

        StringBuffer text = new StringBuffer();

        text.append("Print single node (" + itemsets.numItemsets() + "):\n");
        if (type == true) {
            text.append("    Type:\t\t\tNODE\n");
        } else {
            text.append("    Type:\t\t\tLEAF\n");
        }
        text.append("    Unsmoothed function:\t\t");
        unsmoothed.toString(itemsets, 0);
        System.out.print("    Smoothed function:\t\t");
        smoothed.toString(itemsets, 0);
        text.append("    Value node:\t\t\t" + valueNode + "\n");
        if (errors != null) {
            text.append(errors.toString());
        }
        if (upNode != null) {
            text.append("    upNode:\t\t\tyes\n");
        } else {
            text.append("    upNode:\t\t\tnull\n");
        }
        if (leftNode != null) {
            text.append("    leftNode:\t\t\tyes\n");
        } else {
            text.append("    leftNode:\t\t\tnull\n");
        }
        if (rightNode != null) {
            text.append("    rightNode:\t\t\tyes\n");
        } else {
            text.append("    rightNode:\t\t\tnull\n");
        }
        text.append("    number of parameters:\t" + numParameters + "\n");
        text.append("    LEAF number(lm):\t\t" + lm + "\n");
        text.append("    Number of itemsets\t\t" + itemsets.numItemsets() +
                    "\n");

        return text.toString();
    }

    /**
     * Converts the tree under this node to a string
     * @param treeLevel the depth of this node;
     *        the root of a tree should have treeLevel = 0
     * @param deviation the global deviation of the class column,
     *        used for evaluating relative errors
     * @return the converted string
     */
    public final String treeToString(int treeLevel, double deviation) {

        int i;
        StringBuffer text = new StringBuffer();

        if (type == true) {
            text.append("\n");
            for (i = 1; i <= treeLevel; i++) {
                text.append("    ");
            }
            if (itemsets.getAttribute(splitAttr).name().charAt(0) != '[') {
                text.append(itemsets.getAttribute(splitAttr).name() + " <= " +
                            M5.doubleToStringG(splitValue, 1, 3) + " ");
            } else {
                text.append(itemsets.getAttribute(splitAttr).name() + " false : ");
            }
            treeLevel++;
            text.append(leftNode.treeToString(treeLevel, deviation));
            treeLevel--;
            for (i = 1; i <= treeLevel; i++) {
                text.append("    ");
            }
            if (itemsets.getAttribute(splitAttr).name().charAt(0) != '[') {
                text.append(itemsets.getAttribute(splitAttr).name() + " >  " +
                            M5.doubleToStringG(splitValue, 1, 3) + " ");
            } else {
                text.append(itemsets.getAttribute(splitAttr).name() + " true : ");
            }
            treeLevel++;
            text.append(rightNode.treeToString(treeLevel, deviation));
            treeLevel--;
        } else { // LEAF
            text.append(" THEN LM" + lm + "\n");
            /*      if(deviation > 0.0)
             text.append(" (" + itemsets.numItemsets() + "/" +
                  M5.doubleToStringG((100. * errors.rootMeanSqrErr /
                      deviation),1,3) + "%)\n");
                  else text.append(" (" + itemsets.numItemsets() + ")\n");*/
        }

        return text.toString();
    }

    /**
     * Counts the number of linear models in the tree.
     * @return the number of linear models in the tree.
     */
    public final int numberOfLinearModels() {

        if (type == false) {
            return 1;
        } else {
            return leftNode.numberOfLinearModels() +
                    rightNode.numberOfLinearModels();
        }
    }

    /**
     * Converts all the linear models at the leaves under the node to a string
     * @param smooth either the smoothed models if true, otherwise
     *        the unsmoothed are converted
     * @return the converted string
     * @exception Exception if something goes wrong
     */
    public final String formulaeToString(boolean smooth) throws Exception {
        int startingPoint;
        StringBuffer text = new StringBuffer();

        if (type == true) {
            text.append(leftNode.formulaeToString(smooth));
            text.append(rightNode.formulaeToString(smooth));
        } else {
            text.append("    LM" + lm + ":  ");
            startingPoint = 6 + (int) (Math.log(lm + 0.5) / Math.log(10)) + 1 +
                            3;
            if (smooth == true) {
                text.append(smoothed.toString(itemsets, startingPoint));
            } else {
                text.append(unsmoothed.toString(itemsets, startingPoint));
            }
            text.append("\n");
        }

        return text.toString();
    }

    /**
     * Sets the leaves' numbers
     * @param leafCounter the number of leaves counted
     * @return the number of the total leaves under the node
     */
    public final int numLeaves(int leafCounter) {
        if (type == true) { // NODE
            lm = 0;
            leafCounter = leftNode.numLeaves(leafCounter);
            leafCounter = rightNode.numLeaves(leafCounter);
        } else { // LEAF
            leafCounter++;
            lm = leafCounter;
        }
        return leafCounter;
    }

    /**
     * Splits the node recursively, unless there are few itemsets or
     *     itemsets have similar values of the class attribute
     * @param inst itemsets
     * @exception Exception if something goes wrong
     */
    public final void split(MyDataset inst) throws Exception {
        SplitInfo s, sMax;
        int j, partition;
        MyDataset leftInst, rightInst;

        itemsets = inst;
        if (itemsets.numItemsets() < SPLIT_NUM || M5.stdDev(itemsets.getClassIndex(), itemsets) < deviation * 0.05) {
            type = false;
        } else {
            sMax = new SplitInfo(0, itemsets.numItemsets() - 1, -1);
            s = new SplitInfo(0, itemsets.numItemsets() - 1, -1);
            for (j = 0; j < itemsets.numAttributes(); j++) {
                if (j != itemsets.getClassIndex()) {
                    itemsets.sort(itemsets.getAttribute(j));
                    s.attrSplit(j, itemsets);
                    if ((Math.abs(s.maxImpurity - sMax.maxImpurity) > 1.e-6) &&
                        (s.maxImpurity > sMax.maxImpurity + 1.e-6)) {
                        sMax = s.copy();
                    }
                }
            }

            if (sMax.splitAttr < 0 || sMax.position < 1 || sMax.position > itemsets.numItemsets() - 1) {
                type = false;
            }
            if (type == true) {
                sf = sMax;
                splitAttr = sMax.splitAttr; // split attribute
                splitValue = sMax.splitValue; // split value
                unsmoothed = new Function(splitAttr); // unsmoothed function

                leftInst = new MyDataset(itemsets, itemsets.numItemsets());
                rightInst = new MyDataset(itemsets, itemsets.numItemsets());
                int nmissings=0,missings[]=new int[itemsets.numItemsets()];
                for (int i = 0; i < itemsets.numItemsets(); i++) {
                  if (!itemsets.isMissing(i,splitAttr)){
                    if (itemsets.itemset(i).getValue(splitAttr) <= splitValue) {
                      leftInst.addItemset(itemsets.itemset(i));
                    }
                    else {
                      rightInst.addItemset(itemsets.itemset(i));
                    }
                  }
                  else{
                    missings[nmissings]=i;
                    nmissings++;
                  }
                }

                //Missing values treatment
                if (nmissings>0){
                  //Calculate the average class value
                  double avgRight = 0.0, avgLeft = 0.0;
                  if (itemsets.getAttribute(splitAttr).isEnumerate()) {
                    avgRight = rightInst.averageClassValue();
                    avgLeft = leftInst.averageClassValue();
                  }
                  else {
                    if (rightInst.numItemsets() > 3) {
                      rightInst.sort(splitAttr);
                      int n = rightInst.numItemsets();
                      double sum = rightInst.itemset(n - 1).getClassValue()
                          + rightInst.itemset(n - 2).getClassValue()
                          + rightInst.itemset(n - 3).getClassValue();
                      avgRight = sum / ( (double) n);
                    }
                    else {
                      avgRight = rightInst.averageClassValue();
                    }
                    if (leftInst.numItemsets() > 3) {
                      leftInst.sort(splitAttr);
                      int n = leftInst.numItemsets();
                      double sum = leftInst.itemset(0).getClassValue()
                          + leftInst.itemset(1).getClassValue()
                          + leftInst.itemset(2).getClassValue();
                      avgLeft = sum / ( (double) n);
                    }
                    else {
                      avgLeft = leftInst.averageClassValue();
                    }
                  }
                  double avgClassValue=(avgRight+avgLeft)/2.0;
                  //Give out the missing instances
                  for (int i=0;i<nmissings;i++){
                    if (itemsets.itemset(missings[i]).getClassValue()<=avgClassValue){
                      if (avgRight<=avgLeft){
                        rightInst.addItemset(itemsets.itemset(missings[i]));
                      }
                      else{
                        leftInst.addItemset(itemsets.itemset(missings[i]));
                      }
                    }
                    else{
                      if (avgRight>avgLeft){
                        rightInst.addItemset(itemsets.itemset(missings[i]));
                      }
                      else{
                        leftInst.addItemset(itemsets.itemset(missings[i]));
                      }
                    }
                  }
                }

                leftInst.compactify();
                rightInst.compactify();

                leftNode = new M5TreeNode(leftInst, this);
                leftNode.split(leftInst); // split left node

                rightNode = new M5TreeNode(rightInst, this);
                rightNode.split(rightInst); // split right node

                //Give the missing values the average value for the splitting attribute
                if (nmissings>0){
                  double avgAtt=itemsets.averageValue(splitAttr);
                  for (int i=0;i<nmissings;i++)
                    itemsets.itemset(missings[i]).setValue(splitAttr,avgAtt);
                }

                this.valueNode(); // function of the constant value

                if (model != REGRESSION_TREE) {
                    unsmoothed = Function.combine(unsmoothed,leftNode.unsmoothed);
                    // passes up the attributes found under the left node
                    unsmoothed = Function.combine(unsmoothed,rightNode.unsmoothed);
                    // passes up the attributes found under the right node
                } else {
                    unsmoothed = new Function();
                }
            }
        }

        if (type == false) { // a leaf node
            this.leafNode();
            errors = unsmoothed.errors(itemsets);
        }
    }

    /**
     * Sets the node to a leaf
     * @exception Exception if something goes wrong
     */
    public final void leafNode() throws Exception {
        type = false;
        unsmoothed.terms[0] = 0;
        this.valueNode();
    }

    /**
     * Takes a constant value as the function at the node
     * @exception Exception if something goes wrong
     */
    public final void valueNode() throws Exception {
        int i;

        valueNode = true;
        unsmoothed.coeffs[0] = 0.0;
        for (i = 0; i <= itemsets.numItemsets() - 1; i++) {
            unsmoothed.coeffs[0] += itemsets.itemset(i).getClassValue();
        }
        unsmoothed.coeffs[0] /= itemsets.numItemsets();
    }

    /**
     * Prunes the model tree
     * @exception Exception if something goes wrong
     */
    public final void prune() throws Exception {

        int list[];
        double eps1, eps2, va;
        Results e1, e2;
        Function function;

        if (type == false) { // LEAF
            errors = unsmoothed.errors(itemsets);
            numParameters = 1;
        } else { // NODE
            if (model == LINEAR_REGRESSION) { // outputs linear regression model
                function = new Function(itemsets);
                if (function.terms[0] <
                    Math.sqrt(itemsets.numItemsets()) * 2.0 &&
                    function.terms[0] < 50) {
                    unsmoothed = function;
                }
                this.regression(unsmoothed);
                valueNode = false;
                errors = unsmoothed.errors(itemsets);
                type = false;
            } else {
                leftNode.prune(); // prunes the left node
                rightNode.prune(); // pruned the right node

                if (model != REGRESSION_TREE) { // model tree
                    unsmoothed = Function.combine(unsmoothed,leftNode.unsmoothed);
                    unsmoothed = Function.combine(unsmoothed,rightNode.unsmoothed);
                } else {
                    unsmoothed = new Function(); // regression tree
                }

                numParameters = leftNode.numParameters +
                                rightNode.numParameters + 1;

                this.function(); // computes linear model at node

                e1 = unsmoothed.errors(itemsets); // errors of the linear model
                eps1 = e1.rootMeanSqrErr * this.factor(itemsets.numItemsets(),
                        unsmoothed.terms[0] + 1, pruningFactor);
                e2 = this.errors(itemsets, false); // errors of the subtree
                eps2 = e2.rootMeanSqrErr * this.factor(itemsets.numItemsets(),
                        numParameters, pruningFactor);
                errors = e2;
                if (eps1 <= eps2 || eps1 < deviation * 0.00001) { // chooses linear model
                    type = false;
                    numParameters = unsmoothed.terms[0] + 1;
                    errors = e1;
                }
            }
        }
    }

    /**
     * Computes the coefficients of a linear model using the itemsets at this
     *      node
     * @param function the linear model containing the index of the attributes;
     *      coefficients are to be computed
     */
    public final void regression(Function function) {

        int i, j, n, k;
        M5Matrix x, y;

        n = itemsets.numItemsets();
        k = function.terms[0] + 1;
        x = new M5Matrix(n, k);
        y = new M5Matrix(n, 1);
        for (i = 0; i <= n - 1; i++) {
            x.elements[i][0] = 1.0;
            for (j = 1; j <= k - 1; j++) {
                x.elements[i][j] = itemsets.itemset(i).getValue(function.terms[j]);
            }
            y.elements[i][0] = itemsets.itemset(i).getValue(itemsets.getClassIndex());
        }

        function.coeffs = x.regression(y, n, k);
    }

    /**
     * Finds the appropriate order of the unsmoothed linear model at this node
     * @exception Exception if something goes wrong
     */
    public final void function() throws Exception {

        int n, jmin, flag = 0;
        double err1, err2, sdy;
        Results e1, e2;
        Function f1 = unsmoothed;
        Function f2;

        if (f1.terms[0] != 0) {
            sdy = M5.stdDev(itemsets.getClassIndex(), itemsets);
            this.regression(f1);
            valueNode = false;
            if (model != LINEAR_REGRESSION) {
                e1 = f1.errors(itemsets);
                err1 = e1.rootMeanSqrErr * this.factor(itemsets.numItemsets(),
                        f1.terms[0] + 1, pruningFactor);
                flag = 0;

                while (flag == 0) {
                    jmin = f1.insignificant(sdy, itemsets);
                    if (jmin == -1) {
                        flag = 1;
                    } else {
                        f2 = f1.remove(jmin);
                        this.regression(f2);
                        e2 = f2.errors(itemsets);
                        err2 = e2.rootMeanSqrErr *
                               this.factor(itemsets.numItemsets(),
                                           f2.terms[0] + 1, pruningFactor);
                        if (err2 > err1 && err2 > deviation * 0.00001) {
                            flag = 1;
                        } else { // compare estimated error with and without attr jmin
                            f1 = f2;
                            err1 = err2;
                            if (f1.terms[0] == 0) {
                                flag = 1;
                            }
                        }
                    }
                }
            }
            unsmoothed = f1;
        }
        if (unsmoothed.terms[0] == 0) { // constant function without attributes
            this.valueNode();
        }
    }

    /**
     * Calculates a multiplication factor used at this node
     * @param n the number of itemsets
     * @param v the number of the coefficients
     * @param pruningFactor the pruning factor
     * @return multiplication factor
     */
    public final double factor(int n, int v, double pruningFactor) {

        double factor = 0.0;

        if (n <= v) {
            return 10.0; /* Caution */
        }
        factor = (double) (n + pruningFactor * v) / (double) (n - v);

        return factor;
    }

    /**
     * Smoothens all unsmoothed formulae at the tree leaves under this node.
     */
    public final void smoothen() {
        if (type == false) {
            smoothed = unsmoothed.copy();
            if (upNode != null) {
                this.smoothenFormula(this);
            }
        } else {
            leftNode.smoothen();
            rightNode.smoothen();
        }
    }

    /**
     * Recursively smoothens the unsmoothed linear model at this node with the
     *     unsmoothed linear models at the nodes above this
     * @param current the unsmoothed linear model at the up node of the
     *     'current' will be used for smoothening
     */
    public final void smoothenFormula(M5TreeNode current) {

        int i = smoothed.terms[0], j = current.upNode.unsmoothed.terms[0], k, l,
                                       smoothingConstant = 15;
        Function function;

        function = Function.combine(smoothed, current.upNode.unsmoothed);

        function.coeffs[0] =
                M5.smoothenValue(smoothed.coeffs[0],
                                 current.upNode.unsmoothed.coeffs[0],
                                 current.itemsets.numItemsets(),
                                 smoothingConstant);
        for (k = function.terms[0]; k >= 1; k--) {
            if (i >= 1 && j >= 1) {
                if (function.terms[k] == smoothed.terms[i] && function.terms[k] ==
                    current.upNode.unsmoothed.terms[j]) {
                    function.coeffs[k] =
                            M5.smoothenValue(smoothed.coeffs[i],
                                             current.upNode.unsmoothed.coeffs[j],
                                             current.itemsets.numItemsets(),
                                             smoothingConstant);
                    i--;
                    j--;
                } else if (function.terms[k] == smoothed.terms[i] &&
                           function.terms[k] !=
                           current.upNode.unsmoothed.terms[j]) {
                    function.coeffs[k] =
                            M5.smoothenValue(smoothed.coeffs[i],
                                             0.0,
                                             current.itemsets.numItemsets(),
                                             smoothingConstant);
                    i--;
                } else if (function.terms[k] != smoothed.terms[i] &&
                           function.terms[k] ==
                           current.upNode.unsmoothed.terms[j]) {
                    function.coeffs[k] =
                            M5.smoothenValue(0.0,
                                             current.upNode.unsmoothed.coeffs[j],
                                             current.itemsets.numItemsets(),
                                             smoothingConstant);
                    j--;
                } else {
                    M5.errorMsg("wrong terms value in smoothing_formula().");
                }
            } else if (i < 1 && j < 1) {
                break;
            } else if (j >= 1) {
                for (l = k; l >= 1; l--) {
                    function.coeffs[l] =
                            M5.smoothenValue(0.0,
                                             current.upNode.unsmoothed.coeffs[j--],
                                             current.itemsets.numItemsets(),
                                             smoothingConstant);
                }
                break;
            } else {
                for (l = k; l >= 1; l--) {
                    function.coeffs[l] =
                            M5.smoothenValue(smoothed.coeffs[i--],
                                             0.0,
                                             current.itemsets.numItemsets(),
                                             smoothingConstant);
                }
                break;
            }
        }
        smoothed = function;
        if (current.upNode.upNode != null) {
            this.smoothenFormula(current.upNode);
        }
    }

    /**
     * Converts the predictions by the tree under this node to a string
     * @param inst itemsets
     * @param smooth =true using the smoothed models; otherwise, the unsmoothed
     * @param lmNo the number of the associated linear model
     * @return the converted string
     * @exception Exception if something goes wrong
     */
    public final String predictionsToString(MyDataset inst, int lmNo,
                                            boolean smooth) throws Exception {
        int i, lmNum;
        double value;
        StringBuffer text = new StringBuffer();

        text.append("    Predicting test itemsets (" +
                    inst.getAttribute(inst.getClassIndex()).name() + ", column "
                    + (inst.getClassIndex() + 1) + ")\n\n");
        for (i = 0; i <= inst.numItemsets() - 1; i++) {
            lmNum = this.leafNum(inst.itemset(i));
            if (lmNo == 0 || lmNo == lmNum) {
                text.append("      Predicting " + i + " (LM" + lmNum + "):  ");
                text.append(inst.itemset(i).toString() + "\n");
                value = this.predict(inst.itemset(i), smooth);
                if (inst.itemset(i).classIsMissing() == false) {
                    text.append("      Actual value: " +
                                M5.doubleToStringG(inst.itemset(i).getClassValue(),
                            9, 4) +
                                "    Prediction: " +
                                M5.doubleToStringG(value, 9, 4) +
                                "    Abs. error: " +
                                M5.doubleToStringG(Math.
                            abs(inst.itemset(i).getClassValue() - value), 9, 4) +
                                "\n\n");
                } else {
                    text.append("      Actual value:   missing    Prediction: " +
                                M5.doubleToStringG(value, 9, 4) +
                                "    Abs. Error: undefined\n\n");
                }
            }
        }

        return text.toString();
    }

    /**
     * Detects which leaf a itemset falls into
     * @param itemset the itemset
     * @return the leaf no.
     */
    public final int leafNum(Itemset itemset) {

        int lmNum = 0;

        if (type == false) {
            lmNum = lm;
        } else {
            if (itemset.getValue(splitAttr) <= splitValue) {
                lmNum = leftNode.leafNum(itemset);
            } else {
                lmNum = rightNode.leafNum(itemset);
            }
        }

        return lmNum;
    }

    /**
     * Predicts the class value of an itemset by the tree
     * @param itemset the itemset
     * @param smooth =true, uses the smoothed model; otherwise uses the unsmoothed
     * @return the predicted value
     */
    public final double predict(Itemset itemset, boolean smooth) {

        double y = 0.0;

        if (type == false) { // LEAF
            if (smooth == true) {
                y = smoothed.predict(itemset);
            } else {
                if (valueNode == true) {
                    y = unsmoothed.coeffs[0];
                } else {
                    y = unsmoothed.predict(itemset);
                }
            }
        } else { // NODE
            if (itemset.getValue(splitAttr) <= splitValue) {
                y = leftNode.predict(itemset, smooth);
            } else {
                y = rightNode.predict(itemset, smooth);
            }
        }

        return y;
    }

    /**
     * Evaluates a tree
     * @param inst itemsets
     * @param smooth =true, evaluates the smoothed models;
     *         =false, evaluats the unsmoothed models
     * @return the evaluation results
     * @exception Exception if something goes wrong
     */
    public final Results errors(MyDataset inst, boolean smooth) throws
            Exception {
        int i;
        double tmp;
        Results e = new Results(0, inst.numItemsets() - 1);

        for (i = 0; i <= inst.numItemsets() - 1; i++) {
            tmp = this.predict(inst.itemset(i), smooth) -
                  inst.itemset(i).getClassValue();
            e.sumErr += tmp;
            e.sumAbsErr += Math.abs(tmp);
            e.sumSqrErr += tmp * tmp;
        }

        e.meanAbsErr = e.sumAbsErr / e.numItemsets;
        e.meanSqrErr = (e.sumSqrErr - e.sumErr * e.sumErr / e.numItemsets)
                       / e.numItemsets;
        e.meanSqrErr = Math.abs(e.meanSqrErr);
        e.rootMeanSqrErr = Math.sqrt(e.meanSqrErr);

        return e;
    }

    /**
     * Computes performance measures of a tree
     * @param inst itemsets
     * @param smooth =true uses the smoothed models;
     *        otherwise uses the unsmoothed models
     * @return the performance measures
     * @exception Exception if something goes wrong
     */
    public final Measures measures(MyDataset inst, boolean smooth) throws
            Exception {

        int i, numItemsets, count;
        double sd, y1[], y2[];
        Measures measures = new Measures();

        errors = this.errors(inst, smooth);
        numItemsets = errors.numItemsets - errors.missingItemsets;
        y1 = new double[numItemsets];
        y2 = new double[numItemsets];
        count = 0;
        for (i = 0; i <= inst.numItemsets() - 1; i++) {
            y1[count] = this.predict(inst.itemset(i), smooth);
            y2[count] = inst.itemset(i).getClassValue();
            count++;
        }

        measures.correlation = M5.correlation(y1, y2, numItemsets);

        sd = M5.stdDev(inst.getClassIndex(), inst);
        if (sd > 0.0) {
            measures.meanAbsErr = errors.meanAbsErr;
            measures.meanSqrErr = errors.meanSqrErr;
            measures.type = 0;
        } else {
            if (numItemsets >= 1) {
                measures.type = 1;
                measures.meanAbsErr = errors.meanAbsErr;
                measures.meanSqrErr = errors.meanSqrErr;
            } else {
                measures.type = 2;
                measures.meanAbsErr = 0.0;
                measures.meanSqrErr = 0.0;
            }
        }

        return measures;
    }

    /**
     * Computes performance measures for both unsmoothed and smoothed models
     * @param inst itemsets
     * @return performance measures for both unsmoothed and smoothed models
     * @exception Exception if something goes wrong
     */
    public final Measures[] validation(MyDataset inst) throws Exception {

        Measures measures[] = new Measures[2];

        // without smoothing
        measures[0] = this.measures(inst, false);

        // with smoothing
        if (model == MODEL_TREE) {
            measures[1] = this.measures(inst, true);
        } else {
            measures[1] = new Measures();
        }

        return measures;
    }

    /**
     * Makes a copy of the tree under this node
     * @param up the parant node of the new node
     * @return a copy of the tree under this node
     * @exception Exception if something goes wrong
     */
    public final M5TreeNode copy(M5TreeNode up) throws Exception {

        M5TreeNode node = new M5TreeNode(itemsets, upNode);

        node.type = type;
        node.splitAttr = splitAttr;
        node.splitValue = splitValue;
        node.unsmoothed = unsmoothed.copy();
        node.smoothed = smoothed.copy();
        node.valueNode = valueNode;
        node.upNode = up;
        if (errors == null) {
            node.errors = null;
        } else {
            node.errors = errors.copy();
        }
        node.numParameters = node.numParameters;
        if (sf == null) {
            node.sf = null;
        } else {
            node.sf = sf.copy();
        }
        node.itemsets = new MyDataset(itemsets, 0,
                                         itemsets.numItemsets());
        node.lm = lm;

        node.model = model;
        node.pruningFactor = pruningFactor;
        node.deviation = deviation;

        if (leftNode != null) {
            node.leftNode = leftNode.copy(node);
        } else {
            node.leftNode = null;
        }
        if (rightNode != null) {
            node.rightNode = rightNode.copy(node);
        } else {
            node.rightNode = null;
        }

        return node;
    }

    /**
     * Converts the performance measures into a string
     * @param measures contains both the unsmoothed and smoothed measures
     * @param inst the itemsets
     * @param lmNo also converts the predictions by all linear models if lmNo=0,
     *        or one linear model spedified by lmNo.
     * @param verbosity the verbosity level
     * @param str the type of evaluation, one of
     *        "t" for training, "T" for testing,
     *        "f" for fold training, "F" for fold testing,
     *        "x" for cross-validation
     * @return the converted string
     * @exception Exception if something goes wrong
     */
    public final String measuresToString(Measures measures[], MyDataset
                                         inst, int lmNo, int verbosity,
                                         String str) throws Exception {

        StringBuffer text = new StringBuffer();
        double absDev, sd;

        absDev = M5.absDev(inst.getClassIndex(), inst);
        sd = M5.stdDev(inst.getClassIndex(), inst);

        text.append("  Without smoothing:\n\n");
        if ((verbosity >= 2 || lmNo != 0) &&
            (str.equals("T") == true || str.equals("F") == true)) {
            text.append(predictionsToString(inst, lmNo, false));
        }
        text.append(measures[0].toString(absDev, sd, str, "u") + "\n\n");
        text.append("  With smoothing:\n\n");
        if ((verbosity >= 2 || lmNo != 0) &&
            (str.equals("T") == true || str.equals("F") == true)) {
            text.append(this.predictionsToString(inst, lmNo, true));
        }
        text.append(measures[1].toString(absDev, sd, str, "s") + "\n\n");

        return text.toString();
    }

    public boolean isLeaf(){
      return !type;
    }

    public Function getUnsmoothedFunction(){
      return unsmoothed;
    }

    public Function getSmoothedFunction(){
      return smoothed;
    }

    public int getSplitingAttribute(){
      return splitAttr;
    }

    public double getSplitingValue(){
      return splitValue;
    }

    public M5TreeNode getLeftChild(){
      return leftNode;
    }

    public M5TreeNode getRightChild(){
      return rightNode;
    }



}

