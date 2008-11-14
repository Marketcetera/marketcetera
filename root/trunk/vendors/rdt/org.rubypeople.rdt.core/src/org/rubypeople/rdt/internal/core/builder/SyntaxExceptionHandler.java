package org.rubypeople.rdt.internal.core.builder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.internal.core.parser.Error;

public class SyntaxExceptionHandler {

	public static CategorizedProblem handle(SyntaxException e, String contents) {
		String restOfSource = contents.substring(e.getPosition().getStartOffset());
		if (restOfSource != null && e.getMessage().trim().endsWith("but found '=' instead") && restOfSource.startsWith("begin")) {
    		// we have a multiline comment that doesn't start at first character of line.
    		int endIndex = restOfSource.indexOf("=end");
    		if (endIndex == -1) {
    			endIndex = contents.length();
    		} else {
    			endIndex += e.getPosition().getStartOffset() + 3;
    		}
    		ISourcePosition pos = new IDESourcePosition(e.getPosition().getFile(), e.getPosition().getStartLine(), 
    				e.getPosition().getEndLine(), e.getPosition().getStartOffset() - 1, endIndex);
    		CategorizedProblem problem = new Error(pos, "Multine Comment must start at beginning of line", IProblem.MultineCommentNotAtFirstColumn);
    		// TODO Add arguments into problem?
    		return problem;
		} else if (e.getMessage().trim().endsWith("unexpected end-of-file")) {
			// cases like "@donut."
			Pattern p = Pattern.compile("[@+|$|:]?\\w+\\.[;|\\s]");
			Matcher m = p.matcher(contents);
			if (m.find()) {
				int startLine = getLineOfOffset(m.start(), contents);
				int endLine = getLineOfOffset(m.end(), contents);
				ISourcePosition pos = new IDESourcePosition(e.getPosition().getFile(), startLine, endLine, m.start(), m.end());	    		
				return new Error(pos, "Method invocation without method name", IProblem.Syntax);
			}
		}
		return grabPrecedingPrefixForPosition(e, contents);
	}

	private static CategorizedProblem grabPrecedingPrefixForPosition(SyntaxException e, String contents) {
		int offset = e.getPosition().getStartOffset();
		String prefix = getLeadingPrefix(contents, offset);
		ISourcePosition pos = new IDESourcePosition(e.getPosition().getFile(), e.getPosition().getStartLine(), e.getPosition().getEndLine(), offset - prefix.length() - 1, offset - 1);
		return new Error(pos, e.getMessage(), IProblem.Syntax);
	}

	private static int getLineOfOffset(int offset, String contents) {
		String[] lines = contents.split("\\r|\\n|\\r\\n");
		final int lineDelimeterLength = getLineDelimeterLength(contents);
		int start = 0;
		for (int i = 0; i < lines.length; i++) {
			int end = start + lines[i].length();
			if (offset <= end) return i + 1; // line numbers are 1 based, while array is 0 based
			start = end + lineDelimeterLength;
		}
		return 1;
	}

	private static int getLineDelimeterLength(String string) {
		int index = string.indexOf('\n');
		if (index == -1) return 1; // \r
	
		if (index == 0) return 1; // \n as first char
		char c = string.charAt(index - 1);
		if (c == '\r') return 2; // \r\n
		return 1; // \n
	}

	private static String getLeadingPrefix(String contents, int offset) {
		StringBuffer buffer = new StringBuffer();
		for (int i = offset - 1; i >= 0; i--) {
			char c = contents.charAt(i);
			if (Character.isWhitespace(c)) break;
			buffer.insert(0, c);
		}
		return buffer.toString();
	}

}
