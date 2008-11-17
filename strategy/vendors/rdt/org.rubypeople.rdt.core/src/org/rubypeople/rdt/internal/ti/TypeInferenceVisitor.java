package org.rubypeople.rdt.internal.ti;

import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.ti.data.LiteralNodeTypeNames;
import org.rubypeople.rdt.internal.ti.data.TypicalMethodReturnNames;

public class TypeInferenceVisitor extends InOrderVisitor {
	
	private Scope globalScope;	
	private Scope currentScope;	
	
	// TODO: init globalScope to null, push in first non-null node as
	// globalScope
	public TypeInferenceVisitor( Node rootNode ) {
		globalScope = new Scope( rootNode, null );
		currentScope = globalScope;
	}
	
	/**
	 * Visit a ModuleNode, and extract its local variables from the embedded
	 * body ScopeNode
	 */
	public Instruction visitModuleNode(ModuleNode iVisited) {		
		Scope newScope = pushScope( iVisited );
		Variable.insertLocalsFromScopeNode(iVisited.getScope(), newScope);
		return super.visitModuleNode(iVisited);
	}
	
	/**
	 * Visit a ClassNode, and extract its local variables from the embedded body
	 * ScopeNode
	 */
	public Instruction visitClassNode(ClassNode iVisited) {
		Scope newScope = pushScope( iVisited );
		Variable.insertLocalsFromScopeNode(iVisited.getScope(), newScope);
		return super.visitClassNode(iVisited);
	}
	
	/**
	 * Visit a DefnNode, and extract its local variables from the embedded body
	 * ScopeNode
	 */
	public Instruction visitDefnNode(DefnNode iVisited) {
		Scope newScope = pushScope( iVisited );
		Variable.insertLocalsFromScopeNode(iVisited.getScope(), newScope);
		// TODO: insert from argsNodes
		return super.visitDefnNode(iVisited);
	}
	
	/**
	 * Visit a DefsNode, and extract its local variables from the embedded body
	 * ScopeNode
	 */
	public Instruction visitDefsNode(DefsNode iVisited) {
		Scope newScope = pushScope( iVisited );
		Variable.insertLocalsFromScopeNode(iVisited.getScope(), newScope);
		// TODO: insert from argsNodes
		return super.visitDefsNode(iVisited);
	}
	
	/**
	 * Visit an IterNode, and extract variable references from it
	 */
	public Instruction visitIterNode(IterNode iVisited) {
		// TODO: push iterator var into the iter's scope.
//		Scope newScope = pushScope(iVisited);
// newScope.getVariables().add( new Variable( newScope, ))
		// TODO: insert from varNode; either DAsgnNode or LocalAsgnNode
		// depending... (see: block local var ambiguity)
		pushScope( iVisited );
		return super.visitIterNode(iVisited);
	}
	
	/**
	 * Pushes a new scope onto the stack based on the specified node.
	 * 
	 * @param node
	 *            Node which signifies the scope being pushed
	 * @return newly pushed Scope
	 */
	private Scope pushScope( Node node ) {
		Scope newScope = new Scope( node, currentScope );
		currentScope = newScope;
		return newScope;
	}
	
	// TODO: how to tell when to do this?
	// TODO: perhaps model IndexUpdater rather than InOrderVisitor
	private void popScope() {
		currentScope = currentScope.getParentScope();
	}
	
	/**
	 * Used to build STIGuess instances by recording instances of method
	 * invocation against variables.
	 */
	public Instruction visitCallNode(CallNode iVisited) {
		Variable var = getVariableByVarNode( iVisited.getReceiverNode() );
		if ( var != null ) {
			// TODO: add call to list
		}
		return super.visitCallNode(iVisited);
	}
	
	/**
	 * Gets a Variable reference by a Node
	 * 
	 * @param node -
	 *            LocalVarNode, InstVarNode, GlobalVarNode, ClassVarNode, or
	 *            DVarNode
	 * @return Variable or null
	 */
	private Variable getVariableByVarNode( Node node )
	{
		// For local variables, search current scope for the variable by count.
		if (node instanceof LocalVarNode) {
			LocalVarNode localVarNode = (LocalVarNode) node;
			return currentScope.getLocalVariableByCount(localVarNode.getIndex());
		}
		// TODO: InstVarNode
		// TODO: GlobalVarNode
		// TODO: ClassVarNode
		// TODO: DVarNode
		return null;
	}
	
	/**
	 * Local assignment may provide a concrete type from the rvalue
	 */
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
		Variable var = currentScope.getLocalVariableByCount( iVisited.getIndex() );
		if ( var == null )
		{
			// Local Variable cannot be found... are we in the global scope?
			// (i.e. no localNames given by JRuby)
			if ( currentScope == globalScope )
			{
				// Yes - stick this variable into the global scope.
				// TODO: Shouldn't JRuby give a ScopeNode w/ a .getLocalNames()
				// for the global script?
				var = new Variable( globalScope, iVisited.getName(), iVisited.getIndex() );
				currentScope.getVariables().add(var);
			}
		}
		
		System.out.print("Associating a type to Variable " + var.getName() + ": " );
		
		Node valueNode = iVisited.getValueNode();

		// Try seeing if the rvalue is a constant (5, "foo", [1,2,3], etc.)
		String concreteGuess = LiteralNodeTypeNames.get(valueNode.getClass().getSimpleName()); 
		if ( concreteGuess != null )
		{
    		var.getTypeGuesses().add( new BasicTypeGuess( concreteGuess, 100 ) );
		}
//    	else if ( valueNode instanceof LocalVarNode ) {
//    		// TODO: this method needs to be fixed... see
//			// ReferenceTypeGuess.java
//    		LocalVarNode rhsNode = (LocalVarNode)valueNode;
//    		Variable rhsVar = currentScope.getLocalVariableByCount(rhsNode.getCount());
//    		var.getTypeGuesses().add( new ReferenceTypeGuess( rhsVar ) );
//    	}
		
		// Try seeing if the rvalue is a well-known method call such as 5.to_s or FooClass.new
		else if (valueNode instanceof CallNode)
		{
			CallNode callValueNode = (CallNode)valueNode;
			String method = callValueNode.getName();
			
			// Try ConstNode.new			
			if ( method.equals("new") && callValueNode.getReceiverNode() instanceof ConstNode)
			{
				var.getTypeGuesses().add( new BasicTypeGuess( ((ConstNode)callValueNode.getReceiverNode()).getName() , 100 ) );
			}
			
			else {
				// Try some well-known method from built-in classes.  I.e. 5.to_s yields a String
				String methodReturnTypeGuess = TypicalMethodReturnNames.get(method);
				if ( methodReturnTypeGuess != null )
				{
					var.getTypeGuesses().add( new BasicTypeGuess( methodReturnTypeGuess, 100 ) );
				}
			}
		}
		return super.visitLocalAsgnNode(iVisited);
	}
	
	/**
	 * Similar to Node.toString(),. but with the beginning line number.
	 * 
	 * @param node
	 * @return
	 */
	private String stringifyNode(Node node) {
		return node.getClass().getName() + "@ :" + node.getPosition().getStartLine();
	}

}
