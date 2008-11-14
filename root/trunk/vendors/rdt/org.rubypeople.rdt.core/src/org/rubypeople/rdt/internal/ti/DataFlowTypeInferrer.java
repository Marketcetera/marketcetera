package org.rubypeople.rdt.internal.ti;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.VCallNode;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ti.data.LiteralNodeTypeNames;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.MethodDefinitionLocator;
import org.rubypeople.rdt.internal.ti.util.MethodInvocationLocator;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;
import org.rubypeople.rdt.internal.ti.util.ScopedNodeLocator;

public class DataFlowTypeInferrer implements ITypeInferrer {
	private static final boolean VERBOSE = false;

	private void sysout(String string) {
		if ( VERBOSE ) {
			System.out.println(string);
		}
	}
	
	private void prettyPrint(Node node) {
		sysout( "----------------------------------------\n" +
				"Node: " + node.getClass().getSimpleName() + "\n" +
				"Source:\n[" + node.getPosition().getStartOffset() + ".." + node.getPosition().getEndOffset() + "]\n" +
				source.substring( node.getPosition().getStartOffset(), node.getPosition().getEndOffset()) + "\n" + 
				"----------------------------------------" );
	}
	
	TypeInferenceHelper helper;
	private String source;
	private Node rootNode;
	
	// To detect cycles in dataflow graph
	private List<Node> inferNodeStack;
	
	
	public List<ITypeGuess> infer(String source, int offset) {
		List<ITypeGuess> guesses = new LinkedList<ITypeGuess>();

		this.helper = TypeInferenceHelper.Instance();
		this.source = source;
		this.rootNode = (new RubyParser()).parse(source).getAST();
		this.inferNodeStack = new LinkedList<Node>();
		
		Node node = OffsetNodeLocator.Instance().getNodeAtOffset(rootNode, offset);
		
		if ( node == null ) { return null; }

		guesses = inferNodeType(node);
		
		guesses = redistributeGuessConfidences(guesses);
		
		return guesses;
	}
	
	/**
	 * Redistribute the confidence percentages.  I.e. if guesses contains three guesses, each at 100%, they will now each be 33%.
	 * @param guesses Guesses to redistribute
	 * @return Guesses with confidences redistributes
	 */
	private List<ITypeGuess> redistributeGuessConfidences( List<ITypeGuess> guesses ) {
		int sum = 0;
		for ( ITypeGuess guess : guesses ) {
			sum += guess.getConfidence();
		}
		
		List<ITypeGuess> newGuesses = new ArrayList<ITypeGuess>(guesses.size());
		for ( ITypeGuess guess : guesses ) {
			ITypeGuess newGuess = new BasicTypeGuess( guess.getType(), (int)(((double)guess.getConfidence()) / ((double)sum) * 100.0 ) );
			newGuesses.add( newGuess );
		}
		
		return newGuesses;
	}
	
	// Infer the type of specified node
	private List<ITypeGuess> inferNodeType(Node node) {
		
		sysout("Inferring node: " + node.getClass().getSimpleName());
		List<ITypeGuess> guesses = new ArrayList<ITypeGuess>(1);
		
		// Detect cycles in data flow graph
		if ( inferNodeStack.indexOf( node ) != -1 ) {
			sysout("Data flow graph cycle detected:");
			prettyPrint(node);
			return guesses;
		}
		
		// Push node onto stack
		inferNodeStack.add( 0, node );

		if ( isSelfReferenceNode( node ) ) {
			guesses.add( getSelfReferenceNodeType( node ) );
		}
		
		if ( isAssignmentNode( node ) ) {
			guesses.addAll( inferNodeType( getAssignmentNodeValueNode( node ) ) );
		}
		
		if ( isTypeDefinitionNode( node ) ) {
			guesses.add( getTypeDefinitionNodeType( node ) );
		}

		if ( isConstantNode( node ) ) {
			guesses.add( getConstantNodeType( node ) );
		}
		
		if ( node instanceof LocalVarNode ) {		
			guesses.addAll( getLocalVarReferenceNodeTypes( (LocalVarNode)node ) );
		}
		
		if ( node instanceof DVarNode ) {
			guesses.addAll( getDVarReferenceNodeTypes( (DVarNode)node ) );
		}
		
		if ( node instanceof InstVarNode ) {
			guesses.addAll( getInstanceVarReferenceNodeTypes( (InstVarNode)node ) );
		}

		if ( node instanceof ClassVarNode ) {
			guesses.addAll( getClassVarReferenceNodeTypes( (ClassVarNode)node ) );
		}

		if ( node instanceof GlobalVarNode ) {
			guesses.addAll( getGlobalVarReferenceNodeTypes( (GlobalVarNode)node ) );
		}
		
		if ( isCallNode( node ) ) {
			guesses.addAll( getCallNodeTypes( node ) );
		}
		
		
//		PSEUDOCODE:			
//		if ( element is_a(instvar or global)) {
//ALREADY USING THIS:		
//			types = sum(typeOfEach(getThingsAssignedInto(element, :within => element.lexicalScope)));
//			return types if any found;
//CAN STILL FALLBACK TO:
//			//otherwise
//			usages = list of places where element is passed as a param;
//			sameTypedElements = list of elements passed into the same method-param-location element is;
//			types = sum(typeOfEach(sameTypedElements));
//			return types if any found;
//CAN STILL FALLBACK TO:
//			//otherwise
//			methods = list of methods invoked against element;
//			types = sum(typesRespondingToAllOfThese);
//			return types;
//		}
//		if ( element is_a(method invocation)) {
//ALREADY USING THIS:		
//			klass = typeOf(receiver);
//
//          // nice special case: check for accessors/mutators		
//			// hook for "magic" combinations; (class << ActiveRecord::Base).find(*) => ArrayOf[klass], etc.
//
//			defNode = findDefinition(receiver,method_name);
//			return findReturnedTypesInDefNode();
//		}
		
		
		// Pop node from stack
		inferNodeStack.remove(0);
		
		return guesses;
			
	}

	private boolean isConstantNode(Node node) {		
		return ( node instanceof ConstNode ) || ( null != LiteralNodeTypeNames.get(node.getClass().getSimpleName() ) );
	}
	
	// Look up from LiteralNodeTypeNames
	private ITypeGuess getConstantNodeType(Node node) {
		if ( node instanceof ConstNode ) {
			return new BasicTypeGuess( ((ConstNode)node).getName(), 100 );
		} else {
			return new BasicTypeGuess( LiteralNodeTypeNames.get(node.getClass().getSimpleName()), 100 );
		}
	}
	
	private boolean isTypeDefinitionNode(Node node) {
		return ( node instanceof ClassNode ) || ( node instanceof ModuleNode );
	}
	
	private ITypeGuess getTypeDefinitionNodeType(Node node) {
		String typeNodeName = helper.getTypeNodeName( node );
		if ( typeNodeName != null ) {
			return new BasicTypeGuess( typeNodeName, 100 );
		}
		return null;
	}
	
	private boolean isSelfReferenceNode(Node node) {
		return ( node instanceof SelfNode);
	}
	
	private ITypeGuess getSelfReferenceNodeType( Node node ) {
		Node enclosingTypeNode = findEnclosingTypeNode( node );
		return getTypeDefinitionNodeType( enclosingTypeNode );
	}
	
	private List<Node> findAllSendersOfMethod( String typeName, String methodName ) {
		return MethodInvocationLocator.Instance().findMethodInvocations( rootNode, typeName, methodName, new DataFlowTypeInferrer() );
	}
	
	private List<Node> findAllMethodDefinitions( String typeName, String methodName ) {
		return MethodDefinitionLocator.Instance().findMethodDefinitions( rootNode, typeName, methodName );
	}
	
	private List<Node> findRetvalExprs( Node methodNode ) {
		
		
		//TODO: Does this handle implicit returns??
		
		List<Node> returnNodes = ScopedNodeLocator.Instance().findNodesInScope(methodNode, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ( node instanceof ReturnNode );
			}
		});
		
		List<Node> retvalExprs = new ArrayList<Node>(returnNodes.size());
		for ( Node returnNode : returnNodes ) {
			retvalExprs.add( ((ReturnNode)returnNode).getValueNode() );
		}
		
		sysout("Found " + retvalExprs.size() + " + retval exprs in method " + helper.getMethodDefinitionNodeName( methodNode ));
		
		return retvalExprs;
	}
	
	private Node findEnclosingMethodNode(Node node) {
		Node enclosingScopeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, node.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ( node instanceof DefnNode ) ||
					   ( node instanceof DefsNode );
			}
		});
		
		if ( enclosingScopeNode == null ) {
			enclosingScopeNode = rootNode;
		}
		
		return enclosingScopeNode;
	}
	
	private Node findEnclosingTypeNode(Node node) {
		Node enclosingTypeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, node.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ( node instanceof ClassNode ) || ( node instanceof ModuleNode );
			}
		});
		
		// TODO: Handle reference inside metaclass block:
		// class << foo; [[INFER]] .....
		
		if ( enclosingTypeNode == null ) {
			enclosingTypeNode = rootNode;
		}
		
		return enclosingTypeNode;
	}

	private List<ITypeGuess> getLocalVarReferenceNodeTypes(LocalVarNode node) {
		List<ITypeGuess> possibleTypes = new ArrayList<ITypeGuess>(1);
		
		
		// Get enclosing scope
		Node enclosingScopeNode = findEnclosingMethodNode( node );
		if ( enclosingScopeNode == rootNode ) {
			sysout("localvarnode outside a method!");
			enclosingScopeNode = findEnclosingTypeNode( node );
		}

		//TODO: ScopedNodeLocator doesn't ensure that returned asgns are prior to the ref... relevant to algo?
		// Are there prior assigns into this ref within the scope? 
		final String localVarName = helper.getVarName(node);
		
		List<Node> localAssignsIntoNode = ScopedNodeLocator.Instance().findNodesInScope(enclosingScopeNode, new INodeAcceptor() {
			public boolean doesAccept(Node acceptNode) {
				if ( acceptNode instanceof LocalAsgnNode ) {
					return ( ((LocalAsgnNode)acceptNode).getName().equals( localVarName ) );
				}
				return false;
			}
		});
		
		// If so, return the sum of the RHSes' type inferences
		if ( ( localAssignsIntoNode != null ) && ( localAssignsIntoNode.size() > 0 ) ) {
			for ( Node asgnNode : localAssignsIntoNode ) {
				possibleTypes.addAll( inferNodeType( ((LocalAsgnNode)asgnNode).getValueNode() ) );
			}
			return possibleTypes;
		}
		
		
		// No prior assigns; if is an arg, find send-exprs into that arg, return sum of their inferences
		if ( helper.isArgumentInMethod( localVarName, enclosingScopeNode ) ) {
			
			// Rename for clarity
			Node enclosingMethodNode = enclosingScopeNode;
			
			sysout("Is arg in method");
			// Get enclosing type name
			Node enclosingTypeNode = findEnclosingTypeNode(node);
			String enclosingTypeName = "Kernel";
			if ( enclosingTypeNode != rootNode ) {
				enclosingTypeName = helper.getTypeNodeName( enclosingTypeNode );
			}
			
			// Get enclosing method name
			String enclosingMethodName = helper.getMethodDefinitionNodeName( enclosingMethodNode );
			
			sysout("Inferring type of argument " + localVarName + " in method " + enclosingMethodName );
			
			// Find index of param
			ListNode argsListNode = helper.getArgsListNode( enclosingMethodNode );
			int paramIndex = helper.getArgIndex( argsListNode, localVarName );

			// Find all send-exprs to the enclosing method
			List<Node> sendExprs = findAllSendersOfMethod( enclosingTypeName, enclosingMethodName );
			sysout( "Found " + sendExprs.size() + " senders: " );
			
			
			// Find all arg-exprs in the send-exprs that flow into the local var
			List<Node> argExprs = new ArrayList<Node>(sendExprs.size());
			for ( Node sendExpr : sendExprs ) {
				prettyPrint(sendExpr);
				argExprs.add( helper.findNthArgExprInSendExpr( paramIndex, sendExpr ) );
			}
			
			sysout("Inflowing argexprs:" + argExprs.size());
			// Sum the inferred type of each arg-exprs that flows into the local var
			for ( Node argExpr : argExprs ) {
				prettyPrint(argExpr);
				possibleTypes.addAll( inferNodeType(argExpr) );
			}
			
			return possibleTypes;
		}
		
		sysout("bottom");
		
		// No prior assigns and is not an arg; return empty set of guesses.
		return possibleTypes;
		
	}
	
	private List<ITypeGuess> getDVarReferenceNodeTypes(DVarNode node) {
		List<ITypeGuess> possibleTypes = new ArrayList<ITypeGuess>(1);
		
		Node enclosingScopeNode = findEnclosingMethodNode( node );
		if ( enclosingScopeNode == rootNode ) {
			enclosingScopeNode = findEnclosingTypeNode( node );
		}
		
		// Find assignments into this variable 
		final String varName = node.getName();
		List<Node> dynAsgnNodes = ScopedNodeLocator.Instance().findNodesInScope(enclosingScopeNode, new INodeAcceptor() {
			public boolean doesAccept(Node acceptNode) {
				if ( acceptNode instanceof DAsgnNode ) {
					return ( ((DAsgnNode)acceptNode).getName().equals( varName ) );
				}
				return false;
			}
		});
		
		// Sum the inferred type of assignment RHSes
		if ( dynAsgnNodes != null ) {
			for ( Node dynAsgnNode : dynAsgnNodes ) {
				possibleTypes.addAll( inferNodeType( ((DAsgnNode)dynAsgnNode).getValueNode() ) );
			}
		}
		
		return possibleTypes;
	}
	
	private List<ITypeGuess> getInstanceVarReferenceNodeTypes(InstVarNode node) {
		List<ITypeGuess> possibleTypes = new ArrayList<ITypeGuess>(1);
		
		Node enclosingTypeNode = findEnclosingTypeNode( node );
		
		// Find assignments into this variable 
		final String instanceVarName = helper.getVarName(node);
		List<Node> instAsgnNodes = ScopedNodeLocator.Instance().findNodesInScope(enclosingTypeNode, new INodeAcceptor() {
			public boolean doesAccept(Node acceptNode) {
				if ( acceptNode instanceof InstAsgnNode ) {
					return ( ((InstAsgnNode)acceptNode).getName().equals( instanceVarName ) );
				}
				return false;
			}
		});
		
		//TODO: also collect calls to [parentype].instvarname=
		
		// Sum the inferred type of assignment RHSes
		if ( instAsgnNodes != null ) {
			for ( Node instAsgnNode : instAsgnNodes ) {
				possibleTypes.addAll( inferNodeType( ((InstAsgnNode)instAsgnNode).getValueNode() ) );
			}
		}
		
		return possibleTypes;
	}
	
	private List<ITypeGuess> getClassVarReferenceNodeTypes(ClassVarNode node) {
		List<ITypeGuess> possibleTypes = new ArrayList<ITypeGuess>(1);
		
		Node enclosingTypeNode = findEnclosingTypeNode( node );
		
		// Find assignments into this variable 
		final String classVarName = helper.getVarName(node);
		prettyPrint(enclosingTypeNode);
		List<Node> classAsgnNodes = ScopedNodeLocator.Instance().findNodesInScope(enclosingTypeNode, new INodeAcceptor() {
			public boolean doesAccept(Node acceptNode) {
				if ( acceptNode instanceof ClassVarAsgnNode ) {
					return ( ((ClassVarAsgnNode)acceptNode).getName().equals( classVarName ) );
				} else if ( acceptNode instanceof ClassVarDeclNode ) {
					return ( ((ClassVarDeclNode)acceptNode).getName().equals( classVarName ) );
				}
				return false;
			}
		});

		//TODO: class Klass;@@x=5;@@x=6;@@x;end # @@x=5 is parsed as a ClassDeclNode, and so is @@x=6.  Do ClassAsgnNodes ever pop up???
		
		
		//TODO: also collect calls to [parentypeklass].classvarname=
		
		// Sum the inferred type of assignment RHSes
		if ( classAsgnNodes != null ) {
			sysout("asgns not null: " + classAsgnNodes.size());
			for ( Node classAsgnNode : classAsgnNodes ) {
				if ( classAsgnNode instanceof ClassVarAsgnNode ) { 
					possibleTypes.addAll( inferNodeType( ((ClassVarAsgnNode)classAsgnNode).getValueNode() ) );
				}
				if ( classAsgnNode instanceof ClassVarDeclNode ) { 
					possibleTypes.addAll( inferNodeType( ((ClassVarDeclNode)classAsgnNode).getValueNode() ) );
				}
			}
		}
		
		return possibleTypes;
	}
	
	private List<ITypeGuess> getGlobalVarReferenceNodeTypes(GlobalVarNode node) {
		List<ITypeGuess> possibleTypes = new ArrayList<ITypeGuess>(1);
		
		// Find assignments into this variable 
		final String globalVarName = helper.getVarName(node);
		List<Node> globalAsgnNodes = ScopedNodeLocator.Instance().findNodesInScope(rootNode, new INodeAcceptor() {
			public boolean doesAccept(Node acceptNode) {
				if ( acceptNode instanceof GlobalAsgnNode ) {
					return ( ((GlobalAsgnNode)acceptNode).getName().equals( globalVarName ) );
				}
				return false;
			}
		});
		
		// Sum the inferred type of assignment RHSes
		for ( Node globalAsgnNode : globalAsgnNodes ) {
			possibleTypes.addAll( inferNodeType( ((GlobalAsgnNode)globalAsgnNode).getValueNode() ) );
		}
		
		return possibleTypes;
	}
	
	
	private boolean isAssignmentNode( Node node ) {
		return ( node instanceof LocalAsgnNode ) || ( node instanceof InstAsgnNode ) || ( node instanceof GlobalAsgnNode );
	}
	
	private Node getAssignmentNodeValueNode( Node node ) {
		if ( node instanceof InstAsgnNode ) { return ((InstAsgnNode)node).getValueNode(); }
		if ( node instanceof LocalAsgnNode ) { return ((LocalAsgnNode)node).getValueNode(); }
		if ( node instanceof GlobalAsgnNode ) { return ((GlobalAsgnNode)node).getValueNode(); }
		return null;
	}
	
	private boolean isCallNode( Node node ) {
		return ( node instanceof CallNode ) || ( node instanceof FCallNode ) || ( node instanceof VCallNode );
	}
	
	private List<ITypeGuess> getCallNodeTypes( Node node ) {
		String methodName = helper.getCallNodeMethodName( node );
		
		// Handle class instantiations separately 
		if ( methodName.equals("new") ) {
			return getInstantiationCallNodeTypes( node );
		}
		
		List<ITypeGuess> possibleTypes = new LinkedList<ITypeGuess>();
		String receiverTypeName = null;
		
		if ( node instanceof CallNode ) {
			List<ITypeGuess> receiverTypeInferences = inferNodeType( ((CallNode)node).getReceiverNode() );
			//TODO Handle all types instead of the first
			if ( receiverTypeInferences.size() > 0 ) {
				receiverTypeName = receiverTypeInferences.get(0).getType();
			}
			
		}
		if ( node instanceof FCallNode ) {
			receiverTypeName = helper.getTypeNodeName(findEnclosingTypeNode( node ));
			if ( receiverTypeName == null ) {
				receiverTypeName = "Kernel";
			}
		}
		if ( node instanceof VCallNode ) {
			
			//TODO WTF why doesn't VCallNode support getReceiverNode 
			receiverTypeName="Kernel";
		}
		
		//TODO: Find method defnnode, sum types of its retval-exprs
		List<Node> defnNodes = findAllMethodDefinitions(receiverTypeName, methodName);
		
		sysout("Receiver type name: " + receiverTypeName);
		sysout(" " + defnNodes.size() + " defnnodes found");
		// For each send-expr, collect all retval-exprs
		List<Node> retvalExprs = new LinkedList<Node>();
		for ( Node defnNode : defnNodes ) {
			retvalExprs.addAll( findRetvalExprs( defnNode ) );
		}
		
		// Sum possible types for all retval-exprs
		for ( Node retvalExpr : retvalExprs ) {
			possibleTypes.addAll( inferNodeType( retvalExpr ) );
		}

		return possibleTypes;
	}
	
	private List<ITypeGuess> getInstantiationCallNodeTypes( Node node ) {
		List<ITypeGuess> possibleTypes = new ArrayList<ITypeGuess>(1);
		
		if ( node instanceof CallNode ) {
			Node receiverNode = ((CallNode)node).getReceiverNode();
			return inferNodeType( receiverNode );
		}
		if ( node instanceof FCallNode ) {
			Node enclosingTypeNode = findEnclosingTypeNode(node);
			possibleTypes.add( getTypeDefinitionNodeType( enclosingTypeNode ) );
		}
		if ( node instanceof VCallNode ) {
			Node enclosingTypeNode = findEnclosingTypeNode(node);
			possibleTypes.add( getTypeDefinitionNodeType( enclosingTypeNode ) );
		}
		
		return possibleTypes;
	}
}
