#RPNCalc

A calculator for Reverse Polish Notation

##Installation
Requires a Java 7 installation or later to compile.

The recommended way to install this application is to use the "Import" function in Eclipse. This application does not require any libraries, except for the standard Java libraries.

##Usage
This calculator supports various reverse polish notation expressions. The expressions are stored in a stack. Expressions (multiple operations on a single line) are supported.

The following operations are currently supported:

+, -, *, /, ^, %, -(unary)

Type 'q' (without quotes) to exit the application.

Start the application with the '-v' argument (without quotes) to see verbosely the parse errors.

Start the application with the '-s' argument (without quotes) to see the stack after each expression.

##Sample input/output
## Sample Input/Output

    > 5 
    5
    > 8
    8
    > +
    13

---

    > -3
    -3
    > -2
    -2
    > *
    6
    > 5
    5
    > +
    11

---

    > 2
    2
    > 9
    9
    > 3
    3 
    > +
    12 
    > *
    24

---

    > 20
    20
    > 13
    13
    > -
    7
    > 2
    2
    > / 
    3.5
