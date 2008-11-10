package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.DefnNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class MisspelledConstructorVisitor extends RubyLintVisitor {

	public static final int PROBLEM_ID = 1234567;

	public MisspelledConstructorVisitor(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}

	public Instruction visitDefnNode(DefnNode iVisited) {
		String methodName = iVisited.getName();
		if (methodName.equals("intialize") || methodName.equals("initialise") || methodName.equals("initalize")) {
			createProblem(iVisited.getNameNode().getPosition(), "Possible mis-spelling of constructor");
		}
		return null;
	}
	
	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_MISSPELLED_CONSTRUCTOR;
	}

	@Override
	protected int getProblemID() {
		return PROBLEM_ID;
	}
}
