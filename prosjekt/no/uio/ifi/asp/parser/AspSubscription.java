package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.leftBracketToken;
import static no.uio.ifi.asp.scanner.TokenKind.rightBracketToken;


public class AspSubscription extends AspPrimarySuffix {
	AspExpr expr;
	
	AspSubscription(int n) {
		super(n);
	}
	
	
	public static AspSubscription parse(Scanner s) {
		enterParser("subscription");
		
		AspSubscription sub = new AspSubscription(s.curLineNum());
		
		skip(s, leftBracketToken);        // gyldig start
		sub.expr = AspExpr.parse(s);    // uttrykket
		skip(s, rightBracketToken);    // gyldig slutt
		
		leaveParser("subscription");
		return sub;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(leftBracketToken.toString());
		expr.prettyPrint();
		prettyWrite(rightBracketToken.toString());
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return expr.eval(curScope);
	}
}
