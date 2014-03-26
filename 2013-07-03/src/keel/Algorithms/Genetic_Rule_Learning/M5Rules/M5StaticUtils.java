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
 * Class implementing some simple utility methods.
 */
public final class M5StaticUtils {

    /** The natural logarithm of 2. */
    public static double log2 = Math.log(2);

    /** The small deviation allowed in double comparisons */
    public static double SMALL = 1e-6;

    /**
     * Removes all occurrences of a string from another string.
     *
     * @param inString the string to remove substrings from.
     * @param substring the substring to remove.
     * @return the input string with occurrences of substring removed.
     */
    public static String removeSubstring(String inString, String substring) {

        StringBuffer result = new StringBuffer();
        int oldLoc = 0, loc = 0;
        while ((loc = inString.indexOf(substring, oldLoc)) != -1) {
            result.append(inString.substring(oldLoc, loc));
            oldLoc = loc + substring.length();
        }
        result.append(inString.substring(oldLoc));
        return result.toString();
    }

    /**
     * Replaces with a new string, all occurrences of a string from
     * another string.
     *
     * @param inString the string to replace substrings in.
     * @param subString the substring to replace.
     * @param replaceString the replacement substring
     * @return the input string with occurrences of substring replaced.
     */
    public static String replaceSubstring(String inString, String subString,
                                          String replaceString) {

        StringBuffer result = new StringBuffer();
        int oldLoc = 0, loc = 0;
        while ((loc = inString.indexOf(subString, oldLoc)) != -1) {
            result.append(inString.substring(oldLoc, loc));
            result.append(replaceString);
            oldLoc = loc + subString.length();
        }
        result.append(inString.substring(oldLoc));
        return result.toString();
    }


    /**
     * Pads a string to a specified length, inserting spaces on the left
     * as required. If the string is too long, characters are removed (from
     * the right).
     *
     * @param inString the input string
     * @param length the desired length of the output string
     * @return the output string
     */
    public static String padLeft(String inString, int length) {

        return fixStringLength(inString, length, false);
    }

    /**
     * Pads a string to a specified length, inserting spaces on the right
     * as required. If the string is too long, characters are removed (from
     * the right).
     *
     * @param inString the input string
     * @param length the desired length of the output string
     * @return the output string
     */
    public static String padRight(String inString, int length) {

        return fixStringLength(inString, length, true);
    }

    /**
     * Pads a string to a specified length, inserting spaces as
     * required. If the string is too long, characters are removed (from
     * the right).
     *
     * @param inString the input string
     * @param length the desired length of the output string
     * @param right true if inserted spaces should be added to the right
     * @return the output string
     */
    private static String fixStringLength(String inString, int length,
                                          boolean right) {

        if (inString.length() < length) {
            while (inString.length() < length) {
                inString = (right ? inString.concat(" ") : " ".concat(inString));
            }
        } else if (inString.length() > length) {
            inString = inString.substring(0, length);
        }
        return inString;
    }

    /**
     * Rounds a double and converts it into String.
     *
     * @param value the double value
     * @param afterDecimalPoint the (maximum) number of digits permitted
     * after the decimal point
     * @return the double as a formatted string
     */
    public static String doubleToString(double value, int afterDecimalPoint) {

        StringBuffer stringBuffer;
        double temp;
        int i, dotPosition;
        long precisionValue;

        temp = value * Math.pow(10.0, afterDecimalPoint);
        if (Math.abs(temp) < Long.MAX_VALUE) {
            precisionValue = (temp > 0) ? (long) (temp + 0.5)
                             : -(long) (Math.abs(temp) + 0.5);
            if (precisionValue == 0) {
                stringBuffer = new StringBuffer(String.valueOf(0));
            } else {
                stringBuffer = new StringBuffer(String.valueOf(precisionValue));
            }
            if (afterDecimalPoint == 0) {
                return stringBuffer.toString();
            }
            dotPosition = stringBuffer.length() - afterDecimalPoint;
            while (((precisionValue < 0) && (dotPosition < 1)) ||
                   (dotPosition < 0)) {
                if (precisionValue < 0) {
                    stringBuffer.insert(1, 0);
                } else {
                    stringBuffer.insert(0, 0);
                }
                dotPosition++;
            }
            stringBuffer.insert(dotPosition, '.');
            if ((precisionValue < 0) && (stringBuffer.charAt(1) == '.')) {
                stringBuffer.insert(1, 0);
            } else if (stringBuffer.charAt(0) == '.') {
                stringBuffer.insert(0, 0);
            }
            int currentPos = stringBuffer.length() - 1;
            while ((currentPos > dotPosition) &&
                   (stringBuffer.charAt(currentPos) == '0')) {
                stringBuffer.setCharAt(currentPos--, ' ');
            }
            if (stringBuffer.charAt(currentPos) == '.') {
                stringBuffer.setCharAt(currentPos, ' ');
            }

            return stringBuffer.toString().trim();
        }
        return new String("" + value);
    }

    /**
     * Rounds a double and converts it into a formatted decimal-justified String.
     * Trailing 0's are replaced with spaces.
     *
     * @param value the double value
     * @param width the width of the string
     * @param afterDecimalPoint the number of digits after the decimal point
     * @return the double as a formatted string
     */
    public static String doubleToString(double value, int width,
                                        int afterDecimalPoint) {

        String tempString = doubleToString(value, afterDecimalPoint);
        char[] result;
        int dotPosition;

        if ((afterDecimalPoint >= width)
            || (tempString.indexOf('E') != -1)) { // Protects sci notation
            return tempString;
        }

        // Initialize result
        result = new char[width];
        for (int i = 0; i < result.length; i++) {
            result[i] = ' ';
        }

        if (afterDecimalPoint > 0) {
            // Get position of decimal point and insert decimal point
            dotPosition = tempString.indexOf('.');
            if (dotPosition == -1) {
                dotPosition = tempString.length();
            } else {
                result[width - afterDecimalPoint - 1] = '.';
            }
        } else {
            dotPosition = tempString.length();
        }

        int offset = width - afterDecimalPoint - dotPosition;
        if (afterDecimalPoint > 0) {
            offset--;
        }

        // Not enough room to decimal align within the supplied width
        if (offset < 0) {
            return tempString;
        }

        // Copy characters before decimal point
        for (int i = 0; i < dotPosition; i++) {
            result[offset + i] = tempString.charAt(i);
        }

        // Copy characters after decimal point
        for (int i = dotPosition + 1; i < tempString.length(); i++) {
            result[offset + i] = tempString.charAt(i);
        }

        return new String(result);
    }

    /**
     * Tests if a is equal to b.
     *
     * @param a a double
     * @param b a double
     * @return if a is equal to b.
     */
    public static boolean eq(double a, double b) {

        return (a - b < SMALL) && (b - a < SMALL);
    }

    /**
     * Checks if the given array contains any non-empty options.
     *
     * @param options strings an array of strings
     * @exception Exception if there are any non-empty options
     */
    public static void checkForRemainingOptions(String[] options) throws
            Exception {

        int illegalOptionsFound = 0;
        StringBuffer text = new StringBuffer();

        if (options == null) {
            return;
        }
        for (int i = 0; i < options.length; i++) {
            if (options[i].length() > 0) {
                illegalOptionsFound++;
                text.append(options[i] + ' ');
            }
        }
        if (illegalOptionsFound > 0) {
            throw new Exception("Illegal options: " + text);
        }
    }

    /**
     * Checks if the given array contains the flag "-Char". Stops
     * searching at the first marker "--". If the flag is found,
     * it is replaced with the empty string.
     *
     * @param flag the character indicating the flag.
     * @param options strings the array of strings containing all the options.
     * @return true if the flag was found
     * @exception Exception if an illegal option was found
     */

    public static boolean getFlag(char flag, String[] options) throws Exception {

        if (options == null) {
            return false;
        }
        for (int i = 0; i < options.length; i++) {
            if ((options[i].length() > 1) && (options[i].charAt(0) == '-')) {
                try {
                    Double dummy = Double.valueOf(options[i]);
                } catch (NumberFormatException e) {
                    if (options[i].length() > 2) {
                        throw new Exception("Illegal option: " + options[i]);
                    }
                    if (options[i].charAt(1) == flag) {
                        options[i] = "";
                        return true;
                    }
                    if (options[i].charAt(1) == '-') {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets an option indicated by a flag "-Char" from the given array
     * of strings. Stops searching at the first marker "--". Replaces
     * flag and option with empty strings.
     *
     * @param flag the character indicating the option.
     * @param options the array of strings containing all the options.
     * @return the indicated option or an empty string
     * @exception Exception if the option indicated by the flag can't be found
     */
    public static String getOption(char flag, String[] options) throws
            Exception {

        String newString;

        if (options == null) {
            return "";
        }
        for (int i = 0; i < options.length; i++) {
            if ((options[i].length() > 0) && (options[i].charAt(0) == '-')) {

                // Check if it is a negative number
                try {
                    Double dummy = Double.valueOf(options[i]);
                } catch (NumberFormatException e) {
                    if (options[i].length() > 2) {
                        throw new Exception("Illegal option: " + options[i]);
                    }
                    if (options[i].charAt(1) == flag) {
                        if (i + 1 == options.length) {
                            throw new Exception("No value given for -" + flag +
                                                " option.");
                        }
                        options[i] = "";
                        newString = new String(options[i + 1]);
                        options[i + 1] = "";
                        return newString;
                    }
                    if (options[i].charAt(1) == '-') {
                        return "";
                    }
                }
            }
        }
        return "";
    }

    /**
     * Quotes a string if it contains special characters.
     *
     * The following rules are applied:
     *
     * A character is backquoted version of it is one
     * of <tt>" ' % \ \n \r \t</tt>.
     *
     * A string is enclosed within single quotes if a character has been
     * backquoted using the previous rule above or contains
     * <tt>{ }</tt> or is exactly equal to the strings
     * <tt>, ? space or ""</tt> (empty string).
     *
     * A quoted question mark distinguishes it from the missing value which
     * is represented as an unquoted question mark.
     *
     * @param string the string to be quoted
     * @return the string (possibly quoted)
     */
    public static String quote(String string) {
        boolean quote = false;

        // backquote the following characters
        if ((string.indexOf('\n') != -1) || (string.indexOf('\r') != -1) ||
            (string.indexOf('\'') != -1) || (string.indexOf('"') != -1) ||
            (string.indexOf('\\') != -1) ||
            (string.indexOf('\t') != -1) || (string.indexOf('%') != -1)) {
            string = backQuoteChars(string);
            quote = true;
        }

        // Enclose the string in 's if the string contains a recently added
        // backquote or contains one of the following characters.
        if ((quote == true) ||
            (string.indexOf('{') != -1) || (string.indexOf('}') != -1) ||
            (string.indexOf(',') != -1) || (string.equals("?")) ||
            (string.indexOf(' ') != -1) || (string.equals(""))) {
            string = ("'".concat(string)).concat("'");
        }

        return string;
    }

    /**
     * Converts carriage returns and new lines in a string into \r and \n.
     * Backquotes the following characters: ` " \ \t and %
     * @param string the string
     * @return the converted string
     */
    public static String backQuoteChars(String string) {

        int index;
        StringBuffer newStringBuffer;

        // replace each of the following characters with the backquoted version
        char charsFind[] = {'\\', '\'', '\t', '"', '%'};
        String charsReplace[] = {"\\\\", "\\'", "\\t", "\\\"", "\\%"};
        for (int i = 0; i < charsFind.length; i++) {
            if (string.indexOf(charsFind[i]) != -1) {
                newStringBuffer = new StringBuffer();
                while ((index = string.indexOf(charsFind[i])) != -1) {
                    if (index > 0) {
                        newStringBuffer.append(string.substring(0, index));
                    }
                    newStringBuffer.append(charsReplace[i]);
                    if ((index + 1) < string.length()) {
                        string = string.substring(index + 1);
                    } else {
                        string = "";
                    }
                }
                newStringBuffer.append(string);
                string = newStringBuffer.toString();
            }
        }

        return M5StaticUtils.convertNewLines(string);
    }

    /**
     * Converts carriage returns and new lines in a string into \r and \n.
     * @param string the string
     * @return the converted string
     */
    public static String convertNewLines(String string) {

        int index;

        // Replace with \n
        StringBuffer newStringBuffer = new StringBuffer();
        while ((index = string.indexOf('\n')) != -1) {
            if (index > 0) {
                newStringBuffer.append(string.substring(0, index));
            }
            newStringBuffer.append('\\');
            newStringBuffer.append('n');
            if ((index + 1) < string.length()) {
                string = string.substring(index + 1);
            } else {
                string = "";
            }
        }
        newStringBuffer.append(string);
        string = newStringBuffer.toString();

        // Replace with \r
        newStringBuffer = new StringBuffer();
        while ((index = string.indexOf('\r')) != -1) {
            if (index > 0) {
                newStringBuffer.append(string.substring(0, index));
            }
            newStringBuffer.append('\\');
            newStringBuffer.append('r');
            if ((index + 1) < string.length()) {
                string = string.substring(index + 1);
            } else {
                string = "";
            }
        }
        newStringBuffer.append(string);
        return newStringBuffer.toString();
    }


    /**
     * Returns the secondary set of options (if any) contained in
     * the supplied options array. The secondary set is defined to
     * be any options after the first "--". These options are removed from
     * the original options array.
     *
     * @param options the input array of options
     * @return the array of secondary options
     */
    public static String[] partitionOptions(String[] options) {

        for (int i = 0; i < options.length; i++) {
            if (options[i].equals("--")) {
                options[i++] = "";
                String[] result = new String[options.length - i];
                for (int j = i; j < options.length; j++) {
                    result[j - i] = options[j];
                    options[j] = "";
                }
                return result;
            }
        }
        return new String[0];
    }

    /**
     * Joins all the options in an option array into a single string,
     * as might be used on the command line.
     *
     * @param optionArray the array of options
     * @return the string containing all options.
     */
    public static String joinOptions(String[] optionArray) {

        String optionString = "";
        for (int i = 0; i < optionArray.length; i++) {
            if (optionArray[i].equals("")) {
                continue;
            }
            if (optionArray[i].indexOf(' ') != -1) {
                optionString += '"' + optionArray[i] + '"';
            } else {
                optionString += optionArray[i];
            }
            optionString += " ";
        }
        return optionString.trim();
    }

    /**
     * Creates a new instance of an object given it's class name and
     * (optional) arguments to pass to it's setOptions method. If the
     * object implements OptionHandler and the options parameter is
     * non-null, the object will have it's options set. Example use:<p>
     *
     * <code> <pre>
     * String classifierName = M5StaticUtils.getOption('W', options);
     * Classifier c = (Classifier)M5StaticUtils.forName(Classifier.class,
     *                                          classifierName,
     *                                          options);
     * setClassifier(c);
     * </pre></code>
     *
     * @param classType the class that the instantiated object should
     * be assignable to -- an exception is thrown if this is not the case
     * @param className the fully qualified class name of the object
     * @param options an array of options suitable for passing to setOptions. May
     * be null. Any options accepted by the object will be removed from the
     * array.
     * @return the newly created object, ready for use.
     * @exception Exception if the class name is invalid, or if the
     * class is not assignable to the desired class type, or the options
     * supplied are not acceptable to the object
     */
    /*public static Object forName(Class classType,
                                 String className,
                                 String[] options) throws Exception {

        Class c = null;
        try {
            c = Class.forName(className);
        } catch (Exception ex) {
            throw new Exception("Can't find class called: " + className);
        }
        if (!classType.isAssignableFrom(c)) {
            throw new Exception(classType.getName() +
                                " is not assignable from "
                                + className);
        }
        Object o = c.newInstance();
        if ((o instanceof M5)
            && (options != null)) {
            ((M5) o).setOptions(options);
            M5StaticUtils.checkForRemainingOptions(options);
        }
        return o;
    }*/

    /**
     * Computes entropy for an array of integers.
     *
     * @param counts array of counts
     * @return - a log2 a - b log2 b - c log2 c + (a+b+c) log2 (a+b+c)
     * when given array [a b c]
     */
    public static double info(int counts[]) {

        int total = 0;
        int c;
        double x = 0;
        for (int j = 0; j < counts.length; j++) {
            x -= xlogx(counts[j]);
            total += counts[j];
        }
        return x + xlogx(total);
    }

    /**
     * Tests if a is smaller or equal to b.
     *
     * @param a a double
     * @param b a double
     * @return if a is smaller or equal to b.
     */
    public static boolean smOrEq(double a, double b) {

        return (a - b < SMALL);
    }

    /**
     * Tests if a is greater or equal to b.
     *
     * @param a a double
     * @param b a double
     * @return if a is greater or equal to b.
     */
    public static boolean grOrEq(double a, double b) {

        return (b - a < SMALL);
    }

    /**
     * Tests if a is smaller than b.
     *
     * @param a a double
     * @param b a double
     * @return if a is smaller than b.
     */
    public static boolean sm(double a, double b) {

        return (b - a > SMALL);
    }

    /**
     * Tests if a is smaller than b.
     *
     * @param a a double
     * @param b a double
     * @return if a is smaller than b.
     */
    public static boolean gr(double a, double b) {

        return (a - b > SMALL);
    }

    /**
     * Returns the logarithm of a for base 2.
     *
     * @param a a double
     * @return the logarithm of a for base 2.
     */
    public static double log2(double a) {

        return Math.log(a) / log2;
    }

    /**
     * Returns index of maximum element in a given
     * array of doubles. First maximum is returned.
     *
     * @param doubles the array of doubles
     * @return the index of the maximum element
     */
    public static int maxIndex(double[] doubles) {

        double maximum = 0;
        int maxIndex = 0;

        for (int i = 0; i < doubles.length; i++) {
            if ((i == 0) || (doubles[i] > maximum)) {
                maxIndex = i;
                maximum = doubles[i];
            }
        }

        return maxIndex;
    }

    /**
     * Returns index of maximum element in a given
     * array of integers. First maximum is returned.
     *
     * @param ints the array of integers
     * @return the index of the maximum element
     */
    public static int maxIndex(int[] ints) {

        int maximum = 0;
        int maxIndex = 0;

        for (int i = 0; i < ints.length; i++) {
            if ((i == 0) || (ints[i] > maximum)) {
                maxIndex = i;
                maximum = ints[i];
            }
        }

        return maxIndex;
    }

    /**
     * Computes the mean for an array of doubles.
     *
     * @param vector the array
     * @return the mean
     */
    public static double mean(double[] vector) {

        double sum = 0;

        if (vector.length == 0) {
            return 0;
        }
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i];
        }
        return sum / (double) vector.length;
    }

    /**
     * Returns index of minimum element in a given
     * array of integers. First minimum is returned.
     *
     * @param ints the array of integers
     * @return the index of the minimum element
     */
    public static int minIndex(int[] ints) {

        int minimum = 0;
        int minIndex = 0;

        for (int i = 0; i < ints.length; i++) {
            if ((i == 0) || (ints[i] < minimum)) {
                minIndex = i;
                minimum = ints[i];
            }
        }

        return minIndex;
    }

    /**
     * Returns index of minimum element in a given
     * array of doubles. First minimum is returned.
     *
     * @param doubles the array of doubles
     * @return the index of the minimum element
     */
    public static int minIndex(double[] doubles) {

        double minimum = 0;
        int minIndex = 0;

        for (int i = 0; i < doubles.length; i++) {
            if ((i == 0) || (doubles[i] < minimum)) {
                minIndex = i;
                minimum = doubles[i];
            }
        }

        return minIndex;
    }

    /**
     * Normalizes the doubles in the array by their sum.
     *
     * @param doubles the array of double
     * @exception IllegalArgumentException if sum is Zero or NaN
     */
    public static void normalize(double[] doubles) {

        double sum = 0;
        for (int i = 0; i < doubles.length; i++) {
            sum += doubles[i];
        }
        normalize(doubles, sum);
    }

    /**
     * Normalizes the doubles in the array using the given value.
     *
     * @param doubles the array of double
     * @param sum the value by which the doubles are to be normalized
     * @exception IllegalArgumentException if sum is zero or NaN
     */
    public static void normalize(double[] doubles, double sum) {

        if (Double.isNaN(sum)) {
            throw new IllegalArgumentException(
                    "Can't normalize array. Sum is NaN.");
        }
        if (sum == 0) {
            // Maybe this should just be a return.
            throw new IllegalArgumentException(
                    "Can't normalize array. Sum is zero.");
        }
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] /= sum;
        }
    }

    /**
     * Rounds a double to the next nearest integer value. The JDK version
     * of it doesn't work properly.
     *
     * @param value the double value
     * @return the resulting integer value
     */
    public static int round(double value) {

        int roundedValue = value > 0
                           ? (int) (value + 0.5)
                           : -(int) (Math.abs(value) + 0.5);

        return roundedValue;
    }

    /**
     * Rounds a double to the given number of decimal places.
     *
     * @param value the double value
     * @param afterDecimalPoint the number of digits after the decimal point
     * @return the double rounded to the given precision
     */
    public static double roundDouble(double value, int afterDecimalPoint) {

        double mask = Math.pow(10.0, (double) afterDecimalPoint);

        return (double) (Math.round(value * mask)) / mask;
    }

    /**
     * Sorts a given array of integers in ascending order and returns an
     * array of integers with the positions of the elements of the original
     * array in the sorted array. The sort is stable. (Equal elements remain
     * in their original order.)
     *
     * @param array this array is not changed by the method!
     * @return an array of integers with the positions in the sorted
     * array.
     */
    public static int[] sort(int[] array) {

        int[] index = new int[array.length];
        int[] newIndex = new int[array.length];
        int[] helpIndex;
        int numEqual;

        for (int i = 0; i < index.length; i++) {
            index[i] = i;
        }
        quickSort(array, index, 0, array.length - 1);

        // Make sort stable
        int i = 0;
        while (i < index.length) {
            numEqual = 1;
            for (int j = i + 1; ((j < index.length)
                                 && (array[index[i]] == array[index[j]]));
                         j++) {
                numEqual++;
            }
            if (numEqual > 1) {
                helpIndex = new int[numEqual];
                for (int j = 0; j < numEqual; j++) {
                    helpIndex[j] = i + j;
                }
                quickSort(index, helpIndex, 0, numEqual - 1);
                for (int j = 0; j < numEqual; j++) {
                    newIndex[i + j] = index[helpIndex[j]];
                }
                i += numEqual;
            } else {
                newIndex[i] = index[i];
                i++;
            }
        }
        return newIndex;
    }

    /**
     * Sorts a given array of doubles in ascending order and returns an
     * array of integers with the positions of the elements of the
     * original array in the sorted array. NOTE THESE CHANGES: the sort
     * is no longer stable and it doesn't use safe floating-point
     * comparisons anymore. Occurrences of Double.NaN are treated as
     * Double.MAX_VALUE
     *
     * @param array this array is not changed by the method!
     * @return an array of integers with the positions in the sorted
     * array.
     */
    public static int[] sort(double[] array) {

        int[] index = new int[array.length];
        array = (double[]) array.clone();
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
            if (Double.isNaN(array[i])) {
                array[i] = Double.MAX_VALUE;
            }
        }
        quickSort(array, index, 0, array.length - 1);
        return index;
    }

    /**
     * Sorts a given array of doubles in ascending order and returns an
     * array of integers with the positions of the elements of the original
     * array in the sorted array. The sort is stable (Equal elements remain
     * in their original order.) Occurrences of Double.NaN are treated as
     * Double.MAX_VALUE
     *
     * @param array this array is not changed by the method!
     * @return an array of integers with the positions in the sorted
     * array.
     */
    public static int[] stableSort(double[] array) {

        int[] index = new int[array.length];
        int[] newIndex = new int[array.length];
        int[] helpIndex;
        int numEqual;

        array = (double[]) array.clone();
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
            if (Double.isNaN(array[i])) {
                array[i] = Double.MAX_VALUE;
            }
        }
        quickSort(array, index, 0, array.length - 1);

        // Make sort stable

        int i = 0;
        while (i < index.length) {
            numEqual = 1;
            for (int j = i + 1;
                         ((j < index.length) && M5StaticUtils.eq(array[index[i]],
                    array[index[j]])); j++) {
                numEqual++;
            }
            if (numEqual > 1) {
                helpIndex = new int[numEqual];
                for (int j = 0; j < numEqual; j++) {
                    helpIndex[j] = i + j;
                }
                quickSort(index, helpIndex, 0, numEqual - 1);
                for (int j = 0; j < numEqual; j++) {
                    newIndex[i + j] = index[helpIndex[j]];
                }
                i += numEqual;
            } else {
                newIndex[i] = index[i];
                i++;
            }
        }

        return newIndex;
    }

    /**
     * Computes the variance for an array of doubles.
     *
     * @param vector the array
     * @return the variance
     */
    public static double variance(double[] vector) {

        double sum = 0, sumSquared = 0;

        if (vector.length <= 1) {
            return 0;
        }
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i];
            sumSquared += (vector[i] * vector[i]);
        }
        return (sumSquared - (sum * sum / (double) vector.length)) /
                (double) (vector.length - 1);
    }

    /**
     * Computes the sum of the elements of an array of doubles.
     *
     * @param doubles the array of double
     * @return the sum of the elements
     */
    public static double sum(double[] doubles) {

        double sum = 0;

        for (int i = 0; i < doubles.length; i++) {
            sum += doubles[i];
        }
        return sum;
    }

    /**
     * Computes the sum of the elements of an array of integers.
     *
     * @param ints the array of integers
     * @return the sum of the elements
     */
    public static int sum(int[] ints) {

        int sum = 0;

        for (int i = 0; i < ints.length; i++) {
            sum += ints[i];
        }
        return sum;
    }

    /**
     * Returns c*log2(c) for a given integer value c.
     *
     * @param c an integer value
     * @return c*log2(c) (but is careful to return 0 if c is 0)
     */
    public static double xlogx(int c) {

        if (c == 0) {
            return 0.0;
        }
        return c * M5StaticUtils.log2((double) c);
    }

    /**
     * Implements quicksort for an array of indices.
     *
     * @param array the array of integers to be sorted
     * @param index the index which should contain the positions in the
     * sorted array
     * @param lo0 the first index of the subset to be sorted
     * @param hi0 the last index of the subset to be sorted
     */
    private static void quickSort(int[] array, int[] index,
                                  int lo0, int hi0) {

        int lo = lo0;
        int hi = hi0;
        int mid;
        int help;

        if (hi0 > lo0) {

            // Arbitrarily establishing partition element as the midpoint of
            // the array.
            mid = array[index[(lo0 + hi0) / 2]];

            // loop through the array until indices cross
            while (lo <= hi) {

                // find the first element that is greater than or equal to
                // the partition element starting from the left Index.
                while ((array[index[lo]] < mid) && (lo < hi0)) {
                    ++lo;
                }

                // find an element that is smaller than or equal to
                // the partition element starting from the right Index.
                while ((array[index[hi]] > mid) && (hi > lo0)) {
                    --hi;
                }

                // if the indexes have not crossed, swap
                if (lo <= hi) {
                    help = index[lo];
                    index[lo] = index[hi];
                    index[hi] = help;
                    ++lo;
                    --hi;
                }
            }

            // If the right index has not reached the left side of array
            // must now sort the left partition.
            if (lo0 < hi) {
                quickSort(array, index, lo0, hi);
            }

            // If the left index has not reached the right side of array
            // must now sort the right partition.
            if (lo < hi0) {
                quickSort(array, index, lo, hi0);
            }
        }
    }

    /**
     * Implements unsafe quicksort for an array of indices.
     *
     * @param array the array of doubles to be sorted
     * @param index the index which should contain the positions in the
     * sorted array
     * @param lo0 the first index of the subset to be sorted
     * @param hi0 the last index of the subset to be sorted
     */
    private static void quickSort(double[] array, int[] index, int lo0, int hi0) {

        int lo = lo0;
        int hi = hi0;
        double mid;
        int help;

        if (hi0 > lo0) {

            // Arbitrarily establishing partition element as the midpoint of
            // the array.
            mid = array[index[(lo0 + hi0) / 2]];

            // loop through the array until indices cross
            while (lo <= hi) {

                // find the first element that is greater than or equal to
                // the partition element starting from the left Index.
                while ((array[index[lo]] < mid) && (lo < hi0)) {
                    ++lo;
                }

                // find an element that is smaller than or equal to
                // the partition element starting from the right Index.
                while ((array[index[hi]] > mid) && (hi > lo0)) {
                    --hi;
                }

                // if the indexes have not crossed, swap
                if (lo <= hi) {
                    help = index[lo];
                    index[lo] = index[hi];
                    index[hi] = help;
                    ++lo;
                    --hi;
                }
            }

            // If the right index has not reached the left side of array
            // must now sort the left partition.
            if (lo0 < hi) {
                quickSort(array, index, lo0, hi);
            }

            // If the left index has not reached the right side of array
            // must now sort the right partition.
            if (lo < hi0) {
                quickSort(array, index, lo, hi0);
            }
        }
    }

    /**
     * Main method for testing this class.
     *
     * @param ops some dummy options
     */
  /*  public static void main(String[] ops) {

        double[] doubles = {4.5, 6.7, Double.NaN, 3.4, 4.8, 1.2, 3.4};
        int[] ints = {12, 6, 2, 18, 16, 6, 7, 5};

        try {

            // Option handling
            System.out.println("First option split up:");
            if (ops.length > 0) {
                String[] firstOptionSplitUp = M5StaticUtils.splitOptions(ops[0]);
                for (int i = 0; i < firstOptionSplitUp.length; i++) {
                    System.out.println(firstOptionSplitUp[i]);
                }
            }
            System.out.println("Partitioned options: ");
            String[] partitionedOptions = M5StaticUtils.partitionOptions(ops);
            for (int i = 0; i < partitionedOptions.length; i++) {
                System.out.println(partitionedOptions[i]);
            }
            System.out.println("Get flag -f: " + M5StaticUtils.getFlag('f', ops));
            System.out.println("Get option -o: " +
                               M5StaticUtils.getOption('o', ops));
            System.out.println("Checking for remaining options... ");
            M5StaticUtils.checkForRemainingOptions(ops);

            // Statistics
            System.out.println("Original array (doubles): ");
            for (int i = 0; i < doubles.length; i++) {
                System.out.print(doubles[i] + " ");
            }
            System.out.println();
            System.out.println("Original array (ints): ");
            for (int i = 0; i < ints.length; i++) {
                System.out.print(ints[i] + " ");
            }
            System.out.println();
            System.out.println("Correlation: " +
                               M5StaticUtils.correlation(doubles, doubles,
                    doubles.length));
            System.out.println("Mean: " + M5StaticUtils.mean(doubles));
            System.out.println("Variance: " + M5StaticUtils.variance(doubles));
            System.out.println("Sum (doubles): " + M5StaticUtils.sum(doubles));
            System.out.println("Sum (ints): " + M5StaticUtils.sum(ints));
            System.out.println("Max index (doubles): " +
                               M5StaticUtils.maxIndex(doubles));
            System.out.println("Max index (ints): " +
                               M5StaticUtils.maxIndex(ints));
            System.out.println("Min index (doubles): " +
                               M5StaticUtils.minIndex(doubles));
            System.out.println("Min index (ints): " +
                               M5StaticUtils.minIndex(ints));

            // Sorting and normalizing
            System.out.println("Sorted array (doubles): ");
            int[] sorted = M5StaticUtils.sort(doubles);
            for (int i = 0; i < doubles.length; i++) {
                System.out.print(doubles[sorted[i]] + " ");
            }
            System.out.println();
            System.out.println("Normalized array (doubles): ");
            M5StaticUtils.normalize(doubles);
            for (int i = 0; i < doubles.length; i++) {
                System.out.print(doubles[i] + " ");
            }
            System.out.println();
            System.out.println("Normalized again (doubles): ");
            M5StaticUtils.normalize(doubles, M5StaticUtils.sum(doubles));
            for (int i = 0; i < doubles.length; i++) {
                System.out.print(doubles[i] + " ");
            }
            System.out.println();

            // Pretty-printing
            System.out.println("-4.58: " +
                               M5StaticUtils.doubleToString( -4.57826535, 2));
            System.out.println("-6.78: " +
                               M5StaticUtils.doubleToString( -6.78214234, 6, 2));

            // Comparisons
            System.out.println("5.70001 == 5.7 ? " +
                               M5StaticUtils.eq(5.70001, 5.7));
            System.out.println("5.70001 > 5.7 ? " +
                               M5StaticUtils.gr(5.70001, 5.7));
            System.out.println("5.70001 >= 5.7 ? " +
                               M5StaticUtils.grOrEq(5.70001, 5.7));
            System.out.println("5.7 < 5.70001 ? " +
                               M5StaticUtils.sm(5.7, 5.70001));
            System.out.println("5.7 <= 5.70001 ? " +
                               M5StaticUtils.smOrEq(5.7, 5.70001));

            // Math
            System.out.println("Info (ints): " + M5StaticUtils.info(ints));
            System.out.println("log2(4.6): " + M5StaticUtils.log2(4.6));
            System.out.println("5 * log(5): " + M5StaticUtils.xlogx(5));
            System.out.println("5.5 rounded: " + M5StaticUtils.round(5.5));
            System.out.println("5.55555 rounded to 2 decimal places: " +
                               M5StaticUtils.roundDouble(5.55555, 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}

