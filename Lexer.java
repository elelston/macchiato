/*
*
*		Designer Programming Language
*		Lexical Analyzer Module
*		CS 403 : Spring 2016
*
*		Class: Lexer
*		Macchiato
*		Emily Huynh
*
*/


import java.io.FileReader;
import java.io.PushbackReader;
import java.io.BufferedReader;

public class Lexer {

    //variables
    String filename;
    PushbackReader input;
    int r;
    char ch;
    int line;

    // Lexer constructor, takes in a filename
    public Lexer(String f) throws Exception {
        filename = f;
        input = new PushbackReader(new BufferedReader(new FileReader(filename)));
        line = 1;
    }

    public void skipWhiteSpace() throws Exception {

        while(ch == '\n' || ch == '\t' || ch == ' ' || ch == '\r') {
            if (ch == '\n') {
                line++;
            }
            r = input.read();
            ch = (char) r;
        }

        //check to see if we have a comment
        if (ch == '&') {
            r = input.read();
            ch = (char) r;

            //check to see if we have a block comment
            if (ch == '^') {
                r = input.read();
                ch = (char) r;

                while (ch != '^') {
                    if (ch == '\n')
                        line++;
                    r = input.read();
                    ch = (char) r;
                }

                r = input.read();
                ch = (char) r;
                if (ch != '&') {
                    System.err.printf("Error! Line %d: Single ^ character.", line);
                    System.err.println("Were you trying to make a block comment with &^?");
                    System.exit(1);
                }

                r = input.read();
                ch = (char) r;
                skipWhiteSpace();
            }

            //comment out the rest of the file
            else if (ch == '*') {
                while (r != -1) {
                    r = input.read();
                    ch = (char) r;
                }
            }
            //there is an invalid character
            else if (ch != '&') {

                System.err.printf("Error! Line %d: Single ampersand character.", line);
                System.err.println("Were you trying to make a comment with &&?");
                System.exit(1);

            }

            //we have a one-line comment
            //read to end of the line
            else {
                while (ch != '\n') {
                    r = input.read();
                    ch = (char) r;
                }
                skipWhiteSpace();
            }
        }
    }

    public Lexeme lexString() throws Exception {
        String buffer = "" + ch;
        ch = (char) input.read();

        while (ch != '\"') {

            //check for double quotes within string
            if (ch == '\\') {
                ch = (char) input.read();
            }
            buffer += ch;
            ch = (char) input.read();
        }

        //grab the last quote
        buffer += ch;
        return new Lexeme("STRING", buffer);
    }

    public Lexeme lexWord() throws Exception {
        String buffer = "" + ch;

        ch = (char) input.read();

        while (Character.isDigit(ch) || Character.isLetter(ch) || ch == '_' || ch == '-' || ch == '?' || ch == '!') {
            buffer += ch;
            ch = (char) input.read();
        }

        input.unread(ch);

        //switch statement for keywords
        if (buffer.equals("brew")) {
            return new Lexeme("BREW");
        }
        else if (buffer.equals("latte")) {
            return new Lexeme("LATTE");
        }
        else if (buffer.equals("while")) {
            return new Lexeme("WHILE");
        }
        else if (buffer.equals("if")) {
            return new Lexeme("IF");
        }
        else if (buffer.equals("else")) {
            return new Lexeme("ELSE");
        }
        else if (buffer.equals("and")) {
            return new Lexeme("AND");
        }
        else if (buffer.equals("or")) {
            return new Lexeme("OR");
        }
        else if (buffer.equals("true")) {
            return new Lexeme("BOOLEAN", true);
        }
        else if (buffer.equals("false")) {
            return new Lexeme("BOOLEAN", false);
        }
        else if (buffer.equals("empty")) {
            return new Lexeme("EMPTY");
        }
        else if (buffer.equals("steam")) {
            return new Lexeme("STEAM");
        }
        else if (buffer.equals("steamln")) {
            return new Lexeme(("STEAMLN"));
        }
        return new Lexeme("ID", buffer);
    }

    public Lexeme lexNumber() throws Exception {
        boolean isReal = false;
        String buffer = "" + ch;
        ch = (char) input.read();

        while (Character.isDigit(ch) || ch == '.') {
            //check if we have a real
            if (ch == '.') {
                isReal = true;
            }
            buffer += ch;
            ch = (char) input.read();
        }

        input.unread(ch);

        if (isReal) {
            return new Lexeme("REAL", Double.parseDouble(buffer));
        }
        else {
            return new Lexeme("INTEGER", Integer.parseInt(buffer));
        }
    }

    public Lexeme lex() throws Exception {

        //read in the first char
        r = input.read();
        ch = (char) r;


        //handle whitespace, if necessary
        skipWhiteSpace();

        if(r == -1) {
            return new Lexeme("END_OF_INPUT");
        }

        switch(ch) {

            //single character tokens
            case '(':
                return new Lexeme("OPAREN");
            case ')':
                return new Lexeme("CPAREN");
            case '[':
                return new Lexeme("OBRACKET");
            case ']':
                return new Lexeme("CBRACKET");
            case '{':
                return new Lexeme("OBRACE");
            case '}':
                return new Lexeme("CBRACE");
            case ',':
                return new Lexeme("COMMA");
            case ';':
                return new Lexeme("SEMICOLON");
            case ':':
                return new Lexeme("ASSIGN");
            case '=':
                return new Lexeme("EQUALTO");
            case '+':
                return new Lexeme("PLUS");
            case '-':
                return new Lexeme("MINUS");
            case '*':
                return new Lexeme("TIMES");
            case '/':
                return new Lexeme("DIVIDE");
            case '%':
                return new Lexeme("MOD");
            case '^':
                return new Lexeme("EXP");
            case '~':
                return new Lexeme("NOT");
            case '>':
                return new Lexeme("GREATERTHAN");
            case '<':
                return new Lexeme("LESSTHAN");
            case '$':
                return new Lexeme("DOLLARSIGN");
            case '|':
                return new Lexeme("VBAR");
            case '.':
                return new Lexeme("DOT");
            case '_':
                return new Lexeme("UNDERSCORE");
            case '?':
                return new Lexeme("QUESTION");
            case '!':
                return new Lexeme("EXCLAMATION");
            default:
                //multi-character tokens
                //numbers, variables/keywords, and strings
                if (Character.isDigit(ch)) {
                    return lexNumber();
                }
                else if (Character.isLetter(ch)) {
                    return lexWord();
                }
                else if (ch == '\"') {
                    return lexString();
                }
                else {
                    return new Lexeme("UNKNOWN", ch);
                }
        }

    }

}
