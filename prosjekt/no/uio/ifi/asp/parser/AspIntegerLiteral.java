package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeIntValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.integerToken;


public class AspIntegerLiteral extends AspAtom {
	long value;
	
	AspIntegerLiteral(int n) {
		super(n);
	}
	
	
	public static AspIntegerLiteral parse(Scanner s) {
		enterParser("integer literal");
		
		AspIntegerLiteral ail = new AspIntegerLiteral(s.curLineNum());
		ail.value = s.curToken().integerLit;
		skip(s, integerToken);
		
		leaveParser("integer literal");
		return ail;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(String.valueOf(value));
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeIntValue(value);
	}
	
}
