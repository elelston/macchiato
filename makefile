#############################################
#											#
#		Macchiato							#
#		Designer Programming Language		#
#		CS 403 : Spring 2016				#
#											#
#		Makefile							#
#		Emily Huynh							#
#											#
#############################################

#general make command
all: Lexeme.class Lexer.class Parser.class Environment.class Evaluator.class Interpreter.class
	chmod 755 macch

#files to compile
Lexeme.class: Lexeme.java
	javac -d . -classpath . Lexeme.java

Lexer.class: Lexer.java
	javac -d . -classpath . Lexer.java

Parser.class: Parser.java
	javac -d . -classpath . Parser.java

Environment.class: Environment.java
	javac -d . -classpath . Environment.java

Evaluator.class: Evaluator.java
	javac -d . -classpath . Evaluator.java

Interpreter.class: Interpreter.java
	javac -d . -classpath . Interpreter.java

#clean the .class files
clean:
	rm -f *.class

#run my tests
cat-error1: #missing $ before integer
	cat features/error1.macch
run-error1:
	macch features/error1.macch
cat-error2: #using == instead of =
	cat features/error2.macch
run-error2:
	macch features/error2.macch
cat-error3: #missing semicolon
	cat features/error3.macch
run-error3:
	macch features/error3.macch
cat-arrays:
	cat features/arrays.macch
run-arrays:
	macch features/arrays.macch
cat-conditionals:
	cat features/conditionals.macch
run-conditionals:
	macch features/conditionals.macch
cat-recursion:
	cat features/recursion.macch
run-recursion:
	macch features/recursion.macch
cat-iter:
	cat features/iter.macch
run-iter:
	macch features/iter.macch
cat-functions:
	cat features/funcs.macch
run-functions:
	macch features/funcs.macch
cat-dictionary:
	cat features/dict.macch
run-dictionary:
	macch features/dict.macch
cat-problem:
	cat wire.macch
run-problem:
	macch wire.macch
