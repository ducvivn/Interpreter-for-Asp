package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.noneToken;


public class AspNoneLiteral extends AspAtom {
	
	AspNoneLiteral(int n) {
		super(n);
	}
	
	
	public static AspNoneLiteral parse(Scanner s) {
		enterParser("none literal");
		
		AspNoneLiteral anl = new AspNoneLiteral(s.curLineNum());
		skip(s, noneToken);
		
		leaveParser("none literal");
		return anl;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(noneToken.toString());
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeNoneValue();
	}
}
