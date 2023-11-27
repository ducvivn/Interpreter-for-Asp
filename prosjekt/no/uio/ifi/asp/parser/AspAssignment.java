package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;
import java.util.ArrayList;


public class AspAssignment extends AspSmallStmt {
	AspName name;
	ArrayList<AspSubscription> subscriptions = new ArrayList<>();
	AspExpr expr;
	
	AspAssignment(int n) {
		super(n);
	}
	
	public static AspAssignment parse(Scanner s) {
		enterParser("assignment");
		
		AspAssignment aa = new AspAssignment(s.curLineNum());
		
		aa.name = AspName.parse(s); // start uttrykk
		
		// subscription valgfri, sjekk flere
		while (s.curToken().kind != TokenKind.equalToken) {
			aa.subscriptions.add(AspSubscription.parse(s));
		}
		
		skip(s, TokenKind.equalToken); // sjekk =
		aa.expr = AspExpr.parse(s); // slutt uttrykk
		
		leaveParser("assignment");
		return aa;
	}
	
	
	@Override
	public void prettyPrint() {
		name.prettyPrint();
		
		for (AspSubscription subscription : subscriptions)
			subscription.prettyPrint();
		
		prettyWrite(" "+TokenKind.equalToken.toString()+" ");
		expr.prettyPrint();
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		
		RuntimeValue value = this.expr.eval(curScope);  // beregner høyresiden (3)
		RuntimeScope global = Main.globalScope; // globalskopet
		
		// indeksering f.eks. a[1] = 2
		if (!subscriptions.isEmpty()) {
			RuntimeValue a = name.eval(curScope);  // evaluerer <name> som a (1)
			
			// itererer til nest-siste subscription
			for (int i=0; i < subscriptions.size() - 1; i++) {
				RuntimeValue subExp = subscriptions.get(i).eval(curScope);
				a = a.evalSubscription(subExp, this);
			}
			
			// evaluerer siste subscription
			AspSubscription siste  = subscriptions.get(subscriptions.size()-1);
			RuntimeValue indeks = siste.eval(curScope);  // beregner indeksen (2)
			a.evalAssignElem(indeks, value, this);
			trace("assign subscription: "+ name.name +"["+indeks.showInfo()+"] = " + value.showInfo());
		}
		// ren tilordning f.eks a = 2
		else {
			// finnes i global skope
			if (global.hasGlobalName(name.name)) {
				global.assign(name.name, value);
				trace("assigning global: " + name.name + " to " + expr.toString());
			}
			// finnes ikke globalt
			else {
				curScope.assign(name.name, value);
				trace("assigning: " + name.name + " = " + value.showInfo());
			}
		}
		return null;
	}
	
}
