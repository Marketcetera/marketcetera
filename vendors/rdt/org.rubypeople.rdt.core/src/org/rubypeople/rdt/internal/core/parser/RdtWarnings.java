/*
 * Created on Feb 20, 2005
 */
package org.rubypeople.rdt.internal.core.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jruby.common.IRubyWarnings;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * @author Chris
 */
public class RdtWarnings implements IRubyWarnings {

    private List<CategorizedProblem> warnings;
	private String fileName;
    
	public RdtWarnings(String fileName) {
		this.fileName = fileName;
		warnings = new ArrayList<CategorizedProblem>();
	}

	public List<CategorizedProblem> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

    public void warn(ID id, ISourcePosition position, String message, Object... data) {        
        if (Util.ignore(message)) {
			return;
		}
		if (message.equals("Statement not reached.")) { // TODO Categorize problems that JRuby provides in one place
			String value = RubyCore.getOption(RubyCore.COMPILER_PB_UNREACHABLE_CODE);
			if (value == null || value.equals(RubyCore.WARNING)) {
				warnings.add(new Warning(position, message));
			}
			if (value != null && value.equals(RubyCore.ERROR)) {
				warnings.add(new Error(position, message));
			}
			return;
		} else if (message.equals("parenthesize argument(s) for future version")) {
			ISourcePosition pos = new IDESourcePosition(position.getFile(), position.getStartLine(), position.getEndLine(), position.getStartOffset(), position.getEndOffset() - 2);
			warnings.add(new Warning(pos, message, IProblem.ParenthesizeArguments));
			return;			
		}
		warnings.add(new Warning(position, message));
    }

    public void warn(ID id, String fileName, int lineNumber, String message, Object... data) {
    	warn(id, new IDESourcePosition(fileName, lineNumber, lineNumber), message, data);
    }

    public boolean isVerbose() {
        return true;
    }

    public void warn(ID id, String message, Object... data) {
        warn(id, fileName, 1, message, data);
    }

    public void warning(ID id, String message, Object... data) {
        warning(id, fileName, 1, message, data);
    }
    
    public void warning(ID id, ISourcePosition position, String message, Object... data) {
        warning(id, position.getFile(), position.getEndLine(), message, data);
    }

    public void warning(ID id, String fileName, int lineNumber, String message, Object... data) {
        if (isVerbose()) warn(id, fileName, lineNumber, message, data);
    }
}
