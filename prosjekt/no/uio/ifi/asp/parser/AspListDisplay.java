package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class AspListDisplay extends AspAtom {
	ArrayList<AspExpr> exprs = new ArrayList<>();
	
	AspListDisplay(int n) {
		super(n);
	}
	
	
	public static AspListDisplay parse(Scanner s) {
		enterParser("list display");
		
		AspListDisplay ald = new AspListDisplay(s.curLineNum());
		
		skip(s, leftBracketToken); // start ok
		
		// valgfri, flere uttrykk separert med komma
		while (s.curToken().kind != rightBracketToken) {
			ald.exprs.add(AspExpr.parse(s));
			if (s.curToken().kind == commaToken) {
				skip(s, commaToken);
				
				// sørger for ingen sluttt med komma
				if (s.curToken().kind == rightBracketToken)
					parserError("Illegal end of list. Expected "+rightBracketToken+" but found "+commaToken+" !", s.curLineNum());
			}
		}
		
		skip(s, rightBracketToken); // slutt ok
		
		leaveParser("list display");
		return ald;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(leftBracketToken.toString());
		
		for (int i = 0; i < exprs.size(); i++) {
			exprs.get(i).prettyPrint();
			if (i < exprs.size()-1) prettyWrite(commaToken.toString()+" ");
		}
		
		prettyWrite(rightBracketToken.toString());
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		ArrayList<RuntimeValue> list = new ArrayList<>();
		for (AspExpr expr : exprs) {
			list.add(expr.eval(curScope));
		}
		return new RuntimeListValue(list);
	}
}
