package com.christianbaum.rnpcalc.RPNCalc;

import java.math.BigDecimal;
    
public class MultiplyOperation implements BinaryMathOperation {

    @Override
    public BigDecimal execute( BigDecimal number1, BigDecimal number2  ) {
        return number2.multiply( number1 );
    }
    
    @Override
    public char symbol() {
        return '*';
    }
    
}
