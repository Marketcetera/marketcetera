package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.ArrayNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.IProblem;

public class ComparableInclusionVisitor extends RubyLintVisitor {

	private static final String INCLUDE = "include";
	private static final String COMPARABLE_METHOD = "<=>";
	private static final String COMPARABLE = "Comparable";
	
	private boolean includedComparable = false;
	private boolean definedComparableMethod;
	private ISourcePosition pos;
	
	public ComparableInclusionVisitor(String code) {
		super(AptanaRDTPlugin.getDefault().getOptions(), code);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_COMPARABLE_MISSING_METHOD;
	}

	@Override
	public Instruction visitFCallNode(FCallNode iVisited) {
		if (includedComparable) return null;
		String callName = iVisited.getName();
		if (!callName.equals(INCLUDE))
			return null;
		Node args = iVisited.getArgsNode();
		if (args instanceof ArrayNode) {
			ArrayNode array = (ArrayNode) args;
			for (Object arg : array.childNodes()) {
				if (!(arg instanceof ConstNode))
					continue;
				ConstNode constNode = (ConstNode) arg;
				if (!(constNode.getName().equals(COMPARABLE)))
					continue;
				pos = constNode.getPosition();
				includedComparable = true;
				return null;
			}
		}
		return null;
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		String methodName = iVisited.getName();
		if (methodName.equals(COMPARABLE_METHOD)) {
			definedComparableMethod = true;
		}
		return super.visitDefnNode(iVisited);
	}
	
	@Override
	public void exitClassNode(ClassNode iVisited) {
		if (includedComparable && !definedComparableMethod) {
			createProblem(pos, "Included comparable but did not define a <=> method");
		}
		includedComparable = false;
		pos = null;
		definedComparableMethod = false;
	}
	
	@Override
	protected int getProblemID() {
		return IProblem.ComparableInclusionMissingCompareMethod;
	}

}
