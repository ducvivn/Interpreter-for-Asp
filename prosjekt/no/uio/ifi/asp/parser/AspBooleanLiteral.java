package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeBoolValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;


public class AspBooleanLiteral extends AspAtom {
	boolean state;
	
	AspBooleanLiteral(int n) {
		super(n);
	}
	
	
	public static AspBooleanLiteral parse(Scanner s) {
		enterParser("boolean literal");
		
		AspBooleanLiteral abl = new AspBooleanLiteral(s.curLineNum());
		switch (s.curToken().kind) {
			case trueToken -> abl.state = true;
			case falseToken -> abl.state = false;
		}
		skip(s, s.curToken().kind);
		
		leaveParser("boolean literal");
		return abl;
	}
	
	
	@Override
	public void prettyPrint() {
		String bool = Boolean.toString(state);
		String capBool = bool.substring(0, 1).toUpperCase() + bool.substring(1);
		prettyWrite(capBool);
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeBoolValue(state);
	}
}
