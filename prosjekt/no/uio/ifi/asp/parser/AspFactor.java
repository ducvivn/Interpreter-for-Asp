package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;


import java.util.ArrayList;

public class AspFactor extends AspSyntax {
	ArrayList<AspFactorOpr> factorOprs = new ArrayList<>();
	ArrayList<AspFactorPrefix> prefixes = new ArrayList<>();
	ArrayList<AspPrimary> primaries = new ArrayList<>();
	
	AspFactor(int n) {
		super(n);
	}
	
	
	public static AspFactor parse(Scanner s) {
		enterParser("factor");
		
		AspFactor af = new AspFactor(s.curLineNum());
		
		while (true) {
			// legger til prefiks om finnes
			if (s.isFactorPrefix())
				af.prefixes.add((AspFactorPrefix.parse(s)));
			else af.prefixes.add(null);
			
			af.primaries.add(AspPrimary.parse(s));
			
			// legger til operator om finnes
			if (!s.isFactorOpr()) break;
			af.factorOprs.add(AspFactorOpr.parse(s));
		}
		
		leaveParser("factor");
		return af;
	}
	
	
	@Override
	public void prettyPrint() {
		for (int i=0; i < primaries.size(); i++) {
			if (prefixes.get(i) != null) prefixes.get(i).prettyPrint();
			primaries.get(i).prettyPrint();
			if (i < factorOprs.size()) factorOprs.get(i).prettyPrint();
		}
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue value = primaries.get(0).eval(curScope);
		
		// evaluerer første primary og sin prefix
		if (!prefixes.isEmpty()) {
			if (prefixes.get(0) != null) {
				TokenKind prefix = prefixes.get(0).prefix.kind;
				switch (prefix) {
					case plusToken -> value = value.evalPositive(this);
					case minusToken -> value = value.evalNegate(this);
					default -> Main.panic("Illegal factor prefix " + prefix + "!");
				}
			}
		}
		// går gjennom resten av primaries
		for (int i = 1; i < primaries.size(); i++) {
			RuntimeValue neste = primaries.get(i).eval(curScope);
			TokenKind operator = factorOprs.get(i-1).operator.kind;
			
			// evaluerer neste sin prefix
			if (!prefixes.isEmpty()) {
				if (prefixes.get(i) != null) {
					TokenKind prefix = prefixes.get(i).prefix.kind;
					switch (prefix) {
						case plusToken -> neste = neste.evalPositive(this);
						case minusToken -> neste = neste.evalNegate(this);
						default -> Main.panic("Illegal factor prefix " + prefix + "!");
					}
				}
			}
			
			// evaulerer uttrykk så langt basert på operator
			switch (operator) {
				case astToken -> value = value.evalMultiply(neste, this);
				case slashToken -> value = value.evalDivide(neste, this);
				case percentToken -> value = value.evalModulo(neste, this);
				case doubleSlashToken -> value = value.evalIntDivide(neste, this);
				default -> Main.panic("Illegal factor operator " + operator + "!");
			}
		}
		return value;
	}
}