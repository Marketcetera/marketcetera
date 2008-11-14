package com.aptana.rdt.internal.parser.warnings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class UnusedLocalVariable extends RubyLintVisitor {

	private List<LocalAsgnNode> locals;
	private Set<String> refs;
	private boolean inArgsNode = false;

	public UnusedLocalVariable(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
		locals = new ArrayList<LocalAsgnNode>();
		refs = new HashSet<String>();
	}
	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_UNUSED_LOCAL_VARIABLE;
	}	
	
	@Override
	protected int getProblemID() {
		return IProblem.UnusedPrivateMethod;
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		clear();
		return super.visitDefnNode(iVisited);
	}
	
	@Override
	public Instruction visitArgsNode(ArgsNode visited) {
		inArgsNode = true;
		return super.visitArgsNode(visited);
	}
	
	@Override
	public void exitArgsNode(ArgsNode visited) {
		inArgsNode = false;
		super.exitArgsNode(visited);
	}
	
	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		clear();
		return super.visitDefsNode(iVisited);
	}
	
	@Override
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
		if (!inArgsNode) {
			locals.add(iVisited);
		}
		return super.visitLocalAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitLocalVarNode(LocalVarNode iVisited) {
		refs.add(iVisited.getName());
		return super.visitLocalVarNode(iVisited);
	}
	
	@Override
	public void exitDefnNode(DefnNode iVisited) {
		checkLocals();
		clear();
		super.exitDefnNode(iVisited);
	}
	
	@Override
	public void exitDefsNode(DefsNode iVisited) {
		checkLocals();
		clear();
		super.exitDefsNode(iVisited);
	}
	
	private void clear() {
		locals.clear();
		refs.clear();
	}
	
	private void checkLocals() {
		for (LocalAsgnNode local : locals) {
			if (!refs.contains(local.getName())) {
				createProblem(local.getPosition(), "Unused local variable " + local.getName());
			}
		}
	}
}
