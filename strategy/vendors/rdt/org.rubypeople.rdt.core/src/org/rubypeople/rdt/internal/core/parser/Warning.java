/**
 * 
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.ISourcePosition;


/**
 * @author Chris
 *
 */
public class Warning extends DefaultProblem {

	public Warning(ISourcePosition position, String message) {
		this(position, message, -1);
	}
	
	public Warning(ISourcePosition position, String message, int problemID) {
		super(position, message, problemID);
	}

	public boolean isWarning() {
		return true;
	}
}
