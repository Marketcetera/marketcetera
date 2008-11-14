package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.AndNode;
import org.jruby.ast.Node;
import org.jruby.ast.OrNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

import com.aptana.rdt.AptanaRDTPlugin;

public class AndOrUsedOnRighthandAssignment extends RubyLintVisitor {
	
	public AndOrUsedOnRighthandAssignment(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_ASSIGNMENT_PRECEDENCE;
	}
	
	@Override
	public Instruction visitOrNode(OrNode iVisited) {
		Node leftHand = iVisited.getFirstNode();
		if (isAssignment(leftHand)) {
			createProblem(iVisited.getPosition(), createMessage(iVisited));
		}
		return super.visitOrNode(iVisited);
	}

	@Override
	public Instruction visitAndNode(AndNode iVisited) {
		Node leftHand = iVisited.getFirstNode();
		if (isAssignment(leftHand)) {
			createProblem(iVisited.getPosition(), createMessage(iVisited));
		}
		return super.visitAndNode(iVisited);
	}
	
	private String createMessage(Node iVisited) {
		String type;
		Node leftHand;
		Node rightHand;
		if (iVisited instanceof AndNode) {
			type = "and";
			leftHand = ((AndNode) iVisited).getFirstNode();
			rightHand = ((AndNode) iVisited).getSecondNode();
		} else { // or
			type = "or";
			leftHand = ((OrNode) iVisited).getFirstNode();
			rightHand = ((OrNode) iVisited).getSecondNode();
		}
		StringBuffer message = new StringBuffer();
		message.append("Assignment will happen before '");
		message.append(type);
		message.append("' comparison. Assignment looks like: (");
		message.append(getSource(leftHand));
		message.append(") ");
		message.append(type);
		message.append(" (");
		message.append(getSource(rightHand));
		message.append(")");
		return message.toString();
	}
	
	private boolean isAssignment(Node node) {
		return ASTUtil.isAssignment(node);		
	}
}
