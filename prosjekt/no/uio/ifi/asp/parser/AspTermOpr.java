package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;


public class AspTermOpr extends AspSyntax {
	TokenKind operator;
	
	AspTermOpr(int n) {
		super(n);
	}
	
	
	public static AspTermOpr parse(Scanner s) {
		enterParser("term opr");
		
		// enten + -
		AspTermOpr ato = new AspTermOpr(s.curLineNum());
		ato.operator = s.curToken().kind;
		skip(s, ato.operator);
		
		leaveParser("term opr");
		return ato;
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
