package org.rubypeople.rdt.internal.core.parser.warnings;

import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.WhenNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

public class EmptyStatementVisitor extends RubyLintVisitor {

	public EmptyStatementVisitor(String contents) {
		super(contents);
	}
	
	@Override
	protected String getOptionKey() {
		return RubyCore.COMPILER_PB_EMPTY_STATEMENT;
	}

	public Instruction visitIfNode(IfNode iVisited) {
		String source = getSource(iVisited);
		if (iVisited.getThenBody() == null && source.indexOf("unless") == -1) {
			createProblem(iVisited.getPosition(), "Empty Conditional Body");
		}
		return super.visitIfNode(iVisited);
	}
	
	public Instruction visitDefnNode(DefnNode iVisited) {
		if (iVisited.getBodyNode() == null) {
			createProblem(iVisited.getPosition(), "Empty Method Definition");
		}
		return super.visitDefnNode(iVisited);
	}

	public Instruction visitDefsNode(DefsNode iVisited) {
		if (iVisited.getBodyNode() == null) {
			createProblem(iVisited.getPosition(), "Empty Method Definition");
		}
		return super.visitDefsNode(iVisited);
	}
	
	public Instruction visitWhenNode(WhenNode iVisited) {
		if (iVisited.getBodyNode() == null) {
			createProblem(iVisited.getPosition(), "Empty When Body");
		}
		return super.visitWhenNode(iVisited);
	}
	
	public Instruction visitIterNode(IterNode iVisited) {
		if (iVisited.getBodyNode() == null) {
			createProblem(iVisited.getPosition(), "Empty Block");
		}
		return super.visitIterNode(iVisited);
	}

}
