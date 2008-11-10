package org.rubypeople.rdt.internal.ti;

import org.jruby.ast.BlockNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.Node;
import org.jruby.parser.StaticScope;

/**
 * Visits an AST while retaining memory of current scope.
 * 
 * Nomenclature:
 *   scopingNode: [ModuleNode, ClassNode, DefnNode, DefsNode, IterNode]
 *   scopingNode.getBodyNode() returns a ScopeNode (JRuby AST type)
 * @author Jason
 *
 */
// XXX Pretty broken in concept at the moment - what is really needed is a modification of
// InOrderVisitor with push/pop added in and assoc'ing the closest Scope to each Node along
// the way with some metadata hash.
//todo: How hard is this?

public class ScopingVisitor {
	
	protected Scope globalScope;	
	protected Scope currentScope;	

	/**
	 * Create a new ScopingVisitor, and have it traverse the specified root node
	 * @param root
	 */
	public ScopingVisitor(Node root)
	{
		globalScope = new Scope(root, null);
		currentScope = globalScope;
		
		if ( isScopingNode(root) )
		{
			processNode(root);
		}
		else if ( root instanceof BlockNode )
		{
			BlockNode blockRoot = (BlockNode)root;
			for (Object node : blockRoot.childNodes() ) {
				if (node instanceof NewlineNode) {
					NewlineNode newlineNode = (NewlineNode) node;
					processNode(newlineNode.getNextNode());
				}
			}
		}
//		processNode(root);
	}

	/**
	 * Recursive method for processing an AST.  Handles pushing/popping scopes appropriately.
	 * Calls visitScopingNode or visitNode for scoping/nonscoping nodes; these are to be
	 * filled out by subclasses.
	 * 
	 * @param node Node to visit recursively.
	 */
	private void processNode(Node node) {
    	if (node == null)
    	{
    		return ;
    	}
    	
    	if ( isScopingNode( node ) )
    	{
    		// Push scope
    		currentScope = new Scope(node, currentScope);
    		
    		// Visit node for great power and riches - also for prepopulated localNames.
    		visitScopingNode(node);
    		
    		// Visit children
    		for( Object child : node.childNodes() )
    		{
    			processNode((Node)child);
    		}
    		
    		// Pop scope
    		currentScope = currentScope.getParentScope();
    	}
    	else
    	{
    		// Not a scoping node; visit it.
    		visitNode(node);
    	}
    }
	
	/**
	 * Visits a scoping node's bodyNode.
	 * It extracts the local variables from node.getBodyNode().getLocalNames().
	 * 
	 * Naming is little confusing maybe, since node.getBodyNode() is the actual ScopeNode.
	 * @param node
	 */
	//todo: does this functionality belong here in ScopingVisitor, or in a subclass?
	protected void visitScopingNode(Node node)
	{
		StaticScope bodyNode = null;
		if ( node instanceof ModuleNode ) bodyNode = ((ModuleNode)node).getScope();
		if ( node instanceof ClassNode ) bodyNode = ((ClassNode)node).getScope();
		if ( node instanceof DefnNode ) bodyNode = ((DefnNode)node).getScope();
		if ( node instanceof DefsNode ) bodyNode = ((DefsNode)node).getScope();
		if ( node instanceof IterNode ) bodyNode = ((IterNode)node).getScope();
		
		// Extract localNames
		Variable.insertLocalsFromScopeNode(bodyNode, currentScope);		
	}
	
	/**
	 * Visits the node.  Logs and delegates to more specific visit*Node methods.
	 */
	protected void visitNode(Node node)
	{
		System.out.print("ScopedVisitor :: ");
		if ( node != null )
		{
			String pos = "";
			String cls = "";
			if ( node.getPosition() != null ) pos = Integer.toString(node.getPosition().getStartLine());
			if ( node.getClass() != null )    cls = node.getClass().getName();
			System.out.print("Visiting " + node.getClass().getSimpleName() + "\tat line " + pos + " of class " + cls );
			System.out.println("[ Spanning " + node.getPosition().getStartOffset() + "-" + node.getPosition().getEndOffset()+"]");
		}
		
		// Visit specific node types
		if ( node instanceof CallNode ) visitCallNode( (CallNode)node );
		if ( node instanceof LocalAsgnNode ) visitLocalAsgnNode( (LocalAsgnNode)node );
		// :
		// :
		// :
		
	}
	
	protected void visitCallNode( CallNode node ) {}
	protected void visitLocalAsgnNode( LocalAsgnNode node ) {}
	
	
	private boolean isScopingNode(Node node)
	{
		return
			( node instanceof ModuleNode ) ||
			( node instanceof ClassNode ) ||
			( node instanceof DefnNode ) ||
			( node instanceof DefsNode ) ||
			( node instanceof IterNode );
	}
	
	
}
