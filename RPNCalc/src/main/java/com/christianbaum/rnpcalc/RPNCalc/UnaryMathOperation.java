package com.christianbaum.rnpcalc.RPNCalc;

import java.math.BigDecimal;

public interface UnaryMathOperation {

	/**
	 * The actual operation for the unary math operation.
	 * 
	 * @param number The single to perform the unary operation on
	 * @return
	 */
	public BigDecimal execute( BigDecimal number );
	
	/**
	 * Returns the symbol that the unary operator uses
	 * 
	 * @return The symbol that the unary operator uses
	 */
	public char symbol();

}
