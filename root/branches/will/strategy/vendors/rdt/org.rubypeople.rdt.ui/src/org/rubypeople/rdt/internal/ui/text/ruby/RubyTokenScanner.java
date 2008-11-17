package org.rubypeople.rdt.internal.ui.text.ruby;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jruby.common.NullWarnings;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.RubyYaccLexer;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.lexer.yacc.RubyYaccLexer.LexState;
import org.jruby.parser.ParserConfiguration;
import org.jruby.parser.ParserSupport;
import org.jruby.parser.RubyParserResult;
import org.jruby.parser.Tokens;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * A token scanner which returns integers for ruby tokens. These can later be mapped to colors.
 * Does some smoothing on the tokens to add additional token types that the JRuby parser ignores.
 * 
 * @author Chris Williams
 *
 */
public class RubyTokenScanner implements ITokenScanner {

	private static final int COMMA = 44;
	private static final int COLON = 58;
	private static final int NEWLINE = 10;
	public static final int CHARACTER = 128;
	static final int MIN_KEYWORD = 257;
	static final int MAX_KEYWORD = 303;

	private RubyYaccLexer lexer;
	private LexerSource lexerSource;
	private ParserSupport parserSupport;
	
	private int fTokenLength;
	private int fOffset;
	
	private boolean isInSymbol;
	private boolean inAlias;
	private RubyParserResult result;
	private int origOffset;
	private int origLength;
	private String fContents;	

	public RubyTokenScanner() {
		lexer = new RubyYaccLexer();
		parserSupport = new ParserSupport();
		parserSupport.setConfiguration(new ParserConfiguration(0, true, false));
		result = new RubyParserResult();
		parserSupport.setResult(result);
		lexer.setParserSupport(parserSupport);
		lexer.setWarnings(new NullWarnings());
	}

	public int getTokenLength() {
		return fTokenLength;
	}

	public int getTokenOffset() {
		return fOffset;
	}

	public IToken nextToken() {
		fOffset = getOffset();
		fTokenLength = 0;
		IToken returnValue = new Token(Tokens.tIDENTIFIER);
		boolean isEOF = false;
		try {
			isEOF = !lexer.advance(); // FIXME if we're assigning a string to a variable we may get a NumberFormatException here!
			if (isEOF) {
				returnValue = Token.EOF;
			} else {
				fTokenLength = getOffset() - fOffset;
				returnValue = token(lexer.token());
			}
		} catch (SyntaxException se) {
			if (lexerSource.getOffset() - origLength == 0)
				return Token.EOF; // return eof if we hit a problem found at
									// end of parsing			
			fTokenLength = getOffset() - fOffset;
			return token(Tokens.yyErrorCode); // FIXME This should return a special error token!
		} catch (NumberFormatException nfe) {
			fTokenLength = getOffset() - fOffset;
			return returnValue;
		} catch (IOException e) {
			RubyPlugin.log(e);
		}
		
		return returnValue;
	}

	private int getOffset() {
		return lexerSource.getOffset() + origOffset;
	}

	private IToken token(int i) {		
		
		if (isInSymbol) {
			if (isSymbolTerminator(i)) {
				isInSymbol = false; // we're at the end of the symbol
				if (shouldReturnDefault(i))
					return new Token(new Integer(i));				
			}
			return new Token(new Integer(Tokens.tSYMBEG));
		}
		// The next two conditionals work around a JRuby parsing bug
		// JRuby returns the number for ':' on second symbol's beginning in alias calls
		if (i == Tokens.kALIAS) {
			inAlias = true;
		}
		if (i == COLON && inAlias) {
			isInSymbol = true;
			inAlias = false;
			return new Token(new Integer(Tokens.tSYMBEG));
		} // end JRuby parsing hack for alias
		if (isKeyword(i))
			return new Token(new Integer(Tokens.k__FILE__)); // FIXME Set up a token for user defined keywords
		switch (i) {
		case Tokens.tSYMBEG:
			if (looksLikeTertiaryConditionalWithNoSpaces()) {
				return new Token(new Integer(i));
			}
			isInSymbol = true;
			 // FIXME Set up a token for symbols
			return new Token(new Integer(Tokens.tSYMBEG));
		case Tokens.tGVAR:
		case Tokens.tBACK_REF:
			return new Token(new Integer(Tokens.tGVAR));
		case Tokens.tFLOAT:
		case Tokens.tINTEGER:
			// A character is marked as an integer, lets check for that special case...
			if ((((fOffset - origOffset) + 1) < fContents.length()) && (fContents.charAt((fOffset - origOffset) + 1) == '?'))
				return new Token(new Integer(CHARACTER));
			return new Token(new Integer(i));
		default:
			return new Token(new Integer(i));
		}
	}

	private boolean looksLikeTertiaryConditionalWithNoSpaces() {
		if (fTokenLength > 1) return false;
		int index = (fOffset - origOffset) - 1;
		if (index < 0) return false;
		try {
			char c = fContents.charAt(index);
			return !Character.isWhitespace(c) && Character.isUnicodeIdentifierPart(c);
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean shouldReturnDefault(int i) {
		switch (i) {
		case NEWLINE:
		case COMMA:
		case Tokens.tASSOC:
		case Tokens.tRPAREN:
			return true;
		default:
			return false;
		}
	}

	private boolean isSymbolTerminator(int i) {
		if (isRealKeyword(i)) return true;
		switch (i) {
		case Tokens.tAREF:
		case Tokens.tCVAR:
		case Tokens.tMINUS:
		case Tokens.tPLUS:
		case Tokens.tPIPE:
		case Tokens.tCARET:
		case Tokens.tLT:
		case Tokens.tGT:
		case Tokens.tAMPER:
		case Tokens.tSTAR2:
		case Tokens.tDIVIDE:
		case Tokens.tPERCENT:
		case Tokens.tBACK_REF2:
		case Tokens.tTILDE:
		case Tokens.tCONSTANT: 
		case Tokens.tFID:
		case Tokens.tASET:
		case Tokens.tIDENTIFIER: 
		case Tokens.tIVAR:
		case Tokens.tGVAR:
		case Tokens.tASSOC:
		case Tokens.tLSHFT:
		case Tokens.tRPAREN:
		case COMMA:
		case NEWLINE:
			return true;
		default:
			return false;
		}
	}

	private boolean isRealKeyword(int i) {
		if (i >= MIN_KEYWORD && i <= MAX_KEYWORD) return true;
		return false;
	}

	private boolean isKeyword(int i) {
		if (i != Tokens.tIDENTIFIER) return false;
		String src;
		try {
			src = fContents.substring((fOffset - origOffset), (fOffset - origOffset) + fTokenLength);
		} catch (RuntimeException e) {
			RubyPlugin.log(e);
			return false;
		}
		if (src == null || src.trim().length() == 0) return false;
		Preferences prefs = RubyPlugin.getDefault().getPluginPreferences();
		if (prefs == null) return false;
		String rawKeywords = prefs.getString(PreferenceConstants.EDITOR_USER_KEYWORDS);
		if (rawKeywords == null || rawKeywords.length() == 0) {
			return false;
		}
		String[] keywords = rawKeywords.split(",");
		if (keywords == null || keywords.length == 0) {
			return false;
		}
		for (int j = 0; j < keywords.length; j++) {
			if (keywords[j] == null) continue;
			if (keywords[j].equals(src.trim())) return true;
		}
		return false;
	}

	public void setRange(IDocument document, int offset, int length) {
		lexer.reset();
		lexer.setState(LexState.EXPR_BEG);
		parserSupport.initTopLocalVariables();
		isInSymbol = false;
		ParserConfiguration config = new ParserConfiguration(0, true, false);
		try {
			fContents = document.get(offset, length);			
			lexerSource = LexerSource.getSource("filename", new StringReader(fContents), null, config);
			lexer.setSource(lexerSource);
		} catch (BadLocationException e) {
			lexerSource = LexerSource.getSource("filename", new StringReader(""), null, config);
			lexer.setSource(lexerSource);
		}
		origOffset = offset;
		origLength = length;
	}
}
