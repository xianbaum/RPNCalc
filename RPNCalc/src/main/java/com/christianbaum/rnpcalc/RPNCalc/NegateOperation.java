package com.christianbaum.rnpcalc.RPNCalc;

import java.math.BigDecimal;

public class NegateOperation implements UnaryMathOperation {

	@Override
	public BigDecimal execute( BigDecimal number ) {
		
		return number.negate();
	}
	
	@Override
	public char symbol() {
		
		return '-';
	}

}
