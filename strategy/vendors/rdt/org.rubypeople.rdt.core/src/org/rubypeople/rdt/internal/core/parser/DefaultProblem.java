/*
 * Created on Feb 20, 2005
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;

/**
 * @author Chris
 */
abstract class DefaultProblem extends CategorizedProblem {

	private ISourcePosition position;
	private String message;
	private int id;
	private String[] arguments;
	
//	 cannot directly point to IRubyModelMarker constants from within batch compiler
	private static final String MARKER_TYPE_PROBLEM = "org.rubypeople.rdt.core.problem"; //$NON-NLS-1$
	private static final String MARKER_TYPE_TASK = "org.rubypeople.rdt.core.task"; //$NON-NLS-1$

	/**
	 * @param position
	 * @param message
	 * @param problemID 
	 */
	public DefaultProblem(ISourcePosition position, String message, int problemID) {
		this(position, message, problemID, null);
	}
	
	public DefaultProblem(ISourcePosition position, String message, int problemID, String[] args) {
		this.position = position;
		this.message = message;
		this.id = problemID;
		this.arguments = args;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	public char[] getOriginatingFileName() {
		return position.getFile().toCharArray();
	}

	public int getSourceEnd() {
		return position.getEndOffset();
	}

	public int getSourceLineNumber() {
		return position.getStartLine();
	}

	public int getSourceStart() {
		return position.getStartOffset();
	}

	public String toString() {
		return position.toString() + " => " + message;
	}
	
	public int getID() {
		return this.id;
	}
	
	@Override
	public String getMarkerType() {
		return isTask() ? MARKER_TYPE_TASK : MARKER_TYPE_PROBLEM;
	}
	
	public String[] getArguments() {
		return arguments;
	}
}
