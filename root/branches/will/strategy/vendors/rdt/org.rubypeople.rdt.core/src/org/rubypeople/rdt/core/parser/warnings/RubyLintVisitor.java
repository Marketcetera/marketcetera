package org.rubypeople.rdt.core.parser.warnings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.HashNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.RescueBodyNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.WhenNode;
import org.jruby.ast.visitor.AbstractVisitor;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.internal.core.parser.Error;
import org.rubypeople.rdt.internal.core.parser.Warning;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

public abstract class RubyLintVisitor extends AbstractVisitor {

	private String contents;
	protected Map<String, String> fOptions;
	private List<CategorizedProblem> problems;

	public RubyLintVisitor(String contents) {
		this(RubyCore.getOptions(), contents);
	}
	
	public RubyLintVisitor(Map<String, String> options, String contents) {
		this.problems = new ArrayList<CategorizedProblem>();
		this.contents = contents;
		this.fOptions = options;
	}
	
	protected String getSource(Node node) {
		return ASTUtil.getSource(contents, node);
	}
	
	protected String getSource(int start, int end) {
		if (contents.length() < end) end = contents.length();
		if (start < 0) start = 0;
		return contents.substring(start, end);
	}
	
	public List<CategorizedProblem> getProblems() {
		return problems;
	}
	
	public boolean isIgnored() {
		String value = getSeverity();
		if (value != null && value.equals(RubyCore.IGNORE))
			return true;
		return false;
	}

	protected void createProblem(ISourcePosition position, String message) {
		String value = getSeverity();
		if (value != null && value.equals(RubyCore.IGNORE))
			return;
		CategorizedProblem problem;
		if (value != null && value.equals(RubyCore.ERROR))
			problem = new Error(position, message, getProblemID());
		else
		  problem = new Warning(position, message, getProblemID());
		problems.add(problem);
	}

	protected String getSeverity() {
		return (String) fOptions.get(getOptionKey());
	}
	
	@Override
	protected Instruction visitNode(Node iVisited) {
		return null;
	}

	/**
	 * The key used to store the error/warning severity option.
	 * @return a String key
	 */
	abstract protected String getOptionKey();
	
	/**
	 * Meant to be overriden by classes needing to perform some action when a class definition was exited.
	 * @param iVisited
	 */
	public void exitClassNode(ClassNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a method definition.
	 * @param iVisited
	 */
	public void exitDefnNode(DefnNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitIfNode(IfNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitBlockNode(BlockNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitDefsNode(DefsNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitModuleNode(ModuleNode iVisited) {}

	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitWhenNode(WhenNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitSClassNode(SClassNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a method signature.
	 * @param iVisited
	 */
	public void exitArgsNode(ArgsNode iVisited) {}
	
	public void exitRescueBodyNode(RescueBodyNode iVisited) {}
	
	public void exitHashNode(HashNode iVisited) {}
	
	protected int getProblemID() {
		return IProblem.Uncategorized;
	}
	
}
