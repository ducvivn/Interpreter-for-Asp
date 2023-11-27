package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class AspFuncDef extends AspCompoundStmt {
	
	public AspName func_name; // funksjonsnavn
	public ArrayList<AspName> params = new ArrayList<>(); // "formelle parametere"
	public AspSuite func_suite; // innhold
	
	AspFuncDef(int n) {
		super(n);
	}
	
	
	public static AspFuncDef parse(Scanner s) {
		enterParser("func def");
		
		AspFuncDef afd = new AspFuncDef(s.curLineNum());
		
		// sjekker gyldig start
		skip(s, defToken);
		afd.func_name = AspName.parse(s);
		skip(s, leftParToken);
		
		// loop gjennom parametere
		while (s.curToken().kind != rightParToken) {
			afd.params.add(AspName.parse(s));
			if (s.curToken().kind != commaToken) break;
			skip(s, commaToken);
		}
		
		// sjekker gyldig slutt
		skip(s, rightParToken);
		skip(s, colonToken);
		afd.func_suite = AspSuite.parse(s);
		
		leaveParser("func def");
		return afd;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(defToken.toString()+" ");
		func_name.prettyPrint();
		prettyWrite(leftParToken.toString());
		
		for (int i=0; i < params.size(); i++) {
			params.get(i).prettyPrint();
			if (i < params.size()-1) prettyWrite(commaToken.toString()+" ");
		}
		
		prettyWrite(rightParToken.toString());
		prettyWrite(colonToken.toString());
		func_suite.prettyPrint();
		prettyWriteLn();
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		
		// (1) aktuelle parametre beregnmes og legges i en ArrayList
		ArrayList<RuntimeValue> args = new ArrayList<>();
		for (AspName param : params)
			args.add(new RuntimeStringValue(param.name));
		
		trace("Def: " + this.func_name.name);
		RuntimeFunc function = new RuntimeFunc(this, curScope, func_name.name, args);
		curScope.assign(func_name.name, function);
		
		return null;
	}
}
