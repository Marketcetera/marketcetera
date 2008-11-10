package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.FCallNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.IProblem;

public class RequireGemChecker extends RubyLintVisitor {

	private static final String REQUIRE_GEM = "require_gem";
	private static final String MSG = "'require_gem' is deprecated and removed in RubyGems 1.0. Please use 'gem' instead";
	
	public RequireGemChecker(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_DEPRECATED_REQUIRE_GEM;
	}

	@Override
	public Instruction visitFCallNode(FCallNode iVisited) {
		if (iVisited.getName().equals(REQUIRE_GEM))
			createProblem(iVisited.getPosition(), MSG);
		return super.visitFCallNode(iVisited);
	}
	
	@Override
	protected int getProblemID() {
		return IProblem.DeprecatedRequireGem;
	}
}
