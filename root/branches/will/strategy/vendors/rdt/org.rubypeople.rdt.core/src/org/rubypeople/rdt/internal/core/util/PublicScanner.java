package org.rubypeople.rdt.internal.core.util;

import java.io.IOException;
import java.io.StringReader;

import org.jruby.common.NullWarnings;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.RubyYaccLexer;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.lexer.yacc.RubyYaccLexer.LexState;
import org.jruby.parser.ParserConfiguration;
import org.jruby.parser.ParserSupport;
import org.jruby.parser.RubyParserResult;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.IScanner;
import org.rubypeople.rdt.core.compiler.InvalidInputException;

public class PublicScanner implements IScanner {

	private char[] source;
	private RubyYaccLexer lexer;
	private ParserSupport parserSupport;
	private RubyParserResult result;
	private LexerSource lexerSource;
	private int fOffset;
	private int fTokenLength;

	public PublicScanner() {
		lexer = new RubyYaccLexer();
		parserSupport = new ParserSupport();
		parserSupport.setConfiguration(new ParserConfiguration(0, true, false));
		result = new RubyParserResult();
		parserSupport.setResult(result);
		lexer.setParserSupport(parserSupport);
		lexer.setWarnings(new NullWarnings());
	}
	
	public int getCurrentTokenEndPosition() {
		return fOffset + fTokenLength;
	}

	public int getCurrentTokenStartPosition() {
		return fTokenLength;
	}

	public int getNextToken() throws InvalidInputException {
		fOffset = lexerSource.getOffset();
		fTokenLength = 0;
		int returnValue = 0;
		boolean isEOF = false;
		try {
			isEOF = !lexer.advance();
			if (isEOF) {
				returnValue = TokenNameEOF;
			} else {
				fTokenLength = lexerSource.getOffset() - fOffset;
				returnValue = lexer.token();
			}
		} catch (SyntaxException se) {
			if (lexerSource.getOffset() == 0)
				return TokenNameEOF; // return eof if we hit a problem found at
									// end of parsing			
			fTokenLength = lexerSource.getOffset() - fOffset;
			return 0;
		} catch (NumberFormatException nfe) {
			fTokenLength = lexerSource.getOffset() - fOffset;
			return returnValue;
		} catch (IOException e) {
			RubyCore.log(e);
		}		
		return returnValue;
	}

	public void setSource(char[] source) {
		this.source = source;
		
		lexer.reset();
		lexer.setState(LexState.EXPR_BEG);
		parserSupport.initTopLocalVariables();
		ParserConfiguration config = new ParserConfiguration(0, true, false);
		lexerSource = LexerSource.getSource("filename", new StringReader(new String(source)), null, config);
		lexer.setSource(lexerSource);		
	}

}
