package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;
import java.util.ArrayList;


public class AspTerm extends AspSyntax {
	ArrayList<AspTermOpr> termOprs = new ArrayList<>();
	ArrayList<AspFactor> factors = new ArrayList<>();
	
	AspTerm(int n) {
		super(n);
	}
	
	
	public static AspTerm parse(Scanner s) {
		enterParser("term");
		
		AspTerm at = new AspTerm(s.curLineNum());
		
		// sjekker token om term opr og legger til
		while (true) {
			at.factors.add(AspFactor.parse(s));
			if (!s.isTermOpr()) break;
			at.termOprs.add(AspTermOpr.parse(s));
		}
		
		leaveParser("term");
		return at;
	}
	
	
	@Override
	public void prettyPrint() {
		for (int i=0; i < factors.size(); i++) {
			factors.get(i).prettyPrint();
			if (i < termOprs.size()) termOprs.get(i).prettyPrint();
		}
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue v = factors.get(0).eval(curScope);
		for (int i = 1; i < factors.size(); i++) {
			
			// operator og neste
			TokenKind opr = termOprs.get(i-1).operator;
			RuntimeValue next = factors.get(i).eval(curScope);
			
			// term opr
			switch (opr) {
				case minusToken -> v = v.evalSubtract(next, this);
				case plusToken -> v = v.evalAdd(next, this);
				default -> Main.panic("Illegal term operator: " + opr + "!");
			}
		}
		return v;
	}
}
