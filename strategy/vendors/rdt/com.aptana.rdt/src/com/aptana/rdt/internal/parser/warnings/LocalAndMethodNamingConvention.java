package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.IProblem;

public class LocalAndMethodNamingConvention extends RubyLintVisitor {

	public LocalAndMethodNamingConvention(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_LOCAL_METHOD_NAMING_CONVENTION;
	}

	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		String name = iVisited.getName();
		if (!name.toLowerCase().equals(name)) {
			createProblem(iVisited.getPosition(), "Method name doesn't match the under_scores_all_lower convention: " + name);
		}
		return super.visitDefnNode(iVisited);
	}
	
	@Override
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
		String name = iVisited.getName();
		if (!name.toLowerCase().equals(name)) {
			createProblem(iVisited.getPosition(), "Method name doesn't match the under_scores_all_lower convention: " + name);
		}
		return super.visitLocalAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		String name = iVisited.getName();
		if (!name.toLowerCase().equals(name)) {
			createProblem(iVisited.getPosition(), "Local variable name doesn't match the under_scores_all_lower convention: " + name);
		}
		return super.visitDefsNode(iVisited);
	}
	
	@Override
	protected int getProblemID() {
		return IProblem.LocalAndMethodNamingConvention;
	}
	
}
