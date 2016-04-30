package com.christianbaum.rpncalc;

import java.math.BigDecimal;

public interface BinaryMathOperation {
	public BigDecimal execute( BigDecimal number1, BigDecimal number2 );
}
