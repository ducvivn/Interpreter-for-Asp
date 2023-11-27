// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.Scanner;


public abstract class AspStmt extends AspSyntax {
	
	AspStmt(int n) {
		super(n);
	}
	
	
	static AspStmt parse(Scanner s) {
		enterParser("stmt");
		
		AspStmt as = null;
		
		// hvis compound-tokens
		if (s.isCompoundStmt())
			as = AspCompoundStmt.parse(s);
		
		else as = AspSmallStmtList.parse(s);
 
		leaveParser("stmt");
		return as;
	}
}
