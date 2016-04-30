package com.christianbaum.rpncalc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public final class Interpreter {

	/** Binary operator hashmap*/
	private static HashMap<String,BinaryMathOperation> binaryOperationMap;
	/** Unary operator hashmap */
	private static HashMap<String,UnaryMathOperation> unaryOperationMap;
	/** Symbols used by the interpreter */
	private static char[] reservedSymbols = {'+', '-', '*', '/', '^', '%', ' ', '\t', 'q'};
	/** Unary symbols (possible to add more) */
	private static char[] unaryOperators = {'-'};
	/** Main number stack */
	private Stack<BigDecimal> numberStack;
	/** Unary operation stack (for stacking negatives for example) */
	private Stack<UnaryMathOperation> unaryStack;
	/** The string as it's being parsed, before it's put into the number stack */
	private StringBuilder numberString;
	/** The parse character count number */
	private long characterCount;
	/** False if 'q' is entered */
	private boolean running;
	/** If the interpreter ran into trouble, this is true */
	private boolean interpreterError;
	/** Debug boolean to display stack or not */
	private boolean printStack;
	/** Debug boolean to display errors verbosely or not */
	private boolean verboseErrors;
	
	//Constructors
	
	/**
	 * Initializes the interpreter
	 */
	Interpreter() {
		initialize();
		printStack = false;
	}
	
	/**
	 * Initializes the interpreter
	 * 
	 * @param printStack Whether or not to print the stack
	 * @param verboseErrors Whether or not to print errors verbosely
	 */
	Interpreter( boolean printStack, boolean verboseErrors ) {
		initialize();
		this.printStack = printStack;
		this.verboseErrors = verboseErrors;
	}
	
	//Static initializer
	
	/**
	 * Initializes the static HashMaps for the binary and unary operations via
	 * anonymous classes.
	 * 
	 */
	static {
		binaryOperationMap = new HashMap<String,BinaryMathOperation>();
		unaryOperationMap = new HashMap<String,UnaryMathOperation>();
		
		//Addition
		binaryOperationMap.put("+", new BinaryMathOperation() {
			@Override
			public BigDecimal execute( BigDecimal number1, BigDecimal number2  ) {
				return number2.add( number1 );
			}			
		});
		
		//Subtraction
		binaryOperationMap.put("-", new BinaryMathOperation() {
			@Override
			public BigDecimal execute( BigDecimal number1, BigDecimal number2  ) {
				return number2.subtract( number1 );
			}
		});
		
		//Multiplication
		binaryOperationMap.put("*", new BinaryMathOperation() {
			@Override
			public BigDecimal execute( BigDecimal number1, BigDecimal number2  ) {
				return number2.multiply( number1 );
			}
		});
		
		//Division
		binaryOperationMap.put("/", new BinaryMathOperation() {
			@Override
			public BigDecimal execute( BigDecimal divisor, BigDecimal number ) {
				
				if( divisor.equals(BigInteger.ZERO)) {
					return null;
				}
				
				BigDecimal result = number.divide( divisor, 20, RoundingMode.HALF_UP );
				return result.stripTrailingZeros();
			}
		});

		//Exponents
		binaryOperationMap.put("^", new BinaryMathOperation() {
			@Override
			public BigDecimal execute( BigDecimal exponent, BigDecimal number ) {
				//We need to check if the exponent overflows or not
				int exponentInt = exponent.intValue(); 
				
				if( exponent.equals( new BigDecimal(exponentInt) )) {
					return number.pow( exponentInt );
				}
				
				//Failed
				return null;
			}
		});
		
		// Remainder operator
		binaryOperationMap.put("%", new BinaryMathOperation() {
			@Override
			public BigDecimal execute( BigDecimal divisor, BigDecimal number ) {
				return number.remainder( divisor );
			}
		});
		
		//Negate operator
		unaryOperationMap.put("-", new UnaryMathOperation() {
			@Override
			public BigDecimal execute( BigDecimal number ) {
				return number.negate();
			}
		});
	}
	
	//Private methods
	
	/**
	 *  Is called for every binary operation. It pops two numbers from the stack
	 *  and performs a binary operation on them. The binary operation is defined
	 *  in binaryOperationMap.
	 *  
	 * @param operation The operation to evaluate
	 * @return the result of the operation
	 */
	private BigDecimal binaryOperation( BinaryMathOperation operation ) {
		BigDecimal number[] = new BigDecimal[2];
		
		for(int i = 0; i<2; i++) {
			
			if( numberStack.isEmpty() ) {
				reportError("Expected number, but the number stack is empty");
			}
			
			else {
				number[i] = numberStack.pop();
			}
		}
		
		if(!interpreterError) {
			return operation.execute( number[0], number[1] );
		}
		
		else {
			return null;
		}
	}

	/**  
	 * Called every time a word is ended.
	 */
	private void endNumber() {
		
		if( numberString.length() > 0) {
			String wordOrNumberString = numberString.toString(); 
			boolean isNumber = isNumber( wordOrNumberString );
			
			if( isNumber ) {
				pushExpectedNumber( new BigDecimal( wordOrNumberString ) );
			}
			
			else {
				reportError("Unknown symbol (not a number)");
			}
			
			if( !unaryStack.isEmpty() ) {
				pushExpectedNumber(unaryOperation(numberStack.pop()));
			}
		}
	}

	/**
	 * Initializes member variables for constructor
	 */
	private void initialize() {
		numberStack = new Stack<BigDecimal>();
		unaryStack = new Stack<UnaryMathOperation>();
		numberString = new StringBuilder();
		running = true;
		interpreterError = false;
		printStack = false;
	}

	/**
	 * Checks string and returns true if it's a valid number, or false if it is not.
	 * 
	 * @param expression The expression to evaluate
	 * @return True if the string is a number, false if it is not
	 */
	private boolean isNumber( String expression ) {
		char charArrayToCheck[] = expression.toCharArray();
		
		if( charArrayToCheck[0] == '.' || charArrayToCheck[charArrayToCheck.length-1] == '.' ) {
			return false;
		}
		
		for( char charToCheck : charArrayToCheck ) {
			//if just one isn't a number, then we don't need to check anymore
			
			if(!Character.isDigit( charToCheck ) && charToCheck != '.') {
				return false;
			}
		}
		
		//Only if all passed
		return true;
	}
	
	/**
	 * Iterates all of the reserved operators and returns whether it is or not 
	 * 
	 * @param character The character to compare to an operator
	 * @return Whether or not it is a reserved operator or not
	 */
	private boolean isReservedOperator(char character) {
		
		for( char symbol : reservedSymbols) {
			
			if( character == symbol) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks whether the operation is unary or binary. Useful especially for
	 * the minus sign, which is either used as a subtraction (binary) or 
	 * negate(unary) sign. Able to stack unary operations (like double negating).
	 * 
	 * @param expression The entire expression to evaluate
	 * @param index The current index to evaluate
	 * @return The index where the unary operator ends, or -1 if not unary
	 */
	private int unaryAt( char expression[], int index) {
		char currentChar = expression[index];
		
		if( isUnaryOperator( currentChar) && isReservedOperator( currentChar )) {
			
			if( index > 0 && Character.isDigit((expression[index-1]))) {
				return -1;
			}
			for( int tempIndex = index; tempIndex < expression.length; tempIndex++ ) {
				
				if( Character.isDigit( expression[tempIndex] ) ) {
					return tempIndex-1;
				}
				
				else if( isUnaryOperator( expression[tempIndex]) ) {
					UnaryMathOperation operation = unaryOperationMap.get(""+currentChar);
					unaryStack.push( operation );
				}
				
				else {
					unaryStack.removeAllElements();
					return -1;
				}
			}
		}
		
		else if( isUnaryOperator( currentChar)) {
			UnaryMathOperation operation = unaryOperationMap.get(""+currentChar);
			unaryStack.push( operation );
			return index-1;
		}
		return -1;
	}

	/**
	 * Checks a single operator to see whether it is a unary operator or not.
	 * If it's not, it's probably a binary operator.
	 * 
	 * @param character The character to evaluate
	 * @return Whether it is unary or not
	 */
	private boolean isUnaryOperator( char character ) {
		
		for( char unaryOperator : unaryOperators ) {
			
			if( character == unaryOperator ) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks whether it is a tab or space
	 * 
	 * @param character The character to evaluate
	 * @return Whether the character is a tab or space
	 */
	private static boolean isWhiteSpace(char character) {
		return character == ' ' || character == '\t';
	}
	
	/**
	 * Pushes a number, or reports an error if the number attempted
	 * to push is null.
	 * 
	 * @param number The number to push.
	 */
	private void pushExpectedNumber(BigDecimal number) {
		
		if( number == null) {
			reportError("Tried to push null number");			
		}
		
		else {
			numberStack.push( number );
			numberString.setLength(0);
		}
		
	}

	/**
	 * Reports an error. If verbose messages are set to true in the constructor,
	 * then these messages are displayed. 
	 * 
	 * @param errorMessage
	 */
	private void reportError( String errorMessage ) {
		
		if( verboseErrors ) {
			System.out.println("Calculator Error: " + errorMessage + " when evaluating character " + characterCount);
		}
		
		interpreterError = true;
	}

	/**
	 * Is called once for every unary operation stack. It calls multiple unary operations for
	 * each number, or however many operations are in the stack.
	 * 
	 * @param number The number to perform the operation on
	 * @return The result of all the unary operations
	 */
	private BigDecimal unaryOperation( BigDecimal number ) {
		BigDecimal result = new BigDecimal(number.toString());
		
		while( !unaryStack.isEmpty() ) {
			UnaryMathOperation operation = unaryStack.pop();
			
			if( operation != null) {
				result = operation.execute( result );
			}
			
			else {
				reportError("Null unary operation!");
			}
		}
		
		return result;
	}
	
	/**
	 * Returns true if the interpreter has not been terminated
	 * 
	 * @return Whether or not the interpreter has been terminated or not
	 */
	
	//Public methods
	
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * The main expression logic. It ignores whitespace. It checks for a termination signal,
	 * then checks for a unary operation stack, then performs any binary operations,
	 * then finally checks if it is a number. Every reserved symbol ends a number
	 * 
	 * 
	 * @param expression The expression to evaluate
	 * @return The number result or null if there was an error
	 */
	
	//Main parse logic; returns a number or null if failed
	public BigDecimal parseExpression( String expression ) {		
		characterCount = 0;
		interpreterError = false;
		char expressionCharArray[] = expression.toCharArray();
		
		for( int i =0; i < expressionCharArray.length && !interpreterError; i++ ) {
			
			characterCount ++;
			char current = expressionCharArray[i];
			
			if(isReservedOperator( current)) {
				endNumber();
				//Space just ends a word
				if( !isWhiteSpace(current)) {
					
					if( current == 'q') {
						terminate();
					}
					
					else {
						int newIndex = unaryAt( expressionCharArray, i ) ;
						
						//Does not exist
						if( newIndex == -1) {
							BinaryMathOperation operation = binaryOperationMap.get( "" + current );
							
							if( operation == null) {
								reportError("Null operation");
							}
							
							else {
								pushExpectedNumber(binaryOperation( operation ));
							}
						}
						
						else {
							i = newIndex;
						}
					}
				}
			}
			
			else {
				
				if( Character.isDigit(current) || current == '.') {
					numberString.append(current);
				}
			}
		}
		
		if( interpreterError ) {
			System.out.println("Parse error");
		}
		
		else {
			endNumber();
			
			if( !numberStack.isEmpty()) {
				return numberStack.peek();
			}
		}
		
		return null;
	}
	
	/**
	 * Calls parseExpression, then prints out the results in a human-readable form.
	 * 
	 * @param expression The expression to evaluate
	 */
	
	//Prints out an expression or prints out errors
	public void processExpression( String expression ) {
		BigDecimal number = parseExpression( expression);
		
		if(printStack) {
			String stack;
			
			if( numberStack.isEmpty()) {
				stack = "Empty";
			}
			
			else {
				stack = Arrays.toString(numberStack.toArray());
			}
			
			System.out.println("Number Stack: " + stack);
		}
		
		if( number != null )  {
			
			if( number.toString().equals("0E-255")) {
				reportError("Divide by 0");
			}
			
			System.out.println( number.toString() );
		}
	}
	
	/**
	 * Sends the terminate signal to the interpreter 
	 */
	
	//Can be called from outside the interpreter
	public void terminate() {
		running = false;
	}

}
