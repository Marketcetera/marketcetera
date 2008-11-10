package com.aptana.rdt.internal.parser.warnings;

import java.util.Map;

import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

import com.aptana.rdt.AptanaRDTPlugin;

public class TooManyArgumentsVisitor extends RubyLintVisitor {

	public static final int DEFAULT_MAX_ARGS = 5;
	private int maxArgLength;

	public TooManyArgumentsVisitor(String contents) {
		this(AptanaRDTPlugin.getDefault().getOptions(), contents);		
	}
	
	public TooManyArgumentsVisitor(Map<String, String> options, String contents) {
		super(options, contents);
		maxArgLength = getInt(AptanaRDTPlugin.COMPILER_PB_MAX_ARGUMENTS, DEFAULT_MAX_ARGS); 
	}
	private int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt((String) fOptions.get(key));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_ARGUMENTS;
	}

	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		String[] args = ASTUtil.getArgs(iVisited.getArgsNode(), iVisited.getScope());
		if (args != null && args.length > maxArgLength) {
			createProblem(iVisited.getArgsNode().getPosition(), "Too many method arguments: " + args.length);
		}
		return super.visitDefsNode(iVisited);
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		String[] args = ASTUtil.getArgs(iVisited.getArgsNode(), iVisited.getScope());
		if (args != null && args.length > maxArgLength) {
			createProblem(iVisited.getArgsNode().getPosition(), "Too many method arguments: " + args.length);
		}
		return super.visitDefnNode(iVisited);
	}
}
