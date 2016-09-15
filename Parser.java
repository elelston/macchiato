/*
*
*		Designer Programming Language
*		Recognizer Module
*		CS 403 : Spring 2016
*
*		Class: Recognizer
*		Macchiato
*		Emily Huynh
*
*/

class Parser {

    //Global variables
    Lexer l;
    Lexeme currentLexeme;
    Lexeme output;

    /**************************************************************
     *
     * Helper Functions
     *
     ***************************************************************/
    public boolean check(String type) {
        return currentLexeme.type.equals(type);
    }

    public Lexeme advance() throws Exception {
        Lexeme old = currentLexeme;
        currentLexeme = l.lex();
        return old;
    }

    public Lexeme match(String type) throws Exception {
        matchNoAdvance(type);
        return advance();
    }

    public void matchNoAdvance(String type) {
        if (type.equals("INVALID")) {
            System.out.printf(
                    "SYNTAX ERROR Line %d: invalid character %s. Typo? \n", l.line, currentLexeme.type);
            System.exit(0);
        }
        else if (!check(type)) {
            System.out.printf("SYNTAX ERROR ");
            System.out.printf(
                    "Line %d: received %s, expected %s.\n", l.line, currentLexeme.type, type);
            System.exit(0);
        }
    }

    /**************************************************************
     *
     * Pending Functions
     *
     ***************************************************************/
    public boolean programPending() {
        return definitionPending();
    }

    public boolean definitionPending() {
        return brewDefPending() || check("ID") || parenExprPending();
    }

    public boolean initPending() {
        return check("ASSIGN");
    }

    public boolean brewDefPending() {
        return check("BREW");
    }

    public boolean varDefPending() {
        return initPending() || check("SEMICOLON");
    }

    public boolean funcDefPending() {
        return check ("OBRACKET");
    }

    public boolean funcCallPending() {
        return check("OBRACKET");
    }

    public boolean paramListPending() {
        return check("ID");
    }

    public boolean argListPending() {
        return exprPending();
    }

    public boolean exprPending() {
        return primaryPending();
    }

    public boolean numericPending() {
        return check("INTEGER") || check("REAL");
    }

    public boolean primaryPending() {
        return idExprPending() || check("STRING") || check("BOOLEAN") ||
                check("DOLLARSIGN") || parenExprPending() || check("VBAR") ||
                check("OBRACKET") || lambdaDefPending() || check("EMPTY") ||
                check("STEAM") || check("STEAMLN") || check("MINUS") || check("NOT");
    }

    public boolean parenExprPending() {
        return check("OPAREN");
    }

    public boolean idExprPending() {
        return check("ID");
    }

    public boolean lambdaDefPending() {
        return check("LATTE");
    }

    public boolean operatorPending() {
        return check("EQUALTO") || check("GREATERTHAN") || check("LESSTHAN") || check("NOT") ||
                check("PLUS") || check("TIMES") || check("MINUS") || check("DIVIDE") || check("EXP") ||
                check("MOD") || check("ASSIGN") || check("AND") || check("OR");
    }

    public boolean statementListPending() {
        return statementPending();
    }

    public boolean statementPending() {
        return whileLoopPending() || ifStatementPending() || exprPending() ||
                brewDefPending();
    }

    public boolean ifStatementPending() {
        return check("IF");
    }

    public boolean whileLoopPending() {
        return check("WHILE");
    }

    /**************************************************************
     *
     * Implemented Grammar Rules
     *
     ***************************************************************/
    public Lexeme program() throws Exception {
        Lexeme a = definition();
        Lexeme b = null;
        if (programPending()) {
            b = program();
        }
        return new Lexeme("PROGRAM", a, b);
    }

    public Lexeme definition() throws Exception {
        if (brewDefPending()) {
            return brewDef();
        }
        else if (idExprPending()) {
            Lexeme tree = new Lexeme("DEFINITION");
            tree.left = idExpr();
            tree.right = match("SEMICOLON");
            return tree;
        }
        else {
            Lexeme tree = new Lexeme("DEFINITION");
            tree.left = parenExpr();
            match("SEMICOLON");
            return tree;
        }
    }

    public Lexeme init() throws Exception {
        match("ASSIGN");
        Lexeme a = expr();
        return new Lexeme("INIT", a, null);
    }

    public Lexeme brewDef() throws Exception {
        Lexeme tree = match("BREW");
        tree.left = match("ID");
        if(varDefPending()) {
            tree.right = varDef();
            Lexeme temp = new Lexeme("BREWVARDEF", null, tree);
            tree = temp;
        }
        else {
            tree.right = funcDef();
            Lexeme temp = new Lexeme("BREWFUNCDEF", null, tree);
            tree = temp;
        }
        return tree;
    }

    public Lexeme varDef() throws Exception {
        if (initPending()) {
            Lexeme a = init();
            Lexeme b = match("SEMICOLON");
            return new Lexeme("VARDEF", a, b);
        }
        else {
            return match("SEMICOLON");
        }
    }

    public Lexeme funcDef() throws Exception {
        Lexeme a = match("OBRACKET");
        Lexeme b = optParamList();
        Lexeme c = match("CBRACKET");
        Lexeme d = block();
        return new Lexeme("FUNCDEF", a, new Lexeme("GLUE", b, new Lexeme("GLUE", c,
                new Lexeme("GLUE", d, null))));
    }

    public Lexeme funcCall() throws Exception {
        Lexeme a = match("OBRACKET");
        Lexeme b = optArgList();
        Lexeme c = match("CBRACKET");
        return new Lexeme("FUNCCALL", a, new Lexeme("GLUE", b, new Lexeme("GLUE", c, null)));
    }

    public Lexeme optParamList() throws Exception {
        if (paramListPending()) {
            return paramList();
        }
        return null;
    }

    public Lexeme paramList() throws Exception {
        Lexeme a = match("ID");
        Lexeme b = null;
        if (check("COMMA")) {
            match("COMMA");
            b = paramList();
        }
        return new Lexeme("PARAMLIST", a, b);
    }

    public Lexeme optArgList() throws Exception {
        if (argListPending()) {
            return argList();
        }
        return null;
    }

    public Lexeme argList() throws Exception {
        Lexeme a = expr();
        Lexeme b = null;
        if (check("COMMA")) {
            match("COMMA");
            b = argList();
        }
        return new Lexeme("ARGLIST", a, b);
    }

    public Lexeme expr() throws Exception {
        Lexeme tree = primary();

        if (operatorPending()) {
            Lexeme temp = operator();
            temp.left = tree;
            temp.right = expr();
            tree = temp;
        }
        return tree;
    }

    public Lexeme numeric() throws Exception {
        if (check("INTEGER")) {
            return match("INTEGER");
        }
        else {
            return match("REAL");
        }
    }

    public Lexeme primary() throws Exception {
        Lexeme tree;

        if (idExprPending()) {
            tree = idExpr();
        }
        else if (check("STRING")) {
            tree = match("STRING");
            tree.strVal = tree.strVal.substring(1, tree.strVal.length() - 1);
        }
        else if (check("BOOLEAN")) {
            tree = match("BOOLEAN");
        }
        else if (check("DOLLARSIGN")) {
//            tree = match("DOLLARSIGN");
//            tree.right = numeric();
            match("DOLLARSIGN");
            tree = numeric();
        }
        else if (parenExprPending()) {
            tree = parenExpr();
        }
        else if (check("VBAR")) {
            match("VBAR");
            match("OBRACKET");
            Lexeme a = argList();
            match("CBRACKET");
            match("VBAR");
            tree = new Lexeme("ARRAYDEF", a, null);
        }
        else if (lambdaDefPending()) {
            tree = lambdaDef();
        }
        else if (check("EMPTY")) {
            tree = match("EMPTY");
        }
        else if (check("STEAM")) {
            tree = match("STEAM");
            match("OBRACKET");
            tree.left = optArgList();
            match("CBRACKET");
            tree.right = null;
        }
        else if (check("STEAMLN")) {
            tree = match("STEAMLN");
            match("OBRACKET");
            tree.left = optArgList();
            match("CBRACKET");
            tree.right = null;
        }
        else if (check("MINUS")) {
            tree = match("MINUS");
            tree.type = "NEGATIVE";
            tree.left = null;
            tree.right = primary();
        }
        else if (check("NOT")) {
            tree = match("NOT");
            tree.left = null;
            tree.right = primary();
        }
        else {
            tree = match("INVALID");
        }
        return tree;
    }

    public Lexeme parenExpr() throws Exception {
        match("OPAREN");
        Lexeme a = expr();
        match("CPAREN");

        Lexeme tree = new Lexeme("PARENEXPR", a, null);

        //check to see if it is a lambda call
        if (check("OBRACKET")) {
            match("OBRACKET");
            Lexeme b = optArgList();
            //if(argListPending()) {
            //    b = argList();
           // }
            //else {
            //    b = new Lexeme("INTEGER", 1);   //TODO: fix dummy node in parenExpr()
           // }
            match("CBRACKET");

            tree = new Lexeme("LAMBDACALL", a, b);
        }

        return tree;
    }

    public Lexeme idExpr() throws Exception {
        Lexeme a = match("ID");
        if (funcCallPending()) {
            Lexeme b = funcCall();
            return new Lexeme("IDFUNCCALL", a, b);
        }
        else if (initPending()) {
            Lexeme b = init();
            return new Lexeme("IDINIT", a, b);
        }
        else if (check("VBAR")) {
            match("VBAR");
            match("OBRACKET");
            Lexeme b = primary();
            match("CBRACKET");
            match("VBAR");
            return new Lexeme("ARRACCESS", a, b);
        }
        else if (check("DOT")) {
            Lexeme b = match("DOT");
            b.left = a;
            b.right = idExpr();
            return b;
        }
        return a;
    }

    public Lexeme lambdaDef() throws Exception {
        match("LATTE");
        match("OBRACKET");
        Lexeme a;
        if (paramListPending()) {
            a = paramList();
        }
        else {
            a = new Lexeme("INTEGER", 1); //TODO: fix dummy node in lambdaDef()
        }
        match("CBRACKET");
        Lexeme b = block();
        return new Lexeme("LAMBDADEF", a, b);
    }

    public Lexeme operator() throws Exception {
        if (check("EQUALTO")) {
            return match("EQUALTO");
        }
        else if (check("GREATERTHAN")) {
            Lexeme a = match("GREATERTHAN");
            if (check("EQUALTO")) {
                match("EQUALTO");
                return new Lexeme("GTHANEQUALTO");
            }
            return a;
        }
        else if (check("LESSTHAN")) {
            Lexeme a = match("LESSTHAN");
            if (check("EQUALTO")) {
                match("EQUALTO");
                return new Lexeme("LTHANEQUALTO");
            }
            return a;
        }
        else if (check("NOT")) {
            match("NOT");
            match("EQUALTO");
            return new Lexeme("NOTEQUALTO");
        }
        else if (check("PLUS")) {
            return match("PLUS");
        }
        else if (check("TIMES")) {
            return match("TIMES");
        }
        else if (check("MINUS")) {
            return match("MINUS");
        }
        else if (check("DIVIDE")) {
            return match("DIVIDE");
        }
        else if (check("MOD")) {
            return match("MOD");
        }
        else if (check("EXP")) {
            return match("EXP");
        }
        else if (check("ASSIGN")) {
            return match("ASSIGN");
        }
        else if (check("AND")) {
            return match("AND");
        }
        else {
            return match("OR");
        }
    }

    public Lexeme block() throws Exception {
        match("OBRACE");
        Lexeme a = optStatementList();
        match("CBRACE");
        return new Lexeme("BLOCK", a, null);
    }

    public Lexeme optStatementList() throws Exception {
        if (statementListPending()) {
            return statementList();
        }
        return null;
    }

    public Lexeme statementList() throws Exception {
        Lexeme a = statement();
        Lexeme b = null;

        if (statementListPending()) {
            b = statementList();
        }

        return new Lexeme("STATEMENTLIST", a, b);
    }

    public Lexeme statement() throws Exception {
        Lexeme a = null;

        if (whileLoopPending()) {
            a =  whileLoop();
        }
        else if (ifStatementPending()) {
            a = ifStatement();
        }
        else if (exprPending()) {
            a = expr();
            match("SEMICOLON");
        }
        else {
            a = brewDef();
        }

        return new Lexeme("STATEMENT", a, null);
    }

    public Lexeme ifStatement() throws Exception {
        match("IF");
        match("OBRACKET");
        Lexeme a = expr();
        match("CBRACKET");
        Lexeme b = block();
        Lexeme c = optElse();
        return new Lexeme("IFSTATEMENT", a, new Lexeme("GLUE", b,
                new Lexeme("GLUE", c, null)));
    }

    public Lexeme optElse() throws Exception {
        if (check("ELSE")) {
            return new Lexeme("OPTELSE", match("ELSE"), block());
        }
        else {
            return null;
        }
    }

    public Lexeme whileLoop() throws Exception {
        match("WHILE");
        match("OBRACKET");
        Lexeme a = expr();
        match("CBRACKET");
        Lexeme b = block();
        return new Lexeme("WHILE", a, b);
    }

    public Lexeme parse(String f) throws Exception {
        l = new Lexer(f);
        currentLexeme = l.lex();
        output = program();
        match("END_OF_INPUT");
        return output;
    }


}
