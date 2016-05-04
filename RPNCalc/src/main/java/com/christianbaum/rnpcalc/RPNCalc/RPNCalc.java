package com.christianbaum.rnpcalc.RPNCalc;

import java.util.Scanner;

class RPNCalc {

	public static void main( String args[] ) {
		
		RPNInterpreter interpreter = new RPNInterpreter();
		Scanner scanner = new Scanner(System.in);
		
		while( interpreter.isRunning() ) {
			
			System.out.printf(">");
			String expression = scanner.nextLine();
			interpreter.processExpression(expression);
		}
		
		scanner.close();
	}
	
}
