package com.christianbaum.rpncalc;

import java.util.Scanner;

class RPNCalc {
	public static void main( String args[] ) {
		boolean printStack = false;
		boolean printVerboseErrors = false;
		for( int i = 1; i < args.length; i++ ) {
			if( args[i].equals("-s")) {
				printStack = true;
			}
			else if( args[i].equals("-v")) {
				printVerboseErrors = true;
			}
		}
		runInterpreter( printStack, printVerboseErrors );
	}
	
	/**
	 * 
	 */
	private static void runInterpreter( boolean printStack, boolean printVerboseErrors ) {
		Interpreter interpreter = new Interpreter( printStack, printVerboseErrors);
		Scanner scanner = new Scanner(System.in);
		while( interpreter.isRunning() ) {
			System.out.printf(">");
			String expression = scanner.nextLine();
			interpreter.processExpression(expression);
		}
		scanner.close();
	}
}
