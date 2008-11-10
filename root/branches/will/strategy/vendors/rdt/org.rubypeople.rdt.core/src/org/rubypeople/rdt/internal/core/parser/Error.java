/**
 * 
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.ISourcePosition;


/**
 * @author Chris
 *
 */
public class Error extends DefaultProblem {

	public Error(ISourcePosition position, String message) {
		this(position, message, -1);
	}
	
	public Error(ISourcePosition position, String message, int problemID) {
		super(position, message, problemID);
	}

	public boolean isError() {
		return true;
	}
}
