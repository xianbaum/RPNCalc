package com.christianbaum.rnpcalc.RPNCalc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public final class RPNInterpreter {

	/** Binary operator hashmap*/
	private static HashMap<Character,BinaryMathOperation> binaryOperationMap;
	/** Unary operator hashmap */
	private static HashMap<Character,UnaryMathOperation> unaryOperationMap;
	/** Main number stack */
	private Stack<BigDecimal> numberStack;
	/** Unary operation stack for stacking operations, ----6 = 6 for example */
	private Stack<UnaryMathOperation> unaryStack;
	/** False if 'q' is entered */
	private boolean running;
	/** If the interpreter ran into trouble, this is true */
	private boolean interpreterError;

	//Constructors
	
	/**
	 * Initializes the interpreter
	 */
	RPNInterpreter() {
		
		initialize();
	}
	
	//Static initializer
	
	/**
	 * Initializes the static HashMaps for the binary and unary operations via
	 * classes that implement BinaryMathOperation and UnaryMathOperation
	 */
	static {
		
		binaryOperationMap = new HashMap<Character,BinaryMathOperation>();
		
		final BinaryMathOperation[] binaryOperations = {
				
				new AddOperation(),
				new SubtractOperation(),
				new MultiplyOperation(),
				new DivideOperation(),
				new ExponentOperation(),
				new RemainderOperation()
		};
		
		for( BinaryMathOperation binaryOperation : binaryOperations ) {
			binaryOperationMap.put( binaryOperation.symbol(), binaryOperation);
		}
		
		unaryOperationMap = new HashMap<Character,UnaryMathOperation>();
		
		final UnaryMathOperation[] unaryOperations = {
				
				new NegateOperation()
		};
		
		for( UnaryMathOperation unaryOperation : unaryOperations ) {
			
			unaryOperationMap.put( unaryOperation.symbol(), unaryOperation);
		}
		
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
				
				reportError("Empty number stack");
				return null;
			}
			
			else {
				
				number[i] = numberStack.pop();
			}
			
		}
		
		BigDecimal result = operation.execute( number[0], number[1] );
		
		if( result == null ) {
			
			reportError("Illegal operation");
			return null;
		}
		else {
			
			return result;
		}
		
	}

	/**
	 * Initializes member variables, used by the constructor
	 */
	private void initialize() {
		
		numberStack = new Stack<BigDecimal>();
		unaryStack = new Stack<UnaryMathOperation>();
		running = true;
		interpreterError = false;
	}
	
	/**
	 * Parses a number at the current index, then pushes it if successful.
	 * Returns the index to jump to by the parser.
	 * 
	 * @param expression The entire expression to evaluate
	 * @param index The current index to evaluate
	 * @return The index where the number operator ends, or -1 if not a number
	 */
	private int parseNumber( char expression[], int index) {
		
		if( Character.isDigit( expression[index]) ||
				expression[index] == '.' ) {
			
			String subString = 
					new String(expression, index, expression.length - index);
			String result = subString.split("[^\\d.]")[0];
			
			//If there are more than one occurance of '.'
			if (result.length() - result.replace(".", "").length() > 1) {
				
				reportError("Error parsing number");
				return -1;
			}
						
			if( !unaryStack.isEmpty() ) {
				
				result = unaryOperation( new BigDecimal( result ) ).toString();
			}
			
			pushExpectedNumber( new BigDecimal( result ));
			
			return index + result.length() - 1;
		}
		
		return -1;
	}
	
	/**
	 * Checks whether the operation is unary or binary. Useful especially for
	 * the minus sign, which is either used as a subtraction (binary) or 
	 * negate(unary) sign. Able to stack unary operations (like double negating)
	 * It checks if it's unary by seeing if it's immediately to the left of 
	 * another unary operator, or a number.
	 * 
	 * @param expression The entire expression to evaluate
	 * @param index The current index to evaluate
	 * @return The index where the unary operator ends, or -1 if not unary
	 */
	private int parseUnaryOperators( char expression[], int index) {
		
		char currentChar = expression[index];
		
		if( isUnaryOperator( currentChar) ) {
			
			for( int tempIndex = index;
					tempIndex < expression.length; tempIndex++ ) {
				
				if( Character.isDigit( expression[tempIndex] ) ) {
					
					return tempIndex-1;
				}
				
				else if( isUnaryOperator( expression[tempIndex]) ) {
					
					UnaryMathOperation operation = 
							unaryOperationMap.get( currentChar );
					unaryStack.push( operation );
				}
				
				else {
					
					unaryStack.clear();
					return -1;
				}
				
			}
			
		}
		
		unaryStack.removeAllElements();
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
		
		return unaryOperationMap.containsKey( character );
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
		}
		
	}

	/**
	 * Reports an error. If verbose messages are set to true in the constructor,
	 * then these messages are displayed. 
	 * 
	 * @param errorMessage
	 */
	private void reportError( String errorMessage ) {
		
		System.out.println("Error: " + errorMessage );		
		interpreterError = true;
	}

	/**
	 * Is called once for every unary operation stack. It calls multiple unary
	 * operations for each number, or however many operations are in the stack.
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
	 * The main expression logic. It parses character by character, jumping when
	 * evaluating groups of characters (numbers or stacked unary operators)
	 * It evaluates each character: First checks if it's "q", the terminate
	 * signal. Then it checks for a number, then checks for unary operators,
	 * then binary operators, then whitespace. If none of the above, it
	 * reports invalid.
	 * 
	 * @param expression The expression to evaluate
	 * @return The number result or null if there was an error
	 */
	
	//Main parse logic; returns a number or null if failed
	public BigDecimal parseExpression( String expression ) {		

		interpreterError = false;
		char expressionCharArray[] = expression.toCharArray();
		
		for( int i =0; i < expressionCharArray.length &&
				!interpreterError; i++ ) {
			
			char current = expressionCharArray[i];
			int newIndex = i;
			
			if( current == 'q') {
				
				sendTerminateSignal();
			}
			
			else {
				
				newIndex = parseNumber( expressionCharArray, i );
				
				//If not a number
				if( newIndex == -1 ) {
					
					newIndex = parseUnaryOperators( expressionCharArray, i);
					
					//If not a unary operation
					if( newIndex == -1 ) {
						
						//If a binary operation
						if( binaryOperationMap.containsKey(current)) {
							
							BinaryMathOperation operation = 
									binaryOperationMap.get( current );
							BigDecimal newNumber = binaryOperation( operation );
							
							if( newNumber != null ) {
								
								pushExpectedNumber( newNumber );
							}
							
						}
						
						//Finally, check if it isn't whitespace.
						else if( !isWhiteSpace( current) ) {
							
							reportError("Invalid character");
						}
						
					}
					
					else {
						
						i = newIndex; //Jump to new position from parsed unary
					}
					
				}
				
				else {
					
					i = newIndex; // Jump to new position from parsed number
				}
				
			}
			
		}
		
		if(!interpreterError && !numberStack.isEmpty()) {
			return numberStack.peek();
		}
		
		return null;
	}
	
	/**
	 * Calls parseExpression, then prints out the human-readable results
	 * 
	 * @param expression The expression to evaluate
	 */
	public void processExpression( String expression ) {
		
		BigDecimal number = parseExpression( expression);
		
		//Prints the stack if it's bigger than 1 so that the user doesn't forget
		if( numberStack.size() > 1) {
			
			String stack = Arrays.toString( numberStack.toArray() );
			System.out.println(stack);
		}
		
		if( number != null )  {

			System.out.println( number.toPlainString() );
		}

	}
	
	/**
	 * Sends the terminate signal to the interpreter.
	 * Can be called from outside the interpreter.
	 */
	public void sendTerminateSignal() {
		
		running = false;
	}

}
