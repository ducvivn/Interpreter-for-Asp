package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;


public class AspCompOpr extends AspSyntax {
	
	TokenKind operator;
	
	AspCompOpr(int n) {
		super(n);
	}
	
	
	public static AspCompOpr parse(Scanner s) {
		enterParser("comp opr");
		
		// enten < > == >= <= !=
		AspCompOpr aco = new AspCompOpr(s.curLineNum());
		aco.operator = s.curToken().kind;
		skip(s, aco.operator);
		
		leaveParser("comp opr");
		return aco;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(" "+operator.toString()+" ");
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		// brukes ikke
		return null;
	}
}
