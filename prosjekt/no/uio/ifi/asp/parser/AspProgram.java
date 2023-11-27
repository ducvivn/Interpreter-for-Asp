// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.eofToken;


public class AspProgram extends AspSyntax {
	ArrayList<AspStmt> stmts = new ArrayList<>();
	
	AspProgram(int n) {
		super(n);
	}
	
	
	public static AspProgram parse(Scanner s) {
		enterParser("program");
		
		AspProgram ap = new AspProgram(s.curLineNum());
		
		
		// sjekk om neste er stmt
		while (s.curToken().kind != eofToken) {
			ap.stmts.add(AspStmt.parse(s));
		}
		skip(s, eofToken);
		
		leaveParser("program");
		return ap;
	}
	
	
	@Override
	public void prettyPrint() {
		for (AspStmt stm : stmts)
			stm.prettyPrint();
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		// fra forelesning uke 45 side 33
		for (AspStmt stmt : stmts) {
			try {
				stmt.eval(curScope);
			} catch (RuntimeReturnValue rrv) {
				RuntimeValue.runtimeError("Return statement outside function!", rrv.lineNum);
			}
		}
		return null;
	}
}
