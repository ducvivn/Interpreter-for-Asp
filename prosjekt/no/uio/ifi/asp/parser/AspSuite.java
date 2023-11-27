package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;

import java.util.ArrayList;

import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspSuite extends AspSyntax {
	AspSmallStmtList ssl;
	ArrayList<AspStmt> stmts = new ArrayList<>();
	
	AspSuite(int n) {
		super(n);
	}
	
	
	public static AspSuite parse(Scanner s) {
		enterParser("suite");
		
		AspSuite as = new AspSuite(s.curLineNum());
		
		// hvis ikke small stmt list
		if (s.curToken().kind == newLineToken) {
			skip(s, newLineToken);
			skip(s, indentToken);
			do {
				as.stmts.add(AspStmt.parse(s));
			} while (s.curToken().kind != dedentToken);
			skip(s, dedentToken);
		}
		// hvis small stmt list
		else as.ssl = AspSmallStmtList.parse(s);
		
		leaveParser("suite");
		return as;
	}
	
	
	@Override
	public void prettyPrint() {
		if (ssl != null) ssl.prettyPrint();
		else {
			prettyWriteLn();
			prettyIndent();
			for (AspStmt s : stmts)
				s.prettyPrint();
			prettyDedent();
		}
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue value = null;
		
		if (!stmts.isEmpty()) {
			for (AspStmt stmt : stmts) {
				value = stmt.eval(curScope);
			}
		} else value = ssl.eval(curScope);
		
		return value;
	}
}
