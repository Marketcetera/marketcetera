package org.rubypeople.rdt.internal.ui.text;

import java.io.EOFException;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

public class DocumentationCommentRule extends MultiLineRule {

	private final static String endSequence = "=end";
	
	public DocumentationCommentRule(IToken token) {
		super("=begin", "", token);
		setColumnConstraint(0);
	}

	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		if(scanner.getColumn() != 0)
			return false;
		
		String line = "";
		do {
			try {
				line = readLine(scanner);
			} catch (EOFException e) {
				return true;
			}
		}
		while(! endSequence.equals(line));
		
		return true;
	}
	
	private String readLine(ICharacterScanner scanner) throws EOFException {
		StringBuffer line = new StringBuffer();
		
		while(true) {
			int c = scanner.read();
			if((char) c == '\n' || (char) c == '\r')
				break;
			else if (c == ICharacterScanner.EOF)
				throw new EOFException();
			else
				line.append((char) c);
		}
		
		return line.toString();
	}
}
