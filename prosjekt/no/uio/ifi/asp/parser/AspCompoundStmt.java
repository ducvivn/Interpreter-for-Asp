package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.Scanner;


public abstract class AspCompoundStmt extends AspStmt {
	AspCompoundStmt(int n) {
		super(n);
	}
	
	
	public static AspCompoundStmt parse(Scanner s) {
		enterParser("compound stmt");
		
		AspCompoundStmt acs = null;
		
		switch (s.curToken().kind) {
			case forToken -> acs = AspForStmt.parse(s);
			case ifToken -> acs = AspIfStmt.parse(s);
			case whileToken -> acs = AspWhileStmt.parse(s);
			case defToken -> acs = AspFuncDef.parse(s);
			default -> parserError("Error parsing compound stmt", s.curLineNum());
		}
		
		leaveParser("compound stmt");
		return acs;
	}
}
