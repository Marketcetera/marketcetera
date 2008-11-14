package org.rubypeople.rdt.internal.ti;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.CallNode;
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
import org.jruby.ast.IterNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.ast.VCallNode;
import org.jruby.ast.YieldNode;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.search.CollectingSearchRequestor;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.ti.data.LiteralNodeTypeNames;
import org.rubypeople.rdt.internal.ti.data.TypicalMethodReturnNames;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.FirstPrecursorNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;
import org.rubypeople.rdt.internal.ti.util.ScopedNodeLocator;

public class DefaultTypeInferrer implements ITypeInferrer {

	private static final String CONSTRUCTOR_INVOKE_NAME = "new";
	private RootNode rootNode;
	private Set<Node> dontVisitNodes;
	private HashSet<Node> fVisitedNodes;
	private Map<String, RootNode> parsed;

	/**
	 * Infers type inside the source at given offset.
	 * 
	 * @return List of ITypeGuess objects.
	 */
	public Collection<ITypeGuess> infer(String source, int offset) {
		dontVisitNodes = new HashSet<Node>();
		fVisitedNodes = new HashSet<Node>();
		parsed = new HashMap<String, RootNode>();
		try {
			rootNode = parse(source);
			Node node = OffsetNodeLocator.Instance().getNodeAtOffset(rootNode.getBodyNode(), offset);

			if (node == null) {
				return new ArrayList<ITypeGuess>();
			}
			return infer(node);
		} catch (SyntaxException e) {
			return new ArrayList<ITypeGuess>();
		} finally {
			parsed.clear();
			dontVisitNodes.clear();
			fVisitedNodes.clear();
		}
	}

	/**
	 * Infers the type of the specified node.
	 * 
	 * @param node
	 *            Node to infer type of.
	 * @return List of ITypeGuess objects.
	 */
	private Set<ITypeGuess> infer(Node node) {
		// Try to avoid infinite loop 
		if (fVisitedNodes.contains(node)) return new HashSet<ITypeGuess>();
		fVisitedNodes.add(node);
		
		Set<ITypeGuess> guesses = new HashSet<ITypeGuess>();
		tryLiteralNode(node, guesses);
		tryAsgnNode(node, guesses);

		// TODO refactor these 3 by common features into 1 (or 1+3) method(s)
		tryDVarNode(node, guesses);
		tryLocalVarNode(node, guesses);
		tryInstVarNode(node, guesses);
		tryGlobalVarNode(node, guesses);
		tryMethodNode(node, guesses);
		tryIterNode(node, guesses);
		
		tryWellKnownMethodCalls(node, guesses);
		if (node instanceof Colon2Node) { // if this is a constant, it may be the type name!
			Colon2Node colonNode = (Colon2Node)node;
			String name = ASTUtil.getFullyQualifiedName(colonNode);
			guesses.add(new BasicTypeGuess(name, 100));
		}
		if (node instanceof ConstNode) { // if this is a constant, it may be the type name!
			ConstNode constNode = (ConstNode)node;
			// TODO See if constant is assigned to in scope, if it is, don't add it as a type guess.
			String name = constNode.getName();
			if (!name.equals("ARGV"))
				guesses.add(new BasicTypeGuess(constNode.getName(), 100));
		}
		if (guesses.isEmpty()) { // if we have no guesses..
			if (node instanceof CallNode) { // and it's a method call, try inferring receiver type
				CallNode call = (CallNode) node;
				return infer(call.getReceiverNode());
			}
		}		
		return guesses;
	}

	private void tryDVarNode(Node node, Set<ITypeGuess> guesses) {
		if (!(node instanceof DVarNode)) return;
		Node iterNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, node.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof IterNode);
			}
		});
		Node methodCall = OffsetNodeLocator.Instance().getNodeAtOffset(rootNode, iterNode.getPosition().getStartOffset() - 1);
		
		try {
			SearchEngine engine = new SearchEngine();
			SearchPattern pattern = SearchPattern.createPattern(IRubyElement.METHOD, ASTUtil.getNameReflectively(methodCall), IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
			SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
			IRubySearchScope scope = SearchEngine.createWorkspaceScope();
			CollectingSearchRequestor requestor = new CollectingSearchRequestor();
			engine.search(pattern, participants, scope, requestor, new NullProgressMonitor());
			List<SearchMatch> matches = requestor.getResults();
			for (SearchMatch match : matches) {
				IMethod method = (IMethod) match.getElement();
				// Grab the method's source, search for yields of a var, and then return the inferred type of the yielded var
				String src = method.getRubyScript().getSource();				
				Node otherRoot = parse(src);
				
				Node methodNodeThing = OffsetNodeLocator.Instance().getNodeAtOffset(otherRoot, method.getSourceRange().getOffset());
				List<Node> yields = ScopedNodeLocator.Instance().findNodesInScope(methodNodeThing, new INodeAcceptor() {
				
					public boolean doesAccept(Node node) {
						return node instanceof YieldNode;
					}
				
				});
				if (yields == null) continue;
				for (Node yield : yields) {
					if (yield instanceof YieldNode) {
						YieldNode yieldNode = (YieldNode) yield;
						Node argsNode = yieldNode.getArgsNode();
						guesses.addAll(infer(src, argsNode.getPosition().getStartOffset()));
					}
				}
			}
		} catch (CoreException e) {
			RubyCore.log(e);
		}
	}

	private RootNode parse(String src) {
		if (parsed.containsKey(src)) {
			return parsed.get(src);
		}
		RubyParser parser = new RubyParser();
		RootNode root = (RootNode) parser.parse(src).getAST();
		parsed.put(src, root);
		return root;
	}

	private void tryIterNode(Node node, Set<ITypeGuess> guesses) {
		if (!(node instanceof IterNode)) return;
		tryEnclosingType(node, guesses);		
	}

	private void tryEnclosingType(Node node, Set<ITypeGuess> guesses) {
		Node typeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, node.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof ClassNode || node instanceof ModuleNode);
			}
		});
		if (typeNode == null) {
			// top level
			guesses.add(new BasicTypeGuess("Object", 100));
		} else {
			guesses.add(new BasicTypeGuess(ASTUtil.getFullyQualifiedTypeName(rootNode, typeNode), 100));
		}		
	}

	/**
	 * Resolve a method to it's surrounding type. If in top level, return "Object" as a guess.
	 * 
	 * @param node
	 * @param guesses
	 */
	private void tryMethodNode(Node node, Set<ITypeGuess> guesses) {
		if (!(node instanceof MethodDefNode)) return;
		tryEnclosingType(node, guesses);
	}

	/**
	 * Infers type if node is a literal node; i.e. 5, 'foo', [1,2,3]
	 * 
	 * @param node
	 *            Node to infer type of.
	 * @param guesses
	 *            List of ITypeGuess objects to insert guesses into.
	 */
	private void tryLiteralNode(Node node, Collection<ITypeGuess> guesses) {
		// Try seeing if the rvalue is a constant (5, "foo", [1,2,3], etc.)
		String concreteGuess = LiteralNodeTypeNames.get(node.getClass().getSimpleName());
		if (concreteGuess != null) {
			guesses.add(new BasicTypeGuess(concreteGuess, 100));
		}
	}

	/**
	 * Infers type if node is an assignment node; i.e. x = 5,
	 * 
	 * @y = 'foo', $z = [1,2,3]
	 * @param node
	 *            Node to infer type of.
	 * @param guesses
	 *            List of ITypeGuess objects to insert guesses into.
	 */
	private void tryAsgnNode(Node node, Collection<ITypeGuess> guesses) {
		Node valueNode = null;

		if (node instanceof LocalAsgnNode) {
			valueNode = ((LocalAsgnNode) node).getValueNode();
		}
		if (node instanceof InstAsgnNode) {
			valueNode = ((InstAsgnNode) node).getValueNode();
		}
		if (node instanceof GlobalAsgnNode) {
			valueNode = ((GlobalAsgnNode) node).getValueNode();
		}
		if (valueNode != null) {
			guesses.addAll(infer(valueNode));
		}
	}

	private void tryInstVarNode(Node node, Collection<ITypeGuess> guesses) {
		if (!(node instanceof InstVarNode))
			return;
		final InstVarNode instVarNode = (InstVarNode) node;

		// TODO: see if there is attr_reader/attr_writer, maybe?
		// TODO: find calls to the reader/writers
		// TODO: for STI on InstVar, find references within this ClassNode
		// to this InstVar... record 'em

		// Find first assignment to this var name that occurs before the
		// reference
		// TODO: This will find assignments in other local scopes that
		// precede this reference but have the same variable name.
		// To mitigate, ensure that the closest spanning ScopeNode for both
		// this LocalVarNode and the AsgnNode are the name ScopeNode.
		// Or scopingNode. Still not sure whether IterNodes count or not...
		// silly block-local-var ambiguity ;)
		
		// try and grab the assignment node if this reference is in an assignment, so we can "blacklist" it from being grabbed in next step where we grab all assignments to the instance variable
		final Node assignmentNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, instVarNode.getPosition().getStartOffset(), new INodeAcceptor() {
			
			public boolean doesAccept(Node node) {
				return node instanceof InstAsgnNode;
			}
		
		});
		if (assignmentNode != null) dontVisitNodes.add(assignmentNode);
		List<Node> assignments = new ArrayList<Node>();		
		assignments.addAll(ScopedNodeLocator.Instance().findNodesInScope(rootNode, new INodeAcceptor() {		
			public boolean doesAccept(Node node) {
				return (node instanceof InstAsgnNode) && (((InstAsgnNode)node).getName().equals(instVarNode.getName())) && !dontVisitNodes.contains(node);
			}
		}));

		for (Node assignNode : assignments) {
			tryAsgnNode(assignNode, guesses);
		}
	}

	private void tryGlobalVarNode(Node node, Collection<ITypeGuess> guesses) {
		if (!(node instanceof GlobalVarNode))
			return;
		final GlobalVarNode globalVarNode = (GlobalVarNode) node;
		int nodeStart = node.getPosition().getStartOffset();

		// TODO: for STI on GlobalVar, find references within this ClassNode
		// to this GlobalVar... record 'em
		// TODO: p.s. globals are low-priority.

		// Find first assignment to this var name that occurs before the
		// reference
		// TODO: This will find assignments in other local scopes that
		// precede this reference but have the same variable name.
		// To mitigate, ensure that the closest spanning ScopeNode for both
		// this LocalVarNode and the AsgnNode are the name ScopeNode.
		// Or scopingNode. Still not sure whether IterNodes count or not...
		// silly block-local-var ambiguity ;)
		Node initialAssignmentNode = FirstPrecursorNodeLocator.Instance().findFirstPrecursor(rootNode, nodeStart, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				String name = null;
				if (node instanceof LocalAsgnNode)
					name = ((LocalAsgnNode) node).getName();
				if (node instanceof InstAsgnNode)
					name = ((InstAsgnNode) node).getName();
				if (node instanceof GlobalAsgnNode)
					name = ((GlobalAsgnNode) node).getName();
				return (name != null && name.equals(globalVarNode.getName()));
				/**
				 * refactor to common INodeAcceptor for
				 * instVarName,localVarName,globalVarName
				 */
			}
		});
		if (initialAssignmentNode != null) {
			tryAsgnNode(initialAssignmentNode, guesses);
		}
	}

	private void tryLocalVarNode(Node node, Collection<ITypeGuess> guesses) {
		if (node instanceof VCallNode) {
		  // FIXME How do we handle local variables who show up as VCallNodes?	
		  return;
		}
		
		if (!(node instanceof LocalVarNode))
			return;
		LocalVarNode localVarNode = (LocalVarNode) node;
		int nodeStart = node.getPosition().getStartOffset();
		final String localVarName = TypeInferenceHelper.Instance().getVarName(localVarNode);

		// See if it has been assigned to, earlier [TODO: in this local scope].
		// Find first assignment to this var name that occurs before the
		// reference
		// TODO: This will find assignments in other local scopes that
		// precede this reference but have the same variable name.
		// To mitigate, ensure that the closest spanning ScopeNode for both
		// this LocalVarNode and the AsgnNode are the name ScopeNode.
		// Or scopingNode. Still not sure whether IterNodes count or not...
		// silly block-local-var ambiguity ;)
		Node initialAssignmentNode = FirstPrecursorNodeLocator.Instance().findFirstPrecursor(rootNode, nodeStart, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				String name = null;
				if (node instanceof LocalAsgnNode)
					name = ((LocalAsgnNode) node).getName();
				if (node instanceof InstAsgnNode)
					name = ((InstAsgnNode) node).getName();
				if (node instanceof GlobalAsgnNode)
					name = ((GlobalAsgnNode) node).getName();
				return (name != null && name.equals(localVarName));
			}
		});
		if (initialAssignmentNode != null) {
			tryAsgnNode(initialAssignmentNode, guesses);
		}
		// See if it is a param into this scope
		ArgsNode argsNode = (ArgsNode) FirstPrecursorNodeLocator.Instance().findFirstPrecursor(rootNode, nodeStart, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ((node instanceof ArgsNode) && (doesArgsNodeContainsVariable((ArgsNode) node, localVarName)));
			}
		});
		// If so, find its enclosing method
		if (argsNode != null) {
			// Find enclosing method
			Node defNode = FirstPrecursorNodeLocator.Instance().findFirstPrecursor(rootNode, nodeStart, new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					ArgsNode argsNode = null;
					if (node instanceof DefnNode)
						argsNode = ((DefnNode) node).getArgsNode();
					if (node instanceof DefsNode)
						argsNode = ((DefsNode) node).getArgsNode();
					return ((argsNode != null) && (doesArgsNodeContainsVariable(argsNode, localVarName)));
				}
			});
			if (defNode != null) {
				String methodName = null;
				if (defNode instanceof DefnNode)
					methodName = ((DefnNode) defNode).getName();
				if (defNode instanceof DefsNode)
					methodName = ((DefsNode) defNode).getName();
				// Find all invocations of the surrounding method.
				// TODO: from easiest to hardest:
				// It may be a global function, where simply a CallNode
				// where method name must be matched.
				// It may be a DefsNode static class method, where a
				// CallNode whose receiverNode is a ConstNode whose name is
				// the surrounding class
				// It may be an DefnNode method defined in a class, where a
				// CallNode whose receiverNode must be type-matched to the
				// surrounding class
			}
		}
	}

	private void tryWellKnownMethodCalls(Node node, Collection<ITypeGuess> guesses) {
		if (!(node instanceof CallNode))
			return;
		CallNode callNode = (CallNode) node;
		String method = callNode.getName();
		if (method.equals(CONSTRUCTOR_INVOKE_NAME)) {
			String name = null;
			if (callNode.getReceiverNode() instanceof ConstNode) {
				name = ((ConstNode) callNode.getReceiverNode()).getName();
			} else if (callNode.getReceiverNode() instanceof Colon2Node) {
				name = ASTUtil.getFullyQualifiedName((Colon2Node) callNode.getReceiverNode());
			}
			if (name != null)
				guesses.add(new BasicTypeGuess(name, 100));
		} else {
			// TODO: this NEEDS to be done with a multimap and various
			// confidences for each. i.e. X.slice, X is 50/50 Array or
			// String
			String methodReturnTypeGuess = TypicalMethodReturnNames.get(method);
			if (methodReturnTypeGuess != null) {
				guesses.add(new BasicTypeGuess(methodReturnTypeGuess, 100));
			}
		}
	}

	/**
	 * Determine whether an ArgsNode contains a particular named argument
	 * 
	 * @param argsNode
	 *            ArgsNode to search
	 * @param argName
	 *            Name of argument to find
	 * @return
	 */
	private boolean doesArgsNodeContainsVariable(ArgsNode argsNode, String argName) {
		if (argsNode == null) return false;
		if (argName == null) return false;
		return getArgumentIndex(argsNode, argName) >= 0;
	}

	/**
	 * Finds the index of an argument in an ArgsNode by name, -1 if it is not
	 * contained.
	 * 
	 * @param argsNode
	 *            ArgsNode to search
	 * @param argName
	 *            Name of argument to find
	 * @return Index of argName in argsNode or -1 if it is not there.
	 */
	private int getArgumentIndex(ArgsNode argsNode, String argName) {
		int argNumber = 0;
		ListNode args = argsNode.getArgs();
		if (args == null) return -1; // no args. Maybe we should check arity instead?
		for (Iterator iter = args.childNodes().iterator(); iter.hasNext();) {
			ArgumentNode arg = (ArgumentNode) iter.next();
			if (arg.getName().equals(argName)) {
				break;
			}
			argNumber++;
		}
		if (argNumber == argsNode.getRequiredArgsCount()) {
			return -1;
		}
		return argNumber;
	}
}
