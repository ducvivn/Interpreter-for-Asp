package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.returnToken;


public class AspReturnStmt extends AspSmallStmt {
	AspExpr expr;
	
	AspReturnStmt(int n) {
		super(n);
	}
	
	
	public static AspReturnStmt parse(Scanner s) {
		enterParser("return stmt");
		
		AspReturnStmt ars = new AspReturnStmt(s.curLineNum());
		skip(s, returnToken);
		ars.expr = AspExpr.parse(s);
		
		leaveParser("return stmt");
		return ars;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(returnToken.toString()+" ");
		expr.prettyPrint();
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		// fra forelesning uke 45 side 32
		RuntimeValue value = expr.eval(curScope);
		trace("return " + value.showInfo());
		throw new RuntimeReturnValue(value, lineNum);
	}
}
