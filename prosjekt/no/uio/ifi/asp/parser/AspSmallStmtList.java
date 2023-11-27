package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class AspSmallStmtList extends AspStmt {
	ArrayList<AspSmallStmt> smallStmts = new ArrayList<>();
	
	AspSmallStmtList(int n) {
		super(n);
	}
	
	
	public static AspSmallStmtList parse(Scanner s) {
		enterParser("small stmt list");
		
		AspSmallStmtList assl = new AspSmallStmtList(s.curLineNum());
		
		assl.smallStmts.add(AspSmallStmt.parse(s)); // nødvendig small stmt
		
		// hvis semikolon, er neste enten ny small stmt eller newline
		while (s.curToken().kind == semicolonToken) {
			skip(s, semicolonToken);
			if (s.curToken().kind == newLineToken) break;
			assl.smallStmts.add(AspSmallStmt.parse(s));
		}
		
		skip(s, newLineToken);    // slutt med newline
		
		leaveParser("small stmt list");
		return assl;
	}
	
	
	@Override
	public void prettyPrint() {
		for (int i=0; i < smallStmts.size(); i++) {
			smallStmts.get(i).prettyPrint();
			if (i < smallStmts.size()-1) prettyWrite(semicolonToken.toString()+" ");
		}
		prettyWriteLn(); // newline
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue smallstmt = null;
		for (AspSmallStmt ss : smallStmts) {
			smallstmt = ss.eval(curScope);
		}
		return smallstmt;
	}
	
}
