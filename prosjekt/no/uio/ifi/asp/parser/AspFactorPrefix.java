package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.Token;


public class AspFactorPrefix extends AspSyntax {
	Token prefix;
	
	AspFactorPrefix(int n) {
		super(n);
	}
	
	
	public static AspFactorPrefix parse(Scanner s) {
		enterParser("factor prefix");
		
		// enten + -
		AspFactorPrefix afp = new AspFactorPrefix(s.curLineNum());
		afp.prefix = s.curToken();
		skip(s, afp.prefix.kind);
		
		leaveParser("factor prefix");
		return afp;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(prefix.toString()+" ");
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		// brukes ikke
		return null;
	}
	
}
