package org.rubypeople.rdt.internal.ti;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jruby.ast.Node;

/**
 * Represents a scope in a Ruby script.  This may correspond to:
 *                  has ScopeNode?
 *   ModuleNode     yes
 *   ClassNode      yes for local vars, + ClassVarDeclNodes
 *   DefnNode       yes + .argsNode
 *   DefsNode  	?   yes + .argsNode
 *   IterNode  ?    no;   .varNode may be DAsgnNode (ref'd with DVarNode) or LocalAsgnNode/LocalVarNode
 *                         Iterates over IterNode.getIterNode().getReceiverNode()'s elements
 * @author Jason Morrison
 *
 */
public class Scope extends LinkedList<Scope> {

	private static final long serialVersionUID = -575610283102921342L;
	private List<Variable> variables;
	private List<Scope> childScopes;
	private Scope parentScope;
	private Node node;
	
	public Scope(Node node, Scope parentScope) {
		super();
		this.node = node;
		this.parentScope = parentScope;
		this.childScopes = new LinkedList<Scope>();
		this.variables = new LinkedList<Variable>();
	}	
	public List<Variable> getVariables() {
		return variables;
	}
	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public List<Scope> getChildScopes() {
		return Collections.unmodifiableList(childScopes);
	}
	public void addChildScope(Scope childScope) {
		childScopes.add(childScope);
	}
	public Scope getParentScope() {
		return parentScope;
	}
	public Variable getLocalVariableByCount(int count)
	{
		for (Variable var : variables) {
			if ( var.getCount() == count ) return var;			
		}
		return null;
	}
	
	
	
}
