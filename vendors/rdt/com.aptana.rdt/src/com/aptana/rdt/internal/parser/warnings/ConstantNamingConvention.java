package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.ConstDeclNode;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.IProblem;

public class ConstantNamingConvention extends RubyLintVisitor {

	public ConstantNamingConvention(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_CONSTANT_NAMING_CONVENTION;
	}
	
	@Override
	public Instruction visitConstDeclNode(ConstDeclNode iVisited) {
		String name = iVisited.getName();
		if (!name.toUpperCase().equals(name)) {
			ISourcePosition pos = iVisited.getPosition();
			IDESourcePosition duh = new IDESourcePosition("", pos.getStartLine(), pos.getEndLine(), pos.getStartOffset(), pos.getStartOffset() + name.length());
			createProblem(duh, "Constant name doesn't match ALL_CAPS_WITH_UNDERSCORES convention: " + name);
		}
		return super.visitConstDeclNode(iVisited);
	}
	
	@Override
	protected int getProblemID() {
		return IProblem.ConstantNamingConvention;
	}

}
