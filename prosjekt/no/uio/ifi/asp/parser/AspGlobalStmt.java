package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import java.util.ArrayList;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class AspGlobalStmt extends AspSmallStmt {
	ArrayList<AspName> names = new ArrayList<>();
	
	AspGlobalStmt(int n) {
		super(n);
	}
	
	
	public static AspGlobalStmt parse(Scanner s) {
		enterParser("global stmt");
		
		AspGlobalStmt ags = new AspGlobalStmt(s.curLineNum());
		skip(s, globalToken);
		
		while (true) {
			ags.names.add(AspName.parse(s));
			if (s.curToken().kind != commaToken) break;
			skip(s, commaToken);
		}
		
		leaveParser("global stmt");
		return ags;
	}
	
	
	@Override
	public void prettyPrint() {
		prettyWrite(globalToken.toString()+" ");
		for (int i=0; i < names.size(); i++) {
			names.get(i).prettyPrint();
			if (i < names.size()-1) prettyWrite(commaToken.toString()+" ");
		}
	}
	
	
	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		for (AspName name : names) {
			curScope.registerGlobalName(name.name);
		}
		return null;
	}
}
