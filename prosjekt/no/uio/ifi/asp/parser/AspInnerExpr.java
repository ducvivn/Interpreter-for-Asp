package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.leftParToken;
import static no.uio.ifi.asp.scanner.TokenKind.rightParToken;


public class AspInnerExpr extends AspAtom {
	
	AspExpr expr;
	
	AspInnerExpr(int n) {
		super(n);
	}
	
	public static AspInnerExpr parse(Scanner s) {
		enterParser("inner expr");
		
		AspInnerExpr aie = new AspInnerExpr(s.curLineNum());
		
		skip(s, leftParToken);        	// start ok
		aie.expr = AspExpr.parse(s); 	// legg til uttrykk
		skip(s, rightParToken);     	// slutt ok
		
		leaveParser("inner expr");
		return aie;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(leftParToken.toString());
		expr.prettyPrint();
		prettyWrite(rightParToken.toString());
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return expr.eval(curScope);
	}
}
