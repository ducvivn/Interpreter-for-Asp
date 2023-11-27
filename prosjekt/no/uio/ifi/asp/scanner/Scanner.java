// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.scanner;

import no.uio.ifi.asp.main.Main;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class Scanner {
	private LineNumberReader sourceFile = null;
	private String curFileName;
	private ArrayList<Token> curLineTokens = new ArrayList<>();
	private Stack<Integer> indents = new Stack<>();
	private final int TABDIST = 4;
	
	
	/**
	 * Konstruktør til scanner
	 * tar inn fil og lager en "line-reader"
	 *
	 * @param fileName : filnavn som skal leses
	 */
	public Scanner(String fileName) {
		curFileName = fileName;
		indents.push(0);
		
		try {
			sourceFile = new LineNumberReader(
					new InputStreamReader(
							new FileInputStream(fileName),
							"UTF-8"));
		} catch (IOException e) {
			scannerError("Cannot read " + fileName + "!");
		}
	}
	
	
	/**
	 * Feilmeldingsmetoden for scanner-klassen
	 *
	 * @param message : melding for feilmelding
	 */
	private void scannerError(String message) {
		String m = "Asp scanner error";
		if (curLineNum() > 0)
			m += " on line " + curLineNum();
		m += ": " + message;
		
		Main.error(m);
	}
	
	
	/**
	 * Henter * nåværende symbol - symbol først i curLineTokens (.get(0))
	 *
	 * @return current token
	 */
	public Token curToken() {
		while (curLineTokens.isEmpty()) {
			readNextLine();
		}
		return curLineTokens.get(0);
	}
	
	
	/**
	 * Fjerner nåværende symbol
	 */
	public void readNextToken() {
		if (!curLineTokens.isEmpty())
			curLineTokens.remove(0);
	}
	
	// -------------------------------------------------------------------------------
	// Metoder for å finne symboler
	
	/**
	 * Identifiserer og lager string tokens basert på iterasjon
	 *
	 * @param: String line : linje som undersøkes
	 */
	private void stringFind(String line) {
		boolean insideString = false;
		char stringDelimiter = '\0'; // char == null
		
		StringBuilder currentString = new StringBuilder();
		ArrayList<String> allStrings = new ArrayList<>();
		ArrayList<Integer> stringIndexes = new ArrayList<>();
		
		String startQuote = null;
		String endQuote = null;
		
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '#') break;
			
			// Sjekker om string starter
			if ((c == '\'' || c == '"') && (stringDelimiter == '\0' || stringDelimiter == c)) {
				if (insideString) {
					allStrings.add(currentString.toString());
					stringIndexes.add(i - currentString.length());
					currentString = new StringBuilder();
				}
				insideString = !insideString;
				stringDelimiter = (insideString) ? c : '\0'; // Setter eller resetter delimiteren
				
				// endrer string-quote-type
				if (startQuote == null) {
					startQuote = String.valueOf(c);
				} else if (endQuote == null) {
					endQuote = String.valueOf(c);
				}
				continue;
			}
			// om string - lag setning inne i string
			if (insideString) {
				currentString.append(c);
			}
		}
		// legger til string i tilfeller hvor det er slutten av linjen
		if (!currentString.isEmpty()) {
			allStrings.add(currentString.toString());
			stringIndexes.add(line.length() - currentString.length()); // Save the start index of the last string
		}
		// Lager tokens om godkjent - kvoter rundt er like
		if (startQuote != null && endQuote != null) {
			if (startQuote.equals(endQuote)) {
				for (int k = 0; k < allStrings.size(); k++) {
					if (k < stringIndexes.size()) {
						int lineIndex = stringIndexes.get(k);
						String lineString = allStrings.get(k);
						Token stringToken = new Token(TokenKind.stringToken, curLineNum());
						stringToken.stringLit = lineString;
						stringToken.lineIndex = lineIndex-1;
						stringToken.endIndex = stringIndexes.get(k) + allStrings.get(k).length();
						curLineTokens.add(stringToken);
					}
				}
			}
			// ikke godkjent - (ulike start og slutt på string)
			else {
				scannerError("String [" + currentString + "] on line " + curLineNum() + " is invalid");
			}
		}
		// om en er sant og andre er usann - strng åpnet/lukket feil
		else if ((startQuote == null) ^ (endQuote == null)) {
			scannerError("String [" + currentString + "] on line " + curLineNum() + " is invalid");
		}
	}
	
	/**
	 * Identifiserer og lager navn tokens basert på iterasjon
	 *
	 * @param: String line : linje som undersøkes
	 */
	private void nameFind(String line) {
		boolean insideString = false;
		char stringDelimiter = '\0';
		
		StringBuilder name = new StringBuilder();
		ArrayList<String> names = new ArrayList<>();
		ArrayList<Integer> nameIndexes = new ArrayList<>();
		int startIndex = -1;
		
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '#') break;
			
			// Sjekker start / slutt av string
			if ((c == '\'' || c == '"') && (stringDelimiter == '\0' || stringDelimiter == c)) {
				insideString = !insideString;
				stringDelimiter = (insideString) ? c : '\0'; // Setter eller resetter delimiteren
			}
			
			// Variabel kun utenfor string
			if (!insideString) {
				if (name.isEmpty()) startIndex = i;
				
				// selve bokstaven legges til i name-kandidat
				if (isLetterAZ(c) || (isDigit(c) && !name.isEmpty()) || c == '_' ) {
					
					// sjekk om forrige/neste er digit, inkluder for feilmelding senere
					if (i+1 < line.length() && isDigit(c)) {
						char nextC = line.charAt(i + 1);
						if (isDigit(nextC)) name.append(nextC);
					}
					if (i-1 > 0) {
						char forrigeC = line.charAt(i-1);
						if (isDigit(forrigeC)) name.append(forrigeC);
					}
					name.append(c);
				}
				
				else if (!name.isEmpty() && name.chars().anyMatch(Character::isLetter)) {
					if (validateName(name.toString())) {
						names.add(name.toString());
						name = new StringBuilder();
						nameIndexes.add(startIndex);
					}
				}
			}
		}
		// legger til name i tilfeller hvor det er slutten av linjen
		if (!name.isEmpty() && name.chars().anyMatch(Character::isLetter)) {
			if (validateName(name.toString())) {
				names.add(name.toString());
				nameIndexes.add(startIndex);
			}
		}
		
		// lager tokens for alle navn
		for (int j = 0; j < names.size(); j++) {
			int lineIndex = nameIndexes.get(j);
			String lineName = names.get(j);
			
			// om ikke et reservert ord fra enumklasse
			if (findToken(lineName) == null) {
				Token nameToken = new Token(TokenKind.nameToken, curLineNum());
				nameToken.name = lineName;
				nameToken.lineIndex = lineIndex;
				nameToken.endIndex = nameIndexes.get(j) + names.get(j).length()-1;
				curLineTokens.add(nameToken);
			}
			// er et reservert ord - aka keyword fra TokenKind
			else {
				Token keywordToken = new Token(findToken(lineName), curLineNum());
				keywordToken.name = lineName;
				keywordToken.lineIndex = lineIndex;
				keywordToken.endIndex = nameIndexes.get(j) + names.get(j).length()-1;
				curLineTokens.add(keywordToken);
			}
		}
	}
	
	
	/**
	 * Identifiserer og lager tokens for operatorer og delimiters
	 *
	 * @param: String line : linje som undersøkes
	 */
	private void operatorDelimiterFind(String line) {
		StringBuilder operatorBuilder = new StringBuilder();
		boolean insideString = false;
		char stringDelimiter = '\0';

		// Gyldige operatorer
		List<String> validSingleOperators = Arrays.asList("*", ">", "<", "-", "%", "+", "/");

		// Gyldige delimiters
		List<String> validDelimiters = Arrays.asList(":", ",", "=", "{", "[", "(", "}", "]", ")", ";");

		// gyldige dobbeltsymboler
		List<String> validDoubleOperators = Arrays.asList("==", "//", ">=", "<=", "!=");

		// _Alle_ gyldige symboler
		List<String> validSymbols = new ArrayList<>(validSingleOperators);
		validSymbols.addAll(validDelimiters);
		validSymbols.addAll(validDoubleOperators);
		
		// itererer gjennom hver bokstav i linjen for å lage et ord som skal sjekkes
		for (int i = 0; i < line.length(); i++) {
			boolean validDouble = false;
			char c = line.charAt(i);
			if (c == '#') break;

			// Sjekker start / slutt av string
			if ((c == '\'' || c == '"') && (stringDelimiter == '\0' || stringDelimiter == c)) {
				insideString = !insideString;
				stringDelimiter = (insideString) ? c : '\0'; // Setter eller resetter delimiteren
			}
			
			// sjekker om utenfor string
			if (!insideString) {

				// sjekker nå-symbol (=, <, >, ...)
				if (validSymbols.contains(String.valueOf(c)) || c=='!') {
					operatorBuilder.append(c);

					// sjekker neste-symbol (==, <=, !=, ...)
					if (i+1 < line.length()) {
						String next = String.valueOf(line.charAt(i+1));
						String doubleSymbol = operatorBuilder + next;
						if (validDoubleOperators.contains(doubleSymbol)) {
							operatorBuilder.append(next);
							validDouble = true;
							i++;
						}
					}
					// lager token  (kun om det har vært et gyldig symbol)
					if (validSymbols.contains(operatorBuilder.toString())) {
						String lineOperator = operatorBuilder.toString();
						Token operatorToken = new Token(findToken(lineOperator), curLineNum());
						operatorToken.lineIndex = (validDouble) ? i-1 : i;
						operatorToken.endIndex = (validDouble) ? (i-1)+operatorBuilder.length()-1 : i+operatorBuilder.length()-1;
						curLineTokens.add(operatorToken);
						operatorBuilder = new StringBuilder();
					} else scannerError("Invalid character " + operatorBuilder);
				}
			}
		}
	}
	
	
	/**
	 * Identifiserer og lager tokens for integers og floats
	 *
	 * @param: String line : linje som undersøkes
	 */
	private void numberFind(String line) {
		boolean insideString = false;
		char stringDelimiter = '\0';
		int numberStartIndex = -1;
		
		StringBuilder number = new StringBuilder(); // spesifikk nummer
		List<String> numbers = new ArrayList<>(); // alle numre
		List<Integer> numberIndex = new ArrayList<>(); // alle numre indekser
		
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '#') break;
			
			// Sjekker start / slutt av string
			if ((c == '\'' || c == '"') && (stringDelimiter == '\0' || stringDelimiter == c)) {
				insideString = !insideString;
				stringDelimiter = (insideString) ? c : '\0'; // Setter eller resetter delimiteren
			}
			
			// for å sjekke om del av en variabel/name
			boolean forrigeValid = true;
			boolean nesteValid = true;
			boolean valid = false;
			
			// sjekker forrige - om bokstav eller _ (lovlig variabel-start)
			if (isDigit(c) && (i - 1 >= 0)) {
				if (isLetterAZ(line.charAt(i - 1)) || c == '_')
					forrigeValid = false;
			}
			// sjekker neste - om bokstav
			if (isDigit(c) && (i + 1 < line.length())) {
				if (isLetterAZ(line.charAt(i+1)))
					nesteValid = false;
			}
			
			// lagrer 0 som egen verdi hvis den oppstår før number, eksludert komma-tilfeller
			if ((((i-1>0 && (!isDigit(line.charAt(i-1)) && line.charAt(i-1) != '.')) &&
						  (i+1<line.length() && line.charAt(i+1) != '.')) || i==0)) {
				if (c == '0' && !insideString) {
					numbers.add(String.valueOf(c));
					numberIndex.add(i);
					continue;
				}
			}
			
			// godkjent om forrige/neste er del av variabel
			if (forrigeValid && nesteValid) valid = true;
			
			// Sjekk om nummer er utenfor string + nødvendige krav
			if (!insideString && valid && (isDigit(c) || c == '.')) {
				if (number.isEmpty()) numberStartIndex = i;
				number.append(c);
			}
			else if (!number.isEmpty()) {
				numbers.add(number.toString());
				numberIndex.add(numberStartIndex);
				number = new StringBuilder();
			}
		}
		// om nummer på slutten av linjen
		if (!number.isEmpty()) {
			numbers.add(number.toString());
			numberIndex.add(numberStartIndex);
		}
		
		// Oppretter tokens
		for (int i = 0; i < numbers.size(); i++) {
			String num = numbers.get(i);
			// CASE : float
			if (num.contains(".")) {
				try {
					if (!num.endsWith(".")) {
						if (!num.startsWith(".")) {
							double floatNum = Double.parseDouble(num);
							Token floatToken = new Token(TokenKind.floatToken, curLineNum());
							floatToken.floatLit = floatNum;
							floatToken.lineIndex = numberIndex.get(i);
							floatToken.endIndex = numberIndex.get(i) + num.length()-1;
							curLineTokens.add(floatToken);
						} else {
							scannerError("Decimal " + num + " started illegally");
						}
					} else {
						scannerError("Decimal " + num + " ended illegally");
					}
				} catch (NumberFormatException e) {
					System.err.println("Error parsing float: " + num);
				}
			}
			// Ellers : Integer
			else {
				try {
					int intNum = Integer.parseInt(num);
					Token integerToken = new Token(TokenKind.integerToken, curLineNum());
					integerToken.integerLit = intNum;
					integerToken.lineIndex = numberIndex.get(i);
					integerToken.endIndex = numberIndex.get(i) + num.length()-1;
					curLineTokens.add(integerToken);
				} catch (NumberFormatException e) {
					System.err.println("Error parsing integer: " + num);
				}
			}
		}
	}
	
	
	/**
	 * Hjelpemetode : validerer name ved å sjekke ulovlig start
	 *
	 * @param: String name : kandidat til name-token
	 */
	private boolean validateName(String name) {
		try {Double.parseDouble(String.valueOf(name)); return false;
		} catch (Exception e) {
			if (!name.isEmpty() && (isDigit(name.charAt(0)) || !isLetterAZ(name.charAt(0)))) {
				scannerError("Illegal character '" + name.charAt(0) + "' in " + name);
			}
			return true;
		}
	}
	/**
	 * Hjelpemetode : blar gjennom enum og matcher riktig symbol med token
	 *
	 * @param: String ord : et symbol e.g. "=="
	 * @return: TokenKind : tokenkind av symbolet via enum
	 */
	private TokenKind findToken(String ord) {
		for (TokenKind enumToken : TokenKind.values()) {
			if (ord.equals(enumToken.toString()))
				return enumToken;
		}
		return null;
	}
	// -------------------------------------------------------------------------------
	
	
	/**
	 * Leser neste linje, deler opp i symboler, legger til symboler i 'curLineTokens'
	 */
	private void readNextLine() {
		curLineTokens.clear();
		
		boolean eof = false; // holder styr på end-of-line
		
		// Leser den neste linjen
		String line = null;
		try {
			line = sourceFile.readLine();
			
			// case: Tom fil - legger til End-of-File | etter siste linje
			if (line == null) {
				sourceFile.close();
				sourceFile = null;
				eof = true;
				
				// For alle verdier på indents som er > 0, legges ‘DEDENT’-token i curLineTokens
				for (Integer i : indents)
					if (i > 0) curLineTokens.add(new Token(TokenKind.dedentToken, curLineNum()));
				
				// etter siste linje -> legger til end-of-line på slutten
				curLineTokens.add(new Token(TokenKind.eofToken, curLineNum()));
			} else Main.log.noteSourceLine(curLineNum(), line);
		}
		// case: input/output error
		catch (IOException e) {
			sourceFile = null;
			scannerError("Unspecified I/O error!");
		}
		
		// Hvis line finnes og ikke er på slutten (eof)
		if (!eof && line != null) {
			
			// 1) Innledende TAB-er oversettes til blanke (figur 3.7)
			line = expandLeadingTabs(line);
			
			// 2) Hvis linjen er tom, ignoreres den
			if (!line.trim().isEmpty()) {
				
				// 3) Hvis linjen er en kommentar, ignoreres den
				if (!line.trim().startsWith("#")) {
					
					// 4) Indentering beregnes, og INDENT/DEDENT-er legges i curLineTokens
					int antBlanke = findIndent(line);
					indexHandling(antBlanke);

					// finner og lager tokens basert på iterasjon
					nameFind(line);            		// name
					stringFind(line);				// string
					numberFind(line);               // int og float
					operatorDelimiterFind(line);    // operators og delimiters

					// sorterer rekkefølgen slik at tokens forekommer riktig rekkefølge
					curLineTokens.sort(Comparator.comparingInt(token -> token.lineIndex));

					// bytter ut tokens med blank -> sjekker om noe er igjen -> ulovlig character
					validateLine(line);
					
					// Terminate line: (om ikke kommentar eller blank linje)
					curLineTokens.add(new Token(newLineToken, curLineNum()));
				}
			}
		}
		// logger tokens
		for (Token t : curLineTokens)
			Main.log.noteToken(t);
	}
	
	
	/**
	 * Henter nåværende linjenummer
	 *
	 * @return int : linjenr
	 */
	public int curLineNum() {
		return sourceFile != null ? sourceFile.getLineNumber() : 0;
	}
	
	
	/**
	 * Sjekker om det er ulovlige tegn i linjen etter token-scan
	 * Endrer linjen basert på tokens.
	 * Gyldige tokens blir blanke, ugyldige sitter igjen
	 * Det som stter igjen kastes feilmelding for
	 */
	private void validateLine(String line) {
		for (Token t : curLineTokens) {
			StringBuilder newLine = new StringBuilder(line);

			// bytter ut token i linjen med blanke
			if (0>t.lineIndex || line.length()<t.endIndex) continue;
			for (int i = t.lineIndex; i <= t.endIndex; i++)
				newLine.setCharAt(i, ' ');
			line = newLine.toString();
		}

		// fjerner strenger og kommentarer
		line = line.replaceAll("#.*", "");
		for (Character c : line.trim().toCharArray()) {
			if (c!=' ') scannerError("Illegal character " + c);
		}
	}
	
	/**
	 * Teller antall blanke i starten av nåværende linje
	 *
	 * @param s : en linje
	 * @return indent : Antall blanke i starten
	 */
	private int findIndent(String s) {
		int indent = 0;
		
		while (indent < s.length() && s.charAt(indent) == ' ') indent++;
		return indent;
	}
	
	
	/**
	 * Omformer innledende TAB-tegn til riktig antall blanke (figur 3.7)
	 * Denne funksjonen er stort sett fra ukesoppgavene andre uke, med små endringer.
	 *
	 * @param s : en linje
	 * @return newS.toString() : ny linje uten tabs
	 */
	private String expandLeadingTabs(String s) {
		StringBuilder newS = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\t') {
				do {
					newS.append(" ");
				} while (newS.length() % TABDIST > 0);
			} else if (c == ' ') {
				newS.append(" ");
			} else {
				newS.append(s.substring(i));
				break;
			}
		}
		return newS.toString();
	}
	
	
	/**
	 * Håndterer indeksering av en linje ved å legge til indent/dedent basert på linjen
	 *
	 * @param n : antall innledende blanke tegn
	 */
	private void indexHandling(int n) {
		
		// mer indentert enn forrige
		if (n > indents.peek()) {
			indents.push(n);
			
			// legger INDENT-symbol i curLineTokens
			Token indent = new Token(indentToken, curLineNum());
			indent.lineIndex = -1;
			curLineTokens.add(indent);
		}
		// mindre indentert enn forrige
		int dedentIndex = 0;
		while (n < indents.peek()) {
			indents.pop();
			
			// legger DEDENT-symbol i curLineTokens  (regnes som minus-indeks for rekkefølge)
			Token dedent = new Token(dedentToken, curLineNum());
			dedent.lineIndex = dedentIndex--;
			curLineTokens.add(dedent);
		}
		
		if (n != indents.peek()) scannerError("Indentation error!");
	}
	
	
	/**
	 * Sjekker om et tegn er en bokstav
	 *
	 * @param c : et tegn
	 * @return true / false : om tegn er bokstav
	 */
	private boolean isLetterAZ(char c) {
		return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || (c == '_');
	}
	
	
	/**
	 * Sjekker om et tegn er et siffer
	 *
	 * @param c : et tegn
	 * @return true / false : om tegn er siffer
	 */
	private boolean isDigit(char c) {
		return '0' <= c && c <= '9';
	}
	
	
	/**
	 * Sjekker token er en sammenlignings-operator (< > == >= <= !=</>)
	 *
	 * @return true / false
	 */
	public boolean isCompOpr() {
		return switch (curToken().kind) {
			case lessToken, greaterToken, doubleEqualToken, greaterEqualToken, lessEqualToken, notEqualToken -> true;
			default -> false;
		};
	}
	
	
	/**
	 * Sjekker token er en faktor-prefiks (+ -)
	 *
	 * @return true / false
	 */
	public boolean isFactorPrefix() {
		return switch (curToken().kind) {
			case plusToken, minusToken -> true;
			default -> false;
		};
	}
	
	
	/**
	 * Sjekker token er en faktor-operator (* / % //)
	 *
	 * @return true / false
	 */
	public boolean isFactorOpr() {
		return switch (curToken().kind) {
			case astToken, slashToken, percentToken, doubleSlashToken -> true;
			default -> false;
		};
	}
	
	
	/**
	 * Sjekker token er en term-operator (+ -)
	 *
	 * @return true / false
	 */
	public boolean isTermOpr() {
		return isFactorPrefix();
	}
	
	
	/**
	 * Sjekker om symbolene på nåværende linje inneholder = symbol
	 *
	 * @return true / false
	 */
	// bruk for å finne *assignment stmt*
	public boolean anyEqualToken() {
		for (Token t : curLineTokens) {
			if (t.kind == equalToken) return true;
			if (t.kind == semicolonToken) return false;
		}
		return false;
	}
	
	
	/**
	 * Sjekker om token er i compound stmt
	 *
	 * @return true / false
	 */
	public boolean isCompoundStmt() {
		return switch (curToken().kind) {
			case forToken, ifToken, whileToken, defToken -> true;
			default -> false;
		};
	}
}