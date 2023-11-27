package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.Token;


public class AspFactorOpr extends AspSyntax {
	Token operator;
	
	AspFactorOpr(int n) {
		super(n);
	}
	
	
	public static AspFactorOpr parse(Scanner s) {
		enterParser("factor opr");
		
		// enten * / % //
		AspFactorOpr afo = new AspFactorOpr(s.curLineNum());
		afo.operator = s.curToken();
		skip(s, s.curToken().kind);
		
		leaveParser("factor opr");
		return afo;
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
