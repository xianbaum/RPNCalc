package com.christianbaum.rnpcalc.RPNCalc;

import java.math.BigDecimal;

public class RemainderOperation implements BinaryMathOperation {

	@Override
	public BigDecimal execute( BigDecimal divisor, BigDecimal number ) {
		
		return number.remainder( divisor );
	}
	
	@Override
	public char symbol() {
		
		return '%';
	}

}
