package com.christianbaum.rnpcalc.RPNCalc;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DivideOperation implements BinaryMathOperation {

	@Override
	public BigDecimal execute( BigDecimal divisor, BigDecimal number ) {
		
		//Divide by zero fails
		if( divisor.equals(BigDecimal.ZERO)) {
			
			return null;
		}
		
		/*BigDecimal cannot handle decimal divisors, so we round up at 0.5.
		 * Also, we limit irrational results to 20 decimals.
		 */
		BigDecimal result = number.divide( divisor, 20, RoundingMode.HALF_UP );
		
		//The result will have 20 decimal places, so remove trailing zeroes
		return result.stripTrailingZeros();
	}
	
	@Override
	public char symbol() {
		
		return '/';
	}

}
