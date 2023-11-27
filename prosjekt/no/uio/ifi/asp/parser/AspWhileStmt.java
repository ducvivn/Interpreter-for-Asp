package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.colonToken;
import static no.uio.ifi.asp.scanner.TokenKind.whileToken;


public class AspWhileStmt extends AspCompoundStmt {
	
	AspExpr expr;
	AspSuite suite;
	
	AspWhileStmt(int n) {
		super(n);
	}
	
	
	public static AspWhileStmt parse(Scanner s) {
		enterParser("while stmt");
		
		AspWhileStmt aws = new AspWhileStmt(s.curLineNum());
		
		skip(s, whileToken);            // sjekk om start er "while"
		aws.expr = AspExpr.parse(s);    // parse uttrykk
		skip(s, colonToken);            // sjekk om kolon
		aws.suite = AspSuite.parse(s);    // parse suite
		
		leaveParser("while stmt");
		return aws;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(whileToken.toString()+" ");
		expr.prettyPrint();
		prettyWrite(colonToken.toString());
		suite.prettyPrint();
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		while (true) {
			RuntimeValue test = expr.eval(curScope);
			if (! test.getBoolValue("while loop test", this)) break;
			trace("while True: ...");
			suite.eval(curScope);
		}
		trace("while False:");
		return null;
	}
}
