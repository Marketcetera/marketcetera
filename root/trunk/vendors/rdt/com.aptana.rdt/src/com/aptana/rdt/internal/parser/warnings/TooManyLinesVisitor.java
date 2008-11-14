package com.aptana.rdt.internal.parser.warnings;

import java.util.Map;

import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class TooManyLinesVisitor extends RubyLintVisitor {

	public static final int DEFAULT_MAX_LINES = 20;
	
	private int maxLines;

	public TooManyLinesVisitor(String contents) {
		this(AptanaRDTPlugin.getDefault().getOptions(), contents);		
	}
	
	public TooManyLinesVisitor(Map options, String contents) {
		super(options, contents);
		maxLines = getInt(AptanaRDTPlugin.COMPILER_PB_MAX_LINES, DEFAULT_MAX_LINES); 
	}
	private int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt((String) fOptions.get(key));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_LINES;
	}

	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		ISourcePosition pos = iVisited.getPosition();
		int lines = (pos.getEndLine() - pos.getStartLine()) - 1;
		if (lines > maxLines) {
			createProblem(iVisited.getNameNode().getPosition(), "Too many lines in method: " + lines);
		}
		return super.visitDefsNode(iVisited);
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		ISourcePosition pos = iVisited.getPosition();
		int lines = (pos.getEndLine() - pos.getStartLine()) - 1;
		if (lines > maxLines) {
			createProblem(iVisited.getNameNode().getPosition(), "Too many lines in method: " + lines);
		}
		return super.visitDefnNode(iVisited);
	}
}
