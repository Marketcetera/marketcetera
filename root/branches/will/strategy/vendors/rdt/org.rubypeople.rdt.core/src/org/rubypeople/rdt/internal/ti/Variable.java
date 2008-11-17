package org.rubypeople.rdt.internal.ti;

import java.util.LinkedList;
import java.util.List;

import org.jruby.parser.StaticScope;

/**
 * Holds a reference to a Ruby variable, unique by name and scope.
 * Holds a list of type guesses for this variable.
 * @author Jason Morrison
 */
public class Variable {
	private List<ITypeGuess> typeGuesses;
	private Scope scope;
	private String name;
	private int count;
	
	public Variable(Scope scope, String name, int count) {
		super();
		// XXX Shouldn't scope, name and count all be unmodifiable? You probably don't want setters for these.
		this.count = count;
		this.scope = scope;
		this.name = name;
		this.typeGuesses = new LinkedList<ITypeGuess>();
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	public List<ITypeGuess> getTypeGuesses() {
		// XXX The only uses of this method grab the list and then add to it. You should remove this method and add an addTypeGuess() method. It better encapsulates the class. ("Tell, Don't Ask")
		// XXX Also, when exposing collections use something like Collections.unmodifiableList(typeGuesses) so callers can't modify the original collection
		return typeGuesses;
	}
	
	/**
	 * Extracts the list of locals from the ScopeNode and inserts them into the specified Scope
	 * @param node ScopeNode to extract via localNames
	 * @param scope Scope into which vars will be inserted
	 */
	//todo: this is not a cohesive place to locate this code
	public static void insertLocalsFromScopeNode(StaticScope node, Scope scope)
	{
		int count = 0;
		for( Object varName : node.getVariables() )
		{
			scope.getVariables().add( new Variable( scope, (String)varName, count ) );
			count++;
		}		
	}
	
	/**
	 * Returns a String representation of this variable
	 */
	public String toString()
	{
		return "(var " + getScope().getNode().getClass().getName() + " " + name + ")";
	}
}
