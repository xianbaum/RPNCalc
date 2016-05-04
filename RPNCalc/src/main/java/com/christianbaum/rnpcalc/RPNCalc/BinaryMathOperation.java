package com.christianbaum.rnpcalc.RPNCalc;

import java.math.BigDecimal;

public interface BinaryMathOperation {

	/**
	 * Executes the math operation
	 * 
	 * @param number1 The first number (the older number in the stack)
	 * @param number2 The second number (the newer number in the stack)
	 * @return The result of the operation, or null if the operation failed.
	 */
	public BigDecimal execute( BigDecimal number1, BigDecimal number2 );
	
	/**
	 * Returns the character symbol for the operation.
	 * 
	 * @return The symbol that the operation uses.
	 */
	public char symbol();

}
