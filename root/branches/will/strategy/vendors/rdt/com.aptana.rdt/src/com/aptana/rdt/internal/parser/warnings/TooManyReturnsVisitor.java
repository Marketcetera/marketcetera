package com.aptana.rdt.internal.parser.warnings;

import java.util.Map;

import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.ReturnNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class TooManyReturnsVisitor extends RubyLintVisitor {

	public static final int DEFAULT_MAX_RETURNS = 5;
	
	private int maxReturns;
	private int returnCount;

	public TooManyReturnsVisitor(String contents) {
		this(AptanaRDTPlugin.getDefault().getOptions(), contents);		
	}
	
	public TooManyReturnsVisitor(Map options, String contents) {
		super(options, contents);
		maxReturns = getInt(AptanaRDTPlugin.COMPILER_PB_MAX_RETURNS, DEFAULT_MAX_RETURNS); 
		returnCount = 0;
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
		return AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_RETURNS;
	}

	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		returnCount = 0;
		return super.visitDefsNode(iVisited);
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		returnCount = 0;
		return super.visitDefnNode(iVisited);
	}

	public void exitDefnNode(DefnNode iVisited) {
		if (returnCount > maxReturns) {
			createProblem(iVisited.getNameNode().getPosition(), "Too many explicit returns: " + returnCount);
		}
		returnCount = 0;
	}
	
	@Override
	public void exitDefsNode(DefsNode iVisited) {
		if (returnCount > maxReturns) {
			createProblem(iVisited.getNameNode().getPosition(), "Too many explicit returns: " + returnCount);
		}
		returnCount = 0;
		super.exitDefsNode(iVisited);
	}

	@Override
	public Instruction visitReturnNode(ReturnNode iVisited) {
		returnCount++;
		return super.visitReturnNode(iVisited);
	}

}
