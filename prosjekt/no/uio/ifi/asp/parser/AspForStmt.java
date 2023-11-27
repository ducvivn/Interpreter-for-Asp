package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class AspForStmt extends AspCompoundStmt {
	
	AspName name;
	AspExpr expr;
	AspSuite suite;
	
	AspForStmt(int n) {
		super(n);
	}
	
	
	public static AspForStmt parse(Scanner s) {
		enterParser("for stmt");
		
		AspForStmt afs = new AspForStmt(s.curLineNum());
		
		skip(s, forToken);                // sjekk om start er "for"
		afs.name = AspName.parse(s);    // parser name
		skip(s, inToken);                // sjekk om "in" følger
		afs.expr = AspExpr.parse(s);    // parser uttrykk
		skip(s, colonToken);            // sjekk om kolon følger
		afs.suite = AspSuite.parse(s);  // parser suite
		
		leaveParser("for stmt");
		return afs;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(forToken.toString()+" ");
		name.prettyPrint();
		prettyWrite(" "+inToken.toString()+" ");
		expr.prettyPrint();
		prettyWrite(colonToken.toString());
		suite.prettyPrint();
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		
		RuntimeValue test = expr.eval(curScope);
		
		// gyldig om liste
		if (test instanceof RuntimeListValue) {
			ArrayList<RuntimeValue> list = ((RuntimeListValue) test).getList();
			
			for (int i=0; i < list.size(); i++) {
				trace("for " + name.name + " in " + list);
				
				String name = this.name.name;
				RuntimeValue elem = list.get(i);
				curScope.assign(name, elem);
				suite.eval(curScope);
			}
		}
		return null;
	}
}
