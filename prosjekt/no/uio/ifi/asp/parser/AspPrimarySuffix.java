package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.Scanner;


public abstract class AspPrimarySuffix extends AspSyntax {
	
	AspPrimarySuffix(int n) {
		super(n);
	}
	
	
	public static AspPrimarySuffix parse(Scanner s) {
		enterParser("primary suffix");
		
		AspPrimarySuffix aps = null;
		
		// enten argument ( ) eller subscription [ ]
		switch (s.curToken().kind) {
			case leftParToken -> aps = AspArguments.parse(s);
			case leftBracketToken -> aps = AspSubscription.parse(s);
			default -> parserError("Error parsing primary suffix", s.curLineNum());
		}
		
		leaveParser("primary suffix");
		return aps;
	}
}
