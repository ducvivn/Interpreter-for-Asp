package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.notToken;


public class AspNotTest extends AspSyntax {
	AspComparison comp;
	boolean not = false;
	
	AspNotTest(int n) {
		super(n);
	}
	
	static AspNotTest parse(Scanner s) {
		enterParser("not test");
		
		AspNotTest ant = new AspNotTest(s.curLineNum());
		
		// sjekk om "not-token"
		if (s.curToken().kind == notToken) {
			ant.not = true;
			skip(s, notToken);
		}
		ant.comp = AspComparison.parse(s); // parse sammenligning
		
		leaveParser("not test");
		return ant;
	}
	
	@Override
	void prettyPrint() {
		if (not) prettyWrite(notToken.toString()+" ");
		comp.prettyPrint();
	}
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue v = comp.eval(curScope);
		return not ? v.evalNot(this) : v;
	}
}
