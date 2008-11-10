package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.RescueBodyNode;
import org.jruby.ast.RetryNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.IProblem;

public class RetryOutsideRescueBodyChecker extends RubyLintVisitor {

	boolean insideRescue = false;
	
	public RetryOutsideRescueBodyChecker(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}
	
	@Override
	public Instruction visitRetryNode(RetryNode iVisited) {
		if (!insideRescue) {
			createProblem(iVisited.getPosition(), "'retry' will not be allowed outside a rescue block in Ruby 1.9");
		}
		return super.visitRetryNode(iVisited);
	}
	
	@Override
	public Instruction visitRescueBodyNode(RescueBodyNode iVisited) {
		insideRescue = true;
		return super.visitRescueBodyNode(iVisited);
	}

	@Override
	public void exitRescueBodyNode(RescueBodyNode iVisited) {
		insideRescue = false;
		super.exitRescueBodyNode(iVisited);
	}
	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_RETRY_OUTSIDE_RESCUE;
	}
	
	@Override
	protected int getProblemID() {
		return IProblem.RetryOutsideRescueBody;
	}

}
