package org.rubypeople.rdt.internal.ti.util;

import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;

/**
 * Visitor to find the closest node that spans a given offset that satisfies a given condition.
 * @author Jason Morrison
 */
public class ClosestSpanningNodeLocator extends NodeLocator {

	//Singleton pattern
	private ClosestSpanningNodeLocator() {}
	private static ClosestSpanningNodeLocator staticInstance = new ClosestSpanningNodeLocator();
	public static ClosestSpanningNodeLocator Instance()
	{
		return staticInstance;
	}
	
	/** Offset to span. */
	private int offset;
	
	/** INodeAcceptor that defines the desired node. */
	private INodeAcceptor acceptor;
	
	/** Running best match for closest spanner */
	private Node locatedNode;

	/**
	 * Finds the closest spanning node given offset that is accepted by the acceptor.
	 * @param rootNode Root Node that contains all nodes to search.
	 * @param offset Offset to search for
	 * @param acceptor INodeAcceptor defining the condition which the desired node fulfills.
	 * @return First precursor or null.
	 */
	public Node findClosestSpanner(Node rootNode, int offset, INodeAcceptor acceptor ) {
		locatedNode = null;
		this.offset = offset;
		this.acceptor = acceptor;
		
		// Traverse to find closest precursor
		rootNode.accept(this);
		
		// Return the match
		return locatedNode;
	}

	/**
	 * Searches via InOrderVisitor for the closest spanning node.
	 */
	public Instruction handleNode(Node iVisited)
	{
		boolean nodeSpansOffset = nodeSpansOffset( iVisited, offset );
		boolean nodeSpansMoreCloselyThanCurrent = ( locatedNode == null ) ||
			( calculateSpanLength(iVisited) <= calculateSpanLength(locatedNode) ); 
		
		if ( nodeSpansOffset && nodeSpansMoreCloselyThanCurrent && acceptor.doesAccept( iVisited ) ) {
			locatedNode = iVisited;
		}
		
		return super.handleNode(iVisited);
	}
	
	/**
	 * Determine whether the node's position spans an offset 
	 * @param node Node to check
	 * @param offset Offset to check
	 * @return Whether it spans the offset
	 */
	private boolean nodeSpansOffset(Node node, int offset) {
		return
			( node.getPosition().getStartOffset() <= offset ) &&
			( node.getPosition().getEndOffset()   > offset );
	}
	
	/**
	 * Gets the span length of the node (endOffset - startOffset)
	 * @param node Node to check
	 * @return Span length
	 */
	private int calculateSpanLength(Node node) {
		if ( node == null ) { return 0; }
		if ( node.getPosition() == null ) { return 0; }
		return node.getPosition().getEndOffset() - node.getPosition().getStartOffset();
	}
}
