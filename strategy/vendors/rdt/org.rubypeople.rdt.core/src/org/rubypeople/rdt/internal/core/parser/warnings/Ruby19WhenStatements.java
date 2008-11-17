package org.rubypeople.rdt.internal.core.parser.warnings;

import org.jruby.ast.WhenNode;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

public class Ruby19WhenStatements extends RubyLintVisitor {

	public Ruby19WhenStatements(String contents) {
		super(contents);
	}

	@Override
	protected String getOptionKey() {
		return RubyCore.COMPILER_PB_RUBY_19_WHEN_STATEMENTS;
	}
	
	@Override
	public Instruction visitWhenNode(WhenNode iVisited) {
		if (iVisited.getExpressionNodes() == null) return super.visitWhenNode(iVisited);	
		if (iVisited.getBodyNode() == null) return super.visitWhenNode(iVisited);		
		if (iVisited.getPosition() == null) return super.visitWhenNode(iVisited);
		
		int start = iVisited.getPosition().getStartOffset();
		
		ISourcePosition pos = iVisited.getExpressionNodes().getPosition();		
		if (pos == null) return super.visitWhenNode(iVisited);
		
		ISourcePosition bodyPosition = iVisited.getBodyNode().getPosition();		
		if (bodyPosition == null) return super.visitWhenNode(iVisited);
		
		String src = getSource(iVisited);
		src = src.substring(pos.getEndOffset() - start, bodyPosition.getStartOffset() - start);
		
		if (src.trim().equals(":")) {
			int startOffset = pos.getEndOffset() + src.indexOf(":");
			int endOffset = startOffset + 1;
			ISourcePosition position = new IDESourcePosition("", pos.getEndLine(), pos.getEndLine(), startOffset, endOffset);
			createProblem(position, "':' not supported for when statements in Ruby 1.9. Please use 'then'.");
		}
		return super.visitWhenNode(iVisited);
	}

	@Override
	protected String getSeverity() {
		// TODO Return error on 1.9!
		return super.getSeverity();
	}
	
	@Override
	protected int getProblemID() {
		return IProblem.ColonAfterWhenStatement;
	}
	
}
