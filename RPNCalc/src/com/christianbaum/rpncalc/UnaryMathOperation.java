package com.christianbaum.rpncalc;

import java.math.BigDecimal;

public interface UnaryMathOperation {
	public BigDecimal execute( BigDecimal number );
}
