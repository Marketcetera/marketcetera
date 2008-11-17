/**
 * 
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.IDESourcePosition;
import org.rubypeople.rdt.core.compiler.IProblem;

/**
 * @author Chris
 * 
 */
public class TaskTag extends DefaultProblem implements IProblem {

	private int priority;

	public TaskTag(String message, int priority, int lineNumber, int start, int end) {
		super(new IDESourcePosition("", lineNumber, lineNumber, start, end), message, IProblem.Task);
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public boolean isTask() {
		return true;
	}

}
