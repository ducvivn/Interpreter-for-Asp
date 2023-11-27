package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class AspArguments extends AspPrimarySuffix {
	ArrayList<AspExpr> exprs = new ArrayList<>();
	
	
	AspArguments(int n) {
		super(n);
	}
	
	
	public static AspArguments parse(Scanner s) {
		enterParser("arguments");
		
		AspArguments ae = new AspArguments(s.curLineNum());
		
		skip(s, TokenKind.leftParToken); // start ok
		
		// flere uttrykk separert med komma
		while (s.curToken().kind != rightParToken) {
			ae.exprs.add(AspExpr.parse(s));
			if (s.curToken().kind == commaToken) {
				skip(s, commaToken);
				
				// sørger for ingen sluttt med komma
				if (s.curToken().kind == rightParToken)
					parserError("Illegal end of argument. Expected "+rightParToken+" but found "+commaToken+" !", s.curLineNum());
			}
		}
		
		skip(s, rightParToken); // slutt ok
		
		leaveParser("arguments");
		
		return ae;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(leftParToken.toString());
		for (int i=0; i < exprs.size(); i++) {
			exprs.get(i).prettyPrint();
			if (i < exprs.size()-1) prettyWrite(commaToken.toString()+" ");
		}
		prettyWrite(rightParToken.toString());
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		ArrayList<RuntimeValue> list = new ArrayList<>();
		
		// (1) Aktuelle parametere beregnes og returneres
		for (AspExpr expr : exprs) {
			list.add(expr.eval(curScope));
		}
		return new RuntimeListValue(list);
	}
}
