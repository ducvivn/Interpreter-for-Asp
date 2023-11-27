// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.scanner;

import java.util.EnumSet;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class Token {
	public TokenKind kind;
	public String name, stringLit;
	public long integerLit;
	public double floatLit;
	public int lineNum;
	
	// For å vite index til token i linjen
	public int lineIndex;
	public int endIndex;
	
	Token(TokenKind k) {
		this(k, 0);
	}
	
	Token(TokenKind k, int lNum) {
		kind = k;
		lineNum = lNum;
	}
	
	void checkResWords() {
		if (kind != nameToken) return;
		
		for (TokenKind tk : EnumSet.range(andToken, yieldToken)) {
			if (name.equals(tk.image)) {
				kind = tk;
				break;
			}
		}
	}
	
	public String showInfo() {
		String t = kind + " token";
		if (lineNum > 0) {
			t += " on line " + lineNum;
		}
		
		switch (kind) {
			case floatToken -> t += ": " + floatLit;
			case integerToken -> t += ": " + integerLit;
			case nameToken -> t += ": " + name;
			case stringToken -> {
				if (stringLit.indexOf('"') >= 0)
					t += ": '" + stringLit + "'";
				else
					t += ": " + '"' + stringLit + '"';
			}
		}
		return t;
	}
	
	public String shortInfo() {
		return switch (kind) {
			case floatToken -> Double.toString(floatLit);
			case integerToken -> Long.toString(integerLit);
			case nameToken -> name;
			case stringToken -> (stringLit.indexOf('"')>=0) ? '"'+stringLit+'"' : "'"+stringLit+"'";
			default -> kind.toString();
		};
	}
	
	@Override
	public String toString() {
		return kind.toString();
	}
}
