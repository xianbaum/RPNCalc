#RPNCalc

A calculator for Reverse Polish Notation

This calculator supports various reverse polish notation expressions. The expressions are stored in a stack. Expressions (multiple operations on a single line) are supported.

##Installation
Requires a Java 7 installation or later and Maven to compile. In order to run the Unit Tests, JUnit is also required. In the Maven project root, run:

>mvn package

It will build the project into a "jar" file and run the unit tests. The "jar" file is in the "target" directory, under the name of "RPNCalc-1.0.jar"

##Usage
To run the compiled application, in the root directory type:

>java -jar target/RPNCalc-1.0.jar

The following operations are supported:

* Add (+)
* Subtract (-)
* Multiply (*)
* Divide (/)
* Remainder (%)
* Exponent (^)
* Negate (-) unary

Type 'q' (without quotes) to exit the application at any point in the expression.

When the stack is larger than 2, it is listed above the output to help you keep track of it.

##Running tests
Tests are automatically ran when built, but to run only tests, type:

>mvn test

##Sample input/output

    >1
    1
    >1
    [1, 1]
    1
    >+
    2
    >50
    [2, 50]
    50
    >19
    [2, 50, 19]
    19
    >239
    [2, 50, 19, 239]
    239
    >32509
    [2, 50, 19, 239, 32509]
    32509
    >+ * - /
    -0.00000321459684133714
    >q
