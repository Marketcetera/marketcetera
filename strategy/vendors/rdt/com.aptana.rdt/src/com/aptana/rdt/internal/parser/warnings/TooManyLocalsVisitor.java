package com.aptana.rdt.internal.parser.warnings;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.RootNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class TooManyLocalsVisitor extends RubyLintVisitor {

	public static final int DEFAULT_MAX_LOCALS = 4;
	
	private int maxLocals;
	private Set<String> locals;

	public TooManyLocalsVisitor(String contents) {
		this(AptanaRDTPlugin.getDefault().getOptions(), contents);		
	}
	
	public TooManyLocalsVisitor(Map<String,String> options, String contents) {
		super(options, contents);
		maxLocals = getInt(AptanaRDTPlugin.COMPILER_PB_MAX_LOCALS, DEFAULT_MAX_LOCALS); 
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
		return AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_LOCALS;
	}
	
	@Override
	public Instruction visitRootNode(RootNode iVisited) {
		locals = new HashSet<String>(); // FIXME Keep a scoped stack of locals?!
		Instruction ins = super.visitRootNode(iVisited);
		locals.clear();
		return ins;
	}
	

	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		locals = new HashSet<String>();
		return super.visitDefsNode(iVisited);
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		locals = new HashSet<String>();
		return super.visitDefnNode(iVisited);
	}

	@Override
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
		locals.add(iVisited.getName());
		return super.visitLocalAsgnNode(iVisited);
	}

	public void exitDefnNode(DefnNode iVisited) {
		if (locals.size() > maxLocals) {
			createProblem(iVisited.getNameNode().getPosition(), "Too many local variables: " + locals.size());
		}
		locals.clear();
	}
	
	@Override
	public void exitDefsNode(DefsNode iVisited) {
		if (locals.size() > maxLocals) {
			createProblem(iVisited.getNameNode().getPosition(), "Too many local variables: " + locals.size());
		}
		locals.clear();
		super.exitDefsNode(iVisited);
	}
}
