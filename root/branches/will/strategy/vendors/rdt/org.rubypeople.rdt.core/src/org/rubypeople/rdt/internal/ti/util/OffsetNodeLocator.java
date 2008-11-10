package org.rubypeople.rdt.internal.ti.util;

import java.util.Iterator;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.ConstNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;

/**
 * Given a JRuby AST Node and a source offset, recursively searches
 * the children of the root Node for the Node that most tightly
 * matches the given offset.
 * 
 * @author Jason Morrison
 */
public class OffsetNodeLocator extends NodeLocator {

	// Singleton pattern
	private OffsetNodeLocator() {}
	private static OffsetNodeLocator staticInstance = new OffsetNodeLocator();
	public static OffsetNodeLocator Instance()
	{
		return staticInstance;
	}

	/** Most closely spanning node yet found. */
	private Node locatedNode;
	
	/** Offset sought. */
	private int offset;

	/**
	 * Gets the most closely spanning node of the requested offset. 
	 * @param rootNode Node which should span or have children spanning the offset.
	 * @param offset Offset to locate the node of.
	 * @return Node most closely spanning the requested offset.
	 */
	public Node getNodeAtOffset(Node rootNode, int offset) {
		if ( rootNode == null ) { return null; }
		
		locatedNode = null;
		this.offset = offset;

		// Traverse to find closest node
		rootNode.accept(this);
		
		// Refine the node, if possible, to an inner node not covered by the visitor
		// (Why?  Nodes such as ArgumentNode don't like being visited, so they must be handled here.)
		locatedNode = refine(locatedNode);

		// Return the node
		return locatedNode;
	}
	
	private Node refine(Node node) {
		// If the search returned an ArgsNode, try to find the specific ArgumentNode matched
		if ( node instanceof ArgsNode ) {
			ArgsNode argsNode = (ArgsNode)node;
			if ( argsNode.getRequiredArgsCount() > 0 ) {
				for (Iterator iter = argsNode.getArgs().childNodes().iterator(); iter.hasNext();) {
					ArgumentNode argNode = (ArgumentNode) iter.next();
					if ( nodeDoesSpanOffset(argNode, offset) ) {
						return argNode;
					}
				}
			}
		}
		return node;
	}

	/**
	 * For each node, see if it spans the desired offset.
	 * If so, see if it spans it more closely than any previously identified spanning node.
	 * If so, record it as the most closely spanning yet.
	 */
	public Instruction handleNode(Node iVisited) {
		// Skip the NewlineNode since its position is very unaccurate
		if (!(iVisited instanceof NewlineNode) && nodeDoesSpanOffset(iVisited, offset)) {
			//note: careful... should this be <=?  I think so; since it traverses in-order, this should find the "most specific" closest node. i.e.
			//def foo;x;end offset at 'x' is a 1-char ScopingNode and 1-char LocalVarNode; it should identify the LocalVarNode, which <= does.
			if (locatedNode == null || ( nodeSpanLength(iVisited) <= nodeSpanLength(locatedNode))) {
				if (!((locatedNode instanceof Colon2Node) && (iVisited instanceof ConstNode))) {
				locatedNode = iVisited;
				}
				if (iVisited instanceof ArgsNode) {
					ArgsNode args = (ArgsNode) iVisited;
					handleNode(args.getRestArgNode());
				}
			}
		}
		
		// TODO Since we are moving in order, if a spanning node has been located, and the current node does
		//      not span, we can effectively return early since no subsequent nodes should span.  Not doing this
		//      now, just in case InOrderVisitor proves to not quite be in-order (i.e. offsets reported are off.)
		
		return super.handleNode(iVisited);
	}

	

}
