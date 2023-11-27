package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;
import java.util.ArrayList;


public class AspComparison extends AspSyntax {
	ArrayList<AspTerm> terms = new ArrayList<>();
	ArrayList<AspCompOpr> oprs = new ArrayList<>();
	
	AspComparison(int n) {
		super(n);
	}
	
	
	public static AspComparison parse(Scanner s) {
		enterParser("comparison");
		
		AspComparison ac = new AspComparison(s.curLineNum());
		
		// sjekker token om operator og legger til
		while (true) {
			ac.terms.add(AspTerm.parse(s));
			if (!s.isCompOpr()) break;
			ac.oprs.add(AspCompOpr.parse(s));
		}
		
		leaveParser("comparison");
		return ac;
	}
	
	
	@Override
	public void prettyPrint() {
		for (int i=0; i < terms.size(); i++) {
			terms.get(i).prettyPrint();
			if (i < oprs.size()) oprs.get(i).prettyPrint();
		}
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue v = terms.get(0).eval(curScope);
		for (int i = 1; i < terms.size(); i++) {
			
			// operator og neste
			TokenKind opr = oprs.get(i-1).operator;
			v = terms.get(i-1).eval(curScope);
			RuntimeValue next = terms.get(i).eval(curScope);
			
			// comp opr
			switch (opr) {
				case lessToken -> v = v.evalLess(next, this);
				case greaterToken -> v = v.evalGreater(next, this);
				case doubleEqualToken -> v = v.evalEqual(next, this);
				case greaterEqualToken -> v = v.evalGreaterEqual(next, this);
				case lessEqualToken -> v = v.evalLessEqual(next, this);
				case notEqualToken -> v = v.evalNotEqual(next, this);
				default -> Main.panic("Illegal comp operator: " + opr + "!");
			}
			
			// stopper på false-verdi
			if (!v.getBoolValue("comp opr", this)) return v;
		}
		return v;
	}
}
