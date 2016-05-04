package com.christianbaum.rnpcalc.RPNCalc;

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for RPNCalc
 */
public class RPNTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RPNTest( String testName )
    {
    	
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
    	
        return new TestSuite( RPNTest.class );
    }

    /**
     * Runs parseExpression several times with various expressions and compares
     * the result to the expected output
     */
    public void testApp()
    {
    	
    	RPNInterpreter tester = new RPNInterpreter();
    	
    	//Pushing numbers
    	assertEquals( new BigDecimal("10"), tester.parseExpression("10"));
    	
    	//Pushing negative numbers
    	assertEquals( new BigDecimal("-5"), tester.parseExpression("-5"));
    	
    	//Addition
    	assertEquals( new BigDecimal("5"), tester.parseExpression("+"));
    	
    	//Subtraction
        assertEquals( new BigDecimal("4"), tester.parseExpression("2 -2 -"));
        
        //Multiplication
        assertEquals( new BigDecimal("20"), tester.parseExpression("*"));

        //Division, decimal outputs
        assertEquals( new BigDecimal("4"), tester.parseExpression("5 /"));
        
        //Exponents
        assertEquals( new BigDecimal("1024"), tester.parseExpression("5^"));
        
        /*Remainder operation support (not modulo, since it returns negative
         * numbers as remainders too */
        assertEquals( new BigDecimal("-1"), tester.parseExpression("-1 5 %") );
        
        //Divide by zero error
        assertEquals( null, tester.parseExpression("100 0 /"));
        
        //Gibberish
        assertEquals( null, tester.parseExpression("*O2)(*H&^GFJ*&^$%D%%_)OK"));
        
        //Very large number support
        assertEquals( new BigDecimal("10000094385000000000000000000000"), 
        		tester.parseExpression("1000000000000000000000 10000094385 *"));
    }
    
}

