#Macchiato

**Author: Emily Huynh**

Host Language: Java

Designer Programming Language Project

The University of Alabama

CS 403

Spring 2016

##Getting Started

Macchiato files are suffixed with the .macch file extension. To run a .macch file, use the command `macch filename.macch`.

##Test Problem

The source code for the one-bit full adder problem is in `wire.macch`. To change
the inputs for `in0`, `in1`, and `c-in`, change the values on **lines 367-369**.

Run the test problem using the command `make run-problem`. Results of `carry` and `out0`
will be printed to the console.

##Language Details

Macchiato is object-oriented. Below are details on the semantics of the Macchiato programming language.

##Syntax
- Curly braces `{ }` are required for all code blocks
- All statements end in a semicolon `;`
- All numerics (integers and reals) are preceded with a dollar sign `$`
- Null/nil values are denoted with the `empty` keyword
- Objects are created using the `mug` keyword

##Comments

Macchiato provides three different commenting options.

#####Single line comments

    && This is a single line comment.

#####Block comments

    &^
        This is a block comment.
    ^&

#####End of file comments

    &*
        This will comment to the end of the file.

##Definitions

Defining and functions and initializing variables is done using
the `brew` keyword. Existing variables can be redefined without using `brew`.

**Example Function Definition:**

    brew func[param1, param2] {
        steamln["params are ", param1, " ", param2];
    }

    && call the function
    func["one", "two"];

**Example Variable Definition:**

    brew var : $1;
    steamln[var];

    var : $2;
    stemaln[var];


##Operators

Macchiato provides a basic set of operators:

**Arithmetic:**
- plus `+`
- minus `-`
- multiply `*`
- divide `/`
- exponentiation `^`
- modulus `%`

**Comparison:**
- equal to `=`
- not equal to `~=`
- greater than `>`
- greater than or equal to `>=`
- less than `<`
- less than or equal to `<=`

**Unary:**
- logical not `~`
- negative `-`

**Short Circuiting:**
- logical and `and`
- logical or `or`

**Assignment:**
- assign `:`

####Precedence

Macchiato does not currently support precedence. Parenthesized expressions
can be used in place of built in precedence.

##Object Orientation

Objects are defined using the `mug` keyword.

##Builtin Functions

####`steam` and `steamln`

Macchiato provides two builtin printing functions. Each take in a variable
number of arguments. The code block

    steam["string one", $1]
    steamln["string two", $2]

will yield the following:

    string one1
    string two
    2

##Arrays
####Defining Arrays

Arrays are defined like all other variables.

    brew arr : |[$1, "string item 2", $3]|;

####Accessing Arrays

Arrays use zero-based indexing, and access to each element is constant.

    brew firstItem : arr|[$0]|;


##Lambdas

####Defining Lambdas

Lambdas are defined using the `latte` keyword. Lambdas are considered primaries in the Macchiato programming language. All statements inside the lambda block must end in a semicolon `;`. If a
lambda is defined within a function, it must end in a semicolon `;`.

    brew lambda : latte[param1, param2] { steamln[param1, " ", param2]; };

    brew lambdaFunc[] {
        latte [x] {
            steamln[x];
        };
    }

####Calling Lambdas

The ID of the variable holding the lambda must be wrapped in parentheses `()` in order
to be called.

    brew lambda : latte[param1, param2] { steamln[param1, " ", param2]; };

    brew lambdaFunc[] {
        latte [x] {
            steamln[x];
        };
    }

    (lambda)[arg1, arg2];
    (lambdaFunc[])[arg1];
