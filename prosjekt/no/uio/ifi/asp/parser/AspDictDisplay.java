package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import java.util.HashMap;


import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspDictDisplay extends AspAtom {
	ArrayList<AspStringLiteral> keys = new ArrayList<>();
	ArrayList<AspExpr> values = new ArrayList<>();
	
	
	AspDictDisplay(int n) {
		super(n);
	}
	
	
	public static AspDictDisplay parse(Scanner s) {
		enterParser("dict display");
		
		AspDictDisplay add = new AspDictDisplay(s.curLineNum());
		
		skip(s, leftBraceToken); // start ok
		
		// valgfri, flere nøkler og verdier
		while (s.curToken().kind != rightBraceToken) {
			add.keys.add(AspStringLiteral.parse(s));
			skip(s, colonToken);
			add.values.add(AspExpr.parse(s));
			
			if (s.curToken().kind == commaToken) {
				skip(s, commaToken);
				
				// sørger for ingen sluttt med komma
				if (s.curToken().kind == rightBraceToken)
					parserError("Illegal end of dictionary. Expected "+rightBraceToken+" but found "+commaToken+" !", s.curLineNum());			}
		}
		
		skip(s, rightBraceToken); // slutt ok
		
		leaveParser("dict display");
		return add;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(leftBraceToken.toString());
		
		for (int i=0; i < keys.size(); i++) {
			keys.get(i).prettyPrint();
			prettyWrite(colonToken.toString());
			values.get(i).prettyPrint();
			if (i < keys.size()-1) prettyWrite(commaToken.toString()+" ");
		}
		
		prettyWrite(rightBraceToken.toString());
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		HashMap<RuntimeStringValue, RuntimeValue> dict = new HashMap<>();
		
		for (int i = 0; i < keys.size(); i++) {
			RuntimeStringValue key = (RuntimeStringValue) keys.get(i).eval(curScope);
			RuntimeValue value = values.get(i).eval(curScope);
			
			dict.put(key, value);
		}
		
		return new RuntimeDictValue(dict);
	}
}
