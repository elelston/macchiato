/*
*
*		Designer Programming Language
*		Environment Module
*		CS 403 : Spring 2016
*
*		Class: Environment
*		Macchiato
*		Emily Huynh
*
*/

class Environment {


    /**************************************************************
     *
     * Cons Helper Functions
     *
     ***************************************************************/

    public Lexeme car(Lexeme env) {
        return env.left;
    }

    public Lexeme cdr(Lexeme env) {
        return env.right;
    }

    public Lexeme cadr(Lexeme env) {
        return env.right.left;
    }

    public Lexeme setCar(Lexeme env, Lexeme val) {
        env.left = val;
        return env.left;
    }

    /**************************************************************
     *
     * General Helper Functions
     *
     ***************************************************************/
    public boolean sameVariable(Lexeme a, Lexeme b) {
        return a.strVal.equals(b.strVal);
    }

    /**************************************************************
     *
     * Environment Functions
     *
     ***************************************************************/
    public Lexeme create() {
        return extend(null, null, null);
    }

    //find the value of an ID
    //if it exists, return the val, otherwise, return null
    public Lexeme lookup(Lexeme variable, Lexeme env) {

        while (env != null) {

            Lexeme vars = car(env);
            Lexeme vals = cadr(env);

            while (vars != null) {
                if (sameVariable(variable, car(vars))) {
                    return car(vals);
                }
                vars = cdr(vars);
                vals = cdr(vals);
            }
            env = cdr(cdr(env));
        }

        System.out.printf("Variable %s is undefined\n", variable.strVal);
        System.exit(1);
        return null;
    }

    public Lexeme update(Lexeme variable, Lexeme value, Lexeme env) {

        while (env != null) {

            Lexeme vars = car(env);
            Lexeme vals = cadr(env);

            while (vars != null) {
                if (sameVariable(variable, car(vars))) {
//                    return setCar(cadr(env), value);
                    return setCar(vals, value);
                }
                vars = cdr(vars);
                vals = cdr(vals);
            }
            env = cdr(cdr(env));
        }

        System.out.printf("Variable %s is undefined\n", variable.strVal);
//        System.exit(1);
        return null;
    }

    public Lexeme insert(Lexeme variable, Lexeme value, Lexeme env) {
        setCar(env, new Lexeme("GLUE", variable, car(env)));
        setCar(cdr(env), new Lexeme("GLUE", value, cadr(env)));
        return value;
    }

    public Lexeme extend(Lexeme variables, Lexeme values, Lexeme env) {
        return new Lexeme("ENV", variables, new Lexeme("VALUES", values, env));
    }

}
