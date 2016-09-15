/*
 *
 *      Macchiato Designer Programming Language
 *      Interpreter
 *
 */

public class Interpreter {
    static Evaluator evalObject;
    static Parser parseObject;

    public static void main(String[] args) throws Exception {

        evalObject = new Evaluator();
        parseObject = new Parser();

        evalObject.runEval(parseObject.parse(args[0]));
    }
}