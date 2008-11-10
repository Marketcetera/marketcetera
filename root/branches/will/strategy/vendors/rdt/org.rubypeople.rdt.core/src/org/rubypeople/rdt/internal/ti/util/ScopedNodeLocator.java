package org.rubypeople.rdt.internal.ti.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.ISourcePosition;

/**
 * Visitor to find all nodes within a specific scope adhering to a certain condition.
 * @author Jason Morrison
 */
public class ScopedNodeLocator extends NodeLocator {

	//Singleton pattern
	private ScopedNodeLocator() {}
	private static ScopedNodeLocator staticInstance = new ScopedNodeLocator();
	public static ScopedNodeLocator Instance()
	{
		return staticInstance;
	}
	
	/** INodeAcceptor that defines the desired node. */
	private INodeAcceptor acceptor;
	
	/** Running total of results */
	private List<Node> locatedNodes;

	/**
	 * Finds the first node preceding the given offset that is accepted by the acceptor.
	 * @param rootNode Root Node that contains all nodes to search.
	 * @param acceptor INodeAcceptor defining the condition which the desired node fulfills.
	 * @return List of located nodes.
	 */
	public List<Node> findNodesInScope(Node rootNode, INodeAcceptor acceptor ) {
		if ( rootNode == null ) { return null; }
		if ( acceptor == null ) { return null; }
		
		locatedNodes = new LinkedList<Node>();
		this.acceptor = acceptor;
		
		// Traverse to find all matches
		rootNode.accept(this);
		
		// Return the matches
		return locatedNodes;
	}

	/**
	 * Searches via InOrderVisitor for matches
	 */
	public Instruction handleNode(Node iVisited) {
		if ( acceptor.doesAccept( iVisited ) )
		{
			locatedNodes.add(iVisited);
		}
		
		return super.handleNode(iVisited);
	}
	
	/**
	 * Handle the parsing of ArgsNode, to get at its ArgumentNodes
	 * 
	 * @see org.jruby.ast.visitor.NodeVisitor#visitArgsNode(org.jruby.ast.ArgsNode)
	 */
	public Instruction visitArgsNode(ArgsNode iVisited) {
		if ( iVisited.getRequiredArgsCount() > 0 )
		{
			for (Iterator iter = iVisited.getArgs().childNodes().iterator(); iter.hasNext();) {
				ArgumentNode argNode = (ArgumentNode) iter.next();
				if ( acceptor.doesAccept(argNode))
				{
					locatedNodes.add(argNode);
				}
			}
		}
		
		ArgumentNode argNode = iVisited.getRestArgNode();
		if (argNode != null) {
			if (acceptor.doesAccept(argNode)) {
				ISourcePosition pos = argNode.getPosition();
				pos.adjustStartOffset(1); // Rest args are off by one...
				argNode.setPosition(pos);
				locatedNodes.add(argNode);
			}
		}
		return super.visitArgsNode(iVisited);
//		
//		handleNode(iVisited);
//		acceptNode(iVisited.getBlockArgNode());
//		if (iVisited.getOptArgs() != null) {
//			visitIter(iVisited.getOptArgs().iterator());
//		}
//		return null;
	}
	
	
}
