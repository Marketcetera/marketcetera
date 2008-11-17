package org.rubypeople.rdt.internal.core.parser.warnings;

import java.util.HashSet;
import java.util.Set;

import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.RootNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

public class ConstantReassignmentVisitor extends RubyLintVisitor {

	private Set<String> assignedConstants;
	private RootNode root;
	
	public ConstantReassignmentVisitor(String contents) {
		super(contents);
		assignedConstants = new HashSet<String>();
	}

	@Override
	protected String getOptionKey() {
		return RubyCore.COMPILER_PB_CONSTANT_REASSIGNMENT;
	}
	
	@Override
	public Instruction visitRootNode(RootNode iVisited) {
		this.root = iVisited;
		return super.visitRootNode(iVisited);
	}
	
	public Instruction visitConstDeclNode(ConstDeclNode iVisited) {		
		String namespace = ASTUtil.getNamespace(root, iVisited.getPosition().getStartOffset());
		String name = namespace + "::" + iVisited.getName();
		if (assignedConstants.contains(name)) {
			createProblem(iVisited.getPosition(), "Reassignment of a constant");
		} else
			assignedConstants.add(name);
		return super.visitConstDeclNode(iVisited);
	}

}
