package com.aptana.rdt.internal.parser.warnings;

import java.util.ArrayList;
import java.util.List;

import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.Node;
import org.jruby.ast.SuperNode;
import org.jruby.ast.ZSuperNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class SubclassCallsSuper extends RubyLintVisitor {

	private boolean inConstructor;
	private boolean calledSuper;	
	private List<Boolean> subclassStack = new ArrayList<Boolean>();
	
	public SubclassCallsSuper(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
		subclassStack.add(Boolean.FALSE); // top level should be marked as not subclass
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_SUBCLASS_DOESNT_CALL_SUPER;
	}
	
	@Override
	public Instruction visitClassNode(ClassNode iVisited) {
		Node superNode = iVisited.getSuperNode();
		if (superNode != null) {
			subclassStack.add(Boolean.TRUE);
		} else {
			subclassStack.add(Boolean.FALSE);
		}
		return super.visitClassNode(iVisited);
	}
	
	@Override
	public void exitClassNode(ClassNode iVisited) {
		subclassStack.remove(subclassStack.size() - 1);
		super.exitClassNode(iVisited);
	}

	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		if (!isSubClass()) return null;
		if (!iVisited.getName().equals("initialize")) return null;
		inConstructor = true;
		calledSuper = false;
		
		return super.visitDefnNode(iVisited);
	}
	
	private boolean isSubClass() {
		return subclassStack.get(subclassStack.size() - 1);
	}

	@Override
	public Instruction visitSuperNode(SuperNode iVisited) {
		if (inConstructor) calledSuper = true;
		return super.visitSuperNode(iVisited);
	}
	
	@Override
	public Instruction visitZSuperNode(ZSuperNode iVisited) {
		if (inConstructor) calledSuper = true;
		return super.visitZSuperNode(iVisited);
	}
	
	@Override
	public void exitDefnNode(DefnNode iVisited) {
		if (!isSubClass()) return;
		if (!iVisited.getName().equals("initialize")) return;
		if (!calledSuper) {
			createProblem(iVisited.getNameNode().getPosition(), "Subclass does not call super in constructor");
		}
		inConstructor = false;
		super.exitDefnNode(iVisited);
	}
	
}
