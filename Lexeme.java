/*
*
*		Designer Programming Language
*		Lexical Analyzer Module
*		CS 403 : Spring 2016
*
*		Class: Lexeme
*		Macchiato
*		Emily Huynh
*
*/

import java.util.ArrayList;

class Lexeme {

    //hold our types and values
    String type;
    Lexeme left;
    Lexeme right;

    //values
    String strVal;
    int intVal;
    double realVal;
    char charVal;
    boolean boolVal;
    ArrayList<Lexeme> arrVal;

    //constructors
    public Lexeme() { }

    public Lexeme(String t) {
        type = t;
    }

    public Lexeme(String t, Lexeme l, Lexeme r) {
        type = t;
        left = l;
        right = r;
    }

    public Lexeme(String t, String str) {
        type = t;
        strVal = str;
    }

    public Lexeme(String t, ArrayList<Lexeme> arr) {
        type = t;
        arrVal = arr;
    }

    public Lexeme(String t, int integer) {
        type = t;
        intVal = integer;
    }

    public Lexeme(String t, double real) {
        type = t;
        realVal = real;
    }

    public Lexeme(String t, char c) {
        type = t;
        charVal = c;
    }

    public Lexeme(String t, boolean b) {
        type = t;
        boolVal = b;
    }

    //toString method
    public String toString() {
        String result = "";

        if (type.equals("INTEGER")) {
            return result + intVal;
        }
        else if (type.equals("REAL")) {
            return result + realVal;
        }
        else if (type.equals("STRING") || type.equals("VARIABLE")) {
            return result + strVal;
        }
        else if (type.equals("BOOLEAN")) {
            return result + boolVal;
        }
        else if (type.equals("EMPTY")) {
            return "empty";
        }
        else if (type.equals("UNKNOWN")) {
            return result + charVal;
        }
        else {
            return type;
        }

    }
//    //toString method
//    public String toString() {
//        String result = "";
//
//        if (type.equals("INTEGER")) {
//            return type + " " + intVal;
//        }
//        else if (type.equals("REAL")) {
//            return type + " " + realVal;
//        }
//        else if (type.equals("STRING") || type.equals("VARIABLE")) {
//            return type + " " + strVal;
//        }
//        else if (type.equals("UNKNOWN")) {
//            return type + " " + charVal;
//        }
//        else {
//            return type;
//        }
//
//    }


}
