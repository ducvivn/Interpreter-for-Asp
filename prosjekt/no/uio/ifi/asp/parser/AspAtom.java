package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.Scanner;


public abstract class AspAtom extends AspSyntax {
	
	AspAtom(int n) {
		super(n);
	}
	
	
	public static AspAtom parse(Scanner s) {
		enterParser("atom");
		
		AspAtom aa = null;
		
		switch (s.curToken().kind) {
			case nameToken -> aa = AspName.parse(s);
			case integerToken -> aa = AspIntegerLiteral.parse(s);
			case floatToken -> aa = AspFloatLiteral.parse(s);
			case stringToken -> aa = AspStringLiteral.parse(s);
			case trueToken, falseToken -> aa = AspBooleanLiteral.parse(s);
			case noneToken -> aa = AspNoneLiteral.parse(s);
			case leftParToken -> aa = AspInnerExpr.parse(s);
			case leftBracketToken -> aa = AspListDisplay.parse(s);
			case leftBraceToken -> aa = AspDictDisplay.parse(s);
			default -> parserError("Expected an expression atom but found a " + s.curToken() +"!", s.curLineNum());
		}
		
		leaveParser("atom");
		return aa;
	}
}
