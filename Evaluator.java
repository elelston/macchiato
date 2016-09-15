/*
*
*		Designer Programming Language
*		Evaluator Module
*		CS 403 : Spring 2016
*
*		Class: Evaluator
*		Macchiato
*		Emily Huynh
*
*/

import java.util.ArrayList;

public class Evaluator {

    private Lexeme global;
    private Environment e;

    public Evaluator() {
        e = new Environment();
        global = e.create();
    }

    /**************************************************************
     *
     * Evaluate a tree
     *
     ***************************************************************/
    private Lexeme eval(Lexeme tree, Lexeme env) {
        if (tree == null) {
            return null;
        }
        switch (tree.type) {

            //self-evaluating
            case "STRING": return tree;
            case "BOOLEAN": return tree;
            case "SEMICOLON": return tree;
            case "ID": return e.lookup(tree, env);
            case "DOLLARSIGN": return tree;
            case "INTEGER": return tree;
            case "REAL": return tree;
            case "LAMBDADEF": return tree;
            case "EMPTY": return tree;

            case "NEGATIVE": return evalNegative(tree, env);
            case "NOT": return evalNot(tree, env);

            //operators
            case "EQUALTO": return evalSimpleOp(tree, env);
            case "NOTEQUALTO": return evalSimpleOp(tree, env);
            case "GTHANEQUALTO": return evalSimpleOp(tree, env);
            case "LTHANEQUALTO": return evalSimpleOp(tree, env);
            case "GREATERTHAN": return evalSimpleOp(tree, env);
            case "LESSTHAN": return evalSimpleOp(tree, env);
            case "PLUS": return evalSimpleOp(tree, env);
            case "MINUS": return evalSimpleOp(tree, env);
            case "TIMES": return evalSimpleOp(tree, env);
            case "DIVIDE": return evalSimpleOp(tree, env);
            case "MOD": return evalSimpleOp(tree, env);
            case "EXP": return evalSimpleOp(tree, env);
            case "ASSIGN": return evalAssign(tree, env);


            //operators - short circuit
            case "AND": return evalShortCircuitOp(tree, env);
            case "OR": return evalShortCircuitOp(tree, env);

            //builtin functions
            case "STEAM": return evalSteam(tree, env);
            case "STEAMLN": return evalSteamLn(tree, env);

            //variables and function definitions
            case "PROGRAM": return evalProgram(tree, env);
            case "DEFINITION": return evalDefinition(tree, env);
            case "IDINIT": return evalIDInit(tree, env);
            case "BREWVARDEF": return evalBrewVarDef(tree, env);
            case "BREWFUNCDEF": return evalBrewFuncDef(tree, env);
            case "IDFUNCCALL": return evalIDFuncCall(tree, env);
            case "ARGLIST": return evalArgList(tree, env);
            case "ARRAYDEF": return evalArrayDef(tree, env);
            case "ARRACCESS": return evalArrAccess(tree, env);
            case "PARENEXPR": return evalParenExpr(tree, env);
            case "LAMBDACALL": return evalLambdaCall(tree, env);
            case "BLOCK": return evalBlock(tree, env);
            case "DOT": return evalDot(tree, env);

            //statements
            case "STATEMENTLIST": return evalStatementList(tree, env);
            case "STATEMENT": return evalStatement(tree, env);
            case "IFSTATEMENT": return evalIfStatement(tree, env);
            case "OPTELSE": return evalOptElse(tree, env);
            case "WHILE": return evalWhile(tree, env);

            default:
                System.err.printf("\nFatal error in Evaluator.java: bad expression %s\n", tree.type);
                System.exit(1);
                return null;
        }
    }

    /**************************************************************
     *
     * Top-level Evaluations
     *
     ***************************************************************/
    private Lexeme evalProgram(Lexeme t, Lexeme env) {
//        Lexeme result = new Lexeme("EVALEDPROGRAM");
        while (t != null) {
            eval(t.left, env);
            t = t.right;
        }
        return null;
    }

    private Lexeme evalDefinition(Lexeme t, Lexeme env) {
        while (t != null) {
            eval(t.left, env);
            t = t.right;
        }
        return null;
    }

    /**************************************************************
     *
     * Builtin Evaluations
     *
     ***************************************************************/
    private Lexeme evalSteam(Lexeme t, Lexeme env) {
        Lexeme eargs = evalArgList(t.left, env);

        while (eargs != null) {
            System.out.print(eargs.left);
            eargs = eargs.right;
        }
        return null;
    }

    public Lexeme evalSteamLn(Lexeme t, Lexeme env) {
        Lexeme eargs = evalArgList(t.left, env);

        while (eargs != null) {
            System.out.print(eargs.left);
            eargs = eargs.right;
        }

        System.out.println();
        return null;
    }

    /***************************************************************
     *
     *  Function/Variable evaluations
     *
     ***************************************************************/
    //should work
    private Lexeme evalBrewFuncDef(Lexeme t, Lexeme env) {
        Lexeme closure = new Lexeme("CLOSURE", env, t);
        e.insert(t.right.left, closure, env);
        return null;
    }

    private Lexeme evalBrewVarDef(Lexeme t, Lexeme env) {
        if (t.right.right.type.equals("SEMICOLON")) {
            e.insert(t.right.left, new Lexeme("EMPTY"), env);
        }
        else {
            Lexeme val = eval(t.right.right.left.left, env);
            e.insert(t.right.left, val, env);
        }
        return null;
    }

    private Lexeme evalIDFuncCall(Lexeme t, Lexeme env) {
        Lexeme closure = eval(t.left, env); //get function definition environment
        Lexeme args = t.right.right.left;   //from function call
        Lexeme params = closure.right.right.right.right.left;
        Lexeme body = closure.right.right.right.right.right.right.left;
        Lexeme senv = closure.left;
        Lexeme eargs = evalArgList(args, env);
        Lexeme xenv = e.extend(params, eargs, senv);

        //variable that points to this xenv
        e.insert(new Lexeme("ID", "mug"), xenv, xenv);

        return eval(body, xenv);
    }

    private Lexeme evalDot(Lexeme t, Lexeme env) {
        Lexeme object = eval(t.left, env);
        return eval(t.right, e.extend(object.left, object.right.left, env));
    }

    private Lexeme evalArgList(Lexeme t, Lexeme env) {
        Lexeme result = new Lexeme("EVALEDARGLIST");
        Lexeme ptr = result;
        while (t != null) {
            Lexeme val = eval(t.left, env);
            ptr.left = val;

            t = t.right;
            if(t != null) {
                ptr.right = new Lexeme("EVALEDARGLIST");
                ptr = ptr.right;
            }
        }
        return result;
    }

    private Lexeme evalArrayDef(Lexeme t, Lexeme env) {
        Lexeme arr = new Lexeme("EVALEDARRAY");

        Lexeme evaledArgs = eval(t.left, env);
        ArrayList<Lexeme> ptrs = new ArrayList<>();

        Lexeme cursor = evaledArgs;

        while(cursor != null) {
            ptrs.add(cursor.left);
            cursor = cursor.right;
        }

        arr.left = evaledArgs;
        arr.right = null;
        arr.arrVal = ptrs;

        return arr;
    }

    private Lexeme evalArrAccess(Lexeme t, Lexeme env) {
        Lexeme arr = eval(t.left, env);
        Lexeme index = eval(t.right, env);

        Lexeme result = arr.arrVal.get(index.intVal);
        return result;
    }

    private Lexeme evalIDInit(Lexeme t, Lexeme env) {
        Lexeme temp = new Lexeme("ASSIGN", t.left, t.right.left);
        return eval(temp, env);
    }


    private Lexeme evalParenExpr(Lexeme t, Lexeme env) {
        return eval(t.left, env);
    }

    private Lexeme evalLambdaCall(Lexeme t, Lexeme env) {
        if(t.left.type.equals("IDFUNCCALL")) {
            Lexeme closure = e.lookup(t.left.left, env);
            Lexeme eargs = eval(t.right, env);
            Lexeme params = closure.right.right.right.right.right.right.left.left.left.left.left;
            Lexeme body = closure.right.right.right.right.right.right.left.left.left.left.right.left.left;
            Lexeme xenv = e.extend(params, eargs, env);
            return eval(body, xenv);
        }

        else {
            Lexeme eargs = eval(t.right, env);
            Lexeme params = t.left.left;
            Lexeme body = t.left.right;
            if (body == null) {
                Lexeme closure = e.lookup(t.left, env);
                params = closure.left;
                body = closure.right;
            }

            Lexeme xenv = e.extend(params, eargs, env);
            return eval(body, xenv);
        }
    }

    private Lexeme evalBlock(Lexeme t, Lexeme env) {
        Lexeme result = null;

        while (t != null) {
            result = eval(t.left, env);
            t = t.right;
        }
        return result;
    }

    private Lexeme evalStatementList(Lexeme t, Lexeme env) {
        Lexeme val = null;
        while (t != null) {
            val = eval(t.left, env);
            t = t.right;
        }
        return val;
    }

    private Lexeme evalStatement(Lexeme t, Lexeme env) {
        return eval(t.left, env);
    }

    private Lexeme evalWhile(Lexeme t, Lexeme env) {
        Lexeme result = null;
        while(eval(t.left, env).boolVal) {
            result = eval(t.right, env);
        }
        return result;
    }

    private Lexeme evalIfStatement(Lexeme t, Lexeme env) {
        if(eval(t.left, env).boolVal) {
            return eval(t.right.left, env);
        }
        else {
            return eval(t.right.right.left, env);
        }
    }

    private Lexeme evalOptElse(Lexeme t, Lexeme env) {
        return eval(t.right, env);
    }

    /***************************************************************
     *
     * Unary evaluations
     *
     ***************************************************************/
    private Lexeme evalNegative(Lexeme t, Lexeme env) {
        Lexeme result = eval(t.right, env);
        switch (result.type) {
            case "INTEGER":
                result.intVal *= -1;
            case "REAL":
                result.realVal *= -1.0;
        }
        return result;
    }

    private Lexeme evalNot(Lexeme t, Lexeme env) {
        Lexeme result = eval(t.right, env);
        result.boolVal = !result.boolVal;
        return result;
    }

    /***************************************************************
     *
     * Operator evaluations
     *
     ***************************************************************/

    private Lexeme evalShortCircuitOp(Lexeme t, Lexeme env) {
        switch (t.type) {
            case "AND":
                return evalAnd(t, env);
            case "OR":
                return evalOr(t, env);
            default:
                return null;
        }
    }

    private Lexeme evalAnd(Lexeme t, Lexeme env) {
        if (eval(t.left, env).boolVal && eval(t.right, env).boolVal)
            return new Lexeme("BOOLEAN", true);
        else
            return new Lexeme("BOOLEAN", false);
    }

    private Lexeme evalOr(Lexeme t, Lexeme env) {
        if (eval(t.left, env).boolVal || eval(t.right, env).boolVal)
            return new Lexeme("BOOLEAN", true);
        else
            return new Lexeme("BOOLEAN", false);
    }

    private Lexeme evalSimpleOp(Lexeme t, Lexeme env) {
        switch (t.type) {
            case "PLUS":
                return evalPlus(t, env);
            case "MINUS":
                return evalMinus(t, env);
            case "TIMES":
                return evalTimes(t, env);
            case "DIVIDE":
                return evalDivide(t, env);
            case "EXP":
                return evalExp(t, env);
            case "MOD":
                return evalMod(t, env);
            case "EQUALTO":
                return evalEqualTo(t, env);
            case "NOTEQUALTO":
                return evalNotEqualTo(t, env);
            case "GTHANEQUALTO":
                return evalGthanEqualTo(t, env);
            case "LTHANEQUALTO":
                return evalLthanEqualTo(t, env);
            case "GREATERTHAN":
                return evalGreaterThan(t, env);
            case "LESSTHAN":
                return evalLessThan(t, env);
            default:
                return null;
        }
    }

    private Lexeme evalPlus(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("INTEGER", left.intVal + right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", left.intVal + right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", left.realVal + right.intVal);
        else
            return new Lexeme("REAL", left.realVal + right.realVal);
    }

    private Lexeme evalMinus(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("INTEGER", left.intVal - right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", left.intVal - right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", left.realVal - right.intVal);
        else
            return new Lexeme("REAL", left.realVal - right.realVal);
    }

    private Lexeme evalTimes(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("INTEGER", left.intVal * right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", left.intVal * right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", left.realVal * right.intVal);
        else
            return new Lexeme("REAL", left.realVal * right.realVal);
    }

    private Lexeme evalDivide(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("INTEGER", left.intVal / right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", left.intVal / right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", left.realVal / right.intVal);
        else
            return new Lexeme("REAL", left.realVal / right.realVal);
    }

    private Lexeme evalExp(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", Math.pow((double) left.intVal, (double) right.intVal));
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("REAL", Math.pow(left.intVal, right.realVal));
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("REAL", Math.pow(left.realVal, right.intVal));
        else
            return new Lexeme("REAL", Math.pow(left.realVal, right.realVal));
    }

    private Lexeme evalMod(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        return new Lexeme("INTEGER", left.intVal % right.intVal);
    }

    private Lexeme evalAssign(Lexeme t, Lexeme env) {
        Lexeme value = eval(t.right, env);

        if (t.left.type.equals("ID")) {
            e.update(t.left, value, env);
        }
        else if (t.left.type.equals("DOT")) {
            Lexeme object = eval(t.left.left, env);
            e.update(t.left.right, value, object);
        }

        return value;
    }

    private Lexeme evalEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.intVal == right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.intVal == right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.realVal == right.intVal);
        else if (left.type.equals("REAL") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.realVal == right.realVal);
        else if (left.type.equals("STRING") && right.type.equals("STRING"))
            return new Lexeme("BOOLEAN", left.strVal.equals(right.strVal));
        else if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN"))
            return new Lexeme("BOOLEAN", left.boolVal == right.boolVal);
        else if (left.type.equals("EMPTY") && !right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", false);
        else if (!left.type.equals("EMPTY") && right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", false);
        else if (left.type.equals("EMPTY") && right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", true);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalNotEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.intVal != right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.intVal != right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.realVal != right.intVal);
        else if (left.type.equals("REAL") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.realVal != right.realVal);
        else if (left.type.equals("STRING") && right.type.equals("STRING"))
            return new Lexeme("BOOLEAN", !left.strVal.equals(right.strVal));
        else if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN"))
            return new Lexeme("BOOLEAN", left.boolVal != right.boolVal);
        else if (left.type.equals("EMPTY") && !right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", true);
        else if (!left.type.equals("EMPTY") && right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", true);
        else if (left.type.equals("EMPTY") && right.type.equals("EMPTY"))
            return new Lexeme("BOOLEAN", false);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalGthanEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.intVal >= right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.intVal >= right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.realVal >= right.intVal);
        else if (left.type.equals("REAL") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.realVal >= right.realVal);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalLthanEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.intVal <= right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.intVal <= right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.realVal <= right.intVal);
        else if (left.type.equals("REAL") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.realVal <= right.realVal);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalLessThan(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.intVal < right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.intVal < right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.realVal < right.intVal);
        else
            return new Lexeme("BOOLEAN", left.realVal < right.realVal);
    }

    private Lexeme evalGreaterThan(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.intVal > right.intVal);
        else if (left.type.equals("INTEGER") && right.type.equals("REAL"))
            return new Lexeme("BOOLEAN", left.intVal > right.realVal);
        else if (left.type.equals("REAL") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.realVal > right.intVal);
        else
            return new Lexeme("BOOLEAN", left.realVal > right.realVal);
    }

    void runEval(Lexeme tree) {
        //TODO: load in builtins here
        eval(tree, global);
    }

}
