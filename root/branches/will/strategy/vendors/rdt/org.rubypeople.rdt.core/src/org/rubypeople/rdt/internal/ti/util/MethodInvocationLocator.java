package org.rubypeople.rdt.internal.ti.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.internal.ti.ITypeGuess;
import org.rubypeople.rdt.internal.ti.ITypeInferrer;
import org.rubypeople.rdt.internal.ti.TypeInferenceHelper;

/**
 * Visitor to find all method invocations for the specified type and method names
 * @author Jason Morrison
 */
public class MethodInvocationLocator extends NodeLocator {

	//Singleton pattern
	private MethodInvocationLocator() {}
	private static MethodInvocationLocator staticInstance = new MethodInvocationLocator();
	public static MethodInvocationLocator Instance()
	{
		return staticInstance;
	}
	
	/** Inference helper */
	private TypeInferenceHelper helper = TypeInferenceHelper.Instance();
	
	/** Type of receiver to look for */
	private String typeName;
	
	/** Name of method to search for invocations of */
	private String methodName;
	
	/** Running total of results */
	private List<Node> locatedNodes;
	
	/** Type inferrer to use when resolving receiver-types */
	private ITypeInferrer inferrer;

	/** Source to search within */
	private String source;
	
	/**
	 * Finds all method invocation node within rootNode whose receiver is of type typeName and method is named methodName
	 * @param rootNode Node to search within
	 * @param source Source to search within
	 * @param typeName Name of type of method-send-expr receiver
	 * @param methodName Name of method to find
	 * @param inferrer Inferrer to use for resolving receiver-types
	 * @return
	 */
	public List<Node> findMethodInvocations( Node rootNode, String typeName, String methodName, ITypeInferrer inferrer ) {
		if ( rootNode == null ) { return null; }
		
		this.locatedNodes = new LinkedList<Node>();
		this.typeNameStack = new LinkedList<String>(); 
		this.typeName = typeName;
		this.methodName = methodName;
		this.inferrer = inferrer;
		
		typeNameStack.add("Kernel");
		
		// Traverse to find all matches
		rootNode.accept(this);
		
		// Return the matches
		return locatedNodes;
	}
	

	public Instruction handleNode(Node iVisited) {
		
		// Check for invocations on self
		if ( iVisited instanceof FCallNode ) {
			if ( ((FCallNode)iVisited).getName().equals(methodName)) {
				if ( peekType().equals(typeName)) {
					locatedNodes.add(iVisited);
				}
			}
		}

		// Look for CallNodes where receiver matches typeName and methodName matches method invoked
		if ( iVisited instanceof CallNode ) {
			if ( helper.getCallNodeMethodName(iVisited).equals(methodName)) {
				// TI the receiver
				Node receiverNode = ((CallNode)iVisited).getReceiverNode();
				Collection<ITypeGuess> receiverTypeInferences = inferrer.infer( source, receiverNode.getPosition().getStartOffset());
				
				// If the receiver matches desired typeName, add a match!
				for ( ITypeGuess inference : receiverTypeInferences ) {
					if ( inference.getType().equals( typeName ) ) {
						locatedNodes.add( iVisited );
						break;
					}
				}
			}
		}

//		if ( iVisited instanceof VCallNode ) {
		//TODO: VCallNode does not have getReceiverNode().
		// We don't particularly care for the purpose of finding send-exprs that flow params into args of method-defns, since VCallNodes
		// don't send w/ args.  But, to make this visitor general-purpose, it would have to support VCallNodes.  Consider just renaming the
//		}
		
		return super.handleNode(iVisited);
	}
	
	public Instruction visitClassNode(ClassNode iVisited) {
		pushType( helper.getTypeNodeName( iVisited ) );
		super.visitClassNode( iVisited );
		popType();

		return null;
	}

	public Instruction visitModuleNode(ModuleNode iVisited) {
		pushType( helper.getTypeNodeName( iVisited ) );
		super.visitModuleNode( iVisited );
		popType();

		return null;
	}
	
	
}
