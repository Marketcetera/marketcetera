package com.aptana.rdt.internal.parser.warnings;

import java.util.List;

import org.jruby.ast.IterNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.IProblem;

public class DynamicVariableAliasesLocal extends RubyLintVisitor {

	public DynamicVariableAliasesLocal(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_DYNAMIC_VARIABLE_ALIASES_LOCAL;
	}

	@Override
	public Instruction visitIterNode(IterNode iVisited) {
		checkNode(iVisited.getVarNode());
		return super.visitIterNode(iVisited);
	}
	
	@Override
	protected int getProblemID() {
		return IProblem.DynamicVariableAliasesLocal;
	}
	
	private void checkNode(Node varNode) {
		if (varNode == null) return;
		if (varNode instanceof ListNode) {
			checkListNode((ListNode)varNode);
		} else if (varNode instanceof MultipleAsgnNode) {
			MultipleAsgnNode multi = (MultipleAsgnNode) varNode;
			checkList(multi.childNodes());
		} else if (varNode instanceof LocalAsgnNode) {
			createProblem(varNode.getPosition(), "Dynamic variable aliases local");
		}		
	}

	private void checkListNode(ListNode node) {
		checkList(node.childNodes());
	}

	private void checkList(List list) {
		for (Object child : list) {
			Node childNode = (Node) child;
			checkNode(childNode);
		}
		
	}
}
