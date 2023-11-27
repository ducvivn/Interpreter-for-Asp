package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeFloatValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;


import static no.uio.ifi.asp.scanner.TokenKind.floatToken;

public class AspFloatLiteral extends AspAtom {
	double value;
	
	AspFloatLiteral(int n) {
		super(n);
	}
	
	
	public static AspFloatLiteral parse(Scanner s) {
		enterParser("float literal");
		
		AspFloatLiteral afl = new AspFloatLiteral(s.curLineNum());
		afl.value = s.curToken().floatLit;
		skip(s, floatToken);
		
		leaveParser("float literal");
		return afl;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(Double.toString(value));
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeFloatValue(value);
	}
}
