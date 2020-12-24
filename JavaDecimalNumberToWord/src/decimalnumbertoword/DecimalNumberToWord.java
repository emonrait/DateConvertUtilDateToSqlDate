/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decimalnumbertoword;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 *
 * @author Raihan Mahamud
 */
public class DecimalNumberToWord {

    private static final double MAX_LIMIT = 999999.99;
    private static final String OUT_OF_BOUNDS_INPUT = "Value must be greater than 0 "
            + "and lower or equal to " + String.valueOf(MAX_LIMIT);
    private static final String INVALID_INPUT = "Unknow number pattern informed";
    private static final String ZERO = "Zero Taka";

    private static final String[] oneToNineteenNames = {
        "", // sentinel value
        "One",
        "Two",
        "Three",
        "Four",
        "Five",
        "Six",
        "Seven",
        "Eight",
        "Nine",
        "Ten",
        "Eleven",
        "Twelve",
        "Thirteen",
        "Fourteen",
        "Fifteen",
        "Sixteen",
        "Seventeen",
        "Eighteen",
        "Nineteen"
    };

    private static final String[] tenToNinetyNames = {
        "", // sentinel value
        "Ten",
        "Twenty",
        "Thirty",
        "Forty",
        "Fifty",
        "Sixty",
        "Seventy",
        "Eighty",
        "Ninety"
    };

    public static String convert(String value) throws IllegalArgumentException {
        double number;
        try {
            number = Double.parseDouble(getUSPatternNumber(value));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (number < 0.0 || number > (int) MAX_LIMIT) {
            throw new IllegalArgumentException(OUT_OF_BOUNDS_INPUT);
        }
        if (number == 0.0) {
            return ZERO;
        }

        return capitalize(translate(value));
    }

    /**
     * enum type that represents either integer of floating point types
     */
    private enum NUM_TYPE {
        INTEGER,
        FLOATING_POINT
    }

    /**
     * Gets the words representation for numbers according to the decimal system
     *
     * @param value to be translated to words
     * @return String decimal representation of the digits
     */
    private static String getHundredsTensOnes(int value) {
        StringBuilder returnValue = new StringBuilder();
        if (value % 100 < 20) {
            returnValue.append(oneToNineteenNames[value % 100]);
            value /= 100;
        } else {
            returnValue.append(oneToNineteenNames[value % 10]);
            value /= 10;
            returnValue.insert(0, tenToNinetyNames[value % 10] + " ");
            value /= 10;
        }
        if (value > 0) {
            returnValue.insert(0, oneToNineteenNames[value] + " Hundred ");
        }
        return returnValue.toString();
    }

    /**
     * Translates the taka value of a number
     *
     * @param taka value
     * @return words representation of taka, empty string if taka equal to
 zero
     */
    private static String translateTaka(String taka) throws IllegalArgumentException, ParseException {
        StringBuilder dollarsStrBuilder = new StringBuilder(256);
        String[] values = taka.split(",");
        int valuesIndex = 0;
        if (getIntValue(taka) == 0) {
            // value is lower than one dollar, so print only cents
            return "";
        }
        switch (values.length) {
            case 2: // thousands
                dollarsStrBuilder.append(getHundredsTensOnes(getIntValue(values[valuesIndex++])));
                dollarsStrBuilder.append(" Thousand ");
            case 1: // hundreds or less
                dollarsStrBuilder.append(getHundredsTensOnes(getIntValue(values[valuesIndex])));
                break;
        }
        return dollarsStrBuilder.append(" Taka ").toString();
    }

    /**
     * Translates the paisa values of a floating point value
     *
     * @param paisa value
     * @return words representation of paisa, empty string if paisa equal to
     * zero
     */
    private static String translatePaisa(String paisa) throws ParseException {
        if (paisa.equals("00")) {
            return "";
        }
        return getHundredsTensOnes(getIntValue(paisa)) + " Paisa ";
    }

    /**
     * Does the translation of values from numeral to words
     *
     * @param number to be translated
     * @return a String containing the word version of the informed number
     * @throws Exception
     */
    private static String translate(String number) throws IllegalArgumentException {
        StringBuilder numberInWords = new StringBuilder(256);
        try {
            switch (identifyType(number)) {
                case INTEGER:
                    numberInWords.append(translateTaka(number));
                    break;
                case FLOATING_POINT:
                    int cents_position = number.length() - 2;
                    boolean isMoreThanOneDollar = true;
                    String cents = "";

                    numberInWords.append(translateTaka(number.substring(0, cents_position - 1)));
                    if (numberInWords.toString().equals("")) {
                        isMoreThanOneDollar = false;
                    }
                    cents = translatePaisa(number.substring(cents_position));
                    if (isMoreThanOneDollar && !cents.equals("")) {
                        // gets the "and zero cents" case
                        numberInWords.append("and ");
                    }
                    numberInWords.append(cents);
                    break;
                default:
                    throw new IllegalArgumentException(INVALID_INPUT);
            }
        } catch (IllegalArgumentException | ParseException ex) {
            System.err.println(ex);
            throw new IllegalArgumentException(ex);
        }
        return numberInWords.toString();
    }

    /**
     * Identifies if the informed number is an integer or a floating point
     * number
     *
     * @param number to be tested
     * @return {@code enum NUM_TYPE} value representing integer or floating
     * point
     */
    private static NUM_TYPE identifyType(String number) {
        if (isFloatingPoint(number)) {
            return NUM_TYPE.FLOATING_POINT;
        }
        return NUM_TYPE.INTEGER;
    }

    /**
     * @param number to be evaluated
     * @return true if value fits floating point pattern of string
     * @throws IllegalArgumentException if argument is of invalid type
     */
    private static boolean isFloatingPoint(String number) throws IllegalArgumentException {
        if (!isValid(number)) {
            throw new IllegalArgumentException(INVALID_INPUT);
        }
        return Pattern.matches(".*(\\.\\d\\d)$", number);
    }

    /**
     * Gets the digits of the given value by the US number standards, that is,
     * 99,999 is transformed to 99999
     *
     * @param value to be evaluated
     * @return String containing the digits of the number
     * @throws ParseException if {@code value} is of unrecognizable value
     */
    private static String getUSPatternNumber(String value) throws ParseException {
        return NumberFormat.getNumberInstance(Locale.US).parse(value).toString();
    }

    /**
     * Capitalizes the first character of a string
     *
     * @param line
     * @return String containing capitalized line
     */
    private static String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    /**
     * Helper function
     *
     * @param string to be evaluated
     * @return returns the integer representation of a string
     */
    private static int getIntValue(String s) throws ParseException {
        return Math.abs(Integer.valueOf(getUSPatternNumber(s)));
    }

    /**
     * Tests if user entered a number with error format regarding commas. Commas
     * required between powers of 1,000 Can't start with "." Pass: (1,000,000),
     * (0.01) Fail: (1000000), (1,00,00,00), (.01)
     *
     * @param values to be tested
     * @throws IllegalArgumentException if a single value has more than 3 digits
     * @return true if value informed is within valid restrictions, false
     * otherwise
     */
    private static boolean isValid(String number) {
        if (!Pattern.matches("^\\d{1,3}(,\\d{3})*(\\.\\d\\d)?$", number)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            System.out.println(DecimalNumberToWord.convert("156,443.16"));
            System.out.println(DecimalNumberToWord.convert("123"));
            System.out.println(DecimalNumberToWord.convert("25.12"));
            System.out.println(DecimalNumberToWord.convert("512.00"));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
