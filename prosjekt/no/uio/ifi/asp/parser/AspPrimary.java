package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.leftBracketToken;
import static no.uio.ifi.asp.scanner.TokenKind.leftParToken;


public class AspPrimary extends AspSyntax {
	AspAtom atom;
	ArrayList<AspPrimarySuffix> aps = new ArrayList<>();
	
	AspPrimary(int n) {
		super(n);
	}
	
	
	public static AspPrimary parse(Scanner s) {
		enterParser("primary");
		
		AspPrimary ap = new AspPrimary(s.curLineNum());
		
		ap.atom = AspAtom.parse(s); // start atom
		
		// valgfri, flere primary suffix
		while (s.curToken().kind == leftParToken || s.curToken().kind == leftBracketToken) {
			ap.aps.add(AspPrimarySuffix.parse(s));
		}
		
		leaveParser("primary");
		return ap;
	}
	
	
	@Override
	public void prettyPrint() {
		atom.prettyPrint();
		for (AspPrimarySuffix ap : aps) {
			ap.prettyPrint();
		}
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue value = atom.eval(curScope);
		
		for (AspPrimarySuffix ps : aps) {
			
			// gjelder oppkall i lister/dicts/strings
			if (ps instanceof AspSubscription) {
				// lovlige subscriptions
				if (value instanceof RuntimeStringValue || value instanceof RuntimeListValue || value instanceof RuntimeDictValue) {
					value = value.evalSubscription(ps.eval(curScope), this);
				}
			}
			
			// gjelder funksjonskall
			else if (ps instanceof AspArguments && value instanceof RuntimeFunc) {
				
				RuntimeListValue psArgs = (RuntimeListValue) ps.eval(curScope);
				ArrayList<RuntimeValue> actualParams = psArgs.getList();
				
				// 2) funksjonens evalFuncCall kalles med to parametere: argumenter og kallets sted
				trace("Function call parameters: " + actualParams);
				value = value.evalFuncCall(actualParams, this);
			}
			else Main.panic("Illegal primary suffix " + ps + "!");
		}

		return value;
	}
}
