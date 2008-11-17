package com.aptana.rdt.internal.parser.warnings;

import java.util.HashSet;
import java.util.Set;

import org.jruby.ast.CaseNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.Node;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.RootNode;
import org.jruby.ast.WhenNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class UnecessaryElseVisitor extends RubyLintVisitor {

	public UnecessaryElseVisitor(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_UNNECESSARY_ELSE;
	}
	
	@Override
	public Instruction visitIfNode(IfNode iVisited) {
		String src = getSource(iVisited);
		Node elseBody;
		Node thenBody;
		if (src.startsWith("unless")) {
			// then we need to "swap" the then and else nodes
			elseBody = iVisited.getThenBody();
			thenBody = iVisited.getElseBody();
		} else {
			elseBody = iVisited.getElseBody();
			thenBody = iVisited.getThenBody();
		}
		boolean isUnlessModifier = (iVisited.getThenBody() == null);		
		if (elseBody != null && !isUnlessModifier) {
		  if (alwaysExplicitReturn(thenBody)) {
			  createProblem(elseBody.getPosition(), "Unnecessary else"); // $NON-NLS-1$
		  }
		}
		return super.visitIfNode(iVisited);
	}

	private boolean alwaysExplicitReturn(Node body) {
		if (body == null) return false;
		ReturnVisitor visitor = new ReturnVisitor();
		body.accept(visitor);
		return visitor.alwaysExplicit();
	}
	
	private class ReturnVisitor extends InOrderVisitor {

		private boolean implicit = false;

		private Set<ReturnVisitor> branches = new HashSet<ReturnVisitor>();

		@Override
		protected Instruction visitNode(Node iVisited) {
			if (iVisited != null && !structuralNode(iVisited)
					&& !branchingNode(iVisited)
					&& !(iVisited instanceof ReturnNode)) {
				implicit = true;
			}
			return super.visitNode(iVisited);
		}

		private boolean structuralNode(Node visited) {
			return (visited instanceof RootNode)
					|| (visited instanceof NewlineNode);
		}

		private boolean branchingNode(Node visited) {
			return (visited instanceof IfNode) || (visited instanceof CaseNode);
		}

		@Override
		public Instruction visitReturnNode(ReturnNode iVisited) {
			implicit = false;
			return null;
		}

		@Override
		public Instruction visitCaseNode(CaseNode iVisited) {
			Node node = iVisited.getFirstWhenNode();
			WhenNode whenNode = (WhenNode) node;
			while (whenNode != null) {
				ReturnVisitor visitor = new ReturnVisitor();
				whenNode.getBodyNode().accept(visitor);
				branches.add(visitor);
				whenNode = (WhenNode) whenNode.getNextCase();
			}
			return null;
		}

		@Override
		public Instruction visitIfNode(IfNode iVisited) {
			if (iVisited.getThenBody() != null) {
				ReturnVisitor visitor = new ReturnVisitor();
				iVisited.getThenBody().accept(visitor);
				branches.add(visitor);
			}
			if (iVisited.getElseBody() != null) {
				ReturnVisitor visitor = new ReturnVisitor();
				iVisited.getElseBody().accept(visitor);
				branches.add(visitor);
			} else {
				implicit = true;
			}
			return null;
		}

		public boolean alwaysExplicit() {
			for (ReturnVisitor visitor : branches) {
				if (!visitor.alwaysExplicit())
					return false;
			}
			return !implicit;
		}

	}

}
