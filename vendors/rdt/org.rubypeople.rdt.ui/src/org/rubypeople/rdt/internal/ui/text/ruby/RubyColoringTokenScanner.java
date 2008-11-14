package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jruby.parser.Tokens;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.ui.text.IColorManager;

/**
 * Wraps the RubyTokenScanner, and converts it's integer tokens into tokens containing coloring ifnromation for the ruby editor.
 * 
 * @author Chris Williams
 *
 */
public class RubyColoringTokenScanner extends AbstractRubyTokenScanner {
	
	private static String[] fgTokenProperties = { IRubyColorConstants.RUBY_KEYWORD, IRubyColorConstants.RUBY_DEFAULT, 
		IRubyColorConstants.RUBY_FIXNUM, IRubyColorConstants.RUBY_CHARACTER, IRubyColorConstants.RUBY_SYMBOL, 
		IRubyColorConstants.RUBY_INSTANCE_VARIABLE, IRubyColorConstants.RUBY_GLOBAL, 
		IRubyColorConstants.RUBY_ERROR
	// TODO Add Ability to set colors for return and operators
	// IRubyColorConstants.RUBY_METHOD_NAME,
	// IRubyColorConstants.RUBY_KEYWORD_RETURN,
	// IRubyColorConstants.RUBY_OPERATOR
	};
	
	private ITokenScanner fScanner;

	public RubyColoringTokenScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		fScanner = new RubyTokenScanner();
		initialize();
	}

	public int getTokenLength() {
		return fScanner.getTokenLength();
	}

	public int getTokenOffset() {
		return fScanner.getTokenOffset();
	}

	public IToken nextToken() {
		IToken intToken = fScanner.nextToken();		
		if (intToken == null || intToken.isEOF()) return Token.EOF;
		Integer data = (Integer)intToken.getData();
		if (data == null) return Token.EOF;
		// Convert the integer tokens into tokens containing color information!
		if (isKeyword(data.intValue())) {
			return getToken(IRubyColorConstants.RUBY_KEYWORD);
		}
		switch (data.intValue()) {
		case RubyTokenScanner.CHARACTER:
			return getToken(IRubyColorConstants.RUBY_CHARACTER);
		case Tokens.tFLOAT:
		case Tokens.tINTEGER:
			return getToken(IRubyColorConstants.RUBY_FIXNUM);
		case Tokens.tSYMBEG:
			return getToken(IRubyColorConstants.RUBY_SYMBOL);
		case Tokens.tGVAR:
			return getToken(IRubyColorConstants.RUBY_GLOBAL);
		case Tokens.tIVAR:
			return getToken(IRubyColorConstants.RUBY_INSTANCE_VARIABLE);
		case Tokens.yyErrorCode:
			return getToken(IRubyColorConstants.RUBY_ERROR);
		default:
			return getToken(IRubyColorConstants.RUBY_DEFAULT);
		}
	}
	
	private boolean isKeyword(int i) {
		if (i >= RubyTokenScanner.MIN_KEYWORD && i <= RubyTokenScanner.MAX_KEYWORD) return true;
		return false;
	}

	public void setRange(IDocument document, int offset, int length) {
		fScanner.setRange(document, offset, length);		
	}
	
	/*
	 * @see AbstractRubyScanner#getTokenProperties()
	 */
	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}
}
