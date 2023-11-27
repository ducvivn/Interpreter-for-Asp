package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.Scanner;

public abstract class AspSmallStmt extends AspStmt {
	AspSmallStmt(int n) {
		super(n);
	}
	
	
	public static AspSmallStmt parse(Scanner s) {
		enterParser("small stmt");
		
		AspSmallStmt ass = null;
		if (s.anyEqualToken()) ass = AspAssignment.parse(s);
		else switch (s.curToken().kind) {
			case returnToken -> ass = AspReturnStmt.parse(s);
			case passToken -> ass = AspPassStmt.parse(s);
			case globalToken -> ass = AspGlobalStmt.parse(s);
			default -> ass = AspExprStmt.parse(s);
		}
		
		leaveParser("small stmt");
		return ass;
	}
}