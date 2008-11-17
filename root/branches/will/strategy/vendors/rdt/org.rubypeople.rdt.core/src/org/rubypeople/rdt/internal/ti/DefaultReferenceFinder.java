package org.rubypeople.rdt.internal.ti;

import java.util.LinkedList;
import java.util.List;

import org.jruby.ast.ArgumentNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.Node;
import org.jruby.ast.types.INameNode;
import org.jruby.common.NullWarnings;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.internal.core.parser.RdtWarnings;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ti.util.FirstPrecursorNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;
import org.rubypeople.rdt.internal.ti.util.ScopedNodeLocator;

public class DefaultReferenceFinder implements IReferenceFinder {
	

	private static final boolean VERBOSE = false;

	public List<ISourcePosition> findReferences(String source, int offset) {

		// References to return
		List<ISourcePosition> references = new LinkedList<ISourcePosition>();
		
		// Parse the source
        RubyParser parser = new RubyParser(new NullWarnings());
        Node root = parser.parse(source).getAST();

        // Find origiating node
        Node orig = OffsetNodeLocator.Instance().getNodeAtOffset(root, offset);
        
        log("Origin: " + orig.getClass().getName());

        if ( isLocalVarRef(orig) ) {
        	pushLocalVarRefs( root, orig, references );
        }
        
        if ( isInstanceVarRef(orig) ) {
        	pushInstVarRefs( root, orig, references );
        }
        
        if ( isGlobalVarRef(orig) ) {
        	pushGlobalVarRefs( root, orig, references );
        }
        
//        if ( isMethodRefNode(orig)) {
//        	pushMethodRefs( root, orig, references );
//        }
        
        if ( orig instanceof ConstNode )
        {
        	pushConstRefs( root, orig, references );
        }

		return references;
	}
	
	private ISourcePosition getPositionOfName(Node node, Node scope)
	{
		ISourcePosition pos = node.getPosition();
		
		//todo: refactor the getting-of-name
		String name = null;
		if ( isLocalVarRef(node) )       { name = getLocalVarRefName(node, scope); }
		if ( isInstanceVarRef(node) )    { name = getInstVarRefName(node, scope);  }
		if ( isGlobalVarRef(node) )      { name = getGlobalVarRefName(node);       }
		if ( node instanceof ConstNode ) { name = ((ConstNode)node).getName();     }
		
		if ( name == null )
		{
			System.err.println("Couldn't get the name for: " + node.toString() + " in " + scope.toString() );
		}
		return new IDESourcePosition(pos.getFile(), pos.getStartLine(), pos.getEndLine(), pos.getStartOffset(), pos.getStartOffset() + name.length() );
	}
	
	/**
	 * Returns the name of a local var ref (LocalAsgnNode, ArgumentNode, LocalVarNode)
	 * @param node Node to get the name of
	 * @param scope Enclosing scope (to scrape args, etc.)
	 * @return
	 */
	private String getLocalVarRefName( Node node, Node scope ) {
		if (node instanceof INameNode) {
			return ((INameNode)node).getName();
		}
		
		return null;
	}
	
	private String getClassNodeName( ClassNode classNode ) {
		if (classNode.getCPath() instanceof Colon2Node) {
			Colon2Node c2node = (Colon2Node) classNode.getCPath();
			return c2node.getName();
		}
		System.err.println("ClassNode.getCPath() returned other than Colon2Node: " + classNode.toString() );	
		return null;
	}
	
	
	private String getInstVarRefName( Node node, Node scope ) {
		if ( node instanceof InstAsgnNode ) {
			return ((InstAsgnNode)node).getName();
		}
		
		if ( node instanceof ArgumentNode ) {
			return ((InstAsgnNode)node).getName();
		}
		
		if ( node instanceof InstVarNode ) {
			return ((InstVarNode)node).getName();
		}
		
		if ( node instanceof DVarNode ) {
			return ((DVarNode)node).getName();
		}
		
//		System.err.println("Encountered unhandled node type for getInstVarRefName: " + node.toString() + " in " + scope.toString());
		return null;
	}
	
	private String getGlobalVarRefName( Node node ) {
		if ( node instanceof GlobalVarNode )
		{
			return ((GlobalVarNode)node).getName();
		}
		if ( node instanceof GlobalAsgnNode ) {
			return ((GlobalAsgnNode)node).getName();			
		}
		return null;
	}
	
	private boolean isLocalVarRef( Node node ) {
		return ( ( node instanceof LocalAsgnNode ) || ( node instanceof ArgumentNode ) || ( node instanceof LocalVarNode ) );
	}

	private boolean isInstanceVarRef( Node node ) {
		return ( ( node instanceof InstAsgnNode ) || ( node instanceof InstVarNode ) ) ;
	}
	
	private boolean isGlobalVarRef( Node node ) {
		return ( ( node instanceof GlobalAsgnNode ) || ( node instanceof GlobalVarNode ) );
	}
		
	
	private void pushLocalVarRefs( Node root, Node orig, List<ISourcePosition> references ) {
		log("Finding references for a local variable " + orig.toString());
		
		// Find the search space
		Node searchSpace = FirstPrecursorNodeLocator.Instance().findFirstPrecursor(root, orig.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ( ( node instanceof DefnNode ) || ( node instanceof DefsNode ) /*TODO: Block Body? */  );
			}
		});
		
		// If no enclosing node found, search the entire space
		if ( searchSpace == null ) {
			searchSpace = root;
		}
		
		// Finalize searchSpace because Java's scoping rules are the awesome
		final Node finalSearchSpace = searchSpace; 

		// Get name of local variable reference
		final String origName = getLocalVarRefName(orig,searchSpace);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				String name = getLocalVarRefName(node, finalSearchSpace);
				return ( name != null && name.equals(origName));
			}
		});
		
		// Scrape position from pertinent nodes
		for ( Node searchResult : searchResults ) {
			references.add(getPositionOfName(searchResult, searchSpace));
		}
	}
	
	private void log(String string) {
		if (VERBOSE) System.out.println(string);		
	}

	private void pushInstVarRefs( Node root, Node orig, List<ISourcePosition> references ) {
		log("Finding references for an instance variable " + orig.toString() );
		
		Node searchSpace;
		
		// Find the name of the enclosing class
		ClassNode enclosingClass = (ClassNode)FirstPrecursorNodeLocator.Instance().findFirstPrecursor(root, orig.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ( node instanceof ClassNode );
			}
		});
		
		// If no enclosing class is identified, search root. 
		if ( enclosingClass == null ) {
			searchSpace = root;
		}
		// Find the search space - all ClassNodes for that name within root scope
		else {
			final String className = getClassNodeName(enclosingClass);
			List<Node> classNodes = ScopedNodeLocator.Instance().findNodesInScope(root, new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					if ( node instanceof ClassNode )
					{
						return getClassNodeName((ClassNode)node).equals(className);
					}
					return false;
				}
			});
			BlockNode blockNode = new BlockNode(new IDESourcePosition());
			for ( Node classNode : classNodes )
			{
				blockNode.add( classNode );
			}
			searchSpace = blockNode;
		}
		
		// Finalize searchSpace because Java's scoping rules are the awesome
		final Node finalSearchSpace = searchSpace;
		
		// Get name of local variable reference
		final String origName = getInstVarRefName(orig,searchSpace);
		
		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				if ( isInstanceVarRef(node) )
				{
					String name = getInstVarRefName(node, finalSearchSpace);
					return ( name != null && name.equals(origName));
				}
				return false;
			}
		});
		
		// Scrape position from pertinent nodes
		for ( Node searchResult : searchResults ) {
			references.add(getPositionOfName(searchResult, searchSpace));
		}
		
	}
	
	private void pushGlobalVarRefs( Node root, Node orig, List<ISourcePosition> references ) {
		final Node searchSpace = root;
		final String origName = getGlobalVarRefName(orig);
		
		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return isGlobalVarRef(node) && getGlobalVarRefName(node).equals(origName);
			}
		});
		
		// Scrape position from pertinent nodes
		for ( Node searchResult : searchResults ) {
			references.add(getPositionOfName(searchResult, searchSpace));
		}		
	}
	
	//todo: complete
//	private void pushMethodRefs( Node root, Node orig, List<ISourcePosition> references) {
//	
//		// DefnNode DefsNode CallNode VCallNode
//		
//		System.out.println("Finding references for method reference node " + orig.toString() );
//		
//		final Node searchSpace = root;
//		String origName = getMethodRefName(orig);
//		
//		// If orig is a method definition, find all references to that selector for the orig's enclosing type 
//		if ( orig instanceof DefnNode || orig instanceof DefsNode )
//		{
//			((DefnNode)orig).g
//		}
//		
//		Node receiver = getMethodReceiver(orig);
//	}
	
	private void pushConstRefs( Node root, Node orig, List<ISourcePosition> references) {
		if ( !( orig instanceof ConstNode) )
		{
			return;
		}
		
		final String matchName = ((ConstNode)orig).getName();
		List <Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(root, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				if ( node instanceof ConstNode )
				{
					return ((ConstNode)node).getName().equals(matchName);
				}
				return false;
			}
		});
		
		for ( Node searchResult : searchResults ) {
			references.add(getPositionOfName(searchResult, root ) );
		}
	}
	
	

}
