package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class AspIfStmt extends AspCompoundStmt {
	
	ArrayList<AspExpr> exprs = new ArrayList<>();
	ArrayList<AspSuite> suites = new ArrayList<>();
	int antElifs = 0;
	
	AspIfStmt(int n) {
		super(n);
	}
	
	
	public static AspIfStmt parse(Scanner s) {
		enterParser("if stmt");
		
		AspIfStmt aif = new AspIfStmt(s.curLineNum());
		
		skip(s, ifToken); // sjekk om start er "if"
		
		// looper rundt condition
		while (true) {
			aif.exprs.add(AspExpr.parse(s));
			skip(s, colonToken);
			aif.suites.add(AspSuite.parse(s));
			if (s.curToken().kind != elifToken){
				aif.antElifs++;
				break;
			}
			skip(s, elifToken);
		}
		
		// sjekker om else finnes
		if (s.curToken().kind == elseToken) {
			skip(s, elseToken);
			skip(s, colonToken);
			aif.suites.add(AspSuite.parse(s));
		}
		
		leaveParser("if stmt");
		return aif;
	}
	
	
	@Override
	public void prettyPrint() {
		
		// nødvendig (if-delen)
		prettyWrite(ifToken.toString()+" ");
		exprs.get(0).prettyPrint();
		prettyWrite(colonToken.toString()+" ");
		suites.get(0).prettyPrint();
		
		// om flere expr = flere elifs
		for (int i=1; i < exprs.size(); i++) {
			prettyWrite(elifToken.toString()+" ");
			exprs.get(i).prettyPrint();
			prettyWrite(colonToken.toString()+" ");
			suites.get(i).prettyPrint();
		}
		
		// valgfritt (else-delen)
		// suites større enn expr = må være else
		if (suites.size() > exprs.size()) {
			prettyWrite(elseToken.toString());
			prettyWrite(colonToken.toString()+" ");
			suites.get(exprs.size()).prettyPrint();
		}
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		
		// if-del
		for (int i=0; i < exprs.size(); i++) {
			RuntimeValue test = exprs.get(i).eval(curScope);
			if (test.getBoolValue("if stmt test", this)) {
				trace("if: " + test);
				suites.get(i).eval(curScope);
				return null;
			}
		}
		// else-del - flere suites enn expr
		if (suites.size() > exprs.size()) {
			trace("else: ...");
			suites.get(suites.size()-1).eval(curScope);
		}
		return null;
	}
}
