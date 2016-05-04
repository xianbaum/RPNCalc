package com.christianbaum.rnpcalc.RPNCalc;

import java.math.BigDecimal;

public class ExponentOperation implements BinaryMathOperation {

	@Override
	public BigDecimal execute( BigDecimal exponent, BigDecimal number ) {
		
		// We need to check if the exponent overflows or not
		int exponentInt = exponent.intValue(); 
		
		/* Checking if BigDecimal is the same after converting to an integer.
		 * Also, BigDecimal.pow cannot handle negative numbers.
		 */
		if( exponent.equals( new BigDecimal(exponentInt)) &&
				exponent.compareTo( BigDecimal.ZERO ) != -1 ) {
			
			return number.pow( exponentInt );
		}
		
		//Failed
		return null;
	}
	
	@Override
	public char symbol() {
		return '^';
	}

}
