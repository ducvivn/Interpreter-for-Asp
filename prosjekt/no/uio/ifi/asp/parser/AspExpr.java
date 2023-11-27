// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.orToken;


public class AspExpr extends AspSyntax {
	ArrayList<AspAndTest> andTests = new ArrayList<>();
	
	AspExpr(int n) {
		super(n);
	}
	
	
	public static AspExpr parse(Scanner s) {
		enterParser("expr");
		
		AspExpr expr = new AspExpr(s.curLineNum());
		
		// sjekker om flere and-tests
		while (true) {
			expr.andTests.add(AspAndTest.parse(s));
			if (s.curToken().kind != orToken) break;
			skip(s, orToken);
		}
		
		leaveParser("expr");
		return expr;
	}
	
	
	@Override
	public void prettyPrint() {
		for (int i=0; i < andTests.size(); i++) {
			andTests.get(i).prettyPrint();
			if (i < andTests.size()-1) prettyWrite(" "+orToken.toString()+" ");
		}
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue v = andTests.get(0).eval(curScope);
		for (int i = 1; i < andTests.size(); ++i) {
			if (v.getBoolValue("or operand", this))
				return v;
			v = andTests.get(i).eval(curScope);
		}
		return v;
	}
}
